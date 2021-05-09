package ca.teamdman.zensummoning.client;

import ca.teamdman.zensummoning.client.render.tile.TESRAltar;
import ca.teamdman.zensummoning.common.Proxy;
import ca.teamdman.zensummoning.common.Registrar;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.Arrays;

public class ClientProxy implements Proxy {
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void fillItemGroup(ItemGroup group, Item... items) {
		group.fill(Arrays.stream(items)
						 .map(ItemStack::new)
						 .collect(NonNullList::create, NonNullList::add, NonNullList::addAll));
	}

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		ClientRegistry.bindTileEntityRenderer(Registrar.Tiles.ALTAR, TESRAltar::new);
	}


}
