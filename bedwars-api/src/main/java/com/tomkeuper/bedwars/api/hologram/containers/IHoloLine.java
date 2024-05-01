/*
 * BedWars2023 - A bed wars mini-game.
 * Copyright (C) 2024 Tomas Keuper
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact e-mail: contact@fyreblox.com
 */

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
     * Reveals the hologram line if the line has disappeared
     * been removed by the server or another plugin
     */
    void reveal();

    /**
     * Remove the hologram line but keeping the line object
     * in its bounded hologram
     */
    void remove();

    /**
     * Destroy the hologram line and also remove it
     * from the hologram
     */
    void destroy();
}
