package ch.obermuhlner.empire;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomUtil {

	public static double nextDouble(Random random, double min, double max) {
		return random.nextDouble() * (max - min) + min;
	}

	public static <T> T next(Random random, T[] elements) {
		return next(random, Arrays.asList(elements));
	}
	
	public static <T> T next(Random random, List<T> elements) {
		return elements.get(random.nextInt(elements.size()));
	}

}
