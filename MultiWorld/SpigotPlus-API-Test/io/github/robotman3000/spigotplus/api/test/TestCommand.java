package io.github.robotman3000.spigotplus.api.test;

import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginCommand;
import io.github.robotman3000.bukkit.spigotplus.api.command.CommandParameter;
import io.github.robotman3000.bukkit.spigotplus.api.command.EnumParameter;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TestCommand extends JavaPluginCommand {

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(args.length > 0){
			TestEnum value = (TestEnum) getParameters().get(0).getParameterValue(args[0]);
			sender.sendMessage("You sent the value " + value);
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command,
			String alias, String[] args) {
		return Collections.emptyList();
	}

	@Override
	protected void initializeParameters(List<CommandParameter<?>> commandParameters2) {
		commandParameters2.add(new EnumParameter<>("Test Enum", TestEnum.class));
	}

}
