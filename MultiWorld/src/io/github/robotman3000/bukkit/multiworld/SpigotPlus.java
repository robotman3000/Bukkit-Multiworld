package io.github.robotman3000.bukkit.multiworld;

import io.github.robotman3000.bukkit.multiworld.gamemode.GamemodeManager;
import io.github.robotman3000.bukkit.multiworld.inventory.InventoryManager;
import io.github.robotman3000.bukkit.multiworld.world.WorldManager;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;
import io.github.robotman3000.bukkit.spigotplus.misc.MiscFeatures;

import java.util.ArrayList;

import org.bukkit.plugin.java.JavaPlugin;

public class SpigotPlus extends JavaPlugin {

    private final ArrayList<JavaPluginFeature<SpigotPlus>> features = new ArrayList<>();

    public SpigotPlus() {
        features.add(new MiscFeatures(this));
        features.add(new WorldManager(this));
        features.add(new InventoryManager(this));
        features.add(new GamemodeManager(this));
    }

    @Override
    public void onDisable() {
        // Reminder: Don't assume that this is only called on a server restart
        for (JavaPluginFeature<SpigotPlus> feature : features) {
            if (!getConfig().contains(getName() + "."
                                              + feature.getFeatureName().replaceAll(" ", "-"))) {
                getConfig().set(getName() + "." + feature.getFeatureName().replaceAll(" ", "-"),
                                false);
            }

            if (getConfig().getBoolean(getName() + "."
                                               + feature.getFeatureName().replaceAll(" ", "-"))) {
                feature.shutdown();
            }
        }
        saveConfig();
    }

    @Override
    public void onEnable() {
        // Reminder: Don't assume that this is only called on a server restart
        saveDefaultConfig();

        for (JavaPluginFeature<SpigotPlus> feature : features) {
            if (!getConfig().contains(getName() + "."
                                              + feature.getFeatureName().replaceAll(" ", "-"))) {
                getConfig().set(getName() + "." + feature.getFeatureName().replaceAll(" ", "-"),
                                false);
            }

            if (getConfig().getBoolean(getName() + "."
                                               + feature.getFeatureName().replaceAll(" ", "-"))) {
                feature.initalize();
            }
        }
    }
}
