package in.codifi.api.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import in.codifi.api.entity.ClosurelogEntity;

public interface ClosurelogRepository extends CrudRepository<ClosurelogEntity, Long> {
	ClosurelogEntity findByUserId(String termCode);
	 @Query("SELECT ce FROM tbl_closure_log ce WHERE ce.userId = :userId AND ce.createdOn BETWEEN :fromDate AND :toDate")
	   List<ClosurelogEntity> findByUserIdAndDate(
	            @Param("userId") String userId,
	            @Param("fromDate") Date fromDate,
	            @Param("toDate") Date toDate
	    );
	 
	 @Query("SELECT ce FROM tbl_closure_log ce WHERE ce.createdOn BETWEEN :fromDateTime AND :toDateTime")
	 List<ClosurelogEntity> findByDate(
	         @Param("fromDateTime") Date fromDateTime,
	         @Param("toDateTime") Date toDateTime
	 );
}
