package me.TheNukeDude;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

public class GraveyardManager {
	public static HashMap<Integer, Graveyard> graveyards = new HashMap<>();
	private static HashMap<World, List<Graveyard>> worldGraveyards = new HashMap<>();
	private static int ID = 0;
	
	public static List<Graveyard> GetGraveyardsOfWorld(World world)
	{
		if (worldGraveyards.containsKey(world))
		{
			return worldGraveyards.get(world);
		}
		return new ArrayList<Graveyard>();
	}
	
	public void loadGraveyards(RPGraveyards plugin)
	{
		File file = new File(plugin.getDataFolder(), "graveyards.yml");
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
		return graveyards.get(id);
	}
	
	public static void AddGraveyard(Graveyard graveyard)
	{
		graveyards.put(graveyard.getID(), graveyard);
		AddGraveyardToWorld(graveyard);
	}
	
	public static Graveyard RemoveGraveyard(Integer ID)
	{
		Graveyard graveyard = graveyards.remove(ID);
		RemoveGraveyardFromWorld(graveyard);
		return graveyard;
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
		List<Graveyard> graveyards = worldGraveyards.get(location.getWorld());
		//Store graveyard based on distance
		HashMap<Double, Graveyard> distances = new HashMap<>();
		for(Graveyard graveyard : graveyards){
		    distances.put(location.distanceSquared(graveyard.getLocation()), graveyard);
		}

		//Get all the keys
		List<Double> keys = new ArrayList<Double>(distances.keySet());
		//Sort the keys by value ascending
		Collections.sort(keys);

		//Make our array of graveyards in order from closest to furthest
		ArrayList<Graveyard> ordered = new ArrayList<>();
		for(Double key : keys)
		    ordered.add(distances.get(key));
		    
		//Return the ordered list of graveyards
		return ordered;
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
			List<Graveyard> graveyards = new ArrayList<Graveyard>();
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
