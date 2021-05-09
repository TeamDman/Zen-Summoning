package ca.teamdman.zensummoning.common.summoning;

import ca.teamdman.zensummoning.ZenSummoning;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import org.openzen.zencode.java.ZenCodeType;

import java.util.ArrayList;
import java.util.List;

@ZenRegister
@ZenCodeType.Name(ZenSummoning.ZEN_PACKAGE + ".SummoningDirector")
public class SummoningDirector {
	private static final List<SummoningInfo> summonings = new ArrayList<>();
	private static       int                 stackLimit = 0;

	public static int getStackLimit() {
		return stackLimit;
	}

	public static List<SummoningInfo> getSummonInfos() {
		return summonings;
	}

	@ZenCodeType.Method
	public static void addSummonInfo(SummoningInfo info) {
		summonings.add(info);
		stackLimit = Math.max(info.getReagents()
								  .size(), stackLimit);
		ZenSummoning.log("addSummonInfo");
	}

	@SuppressWarnings("unused")
	@ZenCodeType.Method
	public static void enableDebugging() {
		ZenSummoning.debug = true;
	}

}
