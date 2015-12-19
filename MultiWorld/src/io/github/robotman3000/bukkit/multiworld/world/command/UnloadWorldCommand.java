package io.github.robotman3000.bukkit.multiworld.world.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class UnloadWorldCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
