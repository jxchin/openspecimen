/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-core/LICENSE.txt for details.
 */


package edu.wustl.catissuecore.client;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import edu.wustl.catissuecore.deintegration.DEIntegration;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.CategoryManager;
import edu.common.dynamicextensions.entitymanager.CategoryManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.DataValueMapUtility;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.validation.ValidatorUtil;
import edu.wustl.bulkoperator.appservice.AbstractBulkOperationAppService;
import edu.wustl.bulkoperator.metadata.HookingInformation;
import edu.wustl.bulkoperator.util.BulkOperationConstants;
import edu.wustl.bulkoperator.util.BulkOperationException;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.catissuecore.action.annotations.AnnotationConstants;
import edu.wustl.catissuecore.bizlogic.AnnotationBizLogic;
import edu.wustl.catissuecore.bizlogic.ParticipantBizLogic;
import edu.wustl.catissuecore.bizlogic.SpecimenCollectionGroupBizLogic;
import edu.wustl.catissuecore.util.global.AppUtility;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;

public class CaTissueAppServiceImpl extends AbstractBulkOperationAppService
{

	private CaCoreAppServicesDelegator appService;
	private String userName;

	public CaTissueAppServiceImpl(boolean isAuthenticationRequired, String userName, String password)
			throws BulkOperationException, Exception
	{
		super(isAuthenticationRequired, userName, password);
	}

	@Override
	public void authenticate(String userName, String password) throws BulkOperationException
	{
		try
		{
			if (isAuthRequired && password != null)
			{
				if (!appService.authenticate(userName, password))
				{
					throw new BulkOperationException(
							"Could not login with given username/password.Please check the credentials");
				}
			}
			this.userName = userName;

		}
		catch (Exception appExp)
		{
			throw new BulkOperationException(appExp.getMessage(), appExp);
		}
	}

	@Override
	public void initialize(String userName, String password) throws BulkOperationException
	{
		appService = new CaCoreAppServicesDelegator();
		authenticate(userName, password);
	}

	@Override
	public void deleteObject(Object arg0) throws BulkOperationException
	{
	}

	@Override
	protected Object insertObject(Object domainObject) throws Exception
	{
		try
		{
			Object returnedObject = appService.insertObject(userName, domainObject);
			return returnedObject;
		}
		catch (ApplicationException appExp)
		{
			throw appExp;
		}
		catch (Exception exp)
		{
			throw exp;
		}
	}

	@Override
	protected Object searchObject(Object str) throws Exception
	{
		Object returnedObject = null;
		try
		{
			String hql = (String) str;
			List result = AppUtility.executeQuery(hql);

			if (!result.isEmpty())
			{
				returnedObject = result.get(0);
			}
		}
		catch (Exception appExp)
		{
			throw new Exception(appExp.getMessage(), appExp);
		}
		return returnedObject;
	}

	@Override
	protected Object updateObject(Object domainObject) throws Exception
	{
		try
		{
			Object returnedObject = appService.updateObject(userName, domainObject);
			return returnedObject;
		}
		catch (ApplicationException appExp)
		{
			throw appExp;
		}
		catch (Exception exp)
		{
			throw exp;
		}
	}

	@Override
	protected Long hookStaticDynExtObject(Object hookInformationObject) throws Exception {
		// TODO Auto-generated method stub
		HookingInformation hookInformation = (HookingInformation) hookInformationObject;
		Long dynExtObjectId = hookInformation.getDynamicExtensionObjectId();
		Long containerId;
		NameValueBean hookEntityBean;
		AnnotationBizLogic bizLogic = new AnnotationBizLogic();
		if (hookInformation.getEntityGroupName()!=null && !"".equals(hookInformation.getEntityGroupName()))
		{
			EntityGroupInterface entityGroup = EntityCache.getInstance().getEntityGroupByName(
					hookInformation.getEntityGroupName());
			EntityInterface entity = entityGroup.getEntityByName(hookInformation.getEntityName());
			ContainerInterface container = (ContainerInterface) entity.getContainerCollection()
					.iterator().next();
			containerId = container.getId();
			hookEntityBean = bizLogic.getHookEntiyNameValueBean(entity.getId(), hookInformation
					.getEntityName());
		}
		else
		{
			DEIntegration deIntegration = new DEIntegration();
			containerId = deIntegration.getRootCategoryContainerIdByName(hookInformation
					.getCategoryName());
			hookEntityBean = bizLogic.getHookEntityNameValueBeanForCategory(containerId,
					hookInformation.getCategoryName());
		}

		//write logic to find exact hook entity
		Long selectedStaticEntityRecordId = getSelectedStaticEntityRecordId(hookEntityBean,
				hookInformation);
		Long recordEntryId = bizLogic.createHookEntityObject(dynExtObjectId.toString(), containerId.toString(),
				hookEntityBean.getName(), selectedStaticEntityRecordId.toString(), hookEntityBean
						.getValue(), hookInformation.getSessionDataBean());

		return recordEntryId;
		
	}

