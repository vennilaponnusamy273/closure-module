package in.codifi.api.service;

import java.io.File;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ClosureNSDLandCSDLEntity;
import in.codifi.api.entity.ClosurelogEntity;
import in.codifi.api.entity.DocumentEntity;
import in.codifi.api.model.ClientBasicData;
import in.codifi.api.model.DpResult;
import in.codifi.api.model.FormDataModel;
import in.codifi.api.model.ReKycResmodel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ClosureNSDLandCSDLRepository;
import in.codifi.api.repository.ClosurelogRepository;
import in.codifi.api.repository.DocumentRepository;
import in.codifi.api.service.spec.IClosureoService;
import in.codifi.api.trading.restservice.tradingRestServices;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@ApplicationScoped
public class ClosureoService  implements IClosureoService{
	
	private static String OS = System.getProperty("os.name").toLowerCase();
	private static final Logger logger = LogManager.getLogger(ClosureoService.class);
	@Inject 
	tradingRestServices TradingRestServices;
	@Inject
	CommonMethods commonMethods;
	@Inject
	ApplicationProperties props;
	@Inject
	DocumentRepository docrepository;
	@Inject
	ClosureNSDLandCSDLRepository closureNSDLandCSDLRepository;
	@Inject
	ClosurelogRepository closurelogRepository;

	@Override
	public ResponseModel CheckPositionHoldandfunds(String token) {
	    ResponseModel response = new ResponseModel();
	    ReKycResmodel reKycResmodel = new ReKycResmodel();
	    try {
	        String authToken = "Bearer " + token;
	        boolean positionStatus = false;
	        boolean fundsStatus = false;
	        boolean holdingsStatus = false;

	        // Check Positions
	        positionStatus = TradingRestServices.getPosition(authToken);
	        reKycResmodel.setPositions(positionStatus);
	        if (positionStatus) {
	            reKycResmodel.setPositions_remarks(MessageConstants.POSITIONS_EXIST);
	            response.setResult(reKycResmodel);
	        } else {
	            // Check Funds
	            fundsStatus = TradingRestServices.getFunds(authToken);
	            reKycResmodel.setFunds(fundsStatus);
	            if (fundsStatus) {
	                reKycResmodel.setFunds_remarks(MessageConstants.FUND_AVAILABLE);
	                saveRekycLog(token, reKycResmodel);
	                response.setResult(reKycResmodel);
	            } else {
	                // Check Holdings
	                holdingsStatus = TradingRestServices.getHoldings(authToken);
	                reKycResmodel.setHoldings(holdingsStatus);
	                if (holdingsStatus) {
	                    ClientBasicData clientBasicData = TradingRestServices.getUserDetails(token);
	                    if (clientBasicData != null) {
	                        DocumentEntity oldRecord = docrepository
	                                .findByApplicationIdAndDocumentType(clientBasicData.getTermCode(), EkycConstants.CMR_COPY);
	                        if (oldRecord != null) {
	                            reKycResmodel.setHoldings(false);
	                            reKycResmodel.setHoldings_remarks(MessageConstants.CMR_AVAILABLE);
	                            // Set the result based on your logic when CMR exists
	                            response.setResult(getDpDetails(token));
	                        } else {
	                            reKycResmodel.setHoldings_remarks(MessageConstants.HOLDINGS_EXIST);
	                            response.setResult(reKycResmodel);
	                        }
	                    }
	                    saveRekycLog(token, reKycResmodel);
	                }
	            }
	        }
	    } catch (Exception e) {
	        logger.error("An error occurred: " + e.getMessage());
	        commonMethods.SaveLog(null, "ClosureoService", "checkPositionHoldAndFunds", e.getMessage());
	        commonMethods.sendErrorMail(
	                "An error occurred while processing your ReEKYCService, In checkPositionHoldAndFunds for the Error: "
	                        + e.getMessage(),
	                "ERR-001");
	        response = commonMethods.constructFailedMsg(e.getMessage());
	    }
	    return response;
	}



