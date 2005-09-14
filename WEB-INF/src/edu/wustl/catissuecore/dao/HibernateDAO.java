/**
 * <p>Title: HibernateDAO Class>
 * <p>Description:	HibernateDAO is default implemention of AbstractDAO through Hibernate ORM tool.
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 * Created on Mar 16, 2005
 */

package edu.wustl.catissuecore.dao;

import java.util.HashSet;
import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import edu.wustl.catissuecore.audit.AuditManager;
import edu.wustl.catissuecore.audit.Auditable;
import edu.wustl.catissuecore.bizlogic.AbstractBizLogic;
import edu.wustl.catissuecore.bizlogic.BizLogicFactory;
import edu.wustl.catissuecore.domain.CollectionProtocol;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.ParticipantMedicalIdentifier;
import edu.wustl.catissuecore.domain.Site;
import edu.wustl.catissuecore.domain.User;
import edu.wustl.catissuecore.exception.AuditException;
import edu.wustl.catissuecore.util.Permissions;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.catissuecore.util.global.Variables;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.security.SecurityManager;
import edu.wustl.common.security.exceptions.SMException;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.dbManager.DBUtil;
import edu.wustl.common.util.logger.Logger;

/**
 * Default implemention of AbstractDAO through Hibernate ORM tool.
 * @author kapil_kaveeshwar
 */
public class HibernateDAO extends AbstractDAO
{

    protected Session session = null;
    protected Transaction transaction = null;
    protected AuditManager auditManager;

    /**
     * This method will be used to establish the session with the database.
     * Declared in AbstractDAO class.
     * 
     * @throws DAOException
     */
    public void openSession() throws DAOException
    {
        try
        {
            session = DBUtil.currentSession();
            transaction = session.beginTransaction();

            auditManager = new AuditManager();
        }
        catch (HibernateException dbex)
        {
            Logger.out.error(dbex.getMessage(), dbex);
            throw handleError("Error in opening connection: ", dbex);
        }
    }

    /**
     * This method will be used to close the session with the database.
     * Declared in AbstractDAO class.
     * @throws DAOException
     */
    public void closeSession() throws DAOException
    {
        try
        {
            DBUtil.closeSession();
        }
        catch (HibernateException dx)
        {
            Logger.out.error(dx.getMessage(), dx);
            throw handleError("Error in closing connection: ", dx);
        }
        session = null;
        transaction = null;
        auditManager = null;
    }

    /**
     * Commit the database level changes.
     * Declared in AbstractDAO class.
     * @throws DAOException
     */
    public void commit() throws DAOException
    {
        try
        {
            auditManager.insert(this);

            if (transaction != null)
                transaction.commit();
        }
        catch (HibernateException dbex)
        {
            Logger.out.error(dbex.getMessage(), dbex);
            throw handleError("Error in commit: ", dbex);
        }
    }

    /**
     * Rollback all the changes after last commit. 
     * Declared in AbstractDAO class. 
     * @throws DAOException
     */
    public void rollback() throws DAOException
    {
        try
        {
            if (transaction != null)
                transaction.rollback();
        }
        catch (HibernateException dbex)
        {
            Logger.out.error(dbex.getMessage(), dbex);
            throw handleError("Error in rollback: ", dbex);
        }
    }

