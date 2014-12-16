/*******************************************************************************
 * Copyright (c) 2014 Yuuto.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 * 	   cpw - src reference from Iron Chests
 * 	   doku/Dokucraft staff - base chest texture
 *     Yuuto - initial API and implementation
 ******************************************************************************/
package yuuto.enhancedinventories.gui;

import yuuto.enhancedinventories.client.GuiContainerConnected;
import yuuto.enhancedinventories.client.GuiContainerConnectedLarge;
import yuuto.enhancedinventories.tile.TileConnectiveInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler{

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(!(tile instanceof TileConnectiveInventory))
			return null;
		TileConnectiveInventory inv = (TileConnectiveInventory)tile;
		System.out.println(inv.getType().toString());
		if(inv.getSizeInventory() <= 54){
			return new ContainerConnected(inv, player);
		}
		return new ContainerConnectedLarge(inv, player);
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(!(tile instanceof TileConnectiveInventory))
			return null;
		TileConnectiveInventory inv = (TileConnectiveInventory)tile;
		if(inv.getSizeInventory() <= 54){
			return new GuiContainerConnected(inv, player);
		}
		return new GuiContainerConnectedLarge(inv, player);
	}

}
