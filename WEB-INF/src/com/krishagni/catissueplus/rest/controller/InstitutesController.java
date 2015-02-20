package com.krishagni.catissueplus.rest.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.krishagni.catissueplus.core.administrative.events.DeleteInstituteOp;
import com.krishagni.catissueplus.core.administrative.events.InstituteDetail;
import com.krishagni.catissueplus.core.administrative.events.InstituteQueryCriteria;
import com.krishagni.catissueplus.core.administrative.repository.InstituteListCriteria;
import com.krishagni.catissueplus.core.administrative.services.InstituteService;
import com.krishagni.catissueplus.core.common.events.RequestEvent;
import com.krishagni.catissueplus.core.common.events.ResponseEvent;

import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.beans.SessionDataBean;

@Controller
@RequestMapping("/institutes")
public class InstitutesController {

	@Autowired
	private InstituteService instituteSvc;

	@Autowired
	private HttpServletRequest httpServletRequest;
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public List<InstituteDetail> getInstitutes(
			@RequestParam(value = "startAt", required = false, defaultValue = "0")
			int startAt,
			
			@RequestParam(value = "maxResults", required = false, defaultValue = "100") 
			int maxResults,
			
			@RequestParam(value = "name", required = false)
			String name
			) {
		
		InstituteListCriteria crit  = new InstituteListCriteria()
				.query(name)
				.startAt(startAt)
				.maxResults(maxResults);
		
		RequestEvent<InstituteListCriteria> req = new RequestEvent<InstituteListCriteria>(getSession(), crit);
		ResponseEvent<List<InstituteDetail>> resp = instituteSvc.getInstitutes(req);
		resp.throwErrorIfUnsuccessful();
		
		return resp.getPayload();
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/{id}")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public InstituteDetail getInstitute(@PathVariable Long id) {
		InstituteQueryCriteria crit = new InstituteQueryCriteria(); 
		crit.setId(id);
		
		RequestEvent<InstituteQueryCriteria> req = new RequestEvent<InstituteQueryCriteria>(getSession(), crit);		
		ResponseEvent<InstituteDetail> resp = instituteSvc.getInstitute(req);
		resp.throwErrorIfUnsuccessful();
		
		return resp.getPayload();
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public InstituteDetail createInstitute(@RequestBody InstituteDetail detail) {
		RequestEvent<InstituteDetail> req = new RequestEvent<InstituteDetail>(getSession(), detail);		
		ResponseEvent<InstituteDetail> resp = instituteSvc.createInstitute(req);
		resp.throwErrorIfUnsuccessful();
		
		return resp.getPayload();
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/{instituteId}")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public InstituteDetail updateInstitute(@PathVariable Long instituteId, @RequestBody InstituteDetail instituteDetail) {
		instituteDetail.setId(instituteId);
		
		RequestEvent<InstituteDetail> req = new RequestEvent<InstituteDetail>(getSession(), instituteDetail);
		ResponseEvent<InstituteDetail> resp = instituteSvc.updateInstitute(req);
		resp.throwErrorIfUnsuccessful();
		
		return resp.getPayload();
	}

	@ResponseBody
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Map<String, List> deleteInstitute(@PathVariable Long id,
			@RequestParam(value="close", required=false, defaultValue="false") boolean close) {
		DeleteInstituteOp deleteInstOp = new DeleteInstituteOp();
		deleteInstOp.setId(id);
		deleteInstOp.setClose(close);
		RequestEvent<DeleteInstituteOp> req = new RequestEvent<DeleteInstituteOp>(getSession(), deleteInstOp);
		ResponseEvent<Map<String,List>> resp = instituteSvc.deleteInstitute(req);
		
		resp.throwErrorIfUnsuccessful();
		
		return resp.getPayload();
	}

	private SessionDataBean getSession() {
		return (SessionDataBean) httpServletRequest.getSession().getAttribute(
				Constants.SESSION_DATA);
	}
}