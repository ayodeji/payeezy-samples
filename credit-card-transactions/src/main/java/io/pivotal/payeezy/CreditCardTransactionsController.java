package io.pivotal.payeezy;

import io.pivotal.payeezy.model.SecondaryTransaction;
import io.pivotal.payeezy.repository.SecondaryTransactionRepository;
import io.pivotal.payeezy.ui.Form;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class CreditCardTransactionsController {

	private static Logger logger = Logger
			.getLogger(CreditCardTransactionsController.class);

	@Autowired
	PayeezyClient payeezyClient;

	@Autowired
	SecondaryTransactionRepository secondaryTransactionRepository;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String showForm(Model model) {
		model.addAttribute("form", new Form());
		model.addAttribute("responseMessage", "Enter data and hit Submit");
		return "form";
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String submitForm(Form form, Model model) {
		logger.info("form: " + form.toString());
		ResponseEntity<TransactionResponse> responseEntity = null;
		TransactionRequest request = form.toTransactionRequest();
		String responseMessage = null;

		if (form.getTransactionType().equalsIgnoreCase("VOID")) {
			// Void Transaction
			try {
				responseEntity = voidTransaction(request);
				responseMessage = responseEntity.getBody().getExactMessage();
			} catch (Exception e) {
				responseMessage = "Invalid Data for a VOID Transaction";
				logger.error("Invalid data for voidTransaction. Form: "
						+ form.toString());
			} finally {
				model.addAttribute("responseMessage", responseMessage);
				return "form";
			}

		} else {
			// Primary transaction
			try {
				responseEntity = payeezyClient.post(request);
				responseMessage = responseEntity.getBody().getExactMessage();
			} catch (Exception e) {
				responseMessage = "Invalid Data for AUTHORIZE / PURCHASE Transaction";
			} finally {
				model.addAttribute("responseMessage", responseMessage);
				return "form";
			}
		}
	}

	private ResponseEntity<TransactionResponse> voidTransaction(
			TransactionRequest request) throws Exception {
		TransactionResponse response = null;
		ResponseEntity<TransactionResponse> responseEntity = null;

		TransactionResponse secondaryResponse = null;
		ResponseEntity<TransactionResponse> secondaryResponseEntity = null;

		responseEntity = payeezyClient.post(request);
		response = responseEntity.getBody();

		if (response.getError() == null) {
			TransactionRequest secondaryRequest = new TransactionRequest();
			String id = response.getTransactionId();
			String paymentMethod = response.getMethod();
			String amount = response.getAmount();
			String currency = response.getCurrency();
			String transactionTag = response.getTransactionTag();

			secondaryRequest.setCurrency(currency);
			secondaryRequest.setPaymentMethod(paymentMethod);
			secondaryRequest.setId(id);
			secondaryRequest.setTransactionTag(transactionTag);
			secondaryRequest.setAmount(amount);
			secondaryRequest.setTransactionType("VOID");

			SecondaryTransaction secondaryTransaction = new SecondaryTransaction(
					id, paymentMethod, amount, currency, transactionTag);
			SecondaryTransaction savedSecondaryTransaction = secondaryTransactionRepository
					.save(secondaryTransaction);
			SecondaryTransaction retrievedSecondaryTransaction = secondaryTransactionRepository
					.findOne(id);
			logger.info("Saved secondary transaction is: "
					+ savedSecondaryTransaction.toString());
			logger.info("Retrived secondary transaction is: "
					+ retrievedSecondaryTransaction.toString());

			try {
				secondaryResponseEntity = payeezyClient.post(secondaryRequest,
						id);
				secondaryResponse = responseEntity.getBody();
				logger.info("Transaction Tag:{} Transaction id:{}"
						+ secondaryResponse.getTransactionTag()
						+ secondaryResponse.getTransactionId());
				logger.info("Response: "
						+ secondaryResponse.getExactResponseCode() + " "
						+ secondaryResponse.getExactMessage());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return secondaryResponseEntity;
	}
}
