package me.TheNukeDude.Managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import me.TheNukeDude.Data.Graveyard;
import me.TheNukeDude.RPGraveyards;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

public class GraveyardManager {
	private static HashMap<Integer, Graveyard> graveyards;
	private static HashMap<World, List<Graveyard>> worldGraveyards;
	private static int ID = 0;

	public GraveyardManager(){
		graveyards = new HashMap<>();
		worldGraveyards = new HashMap<>();
	}

	public static List<Graveyard> GetGraveyardsOfWorld(World world)
	{
		if (worldGraveyards.containsKey(world))
		{
			return worldGraveyards.get(world);
		}
		return new ArrayList<>();
	}
	
	public void loadGraveyards()
	{
		graveyards = new HashMap<>();
		worldGraveyards = new HashMap<>();

		File file = new File(RPGraveyards.instance.getDataFolder(), "graveyards.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		ID = config.getInt("ID");
		if (config.contains("Graveyards"))
		{
			for(String key: config.getConfigurationSection("Graveyards").getKeys(false))
			{
				if (!key.equalsIgnoreCase("ID"))
				{
					Graveyard g = new Graveyard(key, config);
					AddGraveyard(g);
				}
			}
		}
	}
	
	public void saveGraveyards(RPGraveyards plugin)
	{
		File file = new File(plugin.getDataFolder(), "graveyards.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("ID", ID);
		config.set("Graveyards", null);
		for (Graveyard graveyard: graveyards.values())
		{
			graveyard.save(config);
		}
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Graveyard GetGraveyardByID(int id)
	{
		if(graveyards.containsKey(id))
			return graveyards.get(id);
		return null;
	}
	
	public static void AddGraveyard(Graveyard graveyard)
	{
		graveyards.put(graveyard.getID(), graveyard);
		AddGraveyardToWorld(graveyard);
	}
	
	public static Graveyard RemoveGraveyard(Integer ID)
	{
	    if(graveyards.containsKey(ID)){
            Graveyard graveyard = graveyards.remove(ID);
            RemoveGraveyardFromWorld(graveyard);
            return graveyard;
        }
		return null;
	}
	
	private static void RemoveGraveyardFromWorld(Graveyard graveyard)
	{
		World w = graveyard.getLocation().getWorld();
		if(worldGraveyards.containsKey(w))
		{
		    worldGraveyards.get(w).remove(graveyard);
		}
	}
	
	public static ArrayList<Graveyard> GetClosestGraveyards(Location location)
	{
	    if(worldGraveyards.containsKey(location.getWorld())){
            List<Graveyard> graveyards = worldGraveyards.get(location.getWorld());
            //Store graveyard based on distance
            HashMap<Double, Graveyard> distances = new HashMap<>();
            for(Graveyard graveyard : graveyards){
                distances.put(location.distanceSquared(graveyard.getLocation()), graveyard);
            }

            //Get all the keys
            List<Double> keys = new ArrayList<>(distances.keySet());
            //Sort the keys by value ascending
            Collections.sort(keys);

            //Make our array of graveyards in order from closest to furthest
            ArrayList<Graveyard> ordered = new ArrayList<>();
            for(Double key : keys)
                ordered.add(distances.get(key));

            //Return the ordered list of graveyards
            return ordered;
        }
        return new ArrayList<>();
	}

	public static Graveyard GetClosestGraveyard(Location location) {
        List<Graveyard> graveyards = worldGraveyards.get(location.getWorld());
        double distance = Double.MAX_VALUE;
        Graveyard closest = null;
        for (Graveyard graveyard : graveyards){
            double currentDistance = location.distanceSquared(graveyard.getLocation());
            if (currentDistance < distance) {
                closest = graveyard;
                distance = currentDistance;
            }
        }
        return closest;
    }

	private static void AddGraveyardToWorld(Graveyard graveyard)
	{
		World w = graveyard.getLocation().getWorld();
		if(worldGraveyards.containsKey(w))
		{
		    worldGraveyards.get(w).add(graveyard);
		}
		else
		{
			List<Graveyard> graveyards = new ArrayList<>();
			graveyards.add(graveyard);
			worldGraveyards.put(w, graveyards);
		}
	}

	public static Integer GetNextID()
	{
		ID ++;
		return ID;
	}
}
