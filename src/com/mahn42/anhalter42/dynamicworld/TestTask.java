/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.dynamicworld;

import com.mahn42.framework.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 *
 * @author andre
 */
public class TestTask implements Runnable {

    public int taskId = 0;
    public Player player = null;
    public int par1 = 0;
    public int par2 = 0;
    public int par3 = 0;
    
    protected boolean fInit = false;

    @Override
    public void run() {
        log("run " + taskId + " with " + par1 + " " + par2 + " " + par3);
        switch (par1) {
            case 0:
                execute_0();
                break;
            case 1:
                execute_1();
                break;
            case 2:
                execute_2();
                break;
            case 3:
                execute_3();
                break;
            default:
                stop();
                break;
        }
        fInit = true;
    }

    protected void stop() {
        log("stop " + taskId + " with " + par1 + " " + par2 + " " + par3);
        DynamicWorld.plugin.getServer().getScheduler().cancelTask(taskId);
    }
    
    protected void log(String aText) {
        Logger.getLogger("TestTask").info(aText);
    }
    
    protected int f1_High = 0;
    protected int f1_WalkPos = 0;
    protected BlockPosition f1_Center;
    protected World f1_World;
    protected WorldCircleWalk f1_Walk;
    
    private void execute_0() {
        // par2 = radius par3 = high
        if (!fInit) {
            f1_High = 0;
            if (player != null) {
                f1_World = player.getWorld();
                f1_Center = new BlockPosition(player.getLocation());
            } else {
                f1_World = DynamicWorld.plugin.getServer().getWorld("world");
                f1_Center = new BlockPosition(f1_World.getSpawnLocation());
            }
        }
        SyncBlockList lList = new SyncBlockList(f1_World);
        if (f1_WalkPos == 0) {
            f1_Walk = new WorldCircleWalk(f1_Center, par2);
        }
        lList.add(f1_Walk.positions.get(f1_WalkPos), Material.WOOL, WoolColors.white, true);
        /*
        for(BlockPosition lPos : new WorldCircleWalk(f1_Center, par2)) {
            lList.add(lPos, Material.WOOL, WoolColors.white, true);
        }
        */
        lList.execute();
        f1_WalkPos++;
        if (f1_WalkPos >= f1_Walk.positions.size()) {
            f1_WalkPos = 0;
            f1_High++;
            f1_Center.add(0, 1, 0);            
        }
        if (f1_High >= par3) {
            stop();
        }
    }

    protected Entity f1_Item;
    
    private void execute_1() {
        // par2 = radius par3 = high
        if (!fInit) {
            f1_High = 0;
            if (player != null) {
                f1_World = player.getWorld();
                f1_Center = new BlockPosition(player.getLocation());
            } else {
                f1_World = DynamicWorld.plugin.getServer().getWorld("world");
                f1_Center = new BlockPosition(f1_World.getSpawnLocation());
            }
            /*
            ItemStack lStack = new ItemStack(Material.FIREBALL, 1);
            f1_Item = f1_World.dropItem(f1_Center.getLocation(f1_World), lStack);
            */
            //f1_Item.setFallDistance(0);
        }
        //SyncBlockList lList = new SyncBlockList(f1_World);
        if (f1_WalkPos == 0) {
            f1_Walk = new WorldCircleWalk(f1_Center, par2);
        }
        if (f1_Item == null) {
            //f1_Item = f1_World.spawn(f1_Center.getLocation(f1_World), Fireball.class);
            f1_Item = f1_World.spawn(f1_Walk.positions.get(f1_WalkPos).getLocation(f1_World), org.bukkit.entity.Boat.class);
            //f1_Item = f1_World.spawn(f1_Walk.positions.get(f1_WalkPos).getLocation(f1_World), org.bukkit.entity.FallingSand.class);
            //((org.bukkit.entity.Boat)f1_Item).
            //f1_Item.setBounce(false);
            //((Fireball)f1_Item).setBounce(false);
        }
        if (f1_Item.isDead()) stop();
        Vector lVelo = new Vector(
                f1_Walk.positions.get(f1_WalkPos).x - f1_Item.getLocation().getBlockX(),
                f1_Walk.positions.get(f1_WalkPos).y - f1_Item.getLocation().getBlockY(),
                f1_Walk.positions.get(f1_WalkPos).z - f1_Item.getLocation().getBlockZ());
        //((Fireball)f1_Item).setDirection(lVelo);
        lVelo = lVelo.normalize();
        lVelo.multiply(0.2);
        //f1_Item.setFallDistance(0);
        //f1_Item.setFallDistance(1000);
        //f1_Item.teleport(f1_Walk.positions.get(f1_WalkPos).getLocation(f1_World), PlayerTeleportEvent.TeleportCause.PLUGIN);
        f1_Item.setVelocity(lVelo);
        log("dest " + f1_Walk.positions.get(f1_WalkPos) + " cur " + new BlockPosition(f1_Item.getLocation()));
        //lList.add(f1_Walk.positions.get(f1_WalkPos), Material.WOOL, WoolColors.white, true);
        //lList.execute();
        f1_WalkPos++;
        if (f1_WalkPos >= f1_Walk.positions.size()) {
            f1_WalkPos = 0;
            f1_High++;
            f1_Center.add(0, 1, 0);            
        }
        if (f1_High >= par3) {
            stop();
        }
    }
    
