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

package com.tomkeuper.bedwars.support.version.v1_19_R3.hologram;

import com.tomkeuper.bedwars.api.hologram.containers.IHoloLine;
import com.tomkeuper.bedwars.api.hologram.containers.IHologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Hologram implements IHologram {
    private final Player p;
    private List<IHoloLine> lines;
    private final Location loc;
    private double gap = 0.25;
    private boolean showing = true;

    public Hologram(Player p, List<IHoloLine> lines, Location loc) {
        this.p = p;
        this.lines = lines;
        this.loc = loc;
    }

    public Player getPlayer() {
        return this.p;
    }

    public Hologram(Player p, Location loc, List<String> lines) {
        this.p = p;
        this.loc = loc;
        this.lines = new ArrayList<>();

        for (String line : lines) {
            this.lines.add(new HoloLine(line, this));
        }
    }

    @Override
    public void addLine(IHoloLine line) {
        this.lines.add(line);
    }

    @Override
    public void removeLine(IHoloLine line) {
        this.lines.remove(line);
    }

    @Override
    public void removeLine(int index) {
        this.lines.remove(index);
    }

    @Override
    public void clearLines() {
        this.lines.clear();
    }

    @Override
    public void update() {
        for (IHoloLine line : this.lines) {
            line.update();
        }
    }

    @Override
    public void show() {
        this.showing = true;
        for (IHoloLine line : this.lines) {
            line.show();
        }
    }

    @Override
    public void hide() {
        this.showing = false;
        for (IHoloLine line : this.lines) {
            line.hide();
        }
    }

    @Override
    public boolean isShowing() {
        return showing;
    }

    @Override
    public int size() {
        return this.lines.size();
    }

    @Override
    public IHoloLine getLine(int index) {
        return this.lines.get(index);
    }

    @Override
    public List<IHoloLine> getLines() {
        return this.lines;
    }

    @Override
    public Location getLocation() {
        return this.loc;
    }

    @Override
    public void setLines(String[] lines, boolean update) {
        this.lines.clear();
        for (String line : lines) {
            this.lines.add(new HoloLine(line, this));
        }
        if (update) {
            this.update();
        }
    }

    @Override
    public void setLines(List<IHoloLine> lines, boolean update) {
        this.lines = lines;
        if (update) {
            this.update();
        }
    }

    @Override
    public void setLine(int index, IHoloLine line) {
        this.lines.set(index, line);
        this.lines.get(index).update();
    }

    @Override
    public void setLine(int index, IHoloLine line, boolean update) {
        this.lines.set(index, line);
        if (update) {
            this.lines.get(index).update();
        }
    }

    @Override
    public void setLine(int index, String line) {
        if (this.lines.isEmpty()) {
            this.lines.add(new HoloLine(line, this));
            return;
        }

        if (this.lines.get(index) == null) {
            this.lines.add(index, new HoloLine(line, this));
            return;
        }
        this.lines.get(index).setText(line);
    }

    @Override
    public void setLine(int index, String line, boolean update) {
        if (this.lines.isEmpty()) {
            this.lines.add(new HoloLine(line, this));
            return;
        }

        if (this.lines.get(index) == null) {
            this.lines.add(index, new HoloLine(line, this));
            return;
        }
        this.lines.get(index).setText(line, update);
    }

    @Override
    public void insertLine(int index, IHoloLine line) {
        this.lines.remove(index);
        this.lines.add(index, line);
    }

    @Override
    public void insertLine(int index, IHoloLine line, boolean update) {
        this.lines.add(index, line);
        if (update) {
            this.update();
        }
    }

    @Override
    public void insertLine(int index, String line) {
        this.lines.remove(index);
        this.lines.add(index, new HoloLine(line, this));
    }

    @Override
    public void insertLine(int index, String line, boolean update) {
        this.lines.remove(index);
        this.lines.add(index, new HoloLine(line, this));
        if (update) {
            this.update();
        }
    }

    @Override
    public void addLine(IHoloLine line, boolean update) {
        this.lines.add(line);
        if (update) {
            this.update();
        }
    }

    @Override
    public void addLine(String line) {
        this.lines.add(new HoloLine(line, this));
    }

    @Override
    public void addLine(String line, boolean update) {
        this.lines.add(new HoloLine(line, this));
        if (update) {
            this.update();
        }
    }

    @Override
    public void removeLine(IHoloLine line, boolean update) {
        this.lines.remove(line);
        if (update) {
            this.update();
        }
    }

    @Override
    public void removeLine(int index, boolean update) {
        this.lines.remove(index);
        if (update) {
            this.update();
        }
    }

    @Override
    public void clearLines(boolean update) {
        this.lines.clear();
        if (update) {
            this.update();
        }
    }

    @Override
    public void setGap(double gap) {
        this.gap = gap;
        this.update();
    }

    @Override
    public void setGap(double gap, boolean update) {
        this.gap = gap;
        if (update) {
            this.update();
        }
    }

    @Override
    public double getGap() {
        return this.gap;
    }

    @Override
    public void remove() {
        for (IHoloLine line : this.lines) line.remove();
        lines.clear();
    }
}
