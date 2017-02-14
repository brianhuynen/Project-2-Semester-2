package ui.events;

/**
 * Interface to handle the GUI Events
 *
 * @author Kareem Horstink
 * @version 1.00
 */
public interface GuiEventListner {

    /**
     * Passes the event to correct event handler
     *
     * @param e The General GUI event
     */
    public void actionPerformed(GuiEvents e);

    /**
     * Handle the creation of new Curve
     *
     * @param e The create event
     */
    public void handleCreate(GuiEventsCreate e);

    /**
     * Handle the addition a point
     *
     * @param e The add event
     */
    public void handleAdd(GuiEventsAdd e);

    /**
     * Handle the movement of point
     *
     * @param e The move event
     */
    public void handleMove(GuiEventsMove e);

    /**
     * Handle the deletion of a point
     *
     * @param e The delete point event
     */
    public void handleDeleteP(GuiEventsDeleteP e);

    /**
     * Handle the deletion of a curve
     *
     * @param e The delete curve event
     */
    public void handleDeleteC(GuiEventsDeleteC e);

    /**
     * Handle the closing of a curve
     *
     * @param e The close event
     */
    public void handleClose(GuiEventsClose e);

    /**
     * Handle opening a curve
     *
     * @param e The open event
     */
    public void handleOpen(GuiEventsOpen e);

    /**
     * Handle changing of the visibility
     *
     * @param e The visibility event
     */
    public void handleVisibility(GuiEventsVisibility e);

    /**
     * Handles the changing of current curve
     *
     * @param e The current event
     */
    public void handleCurrent(GuiEventsCurrent e);

    /**
     * Handles the refreshing of the GUI
     *
     * @param e The refresh event
     */
    public void handleRefresh(GuiEventsRefresh e);

    /**
     * Handles the changing algorithm for area
     *
     * @param e Area change event
     */
    public void handleAreaChange(GuiEventsAreaChange e);

    /**
     * Handles the changing algorithm for length
     *
     * @param e Length change event
     */
    public void handleLengthChange(GuiEventsLengthChange e);
}
