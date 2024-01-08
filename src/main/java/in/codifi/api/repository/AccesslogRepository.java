package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.AccesslogEntity;

public interface AccesslogRepository extends CrudRepository<AccesslogEntity, Long> {

	AccesslogEntity findByApplicationIdAndMethod(String applicationId, String method);

	void deleteByApplicationId(String applicationId);

}
