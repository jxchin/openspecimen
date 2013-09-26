/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-core/LICENSE.txt for details.
 */

package edu.wustl.catissuecore.dbunit.test;
import edu.wustl.catissuecore.domain.User;
import java.io.FileInputStream;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;

import edu.wustl.catissuecore.domain.Address;
import edu.wustl.catissuecore.domain.CancerResearchGroup;
import edu.wustl.catissuecore.domain.Department;
import edu.wustl.catissuecore.domain.Institution;
import edu.wustl.catissuecore.domain.Capacity;
import edu.wustl.catissuecore.domain.CellSpecimen;
import edu.wustl.catissuecore.domain.CellSpecimenRequirement;
import edu.wustl.catissuecore.domain.CellSpecimenReviewParameters;
import edu.wustl.common.util.global.Constants;


public class CellSpecimenReviewParametersDBTestcases extends
		DefaultCatissueDBUnitTestCase {
	
	public void testCellSpecimenReviewParametersCreate()
	{
		
		try
		{
			insertObjectsOf(CellSpecimenReviewParameters.class);
			System.out.println("Inside CellSpecimenReviewParameters...after insert...");
			assertTrue("CellSpecimenReviewParameters",true);
		}catch(Exception e)
		{
			e.printStackTrace();
			fail("failed to insert CellSpecimenReviewParameters...create");
			
		}
	}
	public void testCellSpecimenReviewParametersUpdate()
	{
		try
		{
	
			UpdateObjects(CellSpecimenReviewParameters.class);
			assertTrue("CellSpecimenReviewParameters", true);
		}catch(Exception e)
		{
			e.printStackTrace();
			fail("failed to insert CellSpecimenReviewParameters....update");
		}
	}


}
