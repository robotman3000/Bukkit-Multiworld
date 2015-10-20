package io.github.robotman3000.bukkit.multiworld.inventory;

import io.github.robotman3000.bukkit.multiworld.MultiWorld;
import io.github.robotman3000.bukkit.multiworld.inventory.BukkitInventories.InventoryResult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

//TODO: Add logging
public class InventoryManager implements CommandExecutor, Listener {
    // TODO: make this private
    public final String[] commands = { "inlist" };
    private final BukkitInventories invs = new BukkitInventories();
    private final MultiWorld plugin;

    private final Map<String, WorldGroup> worldGroups = new HashMap<>();

    public boolean seperateGamemodeInventories;
    public boolean teleportOnSwich; // TODO: Add this feature; Implement it the same way as
                                    // forceGamemode is done

    public InventoryManager(MultiWorld multiWorld) {
        plugin = multiWorld;
    }

    private String getGroupForWorld(String world) {
        for (String str : worldGroups.keySet()) {
            WorldGroup group = worldGroups.get(str);
            if (group.getWorlds().contains(world)) {
                return "group_" + str;
            }
        }
        return world;
    }

    private InventoryKey getNewInventoryKey(InventoryKey beforeState, InventoryKey afterState,
            Player player) {
        InventoryKey updatedState = new InventoryKey(player.getUniqueId().toString(),
                afterState.getWorldKey(), afterState.getGamemodeKey().toString(), null);
        if (!seperateGamemodeInventories) {
            // We hardcode survival mode so that the matching inventory will be consistent whenever
            // the gamemode doesn't matter
            updatedState = new InventoryKey(updatedState.getPlayerKey().toString(),
                    updatedState.getWorldKey(), GameMode.SURVIVAL.toString(), null);
        }

        String beforeWorldGroup = getGroupForWorld(beforeState.getWorldKey());
        String afterWorldGroup = getGroupForWorld(afterState.getWorldKey());
        if (beforeWorldGroup.equalsIgnoreCase(afterWorldGroup)) {
            // Bukkit.getLogger().info("World Groups Matched!!");
            // WorldGroup group = worldGroups.get(afterWorldGroup);

            String gamemode = updatedState.getGamemodeKey().toString();
            String gplayer = updatedState.getPlayerKey().toString();
            updatedState = new InventoryKey(gplayer, afterWorldGroup, gamemode, null);
        }
        return updatedState;
    }

    private boolean listInvCommand(CommandSender sender, Command cmd, String label, String[] args) {
        invs.listInvCommand(sender, cmd, label, args);
        if (worldGroups.size() == 0) {
            sender.sendMessage(ChatColor.GOLD + " There are no world groups");
        } else {
            for (String str : worldGroups.keySet()) {
                WorldGroup group = worldGroups.get(str);
                sender.sendMessage("World Group (" + str + "): " + ChatColor.BLUE + group.getName()
                        + " " + ChatColor.GREEN + group.getWorlds() + " " + ChatColor.YELLOW
                        + group.getGamemode());
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
            String gamemodeStr = plugin.getConfig().getString(path + ".gamemode");
            Bukkit.getLogger().warning(gamemodeStr);
            GameMode gamemode = Bukkit.getDefaultGameMode();
            try {
                gamemode = GameMode.valueOf(gamemodeStr);
            } catch (Exception e) {
            }

            WorldGroup wGroup = new WorldGroup(groupKey, gamemode, worlds);
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

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent evt) {
        InventoryKey beforeState = new InventoryKey(evt.getPlayer().getUniqueId().toString(),
                getGroupForWorld(evt.getFrom().getName()),
                evt.getPlayer().getGameMode().toString(), null);
        InventoryKey afterState = new InventoryKey(evt.getPlayer().getUniqueId().toString(),
                getGroupForWorld(evt.getPlayer().getWorld().getName()), evt.getPlayer()
                        .getGameMode().toString(), null);
        swapInventory(beforeState, afterState, evt.getPlayer(), null);
    }

    @EventHandler
    public void onPlayerGamemodeChanged(PlayerGameModeChangeEvent evt) {
        InventoryKey beforeState = new InventoryKey(evt.getPlayer().getUniqueId().toString(),
                getGroupForWorld(evt.getPlayer().getWorld().getName()), evt.getPlayer()
                        .getGameMode().toString(), null);
        InventoryKey afterState = new InventoryKey(evt.getPlayer().getUniqueId().toString(),
                getGroupForWorld(evt.getPlayer().getWorld().getName()), evt.getNewGameMode()
                        .toString(), null);
        swapInventory(beforeState, afterState, evt.getPlayer(), null);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) {
        InventoryKey playerState = new InventoryKey(evt.getPlayer().getUniqueId().toString(),
                getGroupForWorld(evt.getPlayer().getWorld().getName()), evt.getPlayer()
                        .getGameMode().toString(), null);
        swapInventory(playerState, playerState, evt.getPlayer(), Boolean.TRUE);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent evt) {
        InventoryKey playerState = new InventoryKey(evt.getPlayer().getUniqueId().toString(),
                getGroupForWorld(evt.getPlayer().getWorld().getName()), evt.getPlayer()
                        .getGameMode().toString(), null);
        swapInventory(playerState, playerState, evt.getPlayer(), Boolean.FALSE);
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
        /*		for(World world : Bukkit.getWorlds()){
        	if(!knownWorlds.contains(world.getName())){
        		unconfiguredWorlds.add(world.getName());
        	}
        }
        if(unconfiguredWorlds.size() > 0){ // if we need a default section this will be greater than zero
        	Bukkit.getLogger().warning("InventoryManager: Auto adding worlds with no explicitly assigned group to default");
        	String path = "inventory.groups.default";
        	if(!plugin.getConfig().contains(path)){
        		// If it doesn't exist than make it exist
        		plugin.getConfig().createSection(path);
        		plugin.getConfig().set(path + ".gamemode", Bukkit.getDefaultGameMode().toString());
        	}

        	List<String> worlds = plugin.getConfig().getStringList(path + ".worlds");
        	for(String str : unconfiguredWorlds){
        		if(!worlds.contains(str)){
        			worlds.add(str);
        		}
        	}
        	plugin.getConfig().set(path + ".worlds", worlds);
        }*/

    }

    private void swapInventory(InventoryKey beforeState, InventoryKey afterState, Player player,
            Boolean registerOnly) {
        Bukkit.getLogger().info("Swapping Inventory");
        // If registerOnly is null it means not applicable
        // Be careful about which PlayerState to use because it matters
        InventoryKey updatedState = getNewInventoryKey(beforeState, afterState, player);
        if (beforeState.equals(updatedState)) {
            Bukkit.getLogger().info("No swap needed");
            return;
        }
        String message = "InventoryManager: Your inventory failed to be ";
        InventoryResult result = InventoryResult.FAILED;

        if (registerOnly == null || registerOnly == Boolean.FALSE) {
            Bukkit.getLogger().info("Unreg Inventory");
            result = invs.unregisterInventory(beforeState, updatedState, player);

            if (result == InventoryResult.FAILED) {
                player.kickPlayer(message + "unregistered");
            }

            if (registerOnly == Boolean.FALSE) {
                return;
            }
        }

        invs.checkInventoryForEvent(updatedState, player);
        if (registerOnly == null || registerOnly == Boolean.TRUE) {
            Bukkit.getLogger().info("Reg Inventory");
            result = invs.registerInventory(beforeState, updatedState, player);
            if (result == InventoryResult.FAILED) {
                player.kickPlayer(message + "registered");
            }
        }
    }
}
