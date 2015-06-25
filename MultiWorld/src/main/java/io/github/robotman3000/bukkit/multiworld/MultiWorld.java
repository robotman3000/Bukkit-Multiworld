package io.github.robotman3000.bukkit.multiworld;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;

public class MultiWorld extends JavaPlugin implements Listener {

	InventoryManager inventoryKeeper = new InventoryManager();
	/*
	 * 
	 * TeleportManager teleportor = new TeleportManager(this); private
	 * CommandHandler commandGuy = new CommandHandler(this);
	 */

	@Override
	public void onEnable() {
		// Reminder: Don't assume that this is only called on a server restart
		
		this.getConfig().get("multiworld.enableWorldManagement");
		this.getConfig().get("worlds.autoload");
		this.getConfig().get("worlds.firstSpawnWorld");
		this.getConfig().get("worlds.showWorldInChat");
		
		this.getConfig().get("multiworld.enableInventoryManagement");
		this.getConfig().get("inventories.perworldInventory");
		this.getConfig().get("inventories.multiplexPlayerInventories");
		this.getConfig().get("inventories.teleportOnSwitch");
		
		this.getConfig().get("multiworld.enableTeleportManagement");
		this.getConfig().get("teleport.handleNetherPortals");
		this.getConfig().get("teleport.handleEndPortals");
		this.getConfig().get("teleport.enablePortalDestOverride");
		this.getConfig().get("teleport.allowSignPortals");
	
		
		
		
		getServer().getPluginManager().registerEvents(this, this);
		WorldManager.loadWorldConfig(new File(getDataFolder() + "/worlds.json"));
		inventoryKeeper.loadInventoryConfig(new File(getDataFolder() + "/inventories.json"));
	}