	private void saveRekycLog(String authToken, ReKycResmodel reKycResmodel) {
	    try {
	        ClientBasicData clientBasicData = TradingRestServices.getUserDetails(authToken);
	        ClosurelogEntity closurelogEntity = closurelogRepository.findByUserId(clientBasicData.getTermCode());
	        if (closurelogEntity == null) {
	        	closurelogEntity = new ClosurelogEntity();
	        	closurelogEntity.setUserId(clientBasicData.getTermCode());
	        }
	        // Assuming that isPositions(), isHoldings(), and isFunds() return boolean values
	        closurelogEntity.setPosition(reKycResmodel.isPositions());
	        closurelogEntity.setHoldings(reKycResmodel.isHoldings());
	        closurelogEntity.setFunds(reKycResmodel.isFunds());

	        closurelogRepository.save(closurelogEntity);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	@Override
	public ResponseModel getDpDetails(String token) {
		ResponseModel response = new ResponseModel();
		try {
			String authToken = "Bearer " + token;
			List<DpResult> dpModel = TradingRestServices.getDpDetails(authToken);
			if (dpModel != null) {
				for (DpResult dpResult : dpModel) {
					ClosurelogEntity closurelogEntity = closurelogRepository.findByUserId(dpResult.getUserId());
					if (closurelogEntity == null) {
						closurelogEntity = new ClosurelogEntity();
						closurelogEntity.setUserId(dpResult.getUserId());
					}
					String dpId = dpResult.getDpId();
					if (dpId != null) {
					    if (dpId.startsWith("120")) {
					    	closurelogEntity.setCdsl(1);
					    } else if (dpId.startsWith("IN")) {
					    	closurelogEntity.setNsdl(1);
					    }
						 String existingDpId = closurelogEntity.getDpId();
				            if (existingDpId == null || !existingDpId.contains(dpId)) {
				                if (existingDpId != null && !existingDpId.isEmpty()) {
				                	closurelogEntity.setDpId(existingDpId + "," + dpId);
				                } else {
				                	closurelogEntity.setDpId(dpId);
				                }
				            }
				            closurelogRepository.save(closurelogEntity);
					}
				}
				response.setResult(dpModel);
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(null, "ClosureoService", "checkPositionHoldAndFunds", e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your ReEKYCService, In checkPositionHoldAndFunds for the Error: "
							+ e.getMessage(),
					"ERR-001");
			response = commonMethods.constructFailedMsg(e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseModel UploadCMR(FormDataModel  fileModel) {
		System.out.println("the UploadCMR service");
		ResponseModel responseModel = new ResponseModel();
		try {
			String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
			if (OS.contains(EkycConstants.OS_WINDOWS)) {
				slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
			}
			File dir = new File(props.getFileBasePath() + fileModel.getApplicationId());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			if (fileModel.getApplicationId() != null  && fileModel.getFile() != null
					&& StringUtil.isNotNullOrEmpty(fileModel.getFile().contentType())) {
				System.out.println("the UploadCMR service1");
				boolean content = (fileModel.getFile().contentType().equals(EkycConstants.CONST_APPLICATION_PDF));
				if (content) {
					String fileName = fileModel.getApplicationId() + EkycConstants.UNDERSCORE
							+ fileModel.getTypeOfProof() + EkycConstants.PDF_EXTENSION;
					String totalFileName = props.getFileBasePath() + fileModel.getApplicationId() + slash + fileName;
					Path path = fileModel.getFile().filePath();
					String errorMsg = checkPasswordProtected(fileModel);
					if (StringUtil.isNullOrEmpty(errorMsg)) {
						PDDocument document = PDDocument.load(new File(path.toString()), fileModel.getPassword());
						document.getClass();
						if (document.isEncrypted()) {
							document.setAllSecurityToBeRemoved(true);
							document.save(totalFileName);
							document.close();
							responseModel = saveDoc(fileModel, fileName, totalFileName,fileModel.getApplicationId());
						} else {
							document.save(totalFileName);
							document.close();
							responseModel = saveDoc(fileModel, fileName, totalFileName,fileModel.getApplicationId());
						}
					} else {
						return commonMethods.constructFailedMsg(errorMsg);
					}
				} else if (!content) {
					String errorMsg = checkValidate(fileModel);
					if (StringUtil.isNullOrEmpty(errorMsg)) {
						FileUpload f = fileModel.getFile();
						String ext = f.fileName().substring(f.fileName().indexOf("."), f.fileName().length());
						String fileName = fileModel.getApplicationId() + EkycConstants.UNDERSCORE
								+ fileModel.getTypeOfProof() + ext;
						String filePath = props.getFileBasePath() + fileModel.getApplicationId() + slash + fileName;
						Path path = Paths.get(filePath);
						if (Files.exists(path)) {
							Files.delete(path);
						}
						Files.copy(fileModel.getFile().filePath(), path);
						responseModel = saveDoc(fileModel, fileName, filePath,fileModel.getApplicationId());
					} else {
						return commonMethods.constructFailedMsg(errorMsg);
					}
				} else {
					responseModel.setMessage(EkycConstants.FAILED_MSG);
				}
			} else {
				responseModel.setMessage(EkycConstants.FAILED_MSG);
				if (fileModel.getFile() == null || StringUtil.isNullOrEmpty(fileModel.getFile().contentType())) {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.FILE_NULL);
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
				}
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request ClosureoService, In UploadCMR for the Error: " + e.getMessage(),
					"ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	public String checkPasswordProtected(FormDataModel fileModel) {
		String error = "";
		Path path = fileModel.getFile().filePath();
		PDDocument document = PDDocument.load(new File(path.toString()), fileModel.getPassword());
		if (document.isEncrypted()) {
			document.setAllSecurityToBeRemoved(true);
			document.close();
			error = "";
		} else {
			error = "";
		}
		return error;
	}
	
	public String checkValidate(FormDataModel data) {
	    List<String> mimetype = Arrays.asList("image/jpg", "image/jpeg", "image/gif", "image/png");

	    if (!mimetype.contains(data.getFile().contentType())) {
	        return "File not supported";
	    }

	    return "";
	}

	public ResponseModel saveDoc(FormDataModel data, String fileName, String filePath,String userId) {
		ResponseModel responseModel = new ResponseModel();
		try {
			String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
			if (OS.contains(EkycConstants.OS_WINDOWS)) {
				slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
			}
			DocumentEntity updatedDocEntity = null;
			DocumentEntity oldRecord = docrepository.findByApplicationIdAndDocumentType(data.getApplicationId(),
					data.getDocumentType());
			if (oldRecord != null) {
				oldRecord.setAttachement(fileName);
				oldRecord.setDocumentType(EkycConstants.CMR_COPY);
				if (StringUtil.isNotNullOrEmpty(data.getTypeOfProof())) {
					oldRecord.setTypeOfProof(data.getTypeOfProof());
				}
				oldRecord.setAttachementUrl(props.getFileBasePath() + data.getApplicationId() + slash + fileName);
				oldRecord.setPassword(data.getPassword());
				updatedDocEntity = docrepository.save(oldRecord);
			} else {
				DocumentEntity doc = new DocumentEntity();
				doc.setApplicationId(data.getApplicationId());
				doc.setDocumentType(EkycConstants.CMR_COPY);
				doc.setAttachement(fileName);
				if (StringUtil.isNotNullOrEmpty(data.getTypeOfProof())) {
					doc.setTypeOfProof(data.getTypeOfProof());
				}
				doc.setAttachementUrl(props.getFileBasePath() + data.getApplicationId() + slash + fileName);
				doc.setPassword(data.getPassword());
				updatedDocEntity = docrepository.save(doc);
			}
			if (updatedDocEntity != null) {
				ClosurelogEntity closurelogEntity = closurelogRepository.findByUserId(userId);
			        if (closurelogEntity == null) {
			        	closurelogEntity = new ClosurelogEntity();
			            closurelogEntity.setUserId(userId);
			        }
			        closurelogEntity.setCmrpath(updatedDocEntity.getAttachementUrl());
			        closurelogRepository.save(closurelogEntity);
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				responseModel.setResult(updatedDocEntity);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.FAILED_DOC_UPLOAD);
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request ClosureoService, In saveDoc for the Error: " + e.getMessage(),
					"ERR-001");
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	@Override
	public Response GeneratePdf(String token) {
	    String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
	    if (OS.contains(EkycConstants.OS_WINDOWS)) {
	        slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
	    }
	    try {
	        ClientBasicData clientBasicData = TradingRestServices.getUserDetails(token);

	        if (clientBasicData != null) {
	        	 HashMap<String, String> map = mapping(clientBasicData);

	            File file;
	            if (map.get("NSDLPDF")!=null) {
	                file = new File(props.getNsdlpdf());
	            } else if (map.get("CSDLPDF")!=null) {
	                file = new File(props.getCsdlpdf());
	            } else {
	                return Response.status(Response.Status.BAD_REQUEST)
	                        .entity("Invalid value for WayofPdf: ").build();
	            }
	            System.out.println("the file"+file);
	            PDDocument document = PDDocument.load(file);

	           

	            String outputPath = props.getFileBasePath() + clientBasicData.getTermCode();
	            new File(outputPath).mkdir();

	            List<ClosureNSDLandCSDLEntity> pdfDatas = closureNSDLandCSDLRepository.getCoordinates();
	            pdfInsertCoordinates(document, pdfDatas, map, clientBasicData);

	            String fileName = clientBasicData.getTermCode() + EkycConstants.PDF_EXTENSION;
	            document.save(outputPath + slash + fileName);
	            document.close();

	            String contentType = URLConnection.guessContentTypeFromName(fileName);
	            String path = outputPath + slash + fileName;
	            File savedFile = new File(path);

	            ResponseBuilder response = Response.ok(savedFile);
	            response.type(contentType);
	            response.header("Content-Disposition", "attachment;filename=" + savedFile.getName());
	            return response.build();
	        } else {
	            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	                    .entity(MessageConstants.USER_ID_INVALID).build();
	        }
	    } catch (Exception e) {
	        e.printStackTrace(); // Handle exceptions properly in a production environment
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	                .entity(MessageConstants.FILE_NOT_FOUND).build();
	    }
	}


	private void pdfInsertCoordinates(PDDocument document, List<ClosureNSDLandCSDLEntity> pdfDatas,
			HashMap<String, String> map, ClientBasicData clientBasicData) {
		try {
		File fontFile = new File(props.getPdfFontFile());
		PDFont font = PDTrueTypeFont.loadTTF(document, fontFile);
		for (ClosureNSDLandCSDLEntity pdfData : pdfDatas) {
			float x = Float.parseFloat(pdfData.getXCoordinate());
			float y = Float.parseFloat(pdfData.getYCoordinate());
			int pageNo = Integer.parseInt(pdfData.getPageNo());
			PDPage page = document.getPage(pageNo);
			PDPageContentStream contentStream = new PDPageContentStream(document, page, true, true);
			contentStream.setFont(font, 7);
			PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
			graphicsState.setNonStrokingAlphaConstant(1f);
			contentStream.setGraphicsStateParameters(graphicsState);
			contentStream.setCharacterSpacing(0.4f);
			String columnType = pdfData.getColumnType();
			String columnNames = pdfData.getColumnNames();
			if (columnType.equalsIgnoreCase("CSDL")&& map.get("CSDLPDF") != null) {
				contentStream.beginText();
				contentStream.setNonStrokingColor(0, 0, 0);
				contentStream.newLineAtOffset(x, y);
				String inputText = map.get(columnNames);
				if (inputText != null) {
					inputText = inputText.replaceAll("\n", " ");
					contentStream.showText(inputText.toUpperCase());
				}
				contentStream.endText();
			} else if (columnType.equalsIgnoreCase("NSDL")&& map.get("NSDLPDF") != null) {
				contentStream.beginText();
				contentStream.setNonStrokingColor(0, 0, 0);
				contentStream.newLineAtOffset(x, y);
				String inputText = map.get(columnNames);
				if (inputText != null) {
					inputText = inputText.replaceAll("\n", " ");
					contentStream.showText(inputText.toUpperCase());
				}
				contentStream.endText();
			}  else if (columnType.equalsIgnoreCase("CSDLtick")) {
				String tick = "\u2713";
				String inputText = map.get(columnNames);
				if (inputText != null) {
					contentStream.beginText();
					contentStream.setFont(PDType1Font.ZAPF_DINGBATS, 12);
					contentStream.setNonStrokingColor(0, 0, 0);
					contentStream.newLineAtOffset(x, y);
					contentStream.showText(tick);
					contentStream.endText();
				}
			} 
			contentStream.close();
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private HashMap<String, String> mapping(ClientBasicData clientBasicData) {

		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date();

		// Format the current date using the specified pattern
		String formattedDate = formatter.format(date);

		// Populate the map with individual digits
		HashMap<String, String> map = new HashMap<>();
		map.put("Date", formattedDate); // Day
		map.put("Date1", String.valueOf(formattedDate.charAt(0))); // Day
		map.put("Date2", String.valueOf(formattedDate.charAt(1))); // Day
		map.put("Date3", String.valueOf(formattedDate.charAt(3))); // Month
		map.put("Date4", String.valueOf(formattedDate.charAt(4))); // Month
		map.put("Date5", String.valueOf(formattedDate.charAt(6))); // Year
		map.put("Date6", String.valueOf(formattedDate.charAt(7))); // Year
		map.put("Date7", String.valueOf(formattedDate.charAt(8))); // Separator (-)
		map.put("Date8", String.valueOf(formattedDate.charAt(9))); // Separator (-)

		// Print the map
		map.forEach((key, value) -> System.out.println(key + ": " + value));
		map.put("NSDLPDF", "NSDLPDF");
		String tradingId = clientBasicData.getOwnCode();
		if (tradingId != null && tradingId.length() >= 9) {
		    map.put("TRADING ID:1", String.valueOf(tradingId.charAt(0)));
		    map.put("TRADING ID:2", String.valueOf(tradingId.charAt(1)));
		    map.put("TRADING ID:3", String.valueOf(tradingId.charAt(2)));
		    map.put("TRADING ID:4", String.valueOf(tradingId.charAt(3)));
		    map.put("TRADING ID:5", String.valueOf(tradingId.charAt(4)));

		    map.put("TRADING ID:6", String.valueOf(tradingId.charAt(5)));
		    map.put("TRADING ID:7", String.valueOf(tradingId.charAt(6)));
		    map.put("TRADING ID:8", String.valueOf(tradingId.charAt(7)));
		    map.put("TRADING ID:9", String.valueOf(tradingId.charAt(8)));
		    map.put("TRADING ID:10", String.valueOf(tradingId.charAt(9)));
		}
		String clientId = clientBasicData.getTermCode();
		if (clientId != null && clientId.length() >= 6) {
		    map.put("Client ID1", String.valueOf(clientId.charAt(0)));
		    map.put("Client ID2", String.valueOf(clientId.charAt(1)));
		    map.put("Client ID3", String.valueOf(clientId.charAt(2)));
		    map.put("Client ID4", String.valueOf(clientId.charAt(3)));
		    map.put("Client ID5", String.valueOf(clientId.charAt(4)));
		    map.put("Client ID6", String.valueOf(clientId.charAt(5)));
		}
		
		 map.put("Name of the First / Sole Holder",clientBasicData.getNameAsperPan());
		 map.put("Address for Correspondence",clientBasicData.getCorraddress1()+" "+clientBasicData.getCorraddress2());
		 map.put("Address for Correspondence1",clientBasicData.getCorraddress3());
		 map.put("City",clientBasicData.getCity());
		 map.put("State",clientBasicData.getState());
		 String pincode=clientBasicData.getPincode();
		 if(pincode!=null &&pincode.length()>=5) {
		 map.put("PIN1",String.valueOf(pincode.charAt(0)));
		 map.put("PIN2",String.valueOf(pincode.charAt(1)));
		 map.put("PIN3",String.valueOf(pincode.charAt(2)));
		 map.put("PIN4",String.valueOf(pincode.charAt(3)));
		 map.put("PIN5",String.valueOf(pincode.charAt(4)));
		 map.put("PIN6",String.valueOf(pincode.charAt(5)));
		 }
		return  map;
	}



	@Override
	public ResponseModel getRekycLogs(String userId) {
		ResponseModel responseModel=new ResponseModel();
		ClosurelogEntity closurelogEntity = closurelogRepository.findByUserId(userId);
	        if (closurelogEntity != null) {
	        	responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				responseModel.setStat(EkycConstants.SUCCESS_STATUS);
	        	responseModel.setResult(closurelogEntity);
	        }else {
	        	responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
	        }
			return responseModel;
	}
	
}
