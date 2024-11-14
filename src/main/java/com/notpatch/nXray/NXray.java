package com.notpatch.nXray;

import com.notpatch.nXray.command.CommandMain;
import com.notpatch.nXray.listener.PlayerInteractListener;
import com.notpatch.nXray.manager.ArmorStandManager;
import com.notpatch.nXray.manager.ScannerManager;
import dev.respark.licensegate.LicenseGate;
import fr.skytasul.glowingentities.GlowingBlocks;
import org.bukkit.plugin.java.JavaPlugin;

public final class NXray extends JavaPlugin {

    private boolean useDecentHolograms = false;
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

        LicenseGate licenseGate = new LicenseGate("a1de4");
        LicenseGate.ValidationType result = licenseGate.verify(getConfig().getString("license-key"), "nxray");
        if(result.isValid()){
            getLogger().info("License key is valid.");
        }else{
            getLogger().severe("License key is invalid.");
            getServer().getPluginManager().disablePlugin(this);
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
}
