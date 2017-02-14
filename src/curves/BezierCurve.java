package curves;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class BezierCurve extends Curve {
		
	public BezierCurve(Point2D point, String name){
		super(name);
		add(point.getX(), point.getY());
	}

	public double length(int METHOD) {
		arcLengthAlgorithm = METHOD;
		return NumericalApproximation.calcArcLength(this);
	}

	public double area(int METHOD) {
		areaAlgorithm = METHOD;
		return NumericalApproximation.calcArea(this);
	}

	public ArrayList<Point2D> getPlot(int subPoints) {
		ArrayList<Point2D> plottingPoints = new ArrayList<Point2D>();
		
		double tInterval = 1 / ((double) ((super.points.size()-1) * subPoints));
		
		for (double d = 0; d < 1; d+=tInterval){
			plottingPoints.add(deCasteljauSAlgorithm(super.points, d));
		}
		plottingPoints.add(deCasteljauSAlgorithm(super.points, 1));
		
		return plottingPoints;
	}
	
	/**
	 * Method which, without polynomial mathematics, calculates the point at a given t. Can be optimised to be seperate class (static), memory improvements possible. 
	 * @param originalPoints original points of the Bezier Curve
	 * @param ratio t at which the point shall be calculated
	 * @return the point for given t
	 */
	public static Point2D deCasteljauSAlgorithm(ArrayList<Point2D> originalPoints, double ratio){
		ArrayList<Point2D> working = new ArrayList<Point2D>();
		
		if (originalPoints.size() > 1){
			//calculates the first step and copies from the originalPoints to the working points set
			for (int i = 0; i < originalPoints.size()-1; i++){
				Double newX = originalPoints.get(i).getX() + ((originalPoints.get(i+1).getX() - originalPoints.get(i).getX()) * ratio);
				Double newY = originalPoints.get(i).getY() + ((originalPoints.get(i+1).getY() - originalPoints.get(i).getY()) * ratio);
				working.add(new Point2D.Double(newX, newY));
			}
			while (working.size() > 1){
				for (int i = 0; i < working.size()-1; i++){
					Double newX = working.get(i).getX() + ((working.get(i+1).getX() - working.get(i).getX()) * ratio);
					Double newY = working.get(i).getY() + ((working.get(i+1).getY() - working.get(i).getY()) * ratio);
					working.get(i).setLocation(newX, newY);;
				}
				working.remove(working.size()-1);
			}
			return working.get(0);
		}
		else {
			return originalPoints.get(0);
		}
		
	}
	
	public String toString(){
		String string = "Bezier: ";
		for (int i = 0; i < super.points.size(); i++){
			string = string + "[" + super.points.get(i).getX() +","+ super.points.get(i).getY() + "] " ;
		}
		return string;
	}
	
	//Coen, you make sure this returns the right points, that's YOUR responsibility. 
	@Override
	protected List<Point2D> getConversionPoints() {
		return (List<Point2D>)this.points;
	}
}
