package io.github.robotman3000.bukkit.multiworld.world;

import io.github.robotman3000.bukkit.multiworld.SpigotPlus;
import io.github.robotman3000.bukkit.multiworld.world.command.GameruleCommand;
import io.github.robotman3000.bukkit.multiworld.world.command.GenerateWorldCommand;
import io.github.robotman3000.bukkit.multiworld.world.command.ListWorldsCommand;
import io.github.robotman3000.bukkit.multiworld.world.command.UnloadWorldCommand;
import io.github.robotman3000.bukkit.multiworld.world.command.WorldCreateCommand;
import io.github.robotman3000.bukkit.multiworld.world.command.WorldDeleteCommand;
import io.github.robotman3000.bukkit.multiworld.world.command.WorldInfoCommand;
import io.github.robotman3000.bukkit.multiworld.world.command.WorldPropertyCommand;
import io.github.robotman3000.bukkit.spigotplus.api.CommandEnumMethods;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.WorldInitEvent;

public class WorldManager extends JavaPluginFeature<SpigotPlus> {

    private enum Commands implements CommandEnumMethods<SpigotPlus> {
        createworld {
            @Override
            public CommandExecutor getExecutor(SpigotPlus plugin) {
                return new WorldCreateCommand();
            }

            @Override
            public TabCompleter getTabCompleter(SpigotPlus plugin) {
                return new WorldCreateCommand();
            }
        },
        deleteworld {
            @Override
            public CommandExecutor getExecutor(SpigotPlus plugin) {
                return new WorldDeleteCommand();
            }

            @Override
            public TabCompleter getTabCompleter(SpigotPlus plugin) {
                return new WorldDeleteCommand();
            }
        },
        generateworld {
            @Override
            public CommandExecutor getExecutor(SpigotPlus plugin) {
                return new GenerateWorldCommand();
            }

            @Override
            public TabCompleter getTabCompleter(SpigotPlus plugin) {
                return new GenerateWorldCommand();
            }
        },
        unloadworld {
            @Override
            public CommandExecutor getExecutor(SpigotPlus plugin) {
                return new UnloadWorldCommand();
            }

            @Override
            public TabCompleter getTabCompleter(SpigotPlus plugin) {
                return new UnloadWorldCommand();
            }
        },
        worldinfo {
            @Override
            public CommandExecutor getExecutor(SpigotPlus plugin) {
                return new WorldInfoCommand();
            }

            @Override
            public TabCompleter getTabCompleter(SpigotPlus plugin) {
                return new WorldInfoCommand();
            }
        },
        listworlds {

            @Override
            public CommandExecutor getExecutor(SpigotPlus plugin) {
                return new ListWorldsCommand();
            }

            @Override
            public TabCompleter getTabCompleter(SpigotPlus plugin) {
                return new ListWorldsCommand();
            }
        },
        gamerule {
            @Override
            public CommandExecutor getExecutor(SpigotPlus plugin) {
                return new GameruleCommand();
            }

            @Override
            public TabCompleter getTabCompleter(SpigotPlus plugin) {
                return new GameruleCommand();
            }
        },
        worldproperty {
            @Override
            public CommandExecutor getExecutor(SpigotPlus plugin) {
                return new WorldPropertyCommand();
            }

            @Override
            public TabCompleter getTabCompleter(SpigotPlus plugin) {
                return new WorldPropertyCommand();
            }
        };

        @Override
        public abstract CommandExecutor getExecutor(SpigotPlus plugin);

        @Override
        public abstract TabCompleter getTabCompleter(SpigotPlus plugin);
    }

    public boolean autoLoadWorlds;

    public WorldManager(SpigotPlus plugin) {
        super(plugin, "World Manager");
    }

    @Override
    public void initalize() {
        logInfo("Initializing...");
        for (Commands cmd : Commands.values()) { // Register Commands
            logInfo("Registering Command: " + cmd);
            PluginCommand pCmd = getPlugin().getCommand(cmd.name());
            pCmd.setExecutor(cmd.getExecutor(getPlugin()));
            pCmd.setTabCompleter(cmd.getTabCompleter(getPlugin()));
        }

        logInfo("Registering Event Handlers");
        for (Listener evt : getEventHandlers()) {
            getPlugin().getServer().getPluginManager().registerEvents(evt, getPlugin());
        }
        logInfo("Loading Config");
        loadConfig();
    }

    @Override
    public void loadConfig() {
        logInfo("Loading World Configuration");
        autoLoadWorlds = getFeatureConfig().getBoolean("autoLoadWorlds", false);
        List<String> worldList = getFeatureConfig().getStringList("worlds");

        if (worldList != null) {
            for (File file : Bukkit.getWorldContainer().listFiles()) {
                skipWorld: if (WorldManagerHelper.isWorldFolder(file)) {
                    logInfo("Found world " + file.getName());
                    if (autoLoadWorlds) {
                        if (worldList.contains(file.getName())) { // If pending world is in list
                                                                  // then
                                                                  // skip loading it
                            logInfo("World is listed in config; Skipping world " + file.getName());
                            break skipWorld;
                        }
                        World world = Bukkit.createWorld(new WorldCreator(file.getName()));
                        logInfo("Loaded World: " + world);
                    } else {
                        // Only load world if in worldList
                        if (worldList.contains(file.getName())) {
                            logInfo("World is listed in config; Loading world " + file.getName());
                            World world = Bukkit.createWorld(new WorldCreator(file.getName()));
                            logInfo("Loaded World: " + world);
                        }
                    }
                }
            }
        }
        if (worldList.size() == 0) {
            getFeatureConfig().set("worlds", new ArrayList<String>());
        }
        logInfo("Finished Loading Configuration");
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent evt) {
        // TODO: Make this more persistent in finding a safe spawn location
        if (!evt.isBedSpawn()) {
            Location loc = WorldManagerHelper.isLocationSafe(evt.getRespawnLocation());
            if (loc != null) {
                evt.setRespawnLocation(loc);
            } else {
                evt.getPlayer()
                        .sendMessage(ChatColor.RED
                                             + "Warning: Unable to find safe spawn location. You may spawn in a block");
            }
        }
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent evt) {
        Location loc = WorldManagerHelper.isLocationSafe(evt.getWorld().getSpawnLocation());
        if (loc != null) {
            if (!evt.getWorld().getSpawnLocation().equals(loc)) {
                logInfo("Spawn location of " + evt.getWorld().getName() + " wasn't safe");
                logInfo("Spawn location was " + evt.getWorld().getSpawnLocation());
                logInfo("Spawn changed to " + loc);
                evt.getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            }
        }
    }

    @Override
    public void saveConfig() {
        logInfo("Saving World Configuration");
        getFeatureConfig().set("autoLoadWorlds", autoLoadWorlds);
        logInfo("Finished Saving Configuration");
    }

    @Override
    public void shutdown() {
        logInfo("Shutting Down...");
        saveConfig();
    }

}