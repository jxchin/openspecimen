
package com.krishagni.catissueplus.rest.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.krishagni.catissueplus.core.administrative.events.CreateInstituteEvent;
import com.krishagni.catissueplus.core.administrative.events.DisableInstituteEvent;
import com.krishagni.catissueplus.core.administrative.events.DisableUserEvent;
import com.krishagni.catissueplus.core.administrative.events.InstituteCreatedEvent;
import com.krishagni.catissueplus.core.administrative.events.InstituteDetails;
import com.krishagni.catissueplus.core.administrative.events.InstituteDisabledEvent;
import com.krishagni.catissueplus.core.administrative.events.InstituteUpdatedEvent;
import com.krishagni.catissueplus.core.administrative.events.UpdateInstituteEvent;
import com.krishagni.catissueplus.core.administrative.events.UserDisabledEvent;
import com.krishagni.catissueplus.core.administrative.services.InstituteService;
import com.krishagni.catissueplus.core.common.events.EventStatus;

import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.beans.SessionDataBean;

@Controller
@RequestMapping("/institutes")
public class InstituteController {

	@Autowired
	private InstituteService instituteSvc;

	@Autowired
	private HttpServletRequest httpServletRequest;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public InstituteDetails createInstitute(@RequestBody InstituteDetails instituteDetails) {
		CreateInstituteEvent event = new CreateInstituteEvent();
		event.setSessionDataBean(getSession());
		event.setInstituteDetails(instituteDetails);
		InstituteCreatedEvent resp = instituteSvc.createInstitute(event);
		if (resp.getStatus() == EventStatus.OK) {
			
			return resp.getInstituteDetails();
		}
		return null;
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/{instituteId}")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public InstituteDetails updateInstitute(@PathVariable Long instituteId, @RequestBody InstituteDetails instituteDetails) {
		UpdateInstituteEvent event = new UpdateInstituteEvent();
		event.setInstituteDetails(instituteDetails);
		instituteDetails.setId(instituteId);
		InstituteUpdatedEvent resp = instituteSvc.updateInstitute(event);
		if (resp.getStatus() == EventStatus.OK) {
			return resp.getInstituteDetails();
		}
		return null;
	}

	@ResponseBody
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public String disableInst(@PathVariable Long id) {
		DisableInstituteEvent event = new DisableInstituteEvent();
		event.setId(id);
		event.setSessionDataBean(getSession());
		InstituteDisabledEvent resp = instituteSvc.deleteInstitute(event);
		if (resp.getStatus() == EventStatus.OK) {
			return resp.getMessage();
		}
		return null;
	}
	
	@ResponseBody
	@RequestMapping(method = RequestMethod.DELETE, value = "/name={name}")
	@ResponseStatus(HttpStatus.OK)
	public String disableInstitute(@PathVariable String name) {
		DisableInstituteEvent event = new DisableInstituteEvent();
		event.setName(name);
		event.setSessionDataBean(getSession());
		InstituteDisabledEvent resp = instituteSvc.deleteInstitute(event);
		if (resp.getStatus() == EventStatus.OK) {
			return resp.getMessage();
		}
		return null;
	}
	
	private SessionDataBean getSession() {
		return (SessionDataBean) httpServletRequest.getSession().getAttribute(Constants.SESSION_DATA);
	}
}