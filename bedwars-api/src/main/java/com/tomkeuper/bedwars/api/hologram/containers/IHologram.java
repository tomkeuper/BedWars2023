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

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public interface IHologram {

    /**
     * Get the player this hologram is bounded to.
     * @return the player
     */
    Player getPlayer();

    /**
     * Add a line to the hologram.
     * @param line - the line you want to add
     */
    void addLine(IHoloLine line);

    /**
     * Remove a line from the hologram.
     * @param line - the line you want to remove
     */
    void removeLine(IHoloLine line);

    /**
     * Remove a line from the hologram.
     * @param index - the index of the line you want to remove
     */
    void removeLine(int index);

    /**
     * Clear all lines from the hologram.
     */
    void clearLines();

    /**
     * Update the hologram.
     */
    void update();

    /**
     * Show the hologram.
     */
    void show();

    /**
     * Hide the hologram.
     */
    void hide();

    /**
     * Check if the hologram is showing.
     * @return true if the hologram is showing
     */
    boolean isShowing();

    /**
     * Get the size of the hologram.
     * @return the size
     */
    int size();

    /**
     * Get a line from the hologram.
     * @param index - the index of the line
     * @return the line
     */
    IHoloLine getLine(int index);

    /**
     * Get all lines from the hologram.
     * @return the lines
     */
    List<IHoloLine> getLines();

    /**
     * Get the location of the hologram.
     * @return the location
     */
    Location getLocation();

    /**
     * Set the hologram lines.
     * @param lines - the lines
     * @param update - if the hologram should be updated
     */
    void setLines(String[] lines, boolean update);

    /**
     * Set the hologram lines.
     * @param lines - the lines
     * @param update - if the hologram should be updated
     */
    void setLines(List<IHoloLine> lines, boolean update);

    /**
     * Set a line of the hologram.
     * @param index - the index of the line
     * @param line - the line
     */
    void setLine(int index, IHoloLine line);

    /**
     * Set a line of the hologram.
     * @param index - the index of the line
     * @param line - the line
     * @param update - if the hologram should be updated
     */
    void setLine(int index, IHoloLine line, boolean update);

    /**
     * Set a line of the hologram.
     * @param index - the index of the line
     * @param line - the line
     */
    void setLine(int index, String line);

    /**
     * Set a line of the hologram.
     * @param index - the index of the line
     * @param line - the line
     * @param update - if the hologram should be updated
     */
    void setLine(int index, String line, boolean update);

    /**
     * Insert a line to the hologram.
     * @param index - the index of the line
     * @param line - the line
     */
    void insertLine(int index, IHoloLine line);

    /**
     * Insert a line to the hologram.
     * @param index - the index of the line
     * @param line - the line
     * @param update - if the hologram should be updated
     */
    void insertLine(int index, IHoloLine line, boolean update);

    /**
     * Insert a line to the hologram.
     * @param index - the index of the line
     * @param line - the line
     */
    void insertLine(int index, String line);

    /**
     * Insert a line to the hologram.
     * @param index - the index of the line
     * @param line - the line
     * @param update - if the hologram should be updated
     */
    void insertLine(int index, String line, boolean update);

    /**
     * Add a line to the hologram.
     * @param line - the line
     */
    void addLine(IHoloLine line, boolean update);

    /**
     * Add a line to the hologram.
     * @param line - the line
     */
    void addLine(String line);

    /**
     * Add a line to the hologram.
     * @param line - the line
     * @param update - if the hologram should be updated
     */
    void addLine(String line, boolean update);

    /**
     * Remove a line from the hologram.
     * @param line - the line
     * @param update - if the hologram should be updated
     */
    void removeLine(IHoloLine line, boolean update);

    /**
     * Remove a line from the hologram.
     * @param index - the index of the line
     * @param update - if the hologram should be updated
     */
    void removeLine(int index, boolean update);

    /**
     * Clear all lines from the hologram.
     * @param update - if the hologram should be updated
     */
    void clearLines(boolean update);

    /**
     * Set the gap between the lines.
     * @param gap - the gap
     */
    void setGap(double gap);

    /**
     * Set the gap between the lines.
     * @param gap - the gap
     * @param update - if the hologram should be updated
     */
    void setGap(double gap, boolean update);

    /**
     * Get the gap between the lines.
     */
    double getGap();

    /**
     * Remove the hologram.
     */
    void remove();
}
