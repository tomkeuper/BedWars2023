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

package com.tomkeuper.bedwars.levels.internal;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.events.player.PlayerLevelUpEvent;
import com.tomkeuper.bedwars.api.events.player.PlayerXpGainEvent;
import com.tomkeuper.bedwars.configuration.LevelsConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("WeakerAccess")
public class PlayerLevel {

    private final UUID uuid;
    private int level;
    private int nextLevelCost;
    private String levelName;
    private int currentXp;
    private String progressBar;
    private String requiredXp;
    private String formattedCurrentXp;

    // keep trace if current level is different than the one in database
    private boolean modified = false;

    private static final Map<UUID, PlayerLevel> levelByPlayer =
            new HashMap<>(256, 0.75f);

    private static final ThreadLocal<NumberFormat> NF = ThreadLocal.withInitial(() -> {
        NumberFormat f = NumberFormat.getInstance();
        f.setMaximumFractionDigits(2);
        f.setMinimumFractionDigits(0);
        return f;
    });

    private static final String[] PROGRESS_BARS = new String[11];

    static {
        String format = LevelsConfig.levels.getString("progress-bar.format");
        String symbol = LevelsConfig.levels.getString("progress-bar.symbol");
        String unlockedColor = LevelsConfig.levels.getString("progress-bar.unlocked-color");
        String lockedColor = LevelsConfig.levels.getString("progress-bar.locked-color");
        for (int unlocked = 0; unlocked <= 10; unlocked++) {
            int locked = 10 - unlocked;
            String bar = format.replace("{progress}",
                    unlockedColor
                            + String.valueOf(new char[unlocked]).replace("\0", symbol)
                            + lockedColor
                            + String.valueOf(new char[locked]).replace("\0", symbol)
            );
            PROGRESS_BARS[unlocked] = ChatColor.translateAlternateColorCodes('&', bar);
        }
    }

    /**
     * Cache a player level.
     */
    public PlayerLevel(UUID player, int level, int currentXp) {
        this.uuid = player;
        setLevelName(level);
        setNextLevelCost(level, true);

        //fix levels broken in the past by an issue
        if (level < 1) level = 1;
        if (currentXp < 0) currentXp = 0;

        this.level = level;
        this.currentXp = currentXp;
        updateProgressBar();
        if (!levelByPlayer.containsKey(player)) levelByPlayer.put(player, this);
    }

    public void setLevelName(int level) {
        this.levelName = ChatColor.translateAlternateColorCodes('&', LevelsConfig.getLevelName(level)).replace("{number}", String.valueOf(level));
    }

    public void setNextLevelCost(int level, boolean initialize) {
        if (!initialize) modified = true;
        this.nextLevelCost = LevelsConfig.getNextCost(level);
    }

    public void lazyLoad(int level, int currentXp) {
        modified = false;
        if (level < 1) level = 1;
        if (currentXp < 0) currentXp = 0;
        setLevelName(level);
        setNextLevelCost(level, true);
        this.level = level;
        this.currentXp = currentXp;
        updateProgressBar();
        modified = false;
    }

    /**
     * Update the player progress bar.
     */
    private void updateProgressBar() {
        double ratio = (nextLevelCost - currentXp) / (double) nextLevelCost * 10;
        int unlocked = 10 - (int) ratio;
        if (unlocked < 0) unlocked = 0;
        if (unlocked > 10) unlocked = 10;

        progressBar = PROGRESS_BARS[unlocked];
        requiredXp = formatNumber(nextLevelCost);
        formattedCurrentXp = formatNumber(currentXp);
    }

    /**
     * Get player current level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Get the amount of xp required to level up.
     */
    public int getNextLevelCost() {
        return nextLevelCost;
    }

    /**
     * Get PlayerLevel by player.
     */
    public static PlayerLevel getLevelByPlayer(UUID player) {
        return levelByPlayer.getOrDefault(player, new PlayerLevel(player, 1, 0));
    }

    /**
     * Get player uuid.
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Get player current level display name.
     */
    public String getLevelName() {
        return levelName;
    }

    /**
     * Get player xp.
     */
    public int getCurrentXp() {
        return currentXp;
    }

    /**
     * Get progress bar for player.
     */
    public String getProgress() {
        return progressBar;
    }

    /**
     * Get target xp already formatted.
     * Like: 2000 is 2k
     */
    public String getFormattedRequiredXp() {
        return requiredXp;
    }

    /**
     * Add xp to player with source.
     */
    public void addXp(int xp, PlayerXpGainEvent.XpSource source) {
        if (xp < 0) return;
        PlayerXpGainEvent event = new PlayerXpGainEvent(Bukkit.getPlayer(uuid), xp, source);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        this.currentXp += event.getAmount();
        upgradeLevel();
        updateProgressBar();
        modified = true;
    }

    /**
     * Set player xp.
     */
    public void setXp(int currentXp) {
        if (currentXp <= 0) currentXp = 0;
        this.currentXp = currentXp;
        upgradeLevel();
        updateProgressBar();
        modified = true;
    }

    /**
     * Set player level.
     */
    public void setLevel(int level) {
        this.level = level;
        nextLevelCost = LevelsConfig.getNextCost(level);
        this.levelName = ChatColor.translateAlternateColorCodes('&',
                        LevelsConfig.getLevelName(level))
                .replace("{number}", String.valueOf(level));
        requiredXp = nextLevelCost >= 1000
                ? nextLevelCost % 1000 == 0
                ? nextLevelCost / 1000 + "k"
                : (double) nextLevelCost / 1000 + "k"
                : String.valueOf(nextLevelCost);
        updateProgressBar();
        modified = true;
    }

    /**
     * Get player xp already formatted.
     * Like: 1000 is 1k
     */
    public String getFormattedCurrentXp() {
        return formattedCurrentXp;
    }

    /**
     * Used to upgrade player level.
     */
    public void upgradeLevel() {
        if (currentXp >= nextLevelCost) {
            currentXp = currentXp - nextLevelCost;
            level++;
            nextLevelCost = LevelsConfig.getNextCost(level);
            this.levelName = ChatColor.translateAlternateColorCodes('&',
                            LevelsConfig.getLevelName(level))
                    .replace("{number}", String.valueOf(level));
            requiredXp = formatNumber(nextLevelCost);
            formattedCurrentXp = formatNumber(currentXp);
            Bukkit.getPluginManager().callEvent(new PlayerLevelUpEvent(
                    Bukkit.getPlayer(getUuid()), level, nextLevelCost));
            modified = true;
        }
    }

    private String formatNumber(int score) {
        NumberFormat f = NF.get();
        if (score >= 1000) return f.format(score / 1000.0) + "k";

        return f.format(score);
    }

    /**
     * Destroy data.
     */
    public void destroy() {
        levelByPlayer.remove(uuid);
        updateDatabase();
    }

    public void updateDatabase() {
        if (modified) {
            Bukkit.getScheduler().runTaskAsynchronously(BedWars.plugin, () ->
                    BedWars.getRemoteDatabase().setLevelData(
                            uuid, level, currentXp,
                            LevelsConfig.getLevelName(level),
                            nextLevelCost
                    )
            );
            modified = false;
        }
    }
}
