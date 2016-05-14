package io.github.robotman3000.bukkit.multiworld.gamemode;

import io.github.robotman3000.bukkit.multiworld.SpigotPlus;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class GamemodeManager extends JavaPluginFeature<SpigotPlus> {

    private final HashMap<String, GameMode> gamemodes = new HashMap<>();

    public GamemodeManager(SpigotPlus multiWorld) {
        super(multiWorld, "Gamemode Manager");
    }

    private void forceGamemode(PlayerEvent evt) {
        logInfo("Forcing Gamemode");
        if (!evt.getPlayer().hasPermission("multiworld.gamemodeExempt")) {
            GameMode gamemode = gamemodes.get(evt.getPlayer().getWorld().getName());
            if (gamemode == null) {
                gamemode = Bukkit.getDefaultGameMode();
            }
            if (!evt.getPlayer().getGameMode().equals(gamemode)) {
                evt.getPlayer().setGameMode(gamemode);
            } else {
                logInfo("No gamemode change needed");
            }
        } else {
            logInfo("Player is exempt from gamemode force");
        }
    }

    @Override
    public void initalize() {
        logInfo("Registering Event Handlers");
        for (Listener evt : getEventHandlers()) {
            getPlugin().getServer().getPluginManager().registerEvents(evt, getPlugin());
        }
        logInfo("Loading Config");
        loadConfig();
    }

    @Override
    protected void loadConfig() {
        String path = "gamemode";
        for (GameMode gamemode : GameMode.values()) {
            List<String> list = getFeatureConfig().getStringList(path + "." + gamemode.name());
            if (list != null) {
                for (String str : list) {
                    GameMode game = gamemodes.put(str, gamemode);
                    if (game != null) {
                        logWarn("The world " + str + " already had the gamemode " + game
                                + " assigned. It has been overwriten");
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent evt) {
        forceGamemode(evt);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent evt) {
        forceGamemode(evt);
    }

    @Override
    protected void saveConfig() {
        HashMap<GameMode, List<String>> config = new HashMap<GameMode, List<String>>();
        for (GameMode gamemode : GameMode.values()) {
            config.put(gamemode, new ArrayList<String>());
        }

        for (World world : Bukkit.getWorlds()) {
            GameMode gamemode = gamemodes.get(world.getName());
            if (gamemode == null) {
                gamemode = Bukkit.getDefaultGameMode();
                logInfo("The world " + world.getName()
                        + " was not defined in the config. Defining it with gamemode "
                        + gamemode.name());
            }
            config.get(gamemode).add(world.getName());

        }
        for (GameMode gamemode : config.keySet()) {
            getFeatureConfig().set("gamemode." + gamemode.name(), config.get(gamemode));
        }
    }

    @Override
    public void shutdown() {
        logInfo("Shutting Down...");
        saveConfig();
    }
}
