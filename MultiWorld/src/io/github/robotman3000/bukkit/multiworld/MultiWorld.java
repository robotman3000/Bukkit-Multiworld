package io.github.robotman3000.bukkit.multiworld;

import io.github.robotman3000.bukkit.multiworld.inventory.BukkitInventory;
import io.github.robotman3000.bukkit.multiworld.inventory.InventoryManager;
import io.github.robotman3000.bukkit.multiworld.inventory.PlayerState;
import io.github.robotman3000.bukkit.multiworld.world.WorldManager;

import java.util.ArrayList;
import java.util.List;

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
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MultiWorld extends JavaPlugin implements Listener {

	WorldManager worlds = new WorldManager(this);
	InventoryManager inventories = new InventoryManager(this);
	
	private boolean appendWorldInChat;

	@Override
	public void onEnable() {
		// Reminder: Don't assume that this is only called on a server restart
		ConfigurationSerialization.registerClass(PlayerState.class);
		ConfigurationSerialization.registerClass(BukkitInventory.class);
		
		saveDefaultConfig();
		
/*			
		this.getConfig().get("multiworld.enableTeleportManagement");
		this.getConfig().get("teleport.handleNetherPortals");
		this.getConfig().get("teleport.handleEndPortals");
		this.getConfig().get("teleport.enablePortalDestOverride");
		this.getConfig().get("teleport.allowSignPortals");*/
		
		// Base Plugin
		getServer().getPluginManager().registerEvents(this, this);
		appendWorldInChat = this.getConfig().getBoolean("multiworld.appendWorldInChat");
		
/*		// Override the world in server.properties
		if(this.getConfig().getBoolean("multiworld.overrideDefaultWorld")){
			
		}*/
		
		// World Manager
		if(this.getConfig().getBoolean("multiworld.enableWorldManagement")){
			Bukkit.getLogger().info("Initializing World Manager");
			for(String str : worlds.commands){ // Register Commands
				Bukkit.getLogger().info("Registering World Manager Command: " + str);
				this.getCommand(str).setExecutor(worlds);
			}
			Bukkit.getLogger().info("Registering World Manager Event Handlers");
			getServer().getPluginManager().registerEvents(worlds, this); // Register the world event handlers
			worlds.loadWorldConfig(); // Load the worlds list
		}
			
		// Inventory Manager
		if(this.getConfig().getBoolean("multiworld.enableInventoryManagement")){
			Bukkit.getLogger().info("Initializing Inventory Manager");
			for(String str : inventories.commands){
				this.getCommand(str).setExecutor(inventories);
			}
			
			inventories.seperateWorldInventories = this.getConfig().getBoolean("inventory.seperateWorldInventories");
			//this.getConfig().get("inventories.multiplexPlayerInventories");
			inventories.teleportOnSwich = this.getConfig().getBoolean("inventory.teleportOnSwitch");
			inventories.seperateGamemodeInventories = this.getConfig().getBoolean("inventory.seperateGamemodeInventories");
			inventories.forceGamemode = this.getConfig().getBoolean("inventory.forceGamemode");
			
			getServer().getPluginManager().registerEvents(inventories, this); // Register the inventory event handlers
			inventories.loadInventoryConfig(); // Load the worlds list
		}
		
		// Teleport Manager
		
		// Chat Manager
		// This allows basic chat management and chat formating
		
		// Permissions System
		
		// Death Messages
		
	}

	@Override
	public void onDisable() {
		// Reminder: Don't assume that this is only called on a server restart
		worlds.saveWorldConfig();
		inventories.saveInventoryConfig();
		saveConfig();
	}

	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent evt) {
		String name = evt.getFrom().getName();
		evt.getPlayer().sendMessage("You switched from the world " + name + " to "  + evt.getPlayer().getWorld().getName());
	}

	@EventHandler
	public void onPlayerGamemodeChanged(PlayerGameModeChangeEvent evt) {
		evt.getPlayer().sendMessage("You changed your gamemode from " + evt.getPlayer().getGameMode() + " to " + evt.getNewGameMode());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent evt) {
		evt.getPlayer().sendMessage("Welcome to the server");
	}

	@EventHandler
	public void onPlayerPortalEvent(PlayerPortalEvent evt) {
		evt.getPlayer().sendMessage("Portal Event");
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent evt) {
		evt.getPlayer().sendMessage("You were teleported; the cause was " + evt.getCause().toString());
		evt.getPlayer().sendMessage("Teleported from: " + evt.getFrom() + " to " + evt.getTo());
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent evt){
		if(appendWorldInChat){
			String message = evt.getMessage();
			evt.setMessage("[" + evt.getPlayer().getWorld().getName() + "] " + message);
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd,
			String label, String[] args) {
		this.getLogger().warning("Tab complete not implemented yet");
		ArrayList<String> list = new ArrayList<String>();
/*		if(cmd.getName().equalsIgnoreCase("inset")){
			for(InventoryConfig inv : inventoryKeeper.inventories){
				list.add(inv.getInventoryId());
			}
			return list;
		}*/
		list.add(sender.getName());
		return list;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//TODO: add a command to list all loaded worlds
		switch (cmd.getName()) {
		case "goto":
			return gotoCommand(sender, cmd, label, args);
		default:
			sender.sendMessage("Nope, I got nothing; Something went very wrong; CMD was: " + cmd.getName());
			break;
		}
		sender.sendMessage("Command Args are as follows");
		for (String str : args) {
			sender.sendMessage("Command Arg: " + str);
		}
		return true;
	}

	private boolean gotoCommand(CommandSender sender, Command cmd,
			String label, String[] args) {
		//TODO: Add safety checks; in paticular check if the world exists before teleporting
		// TODO: Move this command to teleport manager class
		if(!(args.length >= 1)){
			sender.sendMessage("You must provide a world name");
			return false;
		}
		World world = Bukkit.getServer().getWorld(args[0]);
		if(world != null){
			sender.sendMessage("Teleporting you to world " + args[0]);
			Location loc = world.getSpawnLocation();
			sender.sendMessage("Activating Teleport");
			if (!(sender instanceof Player) && args.length < 2) {
				sender.sendMessage("Error; You must be a player to teleport");
				return false;
			}
			Player thePlayer;
			if(args.length > 1){
				thePlayer = Bukkit.getPlayer(args[1]);
			} else {
				thePlayer = (Player) sender;
			}
			thePlayer.teleport(loc);
			return true;
		}
		return false;
	}
}
