package curves;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class SemiCircle extends CubicSpline {

	private double radius;
	private double intervals;
	private final double CONSTANT = 2.2553244257995195;
	
	/**
	 * Makes a semi circle
	 * @param (optional) is because of the inheritance
	 * @param name of the curve 
	 * @param type of cubic spline
	 */
	public SemiCircle(Point2D point, String name, int type) {
		super(point, name, type);
		points.clear();
		
		radius = 1;
		intervals = 10;
		double delta = radius * 2 / intervals;
		
		for (int i = 0; i <= intervals; i++)
			points.add(new Point2D.Double(-radius + delta * i, circleFunction(-radius + delta * i)));
		
		for (Point2D p : points) {
			System.out.println(p);
		}
		
		update();
	}

	public SemiCircle(String name, double targetLength){
		super(new Point2D.Double(), name, 0);
		points.clear();
		
		radius = targetLength / Math.PI;
		intervals = 50;
		
		recalculate();
		
		System.out.println("ratio: " + (Math.sqrt(area(NumericalApproximation.EXACT_AREA_CUBIC))/length(NumericalApproximation.RICHARDSON_EXTRAPOLATION_ARCLENGTH)));
		System.out.println("length: " + length(NumericalApproximation.RICHARDSON_EXTRAPOLATION_ARCLENGTH));
	}
	
	public void recalculate() {
		double delta = radius * 2 / intervals;
		
		for (int i = 0; i <= intervals; i++)
			points.add(new Point2D.Double(-radius + delta * i, circleFunction(-radius + delta * i)));
		
		update();
	}
	
	public void setPoints(int points) {
		this.intervals = points - 1;
	}
	
	public void setInvervals(int intervals) {
		this.intervals = intervals;
	}
	
	/**
	 * Basic circle
	 * @param the x for the basic circle function
	 * @return the y for the basic circle function
	 */
	private double circleFunction(double x) {
		return -Math.sqrt(radius*radius - x*x);
	}
}
