package ch.obermuhlner.empire.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ch.obermuhlner.empire.MapCell;
import ch.obermuhlner.empire.Race;
import ch.obermuhlner.empire.RaceCell;
import ch.obermuhlner.empire.Simulator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SimulatorUiApp extends Application {

	private static final Color[] STANDARD_COLORS = {
		Color.RED,
		Color.GREEN,
		Color.BLUE,
		Color.YELLOW,
		Color.AZURE,
		Color.MAGENTA
	};
	
	private int cellWidth = 4;
	private int cellHeight = 4;
	private int mapSize = 150;
	private int raceCount = 100;
	
	private Simulator simulator;
	private Map<Race, Color> raceColors;

	
	@Override
	public void start(Stage primaryStage) throws Exception {
		initSimulator();
		
		Group root = new Group();
		Scene scene = new Scene(root, 800, 800, Color.BLACK);

		Canvas canvas = new Canvas(simulator.map.sizeX * cellWidth, simulator.map.sizeY * cellWidth);
		GraphicsContext graphics = canvas.getGraphicsContext2D();
		drawSimulator(graphics);

		root.getChildren().add(canvas);

		Timeline timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(50), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				simulate(1);
				drawSimulator(graphics);
			}
		}));
		timeline.playFromStart();
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void initSimulator() {
		simulator = new Simulator(mapSize, mapSize, raceCount);
		
		Random random = new Random(2);
		raceColors = new HashMap<>();
		int colorIndex = 0;
		for(RaceCell raceCell : simulator.totalRaces.values()) {
			Color color;
			if (colorIndex < STANDARD_COLORS.length) {
				color = STANDARD_COLORS[colorIndex++];
			} else {
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
		//simulator.print();
	}
	
	private void drawSimulator(GraphicsContext graphics) {
		for (int x = 0; x < simulator.map.sizeX; x++) {
			for (int y = 0; y < simulator.map.sizeY; y++) {
				int z = simulator.map.sizeZ / 2;
				MapCell mapCell = simulator.map.get(x, y, z);
				Color color = Color.BLACK;
				for(RaceCell raceCell : mapCell.raceCells) {
					double value = raceCell.relativePopulation();
					value = value * 0.7 + 0.3;
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
