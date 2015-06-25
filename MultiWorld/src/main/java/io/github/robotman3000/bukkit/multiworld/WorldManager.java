package io.github.robotman3000.bukkit.multiworld;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


//TODO: Make this class not static
public class WorldManager {

	public static boolean createWorld(WorldConfig worldData) {
		//TODO: Add safety checks
		//TODO: Add all world properties
		if(isWorldLoaded(worldData.worldName)){
			return true;
		}
		WorldCreator creator = new WorldCreator(worldData.worldName);
		creator.environment(worldData.enviroment);
		creator.generateStructures(worldData.generateStructures);
		creator.generator(worldData.generator);
		creator.seed(worldData.seed);
		creator.type(worldData.type);
		World theWorld = Bukkit.getServer().createWorld(creator);
		theWorld.setAutoSave(worldData.autosave);
		theWorld.setKeepSpawnInMemory(worldData.keepSpawnLoaded);
		theWorld.setSpawnFlags(worldData.allowMonsterSpawns, worldData.allowAnimalSpawns);
		theWorld.setSpawnLocation(worldData.spawnX, worldData.spawnY, worldData.spawnZ);
		theWorld.setDifficulty(worldData.difficulty);
		theWorld.setPVP(worldData.enablePVP);
		for(String gameRule : worldData.gamerules.keySet()){
			theWorld.setGameRuleValue(gameRule, worldData.gamerules.get(gameRule));
		}
		return true;
	}

	public static boolean unloadWorld(String world) {
		if(!isWorldLoaded(world)){
			return true;
		}
		return Bukkit.getServer().unloadWorld(world, true);
	}
	
	public static boolean isWorldLoaded(String world){
		if(Bukkit.getServer().getWorld(world) != null){
			return true;
		}
		return false;
	}

	public static boolean deleteWorldFolder(World worldToDelete) {
		try {
			if(isWorldLoaded(worldToDelete.getName())){
				unloadWorld(worldToDelete.getName());
			}
			CommonLogic.dirDelete(worldToDelete.getWorldFolder());
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public static void loadWorldConfig(File theWorldFile) {
		Type worldType = new TypeToken<List<WorldConfig>>(){}.getType();
		String config = CommonLogic.loadJsonAsString(theWorldFile);
		//Bukkit.getLogger().info(config);
		List<WorldConfig> worldList = new Gson().fromJson(config, worldType);
		//@SuppressWarnings("unchecked")
		//List<WorldConfig> list = (List<WorldConfig>) plugin.getConfig().getList("multiworld.worlds");
		for(WorldConfig conf : worldList){
			createWorld(conf);
		}
	}

	public static void saveWorldConfig(File theWorldFile){
		Type worldType = new TypeToken<List<WorldConfig>>(){}.getType();
		ArrayList<WorldConfig> list = new ArrayList<WorldConfig>();
		for(World world : Bukkit.getWorlds()){
			//Bukkit.getLogger().warning(world.getName() + "\n\t" + world.getUID());
			list.add(generateConfigForWorld(world));
		}
		//plugin.getConfig().set("multiworld.worlds", list);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		CommonLogic.saveJsonAsFile(theWorldFile, gson.toJson(list, worldType));
	}

	public static WorldConfig generateConfigForWorld(World world) {
		WorldConfig config = new WorldConfig();
		config.enablePVP = world.getPVP();
		config.worldName = world.getName();
		config.allowAnimalSpawns = world.getAllowAnimals();
		config.allowMonsterSpawns = world.getAllowMonsters();
		config.difficulty = world.getDifficulty();
		config.enviroment = world.getEnvironment();
		config.generateStructures = world.canGenerateStructures();
		config.parseGamerules(world.getGameRules(), world);
		config.setGenerator(world.getGenerator());
		config.seed = world.getSeed();
		config.keepSpawnLoaded = world.getKeepSpawnInMemory();
		config.type = world.getWorldType();
		config.autosave = world.isAutoSave();
		config.spawnX = world.getSpawnLocation().getBlockX();
		config.spawnY = world.getSpawnLocation().getBlockY();
		config.spawnZ = world.getSpawnLocation().getBlockZ();
		return config;
	}
}
