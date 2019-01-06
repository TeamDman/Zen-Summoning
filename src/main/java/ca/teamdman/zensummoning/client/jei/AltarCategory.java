package ca.teamdman.zensummoning.client.jei;

import ca.teamdman.zensummoning.ZenSummoning;
import ca.teamdman.zensummoning.common.Registrar;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import java.util.List;

public class AltarCategory implements IRecipeCategory<AltarRecipe> {
	private final IDrawable background;
	private final int WIDTH = 160;
	private final int HEIGHT = 200;
	public AltarCategory(IGuiHelper guiHelper) {
		background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
	}

	@Override
	public String getUid() {
		return ZenSummoning.JEI_CATEGORY;
	}

	@Override
	public String getTitle() {
		return I18n.format("jei.zensummoning.recipe.altar");
	}

	@Override
	public String getModName() {
		return ZenSummoning.MOD_NAME;
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, AltarRecipe recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup    guiItemStacks = recipeLayout.getItemStacks();
		List<List<ItemStack>> inputs        = ingredients.getInputs(ItemStack.class);
		guiItemStacks.init(0, true, WIDTH / 2 - 8, HEIGHT/2 - 24);
		int i;
		final double dist = 25 + inputs.size()*2;
		final double cut = (Math.PI * 2 / (inputs.size()-1));
		for (i=0; i < inputs.size()-1; i++) {
			double x = Math.cos(cut * i)*dist;
			double y = Math.sin(cut * i)*dist;
			guiItemStacks.init(i+1, true, WIDTH / 2 - 8 + (int) x, HEIGHT / 2 - 8 + (int) y);
		}
		guiItemStacks.set(ingredients);
		guiItemStacks.init(++i, false, WIDTH / 2 - 8, HEIGHT/2 - 8);
		guiItemStacks.set(i, new ItemStack(Registrar.Blocks.ALTAR));

	}
}
