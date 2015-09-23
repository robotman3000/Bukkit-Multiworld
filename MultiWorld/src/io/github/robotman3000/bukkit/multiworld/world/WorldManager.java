package io.github.robotman3000.bukkit.multiworld.world;

import io.github.robotman3000.bukkit.multiworld.CommonLogic;
import io.github.robotman3000.bukkit.multiworld.MultiWorld;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class WorldManager implements Listener, CommandExecutor {

	public final String[] commands = {"mwcreate", /*"mwdelete",*/ "mwload", "mwunload", "mwinfo", "mwlist"/*, "mwgamerule"*/};
	private BukkitWorlds worlds = new BukkitWorlds();
	private final MultiWorld plugin;
	
	public boolean autoLoadWorlds;
	public boolean overrideDefaultWorlds;
	
	public WorldManager(MultiWorld multiWorld) {
		this.plugin = multiWorld;
	}

	public void loadWorldConfig() {
		Bukkit.getLogger().info("WorldManager: Loading World Configuration");
		List<String> worldList = plugin.getConfig().getStringList("world.worlds");
		//Gson gson = new Gson();
		
		/* TODO
		 * Add a check to make sure all currently loaded worlds are in the plugins world list
		 * before the below code is run
		 */
		
		for(File file : Bukkit.getWorldContainer().listFiles()){
			skipWorld:
			if(isWorldFolder(file)){
				Bukkit.getLogger().info("WorldManager: Found world " + file.getName());
				if(autoLoadWorlds){
					if(worldList.contains(file.getName())){ // If pending world is in list then skip loading it
						Bukkit.getLogger().info("WorldManager: World is listed in config; Skipping world " + file.getName());
						break skipWorld;
					}
					Bukkit.createWorld(new WorldCreator(file.getName()));
				} else {
					// Only load world if in worldList
					if(worldList.contains(file.getName())){
						Bukkit.getLogger().info("WorldManager: World is listed in config; Loading world " + file.getName());
						Bukkit.createWorld(new WorldCreator(file.getName()));
					}	
				}	
			}
		}
		

		if(overrideDefaultWorlds){
			for(World world : Bukkit.getWorlds()){
				if(world != null){
					Bukkit.getLogger().info("WorldManager: Unloading default world " + world.getName());
					Bukkit.unloadWorld(world, true);
				}
			}
		}
		
/*		if(file.isDirectory() && file.getName().contentEquals(conf)){
			
			if(!autoLoadWorlds){
				for(File confFile : file.listFiles()){
					if(confFile.getName().equalsIgnoreCase("level.dat") && confFile.isFile()){
						String worldConfStr = CommonLogic.loadJsonAsString(confFile);
						BukkitWorld newWorld = gson.fromJson(worldConfStr, BukkitWorld.class);
						worlds.createWorld(newWorld);
						worlds.loadWorldToMemory(newWorld.getUUID());
					}
				}
			}
		}
		
		for(String conf : worldList){

		}*/
		Bukkit.getLogger().info("WorldManager: Finished Loading Configuration");
	}

	public void saveWorldConfig(){
		Bukkit.getLogger().info("WorldManager: Saving World Configuration");
		ArrayList<String> worldUUID = new ArrayList<String>();
		//Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
/*		for(File file : Bukkit.getWorldContainer().listFiles()){
			if(isWorldFolder(file)){
				World Bukkit.getWorld(file.getName());
			}
		}
		
		for(World world : Bukkit.getWorlds()){
			worldUUID.add(world.getName());
			File worldFolder = world.getWorldFolder();
			File worldConfFile = new File(worldFolder, "worldConf.json");
			BukkitWorld worldConf = generateConfigForWorld(world);
			CommonLogic.saveJsonAsFile(worldConfFile, gson.toJson(worldConf));
		}*/
		plugin.getConfig().set("world.worlds", worldUUID);
		Bukkit.getLogger().info("WorldManager: Finished Saving Configuration");
 	}

	public static BukkitWorld generateConfigForWorld(World world) {
		// TODO: Make sure this includes all the world properties
		BukkitWorld config = new BukkitWorld();
		config.setEnablePVP(world.getPVP());
		config.setWorldName(world.getName());
		config.setAllowAnimalSpawns(world.getAllowAnimals());
		config.setAllowMonsterSpawns(world.getAllowMonsters());
		config.setDifficulty(world.getDifficulty());
		config.setEnviroment(world.getEnvironment());
		config.setGenerateStructures(world.canGenerateStructures());
		config.parseGamerules(world.getGameRules(), world);
		config.setGenerator(world.getGenerator());
		config.setSeed(world.getSeed());
		config.setKeepSpawnLoaded(world.getKeepSpawnInMemory());
		config.setType(world.getWorldType());
		config.setAutosave(world.isAutoSave());
		config.setSpawnX(world.getSpawnLocation().getBlockX());
		config.setSpawnY(world.getSpawnLocation().getBlockY());
		config.setSpawnZ(world.getSpawnLocation().getBlockZ());
		config.setUUID(world.getUID());
		return config;
	}

	public static void updateConfigForWorld(World theWorld, BukkitWorld world){
		theWorld.setPVP(world.isEnablePVP());
		theWorld.setSpawnFlags(world.isAllowMonsterSpawns(), world.isAllowAnimalSpawns());
		theWorld.setDifficulty(world.getDifficulty());
		theWorld.setKeepSpawnInMemory(world.isKeepSpawnLoaded());
		theWorld.setAutoSave(world.isAutosave());
		theWorld.setSpawnLocation(world.getSpawnX(), world.getSpawnY(), world.getSpawnZ());
		
		for(String key : world.getGamerules().keySet()){
			theWorld.setGameRuleValue(key, world.getGamerules().get(key));
		}
	}
	
	private boolean isWorldFolder(File theFile){
		if(theFile != null){
			if(theFile.isDirectory()){
				for(File file : theFile.listFiles()){
					if(file.getName().equalsIgnoreCase("level.dat")){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	// Command Parsing Logic
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		switch (cmd.getName()) {
		case "mwcreate":
			return createCommand(sender, cmd, label, args);
/*		case "mwdelete":
			return deleteWorldAndData(sender, cmd, label, args);*/
		case "mwunload":
			return unloadWorldCommand(sender, cmd, label, args);
		case "mwload":
			return loadWorldCommand(sender, cmd, label, args);
		case "mwinfo":
			return infoCommand(sender, cmd, label, args);
/*		case "mwgamerule":
			return gameruleCommand(sender, cmd, label, args);*/
		case "mwlist":
			return worldListCommand(sender, cmd, label, args);
		default:
			sender.sendMessage("Command Error in WorldManager Class!!");
			return false;
		}
	}
	
	// Event Handlers

	
	//TODO: Check when a player changes world or goes to spawn that the spawn point is safe
	
	// World Related Commands
	//TODO: Unify the command messages to look and feel the same
/*	private boolean deleteWorldAndData(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO Add a confirmation message
		if(args.length < 1){
			return false;
		}
		World world = Bukkit.getWorld(args[0]);
		if(world == null){
			sender.sendMessage("That world doesn't exist");
			return false;
		}

		boolean var = worlds.deleteWorld(world.getUID(), true);
		if(var != true){
			Bukkit.getLogger().warning("Failed to delete world folder");
			sender.sendMessage("Failed to delete the world folder");
			sender.sendMessage("The world data and config may be in an incomplete state if it still exists");
		}
		return true;
	}*/

/*	private boolean gameruleCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(args.length >= 2)){
			return false;
		}
		
		if(args[0].equalsIgnoreCase("get")){
			if(args.length < 3){
				if(sender instanceof Player){
					Player player = (Player) sender;
					for(String str : player.getWorld().getGameRules()){
						if(str.equalsIgnoreCase(args[1])){
							sender.sendMessage("Gamerule " + ChatColor.GREEN + str + ChatColor.WHITE + " has value " + ( Boolean.valueOf(player.getWorld().getGameRuleValue(str)) ? ChatColor.GREEN : ChatColor.RED) + player.getWorld().getGameRuleValue(str));
							return true;
						}
					}
				} else {
					World world;
					if((world = Bukkit.getWorld(args[1])) != null){
						for(String str : world.getGameRules()){
							if(str.equalsIgnoreCase(args[2])){
								sender.sendMessage("Gamerule " + ChatColor.GREEN + str + ChatColor.WHITE + " has value " + ( Boolean.valueOf(world.getGameRuleValue(str)) ? ChatColor.GREEN : ChatColor.RED) + world.getGameRuleValue(str));
								return true;
							}
						}
					}
				}
			}
		} else if(args[0].equalsIgnoreCase("set")){
			//TODO: Fill me in
			sender.sendMessage("Well, I would change the gamerule for you except for one little bity problem...");
			sender.sendMessage("I kinda, sorta, don't really know how to do that.");
			sender.sendMessage("However, you can change the gamerule in the worlds config file");
			
		}
		return false;
	}*/

	private boolean infoCommand(CommandSender sender, Command cmd, String label, String[] args) {
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
		//TODO: Check if there are players in the world
		if(args.length != 1){
			sender.sendMessage("Argument count is incorrect; expected 1 got " + args.length);
			return false;
		}
		World world = Bukkit.getWorld(args[0]);
		if(world != null){
			sender.sendMessage("Unloading world " + args[0]);
			boolean var = worlds.unloadWorldFromMemory(world.getUID(), true);
			sender.sendMessage("Unloaded world");
			return var;			
		}
		return false;
	}
	
	private boolean loadWorldCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(args.length < 1){
			if(sender instanceof Player){
				Player player = (Player) sender;
				return worlds.loadWorldToMemory(player.getWorld().getUID());
			}
			// If we get here we must be running from a console without a world name
			return false;
		}
		return worlds.loadWorldToMemory(worlds.doesWorldNameExist(args[0]));
	}

	private boolean createCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//TODO: Add safety checks
		if(args.length < 1){
			sender.sendMessage("Missing World Name");
			return false;
		}
		
		BukkitWorld conf = new BukkitWorld();
		conf.setWorldName(args[0]);
		//sender.sendMessage("Begining world creation");
		for(int index = 1; index < args.length; index++){ // start at index 1 so as to skip parsing the world name
			if(args[index].startsWith("-s")){
				try {
					conf.setSeed(Long.parseLong(args[index].substring(2)));
				} catch (NumberFormatException e){
					//TODO: Generate a new random seed
				}
			} else if(args[index].startsWith("-t")){
				try {
					conf.setType(WorldType.valueOf(args[index].toUpperCase().substring(2)));
				} catch (IllegalArgumentException e){
					conf.setType(null); // Makes the world generator use default type
				}
			} else if(args[index].startsWith("-e")){
				try {
					conf.setEnviroment(World.Environment.valueOf(args[index].toUpperCase().substring(2)));
				} catch (IllegalArgumentException e){
					conf.setEnviroment(World.Environment.NORMAL); // Makes the world generator use default enviroment
				}
			} else if(args[index].startsWith("-b")){
				conf.setGenerateStructures(Boolean.parseBoolean(args[index].toUpperCase().substring(2)));
			} else if(args[index].startsWith("-g")){
				//This is the generator option
				//TODO: Implement this
			}
		}
		if(worlds.createWorld(conf)){
			sender.sendMessage("Done with world creation");
			return true;
		}
		return false;
		
	}
	
	private boolean worldListCommand(CommandSender sender, Command cmd, String label, String[] args) {
		for(World world : Bukkit.getWorlds()){
			sender.sendMessage(world.getName() + " - " + CommonLogic.printEnvColor(world.getEnvironment()) + world.getEnvironment());
		}
		return true;
	}
}
