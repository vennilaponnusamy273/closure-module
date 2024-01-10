package in.codifi.api.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import in.codifi.api.entity.ClosureNSDLandCSDLEntity;

public interface ClosureNSDLandCSDLRepository extends CrudRepository<ClosureNSDLandCSDLEntity, Long>  {

	
	@Transactional
	@Query(value = " SELECT A FROM tbl_closure_nsdlcsdl_coordinates_chola  A WHERE A.activeStatus = 1")
	List<ClosureNSDLandCSDLEntity> getCoordinates();
	
	List<ClosureNSDLandCSDLEntity> findByColumnNamesAndColumnTypeAndActiveStatus(String columnNames, String columnType,int activeStatus);

}
