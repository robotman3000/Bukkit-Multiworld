package io.github.robotman3000.bukkit.spigotplus.misc.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class GotoCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(args.length >= 1)) {
            sender.sendMessage(ChatColor.RED + "You must provide a world name");
            return false;
        }
        World world = Bukkit.getServer().getWorld(args[0]);
        if (world != null) {
            sender.sendMessage("Teleporting you to world " + args[0]);
            Location loc = world.getSpawnLocation();
            if (!(sender instanceof Player) && args.length < 2) {
                sender.sendMessage(ChatColor.RED + "You must be a player to teleport");
                return false;
            }
            Player thePlayer;
            if (args.length > 1) {
                thePlayer = Bukkit.getPlayer(args[1]);
            } else {
                thePlayer = (Player) sender;
            }
            thePlayer.teleport(loc);
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias,
            String[] args) {
        ArrayList<String> list = new ArrayList<>();

        if (args.length < 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                list.add(player.getName());
            }
        } else {
            for (World world : Bukkit.getWorlds()) {
                list.add(world.getName());
            }
        }
        return list;
    }

}
