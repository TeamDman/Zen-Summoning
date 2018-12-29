package ca.teamdman.zensummoning.client;

import ca.teamdman.zensummoning.client.render.tile.TESRAltar;
import ca.teamdman.zensummoning.common.CommonProxy;
import ca.teamdman.zensummoning.common.Registrar;
import ca.teamdman.zensummoning.common.tiles.TileAltar;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
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

		ClientRegistry.bindTileEntitySpecialRenderer(TileAltar.class, new TESRAltar());
	}


}
