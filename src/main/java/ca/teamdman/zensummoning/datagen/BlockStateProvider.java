package ca.teamdman.zensummoning.datagen;

import ca.teamdman.zensummoning.ZenSummoning;
import ca.teamdman.zensummoning.common.Registrar;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStateProvider extends net.minecraftforge.client.model.generators.BlockStateProvider {
	public BlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, ZenSummoning.MOD_ID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		//		this.simpleBlock(Registrar.Blocks.ALTAR,
		//						 new ConfiguredModel(models().withExistingParent("altar", new ResourceLocation(ZenSummoning.MOD_ID))
		//													 .texture("altar_new", new ResourceLocation(ZenSummoning.MOD_ID))));

		//https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/src/test/java/net/minecraftforge/debug/client/model/NewModelLoaderTest.java#L247-L252
		simpleBlock(Registrar.ALTAR_BLOCK.get(),
					models().getBuilder(Registrar.ALTAR_BLOCK.getId()
															 .getPath())
							.texture("#altar_new", new ResourceLocation(ZenSummoning.MOD_ID, "blocks/altar_new"))
							.texture("particle", "#altar_new"));
	}
}
