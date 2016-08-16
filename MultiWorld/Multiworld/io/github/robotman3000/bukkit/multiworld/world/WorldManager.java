package io.github.robotman3000.bukkit.multiworld.world;

import io.github.robotman3000.bukkit.multiworld.world.command.WorldConfirmCommand;
import io.github.robotman3000.bukkit.multiworld.world.command.WorldCreateCommand;
import io.github.robotman3000.bukkit.multiworld.world.command.WorldDeleteCommand;
import io.github.robotman3000.bukkit.multiworld.world.command.WorldGameruleCommand;
import io.github.robotman3000.bukkit.multiworld.world.command.WorldGotoCommand;
import io.github.robotman3000.bukkit.multiworld.world.command.WorldInfoCommand;
import io.github.robotman3000.bukkit.multiworld.world.command.WorldListCommand;
import io.github.robotman3000.bukkit.multiworld.world.command.WorldLoadCommand;
import io.github.robotman3000.bukkit.multiworld.world.command.WorldPropertyCommand;
import io.github.robotman3000.bukkit.multiworld.world.command.WorldUnloadCommand;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginCommand;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginCommandList;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class WorldManager extends JavaPluginFeature {

    private enum Commands implements JavaPluginCommandList {
    	worldconfirm (new WorldConfirmCommand()),
        worldcreate (new WorldCreateCommand()),
        worlddelete (new WorldDeleteCommand()),
        worldload (new WorldLoadCommand()),
        worldunload (new WorldUnloadCommand()),
        worldinfo (new WorldInfoCommand()),
        worldlist (new WorldListCommand()),
        worldgamerule (new WorldGameruleCommand()),
        worldproperty (new WorldPropertyCommand()),
        worldgoto (new WorldGotoCommand());

    	private JavaPluginCommand command;
    	
    	private Commands(JavaPluginCommand obj) {
    		this.command = obj;
		}
    	
        @Override
        public CommandExecutor getExecutor(){
        	return command;
        }

        @Override
        public TabCompleter getTabCompleter(){
        	return command;
        }
    }

    private boolean autoLoadWorlds;
	private boolean safeSpawnMode;
	private long confirmWaitDuration;
	
    @EventHandler
    public void onWorldInit(WorldInitEvent evt) {
    	if(safeSpawnMode){
    		Location spawn = WorldManagerHelper.getSafestSpawnPoint(evt.getWorld());
    		evt.getWorld().setSpawnLocation(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());
    		logInfo("Safe Spawn: Updated spawn location " + spawn);
    	}
        WorldManagerHelper.saveWorldConfig(evt.getWorld());
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event){
    	WorldManagerHelper.saveWorldConfig(event.getWorld());
    }
    
    @EventHandler
    public void onPlayerChangedWorld(PlayerTeleportEvent event){
    	World from = event.getFrom().getWorld();
    	World to = event.getTo().getWorld();
    	
    	if(!from.getUID().equals(to.getUID())){
    		Player player = event.getPlayer();
    		
    		if(player.hasPermission("spigotplus.multiworld.leave." + from.getName())){
    			if(player.hasPermission("spigotplus.multiworld.enter." + to.getName())){
    				// We don't set the event to be "uncanceled" to avoid interfering with
    				// the work of other plugins, as we don't care what happens to the event
    				// provided that by our books it is allowed.
    				return;
    			} else {
    				player.sendMessage(ChatColor.RED + "You are not permitted to enter this world.");
    			}
    		} else {
    			player.sendMessage(ChatColor.RED + "You are not permitted to leave this world.");
    		}
    		//event.setTo(event.getFrom());
    		event.setCancelled(true);
    	}
    }
    
    public void loadConfig() {
        autoLoadWorlds = getConfig().getBoolean(ConfigKeys.autoLoadWorlds.name(), false);
        safeSpawnMode = getConfig().getBoolean(ConfigKeys.enableSafeSpawnMode.name(), true);
        confirmWaitDuration = getConfig().getLong(ConfigKeys.confirmTimeout.name(), 10);
        WorldManagerHelper.confirmTimeout = confirmWaitDuration;
        List<String> worldList = getConfig().getStringList(ConfigKeys.worlds.name());

        if (worldList != null) {
            for (File file : Bukkit.getWorldContainer().listFiles()) {
                if (WorldManagerHelper.isWorldFolder(file)) {
                    logInfo("Found world " + file.getName());
                    if(autoLoadWorlds || worldList.contains(file.getName())){
                    	WorldManagerHelper.loadWorld(file);
                    }
                }
            }
        }

        if (worldList == null || worldList.isEmpty()) {
            getConfig().set(ConfigKeys.worlds.name(), new ArrayList<String>());
        }
    }
    
    public void saveConfigValues() {
        getConfig().set(ConfigKeys.autoLoadWorlds.name(), autoLoadWorlds);
        getConfig().set(ConfigKeys.enableSafeSpawnMode.name(), safeSpawnMode);
        getConfig().set(ConfigKeys.confirmTimeout.name(), confirmWaitDuration);
        List<String> worldList = getConfig().getStringList(ConfigKeys.worlds.name());
        if(worldList == null){
        	List<String> list = Collections.emptyList();
        	getConfig().set(ConfigKeys.worlds.name(), list);
        }
    }
    
    @Override
    protected boolean startup() {
    	loadConfig();
    	return true;
    }

    @Override
    public void shutdown() {
        saveConfigValues();
    }

	@Override
	public int getRequiredMajorVersion() {
		return 1;
	}

	@Override
	public int getRequiredMinorVersion() {
		return 0;
	}

	@Override
	protected JavaPluginCommandList[] getCommands() {
		return Commands.values();
	}
}