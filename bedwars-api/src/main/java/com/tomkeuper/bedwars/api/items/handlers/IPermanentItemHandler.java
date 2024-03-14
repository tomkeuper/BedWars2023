package com.tomkeuper.bedwars.api.items.handlers;

import com.tomkeuper.bedwars.api.arena.IArena;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Interface for handling permanent lobby items.
 */
public interface IPermanentItemHandler {

    /**
     * Handles the use of a permanent item by a player in an arena.
     *
     * @param player The player who used the item.
     * @param arena  The arena where the item was used.
     * @param item   The permanent item that was used.
     */
    void handleUse(Player player, IArena arena, IPermanentItem item);

    /**
     * Checks if the permanent item should be visible to the player in the arena.
     *
     * @param player The player viewing the item.
     * @param arena  The arena where the item is located.
     * @return True if the lobby item should be visible; false otherwise.
     */
    boolean isVisible(Player player, IArena arena);

    /**
     * Gets the ID of the permanent item handler.
     *
     * @return The ID of the handler.
     */
    String getId();

    /**
     * Gets the plugin associated with the permanent item handler.
     *
     * @return The plugin associated with the handler.
     */
    Plugin getPlugin();

    /**
     * Gets the type of the permanent item handler.
     *
     * @return The type of the handler.
     */
    HandlerType getType();

    /**
     * Checks if the permanent item handler is registered.
     *
     * @return True if the handler is registered; false otherwise.
     */
    boolean isRegistered();
}
