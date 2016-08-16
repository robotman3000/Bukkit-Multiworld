package io.github.robotman3000.bukkit.multiworld.inventory;

import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginCommandList;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class InventoryManager extends JavaPluginFeature {

    private final InventoryContainer invs = new InventoryContainer(this);
    private final Map<String, WorldGroup> worldGroups = new HashMap<>();
    
    public enum Commands implements JavaPluginCommandList{
		;

		@Override
		public CommandExecutor getExecutor() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TabCompleter getTabCompleter() {
			// TODO Auto-generated method stub
			return null;
		}
    	
    }
    
    private String getGroupName(String name) {
        for (String worldGroupName : worldGroups.keySet()) {
            WorldGroup worldGroup = worldGroups.get(worldGroupName);
            if (worldGroup.getWorlds().contains(name)) {
                return "group_" + worldGroupName;
            }
        }
        return name;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent evt) {
        InventoryKey beforeKey = new InventoryKey(evt.getPlayer().getUniqueId(), getGroupName(evt
                .getFrom().getName()), evt.getPlayer().getGameMode());
        InventoryKey afterKey = new InventoryKey(evt.getPlayer().getUniqueId(), getGroupName(evt
                .getPlayer().getWorld().getName()), evt.getPlayer().getGameMode());
        
        if (!beforeKey.equals(afterKey)) { 
        	switchInventory(beforeKey, afterKey);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerGamemodeChanged(PlayerGameModeChangeEvent evt) {
        InventoryKey beforeKey = new InventoryKey(evt.getPlayer().getUniqueId(), getGroupName(evt
                .getPlayer().getWorld().getName()), evt.getPlayer().getGameMode());
        InventoryKey afterKey = new InventoryKey(evt.getPlayer().getUniqueId(), getGroupName(evt
                .getPlayer().getWorld().getName()), evt.getNewGameMode());

        if (!beforeKey.equals(afterKey)) {
        	switchInventory(beforeKey, afterKey);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent evt) {
        InventoryKey theKey = new InventoryKey(evt.getPlayer().getUniqueId(), getGroupName(evt
                .getPlayer().getWorld().getName()), evt.getPlayer().getGameMode());
        switchInventory(null, theKey);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLeave(PlayerQuitEvent evt) {
        InventoryKey theKey = new InventoryKey(evt.getPlayer().getUniqueId(), getGroupName(evt
                .getPlayer().getWorld().getName()), evt.getPlayer().getGameMode());
        switchInventory(theKey, null);
    }

    private void switchInventory(InventoryKey unregisterInv, InventoryKey registerInv){
    	if(unregisterInv != null){
    		invs.unregisterInventory(unregisterInv);
    	}
    	
    	if(registerInv != null){
    		invs.registerInventory(registerInv);
    	}
    }
    
    protected void loadConfig() {
        invs.readInventoryConfig();

        if (!getConfig().contains("groups")) {
            getConfig().createSection("groups");
        }
        // Load the world groups
        Set<String> groupKeys = getConfig().getConfigurationSection("groups").getKeys(false);

        for (String groupKey : groupKeys) {
            String path = "groups." + groupKey;
            List<String> worlds = getConfig().getStringList(path + ".worlds");
            logInfo("Group: " + groupKey + " has " + worlds);

            WorldGroup wGroup = new WorldGroup(groupKey, worlds);
            worldGroups.put(groupKey, wGroup);
        }
    }
    
	public void saveConfigValues() {
        invs.saveInventoryConfig();
        // Save the world groups and gamemode settings
        HashSet<String> knownWorlds = new HashSet<>();
        for (String groupkey : worldGroups.keySet()) {
            String path = "groups." + groupkey;
            WorldGroup group = worldGroups.get(groupkey);
            getConfig().set(path + ".worlds", group.getWorlds());
            knownWorlds.addAll(group.getWorlds());
        }
    }

    @Override
    public boolean startup() {
        ConfigurationSerialization.registerClass(PlayerInventory.class);
        loadConfig();
        return true;
    }
	
    @Override
    public void shutdown() {
        saveConfigValues();
        ConfigurationSerialization.unregisterClass(PlayerInventory.class);
    }
    
    @Override
    protected JavaPluginCommandList[] getCommands() {
    	return Commands.values();
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
