package com.tomkeuper.bedwars.api.upgrades;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface IUpgradeManager {
    boolean isWatchingUpgrades(UUID uuid);

    int getMoney(Player player, Material currency);

    Material getCurrency(String name);

    MenuContent getMenuContent(ItemStack item);

    MenuContent getMenuContent(String identifier);
}
