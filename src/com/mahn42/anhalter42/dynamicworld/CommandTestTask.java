/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.dynamicworld;

import com.mahn42.framework.Base64;
import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.BlockPositionDelta;
import com.mahn42.framework.EntityControl;
import com.mahn42.framework.EntityControlPathItem;
import com.mahn42.framework.EntityControlPathItemDestination;
import com.mahn42.framework.EntityControlPathItemRelative;
import com.mahn42.framework.EntityControlPathItemTarget;
import com.mahn42.framework.Framework;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author andre
 */
public class CommandTestTask implements CommandExecutor  {

    //ConversationFactory factory;
    @Override
    public boolean onCommand(CommandSender aCommandSender, Command aCommand, String aString, String[] aStrings) {
        /*
        if (factory == null) {
            factory = new ConversationFactory(DynamicWorld.plugin);
            factory.withFirstPrompt(new MessagePrompt() {

                @Override
                protected Prompt getNextPrompt(ConversationContext cc) {
                    return new PlayerNamePrompt(DynamicWorld.plugin) {

                        @Override
                        protected Prompt acceptValidatedInput(ConversationContext cc, Player player) {
                            return END_OF_CONVERSATION;
                        }

                        @Override
                        public String getPromptText(ConversationContext cc) {
                            return "Gib Player:";
                        }
                    };
                }

                @Override
                public String getPromptText(ConversationContext cc) {
                    return "Gib was ein:";
                }
            });
        }
        if (aCommandSender instanceof Conversable) {
            factory.buildConversation((Conversable)aCommandSender).begin();
            return true;
        } else {
            return false;
        }
        */
        if (aStrings[0].equalsIgnoreCase("new")) {
            WorldCreator lC = new WorldCreator("world_flat");
            lC.seed(42);
            lC.generateStructures(false);
            lC.type(WorldType.FLAT);
            World lWorld = lC.createWorld();
            DynamicWorld.plugin.getServer().getWorlds().add(lWorld);
            lWorld.save();
            //DynamicWorld.plugin.getServer().createWorld(lC);
        } else if (aStrings[0].equalsIgnoreCase("tp")) {
            World lWorld = DynamicWorld.plugin.getServer().getWorld(aStrings[1]);
            Location lLoc = lWorld.getSpawnLocation();
            ((Player)aCommandSender).teleport(lLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
        } else if (aStrings[0].equalsIgnoreCase("x")) {
            World lWorld = ((Player)aCommandSender).getWorld();
            List<Block> lastTwoTargetBlocks = ((Player)aCommandSender).getLastTwoTargetBlocks(null, 200);
            Location location = lastTwoTargetBlocks.get(lastTwoTargetBlocks.size()-1).getLocation();
            lWorld.createExplosion(location, Float.parseFloat(aStrings[1]), true);
        } else if (aStrings[0].endsWith("inv")) {
            Inventory aInv = ((Player)aCommandSender).getInventory();
            YamlConfiguration lYaml = new YamlConfiguration();
            ItemStack[] lContents = aInv.getContents();
            ArrayList<Map> lItems = new ArrayList<Map>();
            for(ItemStack lItem : lContents) {
                if (lItem != null) {
                    Map<String, Object> lMap = lItem.serialize();
                    lItems.add(lMap);
                } else {
                    lItems.add(null);
                }
            }
            lYaml.set("Inventory", lItems);
            String lInvStr = lYaml.saveToString();
            Framework.plugin.getLogger().info(lInvStr);
            String l64 = Base64.encodeBytes(lInvStr.getBytes());
            Framework.plugin.getLogger().info(l64);
            lYaml = new YamlConfiguration();
            try {
                try {
                    lInvStr = new String(Base64.decode(l64));
                } catch (IOException ex) {
                    Logger.getLogger(CommandTestTask.class.getName()).log(Level.SEVERE, null, ex);
                }
                lYaml.loadFromString(lInvStr);
                Object lObj = lYaml.get("Inventory");
                if (lObj instanceof ArrayList) {
                    for(Object lItem : (ArrayList)lObj) {
                        if (lItem == null || lItem.toString().equals("null")) {
                            Framework.plugin.getLogger().info("<empty>");
                        } else {
                            ItemStack lIStack = ItemStack.deserialize((Map)lItem);
                            Framework.plugin.getLogger().info(lIStack.toString());
                        }
                    } 
                }
            } catch (InvalidConfigurationException ex) {
                Logger.getLogger(CommandTestTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (aStrings[0].equalsIgnoreCase("task")) {
            TestTask lTask = new TestTask();
            if (aCommandSender instanceof Player) {
              lTask.player = (Player) aCommandSender;
            }
            if (aStrings.length > 1) lTask.par1 = Integer.parseInt(aStrings[1]);
            if (aStrings.length > 2) lTask.par2 = Integer.parseInt(aStrings[2]);
            if (aStrings.length > 3) lTask.par3 = Integer.parseInt(aStrings[3]);
            lTask.taskId = DynamicWorld.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(DynamicWorld.plugin, lTask, 10, 10);
        } else if (aStrings[0].equalsIgnoreCase("navtest")) {
            Player lPlayer = (Player)aCommandSender;
            Location lLoc = lPlayer.getLocation().add(1, 0, 1);
            Entity lEntity = lPlayer.getWorld().spawnEntity(lLoc, EntityType.PIG);
            EntityControl lC = new EntityControl(lEntity);
            if (aStrings.length > 1) {
                if (aStrings[1].equalsIgnoreCase("player")) {
                    lC.path.add(new EntityControlPathItemTarget(lPlayer));
                    Framework.plugin.getEntityController().add(lC);
                } else if (aStrings[1].equalsIgnoreCase("follow")) {
                    EntityControlPathItemTarget lecpi = new EntityControlPathItemTarget(lastEntity);
                    lecpi.stayTicks = 1000;
                    lC.path.add(lecpi);
                    Framework.plugin.getEntityController().add(lC);
                }
            } else {
                lC.path.add(new EntityControlPathItemDestination(new BlockPosition(lLoc.add(20, 0, 20))));
                lC.path.add(new EntityControlPathItemDestination(new BlockPosition(lLoc.add(-10, 0, -10))));
                lC.path.add(new EntityControlPathItemDestination(new BlockPosition(lLoc.add(20, 0, 20))));
                lC.path.add(new EntityControlPathItemDestination(new BlockPosition(lLoc.add(-10, 0, -10))));
                Framework.plugin.getEntityController().add(lC);
            }
            lastEntity = lEntity;
        } else if (aStrings[0].equalsIgnoreCase("dance")) {
            Player lPlayer = (Player)aCommandSender;
            Location lLoc = lPlayer.getLocation().add(1, 0, 1);
            BlockPosition lPos = new BlockPosition(lLoc);
            for(int x=0;x<40;x+=4) {
                BlockPosition lP = lPos.clone();
                lP.add(x, 0, 0);
                Entity lEntity = lPlayer.getWorld().spawnEntity(lP.getLocation(lLoc.getWorld()), EntityType.WOLF);
                EntityControl lC = new EntityControl(lEntity);
                lC.path.add(new EntityControlPathItemRelative(new BlockPositionDelta( 10,  0,   0)));
                lC.path.add(new EntityControlPathItemRelative(new BlockPositionDelta(  0,  0,  10)));
                lC.path.add(new EntityControlPathItemRelative(new BlockPositionDelta(  0,  0, -10)));
                lC.path.add(new EntityControlPathItemRelative(new BlockPositionDelta( 10,  0,   0)));
                lC.path.add(new EntityControlPathItemRelative(new BlockPositionDelta( 10,  0,  10)));
                lC.path.add(new EntityControlPathItemRelative(new BlockPositionDelta(-10,  0, -10)));
                lC.path.add(new EntityControlPathItemRelative(new BlockPositionDelta(  5,  0,   5)));
                lC.path.add(new EntityControlPathItemRelative(new BlockPositionDelta( -5,  0,  -5)));
                Framework.plugin.getEntityController().add(lC);
            }
        } else if (aStrings[0].equalsIgnoreCase("spawn")) {
            Player lPlayer = (Player)aCommandSender;
            Location lLoc = lPlayer.getLocation().add(1, 0, 1);
            Entity lEntity = lPlayer.getWorld().spawnEntity(lLoc, EntityType.valueOf(aStrings[1]));
        }
        return true;
    }
    
    public static Entity lastEntity = null;
    
}
