package com.tomkeuper.bedwars.utils;

import com.tomkeuper.bedwars.BedWars;
import io.github.slimjar.logging.ProcessLogger;

import java.text.MessageFormat;

public class SlimLogger implements ProcessLogger {
    private final BedWars plugin;

    public SlimLogger(BedWars plugin) {
        this.plugin = plugin;
    }

    @Override
    public void log(String message, Object... args) {

        plugin.getLogger().info(MessageFormat.format(message, args));
    }

    @Override
    public void debug(String message, Object... args) {
        ProcessLogger.super.debug(message, args);
    }
}
