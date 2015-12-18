package io.github.robotman3000.bukkit.multiworld.world;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class WorldManagerHelper {

    public static String[] asStringArray(World world) {
        // TODO: Add more color
        return new String[] { "---- Info For [" + world.getName() + "] ----",
                "UUID: " + world.getUID().toString(), "Difficulty: " + world.getDifficulty(),
                "Enviroment: " + world.getEnvironment(),
                "Allow Monster Spawns: " + world.getAllowMonsters(),
                "Allow Animal Spawns: " + world.getAllowAnimals(),
                "Keep Spawn Loaded: " + world.getKeepSpawnInMemory(),
                "Type: " + world.getWorldType(), "Seed: " + world.getSeed(),
                "Generate Structures: " + world.canGenerateStructures(),
                "PVP Enabled: " + world.getPVP(), "Generator: " + "null",
                "Autosave Enabled: " + world.isAutoSave(),
                "Spawn Location: " + world.getSpawnLocation(), "Gamerules: " + world.getGameRules() };
    }

    public static boolean isGamerule(String string) {
        try {
            GameruleList.valueOf(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static Location isLocationSafe(Location loc) {
        // TODO: Make this more persistent is finding a safe spawn
        int blockInc = 0;
        while (loc.getWorld().getMaxHeight() > blockInc) {
            Block block = loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + blockInc,
                                                    loc.getBlockZ());
            if (block.getLightFromSky() > 0) {
                // We may have found a safe spawn location
                if (block.getType() == Material.AIR || block.getType() == Material.WATER) {
                    Block block2 = loc.getWorld().getBlockAt(loc.getBlockX(),
                                                             loc.getBlockY() + blockInc + 1,
                                                             loc.getBlockZ());
                    if (block2.getType() == Material.AIR || block2.getType() == Material.WATER) {
                        return block.getLocation();
                    }
                }
            }
            blockInc++;
        }
        return null;
    }

    public static boolean isWorldFolder(File theFile) {
        if (theFile != null) {
            if (theFile.isDirectory()) {
                for (File file : theFile.listFiles()) {
                    if (file.getName().equalsIgnoreCase("level.dat")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isWorldProperty(String string) {
        try {
            WorldPropertyList.valueOf(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static <T extends Enum<?>> String printEnum(T[] theEnum) {
        StringBuilder build = new StringBuilder();
        for (T prop2 : theEnum) {
            build.append(prop2.toString() + ", ");
        }
        build.delete(build.length() - 2, build.length());
        return build.toString();
    }
}
