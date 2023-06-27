package com.tomkeuper.bedwars.api.tasks;

import com.tomkeuper.bedwars.api.arena.IArena;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

/**
 * The AnnouncementTask interface represents a task for announcing messages to players in an arena.
 * It provides methods to load and add messages for players, retrieve the associated arena, Bukkit task, task ID, and cancel the task.
 */
public interface AnnouncementTask {

    /**
     * Get the arena associated with this announcement task.
     *
     * @return The arena associated with the task.
     */
    IArena getArena();

    /**
     * Get the Bukkit task associated with this announcement task.
     *
     * @return The Bukkit task associated with the task.
     */
    BukkitTask getBukkitTask();

    /**
     * Get the task ID of the Bukkit task.
     *
     * @return The task ID of the Bukkit task.
     */
    int getTask();

    /**
     * Cancel the announcement task.
     */
    void cancel();

    /**
     * Load messages for a specific player from a given message path.
     *
     * @param p    The player to load the messages for.
     * @param path The message path to load the messages from.
     */
    void loadMessagesForPlayer(Player p, String path);

    /**
     * Add a message for a specific player.
     *
     * @param p       The player to add the message for.
     * @param message The message to add.
     */
    void addMessageForPlayer(Player p, String message);

    /**
     * Add a list of messages for a specific player.
     *
     * @param p        The player to add the messages for.
     * @param messages The list of messages to add.
     */
    void addMessagesForPlayer(Player p, List<String> messages);
}
