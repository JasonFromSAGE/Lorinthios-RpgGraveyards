package me.TheNukeDude.Listeners;

import me.TheNukeDude.Data.Graveyard;
import me.TheNukeDude.Data.IconMenu;
import me.TheNukeDude.Data.Properties;
import me.TheNukeDude.GUI.GraveyardGUI;
import me.TheNukeDude.Managers.GraveyardManager;
import me.TheNukeDude.RPGraveyards;
import me.TheNukeDude.Tasks.PlayerDiscoverTask;
import me.TheNukeDude.Util.OutputHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class GraveyardListener implements Listener{

    @EventHandler(ignoreCancelled = true)
    public void onPlayerLogin(PlayerLoginEvent e){
        if(Properties.UseDiscovery){
            PlayerDiscoverTask task = new PlayerDiscoverTask(e.getPlayer());
            task.runTaskTimer(RPGraveyards.instance, 5, 5);
        }
    }

	@EventHandler(ignoreCancelled = true)
	public void onPlayerRespawn(PlayerRespawnEvent e)
	{
		Player player = e.getPlayer();
		if(Properties.DisabledGraveyardWorlds.contains(player.getWorld().getName()))
			return;

		Graveyard selected = null;
		for(Graveyard graveyard: GraveyardManager.GetClosestGraveyards(player.getLocation()))
		{
			if (graveyard.playerHasDiscovered(player))
			{
				selected = graveyard;
				break;
			}
		}
		if (selected != null) 
		{
		    final Graveyard graveyard = selected;
			if(Properties.UseRespawnGUI)
                Bukkit.getScheduler().scheduleSyncDelayedTask(RPGraveyards.instance, () -> {
                    IconMenu menu = new GraveyardGUI(graveyard);
                    menu.open(e.getPlayer());
                });

			else{
			    e.setRespawnLocation(graveyard.getLocation());
				Bukkit.getScheduler().scheduleSyncDelayedTask(RPGraveyards.instance, () -> {
				    graveyard.respawn(e.getPlayer());
                });
			}
		}
	}
}
