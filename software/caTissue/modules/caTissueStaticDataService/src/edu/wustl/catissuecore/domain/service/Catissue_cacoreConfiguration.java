/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-core/LICENSE.txt for details.
 */

package edu.wustl.catissuecore.domain.service;

import gov.nih.nci.cagrid.introduce.servicetools.ServiceConfiguration;

import org.globus.wsrf.config.ContainerConfig;
import java.io.File;
import javax.naming.InitialContext;

import org.apache.axis.MessageContext;
import org.globus.wsrf.Constants;


/** 
 * DO NOT EDIT:  This class is autogenerated!
 * 
 * This class holds all service properties which were defined for the service to have
 * access to.
 * 
 * @created by Introduce Toolkit version 1.4
 * 
 */
public class Catissue_cacoreConfiguration implements ServiceConfiguration {

	public static Catissue_cacoreConfiguration  configuration = null;
    public String etcDirectoryPath;
    	
	public static Catissue_cacoreConfiguration getConfiguration() throws Exception {
		if (Catissue_cacoreConfiguration.configuration != null) {
			return Catissue_cacoreConfiguration.configuration;
		}
		MessageContext ctx = MessageContext.getCurrentContext();

		String servicePath = ctx.getTargetService();

		String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/serviceconfiguration";
		try {
			javax.naming.Context initialContext = new InitialContext();
			Catissue_cacoreConfiguration.configuration = (Catissue_cacoreConfiguration) initialContext.lookup(jndiName);
		} catch (Exception e) {
			throw new Exception("Unable to instantiate service configuration.", e);
		}

		return Catissue_cacoreConfiguration.configuration;
	}
	

	
	private String queryProcessorClass;
	
	private String cql2QueryProcessorClass;
	
	private String cqlQueryProcessorConfig_applicationName;
	
	private String cqlQueryProcessorConfig_useLocalApiFlag;
	
	private String cqlQueryProcessorConfig_applicationHostName;
	
	private String cqlQueryProcessorConfig_applicationHostPort;
	
	private String cqlQueryProcessorConfig_useHttpsUrl;
	
	private String cql2QueryProcessorConfig_applicationName;
	
	private String cql2QueryProcessorConfig_useLocalApiFlag;
	
	private String cql2QueryProcessorConfig_applicationHostName;
	
	private String cql2QueryProcessorConfig_applicationHostPort;
	
	private String cql2QueryProcessorConfig_useHttpsUrl;
	
	private String cqlQueryProcessorConfig_useStaticLogin;
	
	private String cqlQueryProcessorConfig_useGridIdentityLogin;
	
	private String cqlQueryProcessorConfig_staticLoginPass;
	
	private String cqlQueryProcessorConfig_staticLoginUser;
	
	private String cql2QueryProcessorConfig_useStaticLogin;
	
	private String cql2QueryProcessorConfig_useGridIdentityLogin;
	
	private String cql2QueryProcessorConfig_staticLoginPass;
	
	private String cql2QueryProcessorConfig_staticLoginUser;
	
	private String serverConfigLocation;
	
	private String dataService_cqlValidatorClass;
	
	private String dataService_domainModelValidatorClass;
	
	private String dataService_validateCqlFlag;
	
	private String dataService_validateDomainModelFlag;
	
	private String dataService_cql2ValidatorClasses;
	
	private String dataService_classMappingsFilename;
	
	
    public String getEtcDirectoryPath() {
		return ContainerConfig.getBaseDirectory() + File.separator + etcDirectoryPath;
	}
	
