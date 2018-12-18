 package io.github.robotman3000.bukkit.multiworld.inventory;

import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class InventoryManager extends JavaPluginFeature {
	//TODO: Make it so that Creative mode in one world and Survival in another share an inventory
	//TODO: Make it so that if two worlds have the same name the inventories won't be shared
	
	// listgroups, addgroup, delgroup, setgroup, getgroup, renamegroup, addgamemode, delgamemode
	
    private final InventoryContainer invs = new InventoryContainer(this);
    private final Map<UUID, UUID> playerToGroup = new HashMap<>();
    private final Map<UUID, WorldGroup> worldGroups = new HashMap<>();
    
    public UUID getGroupKey(String worldName) {
        for (UUID worldGroupName : worldGroups.keySet()) {
            WorldGroup worldGroup = worldGroups.get(worldGroupName);
            for(Field field : worldGroup.getWorlds().getClass().getDeclaredFields()) {
                System.out.println(field.getGenericType().getTypeName());
            }
            for(WorldKey key : worldGroup.getWorlds()){
            	if(key.getWorldName().equals(worldName)){
            		return worldGroupName;
            	}
            }
        }
        return null;
    }

    private boolean gamemodeCheck(InventoryKey afterKey, GameMode gameMode) {
		WorldGroup group = worldGroups.get(afterKey.getWorldGroupKey());
		for(WorldKey key : group.getWorlds()){
			if(key.getGamemodes().contains(gameMode)){
				return true;
			}
		}
		return false;
	}
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent evt) {
        InventoryKey beforeKey = new InventoryKey(evt.getPlayer().getUniqueId(), getGroupKey(evt
                .getFrom().getName()));
        InventoryKey afterKey = new InventoryKey(evt.getPlayer().getUniqueId(), getGroupKey(evt
                .getPlayer().getWorld().getName()));
        
        if (!beforeKey.equals(afterKey) && gamemodeCheck(afterKey, evt.getPlayer().getGameMode())) { 
        	switchInventory(beforeKey, afterKey);
        }
    }

	@EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerGamemodeChanged(PlayerGameModeChangeEvent evt) {
        InventoryKey beforeKey = new InventoryKey(evt.getPlayer().getUniqueId(), getGroupKey(evt
                .getPlayer().getWorld().getName()));
        InventoryKey afterKey = new InventoryKey(evt.getPlayer().getUniqueId(), getGroupKey(evt
                .getPlayer().getWorld().getName()));

        if (!beforeKey.equals(afterKey) && gamemodeCheck(afterKey, evt.getPlayer().getGameMode())) {
        	switchInventory(beforeKey, afterKey);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent evt) {
        InventoryKey theKey = new InventoryKey(evt.getPlayer().getUniqueId(), getGroupKey(evt
                .getPlayer().getWorld().getName()));
        switchInventory(null, theKey);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLeave(PlayerQuitEvent evt) {
        InventoryKey theKey = new InventoryKey(evt.getPlayer().getUniqueId(), getGroupKey(evt
                .getPlayer().getWorld().getName()));
        switchInventory(theKey, null);
    }

    private void switchInventory(InventoryKey unregisterInv, InventoryKey registerInv){
    	if(unregisterInv != null){
    		try {
    			invs.unregisterInventory(unregisterInv);
    		} catch (Exception e) {
    			logSevere("Failed to unregister inventory for Player: " + unregisterInv.getPlayerKey() + " and WorldGroup: " + unregisterInv.getWorldGroupKey() + "; Reason: " + e.getMessage());
				e.printStackTrace();
    		}
    	}
    	
    	if(registerInv != null){
    		try {
    			invs.registerInventory(registerInv);
    		} catch (Exception e) {
    			logSevere("Failed to register inventory for Player: " + registerInv.getPlayerKey() + " and WorldGroup: " + registerInv.getWorldGroupKey() + "; Reason: " + e.getMessage());
				e.printStackTrace();
    		}
    	}
    }
    
    @SuppressWarnings("unchecked")
	protected void loadConfig() {
        if (!getConfig().contains("groups")) {
            getConfig().createSection("groups");
        }
      
        int confVer = getConfig().getInt("version", -1);
      
        // Load the world groups
        Set<String> groupKeys = getConfig().getConfigurationSection("groups").getKeys(false);

        boolean didUpgrade = false;
        for (String groupKey : groupKeys) {
            String path = "groups." + groupKey;
            List<?> keyFirst = (List<?>) getConfig().getList(path + ".worlds", Collections.EMPTY_LIST);
            
            List<WorldKey> key = (List<WorldKey>) keyFirst;
            // Upgrade old configs            
            UUID id = null;
            String name = null;
            try{
            	id = UUID.fromString(groupKey);
            	name = getConfig().getString(path + ".name");
            } catch (IllegalArgumentException e){
            	id = UUID.randomUUID();
            	name = groupKey;
            	logInfo("Upgrading group " + groupKey + " to the UUID storage format. UUID: " + id);
            	didUpgrade = true;

            	List<GameMode> gamemodes = new ArrayList<>();
            	for (GameMode gm : GameMode.values()) {
            		gamemodes.add(gm);
            	}
            	
            	// We create a WorldKey for each world listed 
            	List<WorldKey> upgradedKey = new ArrayList<>();
            	for(String worldName : (List<String>) keyFirst) {
            		World world = Bukkit.getWorld(worldName);
            		UUID worldID = null;
            		if(world == null) {
            			// Set id null to show not loaded
            			logWarn("Error: No world named \"" + worldName + "\" is currently loaded. Ignoring");
            		} else {
            			worldID = world.getUID();
            		}
              		upgradedKey.add(new WorldKey(worldName, worldID, gamemodes));
            	}
            	key = upgradedKey;
            }
           
            for(Field field : key.getClass().getDeclaredFields()) {
                System.out.println(field.getGenericType().getTypeName());
            }
            logInfo("Group: " + groupKey + " has " + key.toString());

            WorldGroup wGroup = new WorldGroup(name, id, key);
            worldGroups.put(id, wGroup);
            
        }
        if(didUpgrade) {
        	// We clear the old config file and later the upgraded values will be saved
        	getConfig().createSection("groups");
        }
        
        if(worldGroups.isEmpty()){
        	logInfo("No configuration was found. Generating default config");
        	List<WorldKey> worlds = new ArrayList<>();
        	for(World world : Bukkit.getWorlds()){
        		worlds.add(new WorldKey(world.getName(), world.getUID(), Arrays.asList(Bukkit.getDefaultGameMode())));
        	}
        	UUID id2 = UUID.nameUUIDFromBytes("default".getBytes());
        	worldGroups.put(id2, new WorldGroup("default", id2, worlds));
        }
        
        getConfig().set("version", 2);
        invs.readInventoryConfig();
    }
    
	public void saveConfigValues() {
        invs.saveInventoryConfig();
        // Save the world groups and gamemode settings
        for (UUID groupkey : worldGroups.keySet()) {
            String path = "groups." + groupkey;
            WorldGroup group = worldGroups.get(groupkey);
            getConfig().set(path + ".worlds", group.getWorlds());
            getConfig().set(path + ".name", group.getName());
        }
        getConfig().set("version", 2);
    }

    @Override
    public boolean startup() {
        ConfigurationSerialization.registerClass(PlayerInventory.class);
        ConfigurationSerialization.registerClass(WorldKey.class);
        loadConfig();
        return true;
    }
	
    @Override
    public void shutdown() {
        saveConfigValues();
        ConfigurationSerialization.unregisterClass(PlayerInventory.class);
        ConfigurationSerialization.unregisterClass(WorldKey.class);
    }
    
	@Override
	public int getRequiredMajorVersion() {
		return 1;
	}

	@Override
	public int getRequiredMinorVersion() {
		return 0;
	}
}
