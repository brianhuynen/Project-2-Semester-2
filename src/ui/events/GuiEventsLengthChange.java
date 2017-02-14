package ui.events;

/**
 * An event to show that the algorithm to calculate the length should be changed
 *
 * @author Kareem Horstink
 * @version 1.0
 */
public class GuiEventsLengthChange extends GuiEvents {

    private int algorithm;

    /**
     * Constructor
     *
     * @param source The source of the event
     * @param algorithm The wanted algorithm
     */
    public GuiEventsLengthChange(Object source, int algorithm) {
        super(source);
        this.algorithm = algorithm;
    }

    /**
     * Returns the algorithm needed
     *
     * @return The algorithm wanted
     */
    public int getAlgorithm() {
        return algorithm;
    }
}
