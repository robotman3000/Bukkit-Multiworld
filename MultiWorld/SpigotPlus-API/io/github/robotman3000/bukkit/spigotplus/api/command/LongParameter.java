package io.github.robotman3000.bukkit.spigotplus.api.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;

public class LongParameter extends CommandParameter<Long> {

	public LongParameter(String parameterName) {
		super(parameterName);
	}

	@Override
	public Long getParameterValue(String str) {
		try {
			return Long.valueOf(str);
		} catch (NumberFormatException e){
			e.printStackTrace();
		}
		return 0L;
	}

	@Override
	public List<String> getTabCompletions(CommandSender sender, String arg) {
		return Collections.emptyList();
	}

}
