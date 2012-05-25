/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.dynamicworld;

import java.util.ArrayList;
import java.util.HashSet;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 *
 * @author andre
 */
public class FloodBlocks implements Runnable {

    public enum Mode {
        Flood,
        Reverse
    }
    
    public enum Direction {
        HorizontalAndDown,
        Horizontal,
        HorizontalAndUp,
        HorizontalVertical
    }
    
    protected DynamicWorld plugin;
    
    public int x,y,z;
    public World world;
    public boolean active = false;
    public int taskId;
    public boolean updatePhysics = true;
    public int maxBlocks = 10000;
    public Mode mode = Mode.Flood;
    public HashSet<Material> floodedMaterials = new HashSet<Material>();
    public Material floodMaterial;
    public Delta[] propagationDirection;
    public ArrayList<FloodedBlock> floodedBlocks;
    
    public static class Delta {
         public int dx, dy, dz;
         
         public Delta(int aDx, int aDy, int aDz) {
             dx = aDx; dy = aDy; dz = aDz;
         }
    }
    
    public class FloodedBlock {
        public int x,y,z;
        public int typeId;
        public byte data;
        
        @Override
        public String toString() {
            return "(" + new Integer(x) + "," + new Integer(y) + "," + new Integer(z) + ")[" + new Integer(typeId) + "," + new Integer(data) + "]";
        }
    }

    protected class FloodItem {
        public int x,y,z;
        
        @Override
        public boolean equals(Object aObject) {
            if (aObject instanceof FloodItem) {
                FloodItem lItem = (FloodItem)aObject;
                return lItem.x == x && lItem.y == y && lItem.z == z;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + this.x;
            hash = 71 * hash + this.y;
            hash = 71 * hash + this.z;
            return hash;
        }
    }

    protected ArrayList<FloodItem> fItems = new ArrayList<FloodItem>();
    protected ArrayList<FloodItem> fAllItems = new ArrayList<FloodItem>();
    protected int fSettedItemsCount;
    protected boolean fInit = false;
    
    public FloodBlocks(DynamicWorld aPlugin) {
        plugin = aPlugin;
    }
    
    protected static final Delta[] fDeltasHorizontalAndDown = initDeltasHorizontalAndDown();
    protected static final Delta[] fDeltasHorizontal = initDeltasHorizontal();
    protected static final Delta[] fDeltasHorizontalAndUp = initDeltasHorizontalAndUp();
    protected static final Delta[] fDeltasHorizontalVertical = initDeltasHorizontalVertical();

    private static Delta[] initDeltasHorizontalAndDown() {
        return new Delta[] {
            new Delta(-1, 0, 0),
            new Delta( 1, 0, 0),
            new Delta( 0, 0,-1),
            new Delta( 0, 0, 1),
            new Delta( 0,-1, 0)
        };
    }

    private static Delta[] initDeltasHorizontal() {
        return new Delta[] {
            new Delta(-1, 0, 0),
            new Delta( 1, 0, 0),
            new Delta( 0, 0,-1),
            new Delta( 0, 0, 1)
        };
    }

    private static Delta[] initDeltasHorizontalAndUp() {
        return new Delta[] {
            new Delta(-1, 0, 0),
            new Delta( 1, 0, 0),
            new Delta( 0, 0,-1),
            new Delta( 0, 0, 1),
            new Delta( 0, 1, 0)
        };
    }

    private static Delta[] initDeltasHorizontalVertical() {
        return new Delta[] {
            new Delta(-1, 0, 0),
            new Delta( 1, 0, 0),
            new Delta( 0, 0,-1),
            new Delta( 0, 0, 1),
            new Delta( 0,-1, 0),
            new Delta( 0, 1, 0)
        };
    }

    public static Delta[] getPropagationDirection(Direction aKind) {
        switch (aKind) {
            case HorizontalAndDown:
                return fDeltasHorizontalAndDown;
            case Horizontal:
                return fDeltasHorizontal;
            case HorizontalAndUp:
                return fDeltasHorizontalAndUp;
            case HorizontalVertical:
                return fDeltasHorizontalVertical;
        }
        return null;
    }

    protected void init() {
        if (!fInit) {
            switch(mode) {
                case Flood:
                    FloodItem lItem = new FloodItem();
                    lItem.x = x;
                    lItem.y = y;
                    lItem.z = z;
                    fItems.add(lItem);
                    fSettedItemsCount = 0;
                    if (floodedMaterials.isEmpty()) {
                        floodedMaterials.add(Material.AIR);
                    }
                    if (floodMaterial == null) {
                        floodMaterial = Material.AIR;
                    }
                    if (propagationDirection == null) {
                        propagationDirection = getPropagationDirection(Direction.HorizontalAndDown);
                    }
                    break;
                case Reverse:
                    fSettedItemsCount = 0;
                    break;
            }
            fInit = true;
        }
    }
    
    @Override
    public void run() {
        if (active) {
            init();
            switch(mode) {
                case Flood:
                    flood();
                    break;
                case Reverse:
                    reverse();
                    break;
            }
        }
    }
    
    protected void flood() {
        fAllItems.addAll(fItems);
        ArrayList<FloodItem> lNewItems = new ArrayList<FloodItem>();
        for(FloodItem lItem : fItems) {
            Block lBlock = world.getBlockAt(lItem.x, lItem.y, lItem.z);
            Material lMat = lBlock.getType();
            if (floodedMaterials.contains(lMat)) {
                if (!lMat.equals(floodMaterial)) {
                    if (floodedBlocks != null) {
                        FloodedBlock lFBlock = new FloodedBlock();
                        lFBlock.x = lBlock.getX();
                        lFBlock.y = lBlock.getY();
                        lFBlock.z = lBlock.getZ();
                        lFBlock.typeId = lBlock.getTypeId();
                        lFBlock.data = lBlock.getData();
                        floodedBlocks.add(lFBlock);
                    }
                    plugin.setTypeAndData(lBlock.getLocation(), floodMaterial, (byte)0, updatePhysics);
                    fSettedItemsCount++;
                }
                for(Delta lDelta : propagationDirection) {
                    FloodItem lNew = new FloodItem();
                    lNew.x = lItem.x + lDelta.dx;
                    lNew.y = lItem.y + lDelta.dy;
                    lNew.z = lItem.z + lDelta.dz;
                    if (lNew.y >= 0 && lNew.y <= world.getMaxHeight()
                            && !lNewItems.contains(lNew)
                            && !fItems.contains(lNew)
                            && !fAllItems.contains(lNew)) {
                        lNewItems.add(lNew);
                    }
                }
            }
        }
        fItems.clear();
        fItems = lNewItems;
        if (fItems.isEmpty() || fSettedItemsCount > maxBlocks) {
            active = false;
            fInit = false;
            fItems.clear();
            fAllItems.clear();
            plugin.stopFloodBlocks(this);
        }
    }
    
    protected int fReverseBlocksInStep = 10;
    
    protected void reverse() {
        int lCount = fReverseBlocksInStep;
        if (floodedBlocks != null) {
            while (fSettedItemsCount < floodedBlocks.size() && lCount >= 0) {
                FloodedBlock lBlock = floodedBlocks.get(fSettedItemsCount);
                plugin.setTypeAndData(new Location(world, (double)lBlock.x, lBlock.y, lBlock.z), Material.getMaterial(lBlock.typeId), lBlock.data, updatePhysics);
                fSettedItemsCount++;
                lCount--;
            }
        }
        if (floodedBlocks == null || fSettedItemsCount >= floodedBlocks.size()) {
            active = false;
            fInit = false;
            plugin.stopFloodBlocks(this);
        }
    }
    
}
