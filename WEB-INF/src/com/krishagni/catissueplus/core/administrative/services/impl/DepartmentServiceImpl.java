
package com.krishagni.catissueplus.core.administrative.services.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.krishagni.catissueplus.core.administrative.domain.Department;
import com.krishagni.catissueplus.core.administrative.domain.factory.DepartmentFactory;
import com.krishagni.catissueplus.core.administrative.domain.factory.UserErrorCode;
import com.krishagni.catissueplus.core.administrative.events.CreateDepartmentEvent;
import com.krishagni.catissueplus.core.administrative.events.DepartmentCreatedEvent;
import com.krishagni.catissueplus.core.administrative.events.DepartmentDetails;
import com.krishagni.catissueplus.core.administrative.events.DepartmentDisabledEvent;
import com.krishagni.catissueplus.core.administrative.events.DepartmentUpdatedEvent;
import com.krishagni.catissueplus.core.administrative.events.DisableDepartmentEvent;
import com.krishagni.catissueplus.core.administrative.events.UpdateDepartmentEvent;
import com.krishagni.catissueplus.core.administrative.services.DepartmentService;
import com.krishagni.catissueplus.core.biospecimen.repository.DaoFactory;
import com.krishagni.catissueplus.core.common.PlusTransactional;
import com.krishagni.catissueplus.core.common.errors.ObjectCreationException;

public class DepartmentServiceImpl implements DepartmentService {

	private static final String DEPARTMENT_NAME = "department name";

	@Autowired
	private DaoFactory daoFactory;

	@Autowired
	private DepartmentFactory departmentFactory;

	public void setDaoFactory(DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	public void setDepartmentFactory(DepartmentFactory departmentFactory) {
		this.departmentFactory = departmentFactory;
	}

	@Override
	@PlusTransactional
	public DepartmentCreatedEvent createDepartment(CreateDepartmentEvent event) {
		try {
			Department department = departmentFactory.createDepartment(event.getDepartmentDetails());

			ObjectCreationException exceptionHandler = new ObjectCreationException();
			ensureUniqueDepartmentNameInInstitute(department, exceptionHandler);
			exceptionHandler.checkErrorAndThrow();

			daoFactory.getDepartmentDao().saveOrUpdate(department);
			return DepartmentCreatedEvent.ok(DepartmentDetails.fromDomain(department));
		}
		catch (ObjectCreationException ce) {
			return DepartmentCreatedEvent.invalidRequest(UserErrorCode.ERRORS.message(), ce.getErroneousFields());
		}
		catch (Exception e) {
			return DepartmentCreatedEvent.serverError(e);
		}
	}

	private void ensureUniqueDepartmentNameInInstitute(Department department, ObjectCreationException exceptionHandler) {
		if (!daoFactory.getDepartmentDao().isUniqueDepartmentInInstitute(department.getName(),
				department.getName())) {
			exceptionHandler.addError(UserErrorCode.DUPLICATE_DEPARTMENT_NAME, DEPARTMENT_NAME);
		}
	}

	@Override
	@PlusTransactional
	public DepartmentUpdatedEvent updateDepartment(UpdateDepartmentEvent event) {
		try {
			Long departmentId = event.getDepartmentDetails().getId();
			Department oldDepartment = daoFactory.getDepartmentDao().getDepartment(departmentId);
			if (oldDepartment == null) {
				return DepartmentUpdatedEvent.notFound(departmentId);
			}
			Department department = departmentFactory.createDepartment(event.getDepartmentDetails());

			ObjectCreationException exceptionHandler = new ObjectCreationException();
			checkChangeInDepartmentName(oldDepartment, department, exceptionHandler);
			exceptionHandler.checkErrorAndThrow();

			oldDepartment.update(department);
			daoFactory.getDepartmentDao().saveOrUpdate(oldDepartment);
			return DepartmentUpdatedEvent.ok(DepartmentDetails.fromDomain(oldDepartment));
		}
		catch (ObjectCreationException ce) {
			return DepartmentUpdatedEvent.invalidRequest(UserErrorCode.ERRORS.message(), ce.getErroneousFields());
		}
		catch (Exception e) {
			return DepartmentUpdatedEvent.serverError(e);
		}
	}

	private void checkChangeInDepartmentName(Department oldDepartment, Department department,
			ObjectCreationException exceptionHandler) {
		if (!oldDepartment.getName().equals(department.getName())) {
			ensureUniqueDepartmentNameInInstitute(department, exceptionHandler);
		}
	}

	@Override
	@PlusTransactional
	public DepartmentDisabledEvent deleteDepartment(DisableDepartmentEvent event) {
		try {
			Department department = null;
			if (event.getName() != null) {
				department = daoFactory.getDepartmentDao().getDepartmentByName(event.getName());
				if (department == null) {
					return DepartmentDisabledEvent.notFound(event.getId());
				}
			}
			else {
				department = daoFactory.getDepartmentDao().getDepartment(event.getId());
				if (department == null) {
					return DepartmentDisabledEvent.notFound(event.getId());
				}
			}
			department.delete();
			daoFactory.getDepartmentDao().saveOrUpdate(department);
			return DepartmentDisabledEvent.ok();
		}
		catch (Exception e) {
			return DepartmentDisabledEvent.serverError(e);
		}
	}
}
