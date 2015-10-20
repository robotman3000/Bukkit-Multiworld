package io.github.robotman3000.bukkit.multiworld;

import io.github.robotman3000.bukkit.multiworld.gamemode.GamemodeManager;
import io.github.robotman3000.bukkit.multiworld.inventory.BukkitInventory;
import io.github.robotman3000.bukkit.multiworld.inventory.InventoryManager;
import io.github.robotman3000.bukkit.multiworld.world.WorldManager;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MultiWorld extends JavaPlugin implements Listener {

    WorldManager worlds = new WorldManager(this);
    InventoryManager inventories = new InventoryManager(this);
    GamemodeManager gamemodes = new GamemodeManager(this);

    private boolean appendWorldInChat;

    private boolean gotoCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(args.length >= 1)) {
            sender.sendMessage(ChatColor.RED + "You must provide a world name");
            return false;
        }
        World world = Bukkit.getServer().getWorld(args[0]);
        if (world != null) {
            sender.sendMessage("Teleporting you to world " + args[0]);
            Location loc = world.getSpawnLocation();
            if (!(sender instanceof Player) && args.length < 2) {
                sender.sendMessage(ChatColor.RED + "You must be a player to teleport");
                return false;
            }
            Player thePlayer;
            if (args.length > 1) {
                thePlayer = Bukkit.getPlayer(args[1]);
            } else {
                thePlayer = (Player) sender;
            }
            thePlayer.teleport(loc);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        switch (cmd.getName()) {
        case "goto":
            return gotoCommand(sender, cmd, label, args);
        default:
            sender.sendMessage(ChatColor.RED
                    + "Nope, I got nothing; Something went very wrong; CMD was: " + cmd.getName());
            if (args.length > 0) {
                sender.sendMessage(ChatColor.RED + "Command Args are as follows");
                for (String str : args) {
                    sender.sendMessage(ChatColor.RED + "Command Arg: " + str);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "There were no arguments");
            }
            break;
        }
        return true;
    }

    @Override
    public void onDisable() {
        // Reminder: Don't assume that this is only called on a server restart
        worlds.saveWorldConfig();
        inventories.saveInventoryConfig();
        gamemodes.saveGamemodeConfig();
        saveConfig();
    }

    @Override
    public void onEnable() {
        // Reminder: Don't assume that this is only called on a server restart
        ConfigurationSerialization.registerClass(BukkitInventory.class);
        saveDefaultConfig();

        /*	    this.getConfig().get("multiworld.enableTeleportManagement");
        		this.getConfig().get("teleport.handleNetherPortals");
        		this.getConfig().get("teleport.handleEndPortals");
        		this.getConfig().get("teleport.enablePortalDestOverride");
        		this.getConfig().get("teleport.allowSignPortals");*/

        // Base Plugin
        getServer().getPluginManager().registerEvents(this, this);
        appendWorldInChat = getConfig().getBoolean("multiworld.appendWorldInChat");

        // World Manager
        if (getConfig().getBoolean("multiworld.enableWorldManagement")) {
            Bukkit.getLogger().info("[MultiWorld] Initializing World Manager");
            for (String str : worlds.commands) { // Register Commands
                Bukkit.getLogger().info("[MultiWorld] Registering World Manager Command: " + str);
                getCommand(str).setExecutor(worlds);
            }

            worlds.autoLoadWorlds = getConfig().getBoolean("world.autoLoadWorlds");

            Bukkit.getLogger().info("[MultiWorld] Registering World Manager Event Handlers");
            getServer().getPluginManager().registerEvents(worlds, this); // Register the world event
                                                                         // handlers
            worlds.loadWorldConfig(); // Load the worlds list
        }

        // Inventory Manager
        if (getConfig().getBoolean("multiworld.enableInventoryManagement")) {
            Bukkit.getLogger().info("[MultiWorld] Initializing Inventory Manager");
            for (String str : inventories.commands) {
                Bukkit.getLogger().info("[MultiWorld] Registering Inventory Manager Command: "
                                                + str);
                getCommand(str).setExecutor(inventories);
            }

            // this.getConfig().get("inventories.multiplexPlayerInventories");
            inventories.teleportOnSwich = getConfig().getBoolean("inventory.teleportOnSwitch");
            inventories.seperateGamemodeInventories = getConfig()
                    .getBoolean("inventory.seperateGamemodeInventories");

            Bukkit.getLogger().info("[MultiWorld] Registering Inventory Manager Event Handlers");
            getServer().getPluginManager().registerEvents(inventories, this); // Register the
                                                                              // inventory event
                                                                              // handlers
            inventories.loadInventoryConfig(); // Load the worlds list
        }

        // Teleport Manager

        // Chat Manager
        // This allows basic chat management and chat formating

        // Gamemode Manager
        if (getConfig().getBoolean("multiworld.enableGamemodeManagement")) {
            Bukkit.getLogger().info("[MultiWorld] Initializing Gamemode Manager");
            Bukkit.getLogger().info("[MultiWorld] Registering Gamemode Manager Event Handlers");
            getServer().getPluginManager().registerEvents(gamemodes, this); // handlers
            gamemodes.loadGamemodeConfig();
        }

        // Death Messages

        // Broadcast Messages

        // Player Spawn Manager

    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent evt) {
        if (appendWorldInChat) {
            String message = evt.getMessage();
            evt.setMessage("[" + evt.getPlayer().getWorld().getName() + "] " + message);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        getLogger().warning("Tab complete not completely implemented yet");
        ArrayList<String> list = new ArrayList<String>();
        for (World world : Bukkit.getWorlds()) {
            list.add(world.getName());
        }
        return list;
    }
}
