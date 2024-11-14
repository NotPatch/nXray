package com.notpatch.nXray.listener;

import com.notpatch.nXray.NXray;
import com.notpatch.nXray.framework.XrayScanner;
import com.notpatch.nXray.manager.DisplayManager;
import com.notpatch.nXray.manager.ScannerManager;
import com.notpatch.nXray.util.StringUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) throws ReflectiveOperationException {
        Player p = e.getPlayer();
        if(e.getHand() != EquipmentSlot.HAND) return;

        ScannerManager scannerManager = NXray.getInstance().getScannerManager();
        List<XrayScanner> xrayScanners = scannerManager.getXrayScanners();
        ItemStack currentItem = p.getInventory().getItemInMainHand();


        for(XrayScanner xrayScanner : xrayScanners){
            if(scannerManager.areItemsEqual(xrayScanner.toItemStack(), currentItem)){
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                    if (DisplayManager.getBlockLocations().get(p) == null) {
                        DisplayManager displayManager = new DisplayManager(xrayScanner);
                        displayManager.show(p);
                        if (currentItem.getAmount() > 1) {
                            currentItem.setAmount(currentItem.getAmount() - 1);
                        } else {
                            p.getInventory().remove(currentItem);
                        }
                        e.setCancelled(true);
                        break;
                    }else {
                        p.sendMessage(StringUtil.hexColor(NXray.getInstance().getConfig().getString("message.alreadyUse")));
                        e.setCancelled(true);
                        break;
                    }
                }
            }
        }

    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) throws ReflectiveOperationException {
        Player p = e.getPlayer();
        Block block = e.getBlock();
        Location loc = block.getLocation();
        if(DisplayManager.getBlockLocations().get(p) != null) {
            if (DisplayManager.getBlockLocations().get(p).contains(loc)) {
                NXray.getInstance().getGlowingBlocks().unsetGlowing(loc, p);
                DisplayManager.getBlockLocations().get(p).remove(loc);
            }
        }
    }


}
