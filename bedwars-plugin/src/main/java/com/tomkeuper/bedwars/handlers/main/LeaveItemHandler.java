package com.tomkeuper.bedwars.handlers.main;

import com.tomkeuper.bedwars.BedWars;
import com.tomkeuper.bedwars.api.arena.IArena;
import com.tomkeuper.bedwars.api.configuration.ConfigPath;
import com.tomkeuper.bedwars.api.items.handlers.HandlerType;
import com.tomkeuper.bedwars.api.items.handlers.IPermanentItem;
import com.tomkeuper.bedwars.api.language.Language;
import com.tomkeuper.bedwars.api.language.Messages;
import com.tomkeuper.bedwars.arena.Arena;
import com.tomkeuper.bedwars.arena.Misc;
import com.tomkeuper.bedwars.api.items.handlers.PermanentItemHandler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

import static com.tomkeuper.bedwars.BedWars.config;
import static com.tomkeuper.bedwars.commands.bedwars.subcmds.regular.CmdLeave.openLeaveGUI;

public class LeaveItemHandler extends PermanentItemHandler {

    private static final HashMap<UUID, Long> delay = new HashMap<>();
    private static final HashMap<UUID, BukkitTask> leaving = new HashMap<>();
    public LeaveItemHandler(String id, Plugin plugin, com.tomkeuper.bedwars.api.BedWars api) {
        super(id, plugin, api);
    }

    @Override
    public void handleUse(Player player, IArena arena, IPermanentItem item) {

        if (cancel(player.getUniqueId())) return;

        IArena a = Arena.getArenaByPlayer(player);
        if (player.getWorld().getName().equalsIgnoreCase(BedWars.getLobbyWorld())) {
            update(player.getUniqueId());
            Misc.moveToLobbyOrKick(player, a, a != null && a.isSpectator(player.getUniqueId()));
        } else {
            if (a == null) {
                update(player.getUniqueId());
                player.sendMessage(Language.getMsg(player, Messages.COMMAND_FORCESTART_NOT_IN_GAME));
                return;
            }

            if (BedWars.getPartyManager().isOwner(player)) {
                openLeaveGUI(player);
            } else {
                int leaveDelay;
                if (a.isSpectator(player)) {
                    leaveDelay = config.getInt(ConfigPath.GENERAL_CONFIGURATION_SPECTATOR_ITEMS_LEAVE_DELAY.replace("%path%", item.getIdentifier()));
                } else {
                    leaveDelay = config.getInt(ConfigPath.GENERAL_CONFIGURATION_PRE_GAME_ITEMS_LEAVE_DELAY.replace("%path%", item.getIdentifier()));
                }
                if (leaveDelay == 0) {
                    Misc.moveToLobbyOrKick(player, arena, arena.isSpectator(player.getUniqueId()));
                } else {
                    BukkitTask qt = leaving.get(player.getUniqueId());
                    if (qt != null) {
                        update(player.getUniqueId());
                        qt.cancel();
                        leaving.remove(player.getUniqueId());
                        player.sendMessage(Language.getMsg(player, Messages.COMMAND_LEAVE_CANCELED));
                        return;
                    }
                    player.sendMessage(Language.getMsg(player, Messages.COMMAND_LEAVE_STARTED).replace("%bw_leave_delay%", String.valueOf(leaveDelay)));
                    BukkitTask bukkitTask = new BukkitRunnable() {
                        public void run() {
                            Misc.moveToLobbyOrKick(player, arena, arena.isSpectator(player.getUniqueId()));
                            leaving.remove(player.getUniqueId());
                        }
                    }.runTaskLater(BedWars.plugin, leaveDelay * 20L);
                    leaving.put(player.getUniqueId(), bukkitTask);
                }
            }
        }
    }

    private static void update(UUID player){
        if (delay.containsKey(player)){
            delay.replace(player, System.currentTimeMillis() + 2500L);
            return;
        }
        delay.put(player, System.currentTimeMillis() + 2500L);
    }

    private static boolean cancel(UUID player){
        return delay.getOrDefault(player, 0L) > System.currentTimeMillis();
    }

    @Override
    public HandlerType getType() {
        return HandlerType.LEAVE_ARENA;
    }

}
