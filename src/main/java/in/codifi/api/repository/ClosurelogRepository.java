package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.ClosurelogEntity;

public interface ClosurelogRepository extends CrudRepository<ClosurelogEntity, Long> {
	ClosurelogEntity findByUserId(String termCode);
}
