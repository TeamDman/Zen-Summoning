package ca.teamdman.zensummoning.common.tiles;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.List;

public class TileAltar extends TileEntity implements ITickable {
	private final ItemStackHandler inventory = new ItemStackHandler(16) {
		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			markDirty();
		}
	};
	public int renderTick = 0;
	private final AxisAlignedBB    succ      = new AxisAlignedBB(-2, -2, -2, 2, 2, 2).offset(this.pos);

	@Override
	public void update() {
		if (this.world != null && !this.world.isRemote && this.world.getTotalWorldTime() % 20 == 0) {
			List<EntityItem> drops = this.world.getEntitiesWithinAABB(EntityItem.class, succ, EntitySelectors.IS_ALIVE);
			drops.forEach(System.out::println);
		}
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

	public ImmutableList<ItemStack> getStacks() {
		ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
		ItemStack stack;
		for (int slot = 0; slot < inventory.getSlots(); slot++) {
			if (!(stack = inventory.getStackInSlot(slot)).isEmpty()) {
				builder.add(stack);
			}
		}
		return builder.build();
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		inventory.deserializeNBT(compound.getCompoundTag("inventory"));
		super.readFromNBT(compound);
	}

	@SuppressWarnings("NullableProblems")
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("inventory", inventory.serializeNBT());
		return super.writeToNBT(compound);
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
