package io.github.robotman3000.bukkit.multiworld.world;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;

public class BukkitWorld {
    private String worldName = "world";
    private UUID worldUUID = null;
    
    // The World Config
	private Difficulty difficulty = Difficulty.PEACEFUL;
	private World.Environment enviroment = World.Environment.NORMAL;
	private boolean allowMonsterSpawns = true;
	private boolean allowAnimalSpawns = true;
	private HashMap<String, String> gamerules = new HashMap<String, String>();
	private WorldType type =  WorldType.NORMAL;
	private boolean keepSpawnLoaded = true;
	private long seed;
	private boolean generateStructures = true;
	private ChunkGenerator generator;
	private boolean enablePVP = true;
	private boolean autosave = true;
	private int spawnX;
	private int spawnY;
	private int spawnZ;
	
	protected void setUUID(UUID worldUUID){
		this.worldUUID = worldUUID;
	}
	
	public String[] asStringArray() {
		//TODO: Add more color
		return new String[]
				{"---- Info For [" + worldName + "] ----",
				 "UUID: " + worldUUID,
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
				 "Spawn Location: " + "{x:" + spawnX + " y:" + spawnY + " z:" + spawnZ + "}",
				 "Gamerules: " + gamerules};
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}
	
	public World.Environment getEnviroment() {
		return enviroment;
	}

	public HashMap<String, String> getGamerules() {
		return gamerules;
	}

	public ChunkGenerator getGenerator() {
		return generator;
	}

	public long getSeed() {
		return seed;
	}

	public int getSpawnX() {
		return spawnX;
	}

	public int getSpawnY() {
		return spawnY;
	}

	public int getSpawnZ() {
		return spawnZ;
	}

	public WorldType getType() {
		return type;
	}

	public UUID getUUID() {
		return this.worldUUID;
	}

	public WorldCreator getWorldCreator() {
		return new WorldCreator(worldName)
					.environment(this.enviroment)
					.generateStructures(generateStructures)
					.generator(generator)
					.generatorSettings("generatorSettings")
					.seed(seed)
					.type(type);
	}

	public String getWorldName() {
		return worldName;
	}

	public UUID getWorldUUID() {
		return worldUUID;
	}

	public boolean isAllowAnimalSpawns() {
		return allowAnimalSpawns;
	}

	public boolean isAllowMonsterSpawns() {
		return allowMonsterSpawns;
	}

	public boolean isAutosave() {
		return autosave;
	}

	public boolean isEnablePVP() {
		return enablePVP;
	}

	public boolean isGenerateStructures() {
		return generateStructures;
	}

	public boolean isKeepSpawnLoaded() {
		return keepSpawnLoaded;
	}

	public void parseGamerules(String[] gameRules2, World world) {
		HashMap<String, String> gamerules = new HashMap<>();
		for(String gamerule : gameRules2){
			gamerules.put(gamerule, world.getGameRuleValue(gamerule));
		}
		this.gamerules = new HashMap<>(gamerules);
	}

	protected void setAllowAnimalSpawns(boolean allowAnimalSpawns) {
		this.allowAnimalSpawns = allowAnimalSpawns;
	}

	protected void setAllowMonsterSpawns(boolean allowMonsterSpawns) {
		this.allowMonsterSpawns = allowMonsterSpawns;
	}

	protected void setAutosave(boolean autosave) {
		this.autosave = autosave;
	}

	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
	}

	public void setDifficulty(String value){
		this.difficulty = Difficulty.valueOf(value);
	}

	protected void setEnablePVP(boolean enablePVP) {
		this.enablePVP = enablePVP;
	}

	protected void setEnviroment(World.Environment enviroment) {
		this.enviroment = enviroment;
	}

	protected void setGamerules(HashMap<String, String> gamerules) {
		this.gamerules = gamerules;
	}

	protected void setGenerateStructures(boolean generateStructures) {
		this.generateStructures = generateStructures;
	}

	public void setGenerator(ChunkGenerator generator) {
		this.generator = generator;
	}

	protected void setKeepSpawnLoaded(boolean keepSpawnLoaded) {
		this.keepSpawnLoaded = keepSpawnLoaded;
	}

	protected void setSeed(long seed) {
		this.seed = seed;
	}

	protected void setSpawnX(int spawnX) {
		this.spawnX = spawnX;
	}

	protected void setSpawnY(int spawnY) {
		this.spawnY = spawnY;
	}

	protected void setSpawnZ(int spawnZ) {
		this.spawnZ = spawnZ;
	}

	protected void setType(WorldType type) {
		this.type = type;
	}

	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}

	protected void setWorldUUID(UUID worldUUID) {
		this.worldUUID = worldUUID;
	}
}
