package com.notpatch.nXray;

import com.notpatch.nXray.command.CommandMain;
import com.notpatch.nXray.listener.PlayerInteractListener;
import com.notpatch.nXray.manager.ArmorStandManager;
import com.notpatch.nXray.manager.ScannerManager;
import fr.skytasul.glowingentities.GlowingBlocks;
import org.bukkit.plugin.java.JavaPlugin;

public final class NXray extends JavaPlugin {

    private boolean useDecentHolograms = false;
    private boolean useFancyHolograms = false;

    private static NXray instance;
    private static ArmorStandManager armorStandManager;
    private ScannerManager scannerManager;
    private GlowingBlocks glowingBlocks;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        saveConfig();

        armorStandManager = new ArmorStandManager();

        scannerManager = new ScannerManager(this);
        scannerManager.loadItemsFromConfig();

        glowingBlocks = new GlowingBlocks(this);

        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getCommand("nxray").setExecutor(new CommandMain());

        if(getServer().getPluginManager().getPlugin("DecentHolograms") != null) {
            getLogger().info("DecentHolograms found! Enabling support...");
            useDecentHolograms = true;
        }

        if(getServer().getPluginManager().getPlugin("FancyHolograms") != null) {
            getLogger().info("FancyHolograms found! Enabling support...");
            useFancyHolograms = true;
        }

    }

    @Override
    public void onDisable() {
        armorStandManager.removeAllArmorStand();
        glowingBlocks.disable();
    }

    public static NXray getInstance() {
        return instance;
    }

    public GlowingBlocks getGlowingBlocks() {
        return glowingBlocks;
    }

    public ArmorStandManager getArmorStandManager() {
        return armorStandManager;
    }

    public ScannerManager getScannerManager() {
        return scannerManager;
    }

    public boolean isUseDecentHolograms() {
        return useDecentHolograms;
    }

    public boolean isUseFancyHolograms() {
        return useFancyHolograms;
    }

}