    /**
     * Saves the persistent object in the database.
     * @param obj The object to be saved.
     * @param session The session in which the object is saved.
     * @throws DAOException
     * @throws HibernateException Exception thrown during hibernate operations.
     */
    public void insert(Object obj, SessionDataBean sessionDataBean,
            boolean isToAudit, boolean isSecureInsert) throws DAOException, UserNotAuthorizedException
    {
        boolean isAuthorized = true;
        try
        {
            if (isSecureInsert)
            {
                if (null != sessionDataBean)
                {
                    isAuthorized = SecurityManager.getInstance(this.getClass())
                            .isAuthorized(sessionDataBean.getUserName(),
                                    obj.getClass().getName(),
                                    Permissions.CREATE);
                }
                else
                {
                    isAuthorized = false;
                }
            }
            Logger.out.debug(" User's Authorization to insert "+obj.getClass().getName()+" "+isAuthorized);
            if(isAuthorized)
            {
                session.save(obj);
                if (obj instanceof Auditable && isToAudit)
                    auditManager.compare((Auditable) obj, null, "INSERT");
            }
            else
            {
                throw new UserNotAuthorizedException("Not Authorized to insert");
            }

           
        }
        catch (HibernateException hibExp)
        {
            throw handleError("", hibExp);
        }
        catch (AuditException hibExp)
        {
            throw handleError("", hibExp);
        }
        catch( SMException smex)
        {
            throw handleError("", smex);
        }
        
    }

    private DAOException handleError(String message, Exception hibExp)
    {
        Logger.out.error(hibExp.getMessage(), hibExp);
        String msg = generateErrorMessage(message, hibExp);
        return new DAOException(msg, hibExp);
    }

    private String generateErrorMessage(String messageToAdd, Exception ex)
    {
        if (ex instanceof HibernateException)
        {
            HibernateException hibernateException = (HibernateException) ex;
            StringBuffer message = new StringBuffer(messageToAdd);
            String str[] = hibernateException.getMessages();
            if (message != null)
            {
                for (int i = 0; i < str.length; i++)
                {
                    message.append(str[i] + " ");
                }
            }
            else
            {
                return "Unknown Error";
            }
            return message.toString();
        }
        else
        {
            return ex.getMessage();
        }
    }

    /**
     * Updates the persistent object in the database.
     * @param obj The object to be updated.
     * @param session The session in which the object is saved.
     * @throws DAOException 
     * @throws HibernateException Exception thrown during hibernate operations.
     */
    public void update(Object obj, SessionDataBean sessionDataBean, boolean isAuditable, boolean isSecureUpdate) throws DAOException, UserNotAuthorizedException
    {
        boolean isAuthorized = true;
        try
        {
            if (isSecureUpdate)
            {
                if (null != sessionDataBean)
                {
                    isAuthorized = SecurityManager.getInstance(this.getClass())
                            .isAuthorized(sessionDataBean.getUserName(),
                                    obj.getClass().getName(),
                                    Permissions.UPDATE);
                }
                else
                {
                    isAuthorized = false;
                }
            }
            Logger.out.debug(" User's Authorization to update "+obj.getClass().getName()+" "+isAuthorized);
            if(isAuthorized)
            {
                session.update(obj);
            }
            else
            {
                throw new UserNotAuthorizedException("Not Authorized to update");
            }

            //            if(isAuditable)
            //        		auditManager.compare((AbstractDomainObject)obj,null,"INSERT");
        }
        catch (HibernateException hibExp)
        {
            Logger.out.error(hibExp.getMessage(), hibExp);
            throw new DAOException("Error in update", hibExp);
        }
        catch (SMException smex)
        {
            Logger.out.error(smex.getMessage(), smex);
            throw new DAOException("Error in update", smex);
        }
    }

    /**
     * Deletes the persistent object from the database.
     * @param obj The object to be deleted.
     */
    public void delete(Object obj) throws DAOException
    {
        try
        {
            session.delete(obj);

            //            if(isAuditable)
            //        		auditManager.compare((AbstractDomainObject)obj,null,"INSERT");
        }
        catch (HibernateException hibExp)
        {
            Logger.out.error(hibExp.getMessage(), hibExp);
            throw new DAOException("Error in delete", hibExp);
        }
    }

    /**
     * Retrieves all the records for class name in sourceObjectName.
     * @param sourceObjectName Contains the classname whose records are to be retrieved.
     */
    public List retrieve(String sourceObjectName) throws DAOException
    {
        return retrieve(sourceObjectName, null, null, null, null, null);
    }

