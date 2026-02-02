//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.tomkeuper.bedwars.api.events.player;

import com.tomkeuper.bedwars.api.arena.IArena;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerItemDepositEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final @NotNull Player player;
    private final @NotNull IArena arena;
    private final @NotNull ItemStack item;
    private final @NotNull Inventory targetInventory;
    private final boolean isEnderChest;
    private boolean cancelled;

    public PlayerItemDepositEvent(@NotNull Player player, @NotNull IArena arena, @NotNull ItemStack item, @NotNull Inventory targetInventory, boolean isEnderChest) {
        this.player = player;
        this.arena = arena;
        this.item = item.clone();
        this.targetInventory = targetInventory;
        this.isEnderChest = isEnderChest;
        this.cancelled = false;
    }

    public @NotNull Player getPlayer() {
        return this.player;
    }

    public @NotNull IArena getArena() {
        return this.arena;
    }

    public @NotNull ItemStack getItem() {
        return this.item;
    }

    public @NotNull Inventory getTargetInventory() {
        return this.targetInventory;
    }

    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    public boolean isEnderChest() {
        return this.isEnderChest;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }
}
