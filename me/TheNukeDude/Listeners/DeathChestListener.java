package me.TheNukeDude.Listeners;

import me.TheNukeDude.Data.DeathChest;
import me.TheNukeDude.Data.Properties;
import me.TheNukeDude.Managers.DeathChestManager;
import me.TheNukeDude.RPGraveyards;
import me.TheNukeDude.Util.OutputHandler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DeathChestListener implements Listener{

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent e){
        Player player = e.getEntity();
        if(Properties.DeathChestProperties.DisabledWorlds.contains(player.getWorld().getName()))
            return;

        if(Properties.DeathChestProperties.Enabled){
            makeDeathChest(player, player.getTotalExperience() - e.getNewTotalExp());

            e.setDroppedExp(0);
            e.getDrops().clear();
        }
    }

    private void makeDeathChest(Player player, int exp){
        DeathChest chest = new DeathChest(player, exp);
        DeathChestManager.addDeathChest(chest);
    }

    @EventHandler(ignoreCancelled = true)
    public void onChestOpen(PlayerInteractEvent event){
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND){
            Block block = event.getClickedBlock();
            Player player = event.getPlayer();
            if(block != null && block.getType() == Material.CHEST){
                DeathChest chest = DeathChestManager.getDeathChest(block.getLocation());
                if(chest != null){
                    event.setCancelled(true);
                    if(!chest.canOpen(player))
                        OutputHandler.PrintRawInfo("[DeathChest]: That chest is not yours!");
                    else
                        chest.open(player);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event){
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if(block != null && block.getType() == Material.CHEST) {
            DeathChest chest = DeathChestManager.getDeathChest(block.getLocation());
            if(!chest.canOpen(player)) {
                event.setCancelled(true);
                OutputHandler.PrintRawInfo("[DeathChest]: That chest is not yours!");
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        if(block.getType() == Material.CHEST){
            event.setCancelled(nearbyChestsIsDeathChest(block));
            if(event.isCancelled())
                OutputHandler.PrintRawError("Can't place a chest next to a death chest!");
        }
    }

    private boolean nearbyChestsIsDeathChest(Block block){
        Block relative = block.getRelative(-1, 0, 0);
        if(relative.getType() == Material.CHEST)
            if(DeathChestManager.getDeathChest(block.getLocation()) != null)
                return true;

        relative = block.getRelative(1, 0, 0);
        if(relative.getType() == Material.CHEST)
            if(DeathChestManager.getDeathChest(block.getLocation()) != null)
                return true;

        relative = block.getRelative(0, 0, -1);
        if(relative.getType() == Material.CHEST)
            if(DeathChestManager.getDeathChest(block.getLocation()) != null)
                return true;

        relative = block.getRelative(0, 0, 1);
        if(relative.getType() == Material.CHEST)
            if(DeathChestManager.getDeathChest(block.getLocation()) != null)
                return true;

        return false;
    }

}
