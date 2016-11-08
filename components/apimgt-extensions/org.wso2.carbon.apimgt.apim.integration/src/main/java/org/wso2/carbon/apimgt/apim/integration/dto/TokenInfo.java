package org.wso2.carbon.apimgt.apim.integration.dto;

import javax.xml.bind.annotation.XmlElement;

public class TokenInfo {
	
	private String userName;
	private String password;
	private String grantType;
	private String scope;

	@XmlElement(name = "UserName", required = true)
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@XmlElement(name = "Password", required = true)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@XmlElement(name = "GrantType", required = true)
	public String getGrantType() {
		return grantType;
	}

	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}

	@XmlElement(name = "Scope", required = true)
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

}
