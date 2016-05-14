package io.github.robotman3000.bukkit.multiworld;

import io.github.robotman3000.bukkit.multiworld.gamemode.GamemodeManager;
import io.github.robotman3000.bukkit.multiworld.inventory.InventoryManager;
import io.github.robotman3000.bukkit.multiworld.world.WorldManager;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;
import io.github.robotman3000.bukkit.spigotplus.chat.ChatManager;
import io.github.robotman3000.bukkit.spigotplus.death.DeathMessageManager;
import io.github.robotman3000.bukkit.spigotplus.misc.MiscFeatures;
import io.github.robotman3000.bukkit.spigotplus.mods.PluginModsManager;
import io.github.robotman3000.bukkit.spigotplus.performance.PeformanceMonitoringManager;
import io.github.robotman3000.bukkit.spigotplus.schedule.ServerSchedulingManager;
import io.github.robotman3000.bukkit.spigotplus.sound.AtmosphericSoundsManager;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotPlus extends JavaPlugin {

	private final ArrayList<JavaPluginFeature<SpigotPlus>> features = new ArrayList<>();

	public SpigotPlus() {
		features.add(new MiscFeatures(this));
		features.add(new WorldManager(this));
		features.add(new InventoryManager(this));
		features.add(new GamemodeManager(this));
		features.add(new DeathMessageManager(this));
		features.add(new ChatManager(this));
		features.add(new PeformanceMonitoringManager(this));
		features.add(new ServerSchedulingManager(this));
		features.add(new AtmosphericSoundsManager(this));
		features.add(new PluginModsManager(this));
	}

	@Override
	public void onDisable() {
		// Reminder: Don't assume that this is only called on a server restart
		for (JavaPluginFeature<SpigotPlus> feature : features) {
			String fixedName = getName() + "."
					+ feature.getFeatureName().replaceAll(" ", "-");

			if (!getConfig().contains(fixedName)) {
				getConfig().set(fixedName, false);
			}

			if (getConfig().getBoolean(fixedName)) {
				Bukkit.getLogger().info(
						"[SpigotPlus] Disabling plugin feature \""
								+ feature.getFeatureName() + "\"");
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
			String fixedName = getName() + "."
					+ feature.getFeatureName().replaceAll(" ", "-");

			if (!getConfig().contains(fixedName)) {
				getConfig().set(fixedName, false);
			}

			if (getConfig().getBoolean(fixedName)) {
				Bukkit.getLogger().info(
						"[SpigotPlus] Initializing plugin feature \""
								+ feature.getFeatureName() + "\"");
				feature.initalize();
			}
		}
	}
}
