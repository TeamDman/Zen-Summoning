package ca.teamdman.zensummoning;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {
	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SuppressWarnings("ConstantConditions")
	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		for (Item item : Registrar.items.get(Registrar.EntryType.DEFAULT)) {
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
		}
	}
}
