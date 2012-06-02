/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.dynamicworld;

import com.mahn42.framework.DBSet;
import java.io.File;
import org.bukkit.World;

/**
 *
 * @author andre
 */
public class BuildingDB extends DBSet {
    protected World fWorld;
    
    public BuildingDB(World aWorld, File aFile) {
        super(Building.class, aFile);
        fWorld = aWorld;
    }
}
