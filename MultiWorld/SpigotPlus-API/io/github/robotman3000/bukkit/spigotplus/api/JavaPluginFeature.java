package io.github.robotman3000.bukkit.spigotplus.api;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class JavaPluginFeature extends JavaPlugin implements Listener {

	public static int API_MAJOR_VERSION = 1;
	public static int API_MINOR_VERSION = 0;
	private boolean loadFailed = false;

	@Override
	public final void onEnable() {
		if (API_MAJOR_VERSION < getRequiredMajorVersion()) {
			logSevere("API version is too old! ( " + API_MAJOR_VERSION + "." + API_MINOR_VERSION + " < "
					+ getRequiredMajorVersion() + "." + getRequiredMinorVersion() + " )");
			loadFailed = true;
		} else {
			if(API_MAJOR_VERSION > getRequiredMajorVersion()){
				logWarn("Major API version is newer than the minimum. Things may not work as expected");
			}
			
	        for (JavaPluginCommandList cmd : getCommands()) { // Register Commands
	            logInfo("Registering Command: " + cmd.name());
	            PluginCommand pCmd = getCommand(cmd.name());
	            pCmd.setExecutor(cmd.getExecutor());
	            pCmd.setTabCompleter(cmd.getTabCompleter());
	        }

	        for (Listener evt : getEventHandlers()) {
	        	logInfo("Registering Event Listener: " + evt);
	            getServer().getPluginManager().registerEvents(evt, this);
	        }
	        
	        saveDefaultConfig();
			if(!(loadFailed = startup())){
				logWarn("Failed to startup!!");
			}
		}
		
		if(loadFailed){
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}

	@Override
	public final void onDisable() {
		if (!loadFailed) {
			shutdown();
		}
	}
	
	protected final void logInfo(String msg) {
		Bukkit.getLogger().info("[" + getName() + "] " + msg);
	}

	protected final void logSevere(String msg) {
		Bukkit.getLogger().severe("[" + getName() + "] " + msg);
	}

	protected final void logWarn(String msg) {
		Bukkit.getLogger().warning("[" + getName() + "] " + msg);
	}
	
	protected JavaPluginCommandList[] getCommands() {
		return new JavaPluginCommandList[0];
	}

	protected Listener[] getEventHandlers() {
		return new Listener[] { this };
	}

	protected boolean startup() {
		return true;
	}
	
	protected void shutdown() {
		
	}

	public abstract int getRequiredMajorVersion();

	public abstract int getRequiredMinorVersion();
}
