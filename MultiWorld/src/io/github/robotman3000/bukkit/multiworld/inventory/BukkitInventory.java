package io.github.robotman3000.bukkit.multiworld.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BukkitInventory implements ConfigurationSerializable {
	private UUID inventoryId = UUID.randomUUID();
	private boolean canFly = false;
	private Location bedSpawnPoint;
	private Location compassTarget;
	private String displayName = "Steve";
	private float exhaustion;
	private float xpPoints = 0;
	private float fallDistance = 0;
	private int fireTicks = 0;
	private boolean isFlying = false;
	private int foodLevel = 20;
	private double healthPoints = 20;
	private int xpLevel = 0;
	private int remainingAir = 300;
	private float foodSaturation;
	private Vector velocity = new Vector();
	private ItemStack[] armorContents = new ItemStack[0];
	private ItemStack[] inventoryContents = new ItemStack[0];
	private ItemStack[] enderChest = new ItemStack[0];
	private Location playerLocation;
	private PlayerState playerState;

	/**
	 * This constructor is meant for use by gson
	 * @Deprecated
	 */
	@Deprecated
	public BukkitInventory(){
		
	}

	public BukkitInventory(PlayerState playerState){
		this.setPlayerState(playerState);
		this.configureForplayer(playerState.getPlayer());
	}

	protected BukkitInventory(PlayerState playerState, UUID uuid){
		this.setPlayerState(playerState);
		this.inventoryId = uuid;
	}
	
	protected void setLocation(Location location) {
		this.playerLocation = location;
	}

	public UUID getInventoryId() {
		return inventoryId;
	}

	public boolean canFly() {
		return canFly;
	}

	public Location getBedSpawnPoint() {
		return bedSpawnPoint;
	}

	public Location getCompassTarget() {
		return compassTarget;
	}

	public String getDisplayName() {
		return displayName;
	}

	public float getExhaustion() {
		return exhaustion;
	}

	public float getXpPoints() {
		return xpPoints;
	}

	public float getFallDistance() {
		return fallDistance;
	}

	public int getFireTicks() {
		return fireTicks;
	}

	public boolean isFlying() {
		return isFlying;
	}

	public int getFoodLevel() {
		return foodLevel;
	}

	public double getHealthPoints() {
		return healthPoints;
	}

	public int getXpLevel() {
		return xpLevel;
	}

	public int getRemainingAir() {
		return remainingAir;
	}

	public float getFoodSaturation() {
		return foodSaturation;
	}

	public Vector getVelocity() {
		return velocity;
	}

	public ItemStack[] getArmorContents() {
		return armorContents;
	}

	public ItemStack[] getInventoryContents() {
		return inventoryContents;
	}

	protected void setCanFly(boolean canFly) {
		this.canFly = canFly;
	}

	protected void setBedSpawnPoint(Location bedSpawnPoint) {
		this.bedSpawnPoint = bedSpawnPoint;
	}

	protected void setCompassTarget(Location compassTarget) {
		this.compassTarget = compassTarget;
	}

	protected void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	protected void setExhaustion(float exhaustion) {
		this.exhaustion = exhaustion;
	}

	protected void setXpPoints(float xpPoints) {
		this.xpPoints = xpPoints;
	}

	protected void setFallDistance(float fallDistance) {
		this.fallDistance = fallDistance;
	}

	protected void setFireTicks(int fireTicks) {
		this.fireTicks = fireTicks;
	}

	protected void setFlying(boolean isFlying) {
		this.isFlying = isFlying;
	}

	protected void setFoodLevel(int foodLevel) {
		this.foodLevel = foodLevel;
	}

	protected void setHealthPoints(double healthPoints) {
		this.healthPoints = healthPoints;
	}

	protected void setXpLevel(int xpLevel) {
		this.xpLevel = xpLevel;
	}

	protected void setRemainingAir(int remainingAir) {
		this.remainingAir = remainingAir;
	}

	protected void setFoodSaturation(float foodSaturation) {
		this.foodSaturation = foodSaturation;
	}

	protected void setVelocity(Vector velocity) {
		this.velocity = velocity;
	}
	
	protected void setArmorContents(ItemStack[] armorContents) {
		this.armorContents = armorContents;
	}

	protected void setInventoryContents(ItemStack[] inventoryContents) {
		this.inventoryContents = inventoryContents;
	}

	protected void setEnderChest(ItemStack[] enderChest) {
		this.enderChest = enderChest;
	}

	public ItemStack[] getEnderChest() {
		return enderChest;
	}
	
	public Location getLocation(){
		return playerLocation;	
	}
	
	@Override
	public boolean equals(Object conf){
		if(conf instanceof BukkitInventory){
			BukkitInventory inv = (BukkitInventory) conf;
			if(this.inventoryId.equals(inv.inventoryId)){
				return true;
			}
		}
		return false;
	}
	
	private void configureForplayer(Player player){
		setCanFly(player.getAllowFlight());
		setBedSpawnPoint(player.getBedSpawnLocation());
		setCompassTarget(player.getCompassTarget());
		setDisplayName(player.getDisplayName());
		setExhaustion(player.getExhaustion());
		setXpPoints(player.getExp());
		setFallDistance(player.getFallDistance());
		setFireTicks(player.getFireTicks());
		setFlying(player.isFlying());
		setFoodLevel(player.getFoodLevel());
		setHealthPoints(player.getHealth());
		setXpLevel(player.getLevel());
		setRemainingAir(player.getRemainingAir());
		setFoodSaturation(player.getSaturation());
		setVelocity(player.getVelocity());
		setArmorContents(player.getInventory().getArmorContents());
		setInventoryContents(player.getInventory().getContents());
		setEnderChest(player.getEnderChest().getContents());
		setLocation(player.getLocation());
	}

	public PlayerState getPlayerState() {
		return playerState;
	}

	private void setPlayerState(PlayerState playerState) {
		this.playerState = playerState;
	}

	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("inventoryId", inventoryId.toString());
		map.put("canFly", canFly);
		map.put("bedSpawnPoint", bedSpawnPoint);
		map.put("compassTarget", compassTarget); 
		map.put("displayName", displayName);
		map.put("exhaustion", exhaustion);
		map.put("xpPoints", xpPoints);
		map.put("fallDistance", fallDistance);
		map.put("fireTicks", fireTicks);
		map.put("isFlying", isFlying);
		map.put("foodLevel", foodLevel);
		map.put("healthPoints", healthPoints);
		map.put("xpLevel", xpLevel);
		map.put("remainingAir", remainingAir);
		map.put("foodSaturation", foodSaturation);
		map.put("velocity", velocity);
		map.put("armorContents", armorContents);
		map.put("inventoryContents", inventoryContents);
		map.put("enderChest", enderChest);
		map.put("playerLocation", playerLocation);
		map.put("playerKey", playerState);
		return map;
	}
	
	public static BukkitInventory deserialize(Map<String, Object> map){
		BukkitInventory inv = new BukkitInventory();
		inv.inventoryId = UUID.fromString(map.get("inventoryId").toString());
		inv.canFly = (boolean) map.get("canFly");
		inv.bedSpawnPoint = (Location) map.get("bedSpawnPoint");
		inv.compassTarget = (Location) map.get("compassTarget"); 
		inv.displayName = (String) map.get("displayName");
		inv.exhaustion = Float.valueOf(map.get("exhaustion").toString());
		inv.xpPoints = Float.valueOf(map.get("xpPoints").toString());
		inv.fallDistance = Float.valueOf(map.get("fallDistance").toString());
		inv.fireTicks = Integer.valueOf( map.get("fireTicks").toString());
		inv.isFlying = (boolean) map.get("isFlying");
		inv.foodLevel = Integer.valueOf( map.get("foodLevel").toString());
		inv.healthPoints = (double) map.get("healthPoints");
		inv.xpLevel = Integer.valueOf( map.get("xpLevel").toString());
		inv.remainingAir = Integer.valueOf( map.get("remainingAir").toString());
		inv.foodSaturation = Float.valueOf(map.get("foodSaturation").toString());
		inv.velocity = (Vector) map.get("velocity");
		inv.armorContents = toItemStackArray(map.get("armorContents"));
		inv.inventoryContents = toItemStackArray(map.get("inventoryContents"));
		inv.enderChest = toItemStackArray(map.get("enderChest"));
		inv.playerLocation = (Location) map.get("playerLocation");
		inv.playerState = (PlayerState) map.get("playerKey");
		return inv;
	}

	@SuppressWarnings("unchecked")
	private static ItemStack[] toItemStackArray(Object object) {
		ArrayList<ItemStack> items = new ArrayList<>();
		
		if(object instanceof ArrayList<?>){
			ArrayList<Object> list = (ArrayList<Object>) object;
			for(Object item : list){
				if(item instanceof ItemStack){
					items.add((ItemStack) item);
				} else {
					items.add(null);
				}
			}
			return items.toArray(new ItemStack[0]);
		}
		return new ItemStack[0];
	}
}
