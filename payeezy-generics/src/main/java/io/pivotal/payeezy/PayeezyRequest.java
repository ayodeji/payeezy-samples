package io.pivotal.payeezy;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.pivotal.payeezy.TransactionResponse;

@Component
public class PayeezyRequest {
	
	@Autowired
	Credentials credentials;
	
	@Autowired
	RestTemplate restTemplate;
	
	// private static Logger logger = Logger.getLogger(PayeezyRequest.class);

	public PayeezyRequest(){
		
	}
	
	private HttpHeaders getHttpHeader(String payload) throws Exception {
		Map<String, String> encriptedKey = getSecurityKeys(payload);
		HttpHeaders header = new HttpHeaders();
		Iterator<String> iter = encriptedKey.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			if (HeaderField.PAYLOAD.equals(key))
				continue;
			header.add(key, encriptedKey.get(key));
		}

		header.add("Accept", Constants.HEADER_CONTENT_TYPE);

		header.setContentType(MediaType.APPLICATION_JSON);

		List<MediaType> mediatypes = new ArrayList<MediaType>();
		mediatypes.add(MediaType.APPLICATION_JSON);

		mediatypes.add(new MediaType("application", "json", Charset
				.forName("UTF-8")));

		header.add("User-Agent", "Java/1.6.0_26");

		return header;
	}

	private byte[] toHex(byte[] arr) {
		String hex = Hex.encodeHexString(arr);
		return hex.getBytes();
	}

	private String getMacValue(Map<String, String> data) throws Exception {
		Mac mac = Mac.getInstance("HmacSHA256");
		String apiSecret = data.get(HeaderField.APISECRET);
		SecretKeySpec secret_key = new SecretKeySpec(apiSecret.getBytes(),
				"HmacSHA256");
		mac.init(secret_key);
		StringBuilder buff = new StringBuilder();
		buff.append(data.get(HeaderField.APIKEY))
				.append(data.get(HeaderField.NONCE))
				.append(data.get(HeaderField.TIMESTAMP));
		if (data.get(HeaderField.TOKEN) != null)
			buff.append(data.get(HeaderField.TOKEN));
		if (data.get(HeaderField.PAYLOAD) != null)
			buff.append(data.get(HeaderField.PAYLOAD));

		byte[] macHash = mac.doFinal(buff.toString().getBytes("UTF-8"));

		String authorizeString = Base64.encodeBase64String(toHex(macHash));
		System.out.println("*** authorizeString: " + authorizeString);
		System.out.println("*** Whole Buffer: " + buff.toString());
		return authorizeString;

	}

	private Map<String, String> getSecurityKeys(String payload) throws Exception {
		Map<String, String> returnMap = new HashMap<String, String>();
		long nonce;
		try {
			nonce = Math.abs(SecureRandom.getInstance("SHA1PRNG").nextLong());
			returnMap.put(HeaderField.NONCE, Long.toString(nonce));
			returnMap.put(HeaderField.APIKEY, credentials.getApiKey());
			returnMap.put(HeaderField.TIMESTAMP,
					Long.toString(System.currentTimeMillis()));
			returnMap.put(HeaderField.TOKEN, credentials.getMerchantToken());
			returnMap.put(HeaderField.APISECRET, credentials.getApiSecret());
			returnMap.put(HeaderField.PAYLOAD, payload);
			returnMap.put(HeaderField.AUTHORIZE, getMacValue(returnMap));

		} catch (NoSuchAlgorithmException e) {
			// logger.error(e.getMessage());
			throw new RuntimeException(e.getMessage(), e);
		}
		return returnMap;
	}
	
    private String getJSONObject(Object data) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        OutputStream stream = new BufferedOutputStream(byteStream);
        JsonGenerator jsonGenerator = objectMapper.getFactory().createGenerator(stream, JsonEncoding.UTF8);
        objectMapper.writeValue(jsonGenerator, data);
        stream.flush();
        return new String(byteStream.toByteArray());

    }

    
	public String post(TransactionRequest transactionRequest) {
		HttpEntity<TransactionRequest> request = null;
		try {
			String payload=getJSONObject(transactionRequest);
			System.out.println("*** Payload: " + payload);
			System.out.println("*** trans: " + transactionRequest.toString());
			request = new HttpEntity<TransactionRequest>(transactionRequest, getHttpHeader(payload));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String url = new String(Constants.TRANSACTION_URL);

		ResponseEntity<TransactionResponse> response = null;
		try {
//			logger.info("URL: " + url);
//			logger.info("Headers: " + request.getHeaders().toString());
//			logger.info("Body: " + request.getBody().toString());
//			logger.info("Request: " + request.toString());
			restTemplate = new RestTemplate();
			response = restTemplate.exchange(url, HttpMethod.POST, request,
					TransactionResponse.class);
//			logger.info("Response Header: " + response.getHeaders());
//			logger.info("Response Body: " + response.getBody());
			return response.toString();
		} catch (Exception e) {
//			logger.error("Response Error: " + e.getMessage());
			return e.getMessage();
		}
	}

}
