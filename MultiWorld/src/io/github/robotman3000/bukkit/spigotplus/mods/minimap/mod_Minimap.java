package io.github.robotman3000.bukkit.spigotplus.mods.minimap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.robotman3000.bukkit.spigotplus.mods.PluginModBase;

public class mod_Minimap extends PluginModBase implements Listener {

	public mod_Minimap(JavaPlugin hostPlugin) {
		super(hostPlugin);
	}

	@Override
	public String getName() {
		return "Minimap";
	}

	@Override
	public void initialize(ConfigurationSection config) {
		getHost().getServer().getPluginManager().registerEvents(this, getHost());
		Bukkit.getLogger().warning("The minimap mod is only a proof of concept at this time. It is not production ready");
		Bukkit.getLogger().warning("It should not, however, be able to crash the server as it is stable");
	}
	
	@EventHandler
	public void onMapInitialize(MapInitializeEvent event){
		event.getMap().addRenderer(new MapRenderer() {
			@Override
			public void render(MapView map, MapCanvas canvas, Player player) {
				Location loc = player.getLocation();
				map.setCenterX(loc.getBlockX());
				map.setCenterZ(loc.getBlockZ());
			}
		});
	}
	
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

}
