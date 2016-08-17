package ch.obermuhlner.empire;

public class MathUtil {

	public static double clamp(double value, double min, double max) {
		if (value < min) {
			return min;
		}
		if (value > max) {
			return max;
		}
		return value;
	}

	/**
	 * The smoothstep function returns 0.0 if x is smaller then edge0 and 1.0 if x is larger than edge1.
	 * Otherwise the return value is interpolated between 0.0 and 1.0 using Hermite polynomirals.
	 * 
	 * @param edge0 the lower edge
	 * @param edge1 the upper edge
	 * @param x the value to smooth
	 * @return the smoothed value
	 */
	public static double smoothstep (double edge0, double edge1, double x) {
		x = clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
		return x*x*(3 - 2*x);
	}
	
	public static double smootherstep(double edge0, double edge1, double x) {
	    // Scale, and clamp x to 0..1 range
	    x = clamp((x - edge0)/(edge1 - edge0), 0.0, 1.0);
	    // Evaluate polynomial
	    return x*x*x*(x*(x*6 - 15) + 10);
	}
	
	public static double smoothclamp(double edge0, double edge1, double x) {
		return smoothstep(edge0, edge1, x) * (edge1 - edge0) + edge0;
	}
}
