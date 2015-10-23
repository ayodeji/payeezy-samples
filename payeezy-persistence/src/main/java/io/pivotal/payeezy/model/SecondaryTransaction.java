package io.pivotal.payeezy.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SecondaryTransaction implements Serializable {
	@Id
	private String id;
	private String paymentMethod;
	private String amount;
	private String currency;
	private String transactionTag;
	
	public SecondaryTransaction() {
		// TODO Auto-generated constructor stub
	}

	public SecondaryTransaction(String id, String paymentMethod, String amount,
			String currency, String transactionTag) {
		super();
		this.id = id;
		this.paymentMethod = paymentMethod;
		this.amount = amount;
		this.currency = currency;
		this.transactionTag = transactionTag;
	}

	@Override
	public String toString() {
		return "SecondaryTransaction [id=" + id + ", paymentMethod="
				+ paymentMethod + ", amount=" + amount + ", currency="
				+ currency + ", transactionTag=" + transactionTag + "]";
	}

	public String getId() {
		return id;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public String getAmount() {
		return amount;
	}

	public String getCurrency() {
		return currency;
	}

	public String getTransactionTag() {
		return transactionTag;
	}
	
	
}
