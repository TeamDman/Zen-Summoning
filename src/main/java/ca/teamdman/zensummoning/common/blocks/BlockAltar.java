package ca.teamdman.zensummoning.common.blocks;

import ca.teamdman.zensummoning.ZenSummoning;
import ca.teamdman.zensummoning.common.summoning.SummoningAttempt;
import ca.teamdman.zensummoning.common.tiles.TileAltar;
import net.minecraft.block.Block;
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

public class BlockAltar extends Block {
	private final AxisAlignedBB bb = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.0625D, 0.9375D);

	public BlockAltar() {
		super(Material.ROCK);
		setRegistryName(new ResourceLocation(ZenSummoning.MOD_ID, "altar"));
		setTranslationKey("zensummoning.altar");
		setCreativeTab(ZenSummoning.CREATIVE_TAB);
		setHardness(3);
	}

	@Override
	public boolean hasTileEntity(IBlockState ignored) {
		return true;
	}

	@SuppressWarnings("NullableProblems")
	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileAltar();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return bb;
	}

	@SuppressWarnings("deprecation")
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
				ItemStack catalyst = playerIn.getHeldItem(hand);
				SummoningAttempt result = altar.attemptSummon(catalyst);
				playerIn.setHeldItem(hand, catalyst);
				ZenSummoning.log("Altar onBlockActivated summon " + result.getMessage());
				playerIn.sendMessage(new TextComponentTranslation(result.getMessage()));
				if (result.isSuccess()) {
					world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_NOTE_FLUTE, SoundCategory.BLOCKS, 0.5f, 0.1f);
				} else {
					world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.05f, 1f);
				}

			}
		}
		return true;
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
		return true;
	}


	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (!world.isRemote && world.isBlockPowered(pos)) {
			TileEntity tile = world.getTileEntity(pos);
			if (!(tile instanceof TileAltar)) {
				ZenSummoning.log("Altar onBlockActivated tile not altar?");
				return;
			}
			if (((TileAltar) tile).isSummoning())
				return;

			if (((TileAltar) tile).attemptWorldSummon().isPresent()) {
				world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_NOTE_FLUTE, SoundCategory.BLOCKS, 0.5f, 0.1f);
			} else {
				world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.05f, 1f);
			}
		}
	}
}
