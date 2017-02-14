package ui.events;

/**
 * An event to show that the algorithm to calculate the Area should be changed
 *
 * @author Kareem Horstink
 * @version 1.0
 */
public class GuiEventsAreaChange extends GuiEvents {

    private int algorithm;

    /**
     * Constructor
     *
     * @param source The source of the event
     * @param algorithm The algorithm to be changed into
     */
    public GuiEventsAreaChange(Object source, int algorithm) {
        super(source);
        this.algorithm = algorithm;
    }

    /**
     * Returns the number that represents the algorithm
     *
     * @return The number that represents the algorithm
     */
    public int getAlgorithm() {
        return algorithm;
    }
}
