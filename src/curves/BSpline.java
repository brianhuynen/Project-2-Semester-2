package curves;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class BSpline extends Curve {

	public BSpline(String name) {
		super(name);
	}

	@Override
	public ArrayList<Point2D> getPlot(int interval) {
		ArrayList<Point2D> plot = new ArrayList<Point2D>();

		for (int i = 0; i < points.size() + ((closed) ? 0 : -1); i++) {
			for (int j = 1; j <= interval; j++) {
				plot.add(calcPoint(i, j / (float) interval));
			}
		}
		
		if (super.isClosed()){
			plot.add(plot.get(0));
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
	private Point2D calcPoint(int i, float t) {
		double px = 0;
		double py = 0;

		for (int j = 0; j < 4; j++) {
			px += basic(j, t) * points.get(getInBounds(i + j)).getX();
			py += basic(j, t) * points.get(getInBounds(i + j)).getY();
		}

		return new Point2D.Double(px, py);
	}

	/**
	 * the basic functions of the Bezier Spline
	 */
	private double basic(int i, float t) {
		if (i == 0)
			return (((-t + 3) * t - 3) * t + 1) / 6;
		else if (i == 1)
			return (((3 * t - 6) * t) * t + 4) / 6;
		else if (i == 2)
			return (((-3 * t + 3) * t + 3) * t + 1) / 6;
		else
			return (t * t * t) / 6;
	}

	/**
	 * method to make sure that a point is inside the domain
	 * 
	 * @param index of a point
	 * @return index of that point inside the domain
	 */
	private int getInBounds(int i) {
		if (i < 0)
			i = points.size() - i;

		return i % points.size();
	}

	@Override
	protected List<Point2D> getConversionPoints() {
		return points;
	}
}