/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.dynamicworld;

import java.util.ArrayList;
import org.bukkit.util.Vector;

/**
 *
 * @author andre
 */
public class Building extends DBRecord {
    public String name;
    public String playerName;
    public BuildingDescription description;
    public BlockPosition edge1 = new BlockPosition();
    public BlockPosition edge2 = new BlockPosition();
    public ArrayList<BuildingBlock> blocks = new ArrayList<BuildingBlock>();

    public String getName() {
        return name == null ? description.name + "(" + playerName + ")" : name;
    }
    
    @Override
    protected void toCSVInternal(ArrayList aCols) {
        super.toCSVInternal(aCols);
        aCols.add(name);
        aCols.add(playerName);
        aCols.add(description.name);
        aCols.add(edge1.x);
        aCols.add(edge1.y);
        aCols.add(edge1.z);
        aCols.add(edge2.x);
        aCols.add(edge2.y);
        aCols.add(edge2.z);
        //aCols.add(blocks.size());
        String lBlocks = null;
        for(BuildingBlock lBlock : blocks) {
            if (lBlocks == null) {
                lBlocks = lBlock.toCSVValue();
            } else {
                lBlocks = lBlocks + "|" + lBlock.toCSVValue();
            }
        }
        aCols.add(lBlocks);
    }

    @Override
    protected void fromCSVInternal(DBRecordCSVArray aCols) {
        super.fromCSVInternal(aCols);
        name = aCols.pop();
        playerName = aCols.pop();
        String lDescName = aCols.pop();
        description = DynamicWorld.plugin.getBuildingDescription(lDescName);
        edge1.x = aCols.popInt();
        edge1.y = aCols.popInt();
        edge1.z = aCols.popInt();
        edge2.x = aCols.popInt();
        edge2.y = aCols.popInt();
        edge2.z = aCols.popInt();
        //int lCount = aCols.popInt();
        String lBlocks = aCols.pop();
        String lBlockStr[] = lBlocks.split("|");
        for(String lCSVValue : lBlockStr ) {
            BuildingBlock lBlock = new BuildingBlock();
            lBlock.fromCSVValue(description, lCSVValue);
        }
    }
    
    public void update() {
        edge1.x = Integer.MAX_VALUE;
        edge1.y = Integer.MAX_VALUE;
        edge1.z = Integer.MAX_VALUE;
        edge2.x = Integer.MIN_VALUE;
        edge2.y = Integer.MIN_VALUE;
        edge2.z = Integer.MIN_VALUE;
        for(BuildingBlock lBlock : blocks) {
            edge1.x = Math.min(edge1.x, lBlock.position.x);
            edge1.y = Math.min(edge1.y, lBlock.position.y);
            edge1.z = Math.min(edge1.z, lBlock.position.z);
            edge2.x = Math.max(edge2.x, lBlock.position.x);
            edge2.y = Math.max(edge2.y, lBlock.position.y);
            edge2.z = Math.max(edge2.z, lBlock.position.z);
        }
    }
    
    public boolean isInside(BlockPosition aPos) {
        return aPos.x >= edge1.x && aPos.x <= edge2.x
            && aPos.y >= edge1.y && aPos.y <= edge2.y
            && aPos.z >= edge1.z && aPos.z <= edge2.z;
    }
    
    public BuildingBlock getBlock(BlockPosition aPos) {
        for(BuildingBlock lBlock : blocks) {
            if (lBlock.position.equals(aPos)) {
                return lBlock;
            }
        }
        return null;
    }

    public BuildingBlock getNearestBlock(BlockPosition aPos) {
        double lResDist = Double.MAX_VALUE;
        BuildingBlock lResult = null;
        Vector lV1 = aPos.getVector();
        for(BuildingBlock lBlock : blocks) {
            Vector lV2 = lBlock.position.getVector();
            double lDistance = lV1.distance(lV2);
            if (lDistance < lResDist) {
                lResDist = lDistance;
                lResult = lBlock;
            }
        }
        return lResult;
    }
}