package io.github.robotman3000.bukkit.multiworld.world.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import io.github.robotman3000.bukkit.multiworld.world.WorldManagerHelper;

public class WorldInfoCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        World world = null;
        if (args.length > 0) {
            world = Bukkit.getWorld(args[0]);
        } else if (sender instanceof Player) {
            world = ((Player) sender).getWorld();
        } else {
            // sender.sendMessage(ChatColor.RED + "You must provide a world name");
            return false;
        }
        sender.sendMessage(WorldManagerHelper.asStringArray(world));
        return true;
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
