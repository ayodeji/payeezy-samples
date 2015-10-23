package io.pivotal.payeezy.repository;

import io.pivotal.payeezy.model.SecondaryTransaction;

import org.springframework.data.repository.CrudRepository;

public interface SecondaryTransactionRepository extends CrudRepository<SecondaryTransaction, String> {
	
	SecondaryTransaction findOne(String id);
	
	@SuppressWarnings("unchecked")
	SecondaryTransaction save(SecondaryTransaction s);

}
