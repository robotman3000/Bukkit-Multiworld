package io.github.robotman3000.bukkit.multiworld.world;

import io.github.robotman3000.bukkit.spigotplus.api.command.CommandParameter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldManagerHelper {

    protected static long confirmTimeout;

	public static String[] asStringArray(World world) {
        // TODO: Add more color
        return new String[] { "---- Info For [" + world.getName() + "] ----",
                "UUID: " + world.getUID().toString(),
                "Difficulty: " + world.getDifficulty(),
                "Enviroment: " + world.getEnvironment(),
                "Allow Monster Spawns: " + world.getAllowMonsters(),
                "Allow Animal Spawns: " + world.getAllowAnimals(),
                "Keep Spawn Loaded: " + world.getKeepSpawnInMemory(),
                "Type: " + world.getWorldType(),
                "Seed: " + world.getSeed(),
                "Generate Structures: " + world.canGenerateStructures(),
                "PVP Enabled: " + world.getPVP(),
                "Autosave Enabled: " + world.isAutoSave(),
                "Spawn Location: " + world.getSpawnLocation(),
                "Gamerules: " + Arrays.asList(printGamerules(world)) };
    }

	private static String[] printGamerules(World world) {
		String[] string = world.getGameRules();
		for(int index = 0; index < string.length; index++){
			String oldValue = string[index];
			string[index] = oldValue + " = " + world.getGameRuleValue(oldValue);
		}
		return string;
	}

	public static Location isLocationSafe(Location loc) {
        for (int blockInc = 1; loc.getWorld().getMaxHeight() > blockInc; blockInc++) {
            Block block = loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + blockInc,
                                                    loc.getBlockZ());
            if (block.getLightFromSky() > 0) {
                // We may have found a safe spawn location
                if (block.getType() == Material.AIR || block.getType() == Material.WATER) {
                    Block block2 = loc.getWorld().getBlockAt(loc.getBlockX(),
                                                             loc.getBlockY() + blockInc + 1,
                                                             loc.getBlockZ());
                    if (block2.getType() == Material.AIR || block2.getType() == Material.WATER) {
                        Block block3 = loc.getWorld().getBlockAt(loc.getBlockX(),
                                loc.getBlockY() + blockInc - 1,
                                loc.getBlockZ());
                    	if(block3.getType().isSolid()){
                    		return block.getLocation();
                    	}
                    }
                }
            }
        }
        return null;
    }

    public static boolean isWorldFolder(File theFile) {
        if (theFile != null) {
            if (theFile.isDirectory()) {
                for (File file : theFile.listFiles()) {
                    if (file.getName().equalsIgnoreCase("level.dat")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static <T extends Enum<?>> String printEnum(T[] theEnum) {
        StringBuilder build = new StringBuilder();
        for (T prop2 : theEnum) {
            build.append(prop2.toString() + ", ");
        }
        build.delete(build.length() - 2, build.length());
        return build.toString();
    }

    public static void loadWorld(File file) {
        WorldCreator creator = new WorldCreator(file.getName());
        Properties props = new Properties();
        File theFile = new File(file, "world.properties");
        if(!theFile.exists()){
        	try {
				props.store(new FileWriter(theFile), "Don't delete this file");
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        try {
			props.load(new FileReader(theFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        creator.seed(Long.valueOf(props.getProperty("seed", String.valueOf(1234567))));
        creator.type(WorldType.valueOf(props.getProperty("type", "NORMAL")));
        creator.environment(Environment.valueOf(props.getProperty("enviroment", "NORMAL")));
        creator.generateStructures(Boolean.valueOf(props.getProperty("generateStructures", String.valueOf(true))));
        creator.createWorld();
	}

    public static void saveWorldConfig(World world) {
    	File folder = world.getWorldFolder();
    	Properties worldProps = new Properties();
    	worldProps.setProperty("seed", String.valueOf(world.getSeed()));
    	worldProps.setProperty("type", world.getWorldType().name());
    	worldProps.setProperty("enviroment", world.getEnvironment().name());
    	worldProps.setProperty("generateStructures", String.valueOf(world.canGenerateStructures()));
    	try {
			worldProps.store(new FileWriter(new File(folder, "world.properties")), "Don't delete this file!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Location getSafestSpawnPoint(World world) {
		Location spawn = world.getSpawnLocation();

		int spawnRadius = Bukkit.getSpawnRadius();

		int radiusRange = (spawnRadius != 0 ? spawnRadius : 16);
		
		for(int rotation = 0; rotation < 4; rotation++){
			int xMultiplier = -1 + (2 * ((rotation >> 1) & 1));
			int zMultiplier = -1 + (2 * (rotation & 1));
			
			for(int radius = 0; radius < radiusRange; radius++){	
				
				for(int xMod = 0; xMod < radius; xMod++){
					Location loc = isLocationSafe(spawn.add(xMod, 0, radius * zMultiplier));
					if(loc != null){
						return loc;
					}
				}
				
				for(int zMod = 0; zMod < radius; zMod++){
					Location loc = isLocationSafe(spawn.add(radius * xMultiplier, 0, zMod));
					if(loc != null){
						return loc;
					}
				}
			}
		}
		
		
		Block start = spawn.getBlock();
		if(!start.getType().isSolid()){
			start.setType(Material.STONE);
		}
		
		Block block = world.getBlockAt(spawn.add(0, 1, 0));
		if(!block.getType().isTransparent()){
			block.setType(Material.AIR, false);
		}
		
		block = world.getBlockAt(spawn.add(0, 2, 0));
		if(!block.getType().isTransparent()){
			block.setType(Material.AIR, false);
		}
		
		//Bukkit.getLogger().warning(ChatColor.RED + "Safe Spawn: No safe location was found!!");
		return spawn;
	}

	public static boolean getPropertyCommandResult(CommandSender sender, Command command, String label, String[] args, String propName, CommandParameter<?> param){
    	int argIndex = 0;
    	if(args.length > argIndex){
    		// First determine if the world name was provided
    		World world = Bukkit.getWorld(args[argIndex]);
    		if(world != null){
    			// The world is provided
    			argIndex++;
    		} else {
    			// We must infer the world
    			if(sender instanceof Player){
    				world = ((Player) sender).getWorld();
    			} else {
    				sender.sendMessage(ChatColor.RED + "You must provide a valid world name");
    				return false;
    			}
    		}
    		
    		// If we get here it means that we have a world to work with
    		// so the next step is to get the world property to manipulate
    		if(args.length > argIndex){
    			PropertyList property = (PropertyList) param.getParameterValue(args[argIndex]);
    			if(property != null){
    				// Now determine if there was a new value
    				String newValue = null;
    				argIndex++;
    				if(args.length > argIndex){
    					newValue = args[argIndex];
    				}
    				
    				if(newValue == null){
    					// Get the value and print it
    					if(sender.hasPermission("spigotplus.multiworld.command." + propName + ".get." + world.getName())){
    						sender.sendMessage(ChatColor.GOLD + property.name() + ChatColor.RESET +  " is set to " + ChatColor.GREEN + property.getPropertyValue(world) + ChatColor.RESET +  " in the world \"" + ChatColor.BLUE + world.getName() + ChatColor.RESET + "\"");
    						return true;
    					} else {
    						sender.sendMessage(ChatColor.RED + "You are not permitted to get the " + propName + "s of this world");
    					}
    				} else {
    					if(sender.hasPermission("spigotplus.multiworld.command." + propName + ".set." + world.getName())){
							boolean result = property.setPropertyValue(world, newValue);
							sender.sendMessage((result ? ChatColor.GREEN : ChatColor.RED) + "World " + propName + " was " + ( result ? "" : "not " ) + "updated successfully");
							return true;
    					} else {
    						sender.sendMessage(ChatColor.RED + "You are not permitted to modify the " + propName + "s of this world");
    					}
    				}
    			} else {
    				sender.sendMessage(ChatColor.RED + "You must provide a valid world " + propName);
    			}
    		} else {
    			sender.sendMessage(ChatColor.RED + "You must provide a valid world " + propName);
    		}    		
    	}
    	return false;
    }

	public static long getConfirmTimeout() {
		return confirmTimeout;
	}

	public static boolean deleteWorldFolder(File worldFile) {
		if(isWorldFolder(worldFile)){
			return deleteFolder(worldFile);
		}
		return false;
	}

	private static boolean deleteFolder(File file) {
		if(file.isFile()){
			if(!file.delete()){
				Bukkit.getLogger().warning("Failed to delete the file: " + file);
				return false;
			}
		} else if(file.isDirectory()){
			for(File file1 : file.listFiles()){
				deleteFolder(file1);
			}
			file.delete();
		}
		return true;
	}
}
