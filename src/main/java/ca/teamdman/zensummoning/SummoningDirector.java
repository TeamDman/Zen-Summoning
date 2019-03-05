package ca.teamdman.zensummoning;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ZenRegister
@ZenClass(ZenSummoning.ZEN_PACKAGE + ".SummoningDirector")
public class SummoningDirector {
	private static final Set<SummoningInfo> summonings = new HashSet<>();
	private static       int                stackLimit = 0;

	public static int getStackLimit() {
		return stackLimit;
	}

	public static SummoningInfo getSummonInfo(ItemStack stack) {
		return summonings.stream().filter(s -> s.getCatalyst().isItemEqual(stack) && s.getCatalyst().getCount() <= stack.getCount()).findFirst().orElse(null);
	}

	public static Set<SummoningInfo> getSummonInfos() {
		return Collections.unmodifiableSet(summonings);
	}


	@ZenMethod
	public static void addSummonInfo(SummoningInfo info) {
		summonings.add(info);
		stackLimit = Math.max(info.getReagents().stream().mapToInt(r -> r.getCount() != -1 ? Math.max(r.getCount(), r.getMaxStackSize()) / r.getMaxStackSize() : 0).sum(), stackLimit);
		ZenSummoning.log("addSummonInfo");
	}

	@SuppressWarnings("unused")
	@ZenMethod
	public static void enableDebugging() {
		ZenSummoning.debug = true;
	}

}
