package me.TheNukeDude.Managers;

import me.TheNukeDude.Data.DeathChest;
import me.TheNukeDude.Data.Vector;
import me.TheNukeDude.RPGraveyards;
import me.TheNukeDude.Util.OutputHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class DeathChestManager {

    public static DeathChestManager instance;
    private static HashMap<World, ArrayList<DeathChest>> deathChests;
    private static HashMap<World, HashMap<Vector, DeathChest>> deathChestsByLocation;

    public DeathChestManager(){
        instance = this;
        deathChests = new HashMap<>();
        deathChestsByLocation = new HashMap<>();
        for(World world : Bukkit.getWorlds())
            deathChests.put(world, new ArrayList<>());
        for(World world : Bukkit.getWorlds())
            deathChestsByLocation.put(world, new HashMap<>());
    }

    public ArrayList<DeathChest> getAllDeathChests(){
        ArrayList<DeathChest> all = new ArrayList<>();
        for(World world : deathChests.keySet()){
            all.addAll(deathChests.get(world));
        }
        return all;
    }

    public static void addDeathChest(DeathChest deathChest){
        getChestsForWorld(deathChest.getLocation().getWorld()).add(deathChest);
        getChestPositionsForWorld(deathChest.getLocation().getWorld()).put(new Vector(deathChest.getLocation()), deathChest);
    }

    public static void removeDeathChest(DeathChest deathChest){
        getChestsForWorld(deathChest.getLocation().getWorld()).remove(deathChest);
        getChestPositionsForWorld(deathChest.getLocation().getWorld()).remove(new Vector(deathChest.getLocation()));
    }

    public static DeathChest getDeathChest(Location location){
        HashMap<Vector, DeathChest> chestPositions = getChestPositionsForWorld(location.getWorld());
        return chestPositions.get(new Vector(location));
    }

    private static ArrayList<DeathChest> getChestsForWorld(World world){
        if(deathChests.containsKey(world))
            return deathChests.get(world);
        else{
            ArrayList<DeathChest> deathChestsList = new ArrayList<>();
            deathChests.put(world, deathChestsList);
            return deathChestsList;
        }
    }

    private static HashMap<Vector, DeathChest> getChestPositionsForWorld(World world){
        if(deathChestsByLocation.containsKey(world))
            return deathChestsByLocation.get(world);
        else{
            HashMap<Vector, DeathChest> deathChestHashMap = new HashMap<>();
            deathChestsByLocation.put(world, deathChestHashMap);
            return deathChestHashMap;
        }
    }

    public void load(boolean inThread){
        if(inThread)
            new Thread(this::LoadDeathChests).run();
        else
            LoadDeathChests();
    }

    public void save(boolean inThread){
        if(inThread)
            new Thread(this::SaveDeathChests).run();
        else
            SaveDeathChests();
    }

    private void LoadDeathChests(){
        File file = new File(RPGraveyards.instance.getDataFolder(), "DeathChests.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for(String key : config.getConfigurationSection("").getKeys(false)){
            DeathChest chest = new DeathChest().load(config, key);
            if(chest != null)
                DeathChestManager.addDeathChest(chest);
        }
    }

    private void SaveDeathChests(){
        try{
            File file = new File(RPGraveyards.instance.getDataFolder(), "DeathChests.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            ArrayList<DeathChest> deathChests = getAllDeathChests();

            if(deathChests.size() == 0){
                file.delete();
            }
            else{
                clearConfig(config);

                int i = 0;
                for(DeathChest chest : getAllDeathChests()){
                    chest.save(config, i);
                    i++;
                }

                config.save(file);
            }
        }
        catch(Exception exception){
            OutputHandler.PrintError("FATAL ERROR - Death Chests failed to save");
            exception.printStackTrace();
        }
    }

    private void clearConfig(FileConfiguration config){
        for(String key : config.getConfigurationSection("").getKeys(false)){
            config.set(key, null);
        }
    }
}
