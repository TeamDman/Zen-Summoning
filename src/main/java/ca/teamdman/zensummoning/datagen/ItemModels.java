package ca.teamdman.zensummoning.datagen;

import ca.teamdman.zensummoning.ZenSummoning;
import ca.teamdman.zensummoning.common.Registrar;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModels extends ItemModelProvider {
	public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, ZenSummoning.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		//https://github.com/MinecraftForge/MinecraftForge/blob/1.16.x/src/test/java/net/minecraftforge/debug/client/model/NewModelLoaderTest.java#L247-L252
		this.withExistingParent(Registrar.ALTAR_ITEM.getId()
										.getPath(),
								ZenSummoning.MOD_ID + ":block/" + Registrar.ALTAR_BLOCK.getId()
										.getPath());
	}
}
