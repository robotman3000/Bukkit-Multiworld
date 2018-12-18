package io.github.robotman3000.bukkit.multiworld.inventory;

import io.github.robotman3000.bukkit.multiworld.inventory.PlayerInventory.InventoryProperties;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@SuppressWarnings("unused")
public class InventoryContainer {

	// The String is the name of the world group
	private final HashMap<InventoryKey, PlayerInventory> inventories = new HashMap<>();

	private final File inventoryFolder;

	private InventoryManager plugin;
	private static final String invKeyPath = "invKey";

	public InventoryContainer(InventoryManager plugin) {
		this.plugin = plugin;
		inventoryFolder = new File(plugin.getDataFolder(), "inventories");
		inventoryFolder.mkdirs();
	}

	protected void readInventoryConfig() {
		//TODO: Make the inventories be saved per player per world so that we detect if the world the inventory goes to is not loaded
		for (File confFile : inventoryFolder.listFiles()) {
			try {
				if (confFile.getName().contains("playerInv-") && !confFile.getName().contains("-bak") && confFile.isFile()) {
					YamlConfiguration yaml = YamlConfiguration.loadConfiguration(confFile);
					
					// Load the inventory keys
					if (!yaml.contains(invKeyPath)) {
						yaml.createSection(invKeyPath);
					}
	
					ConfigurationSection invKeysSection = yaml.getConfigurationSection(invKeyPath);
					for (String playerKey : invKeysSection.getKeys(false)) {
						String playerKeyPath = invKeyPath + "." + playerKey;
						ConfigurationSection playerKeysSection = yaml.getConfigurationSection(playerKeyPath);
	
						for (String worldGroupKey : playerKeysSection.getKeys(false)) {
							String worldKeyPath = playerKeyPath + "." + worldGroupKey;
							ConfigurationSection section = yaml.getConfigurationSection(worldKeyPath);
							
							
				            // Upgrade old configs
				            UUID id = null;
				            try{
				            	id = UUID.fromString(worldGroupKey);
				            } catch (IllegalArgumentException e1){
				            	id = plugin.getGroupKey(worldGroupKey);
				            	if(id == null){
				            		id = UUID.nameUUIDFromBytes("default".getBytes());
				            	}
				            	
				            	Map<GameMode, ConfigurationSection> sections = new HashMap<>();
				            	ConfigurationSection tempSec = null;
		                        for (String gamemodeKey : section.getKeys(false)) {
		                            String gamemodeKeyPath = worldKeyPath + "." + gamemodeKey;
		                            GameMode gm = GameMode.valueOf(gamemodeKey);
		                         
		                            if(gm == Bukkit.getDefaultGameMode()){
		                            	tempSec = section.getConfigurationSection(gm.name());
		                            } else {
		                            	sections.put(gm, section.getConfigurationSection(gm.name()));
		                            }
		                        }
		                        if(tempSec == null){
		                        	for(ConfigurationSection se : sections.values()){
		                        		tempSec = se;
		                        		break;
		                        	}
		                        }
		                        YamlConfiguration yaml2 = new YamlConfiguration();
		                        for(Entry<GameMode, ConfigurationSection> ent : sections.entrySet()){
		                			File invConfFile = new File(inventoryFolder, playerKey + "-" + worldGroupKey + "-" + ent.getKey().name() + ".yml");
		                			try {
		                				if (invConfFile.exists()) {
		                					yaml2.load(invConfFile);
		                				}
		                				String sectionPath = invKeyPath + "." + playerKey + "." + worldGroupKey + "."
		                						+ ent.getKey().name();
		                				Bukkit.getLogger().warning("InventoryManager: Saving inventory " + sectionPath + " to " + invConfFile.getName() + " because it is an old format that is in conflict with the upgrade process");
		                				yaml2.set(sectionPath + ".inventory", ent.getValue().get(ent.getKey().name()));
		                				yaml2.save(invConfFile);
		                			} catch (IOException e) {
		                				e.printStackTrace();
		                				Bukkit.getLogger()
		                						.severe("InventoryManager: Failed to save an inventory to disk, the inventory has been lost!!");
		                				Bukkit.getLogger()
		                						.severe("InventoryManager: The player id of the inventory was: " + playerKey);
		                			} catch (InvalidConfigurationException e) {
		                				e.printStackTrace();
		                			}
		                        }
		                        section = tempSec;
				            }
				            
				            
							PlayerInventory theInv = (PlayerInventory) section.get("inventory");
							InventoryKey invKey = new InventoryKey(UUID.fromString(playerKey), id);
							inventories.put(invKey, theInv);
						}
					}
				}
			} catch (Exception e) {
				this.plugin.getLogger().severe("Failed to load inventory file " + confFile.getName() + "; Reason: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	protected void saveInventoryConfig() {
		// Save the inventory keys
		for (InventoryKey key : inventories.keySet()) {
			String sectionPath = invKeyPath + "." + key.getPlayerKey().toString() + "." + key.getWorldGroupKey();

			// Save the inventory data
			File invConfFile = new File(inventoryFolder, "playerInv-" + key.getPlayerKey().toString() + ".yml");
			YamlConfiguration yaml = new YamlConfiguration();
			try {
				if (invConfFile.exists()) {
					yaml.load(invConfFile);
				}
				yaml.set(sectionPath + ".inventory", inventories.get(key));
				yaml.set("version", 2);
				yaml.save(invConfFile);
			} catch (IOException e) {
				e.printStackTrace();
				Bukkit.getLogger()
						.severe("InventoryManager: Failed to save an inventory to disk, the inventory has been lost!!");
				Bukkit.getLogger()
						.severe("InventoryManager: The player id of the inventory was: " + key.getPlayerKey());
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}

	}

	public void registerInventory(InventoryKey theKey) {
		Player player = Bukkit.getPlayer(theKey.getPlayerKey());
		if (player != null) {
			PlayerInventory newInv = inventories.get(theKey);
			if (newInv != null) {
				PlayerInventory.setPlayerInventory(player, newInv);
			}
			saveInventory(theKey, player);
		}
	}

	public void unregisterInventory(InventoryKey theKey) {
		Player player = Bukkit.getPlayer(theKey.getPlayerKey());
		if (player != null) {
			saveInventory(theKey, player);
			zeroPlayerInventory(player);
		}
	}

	private void saveInventory(InventoryKey inv, Player player) {
		inventories.put(inv, PlayerInventory.getPlayerInventory(player));
	}

	// TODO: move this functionality into PlayerInventory
	public static void zeroPlayerInventory(Player player) {
		// TODO: Find out the proper default values

		// The player should be allowed to fly if in creative mode or spectator
		// mode
		player.setAllowFlight((player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR));
		player.setBedSpawnLocation(player.getWorld().getSpawnLocation());
		player.setCompassTarget(player.getWorld().getSpawnLocation());
		player.setDisplayName(player.getName());
		player.setExhaustion(0);
		player.setExp(0);
		player.setFallDistance(0);
		player.setFireTicks(0);
		player.setFlying(false);
		player.setFoodLevel(100);
		player.setHealth(20);
		player.setLevel(0);
		player.setRemainingAir(300);
		player.setSaturation(0);
		player.setVelocity(new Vector());
		player.getActivePotionEffects().clear();
		player.getEnderChest().clear();
		player.getEquipment().clear();
		player.getInventory().clear();
	}
}
