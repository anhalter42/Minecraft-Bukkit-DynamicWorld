/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.dynamicworld;

import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 *
 * @author andre
 */
public class WaterFlood  implements Runnable {

    public enum Mode {
        Fill,
        Unfill,
        Flood,
        Unflood
    }
    public int x,y,z;
    public World world;
    public boolean active = false;
    public Mode mode = Mode.Flood;
    public int taskId;
    protected DynamicWorld plugin;
    protected boolean fInit = false;
    
    protected class WaterFloodItem {
        public int x,y,z;
        
        @Override
        public boolean equals(Object aObject) {
            if (aObject instanceof WaterFloodItem) {
                WaterFloodItem lItem = (WaterFloodItem)aObject;
                return lItem.x == x && lItem.y == y && lItem.z == z;
            } else {
                return false;
            }
        }
    }

    protected ArrayList<WaterFloodItem> fItems = new ArrayList<WaterFloodItem>();
    protected ArrayList<WaterFloodItem> fAllItems = new ArrayList<WaterFloodItem>();
    
    protected int fMaxBlocks = 10000;
    
    public WaterFlood(DynamicWorld aPlugin) {
        plugin = aPlugin;
    }
    
    protected class Delta {
         int dx, dy, dz;
         
         Delta(int aDx, int aDy, int aDz) {
             dx = aDx; dy = aDy; dz = aDz;
         }
    }
    
    static Delta[] fDeltas;
    
    @Override
    public void run() {
        if (fDeltas == null) {
            fDeltas = new Delta[5];
            fDeltas[0] = new Delta(-1, 0, 0);
            fDeltas[1] = new Delta( 1, 0, 0);
            fDeltas[2] = new Delta( 0, 0,-1);
            fDeltas[3] = new Delta( 0, 0, 1);
            fDeltas[4] = new Delta( 0,-1, 0);
        }
        if (active) {
            if (!fInit) {
                WaterFloodItem lItem = new WaterFloodItem();
                lItem.x = x;
                lItem.y = y + 1;
                lItem.z = z;
                fItems.add(lItem);
                switch (mode) {
                    case Fill: case Flood:
                        fMaxBlocks = 10000;
                        break;
                    case Unfill: case Unflood:
                        fMaxBlocks = 15000;
                        break;
                }
                fInit = true;
            }
            fAllItems.addAll(fItems);
            ArrayList<WaterFloodItem> lNewItems = new ArrayList<WaterFloodItem>();
            for(WaterFloodItem lItem : fItems) {
                Block lBlock = world.getBlockAt(lItem.x, lItem.y, lItem.z);
                Material lMat = lBlock.getType();
                switch (mode) {
                    case Fill: case Flood:
                        if (lMat.equals(Material.AIR)
                                || lMat.equals(Material.WATER_LILY)
                                || lMat.getId() == 8 //lMat.equals(Material.WATER)
                                || lMat.getId() == 9
                                || lMat.getId() == 31 // lMat.equals(Material.GRASS)
                                || lMat.equals(Material.RED_ROSE)
                                || lMat.equals(Material.YELLOW_FLOWER)) {
                            plugin.setTypeAndData(lBlock.getLocation(), Material.WATER, (byte)0, true);
                            fMaxBlocks--;
                            for(Delta lDelta : fDeltas) {
                                WaterFloodItem lNew = new WaterFloodItem();
                                lNew.x = lItem.x + lDelta.dx;
                                lNew.y = lItem.y + lDelta.dy;
                                lNew.z = lItem.z + lDelta.dz;
                                if (!lNewItems.contains(lNew) && !fItems.contains(lNew) && !fAllItems.contains(lNew)) {
                                    lNewItems.add(lNew);
                                }
                            }
                        }
                        break;
                    case Unfill: case Unflood:
                        if (lMat.getId() == 8 //lMat.equals(Material.WATER)
                                || lMat.getId() == 9) {
                            plugin.setTypeAndData(lBlock.getLocation(), Material.AIR, (byte)0, false);
                            fMaxBlocks--;
                            for(Delta lDelta : fDeltas) {
                                WaterFloodItem lNew = new WaterFloodItem();
                                lNew.x = lItem.x + lDelta.dx;
                                lNew.y = lItem.y + lDelta.dy;
                                lNew.z = lItem.z + lDelta.dz;
                                if (!lNewItems.contains(lNew) && !fItems.contains(lNew) && !fAllItems.contains(lNew)) {
                                    lNewItems.add(lNew);
                                }
                            }
                        }
                        break;
                }
            }
            /*
            Logger.getLogger("WaterFlood").info(new Integer(taskId) + " size = " + new Integer(fItems.size())
                    + " new = " + new Integer(lNewItems.size())
                    + " all = " + new Integer(fAllItems.size()));*/
            fItems.clear();
            fItems = lNewItems;
            if (fItems.isEmpty() || fMaxBlocks <= 0) {
                active = false;
                fItems.clear();
                fAllItems.clear();
                plugin.stopWaterFlood(this);
            }
        }
    }
    
}
