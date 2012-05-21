/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.dynamicworld;

import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

/**
 *
 * @author andre
 */
public class LandSlip implements Runnable {

    public enum Mode {
        Down,
        Up
    }
    public boolean active = false;
    public int x,z;
    public int radius;
    public int strength = 2;
    public World world;
    public Mode mode = Mode.Down;
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
            int lDy = 0;
            switch (mode) {
                case Down:
                    lDy = -1;
                    break;
                case Up:
                    lDy = 1;
                    break;
            }
            Logger.getLogger("LandSlip").info("strength " + new Integer(fStrength));
            for(int dx = -lRadius; dx <= lRadius; dx++) {
                for(int dz = -lRadius; dz <= lRadius; dz++) {
                    Block lBlock = world.getHighestBlockAt(x + dx, z + dz);
                    lBlock = lBlock.getLocation().add(0,-1,0).getBlock();
                    BlockState lState = lBlock.getState();
                    Location lTo = lBlock.getLocation().add(0, lDy, 0);
                    plugin.setTypeAndData(lTo, lState.getType(), lState.getRawData(), true);
                    //if (mode == Mode.Down) {
                        plugin.setTypeAndData(lBlock.getLocation(), Material.AIR, (byte)0, false);
                    //}
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
