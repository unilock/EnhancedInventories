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
package yuuto.enhancedinventories;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import yuuto.enhancedinventories.proxy.ProxyCommon;
import yuuto.enhancedinventories.tile.BlockImprovedChest;
import yuuto.yuutolib.IMod;

@Mod(modid = "EnhancedInventories", name = "Enhanced Inventories", version = "1.7.10-1.0.2")
public class EnhancedInventories implements IMod{

	@Instance("EnhancedInventories")
	public static EnhancedInventories instance;
	
	@SidedProxy(clientSide = "yuuto.enhancedinventories.proxy.ProxyClient", serverSide = "yuuto.enhancedinventories.proxy.ProxyCommon")
	public static ProxyCommon proxy;
	
	public static CreativeTabs tab = new CreativeTabs("EnhancedInventories"){

		@Override
		@SideOnly(Side.CLIENT)
		public Item getTabIconItem() {
			return Item.getItemFromBlock(improvedChest);
		}
		
	};
	
	public static final BlockImprovedChest improvedChest = new BlockImprovedChest();
	
	@Override
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}

	@Override
	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@Override
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

}
