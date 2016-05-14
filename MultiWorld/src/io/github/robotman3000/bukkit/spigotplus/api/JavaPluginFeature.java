package io.github.robotman3000.bukkit.spigotplus.api;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class JavaPluginFeature<T extends JavaPlugin> implements Listener {

    private final T plugin;
    private final String name;

    public JavaPluginFeature(T plugin, String featureName) {
        this.plugin = plugin;
        this.name = featureName;
    }

    protected final String getConfigPath() {
        return plugin.getName() + "." + getFeatureName().replaceAll(" ", "");
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

    public String getFeatureName() {
        return name;
    }

    protected T getPlugin() {
        return plugin;
    }

    public void initalize() {
        loadConfig();
    }
    
    protected void loadConfig() {

    }

    protected void logInfo(String msg) {
        Bukkit.getLogger().info("[" + getPlugin().getName() + "] " + getFeatureName() + ": " + msg);
    }

    protected void logSevere(String msg) {
        Bukkit.getLogger().severe("[" + getPlugin().getName() + "] " + getFeatureName() + ": "
                                          + msg);
    }

    protected void logWarn(String msg) {
        Bukkit.getLogger().warning("[" + getPlugin().getName() + "] " + getFeatureName() + ": "
                                           + msg);
    }

    protected void saveConfig() {

    }

    public void shutdown() {
        saveConfig();
    }
}
