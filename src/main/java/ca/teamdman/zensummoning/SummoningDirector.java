package ca.teamdman.zensummoning;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

@ZenRegister
@ZenClass("mods.zensummoning.SummoningDirector")
public class SummoningDirector {
	private static List<SummonInfo> summonings = new ArrayList<>();
	private static class SummonInfo {
		ItemStack catalyst;
		List<ItemStack> reagents;
		IData data;

		public SummonInfo(ItemStack catalyst, List<ItemStack> reagents, IData data) {
			this.catalyst = catalyst;
			this.reagents = reagents;
			this.data = data;
		}
	}


}
