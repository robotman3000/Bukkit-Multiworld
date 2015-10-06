package io.github.robotman3000.bukkit.multiworld.inventory;

import io.github.robotman3000.bukkit.multiworld.MultiWorld;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class BukkitInventories {

    public enum InventoryResult {
        INVENTORY_REGISTERED, INVENTORY_UNREGISTERED, INVENTORY_CREATED, FAILED,
    }

    public static void updateInventoryContents(Player player, BukkitInventory inv) {
        inv.setCanFly(player.getAllowFlight());
        inv.setBedSpawnPoint(player.getBedSpawnLocation());
        inv.setCompassTarget(player.getCompassTarget());
        inv.setDisplayName(player.getDisplayName());
        inv.setExhaustion(player.getExhaustion());
        inv.setXpPoints(player.getExp());
        inv.setFallDistance(player.getFallDistance());
        inv.setFireTicks(player.getFireTicks());
        inv.setFlying(player.isFlying());
        inv.setFoodLevel(player.getFoodLevel());
        inv.setHealthPoints(player.getHealth());
        inv.setXpLevel(player.getLevel());
        inv.setRemainingAir(player.getRemainingAir());
        inv.setFoodSaturation(player.getSaturation());
        inv.setVelocity(player.getVelocity());
        inv.setArmorContents(player.getInventory().getArmorContents());
        inv.setInventoryContents(player.getInventory().getContents());
        inv.setEnderChest(player.getEnderChest().getContents());
        inv.setLocation(player.getLocation());
    }

    public static void updatePlayerInventory(Player player, BukkitInventory inventory) {
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
        player.setHealth(inventory.getHealthPoints());
        player.setLevel(inventory.getXpLevel());
        player.setRemainingAir(inventory.getRemainingAir());
        player.setSaturation(inventory.getFoodSaturation());
        player.setVelocity(inventory.getVelocity());
        player.getInventory().setArmorContents(inventory.getArmorContents());
        player.getInventory().setContents(inventory.getInventoryContents());
        player.getEnderChest().setContents(inventory.getEnderChest());
        // player.teleport(inventory.getLocation());
    }

    private static void zeroPlayerInventory(Player player) {
        // TODO: Finish adding all the things that need to be cleared
        player.getActivePotionEffects().clear();
        player.getEnderChest().clear();
        player.getEquipment().clear();
        player.getInventory().clear();
    }

    // TODO: use the group name as part of the key instead of the world name
    private final HashSet<InventoryKey> unregisteredInventories = new HashSet<>();

    private final HashSet<InventoryKey> registeredInventories = new HashSet<>();

    public void checkInventoryForEvent(InventoryKey playerState, Player player) {
        String inventoryErrorMessage = "InventoryManager: You have been kicked to protect your inventory from errors; The errors have been fixed so you may now reconnect";
        // This method is the error checker
        // If the inventory sets are in an inconsistent state this method
        // fixes the errors

        // Is this redundant with the next safety check?
        /*		for(InventoryKey key : registeredInventories){
        			// If the inventory we want is in here then we kick him and reset
        			// because a player should never try to register the same inventory twice
        			if(key.playerStateMatches(beforeState)){
        				// This means his old inventory is still registered which
        				// would cause an invalid state if his new inventory is also registered
        				
        				// The player getting kicked will trigger an event which will unregister the offending inventory and fix the error
        				beforeState.getPlayer().kickPlayer(inventoryErrorMessage);
        			}
        		}*/

        // Now we check if the player has more than one inventory already registered
        HashSet<InventoryKey> invCounter = new HashSet<>();
        for (InventoryKey key : registeredInventories) {
            if (key.getPlayerKey().equals(player.getUniqueId())) {
                // Add all inventories that belong to this player to our list
                invCounter.add(key);
            }
        }
        if (invCounter.size() > 1) {
            // If there is more than one inventory then there is an error that needs fixed
            player.kickPlayer(inventoryErrorMessage);
            for (InventoryKey key : invCounter) {
                // After kicking the player we unregister every inventory that belongs to him
                registeredInventories.remove(key);
                unregisteredInventories.add(key);
            }
        }

        // Check if the new inventory exists and if not fix it
        boolean keyMatched = false;
        for (InventoryKey key : unregisteredInventories) {
            // We cycle through all the available inventories
            if (key.equals(playerState)) {
                // if the current inventory matches then it is all systems go
                keyMatched = true;
            }
        }
        if (!keyMatched) {
            // If the inventory key we want doesn't exist then we create a new blank inventory for
            // that key
            unregisteredInventories.add(new InventoryKey(playerState.getPlayerKey().toString(),
                    playerState.getWorldKey(), playerState.getGamemodeKey().toString(),
                    new BukkitInventory(player, true)));
        }
    }

    protected boolean listInvCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (unregisteredInventories.size() == 0) {
            sender.sendMessage(ChatColor.GOLD + "There are no Unregistered inventories");
        } else {
            for (InventoryKey key : unregisteredInventories) {
                BukkitInventory inv = key.getInventory();
                if (inv != null) {
                    sender.sendMessage("Unregistered Inv: " + ChatColor.BLUE
                            + showPlayerName(key.getPlayerKey()) + " " + ChatColor.GREEN
                            + key.getWorldKey() + " " + ChatColor.YELLOW
                            + key.getGamemodeKey().toString() + " " + ChatColor.WHITE
                            + inv.getInventoryId());
                }
            }
        }

        if (registeredInventories.size() == 0) {
            sender.sendMessage(ChatColor.GOLD + "There are no Registered inventories");
        } else {
            for (InventoryKey key : registeredInventories) {
                BukkitInventory inv = key.getInventory();
                if (inv != null) {
                    sender.sendMessage("Registered Inv: " + ChatColor.BLUE
                            + showPlayerName(key.getPlayerKey()) + " " + ChatColor.GREEN
                            + key.getWorldKey() + " " + ChatColor.YELLOW
                            + key.getGamemodeKey().toString() + " " + ChatColor.WHITE
                            + inv.getInventoryId());
                }
            }
        }
        return true;
    }

    protected void readInventoryConfig(MultiWorld plugin) {
        File inventoryFolder = new File(plugin.getDataFolder(), "inventories");
        inventoryFolder.mkdirs();

        for (File confFile : inventoryFolder.listFiles()) {
            if (confFile.getName().contains("playerInv-") && confFile.isFile()) {
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(confFile);
                // Load the inventory keys
                String invKeyPath = "invKey";
                if (!yaml.contains(invKeyPath)) {
                    yaml.createSection(invKeyPath);
                }
                ConfigurationSection invKeysSection = yaml.getConfigurationSection(invKeyPath);
                for (String playerKey : invKeysSection.getKeys(false)) {
                    String playerKeyPath = invKeyPath + "." + playerKey;
                    ConfigurationSection playerKeysSection = yaml
                            .getConfigurationSection(playerKeyPath);
                    for (String worldKey : playerKeysSection.getKeys(false)) {
                        String worldKeyPath = playerKeyPath + "." + worldKey;
                        ConfigurationSection worldKeysSection = yaml
                                .getConfigurationSection(worldKeyPath);
                        for (String gamemodeKey : worldKeysSection.getKeys(false)) {
                            String gamemodeKeyPath = worldKeyPath + "." + gamemodeKey;
                            ConfigurationSection gamemodeConfSection = yaml
                                    .getConfigurationSection(gamemodeKeyPath);

                            BukkitInventory theInv = (BukkitInventory) gamemodeConfSection
                                    .get("inventory");
                            InventoryKey invKey = new InventoryKey(playerKey, worldKey,
                                    gamemodeKey, theInv);
                            unregisteredInventories.add(invKey);
                        }
                    }
                }
            }
        }
    }

    protected InventoryResult registerInventory(InventoryKey beforeState, InventoryKey afterState,
            Player player) {
        for (InventoryKey key : unregisteredInventories) {
            if (key.equals(afterState)) {
                boolean var1 = unregisteredInventories.remove(key);
                BukkitInventories.updatePlayerInventory(player, key.getInventory());
                boolean var2 = registeredInventories.add(key);
                return ((var1 && var2) ? InventoryResult.INVENTORY_REGISTERED
                        : InventoryResult.FAILED);
            }
        }
        return InventoryResult.FAILED;
    }

    protected void saveInventoryConfig(MultiWorld plugin) {

        // Save the inventory keys
        String invKeyPath = "invKey";
        File invDataFolder = new File(plugin.getDataFolder(), "inventories");
        invDataFolder.mkdirs();
        for (InventoryKey key : unregisteredInventories) {
            String sectionPath = invKeyPath + "." + key.getPlayerKey().toString() + "."
                    + key.getWorldKey() + "." + key.getGamemodeKey();

            // Save the inventory data
            File invConfFile = new File(invDataFolder, "playerInv-" + key.getPlayerKey().toString()
                    + ".yml");
            YamlConfiguration yaml = new YamlConfiguration();
            try {
                if (invConfFile.exists()) {
                    yaml.load(invConfFile);
                }
                yaml.set(sectionPath + ".inventory", key.getInventory());
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

    private String showPlayerName(UUID playerKey) {
        Player player = Bukkit.getPlayer(playerKey);
        if (player == null) {
            return playerKey.toString();
        }
        return player.getName();
    }

    protected InventoryResult unregisterInventory(InventoryKey beforeState,
            InventoryKey afterState, Player player) {
        for (InventoryKey key : registeredInventories) {
            if (key.equals(beforeState)) {
                boolean var1 = registeredInventories.remove(key);
                BukkitInventories.updateInventoryContents(player, key.getInventory());
                BukkitInventories.zeroPlayerInventory(player);
                boolean var2 = unregisteredInventories.add(key);
                return ((var1 && var2) ? InventoryResult.INVENTORY_REGISTERED
                        : InventoryResult.FAILED);
            }
        }
        return InventoryResult.FAILED;
    }
}
