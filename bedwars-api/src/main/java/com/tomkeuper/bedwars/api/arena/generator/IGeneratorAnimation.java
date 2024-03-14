package com.tomkeuper.bedwars.api.arena.generator;

public interface IGeneratorAnimation {

    /**
     * Run the animation of the generator
     * Should be used with repeating or scheduled tasks
     */
    void run();
}
