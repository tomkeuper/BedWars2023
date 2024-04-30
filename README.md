![Bedwars2023_Logo](https://github.com/tomkeuper/BedWars2023/assets/29728836/5979c0e8-1333-40a5-b43c-49ceabd06a57)

[![Discord](https://discordapp.com/api/guilds/760851292826107926/widget.png?style=shield)](https://discord.gg/kPaBGwhmjf) [![bStats](https://img.shields.io/bstats/servers/18317)](#)

As of March 2023 development has started on BedWars2023, a fork of the [BedWars1058](https://www.spigotmc.org/resources/bedwars1058-opensource.97320/) plugin. The goal of this fork is to implement new features and to modernize the codebase.

The plugin is published under the open-source GNU GPL 3.0 license. You can read the full license [here](https://www.gnu.org/licenses/gpl-3.0.html).

# Description
BedWars is a mini-game where you have to defend your bed and destroy the others.  
Once your bed is destroyed, you cannot respawn.

# System requirements
This software runs on [Spigot](https://www.spigotmc.org/) and NMS.
Spigot forks without compiled NMS code are not supported.
Officially supported servers are [Spigot](https://www.spigotmc.org/) and [Paper](https://papermc.io/).
You are required to use Java 11 or newer to run this plugin.

The internal world restore system is based on zipping and unzipping maps which can become
heavy if you are still making use of HDD in 2024, and you do not have a decent CPU. For a better
and faster restore system install [SlimeWorldManager](https://www.spigotmc.org/resources/slimeworldmanager.69974/) or [AdvancedWorldManager](https://www.spigotmc.org/resources/advanced-slimeworldmanager.87209/).
BedWars2023 will hook into it and do everything for you.

# Dependencies
- Java 11
- Spigot forks with NMS
- [TAB](https://github.com/NEZNAMY/TAB) plugin

# Basic Installation
- Download the latest release and it's dependency
- Put it in the plugins folder
- Restart the server
- Navigate to TAB's config folder and enable scoreboard

# Pre-made setups and community addons

You can find a list of pre-made setups and community addons [on the wiki](https://wiki.tomkeuper.com/docs/BedWars2023/addons)

# Main features

### Flexible | Ways you can run the plugin:
- **SHARED**: can run among other mini-games on the same spigot instance. Games will only be accessible via commands.
- **MULTIARENA**: will require an entire server instance for hosting the mini-game. It will protect the lobby world and games can be joined via commands, NPCs, signs and GUIs.
- **BUNGEE-LEGACY**: the old classic bungee mode where a game means an entire server instance. You'll be added to the game when joining the server. Arena status will be displayed as MOTD.
- **BUNGEE**: a brand new scalable bungee mode. It can host multiple arenas on the same server instance, clone and start new arenas when needed so other players can join. The server can be automatically restarted after a certain amount of games played. This will require installing [BedWarsProxy](https://www.spigotmc.org/resources/bedwarsproxy.66642/) on your lobby servers so players can join. And of course, you can run as many servers as you want in bungee mode.

### Language | Per player language system:
- Each player can receive messages, holograms, GUIs etc. in their desired language. /bw lang.
- You can either remove or add new languages.
- Team names, group names, shop contents and a lot more can be translated in your languages.
- Custom titles and subtitles for [starting countdown](https://wiki.tomkeuper.com/docs/BedWars2023/configuration/language-configuration#custom-title-sub-title-for-arena-countdown).

### Lobby removal | Optional:
The waiting-lobby inside the map can be removed once the game starts.

### Arena Groups | Customization:
- You can group arenas by type (4v4, 50v50). You can name them however you want.
- Groups can have custom scoreboard layouts, team upgrades, start items and custom generator settings.
- You can join maps by group: /bw join Solo, /bw gui Solo.

### Shop | Customization:
- You may configure quick-buy default items.
- You may add or remove categories.
- You may add new shop items or execute commands when bought.
- Permanent items are given after you re-spawn.
- Permanent items can be downgradable which will make you lose one tier per death.
- Items can have weight, so you can't buy a weaker item than your current one etc.
- Special items available: BedBug, Dream Defender, Egg Bridge, TNT Jump and Straight Fireball.
- Quick buy feature is available and is synced between nodes as well in bungee mode.

### Team Upgrades | Customization:
- You may have different team upgrades per arena group.
- You may either add and remove categories and contents.
- You may make upgrade elements that: enchant items, give potion effects (to team-mates/ base/ enemies when they enter the island), you can edit generator settings and change the dragons amount for the Sudden Death phase.
- You may add new traps that: disenchant-items (sword, armor, bow), give potion effects (team/ base/ enemies), remove potion effect when an enemy enters your island range and trigger commands.

### Ways to join an arena:

- Arena selector, which can be configured. /bw gui will display all arena groups while /bw gui Solo will show games from Solo groups and /bw gui Solo+4v4 will show games from Solo and 4v4.
- You can also join games via NPCs by installing Citizens.
- Join-signs are also available with status block.
- Commands can be used as well. /bw join random will bring you the most filled arena, while /bw join mapName will send you to the given arena and /bw join groupName+groupName2 will bring you on a map from the given groups.

### Arena Settings | Customization:
- You can set a custom display name used on signs, GUIs etc.
- Option to set the amount of min/ max players and team size.
- Toggle options for: allowing spectators, disabling generators for empty teams, disabling NPCs for empty teams, disabling internal drops management, bed holograms usage.
- Protection range for team-spawn and team NPCs.
- Island radius (for features like triggering traps and map) border radius.
- Instant kill on void based on Y coordinate.
- You can create as many teams as you want.
- You can allow map breaking like on a SkyWars game.
- You can toggle generator split.
- Custom game rules per map.
- Unlimited iron/ gold / emerald (this one can pe activated from upgrades) generators per team.

### Vip Kick | Privilege:
Players with `bw.vip` permission are able to join full arenas in starting phase. This will kick a player without `bw.vip` permission from that game.

### Player Statistics:
- The plugin does not provide top list holograms, but you can use ajLeaderboards or LeaderHeads for this, using the placeholders we provide.
- Players can see their stats using the internal stats GUI, which can be customized and accessed by /bw stats.

### Party System:
- We provide a basic and functional internal party system to play with your friends on the same team or arena.
- We also support Parties by AlessioDP and Party and Friends by Simonsator which could be a better solution if you are a large network.

### Anti AFK System:
Inactive players for more than 45 seconds can't pick up items from generators.

### Custom Join Items:
- You can add and remove items that you receive when you join the server (only on multi-arena) and the items you receive when you join a game in starting/ waiting phase or when you join as a spectator.
- Join items can execute commands.

### Map Restore System:
- The default restore adapter from BedWars2023 is based on un-loading the map, unzipping a backup and loading it again. This may be heavy for servers with cheap hardware. We recommend using gaming processors and a SSD.
- To improve performance we added support for SlimeWorldManager, which loads maps way faster with less performance impact thanks to its slime format. We really encourage you installing this plugin. No manual conversion is required. BedWars2023 will handle everything. Read how to install it here.
- You can also implement your own map adapter through the API.
- It may seem heavier than other plugins because we don't simply keep track of modified blocks. We need to restore the entire map because server owners can allow players to destroy the maps like on a SkyWars game. Regions like generators, NPCs and team spawns will be protected.

### Re-Join | Feature:
If you get disconnected, or if you leave a game (configurable) you can re-join it via command or by joining the server again. This is also available in bungee scalable mode.

### TNT Jump | Feature:
- Players are able to do tnt jump with configurable values.
- Players with tnt in their inventory have a red particle on their head (configurable).

### Season events:
- Halloween special. It is enabled automatically based on your machine timezone and will provide cool effects.

# 3rd party libraries
- [bStats](https://bstats.org/getting-started/include-metrics)
- [Commons IO](https://mvnrepository.com/artifact/commons-io/commons-io)
- [HikariCP](https://mvnrepository.com/artifact/com.zaxxer/HikariCP)
- [IridiumColorAPI](https://nexus.iridiumdevelopment.net/#browse/browse:maven-releases:com%2Firidium%2FIridiumColorAP)
- [SlimJar](https://github.com/slimjar/slimjar)
- [SLF4J](http://www.slf4j.org/)
- [Flow-NBT](https://repo.rapture.pw/#browse/browse:maven-releases:com%2Fflowpowered%2Fflow-nbt)
- [Jedis](https://github.com/redis/jedis)
- [CloudNet](https://cloudnetservice.eu/docs/3.3/api/start/)
- [H2](https://www.h2database.com/html/main.html)
- [VipFeatures](https://gitlab.com/andrei1058/VipFeatures)

# Contact
[![Discord Server](https://discordapp.com/api/guilds/760851292826107926/widget.png?style=banner3)](https://discord.gg/kPaBGwhmjf)
