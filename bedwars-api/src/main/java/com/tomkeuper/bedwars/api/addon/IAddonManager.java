
package com.tomkeuper.bedwars.api.addon;

import java.util.List;

public interface IAddonManager {
    /**
     * Get the registered addons
     */
    List<Addon> getAddons();

    /**
     * Get the loaded addons
     */
    List<Addon> getLoadedAddons();

    /**
     * Get the unloaded addons
     */
    List<Addon> getUnloadedAddons();

    /**
     * Get the addons made by a specific author
     * @param author - Input the author of the addon you want to get
     */
    List<Addon> getAddonsByAuthor(String author);

    /**
     * Load an addon
     * @param addon - Load a specific addon
     */
    void loadAddon(Addon addon);

    /**
     * Unload an addon
     * @param addon - Unload a specific addon
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

    void registerAddon(Addon addon);
}