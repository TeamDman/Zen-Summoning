package ca.teamdman.zensummoning.client.jei;

import ca.teamdman.zensummoning.SummoningDirector;
import com.google.common.collect.Lists;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.awt.*;

public class AltarRecipe implements IRecipeWrapper {
	private SummoningDirector.SummonInfo summonInfo;

	public AltarRecipe(SummoningDirector.SummonInfo summonInfo) {
		this.summonInfo = summonInfo;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputs(ItemStack.class, Lists.asList(summonInfo.catalyst, summonInfo.reagents.toArray(new ItemStack[0])));
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("display", new NBTTagCompound());
		nbt.getCompoundTag("display").setString("Name", "Summon " + summonInfo.mob.getPath());
		ItemStack egg = new ItemStack(Items.SPAWN_EGG);
		egg.setTagCompound(nbt);
		ingredients.setOutput(ItemStack.class, egg);
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		minecraft.fontRenderer.drawString(summonInfo.mob.getPath(), 0, 40, Color.GRAY.getRGB());
	}
}
