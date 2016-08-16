package io.github.robotman3000.spigotplus.api.test;

import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginCommand;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginCommandList;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

public class Main extends JavaPluginFeature {
	
    private enum Commands implements JavaPluginCommandList {
    	testcommand (new TestCommand());

    	private JavaPluginCommand command;
    	
    	private Commands(JavaPluginCommand obj) {
    		this.command = obj;
		}
    	
        @Override
        public CommandExecutor getExecutor(){
        	return command;
        }

        @Override
        public TabCompleter getTabCompleter(){
        	return command;
        }
    }
    
	@Override
	public int getRequiredMajorVersion() {
		return 1;
	}

	@Override
	public int getRequiredMinorVersion() {
		return 0;
	}

	@Override
	protected JavaPluginCommandList[] getCommands() {
		return Commands.values();
	}
}
