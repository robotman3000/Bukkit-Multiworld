package io.github.robotman3000.bukkit.spigotplus.mods.minimap;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class MapWraper extends MapRenderer {

	private MapState state;

	public MapWraper(MapState mapState) {
		this.state = mapState;
	}

	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {
		MinimapRenderer.render(state, map, canvas, player);
	}

}
