package io.github.robotman3000.bukkit.multiworld.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BukkitInventory implements ConfigurationSerializable {
    public static BukkitInventory deserialize(Map<String, Object> map) {
        BukkitInventory inv = new BukkitInventory();
        inv.inventoryId = UUID.fromString(map.get("inventoryId").toString());
        inv.canFly = (boolean) map.get("canFly");
        inv.bedSpawnPoint = (Location) map.get("bedSpawnPoint");
        inv.compassTarget = (Location) map.get("compassTarget");
        inv.displayName = (String) map.get("displayName");
        inv.exhaustion = Float.valueOf(map.get("exhaustion").toString());
        inv.xpPoints = Float.valueOf(map.get("xpPoints").toString());
        inv.fallDistance = Float.valueOf(map.get("fallDistance").toString());
        inv.fireTicks = Integer.valueOf(map.get("fireTicks").toString());
        inv.isFlying = (boolean) map.get("isFlying");
        inv.foodLevel = Integer.valueOf(map.get("foodLevel").toString());
        inv.healthPoints = (double) map.get("healthPoints");
        inv.xpLevel = Integer.valueOf(map.get("xpLevel").toString());
        inv.remainingAir = Integer.valueOf(map.get("remainingAir").toString());
        inv.foodSaturation = Float.valueOf(map.get("foodSaturation").toString());
        inv.velocity = (Vector) map.get("velocity");
        inv.armorContents = BukkitInventory.toItemStackArray(map.get("armorContents"));
        inv.inventoryContents = BukkitInventory.toItemStackArray(map.get("inventoryContents"));
        inv.enderChest = BukkitInventory.toItemStackArray(map.get("enderChest"));
        inv.playerLocation = (Location) map.get("playerLocation");
        return inv;
    }

    public static BukkitInventory getInventoryForplayer(Player player) {
        BukkitInventory inv = new BukkitInventory();
        inv.setCanFly(player.getAllowFlight());
        inv.setBedSpawnPoint(player.getBedSpawnLocation());
        inv.setCompassTarget(player.getCompassTarget());
        inv.setDisplayName(player.getDisplayName());
        inv.setExhaustion(player.getExhaustion());
        inv.setXpPoints(player.getExp());
        inv.setFallDistance(player.getFallDistance());
        inv.setFireTicks(player.getFireTicks());
        inv.setFlying(player.isFlying());
        inv.setFoodLevel(player.getFoodLevel());
        inv.setHealthPoints(player.getHealth());
        inv.setXpLevel(player.getLevel());
        inv.setRemainingAir(player.getRemainingAir());
        inv.setFoodSaturation(player.getSaturation());
        inv.setVelocity(player.getVelocity());
        inv.setArmorContents(player.getInventory().getArmorContents());
        inv.setInventoryContents(player.getInventory().getContents());
        inv.setEnderChest(player.getEnderChest().getContents());
        inv.setLocation(player.getLocation());
        return inv;
    }

    @SuppressWarnings("unchecked")
    private static ItemStack[] toItemStackArray(Object object) {
        ArrayList<ItemStack> items = new ArrayList<>();

        if (object instanceof ArrayList<?>) {
            ArrayList<Object> list = (ArrayList<Object>) object;
            for (Object item : list) {
                if (item instanceof ItemStack) {
                    items.add((ItemStack) item);
                } else {
                    items.add(null);
                }
            }
            return items.toArray(new ItemStack[0]);
        }
        return new ItemStack[0];
    }

    private UUID inventoryId = UUID.randomUUID();
    private boolean canFly = false;
    private Location bedSpawnPoint = Bukkit.getWorlds().get(0).getSpawnLocation();
    private Location compassTarget = Bukkit.getWorlds().get(0).getSpawnLocation();
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

    private Location playerLocation = Bukkit.getWorlds().get(0).getSpawnLocation();

    public BukkitInventory() {

    }

    public BukkitInventory(Player player, boolean nameOnly) {
        nameForPlayer(player);
        if (!nameOnly) {
            configureForplayer(player);
        }
    }

    @Deprecated
    protected BukkitInventory(UUID uuid) {
        inventoryId = uuid;
    }

    public boolean canFly() {
        return canFly;
    }

    private void configureForplayer(Player player) {
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

    @Override
    public boolean equals(Object conf) {
        if (conf instanceof BukkitInventory) {
            BukkitInventory inv = (BukkitInventory) conf;
            if (inventoryId.equals(inv.inventoryId)) {
                return true;
            }
        }
        return false;
    }

    public ItemStack[] getArmorContents() {
        return armorContents;
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

    public ItemStack[] getEnderChest() {
        return enderChest;
    }

    public float getExhaustion() {
        return exhaustion;
    }

    public float getFallDistance() {
        return fallDistance;
    }

    public int getFireTicks() {
        return fireTicks;
    }

    public int getFoodLevel() {
        return foodLevel;
    }

    public float getFoodSaturation() {
        return foodSaturation;
    }

    public double getHealthPoints() {
        return healthPoints;
    }

    public ItemStack[] getInventoryContents() {
        return inventoryContents;
    }

    public UUID getInventoryId() {
        return inventoryId;
    }

    public Location getLocation() {
        return playerLocation;
    }

    public int getRemainingAir() {
        return remainingAir;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public int getXpLevel() {
        return xpLevel;
    }

    public float getXpPoints() {
        return xpPoints;
    }

    public boolean isFlying() {
        return isFlying;
    }

    private void nameForPlayer(Player player) {
        // This is so that the default display name is set properly
        setDisplayName(player.getDisplayName());
        // This is so that the player will always be able to fly in creative mode
        setCanFly(player.getAllowFlight());
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
        return map;
    }

    protected void setArmorContents(ItemStack[] armorContents) {
        this.armorContents = armorContents;
    }

    protected void setBedSpawnPoint(Location bedSpawnPoint) {
        this.bedSpawnPoint = bedSpawnPoint;
    }

    protected void setCanFly(boolean canFly) {
        this.canFly = canFly;
    }

    protected void setCompassTarget(Location compassTarget) {
    	if(compassTarget  != null){
    		this.compassTarget = compassTarget;
    	}
    }

    protected void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    protected void setEnderChest(ItemStack[] enderChest) {
        this.enderChest = enderChest;
    }

    protected void setExhaustion(float exhaustion) {
        this.exhaustion = exhaustion;
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

    protected void setFoodSaturation(float foodSaturation) {
        this.foodSaturation = foodSaturation;
    }

    protected void setHealthPoints(double healthPoints) {
        this.healthPoints = healthPoints;
    }

    protected void setInventoryContents(ItemStack[] inventoryContents) {
        this.inventoryContents = inventoryContents;
    }

    protected void setLocation(Location location) {
        playerLocation = location;
    }

    protected void setRemainingAir(int remainingAir) {
        this.remainingAir = remainingAir;
    }

    protected void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    protected void setXpLevel(int xpLevel) {
        this.xpLevel = xpLevel;
    }

    protected void setXpPoints(float xpPoints) {
        this.xpPoints = xpPoints;
    }
}
