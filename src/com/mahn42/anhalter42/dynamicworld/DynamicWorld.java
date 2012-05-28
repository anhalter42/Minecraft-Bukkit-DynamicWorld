/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.dynamicworld;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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

    public static DynamicWorld plugin;
    
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
        plugin = this;
        readDynamicWorldConfig();
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        fSyncBlockSetter = new SyncBlockSetter(this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, fSyncBlockSetter, 10, configSyncBlockSetterTicks);
        fBuildingDetector = new BuildingDetector();
        BuildingDescription lDesc;
        BuildingDescription.BlockDescription lBDesc;

        lDesc = fBuildingDetector.newDescription("Statue.Watergod.Waterpod.1");
        lDesc.influenceRadiusFactor = 10;
        lBDesc = lDesc.newBlockDescription("Water");
        lBDesc.material = Material.STATIONARY_WATER;
        lBDesc.newRelatedTo(new Vector(  0, -6,  0), "AirUnderWater");
        lBDesc.newRelatedTo(new Vector( -9,  0,  0), "AirX1Water");
        lBDesc.newRelatedTo(new Vector(  9,  0,  0), "AirX2Water");
        lBDesc.newRelatedTo(new Vector(  0,  0, -9), "AirZ1Water");
        lBDesc.newRelatedTo(new Vector(  0,  0,  9), "AirZ2Water");
        lBDesc = lDesc.newBlockDescription("AirUnderWater");
        lBDesc.material = Material.AIR;
        lBDesc = lDesc.newBlockDescription("AirX1Water");
        lBDesc.material = Material.AIR;
        lBDesc = lDesc.newBlockDescription("AirX2Water");
        lBDesc.material = Material.AIR;
        lBDesc = lDesc.newBlockDescription("AirZ1Water");
        lBDesc.material = Material.AIR;
        lBDesc = lDesc.newBlockDescription("AirZ2Water");
        lBDesc.material = Material.AIR;
        lDesc.activate();
        
        lDesc = fBuildingDetector.newDescription("Statue.Firegod.Lavapod.1");
        lDesc.influenceRadiusFactor = 10;
        lBDesc = lDesc.newBlockDescription("Lava");
        lBDesc.material = Material.STATIONARY_LAVA;
        lBDesc.newRelatedTo(new Vector(0, -6, 0), "AirUnderLava");
        lBDesc.newRelatedTo(new Vector( -9,  0,  0), "AirX1Lava");
        lBDesc.newRelatedTo(new Vector(  9,  0,  0), "AirX2Lava");
        lBDesc.newRelatedTo(new Vector(  0,  0, -9), "AirZ1Lava");
        lBDesc.newRelatedTo(new Vector(  0,  0,  9), "AirZ2Lava");
        lBDesc = lDesc.newBlockDescription("AirUnderLava");
        lBDesc.material = Material.AIR;
        lBDesc = lDesc.newBlockDescription("AirX1Lava");
        lBDesc.material = Material.AIR;
        lBDesc = lDesc.newBlockDescription("AirX2Lava");
        lBDesc.material = Material.AIR;
        lBDesc = lDesc.newBlockDescription("AirZ1Lava");
        lBDesc.material = Material.AIR;
        lBDesc = lDesc.newBlockDescription("AirZ2Lava");
        lBDesc.material = Material.AIR;
        lDesc.activate();
        
        lDesc = fBuildingDetector.newDescription("Statue.Firegod.Nether.1");
        lDesc.influenceRadiusFactor = 2;
        lBDesc = lDesc.newBlockDescription("Fire");
        lBDesc.material = Material.FIRE;
        lBDesc.newRelatedTo(new Vector(0, -1, 0), "NetherUnderFire");
        lBDesc = lDesc.newBlockDescription("NetherUnderFire");
        lBDesc.material = Material.NETHERRACK;
        lDesc.activate();
        
        lDesc = fBuildingDetector.newDescription("Statue.Firegod.Glow.1");
        lDesc.influenceRadiusFactor = 10;
        lBDesc = lDesc.newBlockDescription("Glow");
        lBDesc.material = Material.GLOWSTONE;
        lBDesc.newRelatedTo(new Vector(0, -1, 0), "StoneUnderGlow");
        lBDesc = lDesc.newBlockDescription("StoneUnderGlow");
        lBDesc.material = Material.STONE;
        lDesc.activate();

        lDesc = fBuildingDetector.newDescription("Sewer.Pump.1");
        lBDesc = lDesc.newBlockDescription("WaterOut");
        lBDesc.material = Material.LAPIS_BLOCK;
        lBDesc.newRelatedTo(new Vector(0, 5, 0), "WaterIn");
        lBDesc.newRelatedTo(new Vector(0, 1, 0), "RedStoneWire");
        lBDesc = lDesc.newBlockDescription("WaterIn");
        lBDesc.material = Material.LAPIS_BLOCK;
        lBDesc = lDesc.newBlockDescription("RedStoneWire");
        lBDesc.material = Material.REDSTONE_WIRE;
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
    
    public ArrayList<Building> detectBuilding(World aWorld, Location aLocation) {
        BlockPosition aPos1 = new BlockPosition(aLocation);
        BlockPosition aPos2 = new BlockPosition(aLocation);
        aPos1.add(-5,-5,-5);
        aPos2.add( 5, 5, 5);
        return fBuildingDetector.detect(aWorld, aPos1, aPos2);
    }
    
    public BuildingDescription getBuildingDescription(String aName) {
        for(BuildingDescription lDesc : fBuildingDetector.fDescriptions) {
            if (aName.equalsIgnoreCase(lDesc.name)) {
                return lDesc;
            }
        }
        return null;
    }

    protected HashMap<String, BuildingDB> fBuildingDBs;
    
    public BuildingDB getBuildingDB(String aWorldName) {
        if (fBuildingDBs == null) {
            fBuildingDBs = new HashMap<String, BuildingDB>();
        }
        if (!fBuildingDBs.containsKey(aWorldName)) {
            World lWorld = getServer().getWorld(aWorldName);
            File lFolder = getDataFolder();
            //File lFolder = lWorld.getWorldFolder();
            if (!lFolder.exists()) {
                lFolder.mkdirs();
            }
            String lPath = lFolder.getPath();
            lPath = lPath + File.separatorChar + aWorldName + "_building.csv";
            File lFile = new File(lPath);
            BuildingDB lDB = new BuildingDB(lWorld, lFile);
            lDB.load();
            getLogger().info("Datafile " + lFile.toString() + " loaded. (Records:" + new Integer(lDB.size()).toString() + ")");
            fBuildingDBs.put(aWorldName, lDB);
        }
        return fBuildingDBs.get(aWorldName);
    }
}
