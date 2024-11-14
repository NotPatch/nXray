package com.notpatch.nXray.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;

import java.util.ArrayList;
import java.util.List;

public class ArmorStandManager {

    private final List<ArmorStand> armorStandList = new ArrayList<>();

    public ArmorStandManager(){

    }

    public List<ArmorStand> getArmorStandList() {
        return armorStandList;
    }

    public void addArmorStand(ArmorStand armorStand){
        armorStandList.add(armorStand);
    }

    public void removeArmorStand(ArmorStand armorStand){
        armorStandList.remove(armorStand);
        armorStand.remove();
    }

    public void removeAllArmorStand(){
        for(ArmorStand armorStand : armorStandList){
            armorStand.remove();
        }
        armorStandList.clear();
    }

    public void fixArmorStands(){
        for(ArmorStand armorStand : Bukkit.getWorld("world").getEntitiesByClass(ArmorStand.class)){
            if(armorStand.hasMetadata("nxray")){
                armorStand.remove();
            }
        }
    }

}
