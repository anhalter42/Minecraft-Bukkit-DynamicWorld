/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.dynamicworld;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author andre
 */
public class DynamicWorld extends JavaPlugin {

    protected SyncBlockSetter fSyncBlockSetter;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        fSyncBlockSetter = new SyncBlockSetter(this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, fSyncBlockSetter, 10, 2);
    }

    public void setTypeAndData(Location aLocation, Material aMaterial, byte aData, boolean  aPhysics) {
        fSyncBlockSetter.setTypeAndData(aLocation, aMaterial, aData, aPhysics);
    }
    
    protected ArrayList<WaterFlood> fWaterFloods = new ArrayList<WaterFlood>();
    
    public WaterFlood createWaterFlood(int aX, int aY, int aZ) {
        WaterFlood lFlood = new WaterFlood(this);
        lFlood.x = aX;
        lFlood.y = aY;
        lFlood.z = aZ;
        if (!fWaterFloods.contains(lFlood)) {
            return lFlood;
        } else {
            return null;
        }
    }
}
