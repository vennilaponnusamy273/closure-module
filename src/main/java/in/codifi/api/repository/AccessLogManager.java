package in.codifi.api.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import in.codifi.api.entity.logs.AccessLogModel;
import in.codifi.api.entity.logs.RestAccessLogModel;
import io.quarkus.logging.Log;

@ApplicationScoped
public class AccessLogManager {
	@Inject
	@Named("logs")
	DataSource dataSource;

	/**
	 * method to insert access logs into data base
	 * 
	 * @author sowmiya
	 * @param accessLogModel
	 */
	public void insertAccessLogsIntoDB(AccessLogModel accLogModel) {
		try {

			Date inTimeDate = new Date();
			Connection connection = null;
			Statement state = null;
			PreparedStatement statement = null;
			String date = new SimpleDateFormat("ddMMYYYY").format(inTimeDate);
			String tableName = "tbl_" + date + "_access_log";
			try {

				connection = dataSource.getConnection();
				state = connection.createStatement();

				String insertQuery = "INSERT INTO " + tableName
						+ "(application_id, uri, method, req_id, req_body, res_body, user_agent, device_ip, content_type, session, inTime, outTime, domain "
						+ " ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

				statement = connection.prepareStatement(insertQuery);
				int paramPos = 1;
				statement.setString(paramPos++, accLogModel.getApplicationId());
				statement.setString(paramPos++, accLogModel.getUri());
				statement.setString(paramPos++, accLogModel.getMethod());
				statement.setString(paramPos++, accLogModel.getReqId());
				statement.setString(paramPos++, accLogModel.getReqBody());
				statement.setString(paramPos++, accLogModel.getResBody());
				statement.setString(paramPos++, accLogModel.getUserAgent());
				statement.setString(paramPos++, accLogModel.getDeviceIp());
				statement.setString(paramPos++, accLogModel.getContentType());
				statement.setString(paramPos++, accLogModel.getSession());
				statement.setTimestamp(paramPos++, accLogModel.getInTime());
				statement.setTimestamp(paramPos++, accLogModel.getOutTime());
				statement.setString(paramPos++, accLogModel.getDomain());
				statement.executeUpdate();

				statement.close();
				state.close();
				connection.close();
			} catch (Exception e) {
				Log.error("Ekyc - insertAccessLog -" + e);
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (state != null) {
						state.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error("chola - insertAccessLogsIntoDB - ", e);
		}
	}

	public void insertRestAccessLogsIntoDB(String applicationId, String Req, String Res, String Method, String Uri) {
		try {
			RestAccessLogModel accRestLogModel = new RestAccessLogModel();
			accRestLogModel.setApplicationId(applicationId);
			accRestLogModel.setReqBody(Req);
			accRestLogModel.setResBody(Res);
			accRestLogModel.setMethod(Method);
			accRestLogModel.setUri(Uri);
			Date inTimeDate = new Date();
			Connection connection = null;
			Statement state = null;
			PreparedStatement statement = null;
			String date = new SimpleDateFormat("ddMMYYYY").format(inTimeDate);
			String tableName = "tbl_" + date + "_rest_access_log";
			try {
				connection = dataSource.getConnection();
				state = connection.createStatement();

				String insertQuery = "INSERT INTO " + tableName + "(application_id, uri, method,req_body, res_body"
						+ " ) VALUES ( ?, ?, ?,?, ?)";

				statement = connection.prepareStatement(insertQuery);
				int paramPos = 1;
				statement.setString(paramPos++, accRestLogModel.getApplicationId());
				statement.setString(paramPos++, accRestLogModel.getUri());
				statement.setString(paramPos++, accRestLogModel.getMethod());
				statement.setString(paramPos++, accRestLogModel.getReqBody());
				statement.setString(paramPos++, accRestLogModel.getResBody());
				statement.executeUpdate();
				statement.close();
				state.close();
				connection.close();
			} catch (Exception e) {
				Log.error("Ekyc - insertAccessLog -" + e);
			} finally {
				try {
					if (statement != null) {
						statement.close();
					}
					if (state != null) {
						state.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error("chola- insertRestAccessLogsIntoDB - ", e);
		}

	}
}