	public void setEtcDirectoryPath(String etcDirectoryPath) {
		this.etcDirectoryPath = etcDirectoryPath;
	}


	
	public String getQueryProcessorClass() {
		return queryProcessorClass;
	}
	
	
	public void setQueryProcessorClass(String queryProcessorClass) {
		this.queryProcessorClass = queryProcessorClass;
	}

	
	public String getCql2QueryProcessorClass() {
		return cql2QueryProcessorClass;
	}
	
	
	public void setCql2QueryProcessorClass(String cql2QueryProcessorClass) {
		this.cql2QueryProcessorClass = cql2QueryProcessorClass;
	}

	
	public String getCqlQueryProcessorConfig_applicationName() {
		return cqlQueryProcessorConfig_applicationName;
	}
	
	
	public void setCqlQueryProcessorConfig_applicationName(String cqlQueryProcessorConfig_applicationName) {
		this.cqlQueryProcessorConfig_applicationName = cqlQueryProcessorConfig_applicationName;
	}

	
	public String getCqlQueryProcessorConfig_useLocalApiFlag() {
		return cqlQueryProcessorConfig_useLocalApiFlag;
	}
	
	
	public void setCqlQueryProcessorConfig_useLocalApiFlag(String cqlQueryProcessorConfig_useLocalApiFlag) {
		this.cqlQueryProcessorConfig_useLocalApiFlag = cqlQueryProcessorConfig_useLocalApiFlag;
	}

	
	public String getCqlQueryProcessorConfig_applicationHostName() {
		return cqlQueryProcessorConfig_applicationHostName;
	}
	
	
	public void setCqlQueryProcessorConfig_applicationHostName(String cqlQueryProcessorConfig_applicationHostName) {
		this.cqlQueryProcessorConfig_applicationHostName = cqlQueryProcessorConfig_applicationHostName;
	}

	
	public String getCqlQueryProcessorConfig_applicationHostPort() {
		return cqlQueryProcessorConfig_applicationHostPort;
	}
	
	
	public void setCqlQueryProcessorConfig_applicationHostPort(String cqlQueryProcessorConfig_applicationHostPort) {
		this.cqlQueryProcessorConfig_applicationHostPort = cqlQueryProcessorConfig_applicationHostPort;
	}

	
	public String getCqlQueryProcessorConfig_useHttpsUrl() {
		return cqlQueryProcessorConfig_useHttpsUrl;
	}
	
	
	public void setCqlQueryProcessorConfig_useHttpsUrl(String cqlQueryProcessorConfig_useHttpsUrl) {
		this.cqlQueryProcessorConfig_useHttpsUrl = cqlQueryProcessorConfig_useHttpsUrl;
	}

	
	public String getCql2QueryProcessorConfig_applicationName() {
		return cql2QueryProcessorConfig_applicationName;
	}
	
	
	public void setCql2QueryProcessorConfig_applicationName(String cql2QueryProcessorConfig_applicationName) {
		this.cql2QueryProcessorConfig_applicationName = cql2QueryProcessorConfig_applicationName;
	}

	
	public String getCql2QueryProcessorConfig_useLocalApiFlag() {
		return cql2QueryProcessorConfig_useLocalApiFlag;
	}
	
	
	public void setCql2QueryProcessorConfig_useLocalApiFlag(String cql2QueryProcessorConfig_useLocalApiFlag) {
		this.cql2QueryProcessorConfig_useLocalApiFlag = cql2QueryProcessorConfig_useLocalApiFlag;
	}

	
	public String getCql2QueryProcessorConfig_applicationHostName() {
		return cql2QueryProcessorConfig_applicationHostName;
	}
	
	
	public void setCql2QueryProcessorConfig_applicationHostName(String cql2QueryProcessorConfig_applicationHostName) {
		this.cql2QueryProcessorConfig_applicationHostName = cql2QueryProcessorConfig_applicationHostName;
	}

	
	public String getCql2QueryProcessorConfig_applicationHostPort() {
		return cql2QueryProcessorConfig_applicationHostPort;
	}
	
	
	public void setCql2QueryProcessorConfig_applicationHostPort(String cql2QueryProcessorConfig_applicationHostPort) {
		this.cql2QueryProcessorConfig_applicationHostPort = cql2QueryProcessorConfig_applicationHostPort;
	}

	
	public String getCql2QueryProcessorConfig_useHttpsUrl() {
		return cql2QueryProcessorConfig_useHttpsUrl;
	}
	
	
	public void setCql2QueryProcessorConfig_useHttpsUrl(String cql2QueryProcessorConfig_useHttpsUrl) {
		this.cql2QueryProcessorConfig_useHttpsUrl = cql2QueryProcessorConfig_useHttpsUrl;
	}

	
	public String getCqlQueryProcessorConfig_useStaticLogin() {
		return cqlQueryProcessorConfig_useStaticLogin;
	}
	
	
	public void setCqlQueryProcessorConfig_useStaticLogin(String cqlQueryProcessorConfig_useStaticLogin) {
		this.cqlQueryProcessorConfig_useStaticLogin = cqlQueryProcessorConfig_useStaticLogin;
	}

	
	public String getCqlQueryProcessorConfig_useGridIdentityLogin() {
		return cqlQueryProcessorConfig_useGridIdentityLogin;
	}
	
	
	public void setCqlQueryProcessorConfig_useGridIdentityLogin(String cqlQueryProcessorConfig_useGridIdentityLogin) {
		this.cqlQueryProcessorConfig_useGridIdentityLogin = cqlQueryProcessorConfig_useGridIdentityLogin;
	}

	
	public String getCqlQueryProcessorConfig_staticLoginPass() {
		return cqlQueryProcessorConfig_staticLoginPass;
	}
	
	
	public void setCqlQueryProcessorConfig_staticLoginPass(String cqlQueryProcessorConfig_staticLoginPass) {
		this.cqlQueryProcessorConfig_staticLoginPass = cqlQueryProcessorConfig_staticLoginPass;
	}

	
	public String getCqlQueryProcessorConfig_staticLoginUser() {
		return cqlQueryProcessorConfig_staticLoginUser;
	}
	
	
	public void setCqlQueryProcessorConfig_staticLoginUser(String cqlQueryProcessorConfig_staticLoginUser) {
		this.cqlQueryProcessorConfig_staticLoginUser = cqlQueryProcessorConfig_staticLoginUser;
	}

	
	public String getCql2QueryProcessorConfig_useStaticLogin() {
		return cql2QueryProcessorConfig_useStaticLogin;
	}
	
	
	public void setCql2QueryProcessorConfig_useStaticLogin(String cql2QueryProcessorConfig_useStaticLogin) {
		this.cql2QueryProcessorConfig_useStaticLogin = cql2QueryProcessorConfig_useStaticLogin;
	}

	
	public String getCql2QueryProcessorConfig_useGridIdentityLogin() {
		return cql2QueryProcessorConfig_useGridIdentityLogin;
	}
	
	
	public void setCql2QueryProcessorConfig_useGridIdentityLogin(String cql2QueryProcessorConfig_useGridIdentityLogin) {
		this.cql2QueryProcessorConfig_useGridIdentityLogin = cql2QueryProcessorConfig_useGridIdentityLogin;
	}

	
	public String getCql2QueryProcessorConfig_staticLoginPass() {
		return cql2QueryProcessorConfig_staticLoginPass;
	}
	
	
	public void setCql2QueryProcessorConfig_staticLoginPass(String cql2QueryProcessorConfig_staticLoginPass) {
		this.cql2QueryProcessorConfig_staticLoginPass = cql2QueryProcessorConfig_staticLoginPass;
	}

	
	public String getCql2QueryProcessorConfig_staticLoginUser() {
		return cql2QueryProcessorConfig_staticLoginUser;
	}
	
	
	public void setCql2QueryProcessorConfig_staticLoginUser(String cql2QueryProcessorConfig_staticLoginUser) {
		this.cql2QueryProcessorConfig_staticLoginUser = cql2QueryProcessorConfig_staticLoginUser;
	}

	
	public String getServerConfigLocation() {
		return ContainerConfig.getBaseDirectory() + File.separator + serverConfigLocation;
	}
	
	
	public void setServerConfigLocation(String serverConfigLocation) {
		this.serverConfigLocation = serverConfigLocation;
	}

	
	public String getDataService_cqlValidatorClass() {
		return dataService_cqlValidatorClass;
	}
	
	
	public void setDataService_cqlValidatorClass(String dataService_cqlValidatorClass) {
		this.dataService_cqlValidatorClass = dataService_cqlValidatorClass;
	}

	
	public String getDataService_domainModelValidatorClass() {
		return dataService_domainModelValidatorClass;
	}
	
	
	public void setDataService_domainModelValidatorClass(String dataService_domainModelValidatorClass) {
		this.dataService_domainModelValidatorClass = dataService_domainModelValidatorClass;
	}

	
	public String getDataService_validateCqlFlag() {
		return dataService_validateCqlFlag;
	}
	
	
	public void setDataService_validateCqlFlag(String dataService_validateCqlFlag) {
		this.dataService_validateCqlFlag = dataService_validateCqlFlag;
	}

	
	public String getDataService_validateDomainModelFlag() {
		return dataService_validateDomainModelFlag;
	}
	
	
	public void setDataService_validateDomainModelFlag(String dataService_validateDomainModelFlag) {
		this.dataService_validateDomainModelFlag = dataService_validateDomainModelFlag;
	}

	
	public String getDataService_cql2ValidatorClasses() {
		return dataService_cql2ValidatorClasses;
	}
	
	
	public void setDataService_cql2ValidatorClasses(String dataService_cql2ValidatorClasses) {
		this.dataService_cql2ValidatorClasses = dataService_cql2ValidatorClasses;
	}

	
	public String getDataService_classMappingsFilename() {
		return ContainerConfig.getBaseDirectory() + File.separator + dataService_classMappingsFilename;
	}
	
	
	public void setDataService_classMappingsFilename(String dataService_classMappingsFilename) {
		this.dataService_classMappingsFilename = dataService_classMappingsFilename;
	}

	
}
