package me.TheNukeDude;

import me.TheNukeDude.Commands.GraveyardCommand;
import me.TheNukeDude.Listeners.GraveyardListener;
import me.TheNukeDude.Managers.GraveyardManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RPGraveyards extends JavaPlugin {
	private static GraveyardManager graveyardManager = new GraveyardManager();
	
	@Override
	public void onEnable() 
	{
		this.getLogger().info("Plugin has activated! :)");
		load();
	}

	@Override
	public void onDisable()
	{
		graveyardManager.saveGraveyards(this);
	}

	private void load(){
		getConfig().options().copyDefaults(true);
		saveConfig();
		graveyardManager.loadGraveyards(this);
		registerPlugin();
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
