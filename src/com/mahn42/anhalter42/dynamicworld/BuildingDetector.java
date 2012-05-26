/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.dynamicworld;

import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 *
 * @author andre
 */
public class BuildingDetector {
    protected ArrayList<BuildingDescription> fDescriptions;
    
    public BuildingDetector() {
        fDescriptions = new ArrayList<BuildingDescription>();
    }
    
    public BuildingDescription detect(World aWorld, BlockPosition aPos1, BlockPosition aPos2) {
        int dx = aPos1.x > aPos2.x ? -1 : 1;
        int dy = aPos1.y > aPos2.y ? -1 : 1;
        int dz = aPos1.z > aPos2.z ? -1 : 1;
        Logger.getLogger("detect").info("count " + new Integer(fDescriptions.size()));
        Logger.getLogger("detect").info(aPos1.toString() + " - " + aPos2.toString());
        for(BuildingDescription lDesc : fDescriptions) {
            Logger.getLogger("detect").info("teste " + lDesc.name);
            for(int lX = aPos1.x; lX <= aPos2.x; lX+=dx) {
                for(int lY = aPos1.y; lY <= aPos2.y; lY+=dy) {
                    for(int lZ = aPos1.z; lZ <= aPos2.z; lZ+=dz) {
                        //Logger.getLogger("detect").info("teste " + new Integer(lX) + "," + new Integer(lY) + "," + new Integer(lZ));
                        if (matchDescription(lDesc, aWorld, lX, lY, lZ)) {
                            return lDesc;
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean matchDescription(BuildingDescription lDesc, World aWorld, int lX, int lY, int lZ) {
        return lDesc.matchDescription(aWorld, lX, lY, lZ);
    }

    public BuildingDescription newDescription(String aName) {
        BuildingDescription lDesc = new BuildingDescription(aName);
        fDescriptions.add(lDesc);
        return lDesc;
    }
}
