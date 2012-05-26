/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.dynamicworld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 *
 * @author andre
 */
public class BuildingDescription {

    public class RelatedTo {
        Vector direction;
        String block;
        BlockDescription description;
        
        public RelatedTo() {
        }

        public RelatedTo(Vector aDirection, String aBlock) {
            direction = aDirection;
            block = aBlock;
        }
    }
    
    public class BlockDescription {
        String name;
        Material material;
        ArrayList<RelatedTo> relatedTo = new ArrayList<RelatedTo>();
        
        public RelatedTo newRelatedTo() {
            RelatedTo lRel = new RelatedTo();
            relatedTo.add(lRel);
            return lRel;
        }

        public RelatedTo newRelatedTo(Vector aDirection, String aBlock) {
            RelatedTo lRel = newRelatedTo();
            lRel.direction = aDirection;
            lRel.block = aBlock;
            return lRel;
        }
    }
    
    public enum Position {
        onGround,
        underGround,
        everywhere
    }
    
    public String name;
    public Position position = Position.onGround;
    public ArrayList<BlockDescription> blocks = new ArrayList<BlockDescription>();
    public boolean active;
    
    public BuildingDescription() {
    }
    
    public BuildingDescription(String aName) {
        name = aName;
    }
    
    public BlockDescription newBlockDescription(String aName) {
        BlockDescription lBDesc = new BlockDescription();
        lBDesc.name = aName;
        blocks.add(lBDesc);
        return lBDesc;
    }
    
    public void activate() {
        HashMap<String, BlockDescription> lHash = new HashMap<String, BlockDescription>();
        for(BlockDescription lBDesc : blocks) {
            lHash.put(lBDesc.name, lBDesc);
        }
        for(BlockDescription lBDesc : blocks) {
            for(RelatedTo lRel : lBDesc.relatedTo) {
                lRel.description = lHash.get(lRel.block);
            }
        }
        active = true;
    }

    public boolean matchDescription(World aWorld, int lX, int lY, int lZ) {
        Material lMat = aWorld.getBlockAt(lX, lY, lZ).getType();
        ArrayList<BlockDescription> lExcludes = new ArrayList<BlockDescription>();
        for(BlockDescription lBlockDesc : blocks) {
            if (lBlockDesc.material.equals(lMat)) {
                //Logger.getLogger("detect").info("mat " + lMat.name());
                if (!canFollowRelateds(lExcludes, aWorld, lBlockDesc, lX, lY, lZ)) {
                    Logger.getLogger("detect").info("not ok");
                    return false;
                } else {
                    if (lExcludes.size() >= blocks.size()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    protected class RelFollower {
        BlockPosition pos;
        BlockDescription desc;
    }
    private boolean canFollowRelateds(ArrayList<BlockDescription> aExcludes, World aWorld, BlockDescription lBlockDesc, int lX, int lY, int lZ) {
        if (!aExcludes.contains(lBlockDesc) && lBlockDesc.relatedTo.size() > 0) {
            Logger.getLogger("detect").info("check desc " + lBlockDesc.name + " at " + new BlockPosition(lX, lY, lZ));
            ArrayList<RelFollower> lFs = new ArrayList<RelFollower>();
            for(RelatedTo lRel : lBlockDesc.relatedTo) {
                boolean lRelated = false;
                boolean lFirst = true;
                BlockPosition lRelatedPos = null;
                for(BlockPosition lPos : new WorldLineWalk(
                        new BlockPosition(lX, lY, lZ),
                        new BlockPosition(lX + lRel.direction.getBlockX(), lY + lRel.direction.getBlockY(), lZ + lRel.direction.getBlockZ()))) {
                    if (lFirst) {
                        lFirst = false;
                    } else {
                        Logger.getLogger("detect").info("rel " + lRel.description.name + " at " + lPos + " mat " + lPos.getBlockType(aWorld).name());
                        if (lPos.getBlockType(aWorld).equals(lRel.description.material)) {
                            Logger.getLogger("detect").info("found rel " + lRel.description.name);
                            lRelatedPos = lPos;
                            lRelated = true;
                            break;
                        }
                    }
                }
                if (!lRelated) {
                    return false;
                }
                if (!aExcludes.contains(lRel.description)) {
                    RelFollower lF = new RelFollower();
                    lF.pos = lRelatedPos;
                    lF.desc = lRel.description;
                    lFs.add(lF);
                    Logger.getLogger("detect").info("found rel " + lF.desc.name);
                }
            }
            if (!aExcludes.contains(lBlockDesc)) {
                aExcludes.add(lBlockDesc);
            }
            for(RelFollower lF : lFs) {
                if (!canFollowRelateds(aExcludes, aWorld, lF.desc, lF.pos.x, lF.pos.y, lF.pos.z)) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }

}
