/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author 
 *@version 1.0
 */

package edu.wustl.catissuecore.bizlogic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


import edu.common.dynamicextensions.dao.impl.DynamicExtensionDAO;
import edu.common.dynamicextensions.domain.integration.EntityMap;
import edu.common.dynamicextensions.domain.integration.EntityMapCondition;
import edu.common.dynamicextensions.domain.integration.EntityMapRecord;
import edu.common.dynamicextensions.domain.integration.FormContext;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.catissuecore.action.annotations.AnnotationConstants;
import edu.wustl.catissuecore.util.CatissueCoreCacheManager;
import edu.wustl.catissuecore.util.global.AppUtility;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.global.Status;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.DAO;
import edu.wustl.dao.exception.DAOException;


/**
 * @author sandeep_chinta
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class AnnotationBizLogic extends CatissueDefaultBizLogic
{

	/**
	 * logger Logger - Generic logger.
	 */
	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}
	/**
	 * logger object.
	 */
	private static final Logger logger = Logger.getCommonLogger(AnnotationBizLogic.class);

	/**
	 *  public constructor.
	 */
	public AnnotationBizLogic()
	{
		this.setAppName(DynamicExtensionDAO.getInstance().getAppName());
	}

	/**
	 * @param staticEntityIdentifier staticEntityId.
	 * @return List of all dynamic entities id from a given static entity
	 * @throws BizLogicException BizLogicException.
	 * eg: returns all dynamic entity id from a Participant,Specimen etc
	 */
	public List getListOfDynamicEntitiesIds(long staticEntityIdentifier) throws BizLogicException
	{
		List<EntityMap> dynamicList = new ArrayList<EntityMap>();

		final List list = new ArrayList();
		dynamicList = this.retrieve(EntityMap.class.getName(),
				"staticEntityId", Long.valueOf(staticEntityIdentifier));
		if (dynamicList != null && !dynamicList.isEmpty())
		{
			for (final EntityMap entityMap : dynamicList)
			{
				list.add(entityMap.getContainerId());
			}
		}

		return list;
	}

	/**
	 * @param staticEntityId staticEntityId.
	 * @return List of all dynamic entities Objects from a given static entity
	 * eg: returns all dynamic entity objects from a Participant,Specimen etc
	 * @throws BizLogicException BizLogicException
	 */
	public List getListOfDynamicEntities(long staticEntityId) throws BizLogicException
	{
		List dynamicList = new ArrayList();

		dynamicList = this.retrieve(EntityMap.class.getName(),
				"staticEntityId", Long.valueOf(staticEntityId));

		return dynamicList;
	}

	/**
	 * @param staticEntityId : staticEntityId.
	 * @param  typeId : typeId
	 * @param staticRecordId :staticRecordId
	 * @return List of all dynamic entities id from a given static entity based on its protocol linkage
	 * eg: returns all dynamic entity id from a Participant,Specimen etc which is linked
	   to Protocol 1,Protocol 2 etc
	 */
	public List getListOfDynamicEntitiesIds(long staticEntityId, long typeId, long staticRecordId)
	{
		List dynamicList = new ArrayList();

		final String[] selectColumnName = {"containerId"};
		final String[] whereColumnName = {"staticEntityId", "typeId", "staticRecordId"};
		final String[] whereColumnCondition = {"=", "=", "="};
		final Object[] whereColumnValue = {Long.valueOf(staticEntityId), Long.valueOf(typeId),
				Long.valueOf(staticRecordId)};
		final String joinCondition = Constants.AND_JOIN_CONDITION;

		try
		{
			dynamicList = this.retrieve(EntityMap.class.getName(), selectColumnName,
					whereColumnName, whereColumnCondition, whereColumnValue, joinCondition);
		}
		catch (final BizLogicException e)
		{
			AnnotationBizLogic.logger.error(e.getMessage(), e);
		}

		return dynamicList;
	}

	/**
	 * Updates the Entity Record object in database.
	 * @param entityRecord : entityRecord
	 * @throws BizLogicException :BizLogicException
	 */
	public void updateEntityRecord(EntityMapRecord entityRecord) throws BizLogicException
	{
		this.update(entityRecord);

	}

	/**
	 * @param entityRecord : entityRecord.
	 * Inserts a new EntityRecord record in Database
	 * @throws BizLogicException : BizLogicException
	 */
	public void insertEntityRecord(EntityMapRecord entityRecord) throws BizLogicException
	{
		this.insert(entityRecord);
		final Long entityMapId = entityRecord.getFormContext().getEntityMap().getId();
		final Long staticEntityRecordId = entityRecord.getStaticEntityRecordId();
		final Long dynExtRecordId = entityRecord.getDynamicEntityRecordId();
		this
				.associateRecords(entityMapId, Long.valueOf(staticEntityRecordId)
						, Long.valueOf(dynExtRecordId));
	}

	/**
	 * This method called to associate records.
	 * @param entityMapId entityMapId
	 * @param staticEntityRecordId staticEntityRecordId
	 * @param dynamicEntityRecordId dynamicEntityRecordId
	 * @throws BizLogicException BizLogicException
	 */
	private void associateRecords(Long entityMapId, Long staticEntityRecordId,
			Long dynamicEntityRecordId) throws BizLogicException
	{
		final DefaultBizLogic bizLogic = new CatissueDefaultBizLogic();
		bizLogic.setAppName(DynamicExtensionDAO.getInstance().getAppName());
		final Object object = bizLogic.retrieve(EntityMap.class.getName(), entityMapId);
		final EntityManagerInterface entityManager = EntityManager.getInstance();
		if (object != null)
		{
			try
			{
				final EntityMap entityMap = (EntityMap) object;
				Long dynamicEntityId = entityManager.getEntityIdByContainerId(entityMap
						.getContainerId());
				final Long rootContainerId = entityMap.getContainerId();
				final Long containerId = entityManager.isCategory(rootContainerId);
				if (containerId != null)
				{
					final Long entityId = entityManager
							.getEntityIdByCategorEntityId(dynamicEntityId);
					if (entityId != null)
					{
						dynamicEntityId = entityId;
					}

				}
				//root category entity id .take that entity from cache

				EntityInterface dynamicEntity = EntityCache.getInstance().getEntityById(
						dynamicEntityId);
				final EntityInterface staticEntity = EntityCache.getInstance().getEntityById(
						entityMap.getStaticEntityId());

				final Collection<AssociationInterface> associationCollection = staticEntity
						.getAssociationCollection();
				do
				{
					AssociationInterface associationInterface = null;
					for (final AssociationInterface association : associationCollection)
					{
						if (association.getTargetEntity().equals(dynamicEntity))
						{
							associationInterface = association;
							break;
						}
					}
					entityManager.associateEntityRecords(associationInterface,
							staticEntityRecordId, dynamicEntityRecordId);
					dynamicEntity = dynamicEntity.getParentEntity();
				}
				while (dynamicEntity != null);
			}
			catch (final DynamicExtensionsSystemException exception)
			{
				AnnotationBizLogic.logger.error(exception.getMessage(), exception);
				throw new BizLogicException(null, null, exception.getMessage());
			}
		}
	}

	/**
	 * @param entityMap entityMap
	 * @throws BizLogicException BizLogicException
	 * Updates the Entity Map object in database
	 */
	public void updateEntityMap(EntityMap entityMap) throws BizLogicException
	{

		this.update(entityMap);

	}

	/**
	 * This method called to insert Entity Map.
	 * @param entityMap entityMap
	 * @throws BizLogicException BizLogicException
	 * Inserts a new EntityMap record in Database
	 */
	public void insertEntityMap(EntityMap entityMap) throws BizLogicException
	{
		final Long staticEntityId = entityMap.getStaticEntityId();
		final Long dynamicEntityId = entityMap.getContainerId();
		final Long deAssociationID = AnnotationUtil.addAssociation(staticEntityId, dynamicEntityId,
				false);
		if (deAssociationID != null)
		{
			this.insert(entityMap);
		}

	}

	/**
	 * This method returns the Static Entity Containers.
	* @param dynamicEntityContainerId dynamicEntityContainerId
	* @return List of Static Entity Id from a given Dynamic Entity Id
	*/
	public List getListOfStaticEntitiesIds(long dynamicEntityContainerId)
	{
		List dynamicList = new ArrayList();

		final String[] selectColumnName = {"staticEntityId"};
		final String[] whereColumnName = {"containerId"};
		final String[] whereColumnCondition = {"="};
		final Object[] whereColumnValue = {Long.valueOf(dynamicEntityContainerId)};
		final String joinCondition = null;

		try
		{
			dynamicList = this.retrieve(EntityMap.class.getName(), selectColumnName,
					whereColumnName, whereColumnCondition, whereColumnValue, joinCondition);
		}
		catch (final BizLogicException e)
		{
			AnnotationBizLogic.logger.error(e.getMessage(), e);
		}

		return dynamicList;
	}

	/**This method returns the Static Entities.
	 * @param dynamicEntityContainerId dynamicEntityContainerId.
	 * @return List of Static Entity Objects from a given Dynamic Entity Id.
	 */
	public List getListOfStaticEntities(long dynamicEntityContainerId)
	{
		List dynamicList = new ArrayList();

		try
		{
			dynamicList = this.retrieve(EntityMap.class.getName(), "containerId", Long.valueOf(
					dynamicEntityContainerId));
		}
		catch (final BizLogicException e)
		{
			AnnotationBizLogic.logger.error(e.getMessage(), e);
		}

		return dynamicList;
	}

	/**
	 * This method is called to get entity Map Id.
	 * @param entityMapId entityMapId.
	 * @return EntityMap object for its given id.
	 */
	public EntityMap getEntityMap(long entityMapId)
	{
		EntityMap map = null;

		try
		{
			map = (EntityMap) this.retrieve(EntityMap.class.getName(), entityMapId);
		}
		catch (final BizLogicException e)
		{
			AnnotationBizLogic.logger.error(e.getMessage(), e);

		}

		return map;
	}

	/**
	 * This method called to get Entity Map.
	 * @param entityMapids entityMapids
	 * @param staticRecordId staticRecordId
	 * @return getEntityMapRecordList.
	 */
	public List getEntityMapRecordList(List entityMapids, long staticRecordId)
	{
		final List dynamicList = new ArrayList();

		final String[] selectColumnName = null;
		final String[] whereColumnName = {"staticEntityRecordId", "formContext.entityMap.id"};
		final String[] whereColumnCondition = {"=", "="};
		final String joinCondition = Constants.AND_JOIN_CONDITION;

		final Iterator iter = entityMapids.iterator();
		while (iter.hasNext())
		{
			final Long entityMapId = (Long) iter.next();
			if (entityMapId != null)
			{
				final Object[] whereColumnValue = {Long.valueOf(staticRecordId), entityMapId};
				try
				{
					final List list = this.retrieve(EntityMapRecord.class.getName(),
							selectColumnName, whereColumnName, whereColumnCondition,
							whereColumnValue, joinCondition);
					if (list != null)
					{
						dynamicList.addAll(list);
					}
				}
				catch (final BizLogicException e)
				{
					AnnotationBizLogic.logger.error(e.getMessage(), e);
				}
			}

		}

		return dynamicList;
	}

	/**
	 * This method called to delete Entity Map.
	 * @param entityMapId entityMapId
	 * @param dynamicEntityRecordId dynamicEntityRecordId.
	 */
	public void deleteEntityMapRecord(long entityMapId, long dynamicEntityRecordId)
	{
		try
		{
			List dynamicList = new ArrayList();
			final String[] selectColumnName = null;
			final String[] whereColumnName = {"formContext.entityMap.id", "dynamicEntityRecordId"};
			final String[] whereColumnCondition = {"=", "="};
			final Object[] whereColumnValue = {Long.valueOf(entityMapId),
					Long.valueOf(dynamicEntityRecordId)};
			final String joinCondition = Constants.AND_JOIN_CONDITION;

			dynamicList = this.retrieve(EntityMapRecord.class.getName(), selectColumnName,
					whereColumnName, whereColumnCondition, whereColumnValue, joinCondition);

			if (dynamicList != null && !dynamicList.isEmpty())
			{

				final EntityMapRecord entityRecord = (EntityMapRecord) dynamicList.get(0);
				entityRecord.setLinkStatus(Status.ACTIVITY_STATUS_DISABLED.toString());
				this.update(entityRecord);

			}
		}
		catch (final BizLogicException e)
		{
			AnnotationBizLogic.logger.error(e.getMessage(), e);
		}
	}

	/**
	 * This method called to delete deleteAnnotationRecords.
	 * @param containerId : containerId.
	 * @param recordIdList : recordIdList.
	 * @throws BizLogicException : BizLogicException.
	 */
	public void deleteAnnotationRecords(Long containerId, List<Long> recordIdList)
			throws BizLogicException
	{
		final EntityManagerInterface entityManagerInterface = EntityManager.getInstance();
		try
		{
			entityManagerInterface.deleteRecords(containerId, recordIdList);
		}
		catch (final Exception e)
		{
			AnnotationBizLogic.logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Deletes an object from the database.
	 * @param obj The object to be deleted.
	 * @param dao dao
	 * @throws BizLogicException
	 */
	@Override
	protected void delete(Object obj, DAO dao)
	{
		try
		{
			dao.delete(obj);
		}
		catch (final DAOException e)
		{
			AnnotationBizLogic.logger.error(e.getMessage(), e);
		}
	}

	/**
	 * This method gets Annotation Ids Based On Condition.
	 * @param dynEntitiesList dynEntities List
	 * @param cpIdList cpId List
	 * @return dynEntitiesIdList
	 * @throws BizLogicException BizLogic Exception
	 */
	public List getAnnotationIdsBasedOnCondition(List dynEntitiesList, List cpIdList)
			throws BizLogicException
	{
		final List dynEntitiesIdList = new ArrayList();
		if (dynEntitiesList != null && !dynEntitiesList.isEmpty())
		{
			final Iterator dynEntitiesIterator = dynEntitiesList.iterator();
			while (dynEntitiesIterator.hasNext())
			{
				final EntityMap entityMap = (EntityMap) dynEntitiesIterator.next();
				final Collection<FormContext> formContexts = AppUtility.getFormContexts(entityMap
						.getId());
				final Iterator<FormContext> formContextIter = formContexts.iterator();
				while (formContextIter.hasNext())
				{
					final FormContext formContext = formContextIter.next();
					final Collection<EntityMapCondition> entityMapConditions = AppUtility
							.getEntityMapConditions(formContext.getId());
					if ((formContext.getNoOfEntries() == null || formContext.getNoOfEntries()
							.equals(""))
							&& (formContext.getStudyFormLabel() == null || formContext
									.getStudyFormLabel().equals("")))
					{
						if (entityMapConditions != null && !entityMapConditions.isEmpty())
						{
							final boolean check = this.checkStaticRecId(
									entityMapConditions,cpIdList);
							if (check)
							{
								dynEntitiesIdList.add(entityMap.getContainerId());
							}
						}
						else
						{
							dynEntitiesIdList.add(entityMap.getContainerId());
						}
					}
				}
			}
		}

		return dynEntitiesIdList;
	}

	/**
	 * This method called to check Static Record Identifier.
	 * @param entityMapConditionCollection entityMapConditionCollection
	 * @param cpIdList cpIdList
	 * @return boolean boolean
	 * @throws CacheException CacheException
	 */
	private boolean checkStaticRecId(Collection entityMapConditionCollection, List cpIdList)
	{
		final Iterator entityMapCondIterator = entityMapConditionCollection.iterator();

		try
		{
			final CatissueCoreCacheManager cache = CatissueCoreCacheManager.getInstance();
			if (cpIdList != null && !cpIdList.isEmpty())
			{
				while (entityMapCondIterator.hasNext())
				{
					final EntityMapCondition entityMapCond = (EntityMapCondition)
					entityMapCondIterator.next();
					if (entityMapCond.getTypeId().toString().equals(
							cache.getObjectFromCache(
							AnnotationConstants.COLLECTION_PROTOCOL_ENTITY_ID).toString())
							&& cpIdList.contains(entityMapCond.getStaticRecordId()))
					{
						return true;
					}
				}
			}
		}
		catch (final Exception e)
		{
			AnnotationBizLogic.logger.error(e.getMessage(), e);
		}
		return false;
	}

	/**
	 * Method called to get Entity Map.
	 * @param containerId containerId
	 * @return EntityMap object for its given id
	 */
	public List getEntityMapOnContainer(long containerId)
	{
		List dynamicList = new ArrayList();

		try
		{
			dynamicList = this.retrieve(EntityMap.class.getName(), "containerId",Long.valueOf(
					containerId));
		}
		catch (final BizLogicException e)
		{
			AnnotationBizLogic.logger.error(e.getMessage(), e);
		}

		return dynamicList;
	}

	/**
	 * This method called to insert Entity Map Condition.
	 * @param entityMapCondition entityMapCondition
	 */
	public void insertEntityMapCondition(EntityMapCondition entityMapCondition)
	{
		try
		{
			this.insert(entityMapCondition);
		}
		catch (final Exception e)
		{
			AnnotationBizLogic.logger.error(e.getMessage(), e);
		}

	}

	//Function added by Preeti :  to get all entitymap entries for a dynamic entity container
	/**
	 * This method called to get Entity Maps for Containers.
	 * @param deContainerId deContainerId
	 * @throws BizLogicException BizLogicException
	 * @return entityMaps entityMaps
	 */
	public Collection getEntityMapsForContainer(Long deContainerId) throws BizLogicException
	{
		final List entityMaps = this.retrieve(EntityMap.class.getName(), "containerId",
				deContainerId);
		return entityMaps;
	}
}