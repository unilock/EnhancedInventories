package yuuto.enhancedinventories.compat;

import com.dynious.refinedrelocation.api.APIUtils;
import com.dynious.refinedrelocation.api.ModObjects;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import yuuto.enhancedinventories.EInventoryMaterial;
import yuuto.enhancedinventories.EnhancedInventories;
import yuuto.enhancedinventories.tile.BlockImprovedChest;
import yuuto.enhancedinventories.tile.TileImprovedChest;

public class BlockImprovedSortingChest extends BlockImprovedChest{
	
	public BlockImprovedSortingChest() {
		super("improvedSortingChest");
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileImprovedSortingChest(EInventoryMaterial.values()[meta]);
	}
	
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i1, float f1, float f2, float f3)
    {
        if (world.isRemote)
        {
            return true;
        }

        if (player.isSneaking())
        {
        	APIUtils.openFilteringGUI(player, world, x, y, z);
            return true;
        }

        return super.onBlockActivated(world, x, y, z, player, i1, f1, f2, f3);
    }
    

}