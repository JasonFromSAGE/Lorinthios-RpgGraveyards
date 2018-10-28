package me.TheNukeDude.Data;

import java.util.ArrayList;
import javax.management.openmbean.InvalidKeyException;

import me.TheNukeDude.Managers.GraveyardManager;
import me.TheNukeDude.RPGraveyards;
import me.TheNukeDude.Tasks.GraveyardRespawnParticleTask;
import me.TheNukeDude.Util.MessageHelper;
import me.TheNukeDude.Util.OutputHandler;
import me.TheNukeDude.Util.TryParse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Graveyard {

    private int id;
    private Location location; // Where it is
    private String name; // Name of the graveyard
    private ArrayList<String> playersDiscovered = new ArrayList<>(); // Who has discovered this graveyard?
    private double distanceToDiscover; // How close does the player need to be to 'discover' this graveyard
    private double distanceToDiscoverSquared;//private int levelRequired; // Level required to respawn here (Plugin Hooks)

    //Constructor
    public Graveyard(String name, Player player)
    {
    	this.id = GraveyardManager.GetNextID();
        this.name = name;
        this.location = player.getLocation();
        this.distanceToDiscover = 50;
        this.distanceToDiscoverSquared = 50 * 50;
        OutputHandler.PrintCommandInfo(player, MessageHelper.Command_GraveyardCreated.replace("{graveyard}", getName()));
    }

    //To load from config
    public Graveyard(String id, FileConfiguration config)
    {
        if(TryParse.parseInt(id))
        {
            this.id = Integer.parseInt(id);
            String path = "Graveyards." + id + ".";
            name = config.getString(path + "Name");
            String locationPath = path + "Location.";
            location = new Location(Bukkit.getWorld(config.getString(locationPath + "WorldName")),
            		config.getDouble(locationPath + "X"), config.getDouble(locationPath + "Y"), config.getDouble(locationPath + "Z"), (float) config.getDouble(locationPath + "YAW"), (float) config.getDouble(locationPath + "Z"));
            distanceToDiscover = config.getDouble(path + "Distance");
            distanceToDiscoverSquared = distanceToDiscover * distanceToDiscover;
            playersDiscovered = (ArrayList<String>) config.getStringList(path + "PlayerList");
        }
        else
            throw new InvalidKeyException("Cannot have non-integer id for Graveyard"); 
        

    }

    public void save(FileConfiguration config)
    {
    	String path = "Graveyards." + id + ".";
    	config.set(path + "Name", name);
    	String locationPath = path + "Location.";
    	config.set(locationPath + "WorldName", location.getWorld().getName());
    	config.set(locationPath + "X", location.getX());
    	config.set(locationPath + "Y", location.getY());
    	config.set(locationPath + "Z", location.getZ());
    	config.set(locationPath + "YAW", location.getYaw());
    	config.set(locationPath + "PITCH", location.getPitch());
    	config.set(path + "Distance", distanceToDiscover);
    	config.set(path + "PlayerList", playersDiscovered);
    }

    public void respawn(Player player){
        player.teleport(location);
        OutputHandler.PrintInfo(player, MessageHelper.Event_Respawn.replace("{graveyard}", getName()));
        GraveyardRespawnParticleTask task = new GraveyardRespawnParticleTask(player);
        task.runTaskTimer(RPGraveyards.instance, 0, 1);
    }

    public int getID(){
    	return id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Location getLocation()
    {
        return location;
    }

    public void setLocation(Location location){
        this.location = location;
    }

    public boolean playerHasDiscovered(Player player){
        if(RPGraveyards.properties.UseDiscovery)
            return playersDiscovered.contains(player.getUniqueId().toString());
        else
        	return true;
    }

    public void checkPlayerDiscovery(Player player){
        if(RPGraveyards.properties.UseDiscovery){
            if(playerHasDiscovered(player))
                return;

            if(player.getLocation().distanceSquared(location) <= distanceToDiscoverSquared){
                addPlayerDiscovered(player);
            }
        }
    }

    private void addPlayerDiscovered(Player player){
        playersDiscovered.add(player.getUniqueId().toString());
        OutputHandler.PrintInfo(player, MessageHelper.Event_Discover.replace("{graveyard}", getName()));
    }

    public double getDistanceToDiscover(){
        return distanceToDiscover;
    }

    public double getSquaredDistanceToDiscover(){
        return distanceToDiscover * distanceToDiscover;
    }

    public void setDistanceToDiscover(double distanceToDiscover){

        this.distanceToDiscover = distanceToDiscover;
        this.distanceToDiscoverSquared = distanceToDiscover * distanceToDiscover;
    }

    public void printInfo(Player player){
        OutputHandler.PrintCommandInfo(player, "ID : " + OutputHandler.HIGHLIGHT + getID());
        OutputHandler.PrintCommandInfo(player, "Name : " + OutputHandler.HIGHLIGHT + getName());
        OutputHandler.PrintCommandInfo(player, "World : " + OutputHandler.HIGHLIGHT + getLocation().getWorld().getName());
        OutputHandler.PrintCommandInfo(player, "Location : " + OutputHandler.HIGHLIGHT + " <" + getLocation().getBlockX() + ", " + getLocation().getBlockY() + ", " + getLocation().getBlockZ() + ">");
        OutputHandler.PrintCommandInfo(player, "Distance : " + OutputHandler.HIGHLIGHT + ((int) getLocation().distance(player.getLocation())));
        OutputHandler.PrintCommandInfo(player, "DiscoveryDistance : " + OutputHandler.HIGHLIGHT + distanceToDiscover);
    }

}
