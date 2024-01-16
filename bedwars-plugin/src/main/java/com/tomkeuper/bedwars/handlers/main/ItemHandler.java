package com.tomkeuper.bedwars.handlers.main;

import com.tomkeuper.bedwars.api.items.handlers.IItemHandler;
import com.tomkeuper.bedwars.api.items.handlers.ILobbyItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class ItemHandler implements IItemHandler {

    Collection<ILobbyItemHandler> lobbyItemHandlers;

    public ItemHandler() {
        this.lobbyItemHandlers = new ArrayList<>();
    }

    @Override
    public Collection<ILobbyItemHandler> getLobbyItemHandlers() {
        return null;
    }

    @Override
    public @Nullable ILobbyItemHandler getLobbyItemHandler(String var1) {
        return null;
    }

    @Override
    public boolean registerLobbyItemHandler(ILobbyItemHandler var1) {
        return false;
    }

    @Override
    public boolean unregisterLobbyItemHandler(ILobbyItemHandler var1) {
        return false;
    }
}
