package io.github.robotman3000.bukkit.multiworld.inventory;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class OldCode {
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
    
    public synchronized boolean checkSanityForPlayer(Player player, int i) {
        int invCounter = 0;
        for (InventoryKey key : usedInventories.keySet()) {
            if (key.getPlayerKey().equals(player.getUniqueId())) {
                invCounter++;
            }
        }
        return (invCounter == i);
    }

    public synchronized boolean hasUnusedInventory(InventoryKey theKey) {
        for (InventoryKey key : unusedInventories.keySet()) {
            if (key.equals(theKey)) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean hasUsedInventory(InventoryKey theKey) {
        for (InventoryKey key : usedInventories.keySet()) {
            if (key.equals(theKey)) {
                return true;
            }
        }
        return false;
    }
    
    protected synchronized boolean listInvCommand(CommandSender sender, Command cmd, String label,
            String[] args) {
        if (usedInventories.size() == 0) {
            sender.sendMessage(ChatColor.GOLD + "There are no Registered inventories");
        } else {
            for (InventoryKey key : usedInventories.keySet()) {
                BukkitInventory inv = usedInventories.get(key);
                if (inv != null) {
                    sender.sendMessage("Registered Inv: " + ChatColor.BLUE
                            + showPlayerName(key.getPlayerKey()) + " " + ChatColor.GREEN
                            + key.getWorldKey() + " " + ChatColor.YELLOW
                            + key.getGamemodeKey().toString() + " " + ChatColor.WHITE
                            + inv.getInventoryId());
                }
            }
        }

        if (unusedInventories.size() == 0) {
            sender.sendMessage(ChatColor.GOLD + "There are no Unegistered inventories");
        } else {
            for (InventoryKey key : unusedInventories.keySet()) {
                BukkitInventory inv = unusedInventories.get(key);
                if (inv != null) {
                    sender.sendMessage("Unregistered Inv: " + ChatColor.BLUE
                            + showPlayerName(key.getPlayerKey()) + " " + ChatColor.GREEN
                            + key.getWorldKey() + " " + ChatColor.YELLOW
                            + key.getGamemodeKey().toString() + " " + ChatColor.WHITE
                            + inv.getInventoryId());
                }
            }
        }
        return true;
    }
    
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
