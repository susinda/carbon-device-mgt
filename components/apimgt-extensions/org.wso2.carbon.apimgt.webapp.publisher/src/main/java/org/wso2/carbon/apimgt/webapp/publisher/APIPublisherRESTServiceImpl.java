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
import org.wso2.carbon.apimgt.apim.integration.common.APIMIntegrationException;
import org.wso2.carbon.apimgt.apim.integration.common.configs.APIMConfig;
import org.wso2.carbon.apimgt.apim.integration.publisher.PublisherClient;
import org.wso2.carbon.apimgt.apim.integration.publisher.dto.PublisherAPIDTO;
import org.wso2.carbon.utils.CarbonUtils;

public class APIPublisherRESTServiceImpl implements APIPublisherService {
	private static final Log log = LogFactory.getLog(APIPublisherRESTServiceImpl.class);
	PublisherClient apimPublisherClient;
	APIMConfig config;

	public APIPublisherRESTServiceImpl() {
		
		String configFile = CarbonUtils.getCarbonConfigDirPath() + File.separator + "apim-integration.xml";
		log.info("configFile  " + configFile);

		try {
			config = APIMConfigReader.getAPIMConfig(configFile);
		} catch (APIManagementException e) {
			log.error(e.getMessage(), e);
		}
		try {
			apimPublisherClient = new PublisherClient(config);
		} catch (APIManagementException e) {
			log.error("Error initializing PublisherClient \n" + e.getMessage(), e);
		}
		
		log.info("APIMConfig config.getDcrEndpointConfig().getUrl() =  " + config.getDcrEndpointConfig().getUrl());
	}

	@Override
	public void publishAPI(API apimAPI) throws APIManagementException, FaultGatewaysException {

		PublisherAPIDTO apiDTO = APIBuilderUtil.fromAPItoDTO(apimAPI);
		try {
			apimPublisherClient.createAndPublishAPIIfNotExists(apiDTO);
		} catch (APIMIntegrationException e) {
			log.error("Error publishing api " + apiDTO.getName() + "\n" + e.getMessage(), e);
			throw new APIManagementException(e.getMessage(), e);
		}
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
