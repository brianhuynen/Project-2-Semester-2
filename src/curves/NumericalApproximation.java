package curves;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class NumericalApproximation {
	
	private static final boolean DEBUG = false;
	
	public static final int SHOELACE_AREA = 1;
	public static final int EXACT_AREA_CUBIC = 2;
	public static final int EXACT_ELLIPSE_AREA = 3;
	public static final int RICHARDSON_EXTRAPOLATION_AREA = 4;
	
	public static final int ROMBERG_ARCLENGTH = 1;
	public static final int SIMPSON_ARCLENGTH = 2;
	public static final int PYTHAGOREAN_ARCLENGTH = 3;
	public static final int RICHARDSON_EXTRAPOLATION_ARCLENGTH = 4;
	public static final int ELLIPSE_ARCLENGTH_EXACT = 5;
	
	private static final int ROMBERG_MAX = 8;
	private static final int SIMPSON_N = 30;
	private static final int RICHARDSON_N = 10;
	private static final int POLYLINE_APPROXIMATION_SUBDIVISIONS = 10;
	
	private static Evaluateable localCurve;

	/**
	 * calculates the area of the curve based solely on the curve as input
	 * @param curve
	 * @return area
	 */
	public static double calcArea(Curve curve){
		if (DEBUG) System.out.println("algorithm: " + curve.areaAlgorithm);
		if (curve.areaAlgorithm == EXACT_AREA_CUBIC){
			if (DEBUG) System.out.println("exact area");
			return Math.abs(exactCubicArea(curve));
		}
		else if (curve.areaAlgorithm == SHOELACE_AREA){
			if (DEBUG)System.out.println("shoelace");
			return Math.abs(shoeLaceArea(curve, POLYLINE_APPROXIMATION_SUBDIVISIONS));
		}
		else if (curve.areaAlgorithm == EXACT_ELLIPSE_AREA){
			if (DEBUG)System.out.println("ellipse area");
			return Math.abs(exactEllipseArea(curve));
		}
		else if (curve.areaAlgorithm == RICHARDSON_EXTRAPOLATION_AREA){
			if (DEBUG)System.out.println("richardson area");
			return Math.abs(richardsonArea(curve));
		}
		return Double.NaN;
	}
	
	/**
	 * calculates the arc-length of the curve, based solely on the curve as input
	 * @param curve
	 * @return arc-length
	 */
	public static double calcArcLength(Curve curve){
		if (curve.arcLengthAlgorithm == ROMBERG_ARCLENGTH){
			if (DEBUG)System.out.println("romberg length using exact formula");
			return Math.abs(rombergArcLength(curve));
		}
		else if (curve.arcLengthAlgorithm == SIMPSON_ARCLENGTH){
			if (DEBUG)System.out.println("simpson length using exact formula");
			return simpsonArcLength(curve);
		}
		else if (curve.arcLengthAlgorithm == PYTHAGOREAN_ARCLENGTH){
			if (DEBUG)System.out.println("pythoagoras");
			return pythagoreanLength(curve, POLYLINE_APPROXIMATION_SUBDIVISIONS);
		}
		else if (curve.arcLengthAlgorithm == RICHARDSON_EXTRAPOLATION_ARCLENGTH){
			if (DEBUG)System.out.println("richardson length");
			return richardsonLength(curve);
		}
		else if (curve.arcLengthAlgorithm == ELLIPSE_ARCLENGTH_EXACT){
			if (DEBUG)System.out.println("ellipse length");
			return exactEllipseLength(curve);
		}
		return Double.NaN;
	}
	
	/**
	 * specific method for the "evaluateable" functions, hence the arc-length equation can be evaluated. Uses romberg integration  
	 * @param curve
	 * @return the area
	 */
	private static double rombergArcLength(Curve curve) {
		localCurve = (Evaluateable)curve;
		double tmpLength = 0;
		
		for (int i = 0; i < curve.points.size()-1; i++){
			tmpLength += rombergEvaluation(i, 0, 1, ROMBERG_MAX);
		}
		if (curve.isClosed()){
			tmpLength += rombergEvaluation(curve.points.size()-1, 0, 1, ROMBERG_MAX);
		}
		return tmpLength;
	}
	
	/**
	 * this method performs the romberg integration for arc-length on each actual piece of the spline
	 * @param piece is the index of the piece to be used
	 * @param lower bound
	 * @param higher higher bound
	 * @param n romberg limit, such as Rn,n
	 * @return arc-length of piece
	 */
	private static double rombergEvaluation(int piece, double lower, double higher, int n){
		
		double[][] rombergMatrix = new double[n][n];
		
		for (int i = 0; i < n; i++){
			rombergMatrix[i][0] = trapezoidArcLength(piece, lower, higher, (int)Math.pow(2, i));
		}
		double pow;
		for (int i = 1; i < n; i++){
			for (int k = i; k < n; k++){
				pow = Math.pow(4, i);
				rombergMatrix[k][i] = (pow/(pow-1))*rombergMatrix[k][i-1] - (1/(pow-1))*rombergMatrix[k-1][i-1];
			}
		}
		return rombergMatrix[n-1][n-1];
	}
	
	/**
	 * this method actually evaluates the arc-length function and uses the trapezoidal method with n sub-divisions
	 * @param piece index
	 * @param lower bound
	 * @param higher bound
	 * @param n number of sub-divisions
	 * @return individual arc-length with n sub-divisions
	 */
	private static double trapezoidArcLength(int piece, double lower, double higher, int n){
		double h = (higher-lower) / n;
		double sum = 0;
		
		sum += 0.5 * localCurve.evaluateArcLengthFunction(piece, lower);
		sum += 0.5 * localCurve.evaluateArcLengthFunction(piece, higher);
		for (int i = 1; i <= (n-1); i++){
			sum += localCurve.evaluateArcLengthFunction(piece, lower + (h*i));
		}
		return sum*h;
	}
	
	/**
	 * method utilizing richardson extrapolation to improve the poly-line approximation of the area
	 * @param curve
	 * @return area
	 */
	private static double richardsonArea(Curve curve) {
		double[][] richardsonMatrix = new double[RICHARDSON_N][RICHARDSON_N];
		
		for (int i = 0; i < RICHARDSON_N; i++){
			richardsonMatrix[i][0] = polyLineArea(curve, (int)Math.pow(2, i));
		}
		
		for (int i = 1; i < RICHARDSON_N; i++){
			for (int k = i; k < RICHARDSON_N; k++){
				richardsonMatrix[k][i] = richardsonMatrix[k][i-1] + ((richardsonMatrix[k][i-1]-richardsonMatrix[k-1][i-1])/(Math.pow(2, i)-1));
			}
		}
		
		return richardsonMatrix[RICHARDSON_N-1][RICHARDSON_N-1];
	}
	
	/**
	 * calculates the polyline-approximation of the area
	 * @param curve
	 * @param n subdivisions
	 * @return polyline area
	 */
	private static double polyLineArea(Curve curve, int n) {
		ArrayList<Point2D> plot = curve.getPlot(n);
		double area = 0;
		for (int i = 0; i < plot.size()-1; i++){
			area += pointWiseArea(plot.get(i), plot.get(i+1));
		}
		return Math.abs(area);
	}

	/**
	 *  calculates the area between two points, considering the four possibilities of dx/dt and dy/dt being positive and negative and then adding or subtracting area respectively
	 * @param point1
	 * @param point2
	 * @return area-slice
	 */
	private static double pointWiseArea(Point2D point1, Point2D point2) {
		double subArea = 0;
		
		double x1 = point1.getX();
		double x2 = point2.getX();
		double y1 = point1.getY();
		double y2 = point2.getY();
		
		double xdiff = x2-x1;
		double midPointY = (y1+y2)/2;
		
		
		if (x1 == x2){ //the x-values are the same, hence the area = 0
			return subArea;
		}
		else if (Math.signum(y1) == Math.signum(y2)){ //both points are either above or below the x-axis
			if (Math.signum(y1) == 1){
				subArea = xdiff*midPointY;
			}
			else if (Math.signum(y1) == -1){
				subArea = xdiff*midPointY;
			}
		}
		else if (y1 == 0 || y2 == 0){ //one of the points is on the x-axis
			if (Math.signum(y1) == 1){
				subArea = xdiff*midPointY;
			}
			else if (Math.signum(y1) == -1){
				subArea = xdiff*midPointY;
			}
			else if (Math.signum(y2) == 1){
				subArea = xdiff*midPointY;
			}
			else if (Math.signum(y2) == -1){
				subArea = xdiff*midPointY;
			}
		}
		else { // one point is above, and one point is below the x-axis
			double m = (y2-y1)/xdiff;
			double c = y1 - x1*m;
			double x3 = -c/m;
			
			double xdiff1 = x3-x1;
			double midPointY1 = (y1)/2;
			
			double xdiff2 = x2-x3;
			double midPointY2 = (y2)/2;
			
			if (Math.signum(y1) == 1) {
				subArea += xdiff1*midPointY1;
				subArea += -xdiff2*midPointY2;
			}
			else {
				subArea += -xdiff1*midPointY1;
				subArea += xdiff2*midPointY2;
			}
		}
		
		return subArea;
	}

	/**
	 * using richardson extrapolation to improve the poly-line approximation of the arc-length 
	 * @param curve
	 * @return length
	 */
	private static double richardsonLength(Curve curve) {
		double[][] richardsonMatrix = new double[RICHARDSON_N][RICHARDSON_N];
		
		for (int i = 0; i < RICHARDSON_N; i++){
			richardsonMatrix[i][0] = pythagoreanLength(curve, (int)Math.pow(2, i));
		}
		
		for (int i = 1; i < RICHARDSON_N; i++){
			for (int k = i; k < RICHARDSON_N; k++){
				richardsonMatrix[k][i] = richardsonMatrix[k][i-1] + ((richardsonMatrix[k][i-1]-richardsonMatrix[k-1][i-1])/(Math.pow(2, i)-1));
			}
		}
		
		return richardsonMatrix[RICHARDSON_N-1][RICHARDSON_N-1];
	}
	
	/**
	 * similar to the romberg integration, simpson length uses the actual arc-length function of the curve
	 * @param curve
	 * @return arc-length
	 */
	private static double simpsonArcLength(Curve curve) {
		localCurve = (Evaluateable)curve;
		double tmpLength = 0;
		
		for (int i = 0; i < curve.points.size()-1; i++){
			tmpLength += simpsonEvaluation(i, 0, 1, SIMPSON_N);
		}
		if (curve.isClosed()){
			tmpLength += simpsonEvaluation(curve.points.size()-1, 0, 1, SIMPSON_N);
		}
		return tmpLength;
	}
	
	/**
	 * evaluates a piece of the spline using simpson's integration
	 * @param piece the index of the piece
	 * @param lower lower bound
	 * @param higher upper bound
	 * @param n number of sub-divisions
	 * @return the piece-wise arc-length
	 */
	private static double simpsonEvaluation(int piece, double lower, double higher, int n){
		double h = (higher-lower) / n;
		double sum = 0;
		
		sum += localCurve.evaluateArcLengthFunction(piece, lower);
		sum += localCurve.evaluateArcLengthFunction(piece, higher);
		
		for (int i = 1; i < n; i+=2){
			sum += 4*localCurve.evaluateArcLengthFunction(piece, (lower + (i * h)));
		}
		
		for (int i = 2; i < n; i+=2){
			sum += 2*localCurve.evaluateArcLengthFunction(piece, (lower + (i * h)));
		}
		
		return (sum * (h/3));
	}
	/**
	 * evaluates the exact area of the cubic spline using the derivatives which can be calculated using the polynomial
	 * @param curve
	 * @return area
	 */
	private static double exactCubicArea(Curve curve){
		CubicSpline local = (CubicSpline)curve;
		
		int cutoff = curve.numberOfPoints()-1;
		if (curve.isClosed()){
			cutoff+= 1;
		}
		
		double finalArea = 0;
		
		for (int i = 0; i < cutoff; i++){
			finalArea += local.calcSubArea(i, 0.0, 1.0);
		}
		
		return finalArea;
	}
	
	/**
	 * shoe-lace algorithm for the area using a poly-line
	 * @param curve
	 * @return area
	 */
	private static  double shoeLaceArea(Curve curve, int subDivisions) {
		double area = 0;

		if (!curve.isClosed()){
			return Double.NEGATIVE_INFINITY;
		}
			
		ArrayList<Point2D> listOfPoints = curve.getPlot(subDivisions);
		for (int i = 0; i < listOfPoints.size() - 1; i++) {
			area += listOfPoints.get(i).getX() * listOfPoints.get(i + 1).getY()
					- listOfPoints.get(i + 1).getX()
					* listOfPoints.get(i).getY();
		}
		area += listOfPoints.get(listOfPoints.size() - 1).getX()
				* listOfPoints.get(0).getY() - listOfPoints.get(0).getX()
				* listOfPoints.get(listOfPoints.size() - 1).getY();
		
		return Math.abs(area / 2);
	}
	
	/**
	 * using a poly-line approximation to measure the arc-length
	 * @param curve
	 * @param subDivisions
	 * @return arc-length
	 */
	private static double pythagoreanLength(Curve curve, int subDivisions) {
		double length = 0;
		
		ArrayList<Point2D> listOfPoints = curve.getPlot(subDivisions);
		
		for (int i = 0; i < listOfPoints.size()-1; i++){
			double x = Math.pow((listOfPoints.get(i+1).getX()-listOfPoints.get(i).getX()), 2);
			double y = Math.pow((listOfPoints.get(i+1).getY()-listOfPoints.get(i).getY()), 2);
			length += Math.sqrt(x+y);
		}
		
		return length;
	}
	
	/**
	 * specific method approximating the arc-length of an ellipse
	 * @param curve
	 * @return arc-length
	 */
	private static double exactEllipseLength(Curve curve){
		Ellipse local = (Ellipse)curve;
		
		//Length of an ellipse is approximated using a formula by Ramanujan
		double vR = local.calc_vR();
		double hR = local.calc_hR();
		double x = (Math.pow((hR - vR), 2))/(Math.pow((vR + hR), 2));
		return Math.PI*(vR + hR)*(1 + (3*x)/(10 + Math.sqrt(4 - 3*x)));
	}
	
	/**
	 * specific method for the area of an ellipse
	 * @param curve
	 * @return area
	 */
	private static double exactEllipseArea(Curve curve){
		Ellipse local = (Ellipse)curve;
		return Math.PI*local.calc_hR()*local.calc_vR();
	}
}
