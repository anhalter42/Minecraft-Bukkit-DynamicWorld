/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.dynamicworld;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

/**
 *
 * @author andre
 */
public class DynamicWorld extends JavaPlugin {

    public int configSyncBlockSetterTicks = 2;
    public int configWaterFloodTicks = 10;
    
    protected SyncBlockSetter fSyncBlockSetter;
    protected BuildingDetector fBuildingDetector;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    }
    
    @Override
    public void onEnable() {
        readDynamicWorldConfig();
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        fSyncBlockSetter = new SyncBlockSetter(this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, fSyncBlockSetter, 10, configSyncBlockSetterTicks);
        fBuildingDetector = new BuildingDetector();
        BuildingDescription lDesc;
        BuildingDescription.BlockDescription lBDesc;

        lDesc = fBuildingDetector.newDescription("Statue.Watergod.1");
        lBDesc = lDesc.newBlockDescription("Water");
        lBDesc.material = Material.STATIONARY_WATER;
        lBDesc.newRelatedTo(new Vector(0, -6, 0), "AirUnderWater");
        lBDesc = lDesc.newBlockDescription("AirUnderWater");
        lBDesc.material = Material.AIR;
        lDesc.activate();
        lDesc = fBuildingDetector.newDescription("Statue.Firegod.1");
        lBDesc = lDesc.newBlockDescription("Lava");
        lBDesc.material = Material.STATIONARY_LAVA;
        lBDesc.newRelatedTo(new Vector(0, -6, 0), "AirUnderLava");
        lBDesc = lDesc.newBlockDescription("AirUnderLava");
        lBDesc.material = Material.AIR;
        lDesc.activate();
        lDesc = fBuildingDetector.newDescription("Statue.Firegod.2");
        lBDesc = lDesc.newBlockDescription("Fire");
        lBDesc.material = Material.FIRE;
        lBDesc.newRelatedTo(new Vector(0, -1, 0), "NetherUnderFire");
        lBDesc = lDesc.newBlockDescription("NetherUnderFire");
        lBDesc.material = Material.NETHERRACK;
        lDesc.activate();
        lDesc = fBuildingDetector.newDescription("Statue.Firegod.3");
        lBDesc = lDesc.newBlockDescription("Glow");
        lBDesc.material = Material.GLOWSTONE;
        lBDesc.newRelatedTo(new Vector(0, -1, 0), "StoneUnderGlow");
        lBDesc = lDesc.newBlockDescription("StoneUnderGlow");
        lBDesc.material = Material.STONE;
        lDesc.activate();
    }

    public void setTypeAndData(Location aLocation, Material aMaterial, byte aData, boolean  aPhysics) {
        fSyncBlockSetter.setTypeAndData(aLocation, aMaterial, aData, aPhysics);
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
    
    public BuildingDescription detectBuilding(World aWorld, Location aLocation) {
        return fBuildingDetector.detect(aWorld, new BlockPosition(aLocation.add(-5, -5, -5)), new BlockPosition(aLocation.add(5, 5, 5)));
    }
}
