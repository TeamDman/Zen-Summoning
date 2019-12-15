package ca.teamdman.zensummoning.common.summoning;

import ca.teamdman.zensummoning.ZenSummoning;
import ca.teamdman.zensummoning.util.WeightedRandomBag;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass(ZenSummoning.ZEN_PACKAGE + ".SummoningDirector")
public class SummoningDirector {
	private static final WeightedRandomBag<SummoningInfo> summonings = new WeightedRandomBag<>();
	private static       int                              stackLimit = 0;

	public static int getStackLimit() {
		return stackLimit;
	}

	public static WeightedRandomBag<SummoningInfo> getSummonInfos() {
		return summonings;
	}

	@ZenMethod
	public static void addSummonInfo(SummoningInfo info) {
		summonings.addEntry(info, info.getWeight());
		stackLimit = Math.max(info.getReagents().size(), stackLimit);
		ZenSummoning.log("addSummonInfo");
	}

	@SuppressWarnings("unused")
	@ZenMethod
	public static void enableDebugging() {
		ZenSummoning.debug = true;
	}

}
