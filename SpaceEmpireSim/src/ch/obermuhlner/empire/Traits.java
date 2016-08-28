package ch.obermuhlner.empire;

import java.util.Random;

public class Traits {

	public RangeValue growth;
	
	public RangeValue agressive;
	
	public RangeValue defensive;
	
	public RangeValue armyExpansive;
	
	public RangeValue expansive;
	
	public RangeValue warProduction;

	public void randomWalk(Random random) {
		growth.randomWalk(random);
		agressive.randomWalk(random);
		defensive.randomWalk(random);
		armyExpansive.randomWalk(random);
		expansive.randomWalk(random);
		warProduction.randomWalk(random);
	}
	
	public void setRelativePopulation(double relativePopulation) {
		growth.setRelativeValue(1.0 - relativePopulation);
		agressive.setRelativeValue(1.0 - relativePopulation);
		defensive.setRelativeValue(1.0 - relativePopulation);
		armyExpansive.setRelativeValue(1.0 - relativePopulation);
		expansive.setRelativeValue(1.0 - relativePopulation);
		warProduction.setRelativeValue(1.0 - relativePopulation);
	}
	
	@Override
	public String toString() {
		return "Traits [growth=" + growth + ", agressive=" + agressive + ", defensive=" + defensive + ", armyExpansive=" + armyExpansive + ", expansive=" + expansive + ", warProduction=" + warProduction + "]";
	}
}
