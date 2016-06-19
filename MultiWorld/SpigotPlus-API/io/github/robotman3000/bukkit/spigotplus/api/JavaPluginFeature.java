package io.github.robotman3000.bukkit.spigotplus.api;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class JavaPluginFeature extends JavaPlugin implements Listener {

	private String name = "";
	private final String VERSION = "v0.2.1-SNAPSHOT";
	private int MAJOR_VERSION = 2;
	private int MINOR_VERSION = 1;
	private boolean loadFailed = false;

	@Override
	public final void onEnable() {
		if (getAPIMajorVersion() < getMinimumMajorVersion()) {
			logSevere("API version is too old! ( " + getAPIMajorVersion() + "." + getAPIMinorVersion() + " < "
					+ getMinimumMajorVersion() + "." + getMinimumMinorVersion() + " )");
			loadFailed = true;
			Bukkit.getPluginManager().disablePlugin(this);
		} else {
			if(getAPIMajorVersion() > getMinimumMajorVersion()){
				logWarn("Major API version is newer than the minimum. Things may not work as expected");
			}
			
	        for (JavaPluginCommand cmd : getCommands()) { // Register Commands
	            logInfo("Registering Command: " + cmd);
	            PluginCommand pCmd = getPlugin().getCommand(cmd.name());
	            pCmd.setExecutor(cmd.getExecutor());
	            pCmd.setTabCompleter(cmd.getTabCompleter());
	        }

	        logInfo("Registering Event Handlers");
	        for (Listener evt : getEventHandlers()) {
	            getPlugin().getServer().getPluginManager().registerEvents(evt, getPlugin());
	        }
	        
			initalize();
			
	        logInfo("Loading Config");
	        loadConfig();
		}
	}

	protected JavaPluginCommand[] getCommands() {
		return new JavaPluginCommand[0];
	}

	@Override
	public final void onDisable() {
		if (!loadFailed) {
			shutdown();
		}
	}

	protected final String getConfigPath() {
		return getName() + ".config." + getFeatureName().replaceAll(" ", "");
	}

	protected Listener[] getEventHandlers() {
		return new Listener[] { this };
	}

	protected final ConfigurationSection getFeatureConfig() {
		if (!getPlugin().getConfig().contains(getConfigPath())) {
			getPlugin().getConfig().createSection(getConfigPath());
		}
		return getPlugin().getConfig().getConfigurationSection(getConfigPath());
	}

	protected void setFeatureName(String str) {
		this.name = str;
	}

	public String getFeatureName() {
		return name;
	}

	protected JavaPlugin getPlugin() {
		return this;
	}

	public boolean initalize() {
		return true;
	}

	protected void loadConfig() {

	}

	protected void logInfo(String msg) {
		Bukkit.getLogger().info("[" + getPlugin().getName() + "] " + msg);
	}

	protected void logSevere(String msg) {
		Bukkit.getLogger().severe("[" + getPlugin().getName() + "] " + msg);
	}

	protected void logWarn(String msg) {
		Bukkit.getLogger().warning("[" + getPlugin().getName() + "] " + msg);
	}

	public void shutdown() {
		saveConfig();
	}

	public abstract int getMinimumMajorVersion();

	public abstract int getMinimumMinorVersion();

	public int getAPIMajorVersion() {
		return MAJOR_VERSION;
	}

	public int getAPIMinorVersion() {
		return MINOR_VERSION;
	}
}
