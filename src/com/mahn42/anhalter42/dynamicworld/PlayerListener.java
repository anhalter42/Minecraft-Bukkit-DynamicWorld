/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.dynamicworld;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author andre
 */
public class PlayerListener implements Listener {

    DynamicWorld plugin;
    
    public PlayerListener(DynamicWorld aPlugin) {
        plugin = aPlugin;
    }
    
    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        Player lPlayer = event.getPlayer();
        World lWorld = lPlayer.getWorld();
        Material lInHand = event.getItem().getType();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.hasItem() && (lInHand.equals(Material.APPLE) || lInHand.equals(Material.BOOK))) {
                Block lBlock = event.getClickedBlock();
                if (lBlock != null) { 
                    LandSlip lLandSlip = new LandSlip(plugin);
                    lLandSlip.world = lWorld;
                    lLandSlip.x = lBlock.getX();
                    lLandSlip.z = lBlock.getZ();
                    lLandSlip.radius = 5;
                    if (lInHand.equals(Material.APPLE)) {
                        lLandSlip.mode = LandSlip.Mode.Down;
                    } else {
                        lLandSlip.mode = LandSlip.Mode.Up;
                    }
                    lLandSlip.strength = 1;
                    lLandSlip.taskId = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, lLandSlip, 2, 10);
                    lLandSlip.active = true;
                }   
            }
            if (event.hasItem() && (lInHand.equals(Material.BONE))) {
                Block lBlock = event.getClickedBlock();
                if (lBlock != null) { 
                    WaterFlood lWaterFlood = plugin.createWaterFlood(lBlock.getX(), lBlock.getY(), lBlock.getZ());
                    lWaterFlood.world = lWorld;
                    if (lBlock.getLocation().add(0, 1, 0).getBlock().isLiquid()) {
                        lWaterFlood.mode = WaterFlood.Mode.Unflood;
                    } else {
                        lWaterFlood.mode = WaterFlood.Mode.Flood;
                    }
                    lWaterFlood.taskId = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, lWaterFlood, 2, 10);
                    lWaterFlood.active = true;
                }   
            }
        }
    }
}
