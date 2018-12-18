package io.github.robotman3000.bukkit.multiworld.inventory;

import java.util.List;
import java.util.UUID;

public class WorldGroup {

    private final List<WorldKey> worlds;
    private final String name;
    private final UUID groupID;

    public WorldGroup(String name, UUID groupID, List<WorldKey> worlds) {
        this.name = name;
        this.worlds = worlds;
        this.groupID = groupID;
    }

    public String getName() {
        return name;
    }
    
    public UUID getID(){
    	return groupID;
    }

    public List<WorldKey> getWorlds() {
        return worlds;
    }
}
