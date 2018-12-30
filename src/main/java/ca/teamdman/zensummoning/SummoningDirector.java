package ca.teamdman.zensummoning;

import com.google.common.collect.ImmutableSet;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.api.item.IItemStack;
import crafttweaker.mc1120.data.NBTConverter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ZenRegister
@ZenClass("mods.zensummoning.SummoningDirector")
public class SummoningDirector {
	private static final List<SummonInfo> summonings = new ArrayList<>();

	public static SummonInfo getSummonInfo(ItemStack stack) {
		return summonings.stream().filter(s -> s.catalyst.isItemEqual(stack) && s.catalyst.getCount() <= stack.getCount()).findFirst().orElse(null);
	}

	public static ImmutableSet<SummonInfo> getSummonInfos() {
		return ImmutableSet.copyOf(summonings);
	}

	@ZenMethod
	public static void addSummonInfo(IItemStack catalyst, List<IItemStack> reagents, String mod, String mob, int height, IData data) {
		summonings.add(
				new SummonInfo(
						(ItemStack) catalyst.getInternal(),
						reagents.stream().map(r -> (ItemStack) r.getInternal()).collect(Collectors.toList()),
						new ResourceLocation(mod, mob),
						height,
						data
				)
		);
		ZenSummoning.log("addSummonInfo for " + mod + ":" + mob);
	}

	@ZenMethod
	public static void enableDebugging() {
		ZenSummoning.debug = true;
	}

	public static class SummonInfo {
		public final ItemStack        catalyst;
		public final NBTTagCompound   data;
		public final int              height;
		public final ResourceLocation mob;
		public final List<ItemStack>  reagents;

		public SummonInfo(ItemStack catalyst, List<ItemStack> reagents, ResourceLocation mob, int height, IData data) {
			this.catalyst = catalyst;
			this.reagents = reagents;
			this.mob = mob;
			this.height = height;
			this.data = (NBTTagCompound) NBTConverter.from(data);
		}

		public SummonInfo(NBTTagCompound compound) {
			this.catalyst = ItemStack.EMPTY;
			this.reagents = new ArrayList<>();
			this.mob = new ResourceLocation(compound.getString("mod"), compound.getString("mob"));
			this.height = compound.getInteger("height");
			this.data = compound.getCompoundTag("data");
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
}
