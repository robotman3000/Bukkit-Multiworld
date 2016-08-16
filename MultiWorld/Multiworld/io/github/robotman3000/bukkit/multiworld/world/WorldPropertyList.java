package io.github.robotman3000.bukkit.multiworld.world;

import org.bukkit.World;

public enum WorldPropertyList implements PropertyList {
    Difficulty {
        @Override
        public String getPropertyValue(World world) {
            return world.getDifficulty().toString();
        }

        @Override
        public boolean setPropertyValue(World world, String newValue) {
            try {
                world.setDifficulty(org.bukkit.Difficulty.valueOf(newValue));
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    },
    AllowPVP {
        @Override
        public String getPropertyValue(World world) {
            return String.valueOf(world.getPVP());
        }

        @Override
        public boolean setPropertyValue(World world, String newValue) {
            world.setPVP(Boolean.valueOf(newValue));
            return true;
        }
    },
    AllowAnimals {
        @Override
        public String getPropertyValue(World world) {
            return String.valueOf(world.getAllowAnimals());
        }

        @Override
        public boolean setPropertyValue(World world, String newValue) {
            world.setSpawnFlags(world.getAllowMonsters(), (Boolean.valueOf(newValue)));
            return true;
        }
    },
    AllowMonsters {
        @Override
        public String getPropertyValue(World world) {
            return String.valueOf(world.getAllowMonsters());
        }

        @Override
        public boolean setPropertyValue(World world, String newValue) {
            world.setSpawnFlags((Boolean.valueOf(newValue)), world.getAllowAnimals());
            return true;
        }
    },
    KeepSpawnLoaded {
        @Override
        public String getPropertyValue(World world) {
            return String.valueOf(world.getKeepSpawnInMemory());
        }

        @Override
        public boolean setPropertyValue(World world, String newValue) {
            world.setKeepSpawnInMemory(Boolean.valueOf(newValue));
            return true;
        }
    },
    AutoSave {
        @Override
        public String getPropertyValue(World world) {
            return String.valueOf(world.isAutoSave());
        }

        @Override
        public boolean setPropertyValue(World world, String newValue) {
            world.setAutoSave(Boolean.valueOf(newValue));
            return true;
        }
    };
}
