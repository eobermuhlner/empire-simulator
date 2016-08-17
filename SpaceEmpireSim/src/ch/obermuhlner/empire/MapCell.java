package ch.obermuhlner.empire;

import java.util.ArrayList;
import java.util.List;

public class MapCell {

	public final List<RaceCell> raceCells = new ArrayList<>();
	
	public RaceCell getRaceCell(Race race) {
		for (RaceCell raceCell : raceCells) {
			if (raceCell.race.equals(race)) {
				return raceCell;
			}
		}
		return null;
	}
	
	public RaceCell getOrCreateRaceCell(Race race) {
		RaceCell raceCell = getRaceCell(race);
		if (raceCell == null) {
			raceCell = new RaceCell(race);
			raceCells.add(raceCell);
		}
		return raceCell;
	}
}
