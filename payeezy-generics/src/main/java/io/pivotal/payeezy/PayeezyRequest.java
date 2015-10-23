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
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.pivotal.payeezy.TransactionResponse;

@Component
public class PayeezyRequest {
	
	//@Autowired
	ObjectMapper objectMapper;

	
	//@Autowired
	RestTemplate restTemplate;
	
	private final static String NONCE_HEADER = "nonce";
	private final static String APIKEY_HEADER = "apikey";
	private final static String APISECRET_HEADER = "pzsecret";
	private final static String TOKEN_HEADER = "token";
	private final static String TIMESTAMP_HEADER = "timestamp";
	private final static String AUTHORIZE_HEADER = "Authorization";
	private final static String PAYLOAD_HEADER = "payload";
	
	private static Logger logger = Logger.getLogger(PayeezyRequest.class);

	public PayeezyRequest(){
		restTemplate = new RestTemplate();
		objectMapper = new ObjectMapper();
	}
	
	private HttpHeaders getHttpHeader(String payload) throws Exception {
		Map<String, String> encriptedKey = getSecurityKeys(payload);
		HttpHeaders header = new HttpHeaders();
		Iterator<String> iter = encriptedKey.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			if (PAYLOAD_HEADER.equals(key))
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
		String apiSecret = data.get(APISECRET_HEADER);
		SecretKeySpec secret_key = new SecretKeySpec(apiSecret.getBytes(),
				"HmacSHA256");
		mac.init(secret_key);
		StringBuilder buff = new StringBuilder();
		buff.append(data.get(APIKEY_HEADER))
				.append(data.get(NONCE_HEADER))
				.append(data.get(TIMESTAMP_HEADER));
		if (data.get(TOKEN_HEADER) != null)
			buff.append(data.get(TOKEN_HEADER));
		if (data.get(PAYLOAD_HEADER) != null)
			buff.append(data.get(PAYLOAD_HEADER));

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
			returnMap.put(NONCE_HEADER, Long.toString(nonce));
			returnMap.put(APIKEY_HEADER, "AMhPuRSAc9KcwE5HFg8yL9LooCqKAeAG");
			returnMap.put(TIMESTAMP_HEADER,
					Long.toString(System.currentTimeMillis()));
			returnMap.put(TOKEN_HEADER, "fdoa-a480ce8951daa73262734cf102641994c1e55e7cdf4c02b6");
			returnMap.put(APISECRET_HEADER, "20053246b3aeaa1817026276884d8645c14b2d1eac499f060f586bcb20666889");
			returnMap.put(PAYLOAD_HEADER, payload);
			returnMap.put(AUTHORIZE_HEADER, getMacValue(returnMap));

		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return returnMap;
	}
	
    private String getJSONObject(Object data) throws IOException {

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        OutputStream stream = new BufferedOutputStream(byteStream);
        JsonGenerator jsonGenerator = objectMapper.getFactory().createGenerator(stream, JsonEncoding.UTF8);
        objectMapper.writeValue(jsonGenerator, data);
        stream.flush();
        return new String(byteStream.toByteArray());

    }
    

    
	public TransactionResponse post(TransactionRequest transactionRequest) {
		HttpEntity<TransactionRequest> request = null;
		try {
			String payload=getJSONObject(transactionRequest);
			request = new HttpEntity<TransactionRequest>(transactionRequest, getHttpHeader(payload));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String url = new String(Constants.TRANSACTION_URL);

		ResponseEntity<TransactionResponse> response = null;
		try {

			response = restTemplate.exchange(url, HttpMethod.POST, request,
					TransactionResponse.class);
		} catch (Exception e) {
			logger.error("Response Error: " + e.getMessage());
		}
		return response.getBody();
	}
	
	/**************************************************************************************/
	
	public TransactionResponse purchaseTransaction(TransactionRequest trans) throws Exception {
		trans.setTransactionType(TransactionType.PURCHASE.name());
		return doPrimaryTransaction(trans);
	}
	
	public TransactionResponse doPrimaryTransaction(TransactionRequest trans) throws Exception {

		String url = Constants.PRIMARY_TRANSACTION_URL_VALUE + "/transactions";

		if ((trans.getToken() == null) && (trans.getType() == "FDToken") && (trans.getTa_token() == Constants.TA_TOKEN_VALUE)
				&& (trans.getAuth() == "false")) {
			url = Constants.PRIMARY_TRANSACTION_URL_VALUE + "/transactions/tokens";
		}
		// this.urltoken = url;
		if (!(url.endsWith("tokens"))) {
			Assert.notNull(trans.getAmount(), "Amount is not present");
			Assert.notNull(trans.getTransactionType(), "Transaction type is not present");
		}

		String payload = getJSONObject(trans);
		HttpEntity<TransactionRequest> request = new HttpEntity<TransactionRequest>(trans,
				getHttpHeader(payload));
		logger.info("doPrimaryTransaction: " + url + " " + request.toString());
		ResponseEntity<TransactionResponse> response = restTemplate.exchange(url, HttpMethod.POST, request,
				TransactionResponse.class);
		return response.getBody();

	}
	
	public TransactionResponse voidTransaction(TransactionRequest trans) throws Exception {
		trans.setTransactionType(TransactionType.VOID.name());
		if ((trans.getToken() != null) && (trans.getToken().getTokenType() != null)
				&& (trans.getToken().getTokenType().toUpperCase() == "FDTOKEN")) {
			return doSecondaryTransactionObject(trans);
		}
		return doSecondaryTransaction(trans);
	}
	
	public TransactionResponse doSecondaryTransaction(TransactionRequest trans) throws Exception {
		Assert.notNull(trans.getTransactionTag(), "Transaction Tag is not present");
		Assert.notNull(trans.getId(), "Id is not present");
		Assert.notNull(trans.getTransactionType(), "Transaction type is not present");
		String url = Constants.PRIMARY_TRANSACTION_URL_VALUE + "/transactions/{id}";
		String payload = getJSONObject(trans);
		HttpEntity<TransactionRequest> request = new HttpEntity<TransactionRequest>(trans,
				getHttpHeader(payload));
		logger.info("doSecondaryTransaction: " + url + " " + request.toString());
		ResponseEntity<TransactionResponse> response = restTemplate.exchange(url, HttpMethod.POST, request,
				TransactionResponse.class, trans.getId());
		// return doTransaction(trans,credentials);
		return response.getBody();
		// return null;
	}
	
	public TransactionResponse doSecondaryTransactionObject(TransactionRequest trans) throws Exception {
		Assert.notNull(trans.getTransactionTag(), "Transaction Tag is not present");
		Assert.notNull(trans.getId(), "Id is not present");
		Assert.notNull(trans.getTransactionType(), "Transaction type is not present");
		String url = Constants.URL_VALUE + "/transactions/{id}";
		trans.setTransactionType(trans.getTransactionType().toLowerCase());
		String payload = getJSONObject(trans);
		HttpEntity<TransactionRequest> request = new HttpEntity<TransactionRequest>(trans,
				getHttpHeader(payload));
		ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, request, Object.class,
				trans.getId());

		Object o2 = response.getBody();
		TransactionResponse resp = GetTransactionResponse(o2.toString());
		return resp;

	}
	
