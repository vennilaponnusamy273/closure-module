package in.codifi.api.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.ClosureDocumentEntity;

public interface ClosureDocumentRepository extends CrudRepository<ClosureDocumentEntity, String> {

	List<ClosureDocumentEntity> findByApplicationId(String applicationId);

	Long countByApplicationId(Long applicationid);

	ClosureDocumentEntity findByApplicationIdAndDocumentType(String applicationId, String documentType);
	
	void deleteByApplicationId( String  applicationId);

}
