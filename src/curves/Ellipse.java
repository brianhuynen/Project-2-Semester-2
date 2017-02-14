package curves;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Ellipse extends Curve{

	private int type;
	private ArrayList <Point2D> plot;
	private double a;
	private double c;
	private int currentSubPoints;
	
	/**
	 * The constructor which creates the ellipse
	 * @param a		The a variable in the formula x(t) = a cos(t)
	 * @param c		The c variable in the formula y(t) = c sin(t)
	 * @param name	The name of the curve
	 * @param type	int that indicates the type of the curve
	 */
	public Ellipse(double a, double c, String name, int type) {
		super(name);
		this.type = type;
		this.a = a;
		this.c = c;
		
		if( a >= 10 || c >= 10){
			currentSubPoints = 10000;
		}
		else{
		currentSubPoints = 1000;
		}
		calcPlot(currentSubPoints);
		
		areaAlgorithms.add(NumericalApproximation.EXACT_ELLIPSE_AREA);
		arcLengthAlgorithms.add(NumericalApproximation.ELLIPSE_ARCLENGTH_EXACT);
		super.setClosed(true);
	}

	/**
	 * Calculates the points for the plot Arraylist
	 * @param subPoints		The amount of subpoints creates the interval on which
	 * the points should be created
	 * It calls calcX and calcY to create a new Point2D in the arraylist plot
	 */
	private void calcPlot(int subPoints){
		double interval = (2*Math.PI)/((double)((double)4*(double)subPoints));
//		System.out.println("subpoint: " + subPoints + ", t interval: " + interval);
		plot = null;
		plot = new ArrayList <Point2D>();
		for(double t = 0; t < 2*Math.PI; t += interval){
			plot.add(new Point2D.Double(calcX(t), calcY(t)));
		}
		plot.add(new Point2D.Double(calcX(0), calcY(0)));
		
	}
	
	/**
	 * This calculates the x(t) with the formula a*cos(b*t)
	 * @param t		The t that needs to be calculated
	 * @return		returns the x(t) on that point
	 */
	private double calcX(double t){
		return (a * Math.cos(t));
	}
	
	/**
	 * This calculates the y(t) with the formula x*sin(d*t)
	 * @param t		The t that needs to be calculated
	 * @return		returns the y(t) on that point
	 */
	private double calcY(double t){
		return (c * Math.sin(t));
	}
	
	/**
	 * @return	Returns the horizontal radius
	 */
	double calc_hR(){
		double hR = c;
		return hR;
	}
	
	/**
	 * @return	Returns the vertical radius
	 */
	double calc_vR(){
		double vR = a;
		return vR;
	}
	
	@Override
    protected double area(int method) {
		areaAlgorithm = method;
		return NumericalApproximation.calcArea(this);
	}
	
	@Override
	protected double length(int method) {
		arcLengthAlgorithm = method;
		return NumericalApproximation.calcArcLength(this);
	}
	@Override
	protected ArrayList<Point2D> getPlot(int subPoints) {
		if (currentSubPoints == subPoints){
			return plot;
		}
		else {
			currentSubPoints = subPoints;
			calcPlot(subPoints);
			return plot;
		}
	}
	
	@Override
	protected List<Point2D> getConversionPoints() {
		return null;
	}

}
