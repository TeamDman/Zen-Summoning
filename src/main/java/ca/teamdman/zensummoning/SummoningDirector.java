package ca.teamdman.zensummoning;

import ca.teamdman.zensummoning.common.Mutator;
import com.google.common.collect.ImmutableSet;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@ZenRegister
@ZenClass("mods.zensummoning.SummoningDirector")
public class SummoningDirector {
	private static final List<SummoningInfo>                               summonings = new ArrayList<>();
	public static        HashMap<SummoningInfo, Mutator<SummoningAttempt>> mutators;
	private static       int                                               stackLimit = 0;

	public static int getStackLimit() {
		return stackLimit;
	}

	public static SummoningInfo getSummonInfo(ItemStack stack) {
		return summonings.stream().filter(s -> s.catalyst.isItemEqual(stack) && s.catalyst.getCount() <= stack.getCount()).findFirst().orElse(null);
	}

	public static ImmutableSet<SummoningInfo> getSummonInfos() {
		return ImmutableSet.copyOf(summonings);
	}

	@ZenMethod
	public static void addSummonInfo(IItemStack catalyst, List<IItemStack> reagents, String mod, String mob) {
		addSummonInfo(catalyst, reagents, mod, mob, 5);
	}

	@ZenMethod
	public static void addSummonInfo(IItemStack catalyst, List<IItemStack> reagents, String mod, String mob, int height) {
		addSummonInfo(catalyst, reagents, mod, mob, height, CraftTweakerMC.getIData(new NBTTagCompound()));
	}

	@ZenMethod
	public static void addSummonInfo(IItemStack catalyst, List<IItemStack> reagents, String mod, String mob, int height, IData data) {
		addSummonInfo(catalyst, reagents, mod, mob, height, data, (__) -> {
		});
	}

	@ZenMethod
	public static void addSummonInfo(IItemStack catalyst, List<IItemStack> reagents, String mod, String mob, int height, IData data, Mutator<SummoningAttempt> mutator) {
		SummoningInfo info = new SummoningInfo(
				(ItemStack) catalyst.getInternal(),
				reagents.stream().map(r -> (ItemStack) r.getInternal()).collect(Collectors.toList()),
				new ResourceLocation(mod, mob),
				height,
				data
		);
		summonings.add(info);
		mutators.put(info, mutator);
		stackLimit = Math.max(reagents.stream().mapToInt(r -> r.getAmount() != -1 ? Math.max(r.getAmount(), r.getMaxStackSize()) / r.getMaxStackSize() : 0).sum(), stackLimit);
		ZenSummoning.log("addSummonInfo for " + mod + ":" + mob);
	}

	@SuppressWarnings("unused")
	@ZenMethod
	public static void enableDebugging() {
		ZenSummoning.debug = true;
	}

}
