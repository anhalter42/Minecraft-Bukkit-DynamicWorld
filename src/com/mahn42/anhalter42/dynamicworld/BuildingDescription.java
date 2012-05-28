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
    
    public BlockDescription getBlock(String lDescName) {
        for(BlockDescription lBlock : blocks) {
            if (lDescName.equalsIgnoreCase(lBlock.name)) {
                return lBlock;
            }
        }
        return null;
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

    public Building matchDescription(World aWorld, int lX, int lY, int lZ) {
        Material lMat = aWorld.getBlockAt(lX, lY, lZ).getType();
        ArrayList<BlockDescription> lExcludes = new ArrayList<BlockDescription>();
        for(BlockDescription lBlockDesc : blocks) {
            if (lBlockDesc.material.equals(lMat)) {
                lExcludes.clear();
                Building aBuilding = new Building();
                aBuilding.description = this;
                //Logger.getLogger("detect").info("mat " + lMat.name());
                if (!canFollowRelateds(lExcludes, aBuilding, aWorld, lBlockDesc, lX, lY, lZ)) {
                    Logger.getLogger("detect").info("not ok");
                    return null;
                } else {
                    if (lExcludes.size() >= blocks.size()) {
                        aBuilding.update();
                        return aBuilding;
                    }
                }
            }
        }
        return null;
    }
    
    protected class RelFollower {
        BlockPosition pos;
        BlockDescription desc;
    }
    private boolean canFollowRelateds(ArrayList<BlockDescription> aExcludes, Building aBuilding, World aWorld, BlockDescription lBlockDesc, int lX, int lY, int lZ) {
        if (!aExcludes.contains(lBlockDesc)) {
            aBuilding.blocks.add(new BuildingBlock(lBlockDesc, new BlockPosition(lX, lY, lZ)));
            if (lBlockDesc.relatedTo.size() > 0) {
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
                            //Logger.getLogger("detect").info("rel " + lRel.description.name + " at " + lPos + " mat " + lPos.getBlockType(aWorld).name());
                            if (lPos.getBlockType(aWorld).equals(lRel.description.material)) {
                                //Logger.getLogger("detect").info("found rel1 " + lRel.description.name);
                                lRelatedPos = lPos;
                                lRelated = true;
                                break;
                            }
                        }
                    }
                    if (!lRelated) {
                        Logger.getLogger("detect").info("rel1 " + lRel.description.name + " not match");
                        return false;
                    }
                    if (!aExcludes.contains(lRel.description)) {
                        RelFollower lF = new RelFollower();
                        lF.pos = lRelatedPos;
                        lF.desc = lRel.description;
                        lFs.add(lF);
                        Logger.getLogger("detect").info("found rel2 " + lF.desc.name);
                    }
                }
                if (!aExcludes.contains(lBlockDesc)) {
                    Logger.getLogger("detect").info("excluded add " + lBlockDesc.name);
                    aExcludes.add(lBlockDesc);
                }
                for(RelFollower lF : lFs) {
                    Logger.getLogger("detect").info("follow rel " + lF.desc.name);
                    if (!canFollowRelateds(aExcludes, aBuilding, aWorld, lF.desc, lF.pos.x, lF.pos.y, lF.pos.z)) {
                        Logger.getLogger("detect").info("rel2 " + lF.desc.name + " not match");
                        return false;
                    }
                }
                return true;
            } else {
                aExcludes.add(lBlockDesc);
                return true;
            }
        } else {
            return true;
        }
    }

}