package io.github.robotman3000.bukkit.multiworld;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class InventoryManager {
	//TODO: make this private
	Set<InventoryConfig> inventories = new HashSet<>();
	private HashMap<String, InventoryConfig> registeredInventories = new HashMap<>();
	
	public void loadInventoryConfig(File inventoryFile) {
		//Type inventoryType = new TypeToken<Set<InventoryConfig>>(){}.getType();
		String config = CommonLogic.loadJsonAsString(inventoryFile);
		Bukkit.getLogger().info(config);
		InventoryConfig[] list = new GsonBuilder().setPrettyPrinting().create().fromJson(config, InventoryConfig[].class);
		if(list != null){
			for(InventoryConfig conf : list){
				inventories.add(conf);
			}
		}
	}

	public void saveInventoryConfig(File file) {
		//Type inventoryType = new TypeToken<Set<InventoryConfig>>(){}.getType();
		InventoryConfig[] theList = new InventoryConfig[inventories.size()];
		inventories.toArray(theList);
		//CommonLogic.saveJsonAsFile(file, new Gson().toJson(inventories, inventoryType));
		CommonLogic.saveJsonAsFile(file, new GsonBuilder().setPrettyPrinting().create().toJson(theList));
	}

	public String createNewInventory(InventoryConfig newInv){
		inventories.add(newInv);
		return newInv.getInventoryId();
	}
	
	public String createBlankInventory() {
		//TODO: fix this methed
		InventoryConfig config = new InventoryConfig();
		inventories.add(config);
		return config.getInventoryId();
	}
	
	public void deleteInventory(String inventoryId){
		
	}
	
	public InventoryConfig getInventory(String inventoryId){
		//TODO: do safety checks
		for(InventoryConfig inv : inventories){
			if(inv.getInventoryId().equalsIgnoreCase(inventoryId)){
				return inv;
			}
		}
		throw new IllegalArgumentException("The inventory id was invalid; id was " + inventoryId);
		//return null;
	}
	
	public void registerInventory(Player player, String indevtoryId){
		Bukkit.getLogger().info("Regestering inventory " + indevtoryId);
		
/*		InventoryConfig conf = new InventoryConfig();
		Iterator<InventoryConfig> it = inventories.iterator();
		while(it.hasNext()){
			conf = it.next();
			if(conf.getInventoryId().equalsIgnoreCase(indevtoryId)){
				it.remove();
			}
		}
		registeredInventories.put(player.getUniqueId().toString(), conf);
		*/
		
		@SuppressWarnings("deprecation")
		InventoryConfig conf = new InventoryConfig();
		Iterator<InventoryConfig> it = inventories.iterator();
		breakLoop:
		while(it.hasNext()){
			conf = it.next();
			if(conf.getInventoryId().equalsIgnoreCase(indevtoryId)){
				if(conf.getDefaultPlayer().equalsIgnoreCase("noPlayer") || conf.getDefaultPlayer().equalsIgnoreCase(player.getUniqueId().toString())){
					it.remove();
					break breakLoop; // we must break out of this loop so that the "conf" var wont get overwritten with the values of the next iterations
					// we can break because we have found the inventory we want
				} else {
					player.sendMessage("You can't use someone elses inventory!");
					return;
				}
			}
		}
		registeredInventories.put(player.getUniqueId().toString(), conf);
		
		setPlayerInventory(player, conf);
		Bukkit.getLogger().info("Unregistred: " + inventories.toString());
		Bukkit.getLogger().info("Registered: " + registeredInventories.toString());
	}
	
	public void unregisterInventory(Player player){
		Bukkit.getLogger().info("Unregestering inventory for player " + player.getUniqueId());
		InventoryConfig inv = registeredInventories.get(player.getUniqueId().toString());
		if(inv == null){
			Bukkit.getLogger().warning("Inventory was null!!!!\nBad things might happen");
		}
		inv.updateContents(player);
		registeredInventories.remove(inv);
		inventories.add(inv);
		Bukkit.getLogger().info("Unregistered: " + inventories.toString());
		Bukkit.getLogger().info("Registered: " + registeredInventories.toString());
	}
	
	public static InventoryConfig generateConfigForPlayer(Player player) {
		InventoryConfig config = new InventoryConfig(player);
		return config;
	}
	
	private void setPlayerInventory(Player player, InventoryConfig inventory){
		player.setAllowFlight(inventory.canFly());
		player.setBedSpawnLocation(inventory.getBedSpawnPoint());
		player.setCompassTarget(inventory.getCompassTarget());
		player.setDisplayName(inventory.getDisplayName());
		player.setExhaustion(inventory.getExhaustion());
		player.setExp(inventory.getXpPoints());
		player.setFallDistance(inventory.getFallDistance());
		player.setFireTicks(inventory.getFireTicks());
		player.setFlying(inventory.isFlying());
		player.setFoodLevel(inventory.getFoodLevel());
		player.setGameMode(inventory.getGamemode());
		player.setHealth(inventory.getHealthPoints());
		player.setLevel(inventory.getXpLevel());
		player.setRemainingAir(inventory.getRemainingAir());
		player.setSaturation(inventory.getFoodSaturation());
		player.setVelocity(inventory.getVelocity());
		player.getInventory().setArmorContents(inventory.getArmorContents());
		player.getInventory().setContents(inventory.getInventoryContents());
		player.getEnderChest().setContents(inventory.getEnderChest());
		player.teleport(inventory.getLocation());
		
	}

	public String getDefaultInventory(Player player) {
		int defaultCount = 0;
		String firstDefault = null;
		String uuid = player.getUniqueId().toString();
		if(inventories.size() > 0){
			for(InventoryConfig inv : inventories){
				String playerId = inv.getDefaultPlayer();
				if(playerId != null){
					// This inventory is a default inventory; but is it our current players default?
					invCheck:
					if(playerId.equalsIgnoreCase(uuid)){
						// Yes, this is our players default inventory; so yes the player does have a default
						defaultCount++;
						if(defaultCount > 1){
							//The player has to many defaults
							//TODO: find and "undefault" all extra inventories
							Bukkit.getLogger().warning("Player " + player.getUniqueId() + " has too many default inventories");
							break invCheck;
						}
						Bukkit.getLogger().info("Player has a default inventory");
						firstDefault = inv.getInventoryId();
					}
				}
			}	
		}
		if(firstDefault == null){
			Bukkit.getLogger().info("Player has no default inventory");
			String id = createBlankInventory();
			this.getInventory(id).setDefaultPlayer(player.getUniqueId().toString());
			return id;
		}
		return firstDefault;
	}

/*	public String getLastInventory(Player player) {
		List<String> playersInvs = new ArrayList<>();
		for(InventoryConfig inv : inventories){
			if(inv.getLastUser().equalsIgnoreCase(player.getUniqueId().toString())){
				playersInvs.add(inv.inventoryId);
			}
		}
		
		HashMap<String, Date> compareMap = new HashMap<>();
		for(String str : playersInvs){
			compareMap.put(str, getInventory(str).lastUsed);
		}
		
		Date previous = null;
		String mostRecentId = "";
		
		for(String str : compareMap.keySet()){
			if(compareMap.size() > 1){ // there must be at least one inv; the players default
				Date date = compareMap.get(str);
				try {
					if(date.after(previous)){
						mostRecentId = str;
					}
				} catch (NullPointerException e){
					mostRecentId = str;
				}
				previous = date;
			} else {
				mostRecentId = str;
				
			}
		}
		return mostRecentId;
	}*/

	public String getCurrentInventory(Player player){
		for(String invKey : registeredInventories.keySet()){
			if(invKey.equalsIgnoreCase(player.getUniqueId().toString())){
				Bukkit.getLogger().info("Id is " + registeredInventories.get(invKey).getInventoryId());
				return registeredInventories.get(invKey).getInventoryId();
			}
		}
		return null; //This should never return null
	}

}
