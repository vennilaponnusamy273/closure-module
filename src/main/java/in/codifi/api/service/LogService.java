package in.codifi.api.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.LogRepository;
import in.codifi.api.service.spec.ILogService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class LogService implements ILogService {

	@Inject
	LogRepository repository;
	@Inject
	CommonMethods commonMethods;

	@Scheduled(cron = "0 0 1 ? * MON") // Run every Monday at 1:00 AM
	public void checkRestAccessLogTableRun() {
		checkRestAccessLogTable();
	}

	@Scheduled(cron = "0 0 1 ? * MON") // Run every Monday at 1:00 AM
	public void checkRestServiceAccessLogTableRun() {
		checkRestServiceAccessLogTable();
	}

	/**
	 * method to check rest access log table
	 * 
	 */
	@Override
	public ResponseModel checkRestAccessLogTable() {
		ResponseModel responseModel = new ResponseModel();
		try {
			/** to get total number of table names from specific database **/
			List<String> existingTable = repository.getExistingTables();
			List<String> tableToCreate = new ArrayList<>();
			LocalDate currentDate = LocalDate.now();
			String tableName = "";
			for (int i = 0; i <= 7; i++) {
				LocalDate local = currentDate.plusDays(i);
				DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("ddMMYYYY");
				String formattedDate = dateTimeFormatter.format(local);
				tableName = "tbl_" + formattedDate + "_access_log";
				if (!existingTable.contains(tableName)) {
					tableToCreate.add(tableName);
				}

			}
			/** if table not exist from database to create the tables **/
			repository.createTables(tableToCreate);
			responseModel.setStat(EkycConstants.SUCCESS_STATUS);
			responseModel.setMessage(EkycConstants.TABLE_CREATED);
			return responseModel;

		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		}
		responseModel = commonMethods.constructFailedMsg(MessageConstants.FAILED);
		return responseModel;
	}

	@Override
	public ResponseModel checkRestServiceAccessLogTable() {
		ResponseModel responseModel = new ResponseModel();
		try {
			/** to get total number of table names from specific database **/
			List<String> existingTable = repository.getExistingTables();
			List<String> tableToCreate = new ArrayList<>();
			LocalDate currentDate = LocalDate.now();
			String tableName = "";
			for (int i = 0; i <= 7; i++) {
				LocalDate local = currentDate.plusDays(i);
				DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("ddMMYYYY");
				String formattedDate = dateTimeFormatter.format(local);
				tableName = "tbl_" + formattedDate + "_rest_access_log";
				if (!existingTable.contains(tableName)) {
					tableToCreate.add(tableName);
				}

			}
			/** if table not exist from database to create the tables **/
			repository.createRestTable(tableToCreate);
			responseModel.setStat(EkycConstants.SUCCESS_STATUS);
			responseModel.setMessage(EkycConstants.TABLE_CREATED);
			return responseModel;

		} catch (Exception e) {
			Log.error(e.getMessage());
			e.printStackTrace();
		}
		responseModel = commonMethods.constructFailedMsg(MessageConstants.FAILED);
		return responseModel;
	}

}
