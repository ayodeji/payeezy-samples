package io.pivotal.payeezy.repository;

import io.pivotal.payeezy.model.TransarmorCard;

import org.springframework.data.repository.CrudRepository;


public interface TransarmorCardRepository extends CrudRepository<TransarmorCard, String> {
	
	TransarmorCard findOne(String id);
	
	@SuppressWarnings("unchecked")
	TransarmorCard save(TransarmorCard transarmorCard);
}
