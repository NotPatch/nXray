package com.notpatch.nXray.manager;

import com.notpatch.nXray.NXray;
import com.notpatch.nXray.model.XrayScanner;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ScannerManager {

    private final NXray main;
    private final List<XrayScanner> xrayScanners = new ArrayList<>();

    public ScannerManager(NXray main){
        this.main = main;
    }

    public void loadItemsFromConfig() {
        xrayScanners.clear();
        Configuration config = main.getConfig();
        for (String section : config.getConfigurationSection("xray-items").getKeys(false)) {
            String name = config.getString("xray-items." + section + ".name");
            String itemType = config.getString("xray-items." + section + ".item-type");
            String texture = config.getString("xray-items." + section + ".texture");
            List<String> lore = config.getStringList("xray-items." + section + ".lore");
            List<String> blocks = config.getStringList("xray-items." + section + ".scan-blocks");
            int duration = config.getInt("xray-items." + section + ".scan-duration");
            int radius = config.getInt("xray-items." + section + ".scan-radius");

            xrayScanners.add(new XrayScanner(section, name, itemType, texture, lore, duration, radius, blocks));
        }
    }

    public int getDuration(ItemStack item){
        for(XrayScanner scanner : xrayScanners){
            if(areItemsEqual(item, scanner.toItemStack())){
                return scanner.getDuration();
            }
        }
        return 0;
    }

    public int getRadius(ItemStack item){
        for(XrayScanner scanner : xrayScanners){
            if(areItemsEqual(item, scanner.toItemStack())){
                return scanner.getRadius();
            }
        }
        return 0;
    }

    public List<String> getBlocks(ItemStack item){
        for(XrayScanner scanner : xrayScanners){
            if(areItemsEqual(item, scanner.toItemStack())){
                return scanner.getScanBlocks();
            }
        }
        return null;
    }

    public List<XrayScanner> getXrayScanners() {
        return xrayScanners;
    }

    public boolean areItemsEqual(ItemStack item1, ItemStack item2) {
        if (item1 == null || item2 == null) return false;
        if (item1.getType() != item2.getType()) return false;

        ItemMeta meta1 = item1.getItemMeta();
        ItemMeta meta2 = item2.getItemMeta();

        if ((meta1 == null) != (meta2 == null)) return false;
        if (meta1 == null) return true;

        if (meta1.hasDisplayName() && meta2.hasDisplayName()) {
            if (!meta1.getDisplayName().equals(meta2.getDisplayName())) return false;
        } else if (meta1.hasDisplayName() || meta2.hasDisplayName()) {
            return false;
        }

        if (meta1.hasLore() && meta2.hasLore()) {
            if (!meta1.getLore().equals(meta2.getLore())) return false;
        } else if (meta1.hasLore() || meta2.hasLore()) {
            return false;
        }

        return true;
    }


}
