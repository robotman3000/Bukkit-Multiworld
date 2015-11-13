package io.github.robotman3000.bukkit.multiworld.world.command;

import io.github.robotman3000.bukkit.multiworld.world.GameruleList;
import io.github.robotman3000.bukkit.multiworld.world.WorldManagerHelper;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class GameruleCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        GameruleList prop;
        World world = null;
        String newValue = "imp0sibl3$tring";
        int inc = 0;

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "You must provide a gamerule");
            sender.sendMessage(WorldManagerHelper.printEnum(GameruleList.values()));
            return false;
        }

        world = Bukkit.getWorld(args[0]);
        if (world != null) {
            inc++;
        }

        if (WorldManagerHelper.isGamerule(args[0 + inc])) {
            prop = GameruleList.valueOf(args[0 + inc]);
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
        sender.sendMessage(WorldManagerHelper.printEnum(GameruleList.values()));
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
