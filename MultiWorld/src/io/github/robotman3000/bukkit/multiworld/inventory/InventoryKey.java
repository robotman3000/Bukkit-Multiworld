package io.github.robotman3000.bukkit.multiworld.inventory;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;

public class InventoryKey {

    private final BukkitInventory theInv;
    private final UUID playerKey;
    private final String worldKey;
    private final GameMode gamemodeKey;

    public InventoryKey(String playerKey, String worldKey, String gamemodeKey,
            BukkitInventory inventory) {
        this.playerKey = UUID.fromString(playerKey);
        theInv = inventory;
        this.worldKey = worldKey;
        GameMode gamemode = Bukkit.getDefaultGameMode();
        try {
            gamemode = GameMode.valueOf(gamemodeKey);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.gamemodeKey = gamemode;
        }
    }

    @Override
    public boolean equals(Object obj) {
        // Bukkit.getLogger().warning("Obj: " + obj);
        if (obj instanceof InventoryKey) {
            // Bukkit.getLogger().warning("It is a InventoryKey");
            InventoryKey key = (InventoryKey) obj;
            boolean var1 = false, var2 = false, var3 = false;
            if (gamemodeKey.equals(key.gamemodeKey)) {
                // Bukkit.getLogger().warning("Gamemode Matched");
                var1 = true;
            }
            if (playerKey.equals(key.playerKey)) {
                // Bukkit.getLogger().warning("Player Matched");
                var2 = true;
            }
            if (worldKey.equals(key.worldKey)) {
                // Bukkit.getLogger().warning("World matched");
                var3 = true;
            }
            return (var1 && var2 && var3);
        }
        return false;
    }

    public GameMode getGamemodeKey() {
        return gamemodeKey;
    }

    public BukkitInventory getInventory() {
        return theInv;
    }

    public UUID getPlayerKey() {
        return playerKey;
    }

    public String getWorldKey() {
        return worldKey;
    }

    /*    public boolean playerStateMatches(PlayerState playerState) {
            if (gamemodeKey.equals(playerState.getGamemode())
                    && playerKey.equals(playerState.getPlayer().getUniqueId())
                    && worldKey.equals(playerState.getWorld().getName())) {
                return true;
            }
            return false;
        }*/

    @Override
    public String toString() {
        return "InventoryKey [playerKey=" + playerKey + ", worldKey=" + worldKey + ", gamemodeKey="
                + gamemodeKey + "]";
    }

}
