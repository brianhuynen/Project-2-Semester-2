package ui.events;

/**
 * Event to handle the change in if the line is visible
 *
 * @author Kareem Horstink
 */
public class GuiEventsVisibility extends GuiEvents {

    /**
     * Constructor
     *
     * @param source The source of the event
     * @param visiblity The boolean to set visible or not
     */
    public GuiEventsVisibility(Object source, boolean visiblity) {
        super(source);
        this.visiblity = visiblity;
    }

    private boolean visiblity;

    /**
     * Return the visibility
     *
     * @return The boolean to set visible or not
     */
    public boolean getVisiablity() {
        return visiblity;
    }

}
