package io.github.robotman3000.bukkit.multiworld.world.command;

import io.github.robotman3000.bukkit.multiworld.world.WorldManagerHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class WorldCreateCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 5) {
            World world = Bukkit.getWorld(args[0]);
            loop: if (world == null) {
                for (File file : Bukkit.getWorldContainer().listFiles()) {
                    if (WorldManagerHelper.isWorldFolder(file)) {
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias,
            String[] args) {
        ArrayList<String> list = new ArrayList<String>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            list.add(player.getName());
        }
        return list;
    }

}
