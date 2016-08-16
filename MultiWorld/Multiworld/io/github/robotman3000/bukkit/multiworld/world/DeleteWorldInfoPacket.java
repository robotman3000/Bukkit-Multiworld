package io.github.robotman3000.bukkit.multiworld.world;

import java.io.File;

public class DeleteWorldInfoPacket {

	private File worldFile;
	private long time;

	public DeleteWorldInfoPacket(File worldFile, long currentTimeMillis) {
		this.worldFile = worldFile;
		this.time = currentTimeMillis;
	}
	
	public File getWorldFile() {
		return worldFile;
	}

	public long getTime() {
		return time;
	}
}
