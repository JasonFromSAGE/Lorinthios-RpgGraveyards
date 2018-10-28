package me.TheNukeDude.Data;

import org.bukkit.Location;

import java.util.Objects;

public class Vector {

    public int X;
    public int Y;
    public int Z;

    public Vector(int x, int y, int z){
        X = x;
        Y = y;
        Z = z;
    }

    public Vector(Location location){
        X = location.getBlockX();
        Y = location.getBlockY();
        Z = location.getBlockZ();
    }

    @Override
    public int hashCode() {
        return Objects.hash(X, Y, Z);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Vector){
            Vector other = (Vector) obj;
            return other.X == X &&
                    other.Y == Y &&
                    other.Z == Z;
        }
        return false;
    }
}
