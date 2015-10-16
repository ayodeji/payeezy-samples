package io.pivotal.payeezy;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
public class CreditCardPaymentsApplication {
	
	private static Logger logger = Logger.getLogger(PayeezyRequest.class);
	
    public static void main(String[] args) {
        SpringApplication.run(CreditCardPaymentsApplication.class, args);
    }
}
