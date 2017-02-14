package curves;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class PolyLine extends Curve {

    private double length;
	private double area;

	protected PolyLine(Point2D point, String name) {
        super(name);
        super.points.add(point);
        recalcAaA();
    }

    @Override
    protected ArrayList<Point2D> getPlot(int interval) {
        return points;
    }
    
    @Override
    protected void setPoint(int index, double x, double y) {
		super.setPoint(index, x, y);
		recalcAaA();
	} 
    
    public void setClosed(boolean closed) {
		if (closed == true && super.isClosed() == false){//close
			super.points.add(super.points.get(0));
			super.setClosed(closed);
		}
		else if (closed == false && super.isClosed() == true){//open
			super.points.remove(super.numberOfPoints()-1);
			super.setClosed(closed);
		}
		recalcAaA();
	}
    
    @Override
    protected int add(double x, double y) {
    	if (this.isClosed()){
    		this.setClosed(false);
    		this.points.add(new Point2D.Double(x, y));
    		this.setClosed(true);
    	}
    	else {
    		this.points.add(new Point2D.Double(x, y));
    	}
    	recalcAaA();
		return points.size() - 1;
	}
    
    private void recalcAaA() {
		this.area = NumericalApproximation.calcArea(this);
		this.length = NumericalApproximation.calcArcLength(this);
	}
    
    protected double area(int method) {
		if (method != areaAlgorithm){
			areaAlgorithm = method;
			recalcAaA();
		}
		return this.area;
	}
	
	@Override
	protected double length(int method) {
		if (method != arcLengthAlgorithm){
			arcLengthAlgorithm = method;
			recalcAaA();
		}
		return this.length;
	}

	@Override
	protected List<Point2D> getConversionPoints() {
		return (List<Point2D>)this.points;
	}
    
}
