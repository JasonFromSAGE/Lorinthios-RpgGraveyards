package me.TheNukeDude;

import me.TheNukeDude.Commands.GraveyardCommand;
import me.TheNukeDude.Data.Properties;
import me.TheNukeDude.Listeners.DeathChestListener;
import me.TheNukeDude.Listeners.GraveyardListener;
import me.TheNukeDude.Managers.DeathChestManager;
import me.TheNukeDude.Managers.GraveyardManager;
import me.TheNukeDude.Tasks.PlayerDiscoverTask;
import me.TheNukeDude.Util.ConfigHelper;
import me.TheNukeDude.Util.MessageHelper;
import me.TheNukeDude.Util.ResourceHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class RPGraveyards extends JavaPlugin {

	private static GraveyardManager graveyardManager;
	private static DeathChestManager deathChestManager;
	public static RPGraveyards instance;
	public static Properties properties = new Properties();

	@Override
	public void onEnable() 
	{
		instance = this;
		firstLoad();
		load();

		for(Player player : Bukkit.getServer().getOnlinePlayers())
			new PlayerDiscoverTask(player);
	}

	@Override
	public void onDisable()
	{
		graveyardManager.saveGraveyards(this);
		deathChestManager.save(false);
	}

	private void load(){
		getConfig().options().copyDefaults(true);
		saveConfig();
		loadConfig();
		loadStaticHelper(MessageHelper.class, getFileConfig("messages.yml"), true);
		graveyardManager = new GraveyardManager();
		graveyardManager.loadGraveyards();
		deathChestManager = new DeathChestManager();
		deathChestManager.load(true);
		registerPlugin();
	}

	private void loadConfig(){
		FileConfiguration config = getConfig();
		if(ConfigHelper.ConfigContainsPath(config, "useRespawnGUI"))
			properties.UseRespawnGUI = config.getBoolean("useRespawnGUI");
		if(ConfigHelper.ConfigContainsPath(config, "useDiscovery"))
			properties.UseDiscovery = config.getBoolean("useDiscovery");
		if(ConfigHelper.ConfigContainsPath(config, "respawnParticleEffect"))
			properties.RespawnParticleEffect = Particle.valueOf(config.getString("respawnParticleEffect"));
		if(ConfigHelper.ConfigContainsPath(config, "DisabledWorlds"))
		    properties.DisabledGraveyardWorlds = config.getStringList("DisabledGraveyardWorlds");
		loadDeathChest(config);
	}

	private void loadDeathChest(FileConfiguration config){
		Properties.DeathChestProperties properties = Properties.DeathChestProperties;

		if(ConfigHelper.ConfigContainsPath(config, "DeathChest.Enabled"))
			properties.Enabled = config.getBoolean("DeathChest.Enabled");
		else {
			config.set("DeathChest.Enabled", false);
			saveConfig();
			properties.Enabled = false;
		}
		if(ConfigHelper.ConfigContainsPath(config, "DeathChest.ValidDuration"))
			properties.ValidDuration = config.getInt("DeathChest.ValidDuration");
		else {
			config.set("DeathChest.ValidDuration", 10);
			saveConfig();
			properties.ValidDuration = 10;
		}
		if(ConfigHelper.ConfigContainsPath(config, "DeathChest.DeathChestParticleEffect"))
			properties.ParticleEffect = Particle.valueOf(config.getString("DeathChest.DeathChestParticleEffect"));
		else {
			config.set("DeathChest.DeathChestParticleEffect", "END_ROD");
			saveConfig();
			properties.ParticleEffect = Particle.END_ROD;
		}
		if(ConfigHelper.ConfigContainsPath(config, "DeathChest.ExpStoragePercent"))
			properties.ExpStoragePercentage = config.getDouble("DeathChest.ExpStoragePercent");
		else {
			config.set("DeathChest.ExpStoragePercent", "50.0");
			saveConfig();
			properties.ExpStoragePercentage = 50.0;
		}
        if(ConfigHelper.ConfigContainsPath(config, "DeathChest.DisabledWorlds"))
            properties.DisabledWorlds = config.getStringList("DeathChest.DisabledWorlds");
        else {
            ArrayList<String> worlds = new ArrayList<String>(){{ add("ExampleWorld"); }};
            config.set("DeathChest.DisabledWorlds", worlds);
            saveConfig();
            properties.DisabledWorlds = worlds;
        }
	}

    private void firstLoad(){
        try{
            ResourceHelper.copy(getResource("messages.yml"), new File(getDataFolder(), "messages.yml"));
        }
        catch(Exception exc){
            exc.printStackTrace();
        }
    }

	private void registerPlugin(){
		registerCommands();
		registerListeners();
	}

	private FileConfiguration getFileConfig(String fileName){
		return YamlConfiguration.loadConfiguration(new File(getDataFolder(), fileName));
	}

	private void registerCommands(){
		getCommand("gy").setExecutor(new GraveyardCommand());
	}

	private void registerListeners()
	{
		Bukkit.getPluginManager().registerEvents(new GraveyardListener(), this);
		if(properties.DeathChestProperties.Enabled)
			Bukkit.getPluginManager().registerEvents(new DeathChestListener(), this);
	}

	private void loadStaticHelper(Class clazz, FileConfiguration config, boolean allowColorCodes){
		for(Field field : clazz.getFields()){
			field.setAccessible(true);
			try{
				Object value = config.get(field.getName().replace("_", "."));
				if(value instanceof String && allowColorCodes)
					value = ChatColor.translateAlternateColorCodes('&', (String) value);
				if(value != null)
					field.set(clazz, value);
			}
			catch(Exception excep){
				excep.printStackTrace();
			}
		}
	}

}
