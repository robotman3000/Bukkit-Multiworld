package io.github.robotman3000.bukkit.multiworld.inventory;

import java.util.List;

public class WorldGroup {

    private final List<String> worlds;
    private final String name;

    public WorldGroup(String name, List<String> worlds) {
        this.name = name;
        this.worlds = worlds;
    }

    public String getName() {
        return name;
    }

    public List<String> getWorlds() {
        return worlds;
    }
}
