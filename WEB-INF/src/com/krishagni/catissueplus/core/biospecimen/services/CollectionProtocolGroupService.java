package com.krishagni.catissueplus.core.biospecimen.services;

import java.util.List;

import com.krishagni.catissueplus.core.biospecimen.events.CollectionProtocolGroupDetail;
import com.krishagni.catissueplus.core.biospecimen.events.CollectionProtocolGroupSummary;
import com.krishagni.catissueplus.core.biospecimen.events.CpGroupFormsDetail;
import com.krishagni.catissueplus.core.biospecimen.events.CpGroupWorkflowCfgDetail;
import com.krishagni.catissueplus.core.biospecimen.repository.CpGroupListCriteria;
import com.krishagni.catissueplus.core.common.events.BulkDeleteEntityOp;
import com.krishagni.catissueplus.core.common.events.BulkDeleteEntityResp;
import com.krishagni.catissueplus.core.common.events.EntityQueryCriteria;
import com.krishagni.catissueplus.core.common.events.RequestEvent;
import com.krishagni.catissueplus.core.common.events.ResponseEvent;

public interface CollectionProtocolGroupService {
	ResponseEvent<List<CollectionProtocolGroupSummary>> getGroups(RequestEvent<CpGroupListCriteria> req);

	ResponseEvent<CollectionProtocolGroupDetail> getGroup(RequestEvent<EntityQueryCriteria> req);

	ResponseEvent<CollectionProtocolGroupDetail> createGroup(RequestEvent<CollectionProtocolGroupDetail> req);

	ResponseEvent<CollectionProtocolGroupDetail> updateGroup(RequestEvent<CollectionProtocolGroupDetail> req);

	ResponseEvent<BulkDeleteEntityResp<CollectionProtocolGroupSummary>> deleteGroups(RequestEvent<BulkDeleteEntityOp> req);

	ResponseEvent<List<CpGroupFormsDetail>> getForms(RequestEvent<EntityQueryCriteria> req);

	ResponseEvent<Boolean> addForms(RequestEvent<CpGroupFormsDetail> req);

	ResponseEvent<Boolean> removeForms(RequestEvent<CpGroupFormsDetail> req);

	ResponseEvent<CpGroupWorkflowCfgDetail> getWorkflows(RequestEvent<EntityQueryCriteria> req);

	ResponseEvent<CpGroupWorkflowCfgDetail> saveWorkflows(RequestEvent<CpGroupWorkflowCfgDetail> req);
}
