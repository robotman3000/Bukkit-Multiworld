package io.github.robotman3000.bukkit.spigotplus.api;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class CommandLogic {

    public abstract boolean doCommand(CommandSender sender, Command cmd, String label, String[] args);

    public abstract List<String> doTabComplete(CommandSender sender, Command cmd, String label,
            String[] args);
}
