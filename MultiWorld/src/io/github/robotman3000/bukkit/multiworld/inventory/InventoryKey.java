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
        this(UUID.fromString(playerKey), worldKey, gamemodeKey, inventory);
    }

    public InventoryKey(UUID playerKey, String worldKey, GameMode gamemodeKey,
            BukkitInventory inventory) {
        this(playerKey, worldKey, gamemodeKey.name(), inventory);
    }

    public InventoryKey(UUID playerKey, String worldKey, String gamemodeKey,
            BukkitInventory inventory) {
        this.playerKey = playerKey;
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
            if (gamemodeKey.equals(key.gamemodeKey)) {
                if (playerKey.equals(key.playerKey)) {
                    if (worldKey.equals(key.worldKey)) {
                        return true;
                    }
                }
            }
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

    @Override
    public String toString() {
        return "InventoryKey [playerKey=" + playerKey + ", worldKey=" + worldKey + ", gamemodeKey="
                + gamemodeKey + "]";
    }
}
