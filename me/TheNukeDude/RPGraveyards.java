package me.TheNukeDude;

import org.bukkit.plugin.java.JavaPlugin;

public class RPGraveyards extends JavaPlugin {
	private static GraveyardManager graveyardManager = new GraveyardManager();
	
	@Override
	public void onEnable() 
	{
		getConfig().options().copyDefaults(true);
        saveConfig();
		this.getLogger().info("Plugin has activated! :)");
		getCommand("gy").setExecutor(new GraveyardCommand());
		graveyardManager.loadGraveyards(this);
		registerListeners();
	}
	@Override
	public void onDisable()
	{
		graveyardManager.saveGraveyards(this);
	}
	private void registerListeners()
	{
		getServer().getPluginManager().registerEvents(new GraveyardListener(), this);
	}

}
