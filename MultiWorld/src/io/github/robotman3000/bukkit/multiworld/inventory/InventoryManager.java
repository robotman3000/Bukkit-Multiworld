package io.github.robotman3000.bukkit.multiworld.inventory;

import io.github.robotman3000.bukkit.multiworld.MultiWorld;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

//TODO: Add logging
public class InventoryManager implements CommandExecutor, Listener {
    // TODO: make this private
    public final String[] commands = { "inlist" };
    private final InventoryContainer invs = new InventoryContainer();
    private final MultiWorld plugin;

    private final Map<String, WorldGroup> worldGroups = new HashMap<>();

    public boolean seperateGamemodeInventories;
    public boolean teleportOnSwich; // TODO: Add this feature; Implement it the same way as
                                    // forceGamemode is done

    public InventoryManager(MultiWorld multiWorld) {
        plugin = multiWorld;
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

    public void loadInventoryConfig() {
        invs.readInventoryConfig(plugin);

        // Load the world groups
        Set<String> groupKeys = plugin.getConfig().getConfigurationSection("inventory.groups")
                .getKeys(false);
        for (String groupKey : groupKeys) {
            String path = "inventory.groups." + groupKey;
            List<String> worlds = plugin.getConfig().getStringList(path + ".worlds");
            Bukkit.getLogger().warning("Worlds " + path + " " + worlds);

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
                    Bukkit.getLogger()
                            .info("[SpigotPlus] World: Creating an inventory with the key: "
                                          + beforeKey
                                          + " (What?? How does the players current inventory not exist?)");
                    invs.resetInventory(beforeKey);
                }

                if (!invs.hasUnusedInventory(afterKey)) {
                    Bukkit.getLogger()
                            .info("[SpigotPlus] World: Creating an inventory with the key: "
                                          + afterKey);
                    invs.resetInventory(afterKey);
                }

                // Bukkit.getLogger().info("[SpigotPlus] World: Unregistering Inventory");
                invs.unregisterInventory(beforeKey);
                if (invs.checkSanityForPlayer(evt.getPlayer(), 0)) {
                    InventoryContainer.zeroPlayerInventory(evt.getPlayer(),
                                                           afterKey.getGamemodeKey());
                    invs.registerInventory(afterKey);
                    // Bukkit.getLogger().info("[SpigotPlus] World: So far so good...");
                    if (invs.checkSanityForPlayer(evt.getPlayer(), 1)) {
                        // Bukkit.getLogger().info("[SpigotPlus] World: Success!!");
                        return;
                    }
                }
            }
            Bukkit.getLogger().severe("[SpigotPlus] World: Failed to register inventory for "
                                              + evt.getPlayer().getName());
        } else {
            Bukkit.getLogger().info("[SpigotPlus] World: No change needed");
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
                    Bukkit.getLogger()
                            .info("[SpigotPlus] Gamemode: Creating an inventory with the key: "
                                          + beforeKey
                                          + " (What?? How does the players current inventory not exist?)");
                    invs.resetInventory(beforeKey);
                }

                if (!invs.hasUnusedInventory(afterKey)) {
                    Bukkit.getLogger()
                            .info("[SpigotPlus] Gamemode: Creating an inventory with the key: "
                                          + afterKey);
                    invs.resetInventory(afterKey);
                }

                // Bukkit.getLogger().info("[SpigotPlus] Gamemode: Unregistering Inventory");
                invs.unregisterInventory(beforeKey);
                if (invs.checkSanityForPlayer(evt.getPlayer(), 0)) {
                    InventoryContainer.zeroPlayerInventory(evt.getPlayer(),
                                                           afterKey.getGamemodeKey());
                    invs.registerInventory(afterKey);
                    // Bukkit.getLogger().info("[SpigotPlus] Gamemode: So far so good...");
                    if (invs.checkSanityForPlayer(evt.getPlayer(), 1)) {
                        // Bukkit.getLogger().info("[SpigotPlus] Success!!");
                        return;
                    }
                }
            }
            Bukkit.getLogger().severe("[SpigotPlus] Gamemode: Failed to register inventory for "
                                              + evt.getPlayer().getName());
        } else {
            Bukkit.getLogger().info("[SpigotPlus] Gamemode: No change needed");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent evt) {
        InventoryKey theKey = new InventoryKey(evt.getPlayer().getUniqueId(), getGroupName(evt
                .getPlayer().getWorld().getName()), evt.getPlayer().getGameMode());

        if (invs.checkSanityForPlayer(evt.getPlayer(), 0)) {
            if (!invs.hasUnusedInventory(theKey)) {
                Bukkit.getLogger().info("[SpigotPlus] Join: Creating an inventory with the key: "
                                                + theKey);
                invs.resetInventory(theKey);
            }

            InventoryContainer.zeroPlayerInventory(evt.getPlayer(), theKey.getGamemodeKey());
            invs.registerInventory(theKey);
            // Bukkit.getLogger().info("[SpigotPlus] Join: So far so good...");
            if (invs.checkSanityForPlayer(evt.getPlayer(), 1)) {
                // Bukkit.getLogger().info("[SpigotPlus] Success!!");
                // We're done. Exit immediately.
                return;
            }
        }
        Bukkit.getLogger().severe("[SpigotPlus] Join: Failed to register inventory for "
                                          + evt.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLeave(PlayerQuitEvent evt) {
        InventoryKey theKey = new InventoryKey(evt.getPlayer().getUniqueId(), getGroupName(evt
                .getPlayer().getWorld().getName()), evt.getPlayer().getGameMode());

        if (invs.checkSanityForPlayer(evt.getPlayer(), 1)) {
            if (!invs.hasUsedInventory(theKey)) {
                Bukkit.getLogger()
                        .info("[SpigotPlus] Leave: Creating an inventory with the key: "
                                      + theKey
                                      + " (What?? Why did I have to create this inventory during an unregister?)");
                invs.resetInventory(theKey);
            }

            invs.unregisterInventory(theKey);
            InventoryContainer.zeroPlayerInventory(evt.getPlayer(), theKey.getGamemodeKey());
            // Bukkit.getLogger().info("[SpigotPlus] Leave: So far so good...");
            if (invs.checkSanityForPlayer(evt.getPlayer(), 0)) {
                // Bukkit.getLogger().info("[SpigotPlus] Success!!");
                return;
            }
        }
        Bukkit.getLogger().severe("[SpigotPlus] Leave: Failed to register inventory for "
                                          + evt.getPlayer().getName());
    }

    public void saveInventoryConfig() {
        invs.saveInventoryConfig(plugin);
        // Save the world groups and gamemode settings
        HashSet<String> knownWorlds = new HashSet<>();
        // HashSet<String> unconfiguredWorlds = new HashSet<>();
        for (String groupkey : worldGroups.keySet()) {
            String path = "inventory.groups." + groupkey;
            WorldGroup group = worldGroups.get(groupkey);
            plugin.getConfig().createSection(path);
            plugin.getConfig().set(path + ".worlds", group.getWorlds());

            knownWorlds.addAll(group.getWorlds());
        }
    }

}
