package com.tomkeuper.bedwars.api.hologram.containers;

public interface IHoloLine {

    /**
     * Get the text of the hologram line.
     * @return the text
     */
    String getText();

    /**
     * Get the hologram bounded to.
     * @return the hologram
     */
    IHologram getHologram();

    /**
     * Update the hologram line.
     */
    void update();

    /**
     * Show the hologram line.
     */
    void show();

    /**
     * Hide the hologram line.
     */
    void hide();

    /**
     * Check if the hologram line is showing.
     * @return true if the hologram line is showing
     */
    boolean isShowing();

    /**
     * Check if the hologram line is destroyed.
     * @return true if the hologram line is destroyed
     */
    boolean isDestroyed();

    /**
     * Set the hologram bounded to.
     * @param hologram - the hologram
     */
    void setHologram(IHologram hologram);

    /**
     * Set the text of the hologram line.
     * @param text - the text
     */
    void setText(String text);

    /**
     * Set the text of the hologram line.
     * @param text - the text
     * @param update - if the hologram line should be updated
     */
    void setText(String text, boolean update);

    /**
     * Reveals the hologram line (Still in the hologram)
     */
    void reveal();

    /**
     * Remove the hologram line (Still in the hologram)
     */
    void remove();

    /**
     * Destroy the hologram line (Will be removed from the hologram)
     */
    void destroy();
}
