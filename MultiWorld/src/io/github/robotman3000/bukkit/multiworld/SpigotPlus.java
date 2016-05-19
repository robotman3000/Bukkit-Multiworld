package io.github.robotman3000.bukkit.multiworld;

import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotPlus extends JavaPlugin {

	private final ArrayList<JavaPluginFeature<? extends JavaPlugin>> features = new ArrayList<>();

	@Override
	public void onDisable() {
		// Reminder: Don't assume that this is only called on a server restart
		for (JavaPluginFeature<? extends JavaPlugin> feature : features) {
			Bukkit.getLogger().info(
					"[SpigotPlus] Disabling plugin feature \""
							+ feature.getFeatureName() + "\"");
			feature.shutdown();
		}
		saveConfig();
	}

	@Override
	public void onEnable() {
		// Reminder: Don't assume that this is only called on a server restart
		saveDefaultConfig();

		List<String> enabledFeatures = getConfig().getStringList("SpigotPlus.feature");
		if(enabledFeatures.isEmpty()){
			Bukkit.getLogger().warning("[SpigotPlus] No features have been enabled so there is no need for this plugin. Automatically disabling plugin");
			
			// The list was empty so we can use it instead of creating a new object
			getConfig().set("SpigotPlus.feature", enabledFeatures);
			Bukkit.getPluginManager().disablePlugin(this);
		}
		for(String featurePath : enabledFeatures){
			try {
				Class<?> temp = Class.forName(featurePath);
				@SuppressWarnings("unchecked")
				Class<JavaPluginFeature<JavaPlugin>> featureClass = (Class<JavaPluginFeature<JavaPlugin>>) temp.asSubclass(JavaPluginFeature.class);
				Constructor<JavaPluginFeature<JavaPlugin>> constructor = featureClass.getConstructor(SpigotPlus.class);
				JavaPluginFeature<JavaPlugin> feature = constructor.newInstance(this);
				
				Bukkit.getLogger().info("[SpigotPlus] Initializing plugin feature \"" + feature.getFeatureName() + "\"");	
				if(feature.initalize()){
					features.add(feature);
				} else {
					feature.shutdown();
					Bukkit.getLogger().warning("[SpigotPlus] The plugin feature \"" + feature.getFeatureName() + "\" reports that it failed to initialize. It has been disabled");
				}
				
			} catch (ClassNotFoundException e) {
				Bukkit.getLogger().severe("[SpigotPlus] Failed to locate the JavaPluginFeature class for the path: " + featurePath);
				e.printStackTrace();
			} catch (ClassCastException e){
				Bukkit.getLogger().severe("[SpigotPlus] " + featurePath + " does not extend JavaPluginFeature and cannot be loaded");
				e.printStackTrace();
			} catch(NoSuchMethodException e){
				Bukkit.getLogger().severe("[SpigotPlus] The constructor of the plugin feature " + featurePath + " does not take the correct parameters and cannot be loaded");
				e.printStackTrace();
			} catch (Exception e){
				Bukkit.getLogger().severe("[SpigotPlus] An unhandled exception occured while loading the plugin feature " + featurePath);
				e.printStackTrace();
			}
		}
	}
}
