package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;
import in.codifi.api.entity.ErrorLogEntity;

public interface ErrorLogRepository extends CrudRepository<ErrorLogEntity, Long> {

	ErrorLogEntity findByApplicationIdAndClassNameAndMethodName(String applicationId, String className,String MethodName);
	
}
