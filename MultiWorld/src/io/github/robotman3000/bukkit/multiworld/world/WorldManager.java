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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;

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

                        loadWorld(file);
                    } else {
                        // Only load world if in worldList
                        if (worldList.contains(file.getName())) {
                            logInfo("World is listed in config; Loading world " + file.getName());
                            loadWorld(file);
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

    private void loadWorld(File file) {
        WorldCreator creator = new WorldCreator(file.getName());
        Properties props = new Properties();
        File theFile = new File(file, "world.properties");
        if(!theFile.exists()){
        	try {
				props.store(new FileWriter(theFile), "Don't delete this file");
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        try {
			props.load(new FileReader(theFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        creator.seed(Long.valueOf(props.getProperty("seed", String.valueOf(1234567))));
        creator.type(WorldType.valueOf(props.getProperty("type", "NORMAL")));
        creator.environment(Environment.valueOf(props.getProperty("enviroment", "NORMAL")));
        creator.generateStructures(Boolean.valueOf(props.getProperty("generateStructures", String.valueOf(true))));
        
        World world = creator.createWorld();
        logInfo("Loaded World: " + world);
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
        
    	World world = evt.getWorld();
    	File folder = world.getWorldFolder();
    	Properties worldProps = new Properties();
    	worldProps.setProperty("seed", String.valueOf(world.getSeed()));
    	worldProps.setProperty("type", world.getWorldType().name());
    	worldProps.setProperty("enviroment", world.getEnvironment().name());
    	worldProps.setProperty("generateStructures", String.valueOf(world.canGenerateStructures()));
    	try {
			worldProps.store(new FileWriter(new File(folder, "world.properties")), "Don't delete this file!");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event){
    	World world = event.getWorld();
    	File folder = world.getWorldFolder();
    	Properties worldProps = new Properties();
    	worldProps.setProperty("seed", String.valueOf(world.getSeed()));
    	worldProps.setProperty("type", world.getWorldType().name());
    	worldProps.setProperty("enviroment", world.getEnvironment().name());
    	worldProps.setProperty("generateStructures", String.valueOf(world.canGenerateStructures()));
    	try {
			worldProps.store(new FileWriter(new File(folder, "world.properties")), "Don't delete this file!");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    @Override
    public void saveConfig() {
    	logInfo("Saving World Configuration");
        getFeatureConfig().set("autoLoadWorlds", autoLoadWorlds);
        List<String> worldList = getFeatureConfig().getStringList("worlds");
        if(worldList == null){
        	List<String> list = Collections.emptyList();
        	getFeatureConfig().set("worlds", list);
        }
        logInfo("Finished Saving Configuration");
    }

    @Override
    public void shutdown() {
        logInfo("Shutting Down...");
        saveConfig();
    }

}