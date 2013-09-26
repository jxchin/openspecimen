/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-core/LICENSE.txt for details.
 */

package gov.nih.nci.caxchange.service.globus.resource;


/** 
 * DO NOT EDIT:  This class is autogenerated!
 *
 * This class is used by the resource to get configuration information about the 
 * resource.
 * 
 * @created by Introduce Toolkit version 1.4
 * 
 */
public class CaXchangeConsumerServiceResourceConfiguration {
	private String registrationTemplateFile;
	private boolean performRegistration;




	public boolean shouldPerformRegistration() {
		return performRegistration;
	}


	public void setPerformRegistration(boolean performRegistration) {
		this.performRegistration = performRegistration;
	}


	public String getRegistrationTemplateFile() {
		return registrationTemplateFile;
	}
	


	public void setRegistrationTemplateFile(String registrationTemplateFile) {
		this.registrationTemplateFile = registrationTemplateFile;
	}
		
}
