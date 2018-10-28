package me.TheNukeDude.Tasks;

import me.TheNukeDude.Data.Graveyard;
import me.TheNukeDude.Data.Properties;
import me.TheNukeDude.Managers.GraveyardManager;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class PlayerDiscoverTask extends BukkitRunnable {
    private Player player;
    private List<Graveyard> graveyards;
    private int index = 0;

    public PlayerDiscoverTask(Player player){
        this.player = player;
    }

    @Override
    public void run() {
        if(!player.isOnline())
            this.cancel();
        if(Properties.DisabledGraveyardWorlds.contains(player.getWorld().getName()))
            return;

        graveyards = GraveyardManager.GetGraveyardsOfWorld(player.getWorld());

        if(index >= graveyards.size()){
            index = 0;
        }
        if(index < graveyards.size()){
            Graveyard graveyard = graveyards.get(index);
            graveyard.checkPlayerDiscovery(player);
        }
        index++;
    }
}
