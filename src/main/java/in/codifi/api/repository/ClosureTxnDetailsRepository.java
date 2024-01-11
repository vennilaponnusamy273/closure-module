package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.ClosureTxnDetailsEntity;

public interface ClosureTxnDetailsRepository extends CrudRepository<ClosureTxnDetailsEntity, Long> {

	ClosureTxnDetailsEntity findByapplicationId(String applicationId);

	ClosureTxnDetailsEntity findBytxnId(String txnId);

}
