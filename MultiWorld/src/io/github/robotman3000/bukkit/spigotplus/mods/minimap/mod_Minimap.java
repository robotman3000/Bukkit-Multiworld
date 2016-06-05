package io.github.robotman3000.bukkit.spigotplus.mods.minimap;

import io.github.robotman3000.bukkit.multiworld.SpigotPlus;
import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

//TODO: Ensure that every map has our renderer
public class mod_Minimap extends JavaPluginFeature<SpigotPlus> implements Listener {

	private List<MapState> knownMaps = new ArrayList<>();
	private MinimapRenderer renderer = new MinimapRenderer(Collections.unmodifiableList(knownMaps), true); 
	
	public mod_Minimap(SpigotPlus hostPlugin) {
		super(hostPlugin, "Minimap Mod");
	}

	@Override
	public boolean initalize() {
		Bukkit.getLogger().info("Starting the minimap mod");
		getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
		return true;
	}
	
	@Override
	public String getFeatureName() {
		return "Minimap Mod";
	}
	
	@EventHandler
	public void onPlayerHotbarChange(PlayerItemHeldEvent event){
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItem(event.getNewSlot());
		
		if(item != null && item.getType() == Material.MAP){
			for(MapState state : knownMaps){
				if(item.getDurability() == state.getMapID()){
					MapView map = state.getMapView();
					boolean instanceMissing = true;
					for(MapRenderer render : map.getRenderers()){
						if(render instanceof MinimapRenderer){
							instanceMissing = false;
						}
					}
					
					if(instanceMissing){
						logInfo("Map " + state.getMapID() + " did not have a Minimap Renderer");
						map.getRenderers().add(renderer);
					}
					
					player.sendMap(map);
					break;
				}
			}
		}
	}
	
	@EventHandler
	public void onMapInitialize(MapInitializeEvent event){
		// Every new map will get our renderer
		event.getMap().addRenderer(renderer);
		knownMaps.add(new MapState(event.getMap()));
	}
}