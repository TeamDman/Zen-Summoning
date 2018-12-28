package ca.teamdman.zensummoning.common.blocks;

import ca.teamdman.zensummoning.ZenSummoning;
import ca.teamdman.zensummoning.common.tiles.TileAltar;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
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

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof TileAltar) {
			TileAltar altar = (TileAltar) tile;
			ItemStack stack;
			while (!(stack = altar.popStack()).isEmpty()) {
				InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
			}
			worldIn.updateComparatorOutputLevel(pos, this);
		}

		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			TileEntity tile = world.getTileEntity(pos);
			if (!(tile instanceof TileAltar))
				return false;
			TileAltar altar = (TileAltar) tile;
			if (!playerIn.isSneaking()) {
				if (playerIn.getHeldItem(hand).isEmpty()) {
					playerIn.setHeldItem(hand, altar.popStack());
				} else {
					playerIn.setHeldItem(hand, altar.pushStack(playerIn.getHeldItem(hand)));
				}
				world.notifyBlockUpdate(pos, state, state, 3);
				altar.markDirty();
			} else {

				playerIn.sendMessage(new TextComponentString("Yeeet"));
			}
		}
		return true;
	}
}
