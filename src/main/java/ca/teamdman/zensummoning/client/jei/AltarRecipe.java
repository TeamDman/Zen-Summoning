package ca.teamdman.zensummoning.client.jei;

import ca.teamdman.zensummoning.MobInfo;
import ca.teamdman.zensummoning.SummoningInfo;
import com.google.common.collect.Lists;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

class AltarRecipe implements IRecipeWrapper {
	private final SummoningInfo summonInfo;

	public AltarRecipe(SummoningInfo summonInfo) {
		this.summonInfo = summonInfo;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
//		summonInfo.catalyst.getTooltip();
		NBTTagList lore = new NBTTagList();
		lore.appendTag(new NBTTagString("Shift-rightclick with the catalyst once all items are in the altar to begin"));
		summonInfo.getCatalyst().getOrCreateSubCompound("display").setTag("Lore", lore);

		List<ItemStack> eggs = summonInfo.getMobs().stream()
				.map(info -> {
					ItemStack stack = new ItemStack(Items.SPAWN_EGG);
					ItemMonsterPlacer.applyEntityIdToItemStack(stack, info.getMob());
					return stack;
				}).collect(Collectors.toList());

		ingredients.setInputs(ItemStack.class, Lists.asList(summonInfo.getCatalyst(), summonInfo.getReagents().toArray(new ItemStack[0])));
		ingredients.setOutputs(ItemStack.class, eggs);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		int i=0;
		for (MobInfo mob : summonInfo.getMobs())
			minecraft.fontRenderer.drawString(I18n.format("jei.zensummoning.recipe.altar.entity",mob.getCount(), I18n.format("entity." + EntityList.getTranslationName(mob.getMob()) + ".name")), 0, 40 + 7*i++, Color.GRAY.getRGB());
	}
}
