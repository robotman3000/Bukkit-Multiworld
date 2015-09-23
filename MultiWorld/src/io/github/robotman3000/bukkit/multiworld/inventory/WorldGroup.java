package io.github.robotman3000.bukkit.multiworld.inventory;

import java.util.List;

import org.bukkit.GameMode;

public class WorldGroup {

	private GameMode gamemode;
	private List<String> worlds;
	private String name;
	
	public WorldGroup(String name, GameMode gamemode, List<String> worlds){
		this.name = name;
		this.gamemode = gamemode;
		this.worlds = worlds;
	}
	
	public GameMode getGamemode() {
		return gamemode;
	}
	public List<String> getWorlds() {
		return worlds;
	}
	public String getName() {
		return name;
	}
}
