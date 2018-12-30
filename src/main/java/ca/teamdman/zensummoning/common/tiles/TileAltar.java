package ca.teamdman.zensummoning.common.tiles;

import ca.teamdman.zensummoning.SummoningDirector;
import ca.teamdman.zensummoning.ZenSummoning;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import javafx.util.Pair;
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

public class TileAltar extends TileEntity implements ITickable {
	public final  ItemStackHandler             clientInventory = new ItemStackHandler();
	public  final int                          TIME_TO_SPAWN   = 5 * 20;
	private final ItemStackHandler             inventory       = new ItemStackHandler(256) {
		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
			markDirty();
		}
	};
	public        int                          renderTick      = -1;
	public        int                          summonCountdown = -1;
	private       SummoningDirector.SummonInfo summonInfo;

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
	 * Attempts to perform a summon, given a catalyst.
	 * Called server-only from {@link ca.teamdman.zensummoning.common.blocks.BlockAltar#onBlockActivated(World, BlockPos, IBlockState, EntityPlayer, EnumHand, EnumFacing, float, float, float)}
	 *
	 * @return True if something was summoned.
	 */
	public boolean summonStart(EntityPlayer player, EnumHand hand) {
		ZenSummoning.log("summonStart");
		ItemStack                    handStack = player.getHeldItem(hand);
		SummoningDirector.SummonInfo info      = SummoningDirector.getSummonInfo(handStack);
		if (info == null)
			return false;

		Multimap<ItemStack, Pair<Integer, Integer>> reagentMap = ArrayListMultimap.create();
		for (ItemStack reagentStack : info.reagents) {
			int remaining = reagentStack.getCount();
			for (int slot = 0; slot < inventory.getSlots() && remaining > 0; slot++) {
				ItemStack slotStack = inventory.getStackInSlot(slot);
				if (reagentStack.isItemEqual(slotStack)) {
					reagentMap.put(reagentStack, new Pair<>(slot, Math.min(remaining, slotStack.getCount())));
					remaining -= slotStack.getCount();
				}
			}
			if (remaining > 0)
				return false;
		}

		summonInfo = info;
		summonCountdown = TIME_TO_SPAWN;
		renderTick = 0;

		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
		world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ILLAGER_CAST_SPELL, SoundCategory.BLOCKS, 0.5f, 1f);


		reagentMap.values().forEach(x -> inventory.extractItem(x.getKey(), x.getValue(), false));
		handStack.shrink(info.catalyst.getCount());
		player.setHeldItem(hand, handStack);

		return true;
	}

	/**
	 * Spawns the current {@link ca.teamdman.zensummoning.SummoningDirector.SummonInfo} into the world
	 */
	private void summonFinish() {
		ZenSummoning.log("summonFinish");
		summonCountdown = -1;
		if (world.isRemote) {
			summonInfo = null;
			return;
		}

		Entity mob = EntityList.createEntityByIDFromName(summonInfo.mob, world);
		if (mob == null) {
			return;
		}

		mob.readFromNBT(summonInfo.data);
		mob.setPosition(pos.getX() + 0.5, pos.getY() + 1 + summonInfo.height, pos.getZ() + 0.5);
		world.spawnEntity(mob);
		summonInfo = null;

		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
		world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.EVOCATION_ILLAGER_PREPARE_WOLOLO, SoundCategory.BLOCKS, 0.5f, 1f);

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
			summonInfo = new SummoningDirector.SummonInfo(compound.getCompoundTag("summonInfo"));
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
