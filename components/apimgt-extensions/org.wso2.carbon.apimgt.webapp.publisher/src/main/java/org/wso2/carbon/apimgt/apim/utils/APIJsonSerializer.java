package org.wso2.carbon.apimgt.apim.utils;

import org.wso2.carbon.apimgt.api.model.API;
import com.google.gson.JsonObject;

public interface APIJsonSerializer {
	JsonObject serialize(API apimAPI);
}
