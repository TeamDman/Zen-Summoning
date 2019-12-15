package ca.teamdman.zensummoning.client.jei;


import ca.teamdman.zensummoning.ZenSummoning;
import ca.teamdman.zensummoning.common.Registrar;
import ca.teamdman.zensummoning.common.summoning.SummoningDirector;
import ca.teamdman.zensummoning.common.summoning.SummoningInfo;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;

@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin {

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		registry.addRecipeCategories(new AltarCategory(registry.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void register(IModRegistry registry) {
		registry.addRecipes(SummoningDirector.getSummonInfos().getList(), ZenSummoning.JEI_CATEGORY);
		registry.handleRecipes(SummoningInfo.class, AltarRecipe::new, ZenSummoning.JEI_CATEGORY);
		registry.addRecipeCatalyst(new ItemStack(Registrar.Blocks.ALTAR), ZenSummoning.JEI_CATEGORY);
	}
}
