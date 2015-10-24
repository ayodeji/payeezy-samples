package io.pivotal.payeezy;

import static org.junit.Assert.assertEquals;
import io.pivotal.payeezy.model.SecondaryTransaction;
import io.pivotal.payeezy.model.TransarmorCard;
import io.pivotal.payeezy.repository.SecondaryTransactionRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CreditCardTransactionsApplication.class)
public class CreditCardTransactionsTests {

	@Autowired
	SecondaryTransactionRepository secondaryTransactionRepository;

	@Test
	public void contextLoads() {
	}

	@Test
	public void saveAndRetrieve(){
		SecondaryTransaction saved = new SecondaryTransaction("id1", "paymentMethod1", "amount1",
				"currency1", "transactionTag1");
		secondaryTransactionRepository.save(saved);
		
		SecondaryTransaction retrieved = secondaryTransactionRepository.findOne("id1");
		assertEquals(saved.toString(), retrieved.toString());
	}
}
