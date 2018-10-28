package me.TheNukeDude.Data;

import me.TheNukeDude.Util.InventorySerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

public class DeathChestInventory {

    private ArrayList<String> armor = new ArrayList<>();
    private ArrayList<String> contents = new ArrayList<>();
    private int exp = 0;

    public DeathChestInventory(List<String> armor, List<String> contents, int exp){
        this.armor = new ArrayList<>(armor);
        this.contents = new ArrayList<>(contents);
        this.exp = exp;
    }

    public DeathChestInventory(Player player, int exp){
        this.exp = (int) (exp * Properties.DeathChestProperties.ExpStoragePercentage / 100.0);
        store(player);
    }

    public void store(Player player){
        PlayerInventory inventory = player.getInventory();
        for(ItemStack item : inventory.getArmorContents()){
            armor.add(InventorySerializer.serializeItemStack(item));
        }
        for(ItemStack item : inventory.getContents()){
            contents.add(InventorySerializer.serializeItemStack(item));
        }
    }

    public void restore(Player player){
        player.giveExp(exp);

        //store current items
        ItemStack[] currentArmor = player.getInventory().getArmorContents();
        ItemStack[] currentItems = player.getInventory().getContents();

        //restore old inventory
        ArrayList<ItemStack> armorItems = new ArrayList<>();
        ArrayList<ItemStack> contentItems = new ArrayList<>();

        for(String armorItem : this.armor){
            armorItems.add(InventorySerializer.deserializeItemStack(armorItem));
        }
        for(String contentItem : this.contents){
            contentItems.add(InventorySerializer.deserializeItemStack(contentItem));
        }

        player.getInventory().setArmorContents(armorItems.toArray(new ItemStack[armorItems.size()]));
        player.getInventory().setContents(contentItems.toArray(new ItemStack[contentItems.size()]));

        //Add previously current items
        for(ItemStack item : currentArmor)
            if(item != null && player.getInventory().firstEmpty() > 0)
                player.getInventory().addItem(item);
        for(ItemStack item : currentItems)
            if(item != null && player.getInventory().firstEmpty() > 0)
                player.getInventory().addItem(item);
    }

    public ArrayList<String> getArmor(){
        return armor;
    }

    public ArrayList<String> getContents(){
        return contents;
    }

    public int getExp(){
        return exp;
    }

}
