package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;
import in.codifi.api.entity.SmsLogEntity;

public interface SmsLogRepository extends CrudRepository<SmsLogEntity, Long> {

	SmsLogEntity findByMobileNoAndLogMethod(Long mobileNo,String logMethod);
}
