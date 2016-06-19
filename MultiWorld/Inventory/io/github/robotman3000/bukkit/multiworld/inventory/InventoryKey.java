package io.github.robotman3000.bukkit.multiworld.inventory;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;

public class InventoryKey {

    private final UUID playerKey;
    private final String worldKey;
    private final GameMode gamemodeKey;

    public InventoryKey(String playerKey, String worldKey, String gamemodeKey) {
        this(UUID.fromString(playerKey), worldKey, gamemodeKey);
    }

    public InventoryKey(UUID playerKey, String worldKey, GameMode gamemodeKey) {
        this(playerKey, worldKey, gamemodeKey.name());
    }

    public InventoryKey(UUID playerKey, String worldKey, String gamemodeKey) {
        this.playerKey = playerKey;
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
        InventoryContainer.bool = true;
        // Bukkit.getLogger().info("[SpigotPlus] DWN: <" + Thread.currentThread().getName()
        // + "> IK.equals{" + obj.hashCode() + "} "
        // + InventoryContainer.bool);
        if (obj instanceof InventoryKey) {
            InventoryKey key = (InventoryKey) obj;
            // Bukkit.getLogger().info("[SpigotPlus] DWN: Loc2: key=" + key.toString());
            if (gamemodeKey.equals(key.gamemodeKey)) {
                // Bukkit.getLogger().info("[SpigotPlus] DWN: Loc3");
                if (playerKey.equals(key.playerKey)) {
                    // Bukkit.getLogger().info("[SpigotPlus] DWN: Loc4");
                    if (worldKey.equals(key.worldKey)) {
                        // Bukkit.getLogger().info("[SpigotPlus] DWN: Loc5");
                        return true;
                    }
                    // Bukkit.getLogger().info("[SpigotPlus] DWN: Loc4x");
                }
                // Bukkit.getLogger().info("[SpigotPlus] DWN: Loc3x");
            }
            // Bukkit.getLogger().info("[SpigotPlus] DWN: Loc2x");
        }
        // Bukkit.getLogger().info("[SpigotPlus] DWN: Loc1x/false");
        return false;
    }

    public GameMode getGamemodeKey() {
        return gamemodeKey;
    }

    public UUID getPlayerKey() {
        return playerKey;
    }

    public String getWorldKey() {
        return worldKey;
    }

    @Override
    public int hashCode() {
        int result = 3; // This just needs to start as non-zero

        int c1 = gamemodeKey.hashCode();
        result = 37 * result + c1;

        int c2 = playerKey.hashCode();
        result = 37 * result + c2;

        int c3 = worldKey.hashCode();
        result = 37 * result + c3;

        return result;
    }

    @Override
    public String toString() {
        return "InventoryKey {" + hashCode() + "} {" + System.identityHashCode(this)
                + "} [playerKey" + "=" + playerKey + ", worldKey=" + worldKey + ", gamemodeKey="
                + gamemodeKey + "]";
    }
}
