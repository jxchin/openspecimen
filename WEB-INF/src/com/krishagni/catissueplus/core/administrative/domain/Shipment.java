package com.krishagni.catissueplus.core.administrative.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.envers.Audited;

import com.krishagni.catissueplus.core.administrative.domain.factory.ShipmentErrorCode;
import com.krishagni.catissueplus.core.biospecimen.domain.BaseEntity;
import com.krishagni.catissueplus.core.biospecimen.domain.Specimen;
import com.krishagni.catissueplus.core.common.CollectionUpdater;
import com.krishagni.catissueplus.core.common.errors.OpenSpecimenException;

@Audited
public class Shipment extends BaseEntity {
	private static final String ENTITY_NAME = "shipment";

	public enum Status {
		PENDING("Pending"),

		SHIPPED("Shipped"),

		RECEIVED("Received");
		
		private final String name;
		
		Status(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public static Status fromName(String name) {
			if (StringUtils.isBlank(name)) {
				return null;
			}
			
			Status result = null;
			for (Status value : Status.values()) {
				if (value.name.equalsIgnoreCase(name)) {
					result = value;
					break;
				}
			}
			
			return result;
		}
	}

	public enum Type {
		SPECIMEN, CONTAINER
	}

	private String name;

	private Type type;
	
	private String  courierName;
	
	private String trackingNumber;
	
	private String trackingUrl;
	
	private Site sendingSite;
	
	private Site receivingSite;
	
	private Date shippedDate;
	
	private User sender;
	
	private String senderComments;
	
	private Date receivedDate;
	
	private User receiver;
	
	private String receiverComments;
	
	private Status status;
	
	private String activityStatus;
	
	private Set<ShipmentSpecimen> shipmentSpecimens = new HashSet<>();

	private Set<ShipmentContainer> shipmentContainers = new HashSet<>();

	private Set<User> notifyUsers = new HashSet<>();

