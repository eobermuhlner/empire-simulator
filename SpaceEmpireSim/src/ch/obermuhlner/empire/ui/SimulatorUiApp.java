package ch.obermuhlner.empire.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ch.obermuhlner.empire.MapCell;
import ch.obermuhlner.empire.Race;
import ch.obermuhlner.empire.RaceCell;
import ch.obermuhlner.empire.Simulator;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SimulatorUiApp extends Application {

	private static final Color[] STANDARD_COLORS = {
		Color.RED,
		Color.GREEN,
		Color.BLUE
	};
	
	private int cellWidth = 5;
	private int cellHeight = 5;

	private Simulator simulator;
	private Map<Race, Color> raceColors;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		initSimulator();
		simulate(200);
		
		Group root = new Group();
		Scene scene = new Scene(root, 800, 600, Color.BLACK);

		Canvas canvas = new Canvas(simulator.map.sizeX * cellWidth, simulator.map.sizeY * cellWidth);
		drawSimulator(canvas.getGraphicsContext2D());

		root.getChildren().add(canvas);
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void initSimulator() {
		simulator = new Simulator(20, 20);
		
		Random random = new Random(1);
		raceColors = new HashMap<>();
		int colorIndex = 0;
		for(RaceCell raceCell : simulator.totalRaces.values()) {
			Color color;
			if (colorIndex < STANDARD_COLORS.length) {
				color = STANDARD_COLORS[colorIndex++];
			} else {
				random.setSeed(raceCell.race.name.hashCode());
				double hue = random.nextDouble() * 360;
				color = Color.hsb(hue, 1.0, 1.0);
			}
			raceColors.put(raceCell.race, color);
		}
	}

	private void simulate(int steps) {
		for (int i = 0; i < steps; i++) {
			simulator.simulate();
		}
		simulator.print();
	}
	
	private void drawSimulator(GraphicsContext graphics) {
		for (int x = 0; x < simulator.map.sizeX; x++) {
			for (int y = 0; y < simulator.map.sizeY; y++) {
				int z = simulator.map.sizeZ / 2;
				MapCell mapCell = simulator.map.get(x, y, z);
				Color color = Color.BLACK;
				for(RaceCell raceCell : mapCell.raceCells) {
					double value = raceCell.relativePopulation();
					value = value * 0.8 + 0.2;
					color = color.interpolate(raceColors.get(raceCell.race), value);
				}
				if (color != null) {
					graphics.setFill(color);
					graphics.fillRect(x * cellWidth, y * cellHeight, cellWidth, cellHeight);
				}
			}
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
	
}
