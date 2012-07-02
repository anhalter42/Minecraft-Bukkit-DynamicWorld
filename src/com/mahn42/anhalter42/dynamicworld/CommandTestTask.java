/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.dynamicworld;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author andre
 */
public class CommandTestTask implements CommandExecutor  {

    @Override
    public boolean onCommand(CommandSender aCommandSender, Command aCommand, String aString, String[] aStrings) {
        TestTask lTask = new TestTask();
        if (aCommandSender instanceof Player) {
          lTask.player = (Player) aCommandSender;
        }
        if (aStrings.length > 0) lTask.par1 = Integer.parseInt(aStrings[0]);
        if (aStrings.length > 1) lTask.par2 = Integer.parseInt(aStrings[1]);
        if (aStrings.length > 2) lTask.par3 = Integer.parseInt(aStrings[2]);
        lTask.taskId = DynamicWorld.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(DynamicWorld.plugin, lTask, 10, 10);
        return true;
    }
    
}
