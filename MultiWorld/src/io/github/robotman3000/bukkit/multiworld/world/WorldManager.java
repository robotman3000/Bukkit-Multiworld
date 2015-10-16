package io.github.robotman3000.bukkit.multiworld.world;

import io.github.robotman3000.bukkit.multiworld.CommonLogic;
import io.github.robotman3000.bukkit.multiworld.MultiWorld;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.WorldInitEvent;

public class WorldManager implements Listener, CommandExecutor {

    public final String[] commands = { "createworld", "deleteworld", "generateworld",
            "unloadworld", "worldinfo", "listworlds", "gamerule", "worldproperty" };

    private final MultiWorld plugin;

    public boolean autoLoadWorlds;

    public WorldManager(MultiWorld multiWorld) {
        plugin = multiWorld;
    }

    public String[] asStringArray(World world) {
        // TODO: Add more color
        return new String[] { "---- Info For [" + world.getName() + "] ----",
                "UUID: " + world.getUID().toString(), "Difficulty: " + world.getDifficulty(),
                "Enviroment: " + world.getEnvironment(),
                "Allow Monster Spawns: " + world.getAllowMonsters(),
                "Allow Animal Spawns: " + world.getAllowAnimals(),
                "Keep Spawn Loaded: " + world.getKeepSpawnInMemory(),
                "Type: " + world.getWorldType(), "Seed: " + world.getSeed(),
                "Generate Structures: " + world.canGenerateStructures(),
                "PVP Enabled: " + world.getPVP(), "Generator: " + "null",
                "Autosave Enabled: " + world.isAutoSave(),
                "Spawn Location: " + world.getSpawnLocation(), "Gamerules: " + world.getGameRules() };
    }

    private boolean createWorldCommand(CommandSender sender, Command cmd, String label,
            String[] args) {
        if (args.length >= 5) {
            World world = Bukkit.getWorld(args[0]);
            loop: if (world == null) {
                for (File file : Bukkit.getWorldContainer().listFiles()) {
                    if (isWorldFolder(file)) {
                        if (file.getName().equals(args[0])) {
                            break loop;
                        }
                    }
                }

                String worldName = args[0];
                long seed = new Random().nextLong();
                try {
                    seed = Long.valueOf(args[1]);
                } catch (Exception e) {
                }

                WorldType type = WorldType.NORMAL;
                try {
                    type = WorldType.valueOf(args[2].toUpperCase());
                } catch (Exception e) {
                }

                Environment env = Environment.NORMAL;
                try {
                    env = Environment.valueOf(args[3].toUpperCase());
                } catch (Exception e) {
                }
                boolean generateStructures = Boolean.valueOf(args[4]);

                WorldCreator creator = new WorldCreator(worldName);
                creator.seed(seed);
                creator.type(type);
                creator.environment(env);
                creator.generateStructures(generateStructures);

                sender.sendMessage("Generating world");
                creator.createWorld();
                Bukkit.getLogger().info("Generator String: " + creator.generatorSettings());
                sender.sendMessage("Done generating world");
                return true;
            }
            sender.sendMessage(ChatColor.RED + "That world already exists");
        }
        return false;
    }

    private boolean deleteWorldCommand(CommandSender sender, Command cmd, String label,
            String[] args) {
        if (args.length > 0) {
            for (File file : Bukkit.getWorldContainer().listFiles()) {
                if (isWorldFolder(file)) {
                    if (file.getName().equals(args[0])) {
                        // is the world currently loaded
                        World world = Bukkit.getWorld(args[0]);
                        boolean result = true;
                        if (world != null) {
                            result = unloadWorldCommand(sender, cmd, label, args);
                        }
                        if (result) {
                            try {
                                CommonLogic.dirDelete(file);
                                return true;
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                return false;
                            }
                        }
                        sender.sendMessage(ChatColor.RED
                                + "An error occured when attempting to unload the world");
                        return false;
                    }
                }
            }
        }
        sender.sendMessage(ChatColor.RED + "You must provide a world name");
        return false;

    }

