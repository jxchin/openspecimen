package com.krishagni.catissueplus.rde.services;

import java.util.List;

import com.krishagni.catissueplus.core.common.events.RequestEvent;
import com.krishagni.catissueplus.core.common.events.ResponseEvent;
import com.krishagni.catissueplus.rde.events.VisitBarcodeDetail;

public interface BarcodeService {
	public ResponseEvent<List<VisitBarcodeDetail>> validate(RequestEvent<List<String>> req);
	
	public List<VisitBarcodeDetail> validate(List<String> barcode);
}
