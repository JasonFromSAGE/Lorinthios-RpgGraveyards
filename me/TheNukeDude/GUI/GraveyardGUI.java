package me.TheNukeDude.GUI;

import me.TheNukeDude.Data.Graveyard;
import me.TheNukeDude.Data.IconMenu;
import me.TheNukeDude.Util.MessageHelper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GraveyardGUI extends IconMenu {

    public GraveyardGUI(final Graveyard graveyard) {
        super(MessageHelper.Ui_Title, 3, (p, menu, row, slot, item) -> {
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

        this.addButton(getRow(1), 3, new ItemStack(Material.EMERALD), MessageHelper.Ui_TeleportTo.replace("{graveyard}", graveyard.getName()));
        this.addButton(getRow(1), 5, new ItemStack(Material.REDSTONE_BLOCK), MessageHelper.Ui_Stay.replace("{graveyard}", graveyard.getName()));
    }
}
