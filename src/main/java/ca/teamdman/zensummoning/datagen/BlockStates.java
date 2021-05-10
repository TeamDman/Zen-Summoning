package ca.teamdman.zensummoning.datagen;

import ca.teamdman.zensummoning.ZenSummoning;
import ca.teamdman.zensummoning.common.Registrar;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStates extends net.minecraftforge.client.model.generators.BlockStateProvider {
	public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, ZenSummoning.MOD_ID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		//https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/src/test/java/net/minecraftforge/debug/client/model/NewModelLoaderTest.java#L247-L252
		//https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/src/test/java/net/minecraftforge/debug/DeferredRegistryTest.java
		simpleBlock(Registrar.ALTAR_BLOCK.get(),
					models().withExistingParent("altar", new ResourceLocation(ZenSummoning.MOD_ID, "block/altar_model"))
							.texture("altar_new", new ResourceLocation(ZenSummoning.MOD_ID, "blocks/altar_new"))
							.texture("particle", "#altar_new"));


	}
}
