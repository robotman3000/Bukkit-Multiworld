package io.github.robotman3000.bukkit.spigotplus.api.command;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

public class BooleanParameter extends CommandParameter<Boolean> {

	private List<String> list = Arrays.asList(Boolean.TRUE.toString(), Boolean.FALSE.toString());
	
	public BooleanParameter(String parameterName) {
		super(parameterName);
	}

	@Override
	public Boolean getParameterValue(String str) {
		return Boolean.valueOf(str);
	}

	@Override
	public List<String> getTabCompletions(CommandSender sender, String arg) {
    	for (String str : list){
    		if(str.startsWith(arg)){
    			return Arrays.asList(str);
    		}
    	}
    	return list;
	}

}
