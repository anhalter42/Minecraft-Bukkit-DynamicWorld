/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.dynamicworld;

import com.mahn42.framework.Building;
import java.util.ArrayList;
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

    protected FloodBlocks fLastFlood = null;
    
    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        Player lPlayer = event.getPlayer();
        World lWorld = lPlayer.getWorld();
        Material lInHand = null;
        if (event.hasItem()) {
          lInHand = event.getItem().getType();
        }
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.hasItem() && (lInHand.equals(Material.DIAMOND_PICKAXE) || lInHand.equals(Material.DIAMOND_AXE))) {
                Block lBlock = event.getClickedBlock();
                if (lBlock != null) { 
                    LandSlip lLandSlip = new LandSlip(plugin);
                    lLandSlip.world = lWorld;
                    lLandSlip.x = lBlock.getX();
                    lLandSlip.z = lBlock.getZ();
                    lLandSlip.radius = 5;
                    if (lInHand.equals(Material.DIAMOND_AXE)) {
                        lLandSlip.mode = LandSlip.Mode.Down;
                    } else {
                        lLandSlip.mode = LandSlip.Mode.Up;
                    }
                    lLandSlip.strength = 1;
                    lLandSlip.taskId = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, lLandSlip, 2, 10);
                    lLandSlip.active = true;
                }   
            }
            if (event.hasItem() && lInHand.equals(Material.BONE)) {
                Block lBlock = event.getClickedBlock();
                if (lBlock != null) {
                    if (!plugin.isFloodRunning(lBlock.getX(), lBlock.getY(), lBlock.getZ())) {
                        FloodBlocks lFlood = new FloodBlocks(plugin);
                        lFlood.x = lBlock.getX();
                        lFlood.y = lBlock.getY() + 1;
                        lFlood.z = lBlock.getZ();
                        lFlood.world = lWorld;
                        lFlood.floodDown = true;
                        lFlood.maxBlocks = 10000;
                        lFlood.turnsUntilReverse = 20;
                        lFlood.propagationDirection = FloodBlocks.getPropagationDirection(FloodBlocks.Direction.HorizontalAndDown);
                        lFlood.floodedBlocks = new ArrayList<FloodBlocks.FloodedBlock>();
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
                        if (lPlayer.isSneaking()) {
                            lFlood.mode = FloodBlocks.Mode.FloodReverse;
                        }
                        fLastFlood = lFlood;
                        plugin.startFloodBlocks(lFlood);
                    }
                }   
            } else if (event.hasItem() && lInHand.equals(Material.DIAMOND_SWORD) && fLastFlood != null) {
                Block lBlock = event.getClickedBlock();
                if (lBlock != null) {
                    fLastFlood.mode = FloodBlocks.Mode.Reverse;
                    fLastFlood.updatePhysics = false;
                    plugin.startFloodBlocks(fLastFlood);
                    fLastFlood = null;
                }
            } else if (event.hasItem() && lInHand.equals(Material.BOOK)) {
                Block lBlock = event.getClickedBlock();
                if (lBlock != null) { 
                    ArrayList<Building> lBuildings = plugin.detectBuilding(lWorld, lBlock.getLocation());
                    if (!lBuildings.isEmpty()) {
                        for(Building lBuilding : lBuildings) {
                            lBuilding.playerName = lPlayer.getName();
                            lPlayer.sendMessage("building " + lBuilding.getName()
                                    + " found. ir = " + new Integer(lBuilding.influenceRadius)
                                    + " maxh = " + new Integer(lBuilding.maxHeight));
                            plugin.getLogger().info(lBuilding.toCSV());
                        }
                    } else {
                        lPlayer.sendMessage("no building found.");
                    }
                }
            }
        }
    }
}
