package com.krishagni.catissueplus.core.de.repository;

import java.util.List;

import com.krishagni.catissueplus.core.common.repository.Dao;
import com.krishagni.catissueplus.core.de.domain.SavedQuery;
import com.krishagni.catissueplus.core.de.events.SavedQuerySummary;

public interface SavedQueryDao extends Dao<SavedQuery>{
	public Long getQueriesCount(Long userId);
	
	public List<SavedQuerySummary> getQueries(Long userId, int startAt, int maxRecords);
		
	public SavedQuery getQuery(Long queryId);
	
	public List<SavedQuery> getQueriesByIds(List<Long> queries);
	
	public Long getQueriesCountByFolderId(Long folderId);
	
	public List<SavedQuerySummary> getQueriesByFolderId(Long folderId, int startAt, int maxRecords);
	
	public boolean isQuerySharedWithUser(Long queryId, Long userId);

}
