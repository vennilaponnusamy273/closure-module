package in.codifi.api.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import in.codifi.api.cache.HazleCacheController;
import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ClosureNSDLandCSDLEntity;
import in.codifi.api.entity.ClosurelogEntity;
import in.codifi.api.entity.DocumentEntity;
import in.codifi.api.entity.TxnDetailsEntity;
import in.codifi.api.helper.ClosureHelper;
import in.codifi.api.model.ClientBasicData;
import in.codifi.api.model.DpResult;
import in.codifi.api.model.FormDataModel;
import in.codifi.api.model.PdfApplicationDataModel;
import in.codifi.api.model.ReKycResmodel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ClosureNSDLandCSDLRepository;
import in.codifi.api.repository.ClosurelogRepository;
import in.codifi.api.repository.DocumentRepository;
import in.codifi.api.repository.TxnDetailsRepository;
import in.codifi.api.service.spec.IClosureService;
import in.codifi.api.trading.restservice.tradingRestServices;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.Esign;
import in.codifi.api.utilities.MessageConstants;
import in.codifi.api.utilities.StringUtil;

@ApplicationScoped
public class ClosureService  implements IClosureService{//Closure
	
	private static String OS = System.getProperty("os.name").toLowerCase();
	private static final Logger logger = LogManager.getLogger(ClosureService.class);
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
	@Inject
	TxnDetailsRepository txnDetailsRepository;
	@Inject
	Esign esign;
	@Inject
	ClosureHelper closureHelper;

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
	            saveRekycLog(token, reKycResmodel);
	            response.setResult(reKycResmodel);
	        } else {
	        	reKycResmodel.setPositions_remarks(MessageConstants.POSITIONS_NOT_EXIST);
	            fundsStatus = TradingRestServices.getFunds(authToken);
	            reKycResmodel.setFunds(fundsStatus);
	            if (fundsStatus) {
	                reKycResmodel.setFunds_remarks(MessageConstants.FUND_AVAILABLE);
	                saveRekycLog(token, reKycResmodel);
	                response.setResult(reKycResmodel);
	            } else {
	            	reKycResmodel.setFunds_remarks(MessageConstants.FUNDS_NOT_EXIST);
	                holdingsStatus = TradingRestServices.getHoldings(authToken);
	                reKycResmodel.setHoldings(holdingsStatus);
	                if (holdingsStatus) {
	                    ClientBasicData clientBasicData = TradingRestServices.getUserDetails(token);
	                    if (clientBasicData != null) {
	                        DocumentEntity oldRecord = docrepository
	                                .findByApplicationIdAndDocumentType(clientBasicData.getTermCode(), EkycConstants.CMR_COPY);
	                        DocumentEntity oldRecordsign = docrepository
	                                .findByApplicationIdAndDocumentType(clientBasicData.getTermCode(), EkycConstants.CLOSURE_SIGN);
	                        if (oldRecord != null&&oldRecordsign!=null) {
	                            reKycResmodel.setHoldings(false);
	                            reKycResmodel.setHoldings_remarks(MessageConstants.CMR_AVAILABLE);
	                            response.setStat(EkycConstants.SUCCESS_STATUS);
	                            response.setMessage(EkycConstants.SUCCESS_MSG);
	                            response.setResult(reKycResmodel);
	                            saveRekycLog(token, reKycResmodel);
	                        } else {
	                            reKycResmodel.setHoldings_remarks(MessageConstants.HOLDINGS_EXIST);
	                            response.setResult(reKycResmodel);
	                            saveRekycLog(token, reKycResmodel);
	                        }
	                    }
	                    
	                }
	            }
	            if (!positionStatus && !fundsStatus && !holdingsStatus) {
	                // All statuses are false, set a common remark or message
	            	 saveRekycLog(token, reKycResmodel);
	            	 response.setStat(EkycConstants.SUCCESS_STATUS);
	            	 response.setMessage(EkycConstants.SUCCESS_MSG);
	            	 response.setReason(MessageConstants.NOT_AVAILABLE_POSITIONS);
	            	 response.setResult(reKycResmodel);
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
			if (fileModel.getApplicationId()!=null  && fileModel.getFile() != null
					&& StringUtil.isNotNullOrEmpty(fileModel.getFile().contentType())) {
				System.out.println("the UploadCMR service1");
				boolean content = (fileModel.getFile().contentType().equals(EkycConstants.CONST_APPLICATION_PDF));
				 if (content) {
		                String fileName = fileModel.getApplicationId() + EkycConstants.UNDERSCORE
		                        + fileModel.getTypeOfProof() + EkycConstants.PDF_EXTENSION;
		                String totalFileName = props.getFileBasePath() + fileModel.getApplicationId() + slash + fileName;
		                Path path = fileModel.getFile().filePath();
		                PDDocument document = PDDocument.load(new File(path.toString()));
		                try {
		                    // document.getClass();
		                    if (document.isEncrypted()) {
		                        return commonMethods.constructFailedMsg(MessageConstants.PDF_ENCRYPTED);
		                    } else {
		                        document.save(totalFileName);
		                        responseModel = saveDoc(fileModel, fileName, totalFileName,fileModel.getApplicationId(), fileModel.getDocumentType());
		                    }
		                } finally {
		                    document.close();
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
						responseModel = saveDoc(fileModel, fileName, filePath,fileModel.getApplicationId(),fileModel.getDocumentType());
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
		        if (!(e instanceof IOException )) {
		            // Log and send email only if it's not an expected PDF encryption issue
		            logger.error("An error occurred: " + e.getMessage());
		            commonMethods.sendErrorMail(
		                    "An error occurred while processing your request ClosureoService, In UploadCMR for the Error: " + e.getMessage(),
		                    "ERR-001");
		        }
		        responseModel = commonMethods.constructFailedMsg(MessageConstants.PDF_ENCRYPTED);
		    }
		    return responseModel;
		}

	public String checkPasswordProtected(FormDataModel fileModel) {
		String error = "";
		Path path = fileModel.getFile().filePath();
		PDDocument document = PDDocument.load(new File(path.toString()));
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
	    List<String> mimetype = Arrays.asList("image/jpg", "image/jpeg","image/png");
	    if (!mimetype.contains(data.getFile().contentType())) {
	        return "File not supported";
	    }
	    return "";
	}

	public ResponseModel saveDoc(FormDataModel data, String fileName, String filePath,String userId,String docType) {
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
				oldRecord.setDocumentType(docType);
				if (StringUtil.isNotNullOrEmpty(data.getTypeOfProof())) {
					oldRecord.setTypeOfProof(data.getTypeOfProof());
				}
				oldRecord.setAttachementUrl(props.getFileBasePath() + data.getApplicationId() + slash + fileName);
				updatedDocEntity = docrepository.save(oldRecord);
			} else {
				DocumentEntity doc = new DocumentEntity();
				doc.setApplicationId(data.getApplicationId());
				doc.setDocumentType(docType);
				doc.setAttachement(fileName);
				if (StringUtil.isNotNullOrEmpty(data.getTypeOfProof())) {
					doc.setTypeOfProof(data.getTypeOfProof());
				}
				doc.setAttachementUrl(props.getFileBasePath() + data.getApplicationId() + slash + fileName);
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
	@Transactional
	public Response GeneratePdf(String token,String dpId) {
	    String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
	    if (OS.contains(EkycConstants.OS_WINDOWS)) {
	        slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
	    }
	    try {
	        ClientBasicData clientBasicData = TradingRestServices.getUserDetails(token);

	        if (clientBasicData != null) {
	        	 HashMap<String, String> map = mapping(clientBasicData,dpId);
					File filensdl = new File(props.getNsdlpdf());
					File filecdsl = new File(props.getCsdlpdf());
					PDDocument document;
					if (map.get("NSDLPDF") != null) {
						document = PDDocument.load(filensdl);
					} else if (map.get("CSDLPDF") != null) {
						document = PDDocument.load(filecdsl);
					} else {
						return Response.status(Response.Status.BAD_REQUEST).entity("Invalid value for WayofPdf: ")
								.build();
					}
	            String outputPath = props.getFileBasePath() + clientBasicData.getTermCode();
	            new File(outputPath).mkdir();

	            List<ClosureNSDLandCSDLEntity> pdfDatas = closureNSDLandCSDLRepository.getCoordinates();
	            pdfInsertCoordinates(document, pdfDatas, map, clientBasicData);

	            String fileName = dpId + EkycConstants.PDF_EXTENSION;
	            document.save(outputPath + slash + fileName);
	            document.close();

	            String contentType = URLConnection.guessContentTypeFromName(fileName);
	            String path = outputPath + slash + fileName;
	            File savedFile = new File(path);
	            ResponseBuilder response = Response.ok((Object) savedFile);
				response.type(contentType);
				response.header("Content-Disposition", "attachment;filename=" + savedFile.getName());
				return response.build();
	        } else {
	            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	                    .entity(MessageConstants.USER_ID_INVALID).build();
	        }
	    } catch (Exception e) {
	        e.printStackTrace(); // Handle exceptions properly in a production environment
	        
	    }
	    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(MessageConstants.FILE_NOT_FOUND).build();
	}


	public void pdfInsertCoordinates(PDDocument document, List<ClosureNSDLandCSDLEntity> pdfDatas,
			HashMap<String, String> map, ClientBasicData clientBasicData) {
		try {
			File fontFile = new File(props.getPdfFontfile());
			System.out.println("the fontFile"+fontFile);
			PDFont font = PDTrueTypeFont.loadTTF(document,fontFile);
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
			}  else if (columnType.equalsIgnoreCase("CSDLtick")&& map.get("CSDLPDF") != null) {
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
			else if (columnType.equalsIgnoreCase("NSDLtick")&& map.get("NSDLPDF") != null) {
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

	private HashMap<String, String> mapping(ClientBasicData clientBasicData,String dpId) {

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

		if (dpId != null) {
		    if (dpId.startsWith("120")) {
		    	map.put("CSDLPDF", "CSDLPDF");
		    	map.put("CDSL",dpId);
		    } else if (dpId.startsWith("IN")) {
		    	map.put("NSDLPDF", "NSDLPDF");
		    	map.put("NSDL",dpId);
		    }
		}
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
		
		
		if (dpId != null && dpId.length() >= 8) {
		    map.put("DP ID1", String.valueOf(dpId.charAt(0)));
		    map.put("DP ID2", String.valueOf(dpId.charAt(1)));
		    map.put("DP ID3", String.valueOf(dpId.charAt(2)));
		    map.put("DP ID4", String.valueOf(dpId.charAt(3)));
		    map.put("DP ID5", String.valueOf(dpId.charAt(4)));
		    map.put("DP ID6", String.valueOf(dpId.charAt(5)));
		    map.put("DP ID7", String.valueOf(dpId.charAt(6)));
		    map.put("DP ID8", String.valueOf(dpId.charAt(7)));
		}
		
		ClosurelogEntity closurelogEntity = closurelogRepository.findByUserId(clientBasicData.getTermCode());
		if (closurelogEntity != null && closurelogEntity.getAccType() > 0) {
			if (closurelogEntity.getAccType() == 1) {
				map.put("Both Trading And Demat", "1");
			} else if (closurelogEntity.getAccType() == 2) {
				map.put("Only Dp", "2");
			} else if (closurelogEntity.getAccType() == 3) {
				map.put("Only trading", "3");
			}
		}
		map.put("Reason for Closure",closurelogEntity.getAccclosingreasion()!=null?closurelogEntity.getAccclosingreasion():"");
		 map.put("Name of the First / Sole Holder",clientBasicData.getNameAsperPan());
		 String corAddress = clientBasicData.getCorraddress1() + " " + clientBasicData.getCorraddress2() + " " + clientBasicData.getCorraddress3();
		 System.out.println("the corAddress"+corAddress);
		 String first95Letters = corAddress.length() > 65 ? corAddress.substring(0, 65) : corAddress;
		 System.out.println("the first95Letters"+first95Letters);
		 String restOfAddress = corAddress.length() > 65 ? corAddress.substring(65) : "";
		 System.out.println("the first95Letters"+restOfAddress);
		 map.put("Address for Correspondence",first95Letters);
		 map.put("Address for Correspondence1",restOfAddress);
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


	@Override
	public ResponseModel updateAccTypeReason(String userId, int accType, String accCloseReason) {
	    ResponseModel responseModel = new ResponseModel();
	    ClosurelogEntity closurelogEntity = closurelogRepository.findByUserId(userId);
	    
	    if (closurelogEntity == null) {
	        closurelogEntity = new ClosurelogEntity();
	        closurelogEntity.setUserId(userId);
	    }
	    
	    closurelogEntity.setAccType(accType);
	    closurelogEntity.setAccclosingreasion(accCloseReason);
	    closurelogRepository.save(closurelogEntity);	    
	    responseModel.setMessage(EkycConstants.SUCCESS_MSG);
	    responseModel.setStat(EkycConstants.SUCCESS_STATUS);
	    responseModel.setResult(closurelogEntity);
	    
	    return responseModel;
	}
	@Override
	public ResponseModel generateEsign(PdfApplicationDataModel pdfModel) {
		ResponseModel model = null;
		ClientBasicData clientBasicData = TradingRestServices.getUserDetails(pdfModel.getToken());
		if (clientBasicData != null) {
			GeneratePdf(pdfModel.getToken(),pdfModel.getDpId());
		}
		model = esign.runMethod(props.getFileBasePath(), pdfModel.getApplicationNo(),pdfModel.getToken(),pdfModel.getDpId());
		return model;
	}
	
	@Override
	public Response getNsdlXml(String msg) {
		String slash = EkycConstants.UBUNTU_FILE_SEPERATOR;
		if (OS.contains(EkycConstants.OS_WINDOWS)) {
			slash = EkycConstants.WINDOWS_FILE_SEPERATOR;
		}
		try {
			
			int random = (int) (Math.random() * 900000) + 100000;
//			commonMethods.generateOTP(9876543210l);
			String fileName = "lastXml" + random + ".xml";
			String cerFile = "usrCertificate" + random + ".cer";
			File fXmlFile = new File(props.getFileBasePath() + "TempXMLFiles" + slash + fileName);
			if (fXmlFile.createNewFile()) {
				FileWriter myWriter = new FileWriter(fXmlFile);
				myWriter.write(msg);
				myWriter.close();
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();
				Element eElement = doc.getDocumentElement();
				String txnName = eElement.getAttribute("txn");
				String errorMessage = eElement.getAttribute("errMsg");
				String errorCode = eElement.getAttribute("errCode");
				TxnDetailsEntity detailsEntity = txnDetailsRepository.findBytxnId(txnName);
				if (detailsEntity != null && detailsEntity.getApplicationId() != null) {
					File nameFile = new File(detailsEntity.getFolderLocation() + slash + cerFile);
					if (nameFile.createNewFile()) {
						JSONObject xmlJSONObj = XML.toJSONObject(msg);
						String userCertificate = parseNSDLNameDetails(xmlJSONObj);
						if (StringUtil.isNotNullOrEmpty(userCertificate)) {
							FileWriter nameWriter = new FileWriter(nameFile);
							nameWriter.append("-----BEGIN CERTIFICATE-----" + System.getProperty("line.separator"));
							nameWriter.append(userCertificate + System.getProperty("line.separator"));
							nameWriter.append("-----END CERTIFICATE-----");
							nameWriter.close();
						}
					}
					String name = commonMethods
							.readUserNameFromCerFile(detailsEntity.getFolderLocation() + slash + cerFile);
					System.out.println(name);
					if (detailsEntity != null) {
						if (txnName != null && errorMessage != null && errorCode != null && !errorMessage.isEmpty()
								&& !errorCode.isEmpty() && errorMessage.equalsIgnoreCase("NA")
								&& errorCode.equalsIgnoreCase("NA")) {
							String filePath = detailsEntity.getFolderLocation();
							String resposne = esign.getSignFromNsdl(
									props.getFileBasePath() + detailsEntity.getApplicationId() + slash
											+ detailsEntity.getDpId()+ EkycConstants.PDF_EXTENSION,
									filePath, msg, detailsEntity.getUsername(),
									detailsEntity.getCity(),
									detailsEntity.getDpId());
							if (StringUtil.isNotNullOrEmpty(resposne)) {
								String esignedFileName =detailsEntity.getDpId() + "_signedFinal"
										+ EkycConstants.PDF_EXTENSION;
								String path = filePath + slash + esignedFileName;
								closureMail(detailsEntity.getEmailID());
								saveEsignDocumntDetails(detailsEntity.getApplicationId(), path, esignedFileName);
								java.net.URI finalPage = new java.net.URI(EkycConstants.SITE_URL_FILE);
								Response.ResponseBuilder responseBuilder = Response
										.status(Response.Status.MOVED_PERMANENTLY).location(finalPage);
								return responseBuilder.build();
							} else {

							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request. In getNsdlXml for the Error: " + e.getMessage(),
					"ERR-001");
		}

		return null;
	}

	public void closureMail(String emailID) throws MessagingException {
		try {
			if (emailID != null) {
				commonMethods.sendEsignClosureMail(emailID);
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request, In closureMail for the Error: " + e.getMessage(),
					"ERR-001");
		}
	}



	private static String parseNSDLNameDetails(JSONObject xmlJSONObj) {
		String response = "";
		try {
			if (xmlJSONObj != null) {
				if (xmlJSONObj.has("EsignResp")) {
					JSONObject sEnvelope = xmlJSONObj.getJSONObject("EsignResp");
					if (sEnvelope.has("UserX509Certificate")) {
						response = sEnvelope.getString("UserX509Certificate");
						return response;
					}
				} else {
					response = null;
				}
			} else {
				response = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;

	}
	public void saveEsignDocumntDetails(String applicationId, String documentPath, String fileName) {
		DocumentEntity oldEntity = docrepository.findByApplicationIdAndDocumentType(applicationId,
				EkycConstants.DOC_CLOSURE_ESIGN);
		if (oldEntity == null) {
			DocumentEntity documentEntity = new DocumentEntity();
			documentEntity.setApplicationId(applicationId);
			documentEntity.setAttachementUrl(documentPath);
			documentEntity.setAttachement(fileName);
			documentEntity.setDocumentType(EkycConstants.DOC_CLOSURE_ESIGN);
			documentEntity.setTypeOfProof(EkycConstants.DOC_CLOSURE_ESIGN);
			docrepository.save(documentEntity);
		} else {
			oldEntity.setAttachementUrl(documentPath);
			oldEntity.setAttachement(fileName);
			docrepository.save(oldEntity);
		}
//		s3BucketHelper.UploadErp(applicationId, EkycConstants.DOC_CLOSURE_ESIGN, documentPath, "", "");
//		savepasswordProtectedFile(applicationId);
	}



	@Override
	public ResponseModel closuremailotp(String EmailID,String MobileNo) {
	    ResponseModel responseModel = new ResponseModel();
	    try {
	        if (MobileNo != null&&EmailID!=null) {
	            commonMethods.sendClosureMail(EmailID);
	            closureHelper.sendClosureOtp(MobileNo);
	            if (responseModel != null) {
	                responseModel.setMessage(EkycConstants.SUCCESS_MSG);
	                responseModel.setStat(EkycConstants.SUCCESS_STATUS);
	            }
	        }
	    } catch (Exception e) {
	        logger.error("An error occurred: " + e.getMessage());
	        commonMethods.sendErrorMail(
	                "An error occurred while processing your request, In sendMailOtp for the Error: " + e.getMessage(),
	                "ERR-001");
	        responseModel = commonMethods.constructFailedMsg(e.getMessage());
	    }
	    return responseModel;
	}



	
	
}
