package curves;

import java.awt.geom.Point2D;

import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

public class BezierSplineColinear extends BezierSpline {

	double SDIFF = 1;
	
	public BezierSplineColinear(String name) {
		super(name);
	}

	/**
	 * Method to adjust the points
	 * 
	 * @param index
	 *            of the moved point
	 * @param relative
	 *            horizontal movement
	 * @param relative
	 *            vertical movement
	 */
	private void adjustColinear(int i, double dx, double dy) {
		if (i % 3 == 0 && i < points.size() - 1) {
			// change neighbours position relative to this one
			points.get(getInBounds(i - 1)).setLocation(
					points.get(getInBounds(i - 1)).getX() + dx,
					points.get(getInBounds(i - 1)).getY() + dy);
			points.get(getInBounds(i + 1)).setLocation(
					points.get(getInBounds(i + 1)).getX() + dx,
					points.get(getInBounds(i + 1)).getY() + dy);
		} else if (i % 3 == 1 && points.size() > 4) {
			adjust(i, getInBounds(i - 1), getInBounds(i - 2)); // adjust the
																// other control
																// point
		} else if (i % 3 == 2 && points.size() > 4) {
			adjust(i, getInBounds(i + 1), getInBounds(i + 2)); // adjust the
																// other control
																// point
		}
	}

	/**
	 * Method to adjust the opposite control point
	 *
	 * @param c1
	 *            first control point
	 * @param knot
	 *            point
	 * @param c2
	 *            second control point which will be adjusted
	 */
	private void adjust(int c1, int knot, int c2) {

		double ij = distance(points.get(c1).getX(), points.get(c1).getY(),
				points.get(knot).getX(), points.get(knot).getY());
		double jk = distance(points.get(knot).getX(), points.get(knot).getY(),
				points.get(c2).getX(), points.get(c2).getY());
		double r = jk / ij;

		points.get(c2).setLocation(
				points.get(knot).getX() + r
						* (points.get(knot).getX() - points.get(c1).getX()),
				points.get(knot).getY() + r
						* (points.get(knot).getY() - points.get(c1).getY()));
	}

	private Point2D orthogonal(Point2D p, Point2D p2, double length) {
		double dx = p2.getX() - p.getX();
		double dy = p2.getY() - p.getY();
		double dist = Math.sqrt(dx*dx + dy*dy);
		
		return new Point2D.Double(-dy / dist * length, dx / dist * length);
	}

	private double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	/**
	 * add an extra point then adjust the other control points
	 *
	 * @param the
	 *            x of the new point
	 * @param the
	 *            y of the new point
	 * @return the index of the new point
	 */
	protected int add(double x, double y) {
//		System.out.println("Adding more points and stuff");
		
		if (points.isEmpty()) {
			points.add(new Point2D.Double(x, y));
			return points.size() - 1;
		} if (points.size() == 1) {
			Point2D otho = orthogonal(points.get(0), new Point2D.Double(x, y), SDIFF);

			points.add(new Point2D.Double(points.get(0).getX() + otho.getX(),
					points.get(0).getY() + otho.getY()));
			points.add(new Point2D.Double(x + otho.getX(), y + otho.getY()));
			points.add(new Point2D.Double(x, y));
			points.add(new Point2D.Double(x - otho.getX(), y - otho.getY()));
			points.add(new Point2D.Double(points.get(0).getX() - otho.getX(),
					points.get(0).getY() - otho.getY()));			
		} else {
			Point2D otho = orthogonal(points.get(points.size() - 3), new Point2D.Double(x, y), SDIFF);
			
			points.add(points.size() - 1, new Point2D.Double(x + otho.getX(), y + otho.getY()));
			points.add(points.size() - 1, new Point2D.Double(x, y));
			points.add(points.size() - 1, new Point2D.Double(x - otho.getX(), y - otho.getY()));
		}
		
		return points.size() - 3;
	}

	@Override
	protected void setPoint(int index, double x, double y) {
		// save old position to translate control points if needed
		double dx = x - points.get(index).getX();
		double dy = y - points.get(index).getY();
		super.setPoint(index, x, y);
		adjustColinear(index, dx, dy);
	}

	@Override
	protected Point2D removePoint(int index) {
		super.removePoint(index);
		adjustColinear(index, 0, 0);

		// TODO make use of this in some way
		return null;
	}

}
