package com.notpatch.nXray.util;

import com.notpatch.nXray.NXray;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemUtil {

    private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    public static ItemStack getItemFromConfig(String section){
        Configuration config = NXray.getInstance().getConfig();
        String itemSection = config.getString("xray-items." + section);
        String itemType = config.getString(itemSection + ".item-type");
        String itemName = config.getString(itemSection + ".name");
        List<String> itemLore = config.getStringList(itemSection + ".lore");
        ItemStack item;
        if(itemType.equalsIgnoreCase("custom_head")){
            item = new ItemStack(Material.PLAYER_HEAD);
        }else{
            item = new ItemStack(Material.matchMaterial(itemSection + ".item-type"));
        }
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(itemName);
        itemMeta.setLore(itemLore);
        return item;
    }

    public static int getItemDuration(ItemStack item){
        ItemMeta itemMeta = item.getItemMeta();
        List<String> lore = itemMeta.getLore();
        for(String line : lore){
            if(line.contains("Duration: ")){
                return Integer.parseInt(line.split(" ")[1]);
            }
        }
        return 0;
    }

    public static int getItemRadius(ItemStack item){
        ItemMeta itemMeta = item.getItemMeta();
        List<String> lore = itemMeta.getLore();
        for(String line : lore){
            if(line.contains("Block Radius: ")){
                return Integer.parseInt(line.split(" ")[1]);
            }
        }
        return 0;
    }

    public static String hexColor(String message) {
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String color = message.substring(matcher.start(), matcher.end());
            message = message.replace(color, "" + ChatColor.of(color));
            matcher = pattern.matcher(message);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
