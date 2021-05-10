package ca.teamdman.zensummoning.common.tiles;

import ca.teamdman.zensummoning.common.Registrar;
import ca.teamdman.zensummoning.common.summoning.MobInfo;
import ca.teamdman.zensummoning.common.summoning.SummoningAttempt;
import ca.teamdman.zensummoning.common.summoning.SummoningDirector;
import ca.teamdman.zensummoning.common.summoning.SummoningInfo;
import ca.teamdman.zensummoning.util.WeightedRandomBag;
import com.blamejared.crafttweaker.api.item.IIngredientWithAmount;
import com.blamejared.crafttweaker.impl.item.MCItemStackMutable;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Optional;

public class TileAltar extends TileEntity implements ITickableTileEntity {
	public final  int              TIME_TO_SPAWN   = 5 * 20;
	private final ItemStackHandler clientInventory = new ItemStackHandler();
	private final ItemStackHandler inventory       = new ItemStackHandler(SummoningDirector.getStackLimit()) {
		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
			markDirty();
		}
	};
	public        int              renderTick      = -1;
	private       int              summonCountdown = -1;
	private       SummoningInfo    summonInfo;

	public TileAltar() {
		//noinspection ConstantConditions
		super(Registrar.ALTAR_TILE.get());
	}

	/**
	 * Attempt to perform a summoning, detecting the catalyst as a dropped item in the world.
	 * Only a successful attempt returns a non-empty value.
	 */
	public Optional<SummoningAttempt> attemptWorldSummon() {
		final AxisAlignedBB itemRange = new AxisAlignedBB(-1, -1, -1, 1, 1, 1).offset(pos);
		for (ItemEntity ent : world.getEntitiesWithinAABB(ItemEntity.class, itemRange)) {
			SummoningAttempt attempt = attemptSummon(ent.getItem());
			if (attempt.isSuccess())
				return Optional.of(attempt);
		}
		return Optional.empty();
	}

	/**
	 * Attempts to perform a summon.
	 * Should be called server-only.
	 * Likely triggered by a redstone pulse.
	 */
	public SummoningAttempt attemptSummon(ItemStack catalyst) {
		SummoningAttempt attempt = new SummoningAttempt(this.world, this.pos);
		if (isSummoning()) {
			attempt.setSuccess(false);
			attempt.setMessage("chat.zensummoning.busy");
			return attempt;
		}

		Optional<SummoningInfo> infoMatch = getSummonInfo(catalyst);
		if (!infoMatch.isPresent()) {
			attempt.setSuccess(false);
			attempt.setMessage(getAssumedErrorMessage(catalyst));
			return attempt;
		}
		SummoningInfo info = infoMatch.get();

		Optional<HashMap<Integer, Integer>> slotsMatch = getIngredientsToConsume(info);
		if (!slotsMatch.isPresent()) {
			attempt.setSuccess(false);
			attempt.setMessage("chat.zensummoning.unknown_error");
			return attempt;
		}

		info.getMutator()
				.accept(attempt);
		if (!attempt.isSuccess())
			return attempt;

		beginSummoning(info);
		slotsMatch.get()
				.forEach((slot, count) -> inventory.extractItem(slot, count, false));
		if (info.isCatalystConsumed())
			catalyst.shrink(info.getCatalyst()
									.getAmount());
		return attempt;
	}

	/**
	 * Determines whether or not the altar is in the middle of spawning an entity.
	 *
	 * @return isSpawning
	 */
	public boolean isSummoning() {
		return summonInfo != null && summonCountdown >= 0;
	}

	/**
	 * Attempts to pick a summoning info for the given catalyst.
	 */
	private Optional<SummoningInfo> getSummonInfo(ItemStack catalyst) {
		WeightedRandomBag<SummoningInfo> bag = new WeightedRandomBag<>();
		SummoningDirector.getSummonInfos()
				.stream()
				.filter(x -> meetsCriteria(x, catalyst))
				.forEach(x -> bag.addEntry(x, x.getWeight()));

		if (bag.isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(bag.getRandom());
		}
	}

	/**
	 * Checks if a given info meets all criteria for summoning.
	 *
	 * @param info      info to check
	 * @param handStack player's held stack
	 * @return info's constraints satisfied
	 */
	private boolean meetsCriteria(SummoningInfo info, ItemStack handStack) {
		if (!info.getCatalyst()
				.getIngredient()
				.matches(new MCItemStackMutable(handStack)))
			return false;
		if (info.getCatalyst()
				.getAmount() > handStack.getCount())
			return false;
		return getIngredientsToConsume(info).isPresent();
	}

	/**
	 * Fills a map of slot:quantity for items to be consumed by the summoning.
	 * If optional is empty, some items were not found and the summon should be aborted.
	 *
	 * @param info info containing ingredients to look for
	 * @return A map of slots and amounts to consume.
	 */
	private Optional<HashMap<Integer, Integer>> getIngredientsToConsume(SummoningInfo info) {
		HashMap<Integer, Integer> slotUsage = new HashMap<>();
		for (IIngredientWithAmount reagent : info.getReagents()) {
			int remaining = reagent.getAmount();
			for (int slot = 0; slot < inventory.getSlots() && remaining > 0; slot++) {
				ItemStack slotStack = inventory.getStackInSlot(slot);
				// Make sure we don't take from the same slot twice without noticing
				int available = slotStack.getCount() - slotUsage.getOrDefault(slot, 0);
				if (reagent.getIngredient()
						.matches(new MCItemStackMutable(slotStack)) && available > 0) {
					slotUsage.merge(slot, Math.min(remaining, available), Integer::sum);
					remaining -= available;
				}
			}
			if (remaining > 0) {
				return Optional.empty();
			}
		}
		return Optional.of(slotUsage);
	}

	/**
	 * Assuming that there's no valid summoning, get the most likely error message.
	 *
	 * @param handStack player's held stack
	 * @return unlocalized error message
	 */
	private String getAssumedErrorMessage(ItemStack handStack) {
		String msg  = "chat.zensummoning.no_match";
		int    mask = 0;
		for (SummoningInfo info : SummoningDirector.getSummonInfos()) {
			if (info.getCatalyst()
					.getIngredient()
					.matches(new MCItemStackMutable(handStack))) {
				mask |= 0x01; // we found a matching catalyst, unknown if hand quantity satisfies
				if (info.getCatalyst()
						.getAmount() < handStack.getCount()) {
					mask |= 0x10; // we found a matching catalyst, knowing hand quantity satisfies
				}
			}
		}
		if (mask == 0x00) // no matching catalyst
			return "chat.zensummoning.no_match";
		else if (mask == 0x01) // no catalyst that hand quantity satisfies
			return "chat.zensummoning.unsatisfied_hand";
		else // assuming that recipe contents are unsatisfied, since the hand quantity satisfies
			return "chat.zensummoning.unsatisfied";
	}

	private void beginSummoning(SummoningInfo info) {
		this.summonInfo = info;
		this.summonCountdown = TIME_TO_SPAWN;
		this.renderTick = 0;

		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
		world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_EVOKER_CAST_SPELL, SoundCategory.BLOCKS, 0.5f, 1f);
	}

	public boolean validIngredient(ItemStack item) {
		MCItemStackMutable stack = new MCItemStackMutable(item);
		return SummoningDirector.getSummonInfos()
				.stream()
				.flatMap(info -> info.getReagents()
						.stream())
				.map(IIngredientWithAmount::getIngredient)
				.anyMatch(r -> r.matches(stack));
	}

	/**
	 * Adds a stack to the buffer, this does not modify the given stack.
	 *
	 * @param stack Remaining itemstack after insertion.
	 * @return stack after insertion
	 */
	public ItemStack pushStack(ItemStack stack) {
		ItemStack rtn = stack;
		for (int slot = 0; slot < inventory.getSlots() && !rtn.isEmpty(); slot++) {
			rtn = inventory.insertItem(slot, rtn, false);
		}
		return rtn;
	}

	/**
	 * Retrieves a stack from the buffer
	 *
	 * @return stack from the buffer that was most recently added
	 */
	public ItemStack popStack() {
		ItemStack rtn;
		for (int slot = inventory.getSlots() - 1; slot >= 0; slot--) {
			if (!(rtn = inventory.getStackInSlot(slot)).isEmpty()) {
				return inventory.extractItem(slot, rtn.getCount(), false);
			}
		}
		return ItemStack.EMPTY;
	}

	/**
	 * Returns the inventory as an immutable array list, this inventory is synced manually.
	 *
	 * @return The inventory of the block on the client side.
	 */
	@OnlyIn(Dist.CLIENT)
	public ImmutableList<ItemStack> getClientStacks() {
		return getStacksFromInventory(clientInventory);
	}

	/**
	 * Composes an ${@link ImmutableList} containing the given inventory contents.
	 *
	 * @param handler Inventory
	 * @return List of slot contents.
	 */
	private ImmutableList<ItemStack> getStacksFromInventory(ItemStackHandler handler) {
		ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
		ItemStack                        stack;
		for (int slot = 0; slot < handler.getSlots(); slot++) {
			if (!(stack = handler.getStackInSlot(slot)).isEmpty()) {
				builder.add(stack);
			}
		}
		return builder.build();
	}


	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? LazyOptional.of(() -> (T) inventory) : super.getCapability(cap, side);
	}

	@Override
	public void tick() {
		if (!isSummoning()) {
			if (renderTick > -1)
				renderTick--;
			return;
		}

		summonCountdown--;
		renderTick++;

		if (summonCountdown > 0)
			return;
		summonFinish();
	}

	/**
	 * Spawns the current {@link SummoningInfo} into the world
	 */
	private void summonFinish() {
		summonCountdown = -1;
		if (world.isRemote) {
			summonInfo = null;
			return;
		}

		for (MobInfo mobInfo : summonInfo.getMobs()) {
			for (int i = 0; i < mobInfo.getCount(); i++) {
				Entity mob = mobInfo.getEntityType()
						.create(world);
				if (mob == null) {
					return;
				}
				mob.deserializeNBT(mobInfo.getData());
				mob.setPosition(getPos().getX() + mobInfo.getOffset()
										.getX() + world.rand.nextInt(Math.abs(mobInfo.getSpread()
																					  .getX() * 2) + 1) - Math.abs(mobInfo.getSpread()
																														   .getX()),
								getPos().getY() + mobInfo.getOffset()
										.getY() + world.rand.nextInt(Math.abs(mobInfo.getSpread()
																					  .getY() * 2) + 1) - Math.abs(mobInfo.getSpread()
																														   .getY()),
								getPos().getZ() + mobInfo.getOffset()
										.getZ() + world.rand.nextInt(Math.abs(mobInfo.getSpread()
																					  .getZ() * 2) + 1) - Math.abs(mobInfo.getSpread()
																														   .getZ()));
				System.out.printf("%s\n", mob.getPosition());
				world.addEntity(mob);
			}
		}

		summonInfo = null;

		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
		world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO, SoundCategory.BLOCKS, 0.5f, 1f);

	}

	@Nullable
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, 255, serializeNBT());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		deserializeNBT(pkt.getNbtCompound());
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		inventory.deserializeNBT(nbt.getCompound("inventory"));
		renderTick = nbt.getInt("renderTick");
		summonCountdown = nbt.getInt("summonCountdown");
		if (nbt.contains("summonInfo", Constants.NBT.TAG_COMPOUND))
			summonInfo = SummoningInfo.fromNBT(nbt.getCompound("summonInfo"));
		else if (!isSummoning()) // keep inventory desync'd so render can animate the reagents
			clientInventory.deserializeNBT(nbt.getCompound("inventory"));

		super.read(state, nbt);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.put("inventory", inventory.serializeNBT());
		compound.putInt("renderTick", renderTick);
		compound.putInt("summonCountdown", summonCountdown);
		if (isSummoning())
			compound.put("summonInfo", summonInfo.serializeNBT());
		return super.write(compound);
	}
}
