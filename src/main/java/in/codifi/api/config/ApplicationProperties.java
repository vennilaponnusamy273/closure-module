package in.codifi.api.config;

import javax.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.Getter;
import lombok.Setter;

@Singleton
@Getter
@Setter
public class ApplicationProperties {

	

	@ConfigProperty(name = "appconfig.esign.pdf.fontfile")
	String pdfFontfile;
	
	@ConfigProperty(name = "appconfig.file.basepath")
	String fileBasePath;
	@ConfigProperty(name = "config.app.hazel.cluster")
	String clusterName;
	@ConfigProperty(name = "config.app.hazel.address")
	String hazleAddress;
	@ConfigProperty(name = "config.app.hazel.for")
	String hazleFor;
	
	@ConfigProperty(name = "appconfig.esign.csdlpdf")
	String csdlpdf;
	@ConfigProperty(name = "appconfig.esign.nsdlpdf")
	String nsdlpdf;
	
	@ConfigProperty(name = "appconfig.mail.password")
	String mailPassword;
	@ConfigProperty(name = "appconfig.mail.from")
	String mailFrom;
	@ConfigProperty(name = "appconfig.mail.port")
	String mailPort;
	@ConfigProperty(name = "appconfig.mail.username")
	String mailUserName;
	@ConfigProperty(name = "appconfig.mail.host")
	String mailHost;

	// token
	@ConfigProperty(name = "appconfig.token.encryption.key")
	String tokenEncryptKey;
	
	@ConfigProperty(name = "appconfig.log.db.name")
	String logDBName;
	
	@ConfigProperty(name = "appconfig.esign.pfx.userid")
	String esignUserId;
	@ConfigProperty(name = "appconfig.esign.pfx.password")
	String esignPassword;
	@ConfigProperty(name = "appconfig.esign.pfx.aspid")
	String esignAspId;
	@ConfigProperty(name = "appconfig.esign.pfx.alias")
	String esignAlias;
	@ConfigProperty(name = "appconfig.esign.pfx.location")
	String esignLocation;
	@ConfigProperty(name = "appconfig.esign.pfx.tickimage")
	String esignTickImage;
	@ConfigProperty(name = "appconfig.esign.return.url")
	String eSignReturnUrl;
	
	// msg Config
	@ConfigProperty(name = "appconfig.sms.userid")
	String smsUserId;
	@ConfigProperty(name = "appconfig.sms.pass")
	String smsPass;
	@ConfigProperty(name = "appconfig.sms.appid")
	String smsAppId;
	@ConfigProperty(name = "appconfig.sms.subappid")
	String smsSubAppId;
	@ConfigProperty(name = "appconfig.sms.contenttype")
	String smsContentType;
//	@ConfigProperty(name = "appconfig.sms.firsttext")
//	String smsFirstText;
//	@ConfigProperty(name = "appconfig.sms.secondtext")
//	String smsSecondText;
	@ConfigProperty(name = "appconfig.sms.from")
	String smsFrom;
	@ConfigProperty(name = "appconfig.sms.selfid")
	String smsSelfid;
	@ConfigProperty(name = "appconfig.sms.alert")
	String smsAlert;
	@ConfigProperty(name = "appconfig.sms.dlrreq")
	String smsDlrReq;
	
	@ConfigProperty(name = "appconfig.approve.url")
	String approveurl;


}
