package ca.teamdman.zensummoning.client.jei;

import ca.teamdman.zensummoning.ZenSummoning;
import ca.teamdman.zensummoning.common.Registrar;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.config.Constants;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import java.util.List;

public class AltarCategory implements IRecipeCategory<AltarRecipe> {
	private final IDrawable background;
	private final int WIDTH = 160;
	private final int HEIGHT = 200;
	public AltarCategory(IGuiHelper guiHelper) {
//		background = guiHelper.createDrawable(Constants.RECIPE_GUI_VANILLA, 0, 168, 125, 18,0, 20 ,0 ,0);
		background = guiHelper.createBlankDrawable(160, 200);
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
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
		guiItemStacks.init(0, true, WIDTH/2-28, 90);
		int i = 1;
		for (; i < inputs.size(); i++) {
			guiItemStacks.init(i, true, (i-1)%6*20 + 22, 124 + (i-1)/6 * 20);
		}

		guiItemStacks.init(i+1, false, WIDTH/2-8, 64);
		guiItemStacks.set(ingredients);

		guiItemStacks.init(i+2, false, WIDTH/2-8, 90);
		guiItemStacks.set(i+2, new ItemStack(Registrar.Blocks.ALTAR));

	}
}
