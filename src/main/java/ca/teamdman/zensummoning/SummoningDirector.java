package ca.teamdman.zensummoning;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ZenRegister
@ZenClass("mods.zensummoning.SummoningDirector")
public class SummoningDirector {
	private static List<SummonInfo> summonings = new ArrayList<>();

	public static SummonInfo getSummonInfo(ItemStack stack) {
		return summonings.stream().filter(s -> s.catalyst.isItemEqual(stack) && s.catalyst.getCount() <= stack.getCount()).findFirst().orElse(null);
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
	}

	public static class SummonInfo {
		public ItemStack        catalyst;
		public IData            data;
		public int              height;
		public ResourceLocation mob;
		public List<ItemStack>  reagents;

		public SummonInfo(ItemStack catalyst, List<ItemStack> reagents, ResourceLocation mob, int height, IData data) {
			this.catalyst = catalyst;
			this.reagents = reagents;
			this.mob = mob;
			this.height = height;
			this.data = data;
		}
	}
}
