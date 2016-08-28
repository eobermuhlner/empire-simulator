package ch.obermuhlner.empire;

import java.util.Random;

public class RaceCell {

	private static final double MAX_POPULATION = 10000;

	private static final double MAX_ARMY = MAX_POPULATION / 4;

	public static final double MAX_TOTAL_POPULATION = MAX_POPULATION + MAX_ARMY;

	private static final double CIVILIAN_COLLATERAL_CASUALTIES_FACTOR = 0.6;
	
	private static final double CIVILIAN_HEAVY_CASUALTIES_FACTOR = 10.0;

	private static final double ARMY_PRODUCTION_FACTOR = 0.4;

	private static final double MAX_OVERWHELM_FACTOR = 5.0;

	public final Race race;
	
	public double population;
	
	public double army;
	
	public RaceCell(Race race) {
		this.race = race;
	}
	
	public double getTotalPopulation() {
		return population + army;
	}
	
	public double relativePopulation() {
		return (population + army) / (MAX_POPULATION + MAX_ARMY);
	}
	
	public void grow(double totalPopulation, double totalArmy, Random random) {
		double total = totalPopulation + totalArmy;
		double available = (MAX_POPULATION + MAX_ARMY) - total;
		available = available / total * (population + army);
		
		double populationGrowth = MathUtil.clamp(population * race.traits.growth.value, 0, available);
		double armyGrowth = populationGrowth * race.traits.warProduction.value;
		populationGrowth -= armyGrowth;
		
		population += populationGrowth;
		army += armyGrowth * ARMY_PRODUCTION_FACTOR;
		
		population = Math.min(MAX_POPULATION, population);
		army = Math.min(MAX_ARMY, army);
	}

	public double calculateMoveArmy(Random random) {
		double attackArmy = army * race.traits.armyExpansive.value;
		return attackArmy;
	}

	public double calculateColonists(Random random) {
		double colonists = population * race.traits.expansive.value;
		return colonists;
	}
	
	public double calculateAttackStrength(Random random) {
		double attackers = army * race.traits.agressive.value;
		return attackers;
	}

	public double calculateDefenseStrength(Random random) {
		double defenders = army * race.traits.defensive.value;
		return defenders;
	}
	
	public double calculateDeadAttackers(double attackStrength, double defenseStrength, Random random) {
		return calculateOverwhelmCasualties(defenseStrength / attackStrength) * Math.max(attackStrength, defenseStrength);
	}

	public double calculateDeadDefenders(double attackStrength, double defenseStrength, Random random) {
		return calculateOverwhelmCasualties(attackStrength / defenseStrength) * Math.max(attackStrength, defenseStrength);
	}

	private double calculateOverwhelmCasualties(double overwhelm) {
		if (!Double.isFinite(overwhelm)) {
			return MAX_OVERWHELM_FACTOR;
		}
		return MathUtil.smoothstep(0.0, MAX_OVERWHELM_FACTOR, overwhelm * overwhelm * 0.25);
	}
	
	public void casualties(double casualties) {
		army -= casualties;
		population -= casualties * CIVILIAN_COLLATERAL_CASUALTIES_FACTOR;
		
		if (army < 0) {
			population += army * CIVILIAN_HEAVY_CASUALTIES_FACTOR;
			army = 0;
		}
	}

	public void cleanup() {
		if (population < 1.0) {
			population = 0;
		}
		if (army < 1.0) {
			army = 0;
		}
		
		population = Math.floor(population);
		army = Math.floor(army);
	}

	public boolean isDead() {
		return population <= 0 && army <= 0;
	}

	@Override
	public String toString() {
		return race.name + ":pop=" + population + " army=" + army;
	}
}
