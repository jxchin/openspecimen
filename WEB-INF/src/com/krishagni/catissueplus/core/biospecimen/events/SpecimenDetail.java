package com.krishagni.catissueplus.core.biospecimen.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.krishagni.catissueplus.core.administrative.domain.PermissibleValue;
import com.krishagni.catissueplus.core.administrative.events.StorageLocationSummary;
import com.krishagni.catissueplus.core.biospecimen.domain.Specimen;
import com.krishagni.catissueplus.core.biospecimen.domain.SpecimenRequirement;
import com.krishagni.catissueplus.core.biospecimen.domain.Visit;
import com.krishagni.catissueplus.core.common.ListenAttributeChanges;
import com.krishagni.catissueplus.core.common.util.Utility;
import com.krishagni.catissueplus.core.de.events.ExtensionDetail;

@ListenAttributeChanges
public class SpecimenDetail extends SpecimenInfo {

	private static final long serialVersionUID = -752005520158376620L;

	private CollectionEventDetail collectionEvent;
	
	private ReceivedEventDetail receivedEvent;
	
	private String labelFmt;
	
	private String labelAutoPrintMode;
	
	private Set<String> biohazards;
	
	private String comments;
	
	private Boolean closeAfterChildrenCreation;  
	
	private List<SpecimenDetail> children;

	private Long pooledSpecimenId;
	
	private String pooledSpecimenLabel;

	private List<SpecimenDetail> specimensPool;

	//
	// Properties required for auto-creation of containers
	//
	private StorageLocationSummary containerLocation;

	private Long containerTypeId;

	private String containerTypeName;

	// This is needed for creation of derivatives from BO for closing parent specimen.
	private Boolean closeParent;
	
	private Boolean poolSpecimen;
	
	private String reqCode;

	private ExtensionDetail extensionDetail;

	private boolean reserved;

	//
	// transient variables specifying action to be performed
	//
	private boolean forceDelete;

	private boolean printLabel;

	private Integer incrParentFreezeThaw;

	private Date transferTime;

	private String transferComments;

	private boolean autoCollectParents;

	private String uid;

	private String parentUid;

	private Long dpId;

	private StorageLocationSummary holdingLocation;

	public CollectionEventDetail getCollectionEvent() {
		return collectionEvent;
	}

	public void setCollectionEvent(CollectionEventDetail collectionEvent) {
		this.collectionEvent = collectionEvent;
	}

	public ReceivedEventDetail getReceivedEvent() {
		return receivedEvent;
	}

	public void setReceivedEvent(ReceivedEventDetail receivedEvent) {
		this.receivedEvent = receivedEvent;
	}

	public String getLabelFmt() {
		return labelFmt;
	}

	public void setLabelFmt(String labelFmt) {
		this.labelFmt = labelFmt;
	}

	public String getLabelAutoPrintMode() {
		return labelAutoPrintMode;
	}

	public void setLabelAutoPrintMode(String labelAutoPrintMode) {
		this.labelAutoPrintMode = labelAutoPrintMode;
	}

	public List<SpecimenDetail> getChildren() {
		return children;
	}

	public void setChildren(List<SpecimenDetail> children) {
		this.children = children;
	}

	public Long getPooledSpecimenId() {
		return pooledSpecimenId;
	}

	public void setPooledSpecimenId(Long pooledSpecimenId) {
		this.pooledSpecimenId = pooledSpecimenId;
	}

	public String getPooledSpecimenLabel() {
		return pooledSpecimenLabel;
	}

	public void setPooledSpecimenLabel(String pooledSpecimenLabel) {
		this.pooledSpecimenLabel = pooledSpecimenLabel;
	}

	public List<SpecimenDetail> getSpecimensPool() {
		return specimensPool;
	}

	public void setSpecimensPool(List<SpecimenDetail> specimensPool) {
		this.specimensPool = specimensPool;
	}

	@JsonIgnore
	public StorageLocationSummary getContainerLocation() {
		return containerLocation;
	}

