package curves;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Curve {

	protected final int POLYLINE_ID = 0;
	protected final int CUBIC_SPLINE_ID = 1;
	protected final int BEZIER_CURVE_ID = 2;
	protected final int BEZIER_SPLINE_ID = 3;
	protected final int COLINEAR_BEZIER_ID = 4;
	protected final int B_SPLINE_ID = 5;
	
	protected ArrayList<Integer> curveID = new ArrayList<Integer>(Arrays.asList(new Integer[] {0,1,2,3,4,5}));
	
	protected ArrayList<Point2D> points;
	protected boolean closed;
	protected String name;
	protected ArrayList<Integer> areaAlgorithms;
	protected ArrayList<Integer> arcLengthAlgorithms;
	protected int areaAlgorithm;
	protected int arcLengthAlgorithm;

	public Curve(String name) {
		this.name = name;
		closed = false;
		points = new ArrayList<Point2D>();
		areaAlgorithms = new ArrayList<Integer>();
		arcLengthAlgorithms = new ArrayList<Integer>();
		
		areaAlgorithms.add(NumericalApproximation.RICHARDSON_EXTRAPOLATION_AREA);
		areaAlgorithms.add(NumericalApproximation.SHOELACE_AREA);
		
		arcLengthAlgorithms.add(NumericalApproximation.PYTHAGOREAN_ARCLENGTH);
		arcLengthAlgorithms.add(NumericalApproximation.RICHARDSON_EXTRAPOLATION_ARCLENGTH);
	}
	

	/**
	 * method which calculates or returns the plotting coordinates such that the
	 * UI can draw all curves. NOTE THE DEFINITION OF THE SUB-POINTS!
	 * 
	 * @param subPoints
	 *            this number signifies the number of plotting points in-between
	 *            each pair of control-points. The larger the number, the more
	 *            fine-grained the curve plot will be.
	 * @return
	 */
	protected abstract ArrayList<Point2D> getPlot(int subPoints);

	/**
	 * returns the length of the curve
	 * @param the method used, selected from NumericalApproximation Class
	 * @return length
	 */
	protected double length(int METHOD) {
		return Double.NaN;
	}

	/**
	 * returns the area of the curve
	 * @param the method used, selected from NumericalApproximation Class
	 * @return area
	 */
	protected double area(int METHOD) {
		return Double.NaN;
	}

	/**
	 * validation if the curve is closed
	 * @return
	 */
	protected boolean isClosed() {
		return closed;
	}

	/**
	 * changing the closed-state of the curve to close
	 * @param closed
	 */
	protected void setClosed(boolean closed) {
		this.closed = closed;
	}

	/**
	 * setting the name
	 * @param name
	 */
	protected void setName(String name) {
		this.name = name;
	}

	/**
	 * retrieving the name
	 * @return name
	 */
	protected String getName() {
		return name;
	}

	/**
	 * adding a new point to the end of the curve
	 * @param x-coordinate
	 * @param y-coordinate
	 * @return
	 */
	protected int add(double x, double y) {
		this.points.add(new Point2D.Double(x, y));
		return points.size() - 1;
	}

	/**
	 * retrieves the number of control points the curve contains
	 * @return
	 */
	protected int numberOfPoints() {
		return points.size();
	}
	
	/**
	 * fetches the control points
	 * @return
	 */
	protected List<Point2D> getControlPoints() {
		return (List<Point2D>) points;
	}

	/**
	 * removes a point from the control-point set
	 * @param index of the to-be removed point
	 * @return the point which was just removed
	 */
	protected Point2D removePoint(int index) {
		return points.remove(index);
	}

	/**
	 * setting the coordinates of a point
	 * @param index of the point
	 * @param x coordinate
	 * @param y coordinate
	 */
	protected void setPoint(int index, double x, double y) {
		points.get(index).setLocation(x, y);
	}
	
	/**
	 * method to return the conversion points, which are not identical to the control-points in all cases
	 * @return
	 */
	protected abstract List<Point2D> getConversionPoints();
}
