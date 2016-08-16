package io.github.robotman3000.bukkit.spigotplus.api.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;

public class StringParameter extends CommandParameter<String> {

	public StringParameter(String parameterName) {
		super(parameterName);
	}

	@Override
	public String getParameterValue(String str) {
		return str;
	}

	@Override
	public List<String> getTabCompletions(CommandSender sender, String arg) {
		return Collections.emptyList();
	}

}
