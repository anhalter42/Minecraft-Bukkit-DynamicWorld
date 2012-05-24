/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.dynamicworld;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author andre
 */
public class DynamicWorld extends JavaPlugin {

    public int configSyncBlockSetterTicks = 2;
    public int configWaterFloodTicks = 10;
    
    protected SyncBlockSetter fSyncBlockSetter;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    }
    
    @Override
    public void onEnable() {
        readDynamicWorldConfig();
        /*
        Plugin lPlugin = getServer().getPluginManager().getPlugin("MAHN42-Framework");
        if (lPlugin != null) {
            getLogger().info("found Framework");
        }
        */
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        fSyncBlockSetter = new SyncBlockSetter(this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, fSyncBlockSetter, 10, configSyncBlockSetterTicks);
    }

    public void setTypeAndData(Location aLocation, Material aMaterial, byte aData, boolean  aPhysics) {
        fSyncBlockSetter.setTypeAndData(aLocation, aMaterial, aData, aPhysics);
    }
    
    protected ArrayList<WaterFlood> fWaterFloods = new ArrayList<WaterFlood>();
    
    public void startWaterFlood(WaterFlood aFlood) {
        fWaterFloods.add(aFlood);
        aFlood.taskId = getServer().getScheduler().scheduleAsyncRepeatingTask(this, aFlood, 2, configWaterFloodTicks);
        aFlood.active = true;
    }
    
    public void stopWaterFlood(WaterFlood aFlood) {
        fWaterFloods.remove(aFlood);
        getServer().getScheduler().cancelTask(aFlood.taskId);
    }

    boolean isWaterFloodRunning(int aX, int aY, int aZ) {
        for(WaterFlood lFlood : fWaterFloods) {
            if (lFlood.x == aX && lFlood.y == aY && lFlood.z == aZ) {
                return true;
            }
        }
        return false;
    }

    protected ArrayList<FloodBlocks> fFloodBlocks = new ArrayList<FloodBlocks>();
    
    public void startFloodBlocks(FloodBlocks aFlood) {
        fFloodBlocks.add(aFlood);
        aFlood.taskId = getServer().getScheduler().scheduleAsyncRepeatingTask(this, aFlood, 2, configWaterFloodTicks);
        aFlood.active = true;
    }
    
    public void stopFloodBlocks(FloodBlocks aFlood) {
        fFloodBlocks.remove(aFlood);
        getServer().getScheduler().cancelTask(aFlood.taskId);
    }

    boolean isFloodRunning(int aX, int aY, int aZ) {
        for(FloodBlocks lFlood : fFloodBlocks) {
            if (lFlood.x == aX && lFlood.y == aY && lFlood.z == aZ) {
                return true;
            }
        }
        return false;
    }

    private void readDynamicWorldConfig() {
        FileConfiguration lConfig = getConfig();
        configSyncBlockSetterTicks = lConfig.getInt("SyncBlockSetter.Ticks");
        configWaterFloodTicks = lConfig.getInt("WaterFlood.Ticks");
    }
}
