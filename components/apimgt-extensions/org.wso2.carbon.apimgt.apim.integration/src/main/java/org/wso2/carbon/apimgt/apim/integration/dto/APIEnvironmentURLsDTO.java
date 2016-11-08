package org.wso2.carbon.apimgt.apim.integration.dto;

public class APIEnvironmentURLsDTO  {
	  
	  private String https = null;
	  
	  private String http = null;

	  public String getHttps() {
	    return https;
	  }
	  public void setHttps(String https) {
	    this.https = https;
	  }

	  public String getHttp() {
	    return http;
	  }
	  public void setHttp(String http) {
	    this.http = http;
	  }

	  @Override
	  public String toString()  {
	    StringBuilder sb = new StringBuilder();
	    sb.append("class APIEnvironmentURLsDTO {\n");
	    
	    sb.append("  https: ").append(https).append("\n");
	    sb.append("  http: ").append(http).append("\n");
	    sb.append("}\n");
	    return sb.toString();
	  }
	}