	@Override
	public Long insertData(final String categoryName,
			final Map<String, Object> dataValue) throws ApplicationException,
			DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, ParseException {
		Long recordIdentifier = null;
		CategoryManager.getInstance();
		CategoryInterface categoryInterface = EntityCache.getInstance()
				.getCategoryByName(categoryName);

		if (categoryInterface == null) {
			throw new BulkOperationException("Category with name '"
					+ categoryName + "' does not exist.");
		}
		ContainerInterface containerInterface = (ContainerInterface) categoryInterface
				.getRootCategoryElement().getContainerCollection().toArray()[0];
		
		Map<BaseAbstractAttributeInterface, Object> attributeToValueMap = DataValueMapUtility
				.getAttributeToValueMap(dataValue, categoryInterface);
		List<String> errorList = ValidatorUtil.validateEntity(
				attributeToValueMap, new ArrayList<String>(),
				containerInterface, true);
		if (errorList.isEmpty()) {			
			recordIdentifier = DynamicExtensionsUtility.insertDataUtility(recordIdentifier, containerInterface, attributeToValueMap);
		} else {
			updateErrorMessages(errorList);
		}
		// TODO pass sessionDataBean instead of null, so that it gets audited.
		return recordIdentifier;
	}
	@Override
	public Long insertDEObject(String arg0, String arg1,
			Map<String, Object> arg2) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * It will form a string of errors seen in the data & will throw a exception with
	 * message as given in the errorList.
	 * @param errorList list of validation errors.
	 * @throws DynamicExtensionsApplicationException exception.
	 */
	private void updateErrorMessages(List<String> errorList)
			throws DynamicExtensionsApplicationException
	{
		StringBuffer buffer = new StringBuffer();
		int count = 1;
		Iterator<String> errorListIterator = errorList.iterator();
		while (errorListIterator.hasNext())
		{
			buffer.append(count).append(')').append(errorListIterator.next());
			count++;
		}
		throw new DynamicExtensionsApplicationException(buffer.toString());
	}
	/**
	 * It will retrieve the ID of the hook entity with which the DE record should be
	 * hooked depending of the hookEntity Bean & hooking information given by user.
	 * @param hookEntityBean bean of the hooking entity details.
	 * @param hookInformation hooking information given by user in data csv.
	 * @return id of the hook entity.
	 * @throws ApplicationException
	 */
	private Long getSelectedStaticEntityRecordId(NameValueBean hookEntityBean,
			HookingInformation hookInformation) throws ApplicationException
	{
		String cpLabel = (String) hookInformation.getDataHookingInformation().get(
				BulkOperationConstants.COLLECTION_PROTOCOL_LABEL);
		Long selectedEntityId = null;

		if (hookEntityBean.getName().equals(AnnotationConstants.ENTITY_NAME_PARTICIPANT_REC_ENTRY))
		{
			selectedEntityId = getParticipantIdForHooking(hookInformation, cpLabel);
		}
		else if (hookEntityBean.getName().equals(AnnotationConstants.ENTITY_NAME_SCG_REC_ENTRY))
		{
			selectedEntityId = getSCGIdforHooking(hookInformation);

		}
		else
		{
			selectedEntityId = getSpecimenIdForHooking(hookInformation);
		}
		return selectedEntityId;
	}
	/**
	 * It will return the Id of the participant retrieving it from the given hooking information.
	 * @param hookInformation hooking information given by user.
	 * @return id of the participant.
	 * @throws ApplicationException
	 */
	private Long getParticipantIdForHooking(HookingInformation hookInformation, String cpLabel)
			throws ApplicationException
	{
		Long selectedEntityId=null;
		ParticipantBizLogic bizLogic = new ParticipantBizLogic();
		if(hookInformation.getDataHookingInformation().get(
				BulkOperationConstants.PARTICIPANT_ID)!=null && !"".equals(hookInformation.getDataHookingInformation().get(
						BulkOperationConstants.PARTICIPANT_ID)))
		{	
			selectedEntityId = Long.valueOf(hookInformation.getDataHookingInformation().get(
					BulkOperationConstants.PARTICIPANT_ID).toString());
			bizLogic.isParticipantExists(selectedEntityId.toString());
		}
		else 
		{
			if(hookInformation.getDataHookingInformation().get(
					BulkOperationConstants.PPI)!=null && !"".equals(hookInformation.getDataHookingInformation().get(
							BulkOperationConstants.PPI)))
			{
				throw new BizLogicException(ErrorKey.getErrorKey("invalid.param.bo.participant"),
						null, null);
			}
			String ppi = (String) hookInformation.getDataHookingInformation().get(
					BulkOperationConstants.PPI);
			selectedEntityId = bizLogic.getParticipantIdByPPI(cpLabel, ppi);
			if (selectedEntityId == null)
			{
				throw new BizLogicException(ErrorKey.getErrorKey("invalid.param.bo.participant"),
						null, null);
			}
		}
		
		return selectedEntityId;
	}
	/**
	 * It will return the Id of the SCG retrieving it from the given hooking information.
	 * @param hookInformation hooking information given by user.
	 * @return id of the SCG.
	 * @throws ApplicationException
	 */
	private Long getSCGIdforHooking(HookingInformation hookInformation) throws ApplicationException
	{
		Long selectedEntityId=null;
		SpecimenCollectionGroupBizLogic bizLogic = new SpecimenCollectionGroupBizLogic();
		if(hookInformation.getDataHookingInformation().get(
				BulkOperationConstants.SCG_ID)!=null && !"".equals(hookInformation.getDataHookingInformation().get(
						BulkOperationConstants.SCG_ID)))
		{	
			selectedEntityId = Long.valueOf(hookInformation.getDataHookingInformation().get(
					BulkOperationConstants.SCG_ID).toString());
			bizLogic.isSCGExists(selectedEntityId.toString());
		}
		else 
		{
			String scgLabel = (String) hookInformation.getDataHookingInformation().get(
					BulkOperationConstants.SCG_NAME);
			String scgBarcode = (String) hookInformation.getDataHookingInformation().get(
					BulkOperationConstants.SCG_BARCODE);

			if (scgLabel != null && !scgLabel.trim().equals(""))
			{

				//get the scgId on the basis of cp label & scg label
				selectedEntityId = bizLogic.getScgIdFromName(scgLabel);
			}
			else

			{
				selectedEntityId = bizLogic.getScgIdFromBarcode(scgBarcode);
			}
			if (selectedEntityId == null)
			{
				throw new BizLogicException(ErrorKey.getErrorKey("invalid.param.bo.scg"), null,
						null);
			}
		}
		
		return selectedEntityId;
	}
	/**
	 * It will return the Id of the specimen retrieving it from the given hooking information.
	 * @param hookInformation hooking information given by user.
	 * @return id of the specimen.
	 * @throws ApplicationException
	 */
	private Long getSpecimenIdForHooking(HookingInformation hookInformation)
			throws ApplicationException
	{
		Long selectedEntityId =null;
		AnnotationBizLogic bizLogic = new AnnotationBizLogic();
		if(hookInformation.getDataHookingInformation().get(
				BulkOperationConstants.SPECIMEN_ID)!=null && !"".equals(hookInformation.getDataHookingInformation().get(
						BulkOperationConstants.SPECIMEN_ID)))
		{	
			selectedEntityId = Long.valueOf(hookInformation.getDataHookingInformation().get(
					BulkOperationConstants.SPECIMEN_ID).toString());
			bizLogic.isSpecimenExists(selectedEntityId.toString());
		}
		else
		{
			String specimenLabel = (String) hookInformation.getDataHookingInformation().get(
					BulkOperationConstants.SPECIMEN_LABEL);
			String specimenBarcode = (String) hookInformation.getDataHookingInformation().get(
					BulkOperationConstants.SPECIMEN_BARCODE);

			if (specimenLabel != null && !specimenLabel.trim().equals(""))
			{

				//get the scgId on the basis of cp label & scg label
				selectedEntityId = bizLogic.getSpecimenByLabel(specimenLabel);
			}
			else

			{
				selectedEntityId = bizLogic.getSpecimenByBarcode(specimenBarcode);
			}
			if (selectedEntityId == null)
			{
				throw new BizLogicException(ErrorKey.getErrorKey("invalid.param.bo.specimen"),
						null, null);
			}
		}
		
		return selectedEntityId;
	}
}