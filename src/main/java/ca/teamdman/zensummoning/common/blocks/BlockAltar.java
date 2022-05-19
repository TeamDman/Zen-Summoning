package ca.teamdman.zensummoning.common.blocks;

import ca.teamdman.zensummoning.common.Registrar;
import ca.teamdman.zensummoning.common.summoning.SummoningAttempt;
import ca.teamdman.zensummoning.common.tiles.TileAltar;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class BlockAltar extends BaseEntityBlock implements EntityBlock {
	protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D);
	private final          AABB       bb    = new AABB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.0625D, 0.9375D);

	public BlockAltar() {
		super(BlockBehaviour.Properties.of(Material.PISTON)
					  .noOcclusion()
					  .noCollission()
					  .strength(5, 6));
		//					  .harvestTool(ToolType.PICKAXE)
		//					  .harvestLevel(ItemTier.STONE.getHarvestLevel()));
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @org.jetbrains.annotations.Nullable Direction direction) {
		return true;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
		if (level.isClientSide) return;
		if (!(entity instanceof ServerPlayer)) return;
		BlockEntity tileEntity = level.getBlockEntity(pos);
		if (!(tileEntity instanceof TileAltar)) return;
		((TileAltar) tileEntity).addToKnownPlayers(((ServerPlayer) entity));
	}

	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		BlockEntity tile = level.getBlockEntity(pos);
		if (tile instanceof TileAltar) {
			TileAltar altar = (TileAltar) tile;
			ItemStack stack;
			while (!(stack = altar.popStack()).isEmpty()) {
				Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
			}
			level.updateNeighbourForOutputSignal(pos, this);
		}

		super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (!world.isClientSide && world.hasNeighborSignal(pos)) {
			BlockEntity tile = world.getBlockEntity(pos);
			if (!(tile instanceof TileAltar)) {
				return;
			}
			if (((TileAltar) tile).isSummoning()) return;

			if (((TileAltar) tile).attemptWorldSummon()
					.isPresent()) {
				world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.NOTE_BLOCK_FLUTE, SoundSource.BLOCKS, 0.5f, 0.1f);
			} else {
				world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.05f, 1f);
			}
		}
		super.neighborChanged(state, world, pos, blockIn, fromPos, isMoving);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.isClientSide) return InteractionResult.CONSUME;
		BlockEntity tile = level.getBlockEntity(pos);
		if (!(tile instanceof TileAltar)) return InteractionResult.CONSUME; // how did we get here
		TileAltar altar = (TileAltar) tile;

		if (player.isShiftKeyDown()) {
			// attempt summoning

			ItemStack        catalyst = player.getItemInHand(hand);
			SummoningAttempt result   = altar.attemptSummon(catalyst, ((ServerPlayer) player));
			player.setItemInHand(hand, catalyst);
			player.sendMessage(new TranslatableComponent(result.getMessage()), Util.NIL_UUID);
			if (result.isSuccess()) {
				level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.NOTE_BLOCK_FLUTE, SoundSource.BLOCKS, 0.5f, 0.1f);
			} else {
				level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.05f, 1f);
			}
		} else {
			// item input and output
			if (player.getItemInHand(hand)
					.isEmpty()) {
				player.setItemInHand(hand, altar.popStack());
				level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5f, -0.5f);
			} else {
				ItemStack handStack = player.getItemInHand(hand);
				if (!altar.validIngredient(handStack)) {
					player.sendMessage(new TranslatableComponent("chat.zensummoning.invalid_ingredient"), Util.NIL_UUID);
					return InteractionResult.CONSUME;
				}
				ItemStack remaining = altar.pushStack(handStack);
				if (handStack.equals(remaining, false)) {
					player.sendMessage(new TranslatableComponent("chat.zensummoning.full"), Util.NIL_UUID);
				} else {
					player.setItemInHand(hand, remaining);
					level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5f, 2f);
				}
			}
		}
		return InteractionResult.CONSUME;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}



	@org.jetbrains.annotations.Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileAltar(pos, state);
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (level.isClientSide) return null;
		return createTickerHelper(type, Registrar.ALTAR_TILE.get(), TileAltar::onServerTick);
	}
}
