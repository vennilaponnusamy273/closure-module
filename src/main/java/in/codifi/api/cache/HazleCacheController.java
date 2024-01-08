package in.codifi.api.cache;

import java.util.Map;

import org.eclipse.microprofile.config.ConfigProvider;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

public class HazleCacheController {
	public static HazleCacheController HazleCacheController = null;
	private HazelcastInstance hz = null;
	String keyFor = ConfigProvider.getConfig().getValue("config.app.hazel.for", String.class);

	public static HazleCacheController getInstance() {
		if (HazleCacheController == null) {
			HazleCacheController = new HazleCacheController();

		}
		return HazleCacheController;
	}

	public HazelcastInstance getHz() {
		if (hz == null) {
			ClientConfig clientConfig = new ClientConfig();
			clientConfig.setClusterName(ConfigProvider.getConfig().getValue("config.app.hazel.cluster", String.class));
			clientConfig.getNetworkConfig()
					.addAddress(ConfigProvider.getConfig().getValue("config.app.hazel.address", String.class));
			hz = HazelcastClient.newHazelcastClient(clientConfig);
		}
		return hz;
	}

	IMap<String, Integer> resendOtp = getHz().getMap("resendOtp" + keyFor); // 30 seconds for both sms and email
	IMap<String, Integer> retryOtp = getHz().getMap("retryOtp" + keyFor); // five minutes for both sms and email
	IMap<String, Integer> verifyOtp = getHz().getMap("verifyOtp" + keyFor);
	private Map<String, String> keycloakAdminSession = getHz().getMap("keycloakAdminSession" + keyFor);
	private Map<String, String> kraKeyValue = getHz().getMap("kraKeyValue" + keyFor);
	private Map<Integer, String> pageDetail = getHz().getMap("pageDetail" + keyFor);
	IMap<String, String> authToken = getHz().getMap("authToken" + keyFor);
	private Map<String, Integer> extService = getHz().getMap("extService" + keyFor);

	public IMap<String, Integer> getRetryOtp() {
		return retryOtp;
	}

	public void setRetryOtp(IMap<String, Integer> retryOtp) {
		this.retryOtp = retryOtp;
	}

	public IMap<String, Integer> getVerifyOtp() {
		return verifyOtp;
	}

	public void setVerifyOtp(IMap<String, Integer> verifyOtp) {
		this.verifyOtp = verifyOtp;
	}

	public IMap<String, Integer> getResendOtp() {
		return resendOtp;
	}

	public void setResendOtp(IMap<String, Integer> resendOtp) {
		this.resendOtp = resendOtp;
	}

	public Map<String, String> getKeycloakAdminSession() {
		return keycloakAdminSession;
	}

	public void setKeycloakAdminSession(Map<String, String> keycloakAdminSession) {
		this.keycloakAdminSession = keycloakAdminSession;
	}

	public Map<String, String> getKraKeyValue() {
		return kraKeyValue;
	}

	public void setKraKeyValue(Map<String, String> kraKeyValue) {
		this.kraKeyValue = kraKeyValue;
	}

	public Map<Integer, String> getPageDetail() {
		return pageDetail;
	}

	public void setPageDetail(Map<Integer, String> pageDetail) {
		this.pageDetail = pageDetail;
	}

	public Map<String, Integer> getExtService() {
		return extService;
	}

	public void setExtService(Map<String, Integer> extService) {
		this.extService = extService;
	}

	public IMap<String, String> getAuthToken() {
		return authToken;
	}

	public void setAuthToken(IMap<String, String> authToken) {
		this.authToken = authToken;
	}

}
