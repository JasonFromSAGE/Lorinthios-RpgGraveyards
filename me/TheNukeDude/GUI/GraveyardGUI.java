package me.TheNukeDude.GUI;

import me.TheNukeDude.Data.Graveyard;
import me.TheNukeDude.Data.IconMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GraveyardGUI extends IconMenu {

    public GraveyardGUI(final Graveyard graveyard) {
        super("Graveyard Respawn", 3, (p, menu, row, slot, item) -> {
            if(item != null){
                if(item.getType() == Material.EMERALD){
                    graveyard.respawn(p);
                    return false;
                }
                else if(item.getType() == Material.REDSTONE_BLOCK){
                    return false;
                }
            }
            return true;
        });

        this.addButton(getRow(1), 3, new ItemStack(Material.EMERALD), ChatColor.GREEN + "Teleport to", graveyard.getName());
        this.addButton(getRow(1), 5, new ItemStack(Material.REDSTONE_BLOCK), ChatColor.RED + "Stay here");
    }
}
