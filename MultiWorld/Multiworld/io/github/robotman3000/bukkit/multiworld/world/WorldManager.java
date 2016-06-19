package io.github.robotman3000.bukkit.multiworld.world;

import java.io.File;
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
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import io.github.robotman3000.bukkit.multiworld.world.command.GameruleCommand;
import io.github.robotman3000.bukkit.multiworld.world.command.GenerateWorldCommand;
import io.github.robotman3000.bukkit.multiworld.world.command.GotoCommand;
import io.github.robotman3000.bukkit.multiworld.world.command.ListWorldsCommand;
import io.github.robotman3000.bukkit.multiworld.world.command.UnloadWorldCommand;
import io.github.robotman3000.bukkit.multiworld.world.command.WorldCreateCommand;
import io.github.robotman3000.bukkit.multiworld.world.command.WorldDeleteCommand;
import io.github.robotman3000.bukkit.multiworld.world.command.WorldInfoCommand;
import io.github.robotman3000.bukkit.multiworld.world.command.WorldPropertyCommand;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginCommand;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;

public class WorldManager extends JavaPluginFeature {

    private enum Commands implements JavaPluginCommand {
        createworld (new WorldCreateCommand()),
        deleteworld (new WorldDeleteCommand()),
        generateworld (new GenerateWorldCommand()),
        unloadworld (new UnloadWorldCommand()),
        worldinfo (new WorldInfoCommand()),
        listworlds (new ListWorldsCommand()),
        gamerule (new GameruleCommand()),
        worldproperty (new WorldPropertyCommand()),
        Goto (new GotoCommand());

    	CommandExecutor command;
    	TabCompleter tabCompleter;
    	
    	private Commands(Object obj) {
			command = (CommandExecutor) obj;
			tabCompleter = (TabCompleter) obj;
		}
    	
        @Override
        public CommandExecutor getExecutor(){
        	return command;
        }

        @Override
        public TabCompleter getTabCompleter(){
        	return tabCompleter;
        }
    }

    public boolean autoLoadWorlds;
	private boolean appendWorldInChat;
	
	public WorldManager() {
		setFeatureName("World Manager");
	}

    @Override
    public void loadConfig() {
        logInfo("Loading World Configuration");
        appendWorldInChat = getFeatureConfig().getBoolean("appendWorldInChat", true);
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

                        WorldManagerHelper.loadWorld(file);
                    } else {
                        // Only load world if in worldList
                        if (worldList.contains(file.getName())) {
                            logInfo("World is listed in config; Loading world " + file.getName());
                            WorldManagerHelper.loadWorld(file);
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
    public void onPlayerChat(AsyncPlayerChatEvent evt) {
        if (appendWorldInChat) {
            String message = evt.getMessage();
            evt.setMessage("[" + evt.getPlayer().getWorld().getName() + "] " + message);
        }
    };
    
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
    	getFeatureConfig().set("appendWorldInChat", appendWorldInChat);
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
        saveConfig();
    }

	@Override
	public int getMinimumMajorVersion() {
		return 2;
	}

	@Override
	public int getMinimumMinorVersion() {
		return 1;
	}

	@Override
	protected JavaPluginCommand[] getCommands() {
		return Commands.values();
	}
}