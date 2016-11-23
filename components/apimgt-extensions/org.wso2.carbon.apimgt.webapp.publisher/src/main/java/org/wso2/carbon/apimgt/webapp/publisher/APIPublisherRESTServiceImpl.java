package org.wso2.carbon.apimgt.webapp.publisher;

import java.io.File;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.FaultGatewaysException;
import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.apimgt.api.model.APIIdentifier;
import org.wso2.carbon.apimgt.apim.integration.common.APIMConfigReader;
import org.wso2.carbon.apimgt.apim.integration.common.configs.APIMConfig;
import org.wso2.carbon.apimgt.apim.integration.common.configs.PublisherEndpointConfig;
import org.wso2.carbon.apimgt.apim.integration.dcr.dto.PublisherAPIDTO;
import org.wso2.carbon.apimgt.apim.integration.publisher.InternalPublisherClient;
import org.wso2.carbon.apimgt.apim.integration.publisher.dto.PublisherAPIListDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.StoreAPIDTO;
import org.wso2.carbon.apimgt.apim.integration.store.dto.StoreAPIListDTO;
import org.wso2.carbon.utils.CarbonUtils;

public class APIPublisherRESTServiceImpl extends APIPublisherServiceImpl {
	private static final Log log = LogFactory.getLog(APIPublisherRESTServiceImpl.class);
	InternalPublisherClient apimRestClient;
	APIMConfig config;

	public APIPublisherRESTServiceImpl() {
		apimRestClient = new InternalPublisherClient();
		String configFile = CarbonUtils.getCarbonConfigDirPath() + File.separator + "apim-integration.xml";
		log.info("configFile  " + configFile);

		try {
			config = APIMConfigReader.getAPIMConfig(configFile);
		} catch (APIManagementException e) {
			log.error(e.getMessage(), e);
		}
		log.info("APIMConfig config.getDcrEndpointConfig().getUrl() =  " + config.getDcrEndpointConfig().getUrl());
	}

	@Override
	public void publishAPI(API apimAPI) throws APIManagementException, FaultGatewaysException {

		PublisherAPIDTO apiDTO = APIBuilderUtil.fromAPItoDTO(apimAPI);
		String accessToken = APIBuilderUtil.getAccessToken(apimRestClient, config);
		log.info("APIBuilderUtil.getAccessToken() accessToken generated sucesfully");

		PublisherEndpointConfig publisherConfig = config.getPublisherEndpointConfig();
		PublisherAPIListDTO list = apimRestClient.searchPublisherAPIs(publisherConfig, "", accessToken);
		List<PublisherAPIDTO> apiLIst = list.getList();
		PublisherAPIDTO existingAPI = getExistingApi(apiDTO, apiLIst);
		if (existingAPI != null) {
			log.info("API " + existingAPI.getName() + " apready exists, therefore not creating");
			if ("PUBLISHED".equals(existingAPI.getStatus())) {
				log.info("API " + existingAPI.getName() + " apready in PUBLISHED state, therefore not publishing");
			} else {
				boolean publishResult = apimRestClient.publishAPI(publisherConfig, existingAPI.getId(), accessToken);
				log.info("API publish result " + publishResult);
			}
		} else {
			PublisherAPIDTO createdAPI = apimRestClient.createAPI(publisherConfig, apiDTO, accessToken);
			log.info("API creation succesful : createdAPI " + createdAPI.getName() + "  " + createdAPI.getId());
			boolean publishResult = apimRestClient.publishAPI(publisherConfig, createdAPI.getId(), accessToken);
			log.info("API publish result " + publishResult);
		}
	}

	private PublisherAPIDTO getExistingApi(PublisherAPIDTO apiDTO, List<PublisherAPIDTO> apiLIst) {
		for (PublisherAPIDTO api : apiLIst) {
			if (api.getContext().equals(apiDTO.getContext())) {
				return api;
			}
		}
		return null;
	}

	@Override
	public void removeAPI(APIIdentifier id) throws APIManagementException {
		if (log.isDebugEnabled()) {
			log.debug("Removing API '" + id.getApiName() + "'");
		}
		//TODO implement this and chnage the debug message
		if (log.isDebugEnabled()) {
			log.debug("API '" + id.getApiName() + "' has NOT been removed");
		}
	}

	@Override
	public void publishAPIs(List<API> apis) throws APIManagementException, FaultGatewaysException {
		if (log.isDebugEnabled()) {
			log.debug("Publishing a batch of APIs");
		}
		for (API api : apis) {
			try {
				this.publishAPI(api);
			} catch (APIManagementException e) {
				log.error("Error occurred while publishing API '" + api.getId().getApiName() + "'", e);
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("End of publishing the batch of APIs");
		}
	}

}
