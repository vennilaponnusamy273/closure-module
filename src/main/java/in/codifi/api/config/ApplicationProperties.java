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

}
