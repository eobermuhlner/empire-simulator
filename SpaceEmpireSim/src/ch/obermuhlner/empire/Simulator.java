package ch.obermuhlner.empire;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Simulator {

	private final Random random = new Random(1);
	
	public final SpaceMap map;
	
	public final Map<Race, RaceCell> totalRaces = new HashMap<>();
	
	public Simulator(int sizeX, int sizeY, int raceCount) {
		map = new SpaceMap(sizeX, sizeY, 1);
		init(raceCount);
	}

	private void init(int raceCount) {
		for (int i = 0; i < raceCount; i++) {
			Race race = initRace();
			totalRaces.put(race, new RaceCell(race));
		}
	}

	public void simulate() {
		clearTotalRaces();
		
		for (int x = 0; x < map.sizeX; x++) {
			for (int y = 0; y < map.sizeY; y++) {
				for (int z = 0; z < map.sizeZ; z++) {
					simulate(x, y, z);
				}
			}
		}

		for(MapCell mapCell : map.cells) {
			countTotalRaces(mapCell);
		}
}
	
	private void clearTotalRaces() {
		for (RaceCell raceCell : totalRaces.values()) {
			raceCell.army = 0;
			raceCell.population = 0;
		}
	}

	private void countTotalRaces(MapCell mapCell) {
		for (RaceCell raceCell : mapCell.raceCells) {
			RaceCell total = totalRaces.get(raceCell.race);
			total.population += raceCell.population;
			total.army += raceCell.army;
		}
	}

	private void simulate(int x, int y, int z) {
		Coord coord = new Coord(x, y, z);

		MapCell mapCell = map.get(x, y, z);
		
		double totalPopulation = 0;
		double totalArmy = 0;
		for (RaceCell raceCell : mapCell.raceCells) {
			totalPopulation += raceCell.population;
			totalArmy += raceCell.army;
		}
		
		Iterator<RaceCell> raceCellsIterator = mapCell.raceCells.iterator();
		while (raceCellsIterator.hasNext()) {
			RaceCell raceCell = raceCellsIterator.next();

			raceCell.grow(totalPopulation, totalArmy, random);
			
			double moveArmy = raceCell.calculateMoveArmy(random);
			if (moveArmy >= 1) {
				Coord neighbourCoord = randomNeighbour(x, y, z);
				if (neighbourCoord != null) {
					MapCell attackMapCell = map.get(neighbourCoord);
					//System.out.println("Move " + coord + " to " + neighbourCoord + " " + raceCell.race.name + " " + moveArmy + " army");
					RaceCell targetRaceCell = attackMapCell.getOrCreateRaceCell(raceCell.race);
					targetRaceCell.army += moveArmy;
					raceCell.army -= moveArmy;
				}
			}
			
			double colonists = raceCell.calculateColonists(random);
			if (colonists >= 1) {
				Coord neighbourCoord = randomNeighbour(x, y, z);
				if (neighbourCoord != null) {
					MapCell colonyMapCell = map.get(neighbourCoord);
					//System.out.println("Move " + coord + " to " + neighbourCoord + " " + raceCell.race.name + " " + colonists + " colonists");
					RaceCell targetRaceCell = colonyMapCell.getOrCreateRaceCell(raceCell.race);
					targetRaceCell.population += colonists;
					raceCell.population -= colonists;				
				}
			}
			
			RaceCell enemyRaceCell = randomEnemyRaceCell(mapCell.raceCells, raceCell);
			if (enemyRaceCell != null && raceCell.army > 0) {
				double attackers = raceCell.calculateAttackStrength(random);
				double defenders = enemyRaceCell.calculateDefenseStrength(random);
				
				double deadAttackers = raceCell.calculateDeadAttackers(attackers, defenders, random);
				double deadDefenders = enemyRaceCell.calculateDeadDefenders(attackers, defenders, random);

				//System.out.println("Attack " + coord + " " + raceCell.race.name + " " + attackers + " -> " + enemyRaceCell.race.name + " " + defenders + " : deadAtt=" + deadAttackers + " deadDef=" + deadDefenders);

				raceCell.casualties(deadAttackers);
				enemyRaceCell.casualties(deadDefenders);
				
				enemyRaceCell.cleanup();
			}

			raceCell.cleanup();
			if (raceCell.isDead()) {
				//System.out.println("Destroyed " + raceCell.race.name + " " + coord); 
				raceCellsIterator.remove();
			}
		}
	}

	private RaceCell randomEnemyRaceCell(List<RaceCell> raceCells, RaceCell raceCell) {
		int n = raceCells.size();
		if (n == 1) {
			return null;
		}
		int offset = random.nextInt(n);
		for (int i = 0; i < n; i++) {
			int index = (i + offset) % n;
			RaceCell enemyCell = raceCells.get(index);
			if (enemyCell != raceCell) {
				if (raceCell.race.getRelationShip(enemyCell.race) == Relationship.War) {
					return enemyCell;
				}
			}
		}
		return null;
	}

	private Coord randomNeighbour(int x, int y, int z) {
		int dx = random.nextInt(3) - 1;
		int dy = random.nextInt(3) - 1;
		int dz = random.nextInt(3) - 1;

		if (dx == 0 && dy == 0 && dz == 0) {
			return null;
		}

		int nx = x + dx;
		int ny = y + dy;
		int nz = z + dz;
		
		if (nx < 0 || nx >= map.sizeX) {
			return null;
		}
		if (ny < 0 || ny >= map.sizeY) {
			return null;
		}
		if (nz < 0 || nz >= map.sizeZ) {
			return null;
		}
		
		return new Coord(nx, ny, nz);
	}

	private Race initRace() {
		Race race = createRace();
		int x = random.nextInt(map.sizeX);
		int y = random.nextInt(map.sizeY);
		int z = random.nextInt(map.sizeZ);
		
		RaceCell raceCell = new RaceCell(race);
		raceCell.population = 10000; //random.nextDouble() * 9000 + 1000;
		
		MapCell mapCell = map.get(x, y, z);
		mapCell.raceCells.add(raceCell);
		
		return race;
	}
	
	private Race createRace() {
		String name = createName();
		Traits traits = createTraits();
		
		System.out.println("Race " + name + " " + traits);
		return new Race(name, traits);
	}

	private Traits createTraits() {
		Traits traits = new Traits();
		traits.growth = nextDouble(random, 0.01, 0.02);
		traits.agressive = nextDouble(random, 0.2, 0.8);
		traits.defensive = nextDouble(random, 0.2, 0.8);
		traits.armyExpansive = nextDouble(random, 0.2, 0.8);
		traits.expansive = nextDouble(random, 0.2, 0.8);
		traits.warProduction = nextDouble(random, 0.0, 0.9);

//		traits.growth = 0.01;
//		traits.agressive = 0.5;
//		traits.defensive = 0.5;
//		traits.armyExpansive = 0.5;
//		traits.expansive = 0.5;
//		traits.warProduction = 0.1;

		return traits;
	}
	
	private static double nextDouble(Random random, double min, double max) {
		return random.nextDouble() * (max - min) + min;
	}

	private static final String VOWELS = "aeiouy";
	private static final String CONSONANTS = "bcdfghjklmnpqrstvwxz";
	
	private String createName() {
		StringBuilder result = new StringBuilder();

		result.append(Character.toUpperCase(randomChar(CONSONANTS)));
		for (int i = 0; i < 2; i++) {
			result.append(randomChar(VOWELS));
			result.append(randomChar(CONSONANTS));
		}
		result.append(randomChar(VOWELS));

		return result.toString();
	}
	
	private char randomChar(String chars) {
		return chars.charAt(random.nextInt(chars.length()));
	}

	public void print() {
		for (int x = 0; x < map.sizeX; x++) {
			for (int y = 0; y < map.sizeY; y++) {
				for (int z = 0; z < map.sizeZ; z++) {
					printMini(x, y, z);
				}
			}
			System.out.println();
		}
		
		for (int x = 0; x < map.sizeX; x++) {
			for (int y = 0; y < map.sizeY; y++) {
				for (int z = 0; z < map.sizeZ; z++) {
					print(x, y, z);
				}
			}
		}
		
		for(RaceCell total : totalRaces.values()) {
			System.out.println("Total " + total);
		}
	}

	private void printMini(int x, int y, int z) {
		MapCell mapCell = map.get(x, y, z);

		for (Race race : totalRaces.keySet()) {
			RaceCell raceCell = mapCell.getRaceCell(race);
			if (raceCell != null) {
				System.out.print(raceCell.race.name.charAt(0));
			} else {
				System.out.print(' ');
			}
			System.out.print('|');
		}
	}

	private void print(int x, int y, int z) {
		MapCell mapCell = map.get(x, y, z);
		
		System.out.println(x + "," + y + "," + z + " : " + mapCell.raceCells);
	}
}
