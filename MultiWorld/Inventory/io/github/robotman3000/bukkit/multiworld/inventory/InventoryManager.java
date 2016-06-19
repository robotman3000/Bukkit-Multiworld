package io.github.robotman3000.bukkit.multiworld.inventory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;

//TODO: Add logging
public class InventoryManager extends JavaPluginFeature implements CommandExecutor {

    private final InventoryContainer invs = new InventoryContainer();

    private final Map<String, WorldGroup> worldGroups = new HashMap<>();

    public boolean seperateGamemodeInventories; // TODO: Implement this config option

    public InventoryManager() {
        setFeatureName("Inventory Manager");
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

    @Override
    public boolean initalize() {
        logInfo("Registering Command: inlist");
        getPlugin().getCommand("inlist").setExecutor(this);
        logInfo("Registering Event Handlers");
        for (Listener evt : getEventHandlers()) {
            getPlugin().getServer().getPluginManager().registerEvents(evt, getPlugin());
        }
        logInfo("Loading Config");
        ConfigurationSerialization.registerClass(BukkitInventory.class);
        loadConfig();
        return true;
    }

    private boolean listInvCommand(CommandSender sender, Command cmd, String label, String[] args) {
        invs.listInvCommand(sender, cmd, label, args);
        if (worldGroups.size() == 0) {
            sender.sendMessage(ChatColor.GOLD + " There are no world groups");
        } else {
            for (String str : worldGroups.keySet()) {
                WorldGroup group = worldGroups.get(str);
                sender.sendMessage("World Group (" + str + "): " + ChatColor.BLUE + group.getName()
                        + " " + ChatColor.GREEN + group.getWorlds());
            }
        }
        return true;
    }

    @Override
    protected void loadConfig() {
        invs.readInventoryConfig(getPlugin(), this);

        if (!getFeatureConfig().contains("groups")) {
            getFeatureConfig().createSection("groups");
        }
        // Load the world groups
        Set<String> groupKeys = getFeatureConfig().getConfigurationSection("groups").getKeys(false);

        for (String groupKey : groupKeys) {
            String path = "groups." + groupKey;
            List<String> worlds = getFeatureConfig().getStringList(path + ".worlds");
            logInfo("Group: " + groupKey + " has " + worlds);

            WorldGroup wGroup = new WorldGroup(groupKey, worlds);
            worldGroups.put(groupKey, wGroup);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        switch (cmd.getName()) {
        case "inlist":
            return listInvCommand(sender, cmd, label, args);
        default:
            sender.sendMessage("Command Error in the InventoryManager Class!!");
        }

        return false;

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent evt) {
        InventoryKey beforeKey = new InventoryKey(evt.getPlayer().getUniqueId(), getGroupName(evt
                .getFrom().getName()), evt.getPlayer().getGameMode());
        InventoryKey afterKey = new InventoryKey(evt.getPlayer().getUniqueId(), getGroupName(evt
                .getPlayer().getWorld().getName()), evt.getPlayer().getGameMode());

        if (!beforeKey.equals(afterKey)) { // If the keys don't match, which means that a change is
                                           // actually required, then continue
            if (invs.checkSanityForPlayer(evt.getPlayer(), 1)) {
                if (!invs.hasUsedInventory(beforeKey)) {
                    logInfo("World: Creating an inventory with the key: " + beforeKey
                            + " (What?? How does the players current inventory not exist?)");
                    invs.resetInventory(beforeKey);
                }

                if (!invs.hasUnusedInventory(afterKey)) {
                    logInfo("World: Creating an inventory with the key: " + afterKey);
                    invs.resetInventory(afterKey);
                }

                // logInfo("World: Unregistering Inventory");
                invs.unregisterInventory(beforeKey);
                if (invs.checkSanityForPlayer(evt.getPlayer(), 0)) {
                    InventoryContainer.zeroPlayerInventory(evt.getPlayer(),
                                                           afterKey.getGamemodeKey());
                    invs.registerInventory(afterKey);
                    // logInfo("World: So far so good...");
                    if (invs.checkSanityForPlayer(evt.getPlayer(), 1)) {
                        // logInfo("World: Success!!");
                        return;
                    }
                }
            }
            logSevere("World: Failed to register inventory for " + evt.getPlayer().getName());
        } else {
            logInfo("World: No change needed");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerGamemodeChanged(PlayerGameModeChangeEvent evt) {
        InventoryKey beforeKey = new InventoryKey(evt.getPlayer().getUniqueId(), getGroupName(evt
                .getPlayer().getWorld().getName()), evt.getPlayer().getGameMode());
        InventoryKey afterKey = new InventoryKey(evt.getPlayer().getUniqueId(), getGroupName(evt
                .getPlayer().getWorld().getName()), evt.getNewGameMode());

        if (!beforeKey.equals(afterKey)) {
            if (invs.checkSanityForPlayer(evt.getPlayer(), 1)) {
                if (!invs.hasUsedInventory(beforeKey)) {
                    logInfo("Gamemode: Creating an inventory with the key: " + beforeKey
                            + " (What?? How does the players current inventory not exist?)");
                    invs.resetInventory(beforeKey);
                }

                if (!invs.hasUnusedInventory(afterKey)) {
                    logInfo("Gamemode: Creating an inventory with the key: " + afterKey);
                    invs.resetInventory(afterKey);
                }

                // logInfo("Gamemode: Unregistering Inventory");
                invs.unregisterInventory(beforeKey);
                if (invs.checkSanityForPlayer(evt.getPlayer(), 0)) {
                    InventoryContainer.zeroPlayerInventory(evt.getPlayer(),
                                                           afterKey.getGamemodeKey());
                    invs.registerInventory(afterKey);
                    // logInfo("Gamemode: So far so good...");
                    if (invs.checkSanityForPlayer(evt.getPlayer(), 1)) {
                        // logInfo("Success!!");
                        return;
                    }
                }
            }
            logSevere("Gamemode: Failed to register inventory for " + evt.getPlayer().getName());
        } else {
            logInfo("Gamemode: No change needed");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent evt) {
        InventoryKey theKey = new InventoryKey(evt.getPlayer().getUniqueId(), getGroupName(evt
                .getPlayer().getWorld().getName()), evt.getPlayer().getGameMode());

        if (invs.checkSanityForPlayer(evt.getPlayer(), 0)) {
            if (!invs.hasUnusedInventory(theKey)) {
                logInfo("Join: Creating an inventory with the key: " + theKey);
                invs.resetInventory(theKey);
            }

            InventoryContainer.zeroPlayerInventory(evt.getPlayer(), theKey.getGamemodeKey());
            invs.registerInventory(theKey);
            // logInfo("Join: So far so good...");
            if (invs.checkSanityForPlayer(evt.getPlayer(), 1)) {
                // logInfo("Success!!");
                // We're done. Exit immediately.
                return;
            }
        }
        logSevere("Join: Failed to register inventory for " + evt.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLeave(PlayerQuitEvent evt) {
        InventoryKey theKey = new InventoryKey(evt.getPlayer().getUniqueId(), getGroupName(evt
                .getPlayer().getWorld().getName()), evt.getPlayer().getGameMode());

        if (invs.checkSanityForPlayer(evt.getPlayer(), 1)) {
            if (!invs.hasUsedInventory(theKey)) {
                logInfo("Leave: Creating an inventory with the key: " + theKey
                        + " (What?? Why did I have to create this inventory during an unregister?)");
                invs.resetInventory(theKey);
            }

            invs.unregisterInventory(theKey);
            InventoryContainer.zeroPlayerInventory(evt.getPlayer(), theKey.getGamemodeKey());
            // logInfo("Leave: So far so good...");
            if (invs.checkSanityForPlayer(evt.getPlayer(), 0)) {
                // logInfo("Success!!");
                return;
            }
        }
        logSevere("Leave: Failed to register inventory for " + evt.getPlayer().getName());
    }

    @Override
	public void saveConfig() {
        invs.saveInventoryConfig(getPlugin(), this);
        // Save the world groups and gamemode settings
        HashSet<String> knownWorlds = new HashSet<>();
        // HashSet<String> unconfiguredWorlds = new HashSet<>();
        for (String groupkey : worldGroups.keySet()) {
            String path = "groups." + groupkey;
            WorldGroup group = worldGroups.get(groupkey);
            getFeatureConfig().set(path + ".worlds", group.getWorlds());

            knownWorlds.addAll(group.getWorlds());
        }
    }

    @Override
    public void shutdown() {
        logInfo("Shutting Down...");
        saveConfig();
        ConfigurationSerialization.unregisterClass(BukkitInventory.class);
    }
    
	@Override
	public int getMinimumMajorVersion() {
		return 2;
	}

	@Override
	public int getMinimumMinorVersion() {
		return 0;
	}
}
