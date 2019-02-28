package ca.teamdman.zensummoning.client.jei;

import ca.teamdman.zensummoning.SummoningInfo;
import com.google.common.collect.Lists;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.awt.*;

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
		summonInfo.catalyst.getOrCreateSubCompound("display").setTag("Lore", lore);
		ingredients.setInputs(ItemStack.class, Lists.asList(summonInfo.catalyst, summonInfo.reagents.toArray(new ItemStack[0])));
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		minecraft.fontRenderer.drawString(summonInfo.mob.getPath(), 0, 40, Color.GRAY.getRGB());
	}
}