    private void execute_2() {
        if (!fInit) {
            f1_High = 0;
            if (player != null) {
                f1_World = player.getWorld();
                f1_Center = new BlockPosition(player.getLocation());
            } else {
                f1_World = DynamicWorld.plugin.getServer().getWorld("world");
                f1_Center = new BlockPosition(f1_World.getSpawnLocation());
            }
        }
        if (f1_High >= par3) {
            if (f1_Walk != null) {
                SyncBlockList lList = new SyncBlockList(f1_World);
                lList.add(f1_Walk.positions.get(f1_Walk.positions.size()-1), Material.AIR, (byte)0);
                lList.execute();
            }
            stop();
        } else {
            SyncBlockList lList = new SyncBlockList(f1_World);
            if (f1_WalkPos == 0) {
                if (f1_Walk != null) {
                    lList.add(f1_Walk.positions.get(f1_Walk.positions.size()-1), Material.AIR, (byte)0);
                }
                f1_Walk = new WorldCircleWalk(f1_Center, par2);
            }
            BlockPosition lPos = f1_Walk.positions.get(f1_WalkPos);
            if (f1_WalkPos > 0) {
                lList.add(f1_Walk.positions.get(f1_WalkPos-1), Material.AIR, (byte)0);
            }
            lList.add(lPos, Material.WOOL, WoolColors.white);
            lList.execute();
            f1_WalkPos++;
            if (f1_WalkPos >= f1_Walk.positions.size()) {
                f1_WalkPos = 0;
                f1_High++;
                f1_Center.add(0, 1, 0);            
            }
        }
    }
    
    BlockPosition f2_pos1;
    BlockPosition f2_pos2;
    Random f2_Random = new Random();
    int f2_dx;
    int f2_dz;
    int f2_height;
    int f2_length = 5;
    
    public class WavePos {
        public BlockPosition pos;
        public boolean active = true;
        public int height = 0;
        
        public WavePos(WavePos aWave) {
            pos = aWave.pos.clone();
            height = aWave.height;
            active = aWave.active;
        }

        public WavePos(BlockPosition aPos, int aHeight, boolean aActive) {
            pos = aPos.clone();
            height = aHeight;
            active = aActive && (aHeight > 0);
        }

        public WavePos(BlockPosition aPos, int aHeight) {
            pos = aPos.clone();
            height = aHeight;
            active = aHeight > 0;
        }
    }
    
    public class WavePosList extends ArrayList<WavePos> {
        public boolean isSomeActive() {
            for(WavePos lPos : this) {
                if (lPos.active) {
                    return true;
                }
            }
            return false;
        }
    }
    
    ArrayList<WavePosList> f2_lines = new ArrayList<WavePosList>();
    //ArrayList<WavePosList> f2_removelines = new ArrayList<WavePosList>();
    
