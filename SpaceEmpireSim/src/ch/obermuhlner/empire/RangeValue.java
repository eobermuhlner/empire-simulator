package ch.obermuhlner.empire;

import java.util.Random;

public class RangeValue {

	public final double minValue;
	
	public final double maxValue;
	
	public double value;

	public RangeValue(double minValue, double maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		value = (maxValue - minValue) + minValue;
	}
	
	public void randomWalk(Random random) {
		value = MathUtil.clamp(RandomUtil.nextDouble(random, 0.9, 1.1) * value, minValue, maxValue);
	}

	public void setRelativeValue(double relative) {
		value = (maxValue - minValue) * relative + minValue;
	}
	
	@Override
	public String toString() {
		return String.valueOf(value);
	}
	
	public static RangeValue of(Random random, double minValue, double maxValue) {
		double randomMinValue = RandomUtil.nextDouble(random, minValue, maxValue);
		double randomMaxValue = RandomUtil.nextDouble(random, randomMinValue, maxValue);
		return new RangeValue(randomMinValue, randomMaxValue);
	}
}
