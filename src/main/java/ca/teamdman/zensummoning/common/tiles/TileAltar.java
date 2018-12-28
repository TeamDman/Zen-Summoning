package ca.teamdman.zensummoning.common.tiles;

import ca.teamdman.zensummoning.SummoningDirector;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import crafttweaker.mc1120.data.NBTConverter;
import javafx.util.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class TileAltar extends TileEntity {
	public final  ItemStackHandler clientInventory = new ItemStackHandler();
	private final ItemStackHandler inventory       = new ItemStackHandler(256) {
		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
			markDirty();
		}
	};

	/**
	 * Attempts to perform a summon, given a catalyst.
	 *
	 * @return True if something was summoned.
	 */
	public boolean summon(EntityPlayer player, EnumHand hand) {
		ItemStack handStack = player.getHeldItem(hand);
		SummoningDirector.SummonInfo info = SummoningDirector.getSummonInfo(handStack);
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

		reagentMap.values().forEach(x -> inventory.extractItem(x.getKey(), x.getValue(), false));
		handStack.shrink(info.catalyst.getCount());
		player.setHeldItem(hand, handStack);

		Entity mob = EntityList.createEntityByIDFromName(info.mob, world);
		if (mob == null)
			return false;

		mob.readFromNBT((NBTTagCompound) NBTConverter.from(info.data));
		mob.setPosition(pos.getX() + 0.5, pos.getY() + 1 + info.height, pos.getZ() + 0.5);
		world.spawnEntity(mob);
		return true;
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

	/**
	 * Gets the inventory as an array list.
	 *
	 * @return ArrayList containing the inventory.
	 */
	public ImmutableList<ItemStack> getStacks() {
		return getStacksFromInventory(inventory);
	}


	@Override
	public void readFromNBT(NBTTagCompound compound) {
		inventory.deserializeNBT(compound.getCompoundTag("inventory"));
		clientInventory.deserializeNBT(compound.getCompoundTag("inventory"));
		super.readFromNBT(compound);
	}

	@SuppressWarnings("NullableProblems")
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("inventory", inventory.serializeNBT());
		return super.writeToNBT(compound);
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound comp = new NBTTagCompound();
		writeToNBT(comp);
		return new SPacketUpdateTileEntity(this.pos, 255, comp);
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