    private void execute_3() {
        int lY;
        if (!fInit) {
            f1_High = 0;
            f2_length = par3;
            if (player != null) {
                f1_World = player.getWorld();
                f1_Center = new BlockPosition(player.getTargetBlock(null, 50).getLocation());
                f1_Center.y++;
            } else {
                f1_World = DynamicWorld.plugin.getServer().getWorld("world");
                f1_Center = new BlockPosition(f1_World.getSpawnLocation());
            }
            lY = f1_World.getHighestBlockYAt(f1_Center.x, f1_Center.z);
            if (f1_Center.y < lY) {
                f1_Center.y = lY;
            }
            f2_height = f1_Center.y - lY;
            int lVec = f2_Random.nextInt(3);
            switch(lVec) {
                case 0:
                    f2_dx = 1; f2_dz = 0;
                    break;
                case 1:
                    f2_dx = -1; f2_dz = 0;
                    break;
                case 2:
                    f2_dx = 0; f2_dz = 1;
                    break;
                case 3:
                    f2_dx = 0; f2_dz = -1;
                    break;
            }
            int lLength = 5 + f2_Random.nextInt(par2);
            f2_pos1 = f1_Center.clone();
            f2_pos1.add(f2_dx * lLength, 0, f2_dz * lLength);
            f2_pos2 = f1_Center.clone();
            f2_pos2.add(-f2_dx * lLength, 0, -f2_dz * lLength);
            BlockPosition lLastPos = null;
            for(BlockPosition lPos : new WorldLineWalk(f1_Center, f2_pos1)) {
                if (f1_World.getHighestBlockYAt(lPos.x, lPos.z) > f1_Center.y) {
                    f2_pos1 = lLastPos;
                    break;
                }
                lLastPos = lPos;
            }
            if (f2_pos1 == null) {
                stop();
                return;
            }
            lLastPos = null;
            for(BlockPosition lPos : new WorldLineWalk(f1_Center, f2_pos2)) {
                if (f1_World.getHighestBlockYAt(lPos.x, lPos.z) > f1_Center.y) {
                    f2_pos2 = lLastPos;
                    break;
                }
                lLastPos = lPos;
            }
            if (f2_pos2 == null) {
                stop();
                return;
            }
            for(int lLineIdx = 0; lLineIdx < f2_length; lLineIdx++) {
                WavePosList lLine = new WavePosList();
                for(BlockPosition lPos : new WorldLineWalk(f2_pos1, f2_pos2)) {
                    lY = f1_World.getHighestBlockYAt(lPos.x, lPos.z);
                    lLine.add(new WavePos(lPos, (lPos.y - lY) + 1));
                }
                f2_lines.add(lLine);
                f2_pos1.add(f2_dz, 0, f2_dx);
                f2_pos2.add(f2_dz, 0, f2_dx);
            }
        }
        SyncBlockList lList = new SyncBlockList(f1_World);
        for(int lLineIdx = 0; lLineIdx < f2_length; lLineIdx++) {
            for(WavePos lWave : f2_lines.get(lLineIdx)) {
                WavePos lOldWave = new WavePos(lWave);
                lWave.pos.add(f2_dz, 0, f2_dx);
                lY = f1_World.getHighestBlockYAt(lWave.pos.x, lWave.pos.z);
                lWave.height = (lWave.pos.y - lY) + 1;
                
                if (lWave.active) {
                    Block lBlock = lWave.pos.getBlock(f1_World);
                    Material lMat = lBlock.getType();
                    lWave.active = (lMat.equals(Material.AIR) || lMat.equals(Material.WATER) || lMat.equals(Material.STATIONARY_WATER));
                }
            }
        }
        stop();
        /*
        for(WavePos lPos : lLine) {
            BlockPosition lNewPos = lPos.pos.clone();
            lNewPos.add(f2_dz, 0, f2_dx);
            lY = f1_World.getHighestBlockYAt(lNewPos.x, lNewPos.z);
            Block lBlock = lNewPos.getBlock(f1_World);
            Material lMat = lBlock.getType();
            boolean lAct = lPos.active && (lMat.equals(Material.AIR) || lMat.equals(Material.WATER) || lMat.equals(Material.STATIONARY_WATER));
            int lHeight = (lNewPos.y - lY) + 1;
            WavePos lWave = new WavePos(lNewPos, lHeight, lAct);
            lWave.prev_height = lPos.height;
            // nach unten fließen
            if (lWave.height > lPos.height) {
                lWave.pos.add(0,lPos.height - lWave.height,0);
                lWave.height = (lWave.pos.y - lY) + 1;
            }
            // abgeschnitten
            //else if (lWave.height < lPos.height) {
                // vorherige Welle ist geschnitten?
                if (lPos.prev_height > lPos.height) { // jetzt dazu packen
                    lWave.pos.add(0, lPos.prev_height - lPos.height, 0);
                    lWave.height = (lWave.pos.y - lY) + 1;
                }
            //}
            f2_line.add(lWave);
        }
        for(WavePos lPos : lLine) {
            if (lPos.active) {
                for(int lYY = 0; lYY < lPos.height; lYY++) {
                    BlockPosition lbpos = lPos.pos.clone();
                    lbpos.add(0,-lYY,0);
                    //lList.add(lbpos, Material.STATIONARY_WATER, (byte)0);
                    lList.add(lbpos, Material.WOOL, WoolColors.red);
                }
            }
        }
        while (!f2_removelines.isEmpty()
                && ((f2_removelines.size() > f2_length) || !f2_line.isSomeActive())) {
            lLine = f2_removelines.get(0);
            f2_removelines.remove(0);
            for(WavePos lPos : lLine) {
                if (lPos.active) {
                    for(int lYY = 0; lYY < lPos.height; lYY++) {
                        BlockPosition lbpos = lPos.pos.clone();
                        lbpos.add(0,-lYY,0);
                        lList.add(lbpos, Material.AIR, (byte)0);
                    }
                }
            }
        }
        lList.execute();
        if (!f2_line.isSomeActive() && f2_removelines.isEmpty()) {
            stop();
        }*/
    }    
}
