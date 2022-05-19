package ca.teamdman.zensummoning.client.jei;

import ca.teamdman.zensummoning.ZenSummoning;
import ca.teamdman.zensummoning.common.Registrar;
import ca.teamdman.zensummoning.common.summoning.MobInfo;
import ca.teamdman.zensummoning.common.summoning.SummoningInfo;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;

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
	public Component getTitle() {
		return new TranslatableComponent("jei.zensummoning.recipe.altar");
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
	public void setIngredients(SummoningInfo recipe, IIngredients ingredients) {
		List<ItemStack> inputs = Stream.concat(Stream.of(recipe.getCatalyst()),
											   recipe.getReagents()
													   .stream())
				.flatMap(x -> Arrays.stream(x.getIngredient()
													.getItems()))
				.map(IItemStack::getInternal)
				.toList();

		ListTag lore = new ListTag();
		lore.add(StringTag.valueOf(I18n.get("jei.zensummoning.catalyst.lore")));
		inputs.get(0)
				.getOrCreateTagElement("display")
				.put("Lore", lore);

		List<ItemStack> outputs = recipe.getMobs()
				.stream()
				.map(MobInfo::getEntityType)
				.map(SpawnEggItem::byId)
				.map(ItemStack::new)
				.filter(stack -> !stack.isEmpty())
				.collect(Collectors.toList());
		outputs.add(new ItemStack(Registrar.ALTAR_BLOCK.get()));
		ingredients.setInputs(VanillaTypes.ITEM, inputs);
		ingredients.setOutputs(VanillaTypes.ITEM, outputs);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, SummoningInfo summoningInfo, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		//		guiItemStacks.init(0, true, WIDTH / 2 - 8, HEIGHT / 2 - 24);
		guiItemStacks.init(0, true, WIDTH / 2 - 8, HEIGHT / 2 - 16);
		int size = ingredients.getInputs(VanillaTypes.ITEM)
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
	public void draw(SummoningInfo recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
		int       i         = 0;
		Minecraft minecraft = Minecraft.getInstance();
		for (MobInfo mob : recipe.getMobs())
			minecraft.font.draw(stack,
								I18n.get("jei.zensummoning.recipe.altar.entity",
										 mob.getCount(),
										 I18n.get(mob.getEntityType()
														  .getDescriptionId())),
								0,
								9 * i++,
								Color.GRAY.getRGB());
		minecraft.font.draw(stack, I18n.get("jei.zensummoning.recipe.altar.isCatalystConsumed", recipe.isCatalystConsumed()), 0, 9 * i++, Color.GRAY.getRGB());
		minecraft.font.draw(stack, I18n.get("jei.zensummoning.recipe.altar.weight", recipe.getWeight()), 0, 9 * i++, Color.GRAY.getRGB());
		for (SummoningInfo.SummoningCondition condition : recipe.getConditions()) {
			minecraft.font.draw(stack, condition.JEI_DESCRIPTION, 0, 9 * i++, Color.GRAY.getRGB());
		}
	}
}
