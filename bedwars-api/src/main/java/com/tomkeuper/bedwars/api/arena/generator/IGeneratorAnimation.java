package com.tomkeuper.bedwars.api.arena.generator;

import org.bukkit.plugin.Plugin;

public interface IGeneratorAnimation {

    /**
     * Get the identifier of the animation
     *
     * @return the identifier of the animation
     */
    String getIdentifier();

    /**
     * Get the plugin that is responsible for this animation
     *
     * @return the plugin that is responsible for this animation
     */
    Plugin getPlugin();

    /**
     * Run the animation of the generator
     * Should be used with repeating or scheduled tasks
     */
    void run();
}
