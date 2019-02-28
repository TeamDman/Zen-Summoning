package ca.teamdman.zensummoning.common.blocks;

import ca.teamdman.zensummoning.SummoningAttempt;
import ca.teamdman.zensummoning.ZenSummoning;
import ca.teamdman.zensummoning.common.tiles.TileAltar;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockAltar extends Block implements ITileEntityProvider {
	private final AxisAlignedBB bb = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.0625D, 0.9375D);

	public BlockAltar() {
		super(Material.ROCK);
		setRegistryName(new ResourceLocation(ZenSummoning.MOD_ID, "altar"));
		setTranslationKey("altar");
		setCreativeTab(ZenSummoning.CREATIVE_TAB);
		setHardness(3);
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileAltar();
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return bb;
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
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
		ZenSummoning.log("Altar onBlockActivated");
		if (!world.isRemote) {
			TileEntity tile = world.getTileEntity(pos);
			if (!(tile instanceof TileAltar)) {
				ZenSummoning.log("Altar onBlockActivated tile not altar?");
				return false;
			}
			TileAltar altar = (TileAltar) tile;
			if (!playerIn.isSneaking()) {
				ZenSummoning.log("Altar onBlockActivated player not sneaking");
				if (playerIn.getHeldItem(hand).isEmpty()) {
					playerIn.setHeldItem(hand, altar.popStack());
					world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5f, -0.5f);
				} else {
					playerIn.setHeldItem(hand, altar.pushStack(playerIn.getHeldItem(hand)));
					world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5f, 2f);
				}
			} else {
				ZenSummoning.log("Altar onBlockActivated player is sneaking");
				SummoningAttempt result = altar.summonStart(playerIn, hand);
				ZenSummoning.log("Altar onBlockActivated summon " + result.getMessage());
				playerIn.sendMessage(new TextComponentTranslation(result.getMessage()));
				if (result.isSuccess()) {
					world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_NOTE_FLUTE, SoundCategory.BLOCKS, 1f, 0.1f);
				} else {
					world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 1f, 1f);
				}

			}
		}
		return true;
	}

}
