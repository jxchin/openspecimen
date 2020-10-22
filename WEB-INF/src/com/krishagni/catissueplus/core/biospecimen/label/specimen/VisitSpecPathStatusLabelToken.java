package com.krishagni.catissueplus.core.biospecimen.label.specimen;

import org.springframework.beans.factory.annotation.Autowired;

import com.krishagni.catissueplus.core.biospecimen.domain.Specimen;
import com.krishagni.catissueplus.core.biospecimen.repository.DaoFactory;
import com.krishagni.catissueplus.core.common.domain.AbstractUniqueIdToken;

public class VisitSpecPathStatusLabelToken extends AbstractUniqueIdToken<Specimen> {

	@Autowired
	private DaoFactory daoFactory;

	public VisitSpecPathStatusLabelToken() {
		this.name = "VISIT_SP_PATH_UID";
	}

	@Override
	public Number getUniqueId(Specimen specimen, String... args) {
		String key = specimen.getVisit().getId().toString() + "_" + specimen.getPathologicalStatus().getId().toString();
		Long uniqueId = daoFactory.getUniqueIdGenerator().getUniqueId(name, key);
		return uniqueId == 1L && !eqArg("output_one", 1, args) ? -1 : uniqueId;
	}
}
