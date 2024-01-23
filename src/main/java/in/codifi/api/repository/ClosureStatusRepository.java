package in.codifi.api.repository;

import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.ClosureStatusEntity;

public interface ClosureStatusRepository  extends CrudRepository<ClosureStatusEntity,Long>{

	ClosureStatusEntity  findByUserId(String userId);
	void deleteByUserId(String userId);
}
