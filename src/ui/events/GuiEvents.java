package ui.events;

/**
 * The general GUI Event
 *
 * @author Kareem Horstink
 * @version 1.0
 */
public abstract class GuiEvents extends java.util.EventObject {

    /**
     * The default constructor
     * 
     * @param source The source of the event
     */
    public GuiEvents(Object source) {
        super(source);
    }   
}