	@JsonProperty
	public void setContainerLocation(StorageLocationSummary containerLocation) {
		this.containerLocation = containerLocation;
	}

	@JsonIgnore
	public Long getContainerTypeId() {
		return containerTypeId;
	}

	@JsonProperty
	public void setContainerTypeId(Long containerTypeId) {
		this.containerTypeId = containerTypeId;
	}

	@JsonIgnore
	public String getContainerTypeName() {
		return containerTypeName;
	}

	@JsonProperty
	public void setContainerTypeName(String containerTypeName) {
		this.containerTypeName = containerTypeName;
	}

	public Set<String> getBiohazards() {
		return biohazards;
	}

	public void setBiohazards(Set<String> biohazards) {
		this.biohazards = biohazards;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@JsonIgnore
	public Boolean getCloseAfterChildrenCreation() {
		return closeAfterChildrenCreation;
	}

	@JsonProperty
	public void setCloseAfterChildrenCreation(Boolean closeAfterChildrenCreation) {
		this.closeAfterChildrenCreation = closeAfterChildrenCreation;
	}

	@JsonIgnore
	public Boolean getCloseParent() {
		return closeParent;
	}

	@JsonProperty
	public void setCloseParent(Boolean closeParent) {
		this.closeParent = closeParent;
	}

	public Boolean getPoolSpecimen() {
		return poolSpecimen;
	}

	public void setPoolSpecimen(Boolean poolSpecimen) {
		this.poolSpecimen = poolSpecimen;
	}
	
	public String getReqCode() {
		return reqCode;
	}

	public void setReqCode(String reqCode) {
		this.reqCode = reqCode;
	}

	@JsonIgnore
	public boolean closeParent() {
		return closeParent == null ? false : closeParent;
	}

	public ExtensionDetail getExtensionDetail() {
		return extensionDetail;
	}

	public void setExtensionDetail(ExtensionDetail extensionDetail) {
		this.extensionDetail = extensionDetail;
	}

	public boolean isReserved() {
		return reserved;
	}

	public void setReserved(boolean reserved) {
		this.reserved = reserved;
	}

	@JsonIgnore
	public boolean isForceDelete() {
		return forceDelete;
	}

	public void setForceDelete(boolean forceDelete) {
		this.forceDelete = forceDelete;
	}
	
	//
	// Do not serialise printLabel from interaction object to response JSON. Therefore @JsonIgnore
	// However, deserialise, if present, from input request JSON to interaction object. Hence @JsonProperty
	//
	@JsonIgnore
	public boolean isPrintLabel() {
		return printLabel;
	}

	@JsonProperty
	public void setPrintLabel(boolean printLabel) {
		this.printLabel = printLabel;
	}

	@JsonIgnore
	public Integer getIncrParentFreezeThaw() {
		return incrParentFreezeThaw;
	}

	@JsonProperty
	public void setIncrParentFreezeThaw(Integer incrParentFreezeThaw) {
		this.incrParentFreezeThaw = incrParentFreezeThaw;
	}

	@JsonIgnore
	public Date getTransferTime() {
		return transferTime;
	}

	@JsonProperty
	public void setTransferTime(Date transferTime) {
		this.transferTime = transferTime;
	}

	@JsonIgnore
	public String getTransferComments() {
		return transferComments;
	}

	@JsonProperty
	public void setTransferComments(String transferComments) {
		this.transferComments = transferComments;
	}

	@JsonIgnore
	public boolean isAutoCollectParents() {
		return autoCollectParents;
	}

	@JsonProperty
	public void setAutoCollectParents(boolean autoCollectParents) {
		this.autoCollectParents = autoCollectParents;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getParentUid() {
		return parentUid;
	}

	public void setParentUid(String parentUid) {
		this.parentUid = parentUid;
	}

	public Long getDpId() {
		return dpId;
	}

	public void setDpId(Long dpId) {
		this.dpId = dpId;
	}

	@JsonIgnore
	public StorageLocationSummary getHoldingLocation() {
		return holdingLocation;
	}

	@JsonProperty
	public void setHoldingLocation(StorageLocationSummary holdingLocation) {
		this.holdingLocation = holdingLocation;
	}

	public static SpecimenDetail from(Specimen specimen) {
		return from(specimen, true, true);
	}

	public static SpecimenDetail from(Specimen specimen, boolean partial, boolean excludePhi) {
		return from(specimen, partial, excludePhi, false);
	}

	public static SpecimenDetail from(Specimen specimen, boolean partial, boolean excludePhi, boolean excludeChildren) {
		SpecimenDetail result = new SpecimenDetail();
		SpecimenInfo.fromTo(specimen, result);
		
		SpecimenRequirement sr = specimen.getSpecimenRequirement();
		if (!excludeChildren) {
			if (sr == null) {
				List<SpecimenDetail> children = Utility.nullSafeStream(specimen.getChildCollection())
					.map(child -> from(child, partial, excludePhi, excludeChildren))
					.collect(Collectors.toList());
				sort(children);
				result.setChildren(children);
			} else {
				if (sr.isPooledSpecimenReq()) {
					result.setSpecimensPool(getSpecimens(specimen.getVisit(), sr.getSpecimenPoolReqs(), specimen.getSpecimensPool(), partial, excludePhi, excludeChildren));
				}
				result.setPoolSpecimen(sr.isSpecimenPoolReq());
				result.setChildren(getSpecimens(specimen.getVisit(), sr.getChildSpecimenRequirements(), specimen.getChildCollection(), partial, excludePhi, excludeChildren));
			}

			if (specimen.getPooledSpecimen() != null) {
				result.setPooledSpecimenId(specimen.getPooledSpecimen().getId());
				result.setPooledSpecimenLabel(specimen.getPooledSpecimen().getLabel());
			}
		}

		//
		// false to ensure we don't end up in infinite recurssion
		//
		result.setLabelFmt(specimen.getLabelTmpl(false));
		if (sr != null && sr.getLabelAutoPrintModeToUse() != null) {
			result.setLabelAutoPrintMode(sr.getLabelAutoPrintModeToUse().name());
		}

		result.setReqCode(sr != null ? sr.getCode() : null);
		result.setBiohazards(PermissibleValue.toValueSet(specimen.getBiohazards()));
		result.setComments(specimen.getComment());
		result.setReserved(specimen.isReserved());

		if (!partial) {
			result.setExtensionDetail(ExtensionDetail.from(specimen.getExtension(), excludePhi));

			if (specimen.isPrimary()) {
				result.setCollectionEvent(CollectionEventDetail.from(specimen.getCollectionEvent()));
				result.setReceivedEvent(ReceivedEventDetail.from(specimen.getReceivedEvent()));
			} else {
				result.setCollectionEvent(CollectionEventDetail.from(specimen.getCollRecvDetails()));
				result.setReceivedEvent(ReceivedEventDetail.from(specimen.getCollRecvDetails()));
			}
		}

		result.setUid(specimen.getUid());
		result.setParentUid(specimen.getParentUid());
		return result;
	}
	
	public static List<SpecimenDetail> from(Collection<Specimen> specimens) {
		return Utility.nullSafeStream(specimens).map(SpecimenDetail::from).collect(Collectors.toList());
	}
	
	public static SpecimenDetail from(SpecimenRequirement anticipated) {
		return SpecimenDetail.from(anticipated, false);
	}

	public static SpecimenDetail from(SpecimenRequirement anticipated, boolean excludeChildren) {
		SpecimenDetail result = new SpecimenDetail();		
		SpecimenInfo.fromTo(anticipated, result);
		
		if (anticipated.isPooledSpecimenReq()) {
			result.setSpecimensPool(fromAnticipated(anticipated.getSpecimenPoolReqs(), excludeChildren));
		}
		
		result.setPoolSpecimen(anticipated.isSpecimenPoolReq());

		if (!excludeChildren) {
			result.setChildren(fromAnticipated(anticipated.getChildSpecimenRequirements(), excludeChildren));
		}

		result.setLabelFmt(anticipated.getLabelTmpl());
		if (anticipated.getLabelAutoPrintModeToUse() != null) {
			result.setLabelAutoPrintMode(anticipated.getLabelAutoPrintModeToUse().name());
		}
		result.setReqCode(anticipated.getCode());
		return result;		
	}

	public static void sort(List<SpecimenDetail> specimens) {
		Collections.sort(specimens);
		
		for (SpecimenDetail specimen : specimens) {
			if (specimen.getChildren() != null) {
				sort(specimen.getChildren());
			}
		}
	}
	
	public static List<SpecimenDetail> getSpecimens(
			Visit visit,
			Collection<SpecimenRequirement> anticipated,
			Collection<Specimen> specimens,
			boolean partial,
			boolean excludePhi,
			boolean excludeChildren) {
		List<SpecimenDetail> result = Utility.stream(specimens)
			.map(s -> SpecimenDetail.from(s, partial, excludePhi, excludeChildren))
			.collect(Collectors.toList());

		merge(visit, anticipated, result, null, getReqSpecimenMap(result), excludeChildren);
		SpecimenDetail.sort(result);
		return result;
	}

	private static Map<Long, SpecimenDetail> getReqSpecimenMap(List<SpecimenDetail> specimens) {
		Map<Long, SpecimenDetail> reqSpecimenMap = new HashMap<>();
						
		List<SpecimenDetail> remaining = new ArrayList<>();
		remaining.addAll(specimens);
		
		while (!remaining.isEmpty()) {
			SpecimenDetail specimen = remaining.remove(0);
			Long srId = (specimen.getReqId() == null) ? -1 : specimen.getReqId();
			reqSpecimenMap.put(srId, specimen);

			if (specimen.getChildren() != null) {
				remaining.addAll(specimen.getChildren());
			}
		}
		
		return reqSpecimenMap;
	}
	
	private static void merge(
			Visit visit,
			Collection<SpecimenRequirement> anticipatedSpecimens, 
			List<SpecimenDetail> result, 
			SpecimenDetail currentParent,
			Map<Long, SpecimenDetail> reqSpecimenMap,
			boolean excludeChildren) {
		
		for (SpecimenRequirement anticipated : anticipatedSpecimens) {
			SpecimenDetail specimen = reqSpecimenMap.get(anticipated.getId());
			if (specimen != null && excludeChildren) {
				continue;
			}

			if (specimen != null) {
				merge(visit, anticipated.getChildSpecimenRequirements(), result, specimen, reqSpecimenMap, excludeChildren);
			} else if (!anticipated.isClosed()) {
				specimen = SpecimenDetail.from(anticipated, excludeChildren);
				setVisitDetails(visit, specimen);

				if (currentParent == null) {
					result.add(specimen);
				} else {
					specimen.setParentId(currentParent.getId());
					currentParent.getChildren().add(specimen);
				}				
			}						
		}
	}

	private static void setVisitDetails(Visit visit, SpecimenDetail specimen) {
		if (visit == null) {
			return;
		}

		specimen.setVisitId(visit.getId());
		specimen.setVisitName(visit.getName());
		specimen.setVisitStatus(visit.getStatus());
		specimen.setSprNo(visit.getSurgicalPathologyNumber());
		specimen.setVisitDate(visit.getVisitDate());
		Utility.nullSafeStream(specimen.getChildren()).forEach(child -> setVisitDetails(visit, child));
	}

	private static List<SpecimenDetail> fromAnticipated(Collection<SpecimenRequirement> anticipatedSpecimens, boolean excludeChildren) {
		return Utility.nullSafeStream(anticipatedSpecimens)
			.filter(anticipated -> !anticipated.isClosed())
			.map(s -> SpecimenDetail.from(s, excludeChildren))
			.collect(Collectors.toList());
	}
}
