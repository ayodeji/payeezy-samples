package io.pivotal.payeezy;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PayeezyClientApplication.class)
public class PayeezyClientTests {
	
	private static Logger logger = Logger.getLogger(PayeezyClientTests.class);

	@Autowired
    PayeezyClient payeezyClient;

	
    @Test
    public void purchaseTransaction()throws Exception {
        TransactionRequest request = createPrimaryTransaction();
        System.out.println("request: " + request.toString());
        ResponseEntity<TransactionResponse> responseEntity = this.payeezyClient.post(request);
        TransactionResponse response = responseEntity.getBody();

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals("approved", response.getTransactionStatus());
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
    
    @Test
    public void voidPayment() throws Exception {
    	logger.info("+++++++++++++++++++++++++++++++++++++ start ++++++++++++++++++");

        TransactionRequest request = createPrimaryTransaction();
        System.out.println("request: " + request.toString());
        ResponseEntity<TransactionResponse> responseEntity = this.payeezyClient.post(request);
        TransactionResponse response = responseEntity.getBody();
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals("approved", response.getTransactionStatus());    	
        
        TransactionRequest trans=new TransactionRequest();
        trans.setPaymentMethod(response.getMethod());
        trans.setAmount(response.getAmount());
        trans.setCurrency(response.getCurrency());
        trans.setTransactionTag(response.getTransactionTag());
        trans.setId(response.getTransactionId());
        trans.setTransactionType("VOID");
        
        logger.info("Secondary Request: " + trans);
        responseEntity = this.payeezyClient.post(trans, response.getTransactionId());
        logger.info("Secondary Response: " + responseEntity.getBody().toString());
    	logger.info("+++++++++++++++++++++++++++++++++++++ end ++++++++++++++++++");

    }
    /*
    @Test
    public void doVoidPayment()throws Exception {
    	logger.info("+++++++++++++++++++++++++++++++++++++ start ++++++++++++++++++");
    	RestTemplate restTemplate = new RestTemplate();
    	PayeezyRequest payeezyRequest = new PayeezyRequest();
        TransactionRequest req=createPrimaryTransaction();
        
        TransactionResponse response=payeezyRequest.purchaseTransaction(req);

        if(response.getError()==null) {
            req=getSecondaryTransaction();
            req.setId(response.getTransactionId());
            req.setTransactionTag(response.getTransactionTag());
            req.setAmount(response.getAmount());
            logger.info("SecondaryRequest: " + req.toString());
            response=payeezyRequest.voidTransaction(req);
            assertNotNull("Response is null ",response);
            assertNull(response.getError());
            logger.info("Transaction Tag:{} Transaction id:{}" + response.getTransactionTag() + response.getTransactionId());
            logger.info("Response: " + response.getExactResponseCode() + " " + response.getExactMessage());

        }
        logger.info("++++++++++++++++++++++++++++++++++++++ end +++++++++++++++++");
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
