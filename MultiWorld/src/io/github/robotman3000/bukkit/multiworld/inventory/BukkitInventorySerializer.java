package io.github.robotman3000.bukkit.multiworld.inventory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public class BukkitInventorySerializer implements JsonSerializer<BukkitInventory>, JsonDeserializer<BukkitInventory> {

	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	public BukkitInventory deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {

		JsonObject root = arg0.getAsJsonObject();
		Gson gson = new Gson();
		Type type = new TypeToken<Map<String, Object>>(){}.getType();
		
		UUID invId = UUID.fromString(root.get("inventoryId").getAsString());
		PlayerState playerSt = PlayerState.deserialize((Map<String, Object>) gson.fromJson(root.get("playerState"), type));
		
		BukkitInventory inv = new BukkitInventory(/*playerSt, invId*/);
		
		List<ItemStack> armor = new ArrayList<ItemStack>();
		for(int index = 0; root.has("armorSlot" + index); index++){
			armor.add(ItemStack.deserialize((Map<String, Object>) gson.fromJson(root.get("armorSlot" + index), type)));
		}
		inv.setArmorContents((ItemStack[]) armor.toArray());

		List<ItemStack> enderChest = new ArrayList<ItemStack>();
		for(int index = 0; root.has("enderSlot" + index); index++){
			enderChest.add(ItemStack.deserialize((Map<String, Object>) gson.fromJson(root.get("enderSlot" + index), type)));
		}
		inv.setEnderChest((ItemStack[]) enderChest.toArray());
		
		List<ItemStack> inventory = new ArrayList<ItemStack>();
		for(int index = 0; root.has("invSlot" + index); index++){
			inventory.add(ItemStack.deserialize((Map<String, Object>) gson.fromJson(root.get("invSlot" + index), type)));
		}
		inv.setInventoryContents((ItemStack[]) inventory.toArray());
		
		inv.setCompassTarget(Location.deserialize((Map<String, Object>) gson.fromJson(root.get("compassTarget"), type)));
		inv.setBedSpawnPoint(Location.deserialize((Map<String, Object>) gson.fromJson(root.get("bedSpawnPoint"), type)));
		inv.setLocation(Location.deserialize((Map<String, Object>) gson.fromJson(root.get("location"), type)));
		inv.setVelocity(Vector.deserialize((Map<String, Object>) gson.fromJson(root.get("velocity"), type)));
		
		inv.setDisplayName(root.get("displayName").getAsString());
		inv.setExhaustion(root.get("exhaustion").getAsFloat());
		inv.setFallDistance(root.get("fallDistance").getAsFloat());
		inv.setFireTicks(root.get("fireTicks").getAsInt());
		inv.setFoodLevel(root.get("foodLevel").getAsInt());
		inv.setFoodSaturation(root.get("foodSaturation").getAsFloat());
		inv.setHealthPoints(root.get("healthPoints").getAsDouble());
		inv.setRemainingAir(root.get("remainingAir").getAsInt());
		inv.setXpLevel(root.get("xpLevel").getAsInt());
		inv.setXpPoints(root.get("xpPoints").getAsFloat());
		return inv;
	}

	@Override
	public JsonElement serialize(BukkitInventory arg0, Type arg1, JsonSerializationContext arg2) {
		// JsonObject extends JsonElement so we can just return "root"
		JsonObject root = new JsonObject();
		
		int index = 0;
		for(ItemStack item : arg0.getArmorContents()){
			root.add("armorSlot" + index, new Gson().toJsonTree(item.serialize(), new TypeToken<Map<String, Object>>(){}.getType()));
			index++;
		}
		
		index = 0;
		for(ItemStack item : arg0.getEnderChest()){
			root.add("enderSlot" + index, new Gson().toJsonTree(item.serialize(), new TypeToken<Map<String, Object>>(){}.getType()));
			index++;
		}
		
		index = 0;
		for(ItemStack item : arg0.getInventoryContents()){
			root.add("invSlot" + index, new Gson().toJsonTree(item.serialize(), new TypeToken<Map<String, Object>>(){}.getType()));
			index++;
		}
		
		root.add("bedSpawnPoint", new Gson().toJsonTree(arg0.getBedSpawnPoint().serialize(), new TypeToken<Map<String, Object>>(){}.getType()));
		root.add("compassTarget", new Gson().toJsonTree(arg0.getCompassTarget().serialize(), new TypeToken<Map<String, Object>>(){}.getType()));
		root.add("location", new Gson().toJsonTree(arg0.getLocation().serialize(), new TypeToken<Map<String, Object>>(){}.getType()));
		//root.add("playerState", new Gson().toJsonTree(arg0.getPlayerState().serialize(), new TypeToken<Map<String, Object>>(){}.getType()));
		root.add("velocity", new Gson().toJsonTree(arg0.getVelocity().serialize(), new TypeToken<Map<String, Object>>(){}.getType()));
		
		root.addProperty("displayName", arg0.getDisplayName());
		root.addProperty("exhaustion", arg0.getExhaustion());
		root.addProperty("fallDistance", arg0.getFallDistance());
		root.addProperty("fireTicks", arg0.getFireTicks());
		root.addProperty("foodLevel", arg0.getFoodLevel());
		root.addProperty("foodSaturation", arg0.getFoodSaturation());
		root.addProperty("healthPoints", arg0.getHealthPoints());
		root.addProperty("inventoryId", arg0.getInventoryId().toString());
		root.addProperty("remainingAir", arg0.getRemainingAir());
		root.addProperty("xpLevel", arg0.getXpLevel());
		root.addProperty("xpPoints", arg0.getXpPoints());
		return root;
	}

}
