package ch.obermuhlner.empire;

import java.util.Random;

import org.junit.Test;

public class RaceCellTest {

	@Test
	public void testCalculateDeadAttackers() {
		Traits traits = new Traits();
		Race race = new Race("Test1", traits);
		RaceCell raceCell = new RaceCell(race);
		
		Random random = null;
		double defenseStrength = 5;
		for (double attackStrength = 0; attackStrength < 10; attackStrength++) {
			double deadAttackers = raceCell.calculateDeadAttackers(attackStrength, defenseStrength, random);
			double deadDefenders = raceCell.calculateDeadDefenders(attackStrength, defenseStrength, random);
			System.out.println("att " + attackStrength + " vs def " + defenseStrength + " => dead att " + deadAttackers + " dead def " + deadDefenders);
		}
	}
}
