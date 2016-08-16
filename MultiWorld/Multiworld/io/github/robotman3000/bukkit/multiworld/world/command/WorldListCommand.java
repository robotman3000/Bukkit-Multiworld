package io.github.robotman3000.bukkit.multiworld.world.command;

import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginCommand;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginUtil;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class WorldListCommand extends JavaPluginCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        for (World world : Bukkit.getWorlds()) {
            sender.sendMessage(world.getName() + " - "
                    + JavaPluginUtil.printEnvColor(world.getEnvironment()) + world.getEnvironment());
        }
        return true;
    }
}
