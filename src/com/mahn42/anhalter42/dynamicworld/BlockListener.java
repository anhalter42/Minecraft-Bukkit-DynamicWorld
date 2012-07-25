/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.dynamicworld;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockBreakEvent;

/**
 *
 * @author andre
 */
public class BlockListener implements Listener {
    
    protected DynamicWorld plugin;
    
    public BlockListener(DynamicWorld aPlugin) {
        plugin = aPlugin;
    }
    
    @EventHandler
    public void breakBlock(BlockBreakEvent aEvent) {
        Block lBlock = aEvent.getBlock();
        //plugin.getLogger().info("BlockBreakEvent:" + lBlock.toString());
    }

    @EventHandler
    public void redstoneBlock(BlockRedstoneEvent aEvent) {
        Block lBlock = aEvent.getBlock();
        //plugin.getLogger().info("BlockRedstoneEvent:" + lBlock.toString() + " current:" + new Integer(aEvent.getNewCurrent()));
    }
}
