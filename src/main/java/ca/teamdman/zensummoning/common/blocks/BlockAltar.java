package ca.teamdman.zensummoning.common.blocks;

import ca.teamdman.zensummoning.ZenSummoning;
import ca.teamdman.zensummoning.common.summoning.SummoningAttempt;
import ca.teamdman.zensummoning.common.tiles.TileAltar;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class BlockAltar extends Block {
	protected static final VoxelShape    SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D);
	private final          AxisAlignedBB bb    = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.0625D, 0.9375D);

	public BlockAltar() {
		super(AbstractBlock.Properties.create(Material.PISTON)
									  .notSolid()
									  .doesNotBlockMovement()
									  .harvestTool(ToolType.PICKAXE)
									  .hardnessAndResistance(5,6)
									  .harvestLevel(ItemTier.STONE.getHarvestLevel()));
		setRegistryName(new ResourceLocation(ZenSummoning.MOD_ID, "altar"));
	}

	@Override
	public boolean hasTileEntity(BlockState ignored) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileAltar();
	}

	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
		return true;
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof TileAltar) {
			TileAltar altar = (TileAltar) tile;
			ItemStack stack;
			while (!(stack = altar.popStack()).isEmpty()) {
				InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
			}
			worldIn.updateComparatorOutputLevel(pos, this);
		}

		super.onBlockHarvested(worldIn, pos, state, player);
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (!world.isRemote && world.isBlockPowered(pos)) {
			TileEntity tile = world.getTileEntity(pos);
			if (!(tile instanceof TileAltar)) {
				return;
			}
			if (((TileAltar) tile).isSummoning())
				return;

			if (((TileAltar) tile).attemptWorldSummon()
								  .isPresent()) {
				world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.BLOCKS, 0.5f, 0.1f);
			} else {
				world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.05f, 1f);
			}
		}
		super.neighborChanged(state, world, pos, blockIn, fromPos, isMoving);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (world.isRemote)
			return ActionResultType.CONSUME;
		TileEntity tile = world.getTileEntity(pos);
		if (!(tile instanceof TileAltar))
			return ActionResultType.CONSUME; // wtf
		TileAltar altar = (TileAltar) tile;

		if (player.isSneaking()) {
			// attempt summoning

			ItemStack        catalyst = player.getHeldItem(hand);
			SummoningAttempt result   = altar.attemptSummon(catalyst);
			player.setHeldItem(hand, catalyst);
			player.sendMessage(new TranslationTextComponent(result.getMessage()), Util.DUMMY_UUID);
			if (result.isSuccess()) {
				world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.BLOCKS, 0.5f, 0.1f);
			} else {
				world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.05f, 1f);
			}
		} else {
			// item input and output
			if (player.getHeldItem(hand)
					  .isEmpty()) {
				player.setHeldItem(hand, altar.popStack());
				world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5f, -0.5f);
			} else {
				ItemStack handStack = player.getHeldItem(hand);
				ItemStack remaining = altar.pushStack(handStack);
				if (handStack.equals(remaining, false)) {
					player.sendMessage(new TranslationTextComponent("chat.zensummoning.invalid_ingredient"), Util.DUMMY_UUID);
				} else {
					player.setHeldItem(hand, remaining);
					world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5f, 2f);
				}
			}
		}
		return ActionResultType.CONSUME;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}
}
