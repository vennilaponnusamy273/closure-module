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
	 
	 ClosurelogEntity findByUserIdAndMobile(String termCode,String mobile);
	 
	 ClosurelogEntity findByIdAndUserId(long id,String userId);
	 
	 @Query("SELECT COUNT(ce) AS recordCount, " +
		       "SUM(CASE WHEN ce.adminstatus = 1 THEN 1 ELSE 0 END) AS approvedCount, " +
		       "SUM(CASE WHEN ce.adminstatus = 2 THEN 1 ELSE 0 END) AS rejectedCount, " +
		       "SUM(CASE WHEN ce.adminstatus = 0 THEN 1 ELSE 0 END) AS unknownCount, " +
		       "SUM(CASE WHEN ce.approveOtpVerified = 1 THEN 1 ELSE 0 END) AS verifiedCount, " +
		       "SUM(CASE WHEN ce.approveOtpVerified = 0 THEN 1 ELSE 0 END) AS notVerifiedCount " +
		       "FROM tbl_closure_log ce " +
		       "WHERE ce.createdOn BETWEEN :fromDateTime AND :toDateTime")
		List<Object[]> findStatus(@Param("fromDateTime") Date fromDateTime,
		                          @Param("toDateTime") Date toDateTime);
		
		
//		@Query("SELECT ce FROM tbl_closure_log ce " +
//			       "WHERE ce.adminstatus <> 1 " +
//			       "AND STR_TO_DATE(ce.approveOtpSendDate, '%d/%m/%Y/%H:%i:%s') >= FUNCTION('DATE_ADD', CURRENT_DATE, -7, 'DAY') " +
//			       "AND STR_TO_DATE(ce.approveOtpSendDate, '%d/%m/%Y/%H:%i:%s') < FUNCTION('DATE_ADD', CURRENT_DATE, -6, 'DAY')")
//			List<ClosurelogEntity> findRecordsToAutoReject();
}
