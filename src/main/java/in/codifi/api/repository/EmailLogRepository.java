package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.EmailLogEntity;


public interface EmailLogRepository extends CrudRepository<EmailLogEntity, Long> {

	EmailLogEntity findByEmailIdAndLogMethod(String emailId,String logMethod);
}
