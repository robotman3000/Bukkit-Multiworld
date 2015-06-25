package io.github.robotman3000.bukkit.multiworld;

import java.util.HashMap;

import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;

public class WorldConfig {
    String worldName = "world";
	Difficulty difficulty = Difficulty.PEACEFUL;
	World.Environment enviroment = World.Environment.NORMAL;
	boolean allowMonsterSpawns = true;
	boolean allowAnimalSpawns = true;
	HashMap<String, String> gamerules = new HashMap<String, String>();
	WorldType type =  WorldType.NORMAL;
	boolean keepSpawnLoaded = true;
    long seed;
	boolean generateStructures = true;
    ChunkGenerator generator;
    boolean enablePVP = true;
	boolean autosave = true;
	public int spawnX;
	public int spawnY;
	public int spawnZ;

	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}

	public void setDifficulty(String value){
		this.difficulty = Difficulty.valueOf(value);
	}
	
	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
	}

	public void setGenerator(ChunkGenerator generator) {
		// TODO Auto-generated method stub
		
	}

	public void parseGamerules(String[] gameRules2, World world) {
		HashMap<String, String> gamerules = new HashMap<>();
		for(String gamerule : gameRules2){
			gamerules.put(gamerule, world.getGameRuleValue(gamerule));
		}
		this.gamerules = new HashMap<>(gamerules);
	}

	public String[] asStringArray() {
		return new String[]
				{"---- Info For [ " + worldName + " ] ----",
				 "Difficulty: " + difficulty,
				 "Enviroment: " + enviroment,
				 "Allow Monster Spawns: " + allowMonsterSpawns,
				 "Allow Animal Spawns: " + allowAnimalSpawns,
				 "Keep Spawn Loaded: " + keepSpawnLoaded,
				 "Type: " + type,
				 "Seed: " + seed,
				 "Generate Structures: " + generateStructures,
				 "PVP Enabled: " + enablePVP,
				 "Generator: " + generator,
				 "Autosave Enabled: " + autosave,
				 "Spawn Location: " + "x" + spawnX + " y" + spawnY + " z" + spawnZ,
				 "Gamerules: " + gamerules};
	}
}
