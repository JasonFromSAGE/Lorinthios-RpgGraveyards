package me.TheNukeDude.Data;

import org.bukkit.Particle;

import java.util.List;

public class Properties {

    public static boolean UseRespawnGUI = true;
    public static boolean UseDiscovery = true;
    public static Particle RespawnParticleEffect = Particle.END_ROD;
    public static DeathChestProperties DeathChestProperties = new DeathChestProperties();
    public static List<String> DisabledGraveyardWorlds;

    public static class DeathChestProperties{
        public static boolean Enabled = false;
        public static Particle ParticleEffect = Particle.END_ROD;
        public static int ValidDuration = 10;
        public static double ExpStoragePercentage = 50.0;
        public static List<String> DisabledWorlds;
    }

}
