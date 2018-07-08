package me.TheNukeDude.Tasks;

import me.TheNukeDude.RPGraveyards;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GraveyardRespawnParticleTask extends BukkitRunnable {

    private Particle particle = RPGraveyards.properties.respawnParticleEffect;
    private double rotation = 0.0;
    private double rotationChange = Math.PI / 20.0;
    private double rotationOffset = Math.PI * 2.0 / 3.0;
    private double height = 0.0f;
    private double heightChange = 0.05;
    private double duration = 80; // 4 seconds
    private boolean up = true;
    private int count = 0;
    private Player player;

    public GraveyardRespawnParticleTask(Player player){
        this.player = player;
    }

    @Override
    public void run() {
        if(!player.isOnline()){
            cancel();
        }
        Location location = player.getLocation();
        checkHeight();

        rotation += rotationChange;
        double rot2 = rotation + rotationOffset;
        double rot3 = rot2 + rotationOffset;

        Location a = location.clone().add(Math.cos(rotation), height, Math.sin(rotation));
        location.getWorld().spawnParticle(particle, a, 0);
        Location b = location.clone().add(Math.cos(rot2), height, Math.sin(rot2));
        location.getWorld().spawnParticle(particle, b, 0);
        Location c = location.clone().add(Math.cos(rot3), height, Math.sin(rot3));
        location.getWorld().spawnParticle(particle, c, 0);

        count++;
        if(count > duration)
            cancel();
    }

    private void checkHeight(){
        if(up)
            height += heightChange;
        else
            height -= heightChange;

        if(height <= 0)
            up = true;
        if(height >= 2.0)
            up = false;
    }
}
