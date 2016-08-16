package io.github.robotman3000.bukkit.spigotplus.mods.minimap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import io.github.robotman3000.bukkit.spigotplus.api.JavaPluginFeature;

//TODO: Ensure that every map has our renderer
public class mod_Minimap extends JavaPluginFeature implements Listener {

	private List<MapState> knownMaps = new ArrayList<>();
	private MinimapRenderer renderer = new MinimapRenderer(Collections.unmodifiableList(knownMaps), true); 
	
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
	
	@Override
	public int getRequiredMajorVersion() {
		return 1;
	}

	@Override
	public int getRequiredMinorVersion() {
		return 0;
	}
}