package ca.teamdman.zensummoning.common;

import ca.teamdman.zensummoning.ZenSummoning;
import ca.teamdman.zensummoning.common.blocks.BlockAltar;
import ca.teamdman.zensummoning.common.tiles.TileAltar;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(modid = ZenSummoning.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registrar {
	public static final ItemGroup group = new ItemGroup(-1, "zensummoning") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Blocks.ALTAR);
		}
	};

	@SubscribeEvent
	public static void onRegisterTileEntityTypes(@Nonnull final RegistryEvent.Register<TileEntityType<?>> e) {
		e.getRegistry()
		 .register(TileEntityType.Builder.create(TileAltar::new, Blocks.ALTAR)
										 .build(null)
										 .setRegistryName(ZenSummoning.MOD_ID, "altar"));
	}

	@SubscribeEvent
	public static void onRegisterItems(final RegistryEvent.Register<Item> e) {
		Item altar = new BlockItem(Blocks.ALTAR, new Item.Properties().group(group)).setRegistryName(ZenSummoning.MOD_ID, "altar");
		e.getRegistry()
		 .register(altar);
		ZenSummoning.PROXY.fillItemGroup(group, altar);
	}

	@SubscribeEvent
	public static void onRegisterBlocks(final RegistryEvent.Register<Block> e) {
		e.getRegistry().register(new BlockAltar());
	}

	@ObjectHolder(ZenSummoning.MOD_ID)
	public static final class Blocks {

		public static final Block ALTAR = null;
	}

	@ObjectHolder(ZenSummoning.MOD_ID)
	public static class Items {
		public static final Item ALTAR = null;
	}

	@ObjectHolder(ZenSummoning.MOD_ID)
	public static final class Tiles {
		public static final TileEntityType<TileAltar> ALTAR = null;
	}
}
