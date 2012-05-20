/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.dynamicworld;

import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

/**
 *
 * @author andre
 */
public class LandSlip implements Runnable {

    public boolean active = false;
    public int x,z;
    public int radius;
    public int strength = 2;
    public World world;
    public int taskId;
    
    protected DynamicWorld plugin;
    protected boolean fInit = false;
    protected int fRadius = 0;
    protected int fStrength = 0;
    
    public LandSlip(DynamicWorld aPlugin) {
        plugin = aPlugin;
    }
    
    @Override
    public void run() {
        if (active) {
            if (!fInit) {
                fInit = true;
            }
            int lRadius = fRadius;
            Logger.getLogger("LandSlip").info("strength " + new Integer(fStrength));
            for(int dx = -lRadius; dx <= lRadius; dx++) {
                for(int dz = -lRadius; dz <= lRadius; dz++) {
                    Block lBlock = world.getHighestBlockAt(x + dx, z + dz);
                    Block lBlockTo = world.getBlockAt(lBlock.getLocation().subtract(0, 1, 0));
                    lBlockTo.setTypeIdAndData(lBlock.getTypeId(), lBlock.getData(), false);
                    lBlock.setTypeId(Material.AIR.getId());
                }
            }
            fRadius++;
            if (fRadius > radius) {
                fRadius = 0;
                fStrength++;
                if (fStrength >= strength) {
                    Logger.getLogger("LandSlip").info("stopped");
                    plugin.getServer().getScheduler().cancelTask(taskId);
                }
            }
        }
    }
    
}
