package ch.obermuhlner.empire;

public class SimulatorApp {
	public static void main(String[] args) {
		Simulator simulator = new Simulator(3, 3);

		simulator.print();

		for (int i = 0; i < 100; i++) {
			System.out.println("------------------------------------------------------------------------------");
			System.out.println("Simulation #" + i);
			
			simulator.simulate();
			simulator.print();

			System.out.println();
		}
		
	}
}
