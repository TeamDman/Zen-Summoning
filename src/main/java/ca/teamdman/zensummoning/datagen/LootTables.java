package ca.teamdman.zensummoning.datagen;

import ca.teamdman.zensummoning.common.Registrar;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LootTables extends LootTableProvider {
	public LootTables(DataGenerator dataGeneratorIn) {
		super(dataGeneratorIn);
	}

	@Override
	protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
		return Lists.newArrayList(Pair.of(BlockLootTableProvider::new, LootContextParamSets.BLOCK));
	}

	@Override
	protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext val) {
		map.forEach((k, v) -> net.minecraft.world.level.storage.loot.LootTables.validate(val, k, v));
	}

	private class BlockLootTableProvider extends BlockLoot {
		@Override
		protected void addTables() {
			dropSelf(Registrar.ALTAR_BLOCK.get());
		}

		@Override
		protected Iterable<Block> getKnownBlocks() {
			return Arrays.asList(Registrar.ALTAR_BLOCK.get());
		}
	}
}
