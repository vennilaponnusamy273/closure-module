package in.codifi.api.utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nsdl.esign.preverifiedNo.controller.EsignApplication;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ClosureNSDLandCSDLEntity;
import in.codifi.api.entity.ClosureTxnDetailsEntity;
import in.codifi.api.model.ClientBasicData;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ClosureNSDLandCSDLRepository;
import in.codifi.api.repository.ClosureTxnDetailsRepository;
import in.codifi.api.trading.restservice.tradingRestServices;
import io.smallrye.common.constraint.NotNull;

@ApplicationScoped
@Service
public class Esign {

	@Inject 
	tradingRestServices TradingRestServices;
	@Inject
	ApplicationProperties props;
	@Inject
	ClosureTxnDetailsRepository txnDetailsRepository;
	@Inject
	CommonMethods commonMethods;
	@Inject
	ClosureNSDLandCSDLRepository closureNSDLandCSDLRepository;
	private static String OS = System.getProperty("os.name").toLowerCase();
	
	public static void main(String[] args) throws IOException {
	}

	public ResponseModel runMethod(String OutPutPath, @NotNull String applicationId,String Token,String DpID) {
		ResponseModel responseModel = new ResponseModel();
		String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
		if (OS.contains(EkycConstants.OS_WINDOWS)) {
			slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
		}
		ClientBasicData clientBasicData = TradingRestServices.getUserDetails(Token);
		System.out.println("the path"+OutPutPath + applicationId + slash + DpID+ EkycConstants.PDF_EXTENSION);
		String getXml = getXmlForEsignSinglePage(
				OutPutPath + applicationId + slash + DpID + EkycConstants.PDF_EXTENSION,
				applicationId,clientBasicData,DpID);
		System.out.println("the getXmlForEsignSinglePage&"+getXml);
		long timeInmillsecods = System.currentTimeMillis();
		String folderName = String.valueOf(timeInmillsecods);
		if (getXml != null) {
			String filePath = props.getFileBasePath() + applicationId + slash + folderName;
			System.out.println("the getXml"+getXml);
			toCreateNewXMLFile(filePath, getXml);
			String txnId = toGetTxnFromXMlpath(filePath + slash + "FirstResponse.xml");
			if (StringUtil.isNotNullOrEmpty(txnId)) {
				ClosureTxnDetailsEntity savingEntity = new ClosureTxnDetailsEntity();
				savingEntity.setApplicationId(applicationId);
				savingEntity.setTxnId(txnId);
				savingEntity.setFolderLocation(filePath);
				savingEntity.setDpId(DpID);
				savingEntity.setUsername(clientBasicData.getNameAsperPan());
				savingEntity.setCity(clientBasicData.getCity());
				savingEntity.setEmailID(clientBasicData.getEmail());
				savingEntity.setMobileNo(clientBasicData.getMobile());
				ClosureTxnDetailsEntity savedEntity = txnDetailsRepository.save(savingEntity);
				if (savedEntity != null) {
					StringBuilder buff = new StringBuilder();
					buff.append(getXml);
					System.out.println("the buff"+buff);
					responseModel.setResult(buff);
					responseModel.setMessage(EkycConstants.SUCCESS_MSG);
					responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.ERROR_WHILE_CREATING_XML);
				}
			}
		}
		return responseModel;
	}
	
	public static String toGetTxnFromXMlpath(String xmlPath) {
		String txnId = "";
		try {
			File fXmlFile = new File(xmlPath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			Element eElement = doc.getDocumentElement();
			txnId = eElement.getAttribute("txn");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return txnId;
	}
	
	private static String toCreateNewXMLFile(String xmlPath, String getXml) {
		try {
			String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
			if (OS.contains(EkycConstants.OS_WINDOWS)) {
				slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
			}
			File chekcFile = new File(xmlPath);
			File myObj = new File(xmlPath + slash + "FirstResponse.xml");
			if (!chekcFile.exists()) {
				chekcFile.mkdirs();
			}
			if (myObj.createNewFile()) {
				FileWriter myWriter = new FileWriter(xmlPath + slash + "FirstResponse.xml");
				myWriter.write(getXml);
				myWriter.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getXml;
	}
	
	private String getXmlForEsignSinglePage(String outPutPath, String applicationId,ClientBasicData clientBasicData,String DPId) {
		String response = "";
		try {
			String ekycID = "";
			String pdfReadServerPath = outPutPath;
			String aspId = props.getEsignAspId();
			String authMode = "1";
			String responseUrl = props.getESignReturnUrl();
			String p12CertificatePath = props.getEsignLocation();
			String p12CertiPwd = props.getEsignPassword();
			String tickImagePath = props.getEsignTickImage();
			int serverTime = 0;
			String alias = props.getEsignAlias();
			String pdfPassword = "";
			String txn = "";
			String reasonForSign = "";
			String city = clientBasicData.getCity().toUpperCase();
			String nameToShowOnSignatureStamp = clientBasicData.getNameAsperPan().toUpperCase();
			String locationToShowOnSignatureStamp = city;
			// Get PDF data coordinates from database
			String wayofPDF=null;
			if (DPId != null) {
			    if (DPId.startsWith("120")) {
			    	wayofPDF="CDSL";
			    } else if (DPId.startsWith("IN")) {
			    	wayofPDF="NSDL";
			    }
			
			  List<ClosureNSDLandCSDLEntity> coordinatesList = closureNSDLandCSDLRepository.findByColumnNamesAndColumnTypeAndActiveStatus("esign",wayofPDF,1);
			if (coordinatesList != null) {
				ArrayList<Integer> xCoordinatesList = new ArrayList<>();
				ArrayList<Integer> yCoordinatesList = new ArrayList<>();
				ArrayList<Integer> PageNo = new ArrayList<>();
				ArrayList<Integer> height = new ArrayList<>();
				ArrayList<Integer> width = new ArrayList<>();

				
				// Loop through coordinates and add to respective lists
				for (ClosureNSDLandCSDLEntity entity : coordinatesList) {
					int xCoordinate = Integer.parseInt(entity.getXCoordinate());
					int yCoordinate = Integer.parseInt(entity.getYCoordinate());
					int pageNumber = Integer.parseInt(entity.getPageNo());
					xCoordinatesList.add(xCoordinate);
					yCoordinatesList.add(yCoordinate);
					PageNo.add(pageNumber + 1);
					height.add(30); 
					width.add(100); 
				}

				EsignApplication eSignApp = new EsignApplication();
				response = eSignApp.getEsignRequestXml(ekycID, pdfReadServerPath, aspId, authMode, responseUrl,
						p12CertificatePath, p12CertiPwd, tickImagePath, serverTime, alias, nameToShowOnSignatureStamp,
						locationToShowOnSignatureStamp, reasonForSign, pdfPassword, txn, PageNo, xCoordinatesList,
						yCoordinatesList, height, width);
			}}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
	public String getSignFromNsdl(String documentLocation, String documentToBeSavedLocation, String receivedXml,
			String applicantName, String city, String DPId) {
		String responseText = null;
		try {
			System.out.println("the documentLocation"+documentLocation);
			System.out.println("the documentToBeSavedLocation"+documentToBeSavedLocation);
			String pathToPDF = documentLocation;
			String tickImagePath = props.getEsignTickImage();
			int serverTime = 0;//	
			String nameToShowOnSignatureStamp = applicantName;
			String locationToShowOnSignatureStamp = city.toUpperCase();
			String reasonForSign = "";
			String pdfPassword = "";
			String esignXml = receivedXml;
			String returnPath = documentToBeSavedLocation;
			try {
				EsignApplication eSignApp = new EsignApplication();
				String wayofPDF=null;
				if (DPId != null) {
				    if (DPId.startsWith("120")) {
				    	wayofPDF="CDSL";
				    } else if (DPId.startsWith("IN")) {
				    	wayofPDF="NSDL";
				    }
				
				  List<ClosureNSDLandCSDLEntity> coordinatesList = closureNSDLandCSDLRepository.findByColumnNamesAndColumnTypeAndActiveStatus("esign",wayofPDF, 1);
					if (coordinatesList != null) {
						ArrayList<Integer> xCoordinatesList = new ArrayList<>();
						ArrayList<Integer> yCoordinatesList = new ArrayList<>();
						ArrayList<Integer> PageNo = new ArrayList<>();
						ArrayList<Integer> height = new ArrayList<>();
						ArrayList<Integer> width = new ArrayList<>();

						
						// Loop through coordinates and add to respective lists
						for (ClosureNSDLandCSDLEntity entity : coordinatesList) {
							int xCoordinate = Integer.parseInt(entity.getXCoordinate());
							int yCoordinate = Integer.parseInt(entity.getYCoordinate());
							int pageNumber = Integer.parseInt(entity.getPageNo());
							xCoordinatesList.add(xCoordinate);
							yCoordinatesList.add(yCoordinate);
							PageNo.add(pageNumber + 1);
							height.add(40); // Change this to the actual height value
							width.add(100); // Change this to the actual width value
						}
					responseText = eSignApp.getSignOnDocument(esignXml, pathToPDF, tickImagePath, serverTime,
							nameToShowOnSignatureStamp, locationToShowOnSignatureStamp, reasonForSign, pdfPassword,
							returnPath, PageNo, xCoordinatesList, yCoordinatesList, height, width);
				}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseText;
	}
	
}