    private boolean gameruleCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Gamerule prop;
        World world = null;
        String newValue = "imp0sibl3$tring";
        int inc = 0;

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "You must provide a gamerule");
            sender.sendMessage(printEnum(Gamerule.values()));
            return false;
        }

        world = Bukkit.getWorld(args[0]);
        if (world != null) {
            inc++;
        }

        if (isGamerule(args[0 + inc])) {
            prop = Gamerule.valueOf(args[0 + inc]);
            if (args.length == 2 + inc) {
                // Now we know we are setting the property
                newValue = args[1 + inc]; // Zero index means that 3rd arg is index 2
            }

            if (world == null) {
                if (sender instanceof Player) {
                    world = ((Player) sender).getWorld();
                } else {
                    sender.sendMessage(ChatColor.RED + "You must provide a world name");
                    return false;
                }
            }

            if (newValue.equals("imp0sibl3$tring")) {
                sender.sendMessage("Gamerule " + prop + " has value "
                        + prop.getPropertyValue(world));
                return true;
            } else {
                boolean bool = prop.setPropertyValue(world, newValue);
                if (bool) {
                    sender.sendMessage("Gamerule updated successfully");
                }
                return bool;
            }
        }
        sender.sendMessage(printEnum(Gamerule.values()));
        return false;
    }

    private boolean generateWorldCommand(CommandSender sender, Command cmd, String label,
            String[] args) {
        if (args.length >= 2) {
            World world = Bukkit.getWorld(args[0]);
            loop: if (world == null) {
                for (File file : Bukkit.getWorldContainer().listFiles()) {
                    if (isWorldFolder(file)) {
                        if (file.getName().equals(args[0])) {
                            break loop;
                        }
                    }
                }
                sender.sendMessage("Generating world");
                WorldCreator creator = new WorldCreator(args[0]);
                creator.generator(args[1], sender).createWorld();
                sender.sendMessage("Done generating world");
                return true;
            }
            sender.sendMessage(ChatColor.RED + "That world already exists");
            return false;
        }
        return false;
    }

    private boolean isGamerule(String string) {
        try {
            Gamerule.valueOf(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private Location isLocationSafe(Location loc) {
        // TODO: Make this more persistent is finding a safe spawn
        int blockInc = 0;
        while (loc.getWorld().getMaxHeight() > blockInc) {
            Block block = loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + blockInc,
                                                    loc.getBlockZ());
            if (block.getLightFromSky() > 0) {
                // We may have found a safe spawn location
                if (block.getType() == Material.AIR || block.getType() == Material.WATER) {
                    Block block2 = loc.getWorld().getBlockAt(loc.getBlockX(),
                                                             loc.getBlockY() + blockInc + 1,
                                                             loc.getBlockZ());
                    if (block2.getType() == Material.AIR || block2.getType() == Material.WATER) {
                        return block.getLocation();
                    }
                }
            }
            blockInc++;
        }
        return null;
    }

    private boolean isWorldFolder(File theFile) {
        if (theFile != null) {
            if (theFile.isDirectory()) {
                for (File file : theFile.listFiles()) {
                    if (file.getName().equalsIgnoreCase("level.dat")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isWorldProperty(String string) {
        try {
            WorldProperty.valueOf(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void loadWorldConfig() {
        Bukkit.getLogger().info("[MultiWorld] Loading World Configuration");
        List<String> worldList = plugin.getConfig().getStringList("world.worlds");

        for (File file : Bukkit.getWorldContainer().listFiles()) {
            skipWorld: if (isWorldFolder(file)) {
                Bukkit.getLogger().info("[MultiWorld] Found world " + file.getName());
                if (autoLoadWorlds) {
                    if (worldList.contains(file.getName())) { // If pending world is in list then
                                                              // skip loading it
                        Bukkit.getLogger()
                                .info("[MultiWorld] World is listed in config; Skipping world "
                                              + file.getName());
                        break skipWorld;
                    }
                    Bukkit.createWorld(new WorldCreator(file.getName()));
                } else {
                    // Only load world if in worldList
                    if (worldList.contains(file.getName())) {
                        Bukkit.getLogger()
                                .info("[MultiWorld] World is listed in config; Loading world "
                                              + file.getName());
                        Bukkit.createWorld(new WorldCreator(file.getName()));
                    }
                }
            }
        }
        Bukkit.getLogger().info("[MultiWorld] Finished Loading Configuration");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        switch (cmd.getName()) {
        case "createworld":
            return createWorldCommand(sender, cmd, label, args);
        case "deleteworld":
            return deleteWorldCommand(sender, cmd, label, args);
        case "generateworld":
            return generateWorldCommand(sender, cmd, label, args);
        case "unloadworld":
            return unloadWorldCommand(sender, cmd, label, args);
        case "worldinfo":
            return worldInfoCommand(sender, cmd, label, args);
        case "listworlds":
            return worldListCommand(sender, cmd, label, args);
        case "gamerule":
            return gameruleCommand(sender, cmd, label, args);
        case "worldproperty":
            return worldPropertyCommand(sender, cmd, label, args);
        default:
            sender.sendMessage(ChatColor.RED + "Command Error in " + this.getClass().getName());
            return false;
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent evt) {
        // TODO: Make this more persistent in finding a safe spawn location
        if (!evt.isBedSpawn()) {
            Location loc = isLocationSafe(evt.getRespawnLocation());
            if (loc != null) {
                evt.setRespawnLocation(loc);
            } else {
                evt.getPlayer()
                        .sendMessage(ChatColor.RED
                                             + "Warning: Unable to find safe spawn location. You may spawn in a block");
            }
        }
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent evt) {
        Location loc = isLocationSafe(evt.getWorld().getSpawnLocation());
        if (loc != null) {
            if (!evt.getWorld().getSpawnLocation().equals(loc)) {
                Bukkit.getLogger().info("Spawn location of " + evt.getWorld().getName()
                                                + " wasn't safe");
                Bukkit.getLogger().info("Spawn changed to " + loc);
                Bukkit.getLogger().info("Spawn location was " + evt.getWorld().getSpawnLocation());
                evt.getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            }
        }
    }

    private <T extends Enum<?>> String printEnum(T[] theEnum) {
        StringBuilder build = new StringBuilder();
        for (T prop2 : theEnum) {
            build.append(prop2.toString() + ", ");
        }
        build.delete(build.length() - 2, build.length());
        return build.toString();
    }

    public void saveWorldConfig() {
        Bukkit.getLogger().info("[MultiWorld] Not saving config as there is nothing to save");
        // Bukkit.getLogger().info("WorldManager: Saving World Configuration");
        // Bukkit.getLogger().info("WorldManager: Finished Saving Configuration");
    }

    private boolean unloadWorldCommand(CommandSender sender, Command cmd, String label,
            String[] args) {
        if (args.length > 0) {
            World world = Bukkit.getWorld(args[0]);
            if (world != null) {
                if (world.getPlayers().size() > 0) {
                    sender.sendMessage(ChatColor.RED
                            + "You can't unload a world with players in it");
                    return false;
                }
                boolean result = Bukkit.unloadWorld(world, true);
                return result;
            }
        }
        // sender.sendMessage(ChatColor.RED + "You must provide a world name");
        return false;
    }

    private boolean worldInfoCommand(CommandSender sender, Command cmd, String label, String[] args) {
        World world = null;
        if (args.length > 0) {
            world = Bukkit.getWorld(args[0]);
        } else if (sender instanceof Player) {
            world = ((Player) sender).getWorld();
        } else {
            // sender.sendMessage(ChatColor.RED + "You must provide a world name");
            return false;
        }
        sender.sendMessage(asStringArray(world));
        return true;
    }

    private boolean worldListCommand(CommandSender sender, Command cmd, String label, String[] args) {
        for (World world : Bukkit.getWorlds()) {
            sender.sendMessage(world.getName() + " - "
                    + CommonLogic.printEnvColor(world.getEnvironment()) + world.getEnvironment());
        }
        return true;
    }

    private boolean worldPropertyCommand(CommandSender sender, Command cmd, String label,
            String[] args) {
        WorldProperty prop;
        World world = null;
        String newValue = "imp0sibl3$tring";
        int inc = 0;

        if (args.length < 2) {
            // sender.sendMessage(ChatColor.RED + "You must provide a world property");
            sender.sendMessage(printEnum(WorldProperty.values()));
            return false;
        }

        world = Bukkit.getWorld(args[0]);
        if (world != null) {
            inc++;
        }

        if (isWorldProperty(args[0 + inc])) {
            prop = WorldProperty.valueOf(args[0 + inc]);
            if (args.length == 2 + inc) {
                // Now we know we are setting the property
                newValue = args[1 + inc]; // Zero index means that 3rd arg is index 2
            }

            if (world == null) {
                if (sender instanceof Player) {
                    world = ((Player) sender).getWorld();
                } else {
                    // sender.sendMessage(ChatColor.RED + "You must provide a world name");
                    return false;
                }
            }

            if (newValue.equals("imp0sibl3$tring")) {
                sender.sendMessage("Property " + prop + " has value "
                        + prop.getPropertyValue(world));
                return true;
            } else {
                boolean bool = prop.setPropertyValue(world, newValue);
                if (bool) {
                    sender.sendMessage("Property updated successfully");
                }
                return bool;
            }
        }
        sender.sendMessage(printEnum(WorldProperty.values()));
        return false;
    }
}