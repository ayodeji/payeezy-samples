package io.pivotal.payeezy;

import static org.junit.Assert.assertEquals;
import io.pivotal.payeezy.model.TransarmorCard;
import io.pivotal.payeezy.repository.TransarmorCardRepository;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PayeezyPersistenceApplication.class)
public class PayeezyPersistenceApplicationTests {
	private static Logger logger = Logger.getLogger(PayeezyPersistenceApplicationTests.class);

	
	@Autowired
	TransarmorCardRepository transarmorCardRepository;

	@Test
	public void contextLoads() {
	}

	@Test
	public void saveAndRetrieve(){
		TransarmorCard savedTc = new TransarmorCard("id1", "type1", "name1", "number1", "expiryDt1", "cvv1");
		transarmorCardRepository.save(savedTc);
		
		TransarmorCard retrievedTc = transarmorCardRepository.findOne("id1");
		assertEquals(savedTc.toString(), retrievedTc.toString());
	}
}
