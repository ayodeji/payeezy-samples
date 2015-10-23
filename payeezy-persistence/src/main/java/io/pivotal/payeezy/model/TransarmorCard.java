package io.pivotal.payeezy.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class TransarmorCard implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String type;
	private String name;
	private String number;
	private String expiryDt;
	private String cvv;
	
	public TransarmorCard() {
		// TODO Auto-generated constructor stub
	}

	public TransarmorCard(String id, String type, String name, String number,
			String expiryDt, String cvv) {
		super();
		this.id = id;
		this.type = type;
		this.name = name;
		this.number = number;
		this.expiryDt = expiryDt;
		this.cvv = cvv;
	}

	@Override
	public String toString() {
		return "TransarmorCard [id=" + id + ", type=" + type + ", name=" + name
				+ ", number=" + number + ", expiryDt=" + expiryDt + ", cvv="
				+ cvv + "]";
	}

}
