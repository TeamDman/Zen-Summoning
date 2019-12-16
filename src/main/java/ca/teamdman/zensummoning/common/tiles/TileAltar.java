package ca.teamdman.zensummoning.common.tiles;

import ca.teamdman.zensummoning.ZenSummoning;
import ca.teamdman.zensummoning.common.summoning.MobInfo;
import ca.teamdman.zensummoning.common.summoning.SummoningAttempt;
import ca.teamdman.zensummoning.common.summoning.SummoningDirector;
import ca.teamdman.zensummoning.common.summoning.SummoningInfo;
import ca.teamdman.zensummoning.util.WeightedRandomBag;
import com.google.common.collect.ImmutableList;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.HashMap;

public class TileAltar extends TileEntity implements ITickable {
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

	@Override
	public void update() {
		if (!isSpawning()) {
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
	 * Determines whether or not the altar is in the middle of spawning an entity.
	 *
	 * @return isSpawning
	 */
	public boolean isSpawning() {
		return summonInfo != null && summonCountdown >= 0;
	}

	/**
	 * Spawns the current {@link SummoningInfo} into the world
	 */
	private void summonFinish() {
		ZenSummoning.log("summonFinish");
		summonCountdown = -1;
		if (world.isRemote) {
			summonInfo = null;
			return;
		}

		for (MobInfo mobInfo : summonInfo.getMobs()) {
			for (int i = 0; i < mobInfo.getCount(); i++) {
				Entity mob = EntityList.createEntityByIDFromName(mobInfo.getMob(), world);
				if (mob == null) {
					return;
				}
				mob.readFromNBT(mobInfo.getData());
				mob.setPosition(
						getPos().getX() + mobInfo.getOffset().getX() + world.rand.nextInt(Math.abs(mobInfo.getSpread().getX() * 2)+1) - Math.abs(mobInfo.getSpread().getX()),
						getPos().getY() + mobInfo.getOffset().getY() + world.rand.nextInt(Math.abs(mobInfo.getSpread().getY() * 2)+1) - Math.abs(mobInfo.getSpread().getY()),
						getPos().getZ() + mobInfo.getOffset().getZ() + world.rand.nextInt(Math.abs(mobInfo.getSpread().getZ() * 2)+1) - Math.abs(mobInfo.getSpread().getZ())
				);
				world.spawnEntity(mob);
			}
		}

		summonInfo = null;

		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
		world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.EVOCATION_ILLAGER_PREPARE_WOLOLO, SoundCategory.BLOCKS, 0.5f, 1f);

	}

	/**
	 * Attempts to perform a summon, given a catalyst.
	 * Called server-only from {@link ca.teamdman.zensummoning.common.blocks.BlockAltar#onBlockActivated(World, BlockPos, IBlockState, EntityPlayer, EnumHand, EnumFacing, float, float, float)}
	 *
	 * @return True if something was summoned.
	 */
	public SummoningAttempt attemptSummon(EntityPlayer player, EnumHand hand) {
		ZenSummoning.log("summonStart");
		SummoningAttempt attempt = new SummoningAttempt(CraftTweakerMC.getIWorld(this.world), CraftTweakerMC.getIBlockPos(this.pos));
		if (isSpawning()) {
			attempt.setSuccess(false);
			attempt.setMessage("chat.zensummoning.busy");
			return attempt;
		}

		ItemStack     handStack = player.getHeldItem(hand);
		WeightedRandomBag<SummoningInfo> bag = new WeightedRandomBag<>();
		SummoningDirector.getSummonInfos().stream()
				.filter(x -> meetsCriteria(x, handStack))
				.forEach(x -> bag.addEntry(x, x.getWeight()));

		if (bag.isEmpty()) {
			attempt.setSuccess(false);
			attempt.setMessage(getAssumedErrorMessage(handStack));
			return attempt;
		}

		SummoningInfo             info = bag.getRandom();
		HashMap<Integer, Integer> slotAmounts = new HashMap<>();
		if (!attemptPopulateSlotsToConsume(info, slotAmounts)) {
			attempt.setSuccess(false);
			attempt.setMessage("chat.zensummoning.unknown_error");
			return attempt;
		}

		info.getMutator().accept(attempt);
		if (!attempt.isSuccess()) {
			return attempt;
		} else {
			beginSummoning(info);
			slotAmounts.forEach((slot, count) -> inventory.extractItem(slot, count, false));
			if (info.isCatalystConsumed())
				handStack.shrink(info.getCatalyst().getAmount());
			player.setHeldItem(hand, handStack);
			return attempt;
		}
	}

	/**
	 * Fills a map of slot:quantity for items to be consumed by the summoning
	 * @param info info containing ingredients to look for
	 * @param rtn map to be mutated
	 * @return info's constraints satisfied
	 */
	private boolean attemptPopulateSlotsToConsume(SummoningInfo info, HashMap<Integer, Integer> rtn) {
		rtn.clear();
		for (IIngredient reagent : info.getReagents()) {
			int remaining = reagent.getAmount();
			for (int slot = 0; slot < inventory.getSlots() && remaining > 0; slot++) {
				ItemStack slotStack = inventory.getStackInSlot(slot);
				// Make sure we don't take from the same slot twice without noticing
				int       available     = slotStack.getCount() - rtn.getOrDefault(slot, 0);
				if (reagent.matches(CraftTweakerMC.getIItemStack(slotStack)) && available > 0) {
					rtn.merge(slot, Math.min(remaining, available), Integer::sum);
					remaining -= available;
				}
			}
			if (remaining > 0) {
				rtn.clear();
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if a given info meets all criteria for summoning.
	 * @param info info to check
	 * @param handStack player's held stack
	 * @return info's constraints satisfied
	 */
	private boolean meetsCriteria(SummoningInfo info, ItemStack handStack) {
		if (!info.getCatalyst().matches(CraftTweakerMC.getIItemStack(handStack)))
			return false;
		if (info.getCatalyst().getAmount() > handStack.getCount())
			return false;
		return attemptPopulateSlotsToConsume(info, new HashMap<>());
	}

	/**
	 * Assuming that there's no valid summoning, get the most likely error message.
	 * @param handStack player's held stack
	 * @return unlocalized error message
	 */
	private String getAssumedErrorMessage(ItemStack handStack) {
		String msg = "chat.zensummoning.no_match";
		int mask = 0;
		for (SummoningInfo info : SummoningDirector.getSummonInfos()) {
			if (info.getCatalyst().amount(1).matches(CraftTweakerMC.getIItemStack(handStack))) {
				mask |= 0x01; // we found a matching catalyst, unknown if hand quantity satisfies
				if (info.getCatalyst().getAmount() < handStack.getCount()) {
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
		world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ILLAGER_CAST_SPELL, SoundCategory.BLOCKS, 0.5f, 1f);
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
	@SideOnly(Side.CLIENT)
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


	@Override
	public void readFromNBT(NBTTagCompound compound) {
		inventory.deserializeNBT(compound.getCompoundTag("inventory"));
		renderTick = compound.getInteger("renderTick");
		summonCountdown = compound.getInteger("summonCountdown");
		if (compound.hasKey("summonInfo"))
			summonInfo = SummoningInfo.fromNBT(compound.getCompoundTag("summonInfo"));
		else if (!isSpawning()) // keep inventory desync'd so render can animate the reagents
			clientInventory.deserializeNBT(compound.getCompoundTag("inventory"));

		super.readFromNBT(compound);
	}

	@SuppressWarnings("NullableProblems")
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("inventory", inventory.serializeNBT());
		compound.setInteger("renderTick", renderTick);
		compound.setInteger("summonCountdown", summonCountdown);
		if (isSpawning())
			compound.setTag("summonInfo", summonInfo.serializeNBT());
		return super.writeToNBT(compound);
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound comp = new NBTTagCompound();
		writeToNBT(comp);
		return new SPacketUpdateTileEntity(pos, 255, comp);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	@Override
	public <T> T getCapability(@Nullable Capability<T> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T) inventory : super.getCapability(capability, facing);
	}
}