	private TransactionResponse GetTransactionResponse(Object obj) {
		TransactionResponse response = new TransactionResponse();
		Token token = new Token();
		Transarmor tokenData = new Transarmor();
		token.setTokenData(tokenData);
		response.setToken(token);
		int beginIndex = 0;
		int endIndex = 0;
		String objstr = obj.toString();
		boolean tokenResponse = false;
		objstr = objstr.trim();
		if (objstr.startsWith("Payeezy.callback")) {
			objstr = objstr.substring(19, objstr.length()); // ("Payeezy.callback",
															// "");
			objstr = objstr.trim();
			tokenResponse = true;
		}

		String[] responseData = objstr.split(",");

		for (int i = 0; i < responseData.length; i++) {
			String str = responseData[i];

			String[] dataVals = str.split("=");
			if (tokenResponse) {
				str = str.trim();
				dataVals = str.split(":");
			}
			if (dataVals.length >= 2) {
				dataVals[1] = dataVals[1].replace("{", "");
				dataVals[1] = dataVals[1].replace("}", "");
				dataVals[1] = dataVals[1].replace(":", "");
				dataVals[1] = dataVals[1].replace("\"", "");
				dataVals[1] = dataVals[1].replace("[", "");
			}
			if (dataVals.length >= 3) {
				dataVals[2] = dataVals[2].replace("{", "");
				dataVals[2] = dataVals[2].replace("}", "");
				dataVals[2] = dataVals[2].replace(":", "");
				dataVals[2] = dataVals[2].replace("\"", "");
				dataVals[2] = dataVals[2].replace("[", "");
			}

			if (dataVals[0].contains("results")) {
				String correlationID = dataVals[2];
				response.setCorrelationID(correlationID);
			}

			// if(str.contains("correlation_id"))
			if (dataVals[0].contains("correlation_id")) {
				String correlationID = dataVals[1];
				response.setCorrelationID(correlationID);
			}
			if (str.contains("status")) {
				if (tokenResponse) {
					String status = dataVals[1];
					try {
						int stat = Integer.parseInt(status);
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
					if (status.length() < 6) // if(stat>0)
					{
						// response.setStatus(status);
					}
				} else {
					String status = dataVals[1];
					// response.setStatus(status);
				}
			}
			if (str.contains("type")) {
				String type = dataVals[1];
				response.getToken().setTokenType(type);
			}
			if (str.contains("token")) {
				String cardtype = dataVals[1];
				if (dataVals.length > 2) {
					cardtype = dataVals[2];
				}
				response.getToken().getTokenData().setType(cardtype);
			}
			if (str.contains("cardholder_name")) {
				String name = dataVals[1];
				response.getToken().getTokenData().setName(name);
			}

			if (str.contains("exp_date")) {
				String expDate = dataVals[1];
				response.getToken().getTokenData().setExpiryDt(expDate);
			}
			if (str.contains("value")) {
				String value = dataVals[1];
				response.getToken().getTokenData().setValue(value);
			}

			if (str.contains("transaction_id")) {
				String transactionId = dataVals[1];
				response.setTransactionId(transactionId);
			}
			if (str.contains("transaction_tag")) {
				String transactionTag = dataVals[1];
				response.setTransactionTag(transactionTag);
			}
			if (str.contains("amount")) {
				String amount = dataVals[1];
				response.setAmount(amount);
			}
			if (str.contains("transaction_status")) {
				String transactionStatus = dataVals[1];
				response.setTransactionStatus(transactionStatus);
			}
			if (str.contains("validation_status")) {
				String validation_status = dataVals[1];
				response.setValidationStatus(validation_status);
			}
			if (str.contains("transaction_type")) {
				String transaction_type = dataVals[1];
				response.setTransactionType(transaction_type);
			}
			if (str.contains("method")) {
				String method = dataVals[1];
				response.setMethod(method);
			}

		}
		return response;

	}

}
