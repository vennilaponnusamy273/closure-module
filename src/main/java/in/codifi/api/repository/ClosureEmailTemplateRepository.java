package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.ClosureEmailTemplateEntity;

public interface ClosureEmailTemplateRepository extends CrudRepository<ClosureEmailTemplateEntity, Long> {

	ClosureEmailTemplateEntity findByKeyData(String keyData);
}
