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
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class WorldManager extends JavaPluginFeature {

	//TODO: Add world properties to enable/disable autoloading a world
	//TODO: Finish Safespawn
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
	private boolean useDeathWorld;
	private boolean generatePortalWorlds;
	
	HashMap<UUID, Properties> worldPropsMap = new HashMap<>();
	
	public static WorldManager self;
	
    @EventHandler
    public void onWorldInit(WorldInitEvent evt) {
    	if(safeSpawnMode){
    		Location spawn = WorldManagerHelper.getSafestSpawnPoint(evt.getWorld());
    		evt.getWorld().setSpawnLocation(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());
    		logInfo("Safe Spawn: Updated spawn location " + spawn);
    	}
    	Properties prop = WorldManagerHelper.saveWorldConfig(this, evt.getWorld());
    	worldPropsMap.put(evt.getWorld().getUID(), prop);
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent evt) {
    	if (evt.getTo() == null) {
    		Properties worldProps = worldPropsMap.getOrDefault(evt.getFrom().getWorld().getUID(), new Properties());
    		String worldName = null;
    		String replaceStr = "";
        	switch (evt.getCause()) {
    		case END_PORTAL:
    			worldName = (String) worldProps.get("endPortalDest");
    			break;
    		case NETHER_PORTAL:
    			worldName = (String) worldProps.get("netherPortalDest");
    			break;
        	}
        	if(evt.getCause() == TeleportCause.END_PORTAL || evt.getCause() == TeleportCause.NETHER_PORTAL) {
	        	World world = Bukkit.getWorld(worldName);
	        	if(world != null) {
	        		evt.setTo(evt.getPortalTravelAgent().findOrCreate(world.getSpawnLocation()));
	        	} else {
	        		logWarn(evt.getPlayer().getDisplayName() + " attempted to use a portal to \"" + worldName + "\", but no world with that name is loaded");
	        		evt.getPlayer().sendMessage(ChatColor.RED + "Sorry, this portal is broken");
	        	}
        	}
    	} else {
    		logInfo("Teleport World: " + evt.getTo().getWorld().getName() + "; Cause: " + evt.getCause().name());
    	}
    }
    
    @EventHandler
    public void onEntityPortal(EntityPortalEnterEvent evt) {
    	
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent evt) {
    	if (useDeathWorld && !evt.isBedSpawn()) {
    		World world = evt.getPlayer().getWorld();
    		evt.setRespawnLocation(world.getSpawnLocation());
    	}
    }
    
    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event){
    	WorldManagerHelper.saveWorldConfig(this, event.getWorld());
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
        useDeathWorld = getConfig().getBoolean(ConfigKeys.respawnInDeathWorld.name(), true);
        generatePortalWorlds = getConfig().getBoolean(ConfigKeys.generatePortalWorlds.name(), true);
        WorldManagerHelper.confirmTimeout = confirmWaitDuration;
        List<String> worldList = getConfig().getStringList(ConfigKeys.worlds.name());

        if (worldList != null) {
            for (File file : Bukkit.getWorldContainer().listFiles()) {
                if (WorldManagerHelper.isWorldFolder(file)) {
                    logInfo("Found world " + file.getName());
                    if(autoLoadWorlds || worldList.contains(file.getName())){
                    	WorldManagerHelper.loadWorld(this, file);
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
        getConfig().set(ConfigKeys.respawnInDeathWorld.name(), useDeathWorld);
        getConfig().set(ConfigKeys.generatePortalWorlds.name(), generatePortalWorlds);
        List<String> worldList = getConfig().getStringList(ConfigKeys.worlds.name());
        if(worldList == null){
        	List<String> list = Collections.emptyList();
        	getConfig().set(ConfigKeys.worlds.name(), list);
        }
    }
    
    @Override
    protected boolean startup() {
    	self = this;
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

	public boolean isGeneratePortalWorlds() {
		return generatePortalWorlds;
	}
}