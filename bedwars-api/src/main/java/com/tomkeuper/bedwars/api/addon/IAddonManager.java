
package com.tomkeuper.bedwars.api.addon;

import java.util.List;

/**
 * The manager responsible for handling addons.
 */
public interface IAddonManager {

    /**
     * Get the list of registered addons.
     *
     * @return The list of registered addons.
     */
    List<Addon> getAddons();

    /**
     * Get the list of loaded addons.
     *
     * @return The list of loaded addons.
     */
    List<Addon> getLoadedAddons();

    /**
     * Get the list of unloaded addons.
     *
     * @return The list of unloaded addons.
     */
    List<Addon> getUnloadedAddons();

    /**
     * Get the list of addons created by a specific author.
     *
     * @param author The author of the addons.
     * @return The list of addons created by the specified author.
     */
    List<Addon> getAddonsByAuthor(String author);

    /**
     * Load an addon.
     *
     * @param addon The addon to load.
     */
    void loadAddon(Addon addon);

    /**
     * Unload an addon.
     *
     * @param addon The addon to unload.
     */
    void unloadAddon(Addon addon);

    /**
     * Unload every addon
     */
    void unloadAddons();

    /**
     * Load every addon
     */
    void loadAddons();

    /**
     * Register an addon.
     *
     * @param addon The addon to register.
     */
    void registerAddon(Addon addon);
}
