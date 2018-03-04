package me.TheNukeDude.Listeners;

import me.TheNukeDude.Data.Graveyard;
import me.TheNukeDude.Data.IconMenu;
import me.TheNukeDude.GUI.GraveyardGUI;
import me.TheNukeDude.Managers.GraveyardManager;
import me.TheNukeDude.RPGraveyards;
import me.TheNukeDude.Tasks.PlayerDiscoverTask;
import me.TheNukeDude.Util.OutputHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class GraveyardListener implements Listener{

    @EventHandler(ignoreCancelled = true)
    public void onPlayerLogin(PlayerLoginEvent e){
        if(RPGraveyards.properties.useDiscovery){
            PlayerDiscoverTask task = new PlayerDiscoverTask(e.getPlayer());
            task.runTaskTimer(RPGraveyards.instance, 2, 2);
        }
    }

	@EventHandler(ignoreCancelled = true)
	public void onPlayerRespawn(PlayerRespawnEvent e)
	{
		Graveyard selected = null;
		for(Graveyard graveyard: GraveyardManager.GetClosestGraveyards(e.getPlayer().getLocation()))
		{
			if (graveyard.playerHasDiscovered(e.getPlayer()))
			{
				selected = graveyard;
				break;
			}
		}
		if (selected != null) 
		{
		    final Graveyard graveyard = selected;
			if(RPGraveyards.properties.useRespawnGUI)
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
