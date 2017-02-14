package curves;

public interface Evaluateable {
	/**
	 * returns the value at x in the specified piece of the following function: sqrt((dx/dt)^2 + (dy/dt)^2). Other algorithms can then integrate it
	 * @param piece the piece of the spline to be used
	 * @param x the x-value in that spline
	 * @return the value of the function at x
	 */
	double evaluateArcLengthFunction(int piece, double x);
}
