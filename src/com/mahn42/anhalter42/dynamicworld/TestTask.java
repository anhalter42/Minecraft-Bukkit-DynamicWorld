/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.dynamicworld;

import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.SyncBlockList;
import com.mahn42.framework.WoolColors;
import com.mahn42.framework.WorldCircleWalk;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.World;
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
            f1_Item = f1_World.spawn(f1_Walk.positions.get(f1_WalkPos).getLocation(f1_World), org.bukkit.entity.Snowball.class);
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
        lVelo.multiply(0.5);
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
    
}
