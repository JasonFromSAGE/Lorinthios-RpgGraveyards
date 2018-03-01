package me.TheNukeDude;

import java.util.ArrayList;
import javax.management.openmbean.InvalidKeyException;

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
    //private int levelRequired; // Level required to respawn here (Plugin Hooks)

    //Constructor
    public Graveyard(String name, Player player)
    {
    	this.id = GraveyardManager.GetNextID();
        this.name = name;
        this.location = player.getLocation();
        OutputHandler.PrintCommandInfo(player, "Graveyard was created with the name, " + name);
    }

    //To load from config
    public Graveyard(String id, FileConfiguration config)
    {
        if(TryParse.parseInt(id))
        {
            this.id = Integer.parseInt(id);
            String path = "Graveyards." + id + ".";
            this.name = config.getString(path + "Name");
            String locationPath = path + "Location.";
            this.location = new Location(Bukkit.getWorld(config.getString(locationPath + "WorldName")),
            		config.getDouble(locationPath + "X"), config.getDouble(locationPath + "Y"), config.getDouble(locationPath + "Z"), (float) config.getDouble(locationPath + "YAW"), (float) config.getDouble(locationPath + "Z"));
            this.distanceToDiscover = config.getDouble(path + "Distance");
            this.playersDiscovered = (ArrayList<String>) config.getStringList(path + "PlayerList");
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
       // return playersDiscovered.contains(player.getUniqueId().toString());
    	return true;
    }

    public Graveyard addPlayerDiscovered(Player player){
        playersDiscovered.add(player.getUniqueId().toString());
        OutputHandler.PrintInfo(player, "You discovered, " + name + OutputHandler.INFO + "!");
        return this;
    }

    public double getDistanceToDiscover(){
        return distanceToDiscover;
    }

    public double getSquaredDistanceToDiscover(){
        return distanceToDiscover * distanceToDiscover;
    }

    public void setDistanceToDiscover(double distanceToDiscover){
        this.distanceToDiscover = distanceToDiscover;
    }

}
