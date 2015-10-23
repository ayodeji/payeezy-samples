package io.pivotal.payeezy;


import io.pivotal.payeezy.model.SecondaryTransaction;
import io.pivotal.payeezy.repository.SecondaryTransactionRepository;
import io.pivotal.payeezy.ui.TransactionFormInput;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class CreditCardPaymentsController {
	 
	private static Logger logger = Logger.getLogger(CreditCardPaymentsController.class);
	
	@Autowired
	PayeezyClient payeezyClient;
	
	@Autowired
	SecondaryTransactionRepository secondaryTransactionRepository;
	
	@RequestMapping(value = "/payment", method = RequestMethod.GET)
	public String showForm(Model model) {
		model.addAttribute("transactionFormInput", new TransactionFormInput());
        model.addAttribute("responseMessage", "Enter data and hit Submit");
		return "form";
	}
	
    @RequestMapping(value="/payment", method=RequestMethod.POST)
    public String submitForm(TransactionFormInput transactionFormInput, Model model) {
    	logger.info("TransactionFormInput: " + transactionFormInput.toString());
    	ResponseEntity<TransactionResponse> responseEntity = null;
    	TransactionRequest request= transactionFormInput.toTransactionRequest();
    	if(transactionFormInput.getTransactionType().equalsIgnoreCase("VOID")) {
    		// Void Transaction
    		responseEntity = voidTransaction(request);
    	}
    	else {
    		// Primary transaction
    		responseEntity = payeezyClient.post(request); 
    	}
        HttpStatus httpStatus = responseEntity.getStatusCode();
        String responseMessage = responseEntity.getBody().getExactMessage();
        logger.info("Status Code: " + responseEntity.getStatusCode());
        
        model.addAttribute("httpStatus", httpStatus.toString());
        model.addAttribute("responseMessage", responseMessage);
        return "redirect:/payment";
    }
    
    private ResponseEntity<TransactionResponse> voidTransaction(TransactionRequest request){
        TransactionResponse response = null;
        ResponseEntity<TransactionResponse> responseEntity = null;
        
        TransactionResponse secondaryResponse = null;
        ResponseEntity<TransactionResponse> secondaryResponseEntity = null;
        
		try {
	        responseEntity = payeezyClient.post(request);
	        response = responseEntity.getBody();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        if(response.getError()==null) {
        	TransactionRequest secondaryRequest = new TransactionRequest();
            String id = response.getTransactionId();
            String paymentMethod = response.getMethod(); //"credit_card";
            String amount = response.getAmount();
            String currency = response.getCurrency();
            String transactionTag = response.getTransactionTag();
 
            
            secondaryRequest.setCurrency(currency);
            secondaryRequest.setPaymentMethod(paymentMethod);
            secondaryRequest.setId(id);
            secondaryRequest.setTransactionTag(transactionTag);
            secondaryRequest.setAmount(amount);
            secondaryRequest.setTransactionType("VOID");
            
            SecondaryTransaction secondaryTransaction = new SecondaryTransaction(id, paymentMethod, amount, currency, transactionTag);
            SecondaryTransaction savedSecondaryTransaction = secondaryTransactionRepository.save(secondaryTransaction);
            logger.info("Saved secondary transaction is: " + savedSecondaryTransaction.toString());
            
            try {
            	secondaryResponseEntity = payeezyClient.post(secondaryRequest, id);
            	secondaryResponse = responseEntity.getBody();
                logger.info("Transaction Tag:{} Transaction id:{}" + secondaryResponse.getTransactionTag() + secondaryResponse.getTransactionId());
                logger.info("Response: " + secondaryResponse.getExactResponseCode() + " " + secondaryResponse.getExactMessage());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return secondaryResponseEntity;
    }
    /*
	@RequestMapping(value = "/voidTransaction", method = RequestMethod.GET)
	public void voidTransaction(Model model) {
    	logger.info("+++++++++++++++++++++++++++++++++++++ start ++++++++++++++++++");
    	
        TransactionRequest req=createPrimaryTransaction();
        
        TransactionResponse response = null;
		try {
			response = payeezyRequest.purchaseTransaction(req);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        logger.info("Primary Transaction Response: " + response);
        logger.info("Transaction Tag:{} Transaction id:{}" + response.getTransactionTag() + response.getTransactionId());

        
        
        if(response.getError()==null) {
            req=getSecondaryTransaction();
            String id = response.getTransactionId();
            String paymentMethod = "credit-card";
            String amount = response.getAmount();
            String currency = response.getCurrency();
            String transactionTag = response.getTransactionTag();
            
            req.setId(response.getTransactionId());
            req.setTransactionTag(response.getTransactionTag());
            req.setAmount(response.getAmount());
            
            SecondaryTransaction secondaryTransaction = new SecondaryTransaction(id, paymentMethod, amount, currency, transactionTag);
            SecondaryTransaction savedSecondaryTransaction = secondaryTransactionRepository.save(secondaryTransaction);
            logger.info("Saved secondary transaction is: " + savedSecondaryTransaction.toString());
            
            try {
				response=payeezyRequest.voidTransaction(req);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            logger.info("Transaction Tag:{} Transaction id:{}" + response.getTransactionTag() + response.getTransactionId());
            logger.info("Response: " + response.getExactResponseCode() + " " + response.getExactMessage());

        }
        logger.info("++++++++++++++++++++++++++++++++++++++ end +++++++++++++++++");
    }
    
    private TransactionRequest createPrimaryTransaction() {
        TransactionRequest request=new TransactionRequest();
        request.setAmount("1100");
        request.setCurrency("USD");
        request.setPaymentMethod("credit_card");
        request.setTransactionType(TransactionType.PURCHASE.name());
        Card card=new Card();
        card.setCvv("123");
        card.setExpiryDt("1219");
        card.setName("Test data ");
        card.setType("visa");
        card.setNumber("4788250000028291");
        request.setCard(card);
        Address address=new Address();
        request.setBilling(address);
        address.setState("NY");
        address.setAddressLine1("sss");
        address.setZip("11747");
        address.setCountry("US");
        //request.setTa_token(null);
        return request;
    }
    
    private TransactionRequest getSecondaryTransaction() {
        TransactionRequest trans=new TransactionRequest();
        trans.setPaymentMethod("credit_card");
        trans.setAmount("0.00");
        trans.setCurrency("USD");
        trans.setTransactionTag("349990997");
        trans.setId("07698G");
        return trans;
    }
    */
}
