package io.github.robotman3000.bukkit.multiworld.world;

import org.bukkit.World;

public enum GameruleList implements PropertyList {
    commandBlockOutput, doDaylightCycle, doEntityDrops, doFireTick, doMobLoot, doMobSpawning, doTileDrops, keepInventory, logAdminCommands, mobGriefing, naturalRegeneration, randomTickSpeed, reducedDebugInfo, sendCommandFeedback, showDeathMessages;

    // The "name()" method is defined by enum.name();
    @Override
    public String getPropertyValue(World world) {
        return world.getGameRuleValue(name());
    }

    @Override
    public boolean setPropertyValue(World world, String newValue) {
        return world.setGameRuleValue(name(), newValue);
    }
}
