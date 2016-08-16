package io.github.robotman3000.bukkit.multiworld.inventory;

import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class InventoryContainer {

    private final HashMap<InventoryKey, PlayerInventory> inventories = new HashMap<>();

	private final File inventoryFolder;
    private static final String invKeyPath = "invKey";
    
    public InventoryContainer(JavaPluginFeature plugin){
        inventoryFolder = new File(plugin.getDataFolder(), "inventories");
        inventoryFolder.mkdirs();
    }
    
    protected void readInventoryConfig() {
        for (File confFile : inventoryFolder.listFiles()) {
            if (confFile.getName().contains("playerInv-") && confFile.isFile()) {
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(confFile);
                // Load the inventory keys
                
                if (!yaml.contains(invKeyPath)) {
                    yaml.createSection(invKeyPath);
                }
                
                ConfigurationSection invKeysSection = yaml.getConfigurationSection(invKeyPath);
                for (String playerKey : invKeysSection.getKeys(false)) {
                    String playerKeyPath = invKeyPath + "." + playerKey;
                    ConfigurationSection playerKeysSection = yaml.getConfigurationSection(playerKeyPath);
                    for (String worldKey : playerKeysSection.getKeys(false)) {
                        String worldKeyPath = playerKeyPath + "." + worldKey;
                        ConfigurationSection worldKeysSection = yaml.getConfigurationSection(worldKeyPath);
                        for (String gamemodeKey : worldKeysSection.getKeys(false)) {
                            String gamemodeKeyPath = worldKeyPath + "." + gamemodeKey;
                            ConfigurationSection gamemodeConfSection = yaml.getConfigurationSection(gamemodeKeyPath);

                            PlayerInventory theInv = (PlayerInventory) gamemodeConfSection.get("inventory");
                            InventoryKey invKey = new InventoryKey(playerKey, worldKey, gamemodeKey);
                            inventories.put(invKey, theInv);
                        }
                    }
                }
            }
        }
    }

    protected void saveInventoryConfig() {
        // Save the inventory keys
        for (InventoryKey key : inventories.keySet()) {
            String sectionPath = invKeyPath + "." + key.getPlayerKey().toString() + "." + key.getWorldKey() + "." + key.getGamemodeKey();

            // Save the inventory data
            File invConfFile = new File(inventoryFolder, "playerInv-" + key.getPlayerKey().toString() + ".yml");
            YamlConfiguration yaml = new YamlConfiguration();
            try {
                if (invConfFile.exists()) {
                    yaml.load(invConfFile);
                }
                yaml.set(sectionPath + ".inventory", inventories.get(key));
                yaml.save(invConfFile);
            } catch (IOException e) {
                e.printStackTrace();
                Bukkit.getLogger()
                        .severe("InventoryManager: Failed to save an inventory to disk, the inventory has been lost!!");
                Bukkit.getLogger().severe("InventoryManager: The player id of the inventory was: "
                                                  + key.getPlayerKey());
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
            zeroPlayerInventory(player, theKey.getGamemodeKey());
        }
    }

	private void saveInventory(InventoryKey inv, Player player) {
		inventories.put(inv, PlayerInventory.getPlayerInventory(player));
	}
	
	//TODO: move this functionality into PlayerInventory
    public static void zeroPlayerInventory(Player player, GameMode gameMode) {
        // TODO: Find out the proper default values
        // The player should be allowed to fly if in creative mode or spectator mode
        player.setAllowFlight((gameMode == GameMode.CREATIVE || gameMode == GameMode.SPECTATOR));
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
