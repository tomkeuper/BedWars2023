package com.tomkeuper.bedwars.configuration;

import com.tomkeuper.bedwars.BedWars;
import org.bukkit.entity.Player;

public class Permissions {
    public static final String PERMISSION_FORCESTART = BedWars.mainCmd+".forcestart";
    public static final String PERMISSION_START = BedWars.mainCmd+".start";
    public static final String PERMISSION_ARENA_SETEVENT = BedWars.mainCmd+".arena.setEvent";

    public static final String PERMISSION_ALL = BedWars.mainCmd+".*";
    public static final String PERMISSION_COMMAND_BYPASS = BedWars.mainCmd+".cmd.bypass";
    public static final String PERMISSION_SHOUT_COMMAND = BedWars.mainCmd+".shout";

    public static final String PERMISSION_SETUP_ARENA = BedWars.mainCmd+".setup";
    public static final String PERMISSION_ARENA_GROUP = BedWars.mainCmd+".groups";
    public static final String PERMISSION_BUILD = BedWars.mainCmd+".build";
    public static final String PERMISSION_CLONE = BedWars.mainCmd+".clone";
    public static final String PERMISSION_DEL_ARENA = BedWars.mainCmd+".delete";
    public static final String PERMISSION_ARENA_ENABLE = BedWars.mainCmd+".enableRotation";
    public static final String PERMISSION_ARENA_DISABLE = BedWars.mainCmd+".disable";
    public static final String PERMISSION_NPC = BedWars.mainCmd+".npc";
    public static final String PERMISSION_RELOAD = BedWars.mainCmd+".reload";
    public static final String PERMISSION_REJOIN = BedWars.mainCmd+".rejoin";
    public static final String PERMISSION_LEVEL = BedWars.mainCmd+".level";
    public static final String PERMISSION_CHAT_COLOR = BedWars.mainCmd+".chatcolor";
    public static final String PERMISSION_VIP = BedWars.mainCmd+".vip";
    public static final String PERMISSION_GMC = BedWars.mainCmd + ".GMC";
    public static final String PERMISSION_GMS = BedWars.mainCmd + ".GMS";


    /**
     * Check if player has one of the given permissions.
     */
    public static boolean hasPermission(Player player, String... permissions){
        for (String permission : permissions){
            if (player.hasPermission(permission)){
                return true;
            }
        }
        return false;
    }
}
