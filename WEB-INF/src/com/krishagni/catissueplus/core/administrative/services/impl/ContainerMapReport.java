package com.krishagni.catissueplus.core.administrative.services.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import com.krishagni.catissueplus.core.administrative.domain.PositionAssigner;
import com.krishagni.catissueplus.core.administrative.domain.RowMajorPositionAssigner;
import com.krishagni.catissueplus.core.administrative.domain.StorageContainer;
import com.krishagni.catissueplus.core.administrative.domain.StorageContainerPosition;
import com.krishagni.catissueplus.core.administrative.domain.factory.StorageContainerErrorCode;
import com.krishagni.catissueplus.core.administrative.services.ContainerReport;
import com.krishagni.catissueplus.core.common.Pair;
import com.krishagni.catissueplus.core.common.errors.OpenSpecimenException;
import com.krishagni.catissueplus.core.common.events.ExportedFileDetail;
import com.krishagni.catissueplus.core.common.util.ConfigUtil;
import com.krishagni.catissueplus.core.common.util.CsvFileWriter;
import com.krishagni.catissueplus.core.common.util.CsvWriter;

public class ContainerMapReport extends AbstractContainerReport implements ContainerReport {

	@Override
	public String getName() {
		return "container_map_report";
	}

	@Override
	public ExportedFileDetail generate(StorageContainer container, Object... params) {
		File file = null;
		CsvWriter writer = null;
		try {
			if (container.isDimensionless()) {
				throw  OpenSpecimenException.userError(StorageContainerErrorCode.DIMLESS_NO_MAP, container.getName());
			}

			String uuid = UUID.randomUUID().toString();
			String csvFilename = getCsvFileId(container, uuid);
			file = new File(ConfigUtil.getInstance().getReportsDir(), csvFilename + ".csv");
			writer = CsvFileWriter.createCsvFileWriter(file);

			exportContainerSummary(container, writer);
			exportOccupiedPositions(container, writer);

			writer.flush();
			return new ExportedFileDetail(csvFilename, file);
		} catch (IOException ioe) {
			throw OpenSpecimenException.serverError(ioe);
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}
		
	private void exportOccupiedPositions(StorageContainer container, CsvWriter writer) {
		PositionAssigner pa = container.getPositionAssigner();
		boolean rowMajor = pa instanceof RowMajorPositionAssigner;

		List<String> cells = new ArrayList<>();
		if (rowMajor) {
			cells.add("");
		}

		for (int i = 1; i <= container.getNoOfColumns(); ++i) {
			Pair<Integer, Integer> coord =  pa.fromMapIdx(container, 0, i - 1);
			if (rowMajor) {
				cells.add(container.toColumnLabelingScheme(coord.second()));
			} else {
				cells.add(container.toColumnLabelingScheme(coord.first()));
			}
		}

		if (!rowMajor) {
			cells.add("");
		}

		writer.writeNext(cells.toArray(new String[0]));

		Map<Integer, StorageContainerPosition> occupantsMap =
			container.getOccupiedPositions().stream().collect(
				Collectors.toMap(
					pos -> pa.toPosition(container, pos.getPosTwoOrdinal(), pos.getPosOneOrdinal()),
					pos -> pos
				)
			);

		for (int j = 1; j <= container.getNoOfRows(); ++j) {
			cells.clear();

			Pair<Integer, Integer> rowCoord = pa.fromMapIdx(container, j - 1, 0);
			String rowLabel;
			if (rowMajor) {
				rowLabel = container.toRowLabelingScheme(rowCoord.first());
				cells.add(rowLabel);
			} else {
				rowLabel = container.toRowLabelingScheme(rowCoord.second());
			}

			for (int i = 1; i <= container.getNoOfColumns(); ++i) {
				Pair<Integer, Integer> posCoord = pa.fromMapIdx(container, j - 1, i - 1);
				Integer pos = pa.toPosition(container, posCoord.first(), posCoord.second());
				StorageContainerPosition occupant = occupantsMap.get(pos);
				if (occupant != null) {
					if (occupant.getOccupyingContainer() != null) {
						cells.add(occupant.getOccupyingContainer().getName());
					} else if (occupant.getOccupyingSpecimen() != null) {
						cells.add(occupant.getOccupyingSpecimen().getLabel());
					} else if (occupant.isBlocked()) {
						cells.add(message(BLOCKED));
					} else {
						cells.add("");
					}
				} else {
					cells.add("");
				}
			}

			if (!rowMajor) {
				cells.add(rowLabel);
			}

			writer.writeNext(cells.toArray(new String[0]));
		}
	}

	private static final String BLOCKED = "storage_container_cell_blocked";
}
