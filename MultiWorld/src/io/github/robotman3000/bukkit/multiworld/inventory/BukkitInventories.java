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

    /**
     * Updates the contents of the provided inventory with the given player
     * 
     * @param player
     * @param inv
     */
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

    /**
     * Update the inventory of the given player with the provided inventory
     * 
     * @param player
     * @param inventory
     */
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

    private final HashSet<InventoryKey> playerInventories = new HashSet<>();

    protected boolean listInvCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (playerInventories.size() == 0) {
            sender.sendMessage(ChatColor.GOLD + "There are no Registered inventories");
        } else {
            for (InventoryKey key : playerInventories) {
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
                            playerInventories.add(invKey);
                        }
                    }
                }
            }
        }
    }

    protected void saveInventoryConfig(MultiWorld plugin) {

        // Save the inventory keys
        String invKeyPath = "invKey";
        File invDataFolder = new File(plugin.getDataFolder(), "inventories");
        invDataFolder.mkdirs();
        for (InventoryKey key : playerInventories) {
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

    protected boolean updatePlayerFromKeys(InventoryKey beforeState, InventoryKey afterState,
            Player player) {

        if (!playerInventories.contains(beforeState)) {
            Bukkit.getLogger().warning("[MultiWorld] Creating \"before\" inventory for "
                                               + player.getName());
            playerInventories
                    .add(new InventoryKey(beforeState.getPlayerKey(), beforeState.getWorldKey(),
                            beforeState.getGamemodeKey(), new BukkitInventory(player, true)));
        }
        if (!playerInventories.contains(afterState)) {
            Bukkit.getLogger().warning("[MultiWorld] Creating \"after\" inventory for "
                                               + player.getName());
            playerInventories
                    .add(new InventoryKey(afterState.getPlayerKey(), afterState.getWorldKey(),
                            afterState.getGamemodeKey(), new BukkitInventory(player, true)));
        }

        InventoryKey beforeKey = null;
        InventoryKey afterKey = null;
        ifEnd: if (playerInventories.contains(beforeState)
                && playerInventories.contains(afterState)) {

            for (InventoryKey key : playerInventories) {
                if (key.equals(beforeState)) {
                    beforeKey = key;
                }
                if (key.equals(afterState)) {
                    afterKey = key;
                }
            }

            if (afterKey == null || beforeKey == null) {
                break ifEnd;
            }

            BukkitInventories.updateInventoryContents(player, beforeKey.getInventory());
            BukkitInventories.zeroPlayerInventory(player);
            BukkitInventories.updatePlayerInventory(player, afterKey.getInventory());
            return true;
        }

        player.kickPlayer("InventoryManager: Your inventories have somehow become nonexistant. I blame the creepers.. er, the zombies.. er, both of them!");
        Bukkit.getLogger().severe("[MultiWorld] What?!? The inventories of " + player.getName()
                                          + " have ceased to exist; Perhaps they never were?");
        Bukkit.getLogger().severe("[MultiWorld] " + player.getName() + " before inventory: "
                                          + beforeKey);
        Bukkit.getLogger().severe("[MultiWorld] " + player.getName() + " after inventory: "
                                          + afterKey);
        return false;
    }
}
