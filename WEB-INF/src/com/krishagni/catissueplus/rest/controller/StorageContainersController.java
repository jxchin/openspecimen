package com.krishagni.catissueplus.rest.controller;

import java.util.List;

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

import com.krishagni.catissueplus.core.administrative.events.StorageContainerDetail;
import com.krishagni.catissueplus.core.administrative.events.StorageContainerPositionDetail;
import com.krishagni.catissueplus.core.administrative.events.StorageContainerSummary;
import com.krishagni.catissueplus.core.administrative.repository.StorageContainerListCriteria;
import com.krishagni.catissueplus.core.administrative.services.StorageContainerService;
import com.krishagni.catissueplus.core.common.events.RequestEvent;
import com.krishagni.catissueplus.core.common.events.ResponseEvent;

import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.beans.SessionDataBean;

@Controller
@RequestMapping("/storage-containers")
public class StorageContainersController {
	
	@Autowired
	private StorageContainerService storageContainerSvc;
	
	@Autowired
	private HttpServletRequest httpReq;	
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<StorageContainerSummary> getStorageContainers(
			@RequestParam(value = "name", required = false) 
			String name,
			
			@RequestParam(value = "site", required = false)
			String siteName,
			
			@RequestParam(value = "onlyFreeContainers", required = false, defaultValue = "false")
			boolean onlyFreeContainers,
			
			@RequestParam(value = "startAt", required = false, defaultValue = "0")
			int startAt,
			
			@RequestParam(value = "maxRecords", required = false, defaultValue = "100")
			int maxRecords,
			
			@RequestParam(value = "parentContainerId", required = false)
			Long parentContainerId,
			
			@RequestParam(value = "includeChildren", required = false, defaultValue = "false")
			boolean includeChildren,
			
			@RequestParam(value = "topLevelContainers", required = false, defaultValue = "false")
			boolean topLevelContainers,
			
			@RequestParam(value = "specimenClass", required = false)
			String specimenClass,
			
			@RequestParam(value = "specimenType", required = false)
			String specimenType,
			
			@RequestParam(value = "cpId", required = false)
			Long cpId
			) {
		
		StorageContainerListCriteria crit = new StorageContainerListCriteria()
			.query(name)
			.siteName(siteName)
			.onlyFreeContainers(onlyFreeContainers)
			.startAt(startAt)
			.maxResults(maxRecords)
			.parentContainerId(parentContainerId)
			.includeChildren(includeChildren)
			.topLevelContainers(topLevelContainers)
			.specimenClass(specimenClass)
			.specimenType(specimenType)
			.cpId(cpId);
					
		RequestEvent<StorageContainerListCriteria> req = new RequestEvent<StorageContainerListCriteria>(getSession(), crit);
		ResponseEvent<List<StorageContainerSummary>> resp = storageContainerSvc.getStorageContainers(req);
		resp.throwErrorIfUnsuccessful();
		
		return resp.getPayload();
	}

	@RequestMapping(method = RequestMethod.GET, value="{id}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public StorageContainerDetail getStorageContainer(@PathVariable("id") Long containerId) {
		RequestEvent<Long> req = new RequestEvent<Long>(getSession(), containerId);
		ResponseEvent<StorageContainerDetail> resp = storageContainerSvc.getStorageContainer(req);
		resp.throwErrorIfUnsuccessful();
		
		return resp.getPayload();
	}
	
	@RequestMapping(method = RequestMethod.GET, value="{id}/occupied-positions")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<StorageContainerPositionDetail> getStorageContainerOccupiedPositions(@PathVariable("id") Long containerId) {
		RequestEvent<Long> req = new RequestEvent<Long>(getSession(), containerId);
		ResponseEvent<List<StorageContainerPositionDetail>> resp = storageContainerSvc.getOccupiedPositions(req);
		resp.throwErrorIfUnsuccessful();
		
		return resp.getPayload();
	}
		
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public StorageContainerDetail createStorageContainer(@RequestBody StorageContainerDetail detail) {
		RequestEvent<StorageContainerDetail> req = new RequestEvent<StorageContainerDetail>(getSession(), detail);
		ResponseEvent<StorageContainerDetail> resp = storageContainerSvc.createStorageContainer(req);
		resp.throwErrorIfUnsuccessful();
		
		return resp.getPayload();
	}

	@RequestMapping(method = RequestMethod.PUT, value="{id}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public StorageContainerDetail updateStorageContainer(
			@PathVariable("id") 
			Long containerId,
			
			@RequestBody 
			StorageContainerDetail detail) {
		
		detail.setId(containerId);
		
		RequestEvent<StorageContainerDetail> req = new RequestEvent<StorageContainerDetail>(getSession(), detail);
		ResponseEvent<StorageContainerDetail> resp = storageContainerSvc.updateStorageContainer(req);
		resp.throwErrorIfUnsuccessful();
		
		return resp.getPayload();
	}
	
	private SessionDataBean getSession() {
		return (SessionDataBean) httpReq.getSession().getAttribute(Constants.SESSION_DATA);
	}	
}
