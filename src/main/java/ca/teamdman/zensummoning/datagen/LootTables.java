package ca.teamdman.zensummoning.datagen;

import ca.teamdman.zensummoning.common.Registrar;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.*;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LootTables extends net.minecraft.data.LootTableProvider {
	public LootTables(DataGenerator dataGeneratorIn) {
		super(dataGeneratorIn);
	}

	@Override
	protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
		return Lists.newArrayList(Pair.of(BlockLootTableProvider::new, LootParameterSets.BLOCK));
	}

	@Override
	protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker val) {
		map.forEach((k,v) -> LootTableManager.validateLootTable(val, k, v));
	}

	private class BlockLootTableProvider extends BlockLootTables {
		@Override
		protected void addTables() {
			registerDropSelfLootTable(Registrar.ALTAR_BLOCK.get());
		}

		@Override
		protected Iterable<Block> getKnownBlocks() {
			return Arrays.asList(Registrar.ALTAR_BLOCK.get());
		}
	}
}
