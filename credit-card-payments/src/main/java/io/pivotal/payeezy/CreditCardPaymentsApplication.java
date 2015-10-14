package io.pivotal.payeezy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;


@SpringBootApplication
public class CreditCardPaymentsApplication {
	
	private static Logger logger = Logger.getLogger(PayeezyRequest.class);
	
	@Autowired
	List<HttpMessageConverter<?>> list;
	
	@Autowired
	StringHttpMessageConverter stringHttpMessageConverter;
	
	@Autowired
	RestTemplate restTemplate;
	
	

	private static String getPayload() {
		String creditCardPayload = "{\"type\":\"visa\",\"cardholder_name\":\"John Smith\",\"card_number\":\"4788250000028291\","
				+ "\"exp_date\":1225,\"cvv\":\"123\"}";

		String payload = "{\"merchant_ref\":\"Astonishing-Sale\",\"transaction_type\":\"authorize\","
				+ "\"method\":\"credit_card\",\"amount\":1299,\"currency_code\":\"USD\",\"credit_card\":"
				+ creditCardPayload + "}";

		return payload;
	}
	
	@Bean
	List<HttpMessageConverter<?>> list(){
		return new ArrayList();
	}
	
	@Bean
	StringHttpMessageConverter stringHttpMessageConverter() {
		return new StringHttpMessageConverter();
	}
	
	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate(list);
		// return new RestTemplate(stringHttpMessageConverter);
	}
	
    public static void main(String[] args) {
        SpringApplication.run(CreditCardPaymentsApplication.class, args);

    }
}
