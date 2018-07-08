package me.TheNukeDude;

import me.TheNukeDude.Commands.GraveyardCommand;
import me.TheNukeDude.Data.Properties;
import me.TheNukeDude.Listeners.GraveyardListener;
import me.TheNukeDude.Managers.GraveyardManager;
import me.TheNukeDude.Tasks.PlayerDiscoverTask;
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

public class RPGraveyards extends JavaPlugin {
	private static GraveyardManager graveyardManager = new GraveyardManager();
	public static RPGraveyards instance;
	public static Properties properties;
	
	@Override
	public void onEnable() 
	{
		firstLoad();
		load();
		instance = this;

		for(Player player : Bukkit.getServer().getOnlinePlayers())
			new PlayerDiscoverTask(player);
	}

	@Override
	public void onDisable()
	{
		graveyardManager.saveGraveyards(this);
	}

	private void load(){
		getConfig().options().copyDefaults(true);
		saveConfig();
		loadConfig();
		loadStaticHelper(MessageHelper.class, getFileConfig("messages.yml"), true);
		graveyardManager.loadGraveyards(this);
		registerPlugin();
	}

	private void loadConfig(){
		FileConfiguration config = getConfig();
		properties.useRespawnGUI = config.getBoolean("useRespawnGUI");
		properties.useDiscovery = config.getBoolean("useDiscovery");
		properties.respawnParticleEffect = Particle.valueOf(config.getString("respawnParticleEffect"));
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
		getServer().getPluginManager().registerEvents(new GraveyardListener(), this);
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
