package ca.teamdman.zensummoning.common.summoning;

import ca.teamdman.zensummoning.ZenSummoning;
import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.actions.IUndoableAction;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import net.minecraftforge.fml.LogicalSide;
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
		CraftTweakerAPI.apply(new AddSummonInfoAction(info, summonings));
	}

	private static void tightenStackLimit() {
		stackLimit = summonings.stream()
							   .map(SummoningInfo::getReagents)
							   .mapToInt(List::size)
							   .max()
							   .orElse(0);
	}

	@SuppressWarnings("unused")
	@ZenCodeType.Method
	public static void enableDebugging() {
		ZenSummoning.debug = true;
	}

	private static class AddSummonInfoAction implements IUndoableAction {
		private final SummoningInfo INFO;

		public AddSummonInfoAction(SummoningInfo info, List<SummoningInfo> list) {
			this.INFO = info;
		}

		@Override
		public void undo() {
			summonings.remove(INFO);
			SummoningDirector.tightenStackLimit();
		}

		@Override
		public String describeUndo() {
			return "Unregistering summoning " + INFO.toString();
		}

		@Override
		public void apply() {
			summonings.add(INFO);
			SummoningDirector.tightenStackLimit();
			ZenSummoning.log("addSummonInfo");
		}

		@Override
		public String describe() {
			return "Registering summoning " + INFO.toString();
		}

		public boolean shouldApplyOn(LogicalSide side) {
			return this.shouldApplySingletons();
		}
	}
}
