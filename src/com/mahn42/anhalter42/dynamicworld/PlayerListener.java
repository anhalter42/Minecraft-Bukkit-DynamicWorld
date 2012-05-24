/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.dynamicworld;

import java.util.HashSet;
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
        Material lInHand = null;
        if (event.hasItem()) {
          lInHand = event.getItem().getType();
        }
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
                    if (!plugin.isFloodRunning(lBlock.getX(), lBlock.getY(), lBlock.getZ())) {
                        FloodBlocks lFlood = new FloodBlocks(plugin);
                        lFlood.x = lBlock.getX();
                        lFlood.y = lBlock.getY() + 1;
                        lFlood.z = lBlock.getZ();
                        lFlood.world = lWorld;
                        lFlood.maxBlocks = 40;
                        lFlood.propagationDirection = FloodBlocks.getPropagationDirection(FloodBlocks.Direction.HorizontalAndDown);
                        lFlood.floodedMaterials.clear();
                        if (lBlock.getLocation().add(0, 1, 0).getBlock().isLiquid()) {
                            lFlood.floodedMaterials.add(Material.WATER);
                            lFlood.floodedMaterials.add(Material.STATIONARY_WATER);
                            lFlood.floodedMaterials.add(Material.LAVA);
                            lFlood.floodedMaterials.add(Material.STATIONARY_LAVA);
                            lFlood.floodMaterial = Material.AIR;
                            lFlood.updatePhysics = false;
                        } else {
                            lFlood.floodedMaterials.add(Material.AIR);
                            lFlood.floodedMaterials.add(Material.WATER_LILY);
                            lFlood.floodedMaterials.add(Material.LAVA);
                            lFlood.floodedMaterials.add(Material.STATIONARY_LAVA);
                            lFlood.floodedMaterials.add(Material.WATER);
                            lFlood.floodedMaterials.add(Material.STATIONARY_WATER);
                            lFlood.floodedMaterials.add(Material.LONG_GRASS);
                            lFlood.floodedMaterials.add(Material.YELLOW_FLOWER);
                            lFlood.floodedMaterials.add(Material.RED_ROSE);
                            lFlood.floodMaterial = Material.STATIONARY_WATER;
                            lFlood.updatePhysics = true;
                        }
                        plugin.startFloodBlocks(lFlood);
                    }
                    /*
                    if (!plugin.isWaterFloodRunning(lBlock.getX(), lBlock.getY(), lBlock.getZ())) {
                        WaterFlood lWaterFlood = new WaterFlood(plugin);
                        lWaterFlood.x = lBlock.getX();
                        lWaterFlood.y = lBlock.getY();
                        lWaterFlood.z = lBlock.getZ();
                        lWaterFlood.world = lWorld;
                        if (lBlock.getLocation().add(0, 1, 0).getBlock().isLiquid()) {
                            lWaterFlood.mode = WaterFlood.Mode.Unfill;
                        } else {
                            lWaterFlood.mode = WaterFlood.Mode.Fill;
                        }
                        plugin.startWaterFlood(lWaterFlood);
                    }
                    */
                }   
            }
        }
    }
}
