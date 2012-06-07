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
        Reverse,
        FloodReverse
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
    public int turnsUntilReverse = 5;
    public boolean floodDown = false;
    
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

    protected int fTurns;
    protected ArrayList<Integer> fReverseCounts = new ArrayList<Integer>();
    protected int fReversePos;

    protected void init() {
        if (!fInit) {
            fTurns = 0;
            fReverseCounts.clear();
            fReversePos = 0;
            switch(mode) {
                case Flood: case FloodReverse:
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
                    if (mode == Mode.FloodReverse) {
                        if (floodedBlocks == null) {
                            floodedBlocks = new ArrayList<FloodedBlock>();
                        }
                    }
                    break;
                case Reverse:
                    fSettedItemsCount = 0;
                    break;
            }
            fInit = true;
        }
    }

    protected boolean fInRun = false;
    
    @Override
    public void run() {
        if (active && !fInRun) {
            fInRun = true;
            try {
                init();  
                switch(mode) {
                    case Flood: case FloodReverse:
                        flood();
                        break;
                    case Reverse:
                        reverse();
                        break;
                }
                fTurns++;
            } finally {
                fInRun = false;
            }
        }
    }
    
    protected void flood() {
        if (fSettedItemsCount < maxBlocks) {
            fAllItems.addAll(fItems);
            ArrayList<FloodItem> lNewItems = new ArrayList<FloodItem>();
            int lOldFloodedCount = floodedBlocks.size();
            for(FloodItem lItem : fItems) {
                Block lBlock = world.getBlockAt(lItem.x, lItem.y, lItem.z);
                Material lMat = lBlock.getType();
                if (floodedMaterials.contains(lMat)) {
                    if (floodDown) {
                        floodDown(lItem.x, lItem.y, lItem.z);
                    } else {
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
                    }
                    floodDirections(lItem, lNewItems);
                }
            }
            fItems.clear();
            fItems = lNewItems;
            fReverseCounts.add(floodedBlocks.size() - lOldFloodedCount);
        }
        if (mode == Mode.FloodReverse && fTurns > turnsUntilReverse && !fReverseCounts.isEmpty()) {
            int lCount = fReverseCounts.get(0);
            fReverseCounts.remove(0);
            while (fReversePos < floodedBlocks.size() && lCount >= 0) {
                FloodedBlock lBlock = floodedBlocks.get(fReversePos);
                fReversePos++;
                plugin.setTypeAndData(new Location(world, (double)lBlock.x, lBlock.y, lBlock.z), Material.getMaterial(lBlock.typeId), lBlock.data, false);
                lCount--;
            }
            if (fReverseCounts.isEmpty()) {
                floodStop();
            }
        }
        if (mode == Mode.Flood && (fItems.isEmpty() || fSettedItemsCount > maxBlocks)) {
            floodStop();
        }
    }

    protected void floodDirections(FloodItem aItem, ArrayList<FloodItem> aNewItems) {
        for(Delta lDelta : propagationDirection) {
            FloodItem lNew = new FloodItem();
            lNew.x = aItem.x + lDelta.dx;
            lNew.y = aItem.y + lDelta.dy;
            lNew.z = aItem.z + lDelta.dz;
            if (lNew.y >= 0 && lNew.y <= world.getMaxHeight()
                    && !aNewItems.contains(lNew)
                    && !fItems.contains(lNew)
                    && !fAllItems.contains(lNew)) {
                aNewItems.add(lNew);
            }
        }
    }
    
    protected void floodDown(int aX, int aY, int aZ) {
        do {
            Block lBlock = world.getBlockAt(aX, aY, aZ);
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
                    aY--;
                } else {
                    return;
                }
            } else {
                return;
            }
        } while (aY > 0);
    }
    

    protected void floodStop() {
        active = false;
        fInit = false;
        fItems.clear();
        fAllItems.clear();
        plugin.stopFloodBlocks(this);
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
