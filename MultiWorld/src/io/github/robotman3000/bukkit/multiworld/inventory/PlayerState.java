package io.github.robotman3000.bukkit.multiworld.inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

public class PlayerState implements ConfigurationSerializable {

	private GameMode gamemode;
	
	private World world;
	private UUID worldId;
	
	private Player player;
	private UUID playerId;
	
	public PlayerState(){
		
	}
	
	public PlayerState(Player player, World world, GameMode gamemode){
		this.setGamemode(gamemode);
		this.setWorld(world);
		this.setPlayer(player);
	}
	
	protected void setIDs(UUID playerId, UUID worldId){
		this.playerId = playerId;
		this.worldId = worldId;
	}

	public Player getPlayer() {
		if(player == null){
			this.player = Bukkit.getPlayer(playerId);
		}
		return player;
	}

	private void setPlayer(Player player) {
		this.player = player;
	}

	public World getWorld() {
		if(world == null){
			this.world = Bukkit.getWorld(worldId);
		}
		return world;
	}

	private void setWorld(World world) {
		this.world = world;
	}

	public GameMode getGamemode() {
		return gamemode;
	}

	private void setGamemode(GameMode gamemode) {
		this.gamemode = gamemode;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof PlayerState){
			PlayerState play = (PlayerState) obj;
			try{
				if(getGamemode().equals(play.getGamemode()) 
						&& getPlayer().getUniqueId().equals(play.getPlayer().getUniqueId()) 
						&& getWorld().getUID().equals(play.getWorld().getUID())){
					return true;
				}
			} catch (NullPointerException e){}
		}
		return false;
	}

	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("gamemode", gamemode.toString());
		map.put("world", world.getUID().toString());
		map.put("player", player.getUniqueId().toString());
		return map;
	}
	
	public static PlayerState deserialize(Map<String, Object> map){
		PlayerState ps = new PlayerState(
				Bukkit.getPlayer(UUID.fromString(map.get("player").toString())), 
				Bukkit.getWorld(UUID.fromString(map.get("world").toString())),
				GameMode.valueOf(map.get("gamemode").toString()));
		ps.setIDs(UUID.fromString(map.get("player").toString()), UUID.fromString(map.get("world").toString()));
		return ps;
	}
}
