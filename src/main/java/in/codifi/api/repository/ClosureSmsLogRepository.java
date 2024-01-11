package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.ClosureSmsLogEntity;

public interface ClosureSmsLogRepository extends CrudRepository<ClosureSmsLogEntity, Long> {

	ClosureSmsLogEntity findByMobileNoAndLogMethod(Long mobileNo,String logMethod);
}
