package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.ClosureEmailLogEntity;


public interface ClosureEmailLogRepository extends CrudRepository<ClosureEmailLogEntity, Long> {

	ClosureEmailLogEntity findByEmailIdAndLogMethod(String emailId,String logMethod);
}
