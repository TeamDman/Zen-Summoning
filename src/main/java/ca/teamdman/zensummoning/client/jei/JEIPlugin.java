package ca.teamdman.zensummoning.client.jei;


import ca.teamdman.zensummoning.ZenSummoning;
import ca.teamdman.zensummoning.common.Registrar;
import ca.teamdman.zensummoning.common.summoning.SummoningDirector;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class JEIPlugin implements IModPlugin {


	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(ZenSummoning.MOD_ID, "altar_plugin");
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new AltarCategory(registration.getJeiHelpers()
																	   .getGuiHelper()));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(Registrar.ALTAR_BLOCK.get()), ZenSummoning.JEI_CATEGORY);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		registration.addRecipes(SummoningDirector.getSummonInfos(), ZenSummoning.JEI_CATEGORY);
	}
}
