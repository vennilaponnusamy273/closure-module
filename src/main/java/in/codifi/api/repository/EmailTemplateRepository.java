package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.EmailTemplateEntity;

public interface EmailTemplateRepository extends CrudRepository<EmailTemplateEntity, Long> {

	EmailTemplateEntity findByKeyData(String keyData);
}
