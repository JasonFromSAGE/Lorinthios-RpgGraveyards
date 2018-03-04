package me.TheNukeDude;

import me.TheNukeDude.Commands.GraveyardCommand;
import me.TheNukeDude.Data.Properties;
import me.TheNukeDude.Listeners.GraveyardListener;
import me.TheNukeDude.Managers.GraveyardManager;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class RPGraveyards extends JavaPlugin {
	private static GraveyardManager graveyardManager = new GraveyardManager();
	public static RPGraveyards instance;
	public static Properties properties;
	
	@Override
	public void onEnable() 
	{
		this.getLogger().info("Plugin has activated! :)");
		load();
		instance = this;
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
		graveyardManager.loadGraveyards(this);
		registerPlugin();
	}

	private void loadConfig(){
		FileConfiguration config = getConfig();
		properties.useRespawnGUI = config.getBoolean("useRespawnGUI");
		properties.useDiscovery = config.getBoolean("useDiscovery");
		properties.respawnParticleEffect = Particle.valueOf(config.getString("respawnParticleEffect"));
	}

	private void registerPlugin(){
		registerCommands();
		registerListeners();
	}

	private void registerCommands(){
		getCommand("gy").setExecutor(new GraveyardCommand());
	}

	private void registerListeners()
	{
		getServer().getPluginManager().registerEvents(new GraveyardListener(), this);
	}

}
