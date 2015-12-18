package io.github.robotman3000.bukkit.multiworld.world;

import org.bukkit.World;

public enum GameruleList {
    commandBlockOutput, doDaylightCycle, doEntityDrops, doFireTick, doMobLoot, doMobSpawning, doTileDrops, keepInventory, logAdminCommands, mobGriefing, naturalRegeneration, randomTickSpeed, reducedDebugInfo, sendCommandFeedback, showDeathMessages;

    // The "name()" method is defined by enum.name();
    public String getPropertyValue(World world) {
        return world.getGameRuleValue(name());
    }

    public boolean setPropertyValue(World world, String newValue) {
        return world.setGameRuleValue(name(), newValue);
    }
}
