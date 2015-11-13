package io.github.robotman3000.bukkit.spigotplus.misc;

import io.github.robotman3000.bukkit.multiworld.SpigotPlus;
import io.github.robotman3000.bukkit.spigotplus.api.CommandEnumMethods;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;
import io.github.robotman3000.bukkit.spigotplus.misc.command.GotoCommand;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MiscFeatures extends JavaPluginFeature<SpigotPlus> {

    private enum Commands implements CommandEnumMethods<SpigotPlus> {
        Goto {
            @Override
            public CommandExecutor getExecutor(SpigotPlus plugin) {
                return new GotoCommand();
            }

            @Override
            public TabCompleter getTabCompleter(SpigotPlus plugin) {
                return new GotoCommand();
            }
        };

        @Override
        public abstract CommandExecutor getExecutor(SpigotPlus plugin);

        @Override
        public abstract TabCompleter getTabCompleter(SpigotPlus plugin);
    }

    private boolean appendWorldInChat;

    public MiscFeatures(SpigotPlus plugin) {
        super(plugin, "Misc Features");
    }

    @Override
    public void initalize() {
        logInfo("Initializing...");
        for (Commands cmd : Commands.values()) { // Register Commands
            logInfo("Registering Command: " + cmd);
            PluginCommand pCmd = getPlugin().getCommand(cmd.name().toLowerCase());
            pCmd.setExecutor(cmd.getExecutor(getPlugin()));
            pCmd.setTabCompleter(cmd.getTabCompleter(getPlugin()));
        }

        logInfo("Registering Event Handlers");
        for (Listener evt : getEventHandlers()) {
            getPlugin().getServer().getPluginManager().registerEvents(evt, getPlugin());
        }
        logInfo("Loading Config");
        loadConfig();
    }

    @Override
    protected void loadConfig() {
        appendWorldInChat = getFeatureConfig().getBoolean("appendWorldInChat", true);
    };

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent evt) {
        if (appendWorldInChat) {
            String message = evt.getMessage();
            evt.setMessage("[" + evt.getPlayer().getWorld().getName() + "] " + message);
        }
    };

    @Override
    protected void saveConfig() {
        getFeatureConfig().set("appendWorldInChat", appendWorldInChat);
    }

    @Override
    public void shutdown() {
        logInfo("Shutting Down...");
    }
}