	public static String getEntityName() {
		return ENTITY_NAME;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getCourierName() {
		return courierName;
	}

	public void setCourierName(String courierName) {
		this.courierName = courierName;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	public String getTrackingUrl() {
		return trackingUrl;
	}

	public void setTrackingUrl(String trackingUrl) {
		this.trackingUrl = trackingUrl;
	}

	public Site getSendingSite() {
		return sendingSite;
	}
	
	public void setSendingSite(Site sendingSite) {
		this.sendingSite = sendingSite;
	}
	
	public Site getReceivingSite() {
		return receivingSite;
	}

	public void setReceivingSite(Site receivingSite) {
		this.receivingSite = receivingSite;
	}

	public Date getShippedDate() {
		return shippedDate;
	}

	public void setShippedDate(Date shippedDate) {
		this.shippedDate = shippedDate;
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public String getSenderComments() {
		return senderComments;
	}

	public void setSenderComments(String senderComments) {
		this.senderComments = senderComments;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public User getReceiver() {
		return receiver;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}

	public String getReceiverComments() {
		return receiverComments;
	}

	public void setReceiverComments(String receiverComments) {
		this.receiverComments = receiverComments;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getActivityStatus() {
		return activityStatus;
	}

	public void setActivityStatus(String activityStatus) {
		this.activityStatus = activityStatus;
	}

	public Set<ShipmentSpecimen> getShipmentSpecimens() {
		return shipmentSpecimens;
	}

	public void setShipmentSpecimens(Set<ShipmentSpecimen> shipmentSpecimens) {
		this.shipmentSpecimens = shipmentSpecimens;
	}

	public Set<ShipmentContainer> getShipmentContainers() {
		return shipmentContainers;
	}

	public void setShipmentContainers(Set<ShipmentContainer> shipmentContainers) {
		this.shipmentContainers = shipmentContainers;
	}

	public Set<User> getNotifyUsers() {
		return notifyUsers;
	}

	public void setNotifyUsers(Set<User> notifyUsers) {
		this.notifyUsers = notifyUsers;
	}

	public boolean isSpecimenShipment() {
		return getType() == Type.SPECIMEN;
	}

	public boolean isContainerShipment() {
		return getType() == Type.CONTAINER;
	}

	public void update(Shipment other) {
		setName(other.getName());
		setCourierName(other.getCourierName());
		setTrackingNumber(other.getTrackingNumber());
		setTrackingUrl(other.getTrackingUrl());
		setShippedDate(other.getShippedDate());
		setSender(other.getSender());
		setSenderComments(other.getSenderComments());
		setReceivedDate(other.getReceivedDate());
		setReceiver(other.getReceiver());
		setReceiverComments(other.getReceiverComments());
		setActivityStatus(other.getActivityStatus());
		if (isPending()) {
			setSendingSite(other.getSendingSite());
			setReceivingSite(other.getReceivingSite());
		}

		updateShipmentSpecimens(other);
		updateShipmentContainers(other);
		updateNotifyUsers(other);
		updateStatus(other);
	}

	public void ship() {
		if (isShipped()) {
			//
			// the shipment is already in 'Shipped' state.
			// no need to do any extra work
			//
			return;
		}

		if (!isPending()) {
			throw OpenSpecimenException.userError(ShipmentErrorCode.STATUS_CHANGE_NOT_ALLOWED);
		}

		//
		// if we are here - it means the shipment is in pending state
		//

		if (isSpecimenShipment()) {
			if (CollectionUtils.isEmpty(getShipmentSpecimens())) {
				throw OpenSpecimenException.userError(ShipmentErrorCode.NO_SPECIMENS_TO_SHIP);
			}

			getShipmentSpecimens().forEach(ShipmentSpecimen::ship);
		} else if (isContainerShipment()) {
			if (CollectionUtils.isEmpty(getShipmentContainers())) {
				throw OpenSpecimenException.userError(ShipmentErrorCode.NO_CONTAINERS_TO_SHIP);
			}

			getShipmentContainers().forEach(ShipmentContainer::ship);
		}

		setStatus(Status.SHIPPED);
	}
	
	public void receive(Shipment other) {
		if (!isShipped() && !isReceived()) {
			throw OpenSpecimenException.userError(ShipmentErrorCode.STATUS_CHANGE_NOT_ALLOWED);
		}

		if (isSpecimenShipment()) {
			receiveSpecimens(other);
		} else if (isContainerShipment()) {
			receiveContainers(other);
		}

		setStatus(Status.RECEIVED);
 	}

	public ShipmentSpecimen addShipmentSpecimen(Specimen spmn) {
		ShipmentSpecimen shipmentSpmn = ShipmentSpecimen.createShipmentSpecimen(this, spmn);
		getShipmentSpecimens().add(shipmentSpmn);
		return shipmentSpmn;
	}

	public boolean isPending() {
		return Status.PENDING == getStatus();
	}
	
	public boolean isShipped() {
		return Status.SHIPPED == getStatus();
	}
	
	public boolean isReceived() {
		return Status.RECEIVED == getStatus();
	}

	private Map<Specimen, ShipmentSpecimen> getShipmentSpecimensMap() {
		return getShipmentSpecimens().stream()
			.collect(Collectors.toMap(ShipmentSpecimen::getSpecimen, item -> item));
	}

	private Map<StorageContainer, ShipmentContainer> getShipmentContainersMap() {
		return getShipmentContainers().stream()
			.collect(Collectors.toMap(ShipmentContainer::getContainer, item -> item));
	}

	private void updateShipmentSpecimens(Shipment other) {
		if (!isSpecimenShipment() || !isPending()) {
			return;
		}

		Map<Specimen, ShipmentSpecimen> existingItems = getShipmentSpecimensMap();
		for (ShipmentSpecimen newItem : other.getShipmentSpecimens()) {
			ShipmentSpecimen oldItem = existingItems.remove(newItem.getSpecimen());
			if (oldItem == null) {
				newItem.setShipment(this);
				getShipmentSpecimens().add(newItem);
			}
		}
		
		getShipmentSpecimens().removeAll(existingItems.values());
	}

	private void receiveSpecimens(Shipment other) {
		if (!isReceived()) {
			ensureShippedSpecimens(other);
		}

		Map<Specimen, ShipmentSpecimen> existingItems = getShipmentSpecimensMap();
		for (ShipmentSpecimen newItem : other.getShipmentSpecimens()) {
			ShipmentSpecimen oldItem = existingItems.remove(newItem.getSpecimen());
			oldItem.receive(newItem);
		}
	}

	private void ensureShippedSpecimens(Shipment other) {
		Function<ShipmentSpecimen, Long> fn = (ss) -> ss.getSpecimen().getId();
		List<Long> existingSpecimens = getShipmentSpecimens().stream().map(fn).collect(Collectors.toList());
		List<Long> newSpecimens = other.getShipmentSpecimens().stream().map(fn).collect(Collectors.toList());

		if (!CollectionUtils.isEqualCollection(existingSpecimens, newSpecimens)) {
			throw OpenSpecimenException.userError(ShipmentErrorCode.INVALID_SHIPPED_SPECIMENS);
		}
	}

	private void updateShipmentContainers(Shipment other) {
		if (!isContainerShipment() || !isPending()) {
			return;
		}

		Map<StorageContainer, ShipmentContainer> existingItems = getShipmentContainersMap();
		for (ShipmentContainer newItem : other.getShipmentContainers()) {
			ShipmentContainer oldItem = existingItems.remove(newItem.getContainer());
			if (oldItem == null) {
				newItem.setShipment(this);
				getShipmentContainers().add(newItem);
			}
		}

		getShipmentContainers().removeAll(existingItems.values());
	}

	private void receiveContainers(Shipment other) {
		if (!isReceived()) {
			ensureShippedContainers(other);
		}

		Map<StorageContainer, ShipmentContainer> existingItems = getShipmentContainersMap();
		for (ShipmentContainer newItem : other.getShipmentContainers()) {
			ShipmentContainer oldItem = existingItems.get(newItem.getContainer());
			oldItem.receive(newItem);
		}
	}

	private void ensureShippedContainers(Shipment other) {
		Function<ShipmentContainer, String> fn = (ss) -> ss.getContainer().getName();
		List<String> existingContainers = getShipmentContainers().stream().map(fn).collect(Collectors.toList());
		List<String> newContainers = other.getShipmentContainers().stream().map(fn).collect(Collectors.toList());

		if (!CollectionUtils.isEqualCollection(existingContainers, newContainers)) {
			throw OpenSpecimenException.userError(ShipmentErrorCode.INVALID_SHIPPED_CONTAINERS);
		}
	}

	private void updateNotifyUsers(Shipment other) {
		if (getStatus() != Status.PENDING && getStatus() != Status.SHIPPED) {
			return;
		}

		CollectionUpdater.update(getNotifyUsers(), other.getNotifyUsers());
	}

	private void updateStatus(Shipment other) {
		if ((isPending() || isShipped()) && other.isShipped()) {
			ship();
		} else if ((isShipped() || isReceived()) && other.isReceived()) {
			receive(other);
		} else if (getStatus() != other.getStatus()) {
			throw OpenSpecimenException.userError(ShipmentErrorCode.STATUS_CHANGE_NOT_ALLOWED);
		}
	}
}