	@Override
	public void onDisable() {
		// Reminder: Don't assume that this is only called on a server restart
		WorldManager.saveWorldConfig(new File(getDataFolder() + "/worlds.json"));
		inventoryKeeper.saveInventoryConfig(new File(getDataFolder() + "/inventories.json"));
		saveConfig();
	}

	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent evt) {
		String name = evt.getFrom().getName();
		evt.getPlayer().sendMessage("You switched from the world " + name + " to "  + evt.getPlayer().getWorld().getName());
	}

	@EventHandler
	public void onPlayerGamemodeChanged(PlayerGameModeChangeEvent evt) {
		evt.getPlayer().sendMessage("You changed your gamemode");
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent evt) {
		evt.getPlayer().sendMessage("Welcome to the server");
		
		String invId = inventoryKeeper.getDefaultInventory(evt.getPlayer());
		inventoryKeeper.registerInventory(evt.getPlayer(), invId);
		
		//Bukkit.getLogger().info("Config");
		//Bukkit.getLogger().info(new Gson().toJson(InventoryManager.generateConfigForPlayer(evt.getPlayer())));  // Delete This
		
/*		InventoryConfig inv = InventoryManager.generateConfigForPlayer(evt.getPlayer());
		inventoryKeeper.inventories.add(inv);
		inventoryKeeper.setPlayerInventory(evt.getPlayer(), inv);
		evt.getPlayer().sendMessage("Inventory Id: " + inv.inventoryId);*/
	}

	@EventHandler
	public void onPlayerPortalEvent(PlayerPortalEvent evt) {
		evt.getPlayer().sendMessage("Portal Event");
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent evt) {
		inventoryKeeper.unregisterInventory(evt.getPlayer()); // Any changes the player makes to his inventory past this point will not be saved
		evt.getPlayer().getInventory().clear(); // This is just to "clean our slate" for the next time the player joins; its not required
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent evt) {
		evt.getPlayer().sendMessage("You were teleported; the cause was " + evt.getCause().toString());
		evt.getPlayer().sendMessage("Teleported from: " + evt.getFrom() + " to " + evt.getTo());
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd,
			String label, String[] args) {
		this.getLogger().warning("Tab complete not implemented yet");
		ArrayList<String> list = new ArrayList<String>();
		if(cmd.getName().equalsIgnoreCase("inset")){
			for(InventoryConfig inv : inventoryKeeper.inventories){
				list.add(inv.getInventoryId());
			}
			return list;
		}
		list.add(sender.getName());
		return list;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//TODO: add a command to list all loaded worlds
		switch (cmd.getName()) {
		case "mwcreate":
			return createCommand(sender, cmd, label, args);
		case "mwdelete":
			return deleteWorldAndData(sender, cmd, label, args);
		case "mwunload":
			return unloadWorldCommand(sender, cmd, label, args);
		case "mwinfo":
			return infoCommand(sender, cmd, label, args);
		case "mwgamerule":
			return gameruleCommand(sender, cmd, label, args);
		case "mwlist":
			return worldListCommand(sender, cmd, label, args);
		case "goto":
			return gotoCommand(sender, cmd, label, args);
		case "inlist":
			return listInvCommand(sender, cmd, label, args);
		case "inset":
			return setInvCommand(sender, cmd, label, args);
		case "increate":
			return createInvCommand(sender, cmd, label, args);
		case "ininfo":
			return invInfoCommand(sender, cmd, label, args);
		default:
			sender.sendMessage("Nope, i got nothing; Something went very wrong; cmd was: " + cmd.getName());
			break;
		}
		for (String str : args) {
			sender.sendMessage("Command Arg: " + str);
		}
		return true;
	}

	private boolean invInfoCommand(CommandSender sender, Command cmd,
			String label, String[] args) {
		Player player = (Player) sender;
		player.sendMessage(inventoryKeeper.getCurrentInventory(player));
		return true;
	}

	private boolean createInvCommand(CommandSender sender, Command cmd,
			String label, String[] args) {
		Player player = (Player) sender;
		player.sendMessage(inventoryKeeper.createBlankInventory());
		return true;
	}

	private boolean setInvCommand(CommandSender sender, Command cmd,
			String label, String[] args) {
		Player player = (Player) sender;
		player.sendMessage("Updating your inventory");
		//String str = inventoryKeeper.getCurrentInventory(player);
		inventoryKeeper.unregisterInventory(player);
		inventoryKeeper.registerInventory(player, args[0]);
		//inventoryKeeper.setPlayerInventory(player, inventoryKeeper.getInventory(args[0]));
		return true;
	}

	private boolean listInvCommand(CommandSender sender, Command cmd,
			String label, String[] args) {
		for(InventoryConfig inv : inventoryKeeper.inventories){
			sender.sendMessage(inv.getInventoryId());
		}
		return true;
	}

	private boolean worldListCommand(CommandSender sender, Command cmd,
			String label, String[] args) {
		for(World world : Bukkit.getWorlds()){
			sender.sendMessage(world.getName() + " - " + world.getEnvironment());
		}
		return true;
	}

	private boolean deleteWorldAndData(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO Add a confirmation message
		// this is broken; the world folder cant be gotten if the world is unloaded
		if(args.length != 1){
			sender.sendMessage("Argument count is incorrect; expected 1 got " + args.length);
			return false;
		}
		World world = Bukkit.getWorld(args[0]);
		if(world == null){
			sender.sendMessage("That world doesn't exist");
			return false;
		}
		File worldFolder = world.getWorldFolder();
		unloadWorldCommand(sender, cmd, label, args);
		try {
			CommonLogic.dirDelete(worldFolder);
		} catch (IOException e) {
			Bukkit.getLogger().warning("Failed to delete world folder");
			sender.sendMessage("Failed to delete the world folder");
			e.printStackTrace();
		}
		return true;
	}

	private boolean gameruleCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(args.length >= 2)){
			sender.sendMessage("Not enough arguments");
			return false;
		}
		
		if(args[0].equalsIgnoreCase("get")){
			if(args.length < 3){
				if(sender instanceof Player){
					Player player = (Player) sender;
					for(String str : player.getWorld().getGameRules()){
						if(str.equalsIgnoreCase(args[1])){
							sender.sendMessage("Gamerule " + ChatColor.GREEN + str + ChatColor.WHITE + " has value " + ChatColor.GREEN + player.getWorld().getGameRuleValue(str));
							return true;
						}
					}
				} else {
					sender.sendMessage("You must provide a world name");
					return false;
				}
			}
			World world = Bukkit.getWorld(args[2]);
			if(world == null){
				sender.sendMessage("That world doesn't exist");
				return false;
			}
			for(String str : world.getGameRules()){
				if(str.equalsIgnoreCase(args[1])){
					sender.sendMessage("Gamerule " + ChatColor.GREEN + str + " has value " + ChatColor.GREEN + world.getGameRuleValue(str));
					return true;
				}
			}
		} else if(args[0].equalsIgnoreCase("set")){
			//TODO: Fill me in
			
		}
		return false;
	}

	private boolean infoCommand(CommandSender sender, Command cmd,
			String label, String[] args) {
		if(args.length >= 1){
			World world = Bukkit.getWorld(args[0]);
			if(world == null){
				sender.sendMessage("That world doesn't exist!");
				return true;
			}
			sender.sendMessage(WorldManager.generateConfigForWorld(world).asStringArray());
		}
		
		if(!(sender instanceof Player)){
			sender.sendMessage("You must provide a world name");
			return false;
		}
		
		Player player = (Player) sender;
		player.sendMessage(WorldManager.generateConfigForWorld(player.getWorld()).asStringArray());
		return true;
	}

	private boolean unloadWorldCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length != 1){
			sender.sendMessage("Argument count is incorrect; expected 1 got " + args.length);
			return false;
		}
		sender.sendMessage("Unloading world " + args[0]);
		boolean var = WorldManager.unloadWorld(args[0]);
		sender.sendMessage("Unloaded world");
		return var;
	}

	private boolean createCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//TODO: Add safety checks
		if(args.length < 1){
			sender.sendMessage("Missing World Name");
			return false;
		}
		
		WorldConfig conf = new WorldConfig();
		conf.worldName = args[0];
		sender.sendMessage("Begining world creation");
		for(int index = 1; index < args.length; index++){ // start at index 1 so as to skip parsing the world name
			if(args[index].startsWith("-s")){
				try {
					conf.seed = Long.parseLong(args[index].substring(2));
				} catch (NumberFormatException e){
					//TODO: Generate a new random seed
				}
			} else if(args[index].startsWith("-t")){
				try {
					conf.type = WorldType.valueOf(args[index].toUpperCase().substring(2));
				} catch (IllegalArgumentException e){
					conf.type = null; // Makes the world generator use default type
				}
			} else if(args[index].startsWith("-e")){
				try {
					conf.enviroment = World.Environment.valueOf(args[index].toUpperCase().substring(2));
				} catch (IllegalArgumentException e){
					conf.enviroment = World.Environment.NORMAL; // Makes the world generator use default enviroment
				}
			} else if(args[index].startsWith("-b")){
				conf.generateStructures = Boolean.parseBoolean(args[index].toUpperCase().substring(2));
			} else if(args[index].startsWith("-g")){
				//This is the generator option
				//TODO: Implement this
			}
		}
		boolean var = WorldManager.createWorld(conf);
		sender.sendMessage("Done with world creation");
		return var;
		
	}

	private boolean gotoCommand(CommandSender sender, Command cmd,
			String label, String[] args) {
		//TODO: Add safety checks; in paticular check if the world exists before teleporting
		if(!(args.length >= 1)){
			sender.sendMessage("You must provide a world name");
			return false;
		}
		sender.sendMessage("Teleporting you to world " + args[0]);
		Location loc = Bukkit.getServer().getWorld(args[0]).getSpawnLocation();
		sender.sendMessage("Activating Teleport");
		if (!(sender instanceof Player)) {
			sender.sendMessage("Error; You must be a player to teleport");
			return false;
		}
		Player thePlayer = (Player) sender;
		thePlayer.teleport(loc);
		return true;
	}
}
