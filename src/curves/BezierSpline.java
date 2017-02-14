package curves;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class BezierSpline extends Curve {
	
	public BezierSpline(String name) {
		super(name);
	}

	@Override
	public ArrayList<Point2D> getPlot(int interval) {
		ArrayList<Point2D> plot = new ArrayList<Point2D>();

		plot.add(points.get(0));

		for (int i = 0; i < points.size() - ((closed) ? 2 : 3); i += 3) {
			for (int j = 1; j <= interval; j++) {
				plot.add(calculatePoint(i, (double) (j / (float)interval)));
			}
		}

		return plot;
	}

	/**
	 * get point at line piece i, at interval t
	 *
	 * @param line piece
	 * @param interval
	 * @return a point on the curve
	 */
	protected Point2D calculatePoint(int i, double t) {
		double px = 0;
		double py = 0;

		for (int j = 0; j <= 3; j++) {
			px += basic(j, t) * points.get(getInBounds(i + j)).getX();
			py += basic(j, t) * points.get(getInBounds(i + j)).getY();
		}

		return new Point2D.Double(px, py);
	}

	/**
	 * the basic functions of the Bezier Spline
	 * @param i the 
	 * @param t
	 * @return
	 */
	protected double basic(int i, double t) {
		if (i == 0)
			return (1 - t) * (1 - t) * (1 - t);
		else if (i == 1)
			return 3 * t * (1 - t) * (1 - t);
		else if (i == 2)
			return 3 * t * t * (1 - t);
		else
			return t * t * t;
	}

	/**
	 * method to make sure that a point is inside the domain
	 * 
	 * @param index of a point
	 * @return index of that point inside the domain
	 */
	int getInBounds(int i) {
		if (points.size() == 0)
			return 0;

		if (i < 0)
			i = points.size() + i;

		return i % points.size();
	}

	@Override
	protected List<Point2D> getConversionPoints() {
		ArrayList<Point2D> list = new ArrayList<Point2D>();

		for (int i = 0; i < points.size(); i += 3) {
			list.add(points.get(i));
		}

		return list;
	}
	
	@Override
	protected double length(int METHOD){
		arcLengthAlgorithm = METHOD;
		return NumericalApproximation.calcArcLength(this);
	}
}
