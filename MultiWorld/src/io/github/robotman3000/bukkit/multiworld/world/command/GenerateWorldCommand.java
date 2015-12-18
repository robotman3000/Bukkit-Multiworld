package io.github.robotman3000.bukkit.multiworld.world.command;

import io.github.robotman3000.bukkit.multiworld.world.WorldManagerHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class GenerateWorldCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 2) {
            World world = Bukkit.getWorld(args[0]);
            loop: if (world == null) {
                for (File file : Bukkit.getWorldContainer().listFiles()) {
                    if (WorldManagerHelper.isWorldFolder(file)) {
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
