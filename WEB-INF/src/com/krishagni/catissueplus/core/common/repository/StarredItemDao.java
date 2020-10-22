package com.krishagni.catissueplus.core.common.repository;

import com.krishagni.catissueplus.core.common.domain.StarredItem;

public interface StarredItemDao extends Dao<StarredItem> {
	StarredItem getItem(String itemType, Long itemId, Long userId);
}
