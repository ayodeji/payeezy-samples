package io.pivotal.payeezy;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PayeezyGenericsApplication.class)
public class PayeezyGenericsApplicationTests {
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	Credentials credentials;
	
	@Autowired
	PayeezyRequest payeezyRequest;
	
	private static Logger logger = Logger.getLogger(PayeezyGenericsApplicationTests.class);

	
	
	@Test
	public void contextLoads() {
	}

    @Test
    public void makeCreditCardTransaction()throws Exception {
        assertNotNull("RESTTemplate is null:", restTemplate);
        assertNotNull("PayeezyRequest is null:", payeezyRequest);
        TransactionRequest transactionRequest = getPrimaryTransaction();
        System.out.println("Credentials: " + credentials.toString());
        String response=payeezyRequest.post(transactionRequest);
        logger.info(response);
//        assertNotNull("Response is null ",response);
//        assertNull("Error in response",response.getError());
//        log.info("Transaction Tag:{} Transaction id:{}",response.getTransactionTag(),response.getTransactionId());
    }
    
    private TransactionRequest getPrimaryTransaction() {
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
}
