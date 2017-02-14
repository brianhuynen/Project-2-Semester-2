package curves;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class GenitorBowl extends Curve{
	private static final double SELECTIVE_PRESSURE_POWER = 2;
	
	private static final int POP_SIZE = 1000; //1000
	private static final int CHILDREN = 100000; //100000
	private static final int NUMBER_OF_POINTS = 5;
	
	ArrayList<GenitorIndividual> population;
	
	public GenitorBowl(String name, double targetLength){
		super(name);
		population = new ArrayList<GenitorIndividual>();
		generatePopulation(Math.abs(targetLength), NUMBER_OF_POINTS);
		replicate();
		System.out.println("best: " + population.get(0).getALRatio() + ", length: " + population.get(0).getElement().length(NumericalApproximation.ROMBERG_ARCLENGTH));
	}
	
	private void replicate(){
		for (int i = 0; i < CHILDREN; i++){
			binaryInsert(population.get(((int) ((Math.pow(Math.random(), SELECTIVE_PRESSURE_POWER))* POP_SIZE))).reproduce(population.get(((int) ((Math.pow(Math.random(), SELECTIVE_PRESSURE_POWER))* POP_SIZE)))));
			population.remove(POP_SIZE);
		}
	}
	
	private void generatePopulation(double targetLength, int numberOfPoints) {
		for (int i = 0; i < POP_SIZE; i++){
			binaryInsert(new GenitorIndividual(targetLength, numberOfPoints));
		}
	}

	/**
	 * inserts a new individual in a sorted fashion into the population using binary search
	 * @param indiv individual to be inserted
	 */
	private void binaryInsert(GenitorIndividual indiv){
		if (population.size() == 0){
			population.add(indiv);
		}
		else {
			int test = population.size()/2;
			int min = 0;
			int max = population.size();
			while (max-min > 1){
				if (indiv.compareTo(population.get(test)) == 0){
					max = test;
					min = test;
					population.add(test, indiv);
				}
				else if (indiv.compareTo(population.get(test)) > 0){
					max = test;
					test = (test+min)/2;
				}
				else {
					min = test;
					test = (max+test)/2;
				}
			}
			if (indiv.compareTo(population.get(test)) > 0){
				population.add(test,indiv);
			}
			else {
				population.add(test+1,indiv);
			}
		}
	}

	@Override
	protected ArrayList<Point2D> getPlot(int subPoints) {
		return population.get(0).getElement().getPlot(subPoints);
	}
	
	@Override
	protected double area(int method){
		population.get(0).getElement().update();
		return population.get(0).getElement().area(NumericalApproximation.EXACT_AREA_CUBIC);
	}
	
	@Override
	protected double length(int method){
		population.get(0).getElement().update();
		return population.get(0).getElement().length(NumericalApproximation.ROMBERG_ARCLENGTH);
	}
	
	@Override
	protected boolean isClosed() {
		return false;
	}
	
	@Override
	public String toString(){
		return "fittest: " + population.get(0).getALRatio();
	}
	
	@Override
	protected List<Point2D> getControlPoints() {
		return population.get(0).getElement().points;
	}
	
    @Override
	protected void setPoint(int index, double x, double y) {
	}

	@Override
	protected List<Point2D> getConversionPoints() {
		return population.get(0).getElement().points;
	}
	
}
