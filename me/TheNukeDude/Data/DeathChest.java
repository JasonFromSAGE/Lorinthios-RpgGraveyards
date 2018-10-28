package me.TheNukeDude.Data;

import me.TheNukeDude.Managers.DeathChestManager;
import me.TheNukeDude.RPGraveyards;
import me.TheNukeDude.Tasks.DeathChestParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DeathChest {

    private String uuid;
    private Location location;
    private long ticks;
    private boolean opened = false;
    private DeathChestParticle particleTask;
    private DeathChestInventory inventory;

    public DeathChest(Player player, int exp){
        uuid = player.getUniqueId().toString();
        location = player.getLocation();
        ticks = System.currentTimeMillis();
        makeChest(player, exp);

        particleTask = new DeathChestParticle(this);
    }

    public DeathChest(){}

    private void makeChest(Player player, int exp){
        location = findValidLocation(1).getBlock().getLocation();

        Block block = location.getBlock();
        block.setType(Material.CHEST);

        inventory = new DeathChestInventory(player, exp);
    }

    private Location findValidLocation(int radius){
        Location newLocation = location.clone();
        if(!nearbyChestsIsDeathChest(newLocation.getBlock()) && newLocation.getBlock().getType() == Material.AIR)
            return newLocation;

        for(int x=-radius; x<=radius; x++){
            for(int z=-radius; z<=radius; z++){
                for(int y=-1; y<=2; y++){
                    newLocation = location.clone().add(x, y, z);
                    if(!nearbyChestsIsDeathChest(newLocation.getBlock()) && newLocation.getBlock().getType() == Material.AIR)
                        return newLocation;
                }
            }
        }
        return findValidLocation(radius++);
    }

    public Location getLocation(){
        return location;
    }

    public boolean isLocation(Location location){
        return this.location.getWorld().getName().equalsIgnoreCase(location.getWorld().getName()) &&
                this.location.getBlockX() == location.getBlockX() &&
                this.location.getBlockY() == location.getBlockY() &&
                this.location.getBlockZ() == location.getBlockZ();
    }

    public String getUuid(){
        return uuid;
    }

    public boolean canOpen(Player player){
        if(isValid() && !opened)
            return player.getUniqueId().toString().equalsIgnoreCase(uuid);
        return false;
    }

    public void open(Player player){
        if(canOpen(player)){
            Block block = location.getBlock();
            if(block.getState() instanceof Chest){
                inventory.restore(player);
                location.getBlock().setType(Material.AIR);
                particleTask.cancel();
                DeathChestManager.removeDeathChest(this);
            }
        }
    }

    public void save(FileConfiguration config, int id){
        String path = id + ".";
        config.set(path + "uuid", uuid);
        config.set(path + "ticks", ticks);
        config.set(path + "Location.World", location.getWorld().getName());
        config.set(path + "Location.X", location.getBlockX());
        config.set(path + "Location.Y", location.getBlockY());
        config.set(path + "Location.Z", location.getBlockZ());
        config.set(path + "Items.Armor", inventory.getArmor());
        config.set(path + "Items.Contents", inventory.getContents());
        config.set(path + "Items.Exp", inventory.getExp());
    }

    private boolean isValid(){
        long minutesSinceCreation = ((System.currentTimeMillis() / 1000) - (ticks / 1000)) / 60;

        if(RPGraveyards.properties.DeathChestProperties.ValidDuration != -1){
            return true;
        }
        if(minutesSinceCreation >= RPGraveyards.properties.DeathChestProperties.ValidDuration){
            this.location.getBlock().setType(Material.AIR);
            return false;
        }
        return true;
    }

    public DeathChest load(FileConfiguration config, String id){
        String path = id + ".";
        this.uuid = config.getString(path + "uuid");
        this.ticks = config.getLong(path + "ticks");
        this.location = new Location(
                Bukkit.getWorld(config.getString(path + "Location.World")),
                config.getInt(path + "Location.X"),
                config.getInt(path + "Location.Y"),
                config.getInt(path + "Location.Z")
        );

        this.inventory = new DeathChestInventory(config.getStringList(path + "Items.Armor"),
                config.getStringList(path + "Items.Contents"),
                config.getInt(path + "Items.Exp"));

        if(!isValid()) {
            return null;
        }

        particleTask = new DeathChestParticle(this);
        return this;
    }

    private boolean nearbyChestsIsDeathChest(Block block){
        Block relative = block.getRelative(-1, 0, 0);
        if(relative.getType() == Material.CHEST)
            if(DeathChestManager.getDeathChest(block.getLocation()) != null)
                return true;

        relative = block.getRelative(1, 0, 0);
        if(relative.getType() == Material.CHEST)
            if(DeathChestManager.getDeathChest(block.getLocation()) != null)
                return true;

        relative = block.getRelative(0, 0, -1);
        if(relative.getType() == Material.CHEST)
            if(DeathChestManager.getDeathChest(block.getLocation()) != null)
                return true;

        relative = block.getRelative(0, 0, 1);
        if(relative.getType() == Material.CHEST)
            if(DeathChestManager.getDeathChest(block.getLocation()) != null)
                return true;

        return false;
    }

}