    public List retrieve(String sourceObjectName, String whereColumnName,
            Object whereColumnValue) throws DAOException
    {
        String whereColumnNames[] = {whereColumnName};
        String colConditions[] = {"="};
        Object whereColumnValues[] = {whereColumnValue};

        return retrieve(sourceObjectName, null, whereColumnNames,
                colConditions, whereColumnValues, Constants.AND_JOIN_CONDITION);
    }

    /* (non-Javadoc)
     * @see edu.wustl.catissuecore.dao.AbstractDAO#retrieve(java.lang.String, java.lang.String[])
     */
    public List retrieve(String sourceObjectName, String[] selectColumnName)
            throws DAOException
    {
        String[] whereColumnName = null;
        String[] whereColumnCondition = null;
        Object[] whereColumnValue = null;
        String joinCondition = null;
        return retrieve(sourceObjectName, selectColumnName, whereColumnName,
                whereColumnCondition, whereColumnValue, joinCondition);
    }

    /**
     * Retrieves the records for class name in sourceObjectName according to field values passed in the passed session.
     * @param whereColumnName An array of field names.
     * @param whereColumnCondition The comparision condition for the field values. 
     * @param whereColumnValue An array of field values.
     * @param joinCondition The join condition.
     * @param The session object.
     */
    public List retrieve(String sourceObjectName, String[] selectColumnName,
            String[] whereColumnName, String[] whereColumnCondition,
            Object[] whereColumnValue, String joinCondition)
            throws DAOException
    {
        List list = null;
        try
        {
            StringBuffer sqlBuff = new StringBuffer();

            String className = parseClassName(sourceObjectName);

            if (selectColumnName != null && selectColumnName.length > 0)
            {
                sqlBuff.append("Select ");
                for (int i = 0; i < selectColumnName.length; i++)
                {
                    sqlBuff.append(className + "." + selectColumnName[i]);
                    if (i != selectColumnName.length - 1)
                    {
                        sqlBuff.append(", ");
                    }
                }
                sqlBuff.append(" ");
            }
            Logger.out.debug(" String : " + sqlBuff.toString());

            Query query = null;
            sqlBuff.append("from " + sourceObjectName + " " + className);
            Logger.out.debug(" String : " + sqlBuff.toString());

            if ((whereColumnName != null && whereColumnName.length > 0)
                    && (whereColumnCondition != null && whereColumnCondition.length == whereColumnName.length)
                    && (whereColumnValue != null && whereColumnName.length == whereColumnValue.length))
            {
                if (joinCondition == null)
                    joinCondition = Constants.AND_JOIN_CONDITION;

                sqlBuff.append(" where ");

                //Adds the column name and search condition in where clause. 
                for (int i = 0; i < whereColumnName.length; i++)
                {
                    sqlBuff.append(className + "." + whereColumnName[i] + " ");
                    sqlBuff.append(whereColumnCondition[i] + " ? ");
                    if (i < (whereColumnName.length - 1))
                        sqlBuff.append(" " + joinCondition + " ");
                }

                System.out.println(sqlBuff.toString());

                query = session.createQuery(sqlBuff.toString());

                //Adds the column values in where clause
                for (int i = 0; i < whereColumnValue.length; i++)
                {
                    Logger.out.debug("whereColumnValue[i]. "
                            + whereColumnValue[i]);
                    query.setParameter(i, whereColumnValue[i]);
                }
            }
            else
            {
                query = session.createQuery(sqlBuff.toString());
            }

            list = query.list();

            Logger.out.debug(" String : " + sqlBuff.toString());
        }

        catch (HibernateException hibExp)
        {
            Logger.out.error(hibExp.getMessage(), hibExp);
            throw new DAOException("Error in retrieve " + hibExp.getMessage(),
                    hibExp);
        }
        catch (Exception exp)
        {
            Logger.out.error(exp.getMessage(), exp);
            throw new DAOException("Logical Erroe in retrieve method "
                    + exp.getMessage(), exp);
        }
        return list;
    }

