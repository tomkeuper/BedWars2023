package com.tomkeuper.bedwars.api.items.handlers;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface IItemHandler {

    Collection<ILobbyItemHandler> getLobbyItemHandlers();

    @Nullable ILobbyItemHandler getLobbyItemHandler(String var1);

    boolean registerLobbyItemHandler(ILobbyItemHandler var1);

    boolean unregisterLobbyItemHandler(ILobbyItemHandler var1);

}
