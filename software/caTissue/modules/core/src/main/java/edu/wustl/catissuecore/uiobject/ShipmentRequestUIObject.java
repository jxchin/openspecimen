/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-core/LICENSE.txt for details.
 */

package edu.wustl.catissuecore.uiobject;

import edu.wustl.common.domain.UIObject;


public class ShipmentRequestUIObject implements UIObject
{
	boolean requestProcessed=false;


	public boolean isRequestProcessed()
	{
		return requestProcessed;
	}


	public void setRequestProcessed(boolean requestProcessed)
	{
		this.requestProcessed = requestProcessed;
	}



}
