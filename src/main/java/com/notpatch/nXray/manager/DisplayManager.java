package com.notpatch.nXray.manager;

import com.notpatch.nXray.NXray;
import com.notpatch.nXray.model.XrayScanner;
import com.notpatch.nXray.util.StringUtil;
import eu.decentsoftware.holograms.api.DHAPI;
import fr.skytasul.glowingentities.GlowingBlocks;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DisplayManager {

    private static final HashMap<Player, List<Location>> blockLocations = new HashMap<>();
    private static final HashMap<Material, ChatColor> materialColorMap = new HashMap<>();

    static {
        materialColorMap.put(Material.COPPER_ORE, ChatColor.GOLD);
        materialColorMap.put(Material.DEEPSLATE_COPPER_ORE, ChatColor.GOLD);
        materialColorMap.put(Material.REDSTONE_ORE, ChatColor.RED);
        materialColorMap.put(Material.DEEPSLATE_REDSTONE_ORE, ChatColor.RED);
        materialColorMap.put(Material.LAPIS_ORE, ChatColor.DARK_BLUE);
        materialColorMap.put(Material.DEEPSLATE_LAPIS_ORE, ChatColor.DARK_BLUE);
        materialColorMap.put(Material.EMERALD_ORE, ChatColor.GREEN);
        materialColorMap.put(Material.DEEPSLATE_EMERALD_ORE, ChatColor.GREEN);
        materialColorMap.put(Material.GOLD_ORE, ChatColor.YELLOW);
        materialColorMap.put(Material.DEEPSLATE_GOLD_ORE, ChatColor.YELLOW);
        materialColorMap.put(Material.IRON_ORE, ChatColor.DARK_GRAY);
        materialColorMap.put(Material.DEEPSLATE_IRON_ORE, ChatColor.DARK_GRAY);
        materialColorMap.put(Material.ANCIENT_DEBRIS, ChatColor.DARK_RED);
        materialColorMap.put(Material.DIAMOND_ORE, ChatColor.AQUA);
        materialColorMap.put(Material.DEEPSLATE_DIAMOND_ORE, ChatColor.AQUA);
        materialColorMap.put(Material.COAL_ORE, ChatColor.BLACK);
        materialColorMap.put(Material.DEEPSLATE_COAL_ORE, ChatColor.BLACK);
        materialColorMap.put(Material.SPAWNER, ChatColor.DARK_PURPLE);
        materialColorMap.put(Material.TRIAL_SPAWNER, ChatColor.DARK_PURPLE);
    }

    Player p;
    private final XrayScanner scanner;


    public DisplayManager(XrayScanner scanner) {
        this.scanner = scanner;
    }

    public DisplayManager show(Player p) throws ReflectiveOperationException {
        this.p = p;
        Location l = p.getLocation();
        ArmorStand armorStand = p.getWorld().spawn(l, ArmorStand.class);
        armorStand.setArms(false);
        armorStand.setVisible(false);
        armorStand.setHelmet(scanner.toItemStack());
        armorStand.setGravity(false);
        armorStand.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);
        armorStand.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.ADDING);
        armorStand.setMetadata("nxray", new FixedMetadataValue(NXray.getInstance(), "nxray"));
        NXray.getInstance().getArmorStandManager().addArmorStand(armorStand);

        GlowingBlocks glowingBlocks = NXray.getInstance().getGlowingBlocks();

        blockLocations.put(p, getBlocksInRadius(l, scanner.getRadius()));
        displayBlocks(blockLocations.get(p), p);
        if(NXray.getInstance().isUseDecentHolograms()){
            List<String> loreList = new ArrayList<>();
            for(String line : NXray.getInstance().getConfig().getStringList("hologram.lines")){
                line = StringUtil.hexColor(line);
                line = line.replaceAll("%duration%", String.valueOf(scanner.getDuration()));
                line = line.replaceAll("%radius%", String.valueOf(scanner.getRadius()));
                loreList.add(line);
            }
            DHAPI.createHologram(p.getName(), l.clone().add(0,3.95, 0), false, loreList);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                NXray.getInstance().getArmorStandManager().removeArmorStand(armorStand);
                if (blockLocations.get(p) != null){
                    for (Location loc : blockLocations.get(p)) {
                        try {
                            glowingBlocks.unsetGlowing(loc, p);

                        } catch (ReflectiveOperationException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                blockLocations.remove(p);
                if(NXray.getInstance().isUseDecentHolograms()) {
                    if(DHAPI.getHologram(p.getName()) != null){
                        DHAPI.getHologram(p.getName()).delete();
                    }
                }
            }
        }.runTaskLater(NXray.getInstance(), scanner.getDuration() * 20L);

        new BukkitRunnable() {

            @Override
            public void run() {
                if(!p.isOnline()){
                    for (Location loc : blockLocations.get(p)) {
                        try {
                            glowingBlocks.unsetGlowing(loc, p);

                        } catch (ReflectiveOperationException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if(NXray.getInstance().isUseDecentHolograms()) {
                        if(DHAPI.getHologram(p.getName()) != null){
                            DHAPI.getHologram(p.getName()).delete();
                        }
                    }
                    blockLocations.remove(p);
                    NXray.getInstance().getArmorStandManager().removeArmorStand(armorStand);
                    cancel();
                }

                if (armorStand.isDead()) {
                    cancel();
                }
                armorStand.setHeadPose(armorStand.getHeadPose().add(0, Math.toRadians(10), 0));
            }
        }.runTaskTimer(NXray.getInstance(), 0, 1);
        return this;

    }

    private List<Location> getBlocksInRadius(Location center, int radius) {
        List<Location> blocks = new ArrayList<>();
        World world = center.getWorld();
        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (scanner.getScanBlocks().contains(block.getType().name().toUpperCase())) {
                        blocks.add(block.getLocation());
                    }
                }
            }
        }

        return blocks;
    }

    public static HashMap<Player, List<Location>> getBlockLocations() {
        return blockLocations;
    }

    public void displayBlocks(List<Location> locations, Player p) throws ReflectiveOperationException {
        GlowingBlocks glowingBlocks = NXray.getInstance().getGlowingBlocks();
        for (Location loc : locations) {
            Block block = loc.getBlock();
            ChatColor color = materialColorMap.get(block.getType());
            if (color != null) {
                glowingBlocks.setGlowing(block, p, color);
            }
        }
    }

}
