package io.pivotal.payeezy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CreditCardPaymentsApplication.class)
public class CreditCardPaymentsApplicationTests {

	@Autowired
	Credentials credentials;
	
	@Test
	public void contextLoads() {
	}
	
	@Test
	public void makeCreditCardPayment(){
		System.out.println("Credentials: " + credentials.toString());
	}

}
