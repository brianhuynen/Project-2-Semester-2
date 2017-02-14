package curves;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class CubicSpline extends Curve implements Evaluateable {
	public static final int NATURAL_SPLINE = 1;
	public static final int CLOSED_SPLINE = 4;
	
	private static final double EPSILON = 1e-10;
	
	private double[][] Xcoefficients;
	private double[][] Ycoefficients;
	private double[][] dXcoefficients;
	private double[][] dYcoefficients;
	private double[][] areaCoefficients;
	
	private int type;
	private ArrayList<Point2D> plot;
	private int subDivisions;
	private double area;
	private double length;

	/**
	 * constructor with a name, starting point and type
	 * @param point
	 * @param name
	 * @param type
	 */
	public CubicSpline(Point2D point, String name, int type){
		super(name);
		add(point.getX(), point.getY());
		this.type = type;
		subDivisions = 1;
		update();
		
		areaAlgorithms.add(NumericalApproximation.EXACT_AREA_CUBIC);
		
		arcLengthAlgorithms.add(NumericalApproximation.ROMBERG_ARCLENGTH);
		arcLengthAlgorithms.add(NumericalApproximation.SIMPSON_ARCLENGTH);
	}
	
	@Override
	protected Point2D removePoint(int index) {
    	Point2D returnPoint = super.removePoint(index);
    	update();
    	return returnPoint;
    }
	
	/**
	 * updates all internal coefficients and calculations including area and arclength
	 */
	protected void update() {
		calcCoefficients();
		calcPlot();
		calcDerivatives();
		recalcAaA();
	}
	
	@Override
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
	
	/**
	 * recalculates only the area and arclength
	 */
	private void recalcAaA(){
		calcAreaCoefs();
		this.area = NumericalApproximation.calcArea(this);
		this.length = NumericalApproximation.calcArcLength(this);
	}
	
    @Override
	protected void setPoint(int index, double x, double y) {
		super.setPoint(index, x, y);
		update();
	}
	
    @Override
	public void setClosed(boolean closed) {
		if (closed == true && super.isClosed() == false){
			type = CLOSED_SPLINE;
			super.setClosed(closed);
		}
		else if (closed == false && super.isClosed() == true){
			type = NATURAL_SPLINE;
			super.setClosed(closed);
		}
		update();
	}
	
    /**
     * calculates the plot. This is based on a local variable subDivisions. The plot is calculated using the coefficients of the spline
     */
	private void calcPlot() {
		ArrayList<Point2D> plottingPoints = new ArrayList<Point2D>();
		
		double tInterval = (1 / (double)subDivisions);
		
		int cutoff = Xcoefficients.length-1;
		if (type == CLOSED_SPLINE){
			cutoff+= 1;
		}
		
		for (int i = 0; i < cutoff; i++){
			for (double k = 0.0; k < 1; k+=tInterval){
				plottingPoints.add(new Point2D.Double((Xcoefficients[i][0] + k*(Xcoefficients[i][1] + k*(Xcoefficients[i][2] + Xcoefficients[i][3] * k))),
						(Ycoefficients[i][0] + k*(Ycoefficients[i][1] + k*(Ycoefficients[i][2] + Ycoefficients[i][3] * k)))));
			}
		}
		
		if (type != CLOSED_SPLINE){
			plottingPoints.add(super.points.get(super.points.size()-1));
		}
		else {
			plottingPoints.add(super.points.get(0));
		}
		
		this.plot = plottingPoints;
	}

	@Override
	public ArrayList<Point2D> getPlot(int subPoints) {
		if (subPoints == subDivisions){
			return plot;
		}
		else {
			subDivisions = subPoints;
			calcPlot();
			return plot;
		}
	}
	
	@Override
	public int add(double x, double y){
		int i = super.add(x, y);
		update();
		return i;
	}
	
	/**
	 * this method calculates the actual coefficients of the spline and stores them in a 2D Double array. The c-coefficients are found using a matrix and gaussian elimination, the remainder numerically. 
	 */
	private void calcCoefficients(){
		double[][] CMatrixX = new double[super.points.size()][super.points.size()];
		double[][] CMatrixY = new double[super.points.size()][super.points.size()];
		double[] XvectorK = new double[super.points.size()];
		double[] YvectorK = new double[super.points.size()];
		
		double[] XvectorC = new double[super.points.size()];
		double[] YvectorC = new double[super.points.size()];
		
		//Create Vector Solution for C Matrix
		for (int i = 1; i < XvectorK.length-1; i++){
			XvectorK[i] = 3 * (super.points.get(i-1).getX() - (2*super.points.get(i).getX()) + super.points.get(i+1).getX());
			YvectorK[i] = 3 * (super.points.get(i-1).getY() - (2*super.points.get(i).getY()) + super.points.get(i+1).getY());
		}
		
		//Create C coefficient matrix 
		for (int i = 1; i < CMatrixX.length-1; i++){
			if ( i == 0){
				CMatrixX[i][i] = (double) 1;
				
				CMatrixY[i][i] = (double) 1;
			}
			else if (i == CMatrixX.length-1){
				CMatrixX[i][i] = (double) 1;
				
				CMatrixY[i][i] = (double) 1;
			}
			else{
				CMatrixX[i][i-1] = (double) 1;
				CMatrixX[i][i] = (double) 4;
				CMatrixX[i][i+1] = (double) 1;
				
				CMatrixY[i][i-1] = (double) 1;
				CMatrixY[i][i] = (double) 4;
				CMatrixY[i][i+1] = (double) 1;
			}
		}
		
		//adjust C coefficient matrix for each type of spline, currently only natural
		CMatrixX[0][0] = (double) 1;
		CMatrixY[0][0] = (double) 1;
		CMatrixX[CMatrixX.length -1][CMatrixX.length -1] = (double) 1;
		CMatrixY[CMatrixX.length -1][CMatrixX.length -1] = (double) 1;
		
		
		//changing Matrix and Vector for closed splines
		if (super.points.size() > 2 && type == CLOSED_SPLINE){
			CMatrixX[0][0] = 4;
			CMatrixX[0][1] = 1;
			CMatrixX[0][CMatrixX[0].length-1] = 1;
			
			CMatrixX[CMatrixX.length-1][CMatrixX[0].length-1] = 4;
			CMatrixX[CMatrixX.length-1][CMatrixX[0].length-2] = 1;
			CMatrixX[CMatrixX.length-1][0] = 1;
			
			CMatrixY[0][0] = 4;
			CMatrixY[0][1] = 1;
			CMatrixY[0][CMatrixY[0].length-1] = 1;
			
			CMatrixY[CMatrixY.length-1][CMatrixY[0].length-1] = 4;
			CMatrixY[CMatrixY.length-1][CMatrixY[0].length-2] = 1;
			CMatrixY[CMatrixY.length-1][0] = 1;
			
			XvectorK[0] = 3 * (super.points.get(XvectorK.length-1).getX() - (2*super.points.get(0).getX()) + super.points.get(1).getX());
			YvectorK[0] = 3 * (super.points.get(YvectorK.length-1).getY() - (2*super.points.get(0).getY()) + super.points.get(1).getY());
			
			XvectorK[XvectorK.length-1] = 3 * (super.points.get(XvectorK.length-2).getX() - (2*super.points.get(XvectorK.length-1).getX()) + super.points.get(0).getX());
			YvectorK[YvectorK.length-1] = 3 * (super.points.get(YvectorK.length-2).getY() - (2*super.points.get(YvectorK.length-1).getY()) + super.points.get(0).getY());
		}
		
		
		//Gaussian elimination to find c coefficients
//		if (XvectorK.length > 2){
//			XvectorC = gaussianElimination(CMatrixX, XvectorK);
//			YvectorC = gaussianElimination(CMatrixY, YvectorK);
//		}
		if (XvectorK.length > 2){
			XvectorC = lsolve(CMatrixX, XvectorK);
			YvectorC = lsolve(CMatrixY, YvectorK);
		}
		else {
			XvectorC = XvectorK;
			YvectorC = YvectorK;
		}
		
		Xcoefficients = new double[super.points.size()][4];
		Ycoefficients = new double[super.points.size()][4];
		
		//copies c coefficients and a coefficients into matrix
		for (int i = 0; i < Xcoefficients.length; i++){
			Xcoefficients[i][2] = XvectorC[i];
			Xcoefficients[i][0] = super.points.get(i).getX();
			
			Ycoefficients[i][2] = YvectorC[i];
			Ycoefficients[i][0] = super.points.get(i).getY();
		}
		
		//calculates b coefficients
		for (int i = 0; i < Xcoefficients.length-1; i++){
			Xcoefficients[i][1] = (Xcoefficients[i+1][0] - Xcoefficients[i][0]) - (((2*Xcoefficients[i][2]) + Xcoefficients[i+1][2])/3);
			
			Ycoefficients[i][1] = (Ycoefficients[i+1][0] - Ycoefficients[i][0]) - (((2*Ycoefficients[i][2]) + Ycoefficients[i+1][2])/3);
		}
		
		//calculates d coefficients
		for (int i = 0; i < Xcoefficients.length-1; i++){
			Xcoefficients[i][3] = ((Xcoefficients[i+1][2] - Xcoefficients[i][2])/3);
			
			Ycoefficients[i][3] = ((Ycoefficients[i+1][2] - Ycoefficients[i][2])/3);
		}
		
		if (super.points.size() > 2 && type == CLOSED_SPLINE){
			//b coefficients:
			Xcoefficients[Xcoefficients.length-1][1] = (Xcoefficients[0][0] - Xcoefficients[Xcoefficients.length-1][0]) - (((2*Xcoefficients[Xcoefficients.length-1][2]) + Xcoefficients[0][2])/3);
			Ycoefficients[Ycoefficients.length-1][1] = (Ycoefficients[0][0] - Ycoefficients[Ycoefficients.length-1][0]) - (((2*Ycoefficients[Ycoefficients.length-1][2]) + Ycoefficients[0][2])/3);
			
			//d coefficients
			Xcoefficients[Xcoefficients.length-1][3] = ((Xcoefficients[0][2] - Xcoefficients[Xcoefficients.length-1][2])/3);
			Ycoefficients[Ycoefficients.length-1][3] = ((Ycoefficients[0][2] - Ycoefficients[Ycoefficients.length-1][2])/3);
		}
		
	}
	
	/**
	 * Method used to calculate the numerical value of the area under a piece of the cubic spline. This method uses integrated form of the function y(t)*x'(t)
	 * @param i the index of the piece which is to be used for the integration
	 * @param a lower bound
	 * @param b upper bound
	 * @return area of defined sub-section of spline
	 */
	protected double calcSubArea(int i, double a, double b){
		double upper = ((((((areaCoefficients[i][5] * b + areaCoefficients[i][4]) * b + areaCoefficients[i][3])* b + areaCoefficients[i][2]) * b + areaCoefficients[i][1]) * b + areaCoefficients[i][0]) * b);
		double lower = ((((((areaCoefficients[i][5] * a + areaCoefficients[i][4]) * a + areaCoefficients[i][3])* a + areaCoefficients[i][2]) * a + areaCoefficients[i][1]) * a + areaCoefficients[i][0]) * a);
		return upper-lower;
	}
	
	/**
	 * this method calculates the coefficients of the polynomial representing the area which is the formula integral(y(t)*x'(t)dt). this is only possible because the x and y functions are polynomials and easily stored
	 */
	private void calcAreaCoefs(){
		double[][] unintegratedCoefficients = new double[dYcoefficients.length][6];
		
		//finding coefficients of y(t)*x'(t)
		for (int i = 0; i < unintegratedCoefficients.length; i++){
			unintegratedCoefficients[i][0] = Ycoefficients[i][0]*dXcoefficients[i][0];
			unintegratedCoefficients[i][1] = Ycoefficients[i][1]*dXcoefficients[i][0] + Ycoefficients[i][0]*dXcoefficients[i][1];
			unintegratedCoefficients[i][2] = Ycoefficients[i][2]*dXcoefficients[i][0] + Ycoefficients[i][1]*dXcoefficients[i][1] + Ycoefficients[i][0]*dXcoefficients[i][2];
			unintegratedCoefficients[i][3] = Ycoefficients[i][3]*dXcoefficients[i][0] + Ycoefficients[i][2]*dXcoefficients[i][1] + Ycoefficients[i][1]*dXcoefficients[i][2];
			unintegratedCoefficients[i][4] = Ycoefficients[i][3]*dXcoefficients[i][1] + Ycoefficients[i][2]*dXcoefficients[i][2];
			unintegratedCoefficients[i][5] = Ycoefficients[i][3]*dXcoefficients[i][2];
		}
		
		//integration
		for (int i = 0; i < unintegratedCoefficients.length; i++){
			for (int k = 0; k < unintegratedCoefficients[0].length; k++){
				unintegratedCoefficients[i][k] = unintegratedCoefficients[i][k] / (k+1);
			}
		}
		this.areaCoefficients = unintegratedCoefficients;
	}
	
	/**
	 * this method calculates the derivative coefficients using calculus methods, as it is a trivial polynomial
	 */
	private void calcDerivatives(){
		dXcoefficients = new double[Xcoefficients.length][3];
		dYcoefficients = new double[Ycoefficients.length][3];
		
		for (int i = 0; i < dXcoefficients.length; i++){
			for (int k = 0; k < 3; k++){
				dXcoefficients[i][k] = (k+1)*Xcoefficients[i][k+1];
				dYcoefficients[i][k] = (k+1)*Ycoefficients[i][k+1];
			}
		}
		
	}
	
	@Override
	public double evaluateArcLengthFunction(int piece, double t){
		double dx2 = dXcoefficients[piece][0] + t*(dXcoefficients[piece][1] + t*dXcoefficients[piece][2]);
		double dy2 = dYcoefficients[piece][0] + t*(dYcoefficients[piece][1] + t*dYcoefficients[piece][2]);
		dx2 = dx2 * dx2;
		dy2 = dy2 * dy2;
		return Math.sqrt(dx2+dy2);
	}
	
	/**
	 * uses gaussian elimination to solve a matrix
	 * @param A Matrix
	 * @param b vector b
	 * @return solution vector c
	 */
	public static double[] gaussianElimination(double[][] A, double[] b) {
		if (A.length == b.length) {
			double[][] matrix = new double[A.length][A[0].length + 1];
			// Creates the matrix with the b vector in it
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[0].length; j++) {
					if (j == matrix[0].length - 1) {
						matrix[i][j] = b[i];
					} else {
						matrix[i][j] = A[i][j];
					}
				}
			}
			int x = matrix.length;
			double[] temp = new double[matrix[0].length];
			for (int i = 0; i < x; i++) {
				int pivot = i;
				for (int k = i + 1; k < x; k++) {
					if (matrix[k][i] == 1) {
						pivot = k;
					}
				}
				if (pivot != i) {
					System.arraycopy(matrix[i], 0, temp, 0, temp.length);
					System.arraycopy(matrix[pivot], 0, matrix[i], 0,
							matrix[0].length);
					System.arraycopy(temp, 0, matrix[pivot], 0,
							matrix[0].length);
				} else {
					double div = 1;
					for (int k = 0; k < matrix[0].length; k++) {
						if (matrix[i][k] != 0) {
							div = matrix[i][k];
							break;
						}
					}
					for (int j = 0; j < matrix[0].length; j++) {
						if (matrix[i][j] == 0) {
						} else {
							matrix[i][j] = matrix[i][j] / div;
						}
					}
				}
				// Makes all rows above 0
				if (i != 0) {
					for (int j = i - 1; j >= 0; j--) {
						double mul = 1;
						for (int k = 0; k < matrix[0].length; k++) {
							if (matrix[i][k] != 0) {
								mul = matrix[j][k];
//								System.out.println("mul " + matrix[j][k]);
								break;
							}
						}
						for (int k = 0; k < matrix[0].length; k++) {
							matrix[j][k] = matrix[j][k] - (matrix[i][k] * mul);
						}
					}
				}
				// Makes all rows underneath 0
				for (int j = i + 1; j < x; j++) {
					double mul = 1;
					for (int k = 0; k < matrix[0].length; k++) {
						if (matrix[i][k] != 0) {
							mul = matrix[j][k];
							break;
						}
					}
					for (int k = 0; k < matrix[0].length; k++) {
						matrix[j][k] = matrix[j][k] - (matrix[i][k] * mul);
					}
				}
			}
			double[] result = new double[b.length];
			for (int i = 0; i < b.length; i++) {
				result[i] = matrix[i][matrix[0].length - 1];
			}
			return result;
		} else {
//			System.out.println("The vector is incompatible with the matrix");
		}
		return null;
	}
	
	@Override
    public String toString(){
		String string = "CubicSpline: ";
		for (int i = 0; i < super.points.size(); i++){
			string = string + "[" + super.points.get(i).getX() +","+ super.points.get(i).getY() + "] " ;
		}
		return string;
	}

	@Override
	protected List<Point2D> getConversionPoints() {
		return (List<Point2D>)this.points;
	}
	
    // Gaussian elimination with partial pivoting
    public static double[] lsolve(double[][] A, double[] b) {
    	
    	//http://introcs.cs.princeton.edu/java/95linear/GaussianElimination.java.html
    	
        int N  = b.length;

        for (int p = 0; p < N; p++) {

            // find pivot row and swap
            int max = p;
            for (int i = p + 1; i < N; i++) {
                if (Math.abs(A[i][p]) > Math.abs(A[max][p])) {
                    max = i;
                }
            }
            double[] temp = A[p]; A[p] = A[max]; A[max] = temp;
            double   t    = b[p]; b[p] = b[max]; b[max] = t;

            // singular or nearly singular
            if (Math.abs(A[p][p]) <= EPSILON) {
                throw new RuntimeException("Matrix is singular or nearly singular");
            }

            // pivot within A and b
            for (int i = p + 1; i < N; i++) {
                double alpha = A[i][p] / A[p][p];
                b[i] -= alpha * b[p];
                for (int j = p; j < N; j++) {
                    A[i][j] -= alpha * A[p][j];
                }
            }
        }

        // back substitution
        double[] x = new double[N];
        for (int i = N - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = i + 1; j < N; j++) {
                sum += A[i][j] * x[j];
            }
            x[i] = (b[i] - sum) / A[i][i];
        }
        return x;
    }
	
}
