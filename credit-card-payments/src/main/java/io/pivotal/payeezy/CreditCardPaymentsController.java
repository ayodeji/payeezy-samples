package io.pivotal.payeezy;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class CreditCardPaymentsController {

	@RequestMapping(value="creditCardPayment", method=RequestMethod.POST)
	public void makeCreditCardTransaction(@ModelAttribute TransactionRequest transactionRequest){
		
	}
}
