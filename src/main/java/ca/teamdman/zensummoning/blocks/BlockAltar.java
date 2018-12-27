package ca.teamdman.zensummoning.blocks;

import ca.teamdman.zensummoning.ZenSummoning;
import ca.teamdman.zensummoning.tiles.TileAltar;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockAltar extends Block implements ITileEntityProvider {
	public BlockAltar() {
		super(Material.ROCK);
		setRegistryName(new ResourceLocation(ZenSummoning.MOD_NAME, "altar"));
		setTranslationKey("altar");
		setCreativeTab(ZenSummoning.CREATIVE_TAB);
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileAltar();
	}
}
