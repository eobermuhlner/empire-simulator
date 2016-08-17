package ch.obermuhlner.empire;

import java.util.Map;
import java.util.HashMap;

public class Race {

	public final String name;
	
	public final Traits traits;

	public final Map<Race, Relationship> relationShips = new HashMap<>();
	
	public Race(String name, Traits traits) {
		this.name = name;
		this.traits = traits;
	}
	
	public Relationship getRelationShip(Race otherRace) {
		Relationship relationship = relationShips.get(otherRace);
		if (relationship == null) {
			relationship = Relationship.War;
			relationShips.put(otherRace, relationship);
		}
		
		return relationship;
	}
}
