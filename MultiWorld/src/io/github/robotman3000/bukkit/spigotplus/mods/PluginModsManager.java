package io.github.robotman3000.bukkit.spigotplus.mods;

import io.github.robotman3000.bukkit.multiworld.SpigotPlus;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.plugin.java.JavaPlugin;

public class PluginModsManager extends JavaPluginFeature<SpigotPlus> {

	private Set<PluginModBase> managedMods = new HashSet<>();
	
	public PluginModsManager(SpigotPlus plugin) {
		super(plugin, "Plugin Based Mods");
	}

	@Override
	public void initalize() {
		loadConfig();
		List<String> enabledMods = getFeatureConfig().getStringList("mods");
		for(String mod : enabledMods){
			try {
				Class<?> temp = Class.forName(mod);
				@SuppressWarnings("unchecked")
				Class<PluginModBase> pluginModClass = (Class<PluginModBase>) temp.asSubclass(PluginModBase.class);
				Constructor<PluginModBase> constructor = pluginModClass.getConstructor(JavaPlugin.class);
				PluginModBase pluginMod = constructor.newInstance(getPlugin());
					
				logInfo("Initializing Plugin Mod \'" + mod + "\'");
				pluginMod.initialize(getFeatureConfig().getConfigurationSection("modConfig." + mod));
				managedMods.add(pluginMod);
			} catch (ClassNotFoundException e) {
				logSevere("Failed to locate the PluginModBase class for the path: " + mod);
				e.printStackTrace();
			} catch (ClassCastException e){
				logSevere(mod + " does not extend PluginModBase and cannot be loaded");
				e.printStackTrace();
			} catch(NoSuchMethodException e){
				logSevere("The constructor of the mod " + mod + " does not take the correct parameters and cannot be loaded");
				e.printStackTrace();
			} catch (Exception e){
				logSevere("An unhandled exception occured while loading the plugin mod " + mod);
				logWarn("Failed to load a mod");
				e.printStackTrace();
			}
		}
	}

	@Override
	public void shutdown() {
		for(PluginModBase pluginMod : managedMods){
			logInfo("Shutting Down Plugin Mod \'" + pluginMod.getName() + "\'");
			pluginMod.shutdown();
		}
		managedMods.clear();
		saveConfig();
	}

	@Override
	protected void loadConfig() {
	}

	@Override
	protected void saveConfig() {
	}

}
