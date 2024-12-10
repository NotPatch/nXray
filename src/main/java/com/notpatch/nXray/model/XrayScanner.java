package com.notpatch.nXray.model;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.notpatch.nXray.NXray;
import com.notpatch.nXray.util.StringUtil;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

public class XrayScanner {

    private final String id;
    private final String name;
    private final String itemType;
    private final String texture;
    private final List<String> lore;
    private final int duration;
    private final int radius;
    private final List<String> scanBlocks;

    public XrayScanner(String id, String name, String itemType, String texture, List<String> lore, int duration, int radius, List<String> scanBlocks) {
        this.id = id;
        this.name = name;
        this.itemType = itemType;
        this.texture = texture;
        this.lore = lore;
        this.duration = duration;
        this.radius = radius;
        this.scanBlocks = scanBlocks;
    }

    public ItemStack toItemStack() {
        ItemStack itemStack;

        if ("CUSTOM_HEAD".equalsIgnoreCase(itemType) && texture != null) {
            itemStack = getSkullFromBase64(texture);
        } else {
            Material material = Material.matchMaterial(itemType);
            if (material == null) material = Material.STONE;
            itemStack = new ItemStack(material);
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            if (name != null) {
                meta.setDisplayName(StringUtil.hexColor(name));
            }

            List<String> coloredLore = lore.stream()
                    .map(StringUtil::hexColor)
                    .toList();

            meta.setLore(coloredLore);

            itemStack.setItemMeta(meta);
        }

        return itemStack;
    }

    public String getName() {
        return name;
    }

    public String getItemType() {
        return itemType;
    }

    public String getTexture() {
        return texture;
    }

    public List<String> getLore() {
        return lore;
    }

    public int getDuration() {
        return duration;
    }

    public int getRadius() {
        return radius;
    }

    public List<String> getScanBlocks() {
        return scanBlocks;
    }

    public String getId() {
        return id;
    }

    private ItemStack getSkullFromBase64(String base64) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        if (base64 == null || base64.isEmpty())
            return skull;


        if(NXray.getInstance().getServer().getVersion().contains("1.21")){
            final UUID uuid = UUID.randomUUID();
            PlayerProfile profile = NXray.getInstance().getServer().createPlayerProfile(uuid, uuid.toString().substring(0, 16));
            PlayerTextures playerTextures = profile.getTextures();
            try {
                URL url = new URL("http://textures.minecraft.net/texture/" + base64);
                playerTextures.setSkin(url);

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            profile.setTextures(playerTextures);
            skullMeta.setOwnerProfile(profile);
            skull.setItemMeta(skullMeta);
            return skull;
        }else{
            GameProfile profile = new GameProfile(UUID.randomUUID(), UUID.randomUUID().toString().substring(0, 16));
            profile.getProperties().put("textures", new Property("textures", base64));
            Field profileField = null;
            try {
                profileField = skullMeta.getClass().getDeclaredField("profile");
            } catch (NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }
            profileField.setAccessible(true);
            try {
                profileField.set(skullMeta, profile);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
            skull.setItemMeta(skullMeta);
            return skull;
        }
    }



}