    /**
     * Parses the fully qualified classname and returns only the classname.
     * @param fullyQualifiedName The fully qualified classname. 
     * @return The classname.
     */
    private String parseClassName(String fullyQualifiedName)
    {
        try
        {
            return fullyQualifiedName.substring(fullyQualifiedName
                    .lastIndexOf(".") + 1);
        }
        catch (Exception e)
        {
            return fullyQualifiedName;
        }
    }

    public Object retrieve(String sourceObjectName, Long systemIdentifier)
            throws DAOException
    {
        try
        {
            return session.load(Class.forName(sourceObjectName),
                    systemIdentifier);
        }
        catch (ClassNotFoundException cnFoundExp)
        {
            Logger.out.error(cnFoundExp.getMessage(), cnFoundExp);
            throw new DAOException("Error in retrieve "
                    + cnFoundExp.getMessage(), cnFoundExp);
        }
        catch (HibernateException hibExp)
        {
            Logger.out.error(hibExp.getMessage(), hibExp);
            throw new DAOException("Error in retrieve " + hibExp.getMessage(),
                    hibExp);
        }
    }

    public static void main1(String[] args) throws Exception
    {
        Variables.catissueHome = System.getProperty("user.dir");
        Logger.configure("Application.properties");

        HibernateDAO dao = new HibernateDAO();

        CollectionProtocol collectionProtocol = null;
        try
        {
            dao.openSession();

            collectionProtocol = (CollectionProtocol) dao.retrieve(
                    CollectionProtocol.class.getName(), new Long(3));

            //user = (User)dao.retrieve(User.class.getName(),new Long(1));

            dao.commit();
        }
        catch (DAOException ex)
        {
            ex.printStackTrace();
            try
            {
                dao.rollback();
            }
            catch (DAOException sex)
            {

            }
        }

        dao.closeSession();

        User user = new User();
        user.setSystemIdentifier(new Long(16));
        collectionProtocol.getUserCollection().add(user);

        AbstractBizLogic bl = BizLogicFactory
                .getBizLogic(Constants.COLLECTION_PROTOCOL_FORM_ID);

//        bl.update(collectionProtocol, Constants.HIBERNATE_DAO, getSessionData(request));
        //    	dao.openSession();
        //		
        //		dao.update(collectionProtocol);
        //		
        //		collectionProtocol.getUserCollection()
        //		//user = (User)dao.retrieve(User.class.getName(),new Long(1));
        //		
        //    	dao.commit();

        //    	System.out.println("storageType "+storageType.getSystemIdentifier());
        //    	System.out.println("StorageCapacity "+storageType.getDefaultStorageCapacity().getSystemIdentifier());

        //    	System.out.println("user "+user.getSystemIdentifier());
        //    	System.out.println("Department "+user.getDepartment().getSystemIdentifier());
    }

    public static void main(String[] args) throws Exception
    {
        Variables.catissueHome = System.getProperty("user.dir");
        Logger.configure("Application.properties");
        //		HibernateDAO dao = new HibernateDAO();

        Participant p = new Participant();
        p.setFirstName("A");
        p.setLastName("b");
        p.setParticipantMedicalIdentifierCollection(new HashSet());

        ParticipantMedicalIdentifier pmi = new ParticipantMedicalIdentifier();
        Site aSite = new Site();
        aSite.setSystemIdentifier(new Long(2));
        pmi.setSite(aSite);
        pmi.setMedicalRecordNumber("1");

        p.getParticipantMedicalIdentifierCollection().add(pmi);

        AbstractBizLogic bl = BizLogicFactory
                .getBizLogic(Constants.PARTICIPANT_FORM_ID);
        //		bl.insert(p,getSessionData(request), Constants.HIBERNATE_DAO, true);

        //		dao.openSession();
        //		
        //		dao.insert(p,false);
        //		
        //		dao.commit();
        //		
        //		dao.closeSession();
    }
}