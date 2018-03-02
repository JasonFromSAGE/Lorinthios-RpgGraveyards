package me.TheNukeDude;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class GraveyardListener implements Listener{
	@EventHandler
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
			e.setRespawnLocation(selected.getLocation());
			OutputHandler.PrintInfo(e.getPlayer(), "You have respawned at " + selected.getName());
		}
	}
}