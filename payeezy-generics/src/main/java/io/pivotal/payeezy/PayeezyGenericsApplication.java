package io.pivotal.payeezy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class PayeezyGenericsApplication {

	private static Logger logger = Logger.getLogger(PayeezyRequest.class);
	
	@Autowired
	Environment env;
	
	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}
	
	@Bean
	public Credentials credentials() {
		return new Credentials(
				env.getProperty("api_key"),
				env.getProperty("api_secret"),
				env.getProperty("merchant_id"),
				env.getProperty("merchant_token"),
				env.getProperty("reporting_token"));
	}


    
	public static void main(String[] args) {
		SpringApplication.run(PayeezyGenericsApplication.class, args);
	}

}
