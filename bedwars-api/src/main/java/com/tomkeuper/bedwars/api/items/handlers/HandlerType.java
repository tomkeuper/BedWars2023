package com.tomkeuper.bedwars.api.items.handlers;

import org.jetbrains.annotations.Nullable;

public enum HandlerType {
    LEAVE_ARENA("bw:leave_arena"),
    VIEW_STATS("bw:player_stats"),
    COMMAND("bw:command_item"),
    PLUGIN(null);

    private final String id;

    HandlerType(String id) {
        this.id = id;
    }

    public @Nullable String getId() {
        return this.id;
    }
}
