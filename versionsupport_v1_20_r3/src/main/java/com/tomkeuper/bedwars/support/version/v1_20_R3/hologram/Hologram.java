package com.tomkeuper.bedwars.support.version.v1_20_R3.hologram;

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
        this.lines.get(index).setText(line);
    }

    @Override
    public void setLine(int index, String line, boolean update) {
        this.lines.remove(index);
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
        this.lines.forEach(IHoloLine::remove);
    }
}
