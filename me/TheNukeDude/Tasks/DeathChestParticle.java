package me.TheNukeDude.Tasks;

import me.TheNukeDude.Data.DeathChest;
import me.TheNukeDude.RPGraveyards;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class DeathChestParticle extends BukkitRunnable {

    private DeathChest deathChest;
    private Player player;

    private boolean checkForPlayer = true;
    private int checkDelay = 100;

    public DeathChestParticle(DeathChest deathChest){
        this.deathChest = deathChest;
        runTaskTimer(RPGraveyards.instance, 10, 10);
    }

    @Override
    public void run() {
        if(player != null){
            if(!player.isOnline()){
                player = null;
                checkDelay = 100;
                return;
            }

            if(player.getPlayer().getWorld() == deathChest.getLocation().getWorld() && deathChest.getLocation().getChunk().isLoaded()) {
                Location start = deathChest.getLocation().clone().add(0.5, 0.5, 0.5);

                for(int i=0; i<2; i++)
                    player.spawnParticle(RPGraveyards.properties.DeathChestProperties.ParticleEffect, start.add(0, 10, 0), 20, 0.2, 10.2, 0.2, 0);
            }
        }
        else{
            if(checkDelay > 0){
                checkDelay -= 10;
            }

            if(player == null && checkForPlayer && checkDelay <= 0){
                player = Bukkit.getPlayer(UUID.fromString(deathChest.getUuid()));
                if(player == null) {
                    checkDelay += 100;
                    return;
                }
                else{
                    checkDelay = 0;
                }
            }
        }
    }
}
