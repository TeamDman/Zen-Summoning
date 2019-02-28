package ca.teamdman.zensummoning;

import crafttweaker.api.data.IData;
import crafttweaker.mc1120.data.NBTConverter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class SummoningInfo {
	public final ItemStack        catalyst;
	public final NBTTagCompound   data;
	public final int              height;
	public final ResourceLocation mob;
	public final List<ItemStack>  reagents;

	public SummoningInfo(ItemStack catalyst, List<ItemStack> reagents, ResourceLocation mob, int height, IData data) {
		this.catalyst = catalyst;
		this.reagents = reagents;
		this.mob = mob;
		this.height = height;
		this.data = (NBTTagCompound) NBTConverter.from(data);
	}

	public SummoningInfo(NBTTagCompound compound) {
		this.catalyst = ItemStack.EMPTY;
		this.reagents = new ArrayList<>();
		this.mob = new ResourceLocation(compound.getString("mod"), compound.getString("mob"));
		this.height = compound.getInteger("height");
		this.data = compound.getCompoundTag("data");
	}

	@Override
	public int hashCode() {
		return catalyst.getTranslationKey().hashCode() * 31 + mob.hashCode();
	}

	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setString("mod", mob.getNamespace());
		compound.setString("mob", mob.getPath());
		compound.setInteger("height", height);
		compound.setTag("data", data);
		return compound;
	}
}
