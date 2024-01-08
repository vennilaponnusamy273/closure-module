package in.codifi.api.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.DocumentEntity;

public interface DocumentRepository extends CrudRepository<DocumentEntity, String> {

	List<DocumentEntity> findByApplicationId(String applicationId);

	Long countByApplicationId(Long applicationid);

	DocumentEntity findByApplicationIdAndDocumentType(String applicationId, String documentType);
	
	void deleteByApplicationId( String  applicationId);

}
