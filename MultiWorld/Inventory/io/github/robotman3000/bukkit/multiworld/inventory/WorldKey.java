package io.github.robotman3000.bukkit.multiworld.inventory;

import io.github.robotman3000.bukkit.multiworld.inventory.PlayerInventory.InventoryProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class WorldKey implements ConfigurationSerializable {
	private String worldName;
	private UUID worldID = null;
	private List<GameMode> gamemodes;
	
	public WorldKey(String name, UUID id, List<GameMode> gamemodes){
		this.worldName = name;
		this.worldID = id;
		this.gamemodes = gamemodes;
	}
	
	public String getWorldName() {
		return worldName;
	}

	public UUID getWorldID() {
		// Note to self; When this is called always check if the result is null
		return worldID;
	}

	public List<GameMode> getGamemodes() {
		return gamemodes;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("name", worldName);
		map.put("id", worldID.toString());
		List<String> gModes = new ArrayList<>();
		for(GameMode gamemode : gamemodes){
			gModes.add(gamemode.name());
		}
		map.put("gamemodes", gModes);
		return map;
	}
	
	public static WorldKey deserialize(Map<String, Object> map) {
		String name = (String) map.get("name");
		UUID id = UUID.fromString((String) map.get("id"));
		List<String> gamemodes = (List<String>) map.get("gamemodes");
		
		List<GameMode> list = new ArrayList<>();
		for(String str : gamemodes){
			try {
				list.add(GameMode.valueOf(str));
			} catch (Exception e){
				e.printStackTrace();
				//TODO: Print an error
			}
		}
		return new WorldKey(name, id, list);
	}
	
}
