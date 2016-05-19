package io.github.robotman3000.bukkit.multiworld.world.command;

import io.github.robotman3000.bukkit.multiworld.CommonLogic;
import io.github.robotman3000.bukkit.multiworld.world.WorldManagerHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class WorldDeleteCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            for (File file : Bukkit.getWorldContainer().listFiles()) {
                if (WorldManagerHelper.isWorldFolder(file)) {
                    if (file.getName().equals(args[0])) {
                        // is the world currently loaded
                        World world = Bukkit.getWorld(args[0]);
                        boolean result = true;
                        if (world != null) {
                            result = new UnloadWorldCommand().onCommand(sender, cmd, label, args);
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias,
            String[] args) {
        ArrayList<String> list = new ArrayList<String>();
        for (World world : Bukkit.getWorlds()) {
            list.add(world.getName());
        }
        return list;
    }

}
