package ca.teamdman.zensummoning.common.tiles;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
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
	public boolean summon(ItemStack stack) {
		Entity mob = new EntityCow(world);
		mob.setPosition(pos.getX(), pos.getY() + 2, pos.getZ());
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

	@SideOnly(Side.CLIENT)
	public ImmutableList<ItemStack> getClientStacks() {
		ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
		ItemStack                        stack;
		for (int slot = 0; slot < clientInventory.getSlots(); slot++) {
			if (!(stack = clientInventory.getStackInSlot(slot)).isEmpty()) {
				builder.add(stack);
			}
		}
		return builder.build();
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