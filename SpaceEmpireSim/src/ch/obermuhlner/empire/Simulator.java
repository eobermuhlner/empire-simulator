package ch.obermuhlner.empire;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Simulator {

	private final Random random = new Random(1);
	
	private final Map map;
	
	public Simulator(int sizeX, int sizeY) {
		map = new Map(sizeX, sizeY, 1);
		init();
	}

	private void init() {
		for (int i = 0; i < 3; i++) {
			initRace();
		}
	}

	public void simulate() {
		for (int x = 0; x < map.sizeX; x++) {
			for (int y = 0; y < map.sizeY; y++) {
				for (int z = 0; z < map.sizeZ; z++) {
					simulate(x, y, z);
				}
			}
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
					System.out.println("Move " + coord + " to " + neighbourCoord + " " + raceCell.race.name + " " + moveArmy + " army");
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
					System.out.println("Move " + coord + " to " + neighbourCoord + " " + raceCell.race.name + " " + colonists + " colonists");
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

				System.out.println("Attack " + coord + " " + raceCell.race.name + " " + attackers + " -> " + enemyRaceCell.race.name + " " + defenders + " : deadAtt=" + deadAttackers + " deadDef=" + deadDefenders);

				raceCell.casualties(deadAttackers);
				enemyRaceCell.casualties(deadDefenders);
				
				enemyRaceCell.cleanup();
			}

			raceCell.cleanup();
			if (raceCell.isDead()) {
				System.out.println("Killed " + raceCell.race.name + " " + coord); 
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

	private void initRace() {
		Race race = createRace();
		int x = random.nextInt(map.sizeX);
		int y = random.nextInt(map.sizeY);
		int z = random.nextInt(map.sizeZ);
		
		RaceCell raceCell = new RaceCell(race);
		raceCell.population = 10000; //random.nextDouble() * 9000 + 1000;
		
		MapCell mapCell = map.get(x, y, z);
		mapCell.raceCells.add(raceCell);
	}
	
	private Race createRace() {
		String name = createName();
		Traits traits = createTraits();
		
		System.out.println("Race " + name + " " + traits);
		return new Race(name, traits);
	}

	private Traits createTraits() {
		Traits traits = new Traits();
		traits.growth = random.nextDouble() * 0.01;
		traits.agressive = random.nextDouble();
		traits.defensive = random.nextDouble();
		traits.armyExpansive = random.nextDouble();
		traits.expansive = random.nextDouble();
		traits.warProduction = random.nextDouble();

		traits.growth = 0.005;
		traits.agressive = 0.5;
		traits.defensive = 0.5;
		traits.armyExpansive = 0.5;
		traits.expansive = 0.5;
		traits.warProduction = 0.1;

		return traits;
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
					print(x, y, z);
				}
			}
		}
	}

	private void print(int x, int y, int z) {
		MapCell mapCell = map.get(x, y, z);
		
		System.out.println(x + "," + y + "," + z + " : " + mapCell.raceCells);
	}
}
