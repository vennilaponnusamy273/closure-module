package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.ClosureErrorLogEntity;

public interface ClosureErrorLogRepository extends CrudRepository<ClosureErrorLogEntity, Long> {

	ClosureErrorLogEntity findByApplicationIdAndClassNameAndMethodName(String applicationId, String className,String MethodName);
	
}
