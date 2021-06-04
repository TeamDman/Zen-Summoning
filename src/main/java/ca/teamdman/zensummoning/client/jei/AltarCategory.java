package ca.teamdman.zensummoning.client.jei;

import ca.teamdman.zensummoning.ZenSummoning;
import ca.teamdman.zensummoning.common.Registrar;
import ca.teamdman.zensummoning.common.summoning.MobInfo;
import ca.teamdman.zensummoning.common.summoning.SummoningInfo;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AltarCategory implements IRecipeCategory<SummoningInfo> {
	private final int       HEIGHT = 120;
	private final int       WIDTH  = 160;
	private final IDrawable background;
	private final IDrawable icon;


	public AltarCategory(IGuiHelper guiHelper) {
		this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
		this.icon = guiHelper.createDrawableIngredient(new ItemStack(Registrar.ALTAR_BLOCK.get()));
	}

	@Override
	public ResourceLocation getUid() {
		return ZenSummoning.JEI_CATEGORY;
	}

	@Override
	public Class<SummoningInfo> getRecipeClass() {
		return SummoningInfo.class;
	}

	@Override
	public String getTitle() {
		return I18n.format("jei.zensummoning.recipe.altar");
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setIngredients(SummoningInfo summoningInfo, IIngredients ingredients) {
		List<ItemStack> inputs = Stream.concat(Stream.of(summoningInfo.getCatalyst()),
											   summoningInfo.getReagents()
															.stream())
									   .flatMap(x -> Arrays.stream(x.getIngredient()
																	.getItems()))
									   .map(IItemStack::getInternal)
									   .collect(Collectors.toList());

		ListNBT lore = new ListNBT();
		lore.add(StringNBT.valueOf(I18n.format("jei.zensummoning.catalyst.lore")));
		inputs.get(0)
			  .getOrCreateChildTag("display")
			  .put("Lore", lore);

		List<ItemStack> outputs = summoningInfo.getMobs()
											   .stream()
											   .map(MobInfo::getEntityType)
											   .map(SpawnEggItem::getEgg)
											   .map(ItemStack::new)
											   .collect(Collectors.toList());

		ingredients.setInputs(VanillaTypes.ITEM, inputs);
		ingredients.setOutputs(VanillaTypes.ITEM, outputs);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, SummoningInfo summoningInfo, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		//		guiItemStacks.init(0, true, WIDTH / 2 - 8, HEIGHT / 2 - 24);
		guiItemStacks.init(0, true, WIDTH / 2 - 8, HEIGHT / 2 - 16);
		int          size = ingredients.getInputs(VanillaTypes.ITEM)
									   .size();
		int          i;
		final double dist = 25 + size * 2;
		final double cut  = (Math.PI * 2 / (size - 1));
		for (i = 0; i < size - 1; i++) {
			double x = Math.cos(cut * i) * dist;
			double y = Math.sin(cut * i) * dist;
			guiItemStacks.init(i + 1, true, WIDTH / 2 - 8 + (int) x, HEIGHT / 2 - 8 + (int) y);
		}
		guiItemStacks.set(ingredients);
		guiItemStacks.init(++i, false, WIDTH / 2 - 8, HEIGHT / 2 - 8);
		guiItemStacks.set(i, new ItemStack(Registrar.ALTAR_BLOCK.get()));
	}

	@Override
	public void draw(SummoningInfo summonInfo, MatrixStack matrixStack, double mouseX, double mouseY) {
		int       i         = 0;
		Minecraft minecraft = Minecraft.getInstance();
		for (MobInfo mob : summonInfo.getMobs())
			minecraft.fontRenderer.drawString(matrixStack,
											  I18n.format("jei.zensummoning.recipe.altar.entity",
														  mob.getCount(),
														  I18n.format(mob.getEntityType()
																		 .getTranslationKey())),
											  0,
											  9 * i++,
											  Color.GRAY.getRGB());
		minecraft.fontRenderer.drawString(matrixStack, I18n.format("jei.zensummoning.recipe.altar.isCatalystConsumed", summonInfo.isCatalystConsumed()), 0, 9 * i++, Color.GRAY.getRGB());
		minecraft.fontRenderer.drawString(matrixStack, I18n.format("jei.zensummoning.recipe.altar.weight", summonInfo.getWeight()), 0, 9 * i++, Color.GRAY.getRGB());
		for (SummoningInfo.SummoningCondition condition : summonInfo.getConditions()) {
			minecraft.fontRenderer.drawString(matrixStack, condition.JEI_DESCRIPTION, 0, 9 * i++, Color.GRAY.getRGB());
		}
	}
}
