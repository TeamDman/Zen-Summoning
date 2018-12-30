package ca.teamdman.zensummoning.common;

import ca.teamdman.zensummoning.ZenSummoning;
import ca.teamdman.zensummoning.common.blocks.BlockAltar;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Objects;

public class Registrar {
	public static final Multimap<EntryType, Block> blocks = ArrayListMultimap.create();
	public static final Multimap<EntryType, Item>  items  = ArrayListMultimap.create();
	public enum EntryType {
		NEEDS_DEFAULT_ITEMBLOCK,
		DEFAULT
	}

	@GameRegistry.ObjectHolder(ZenSummoning.MOD_ID)
	public static class Blocks {
		public static final Block ALTAR = net.minecraft.init.Blocks.AIR;
	}

	@GameRegistry.ObjectHolder(ZenSummoning.MOD_ID)
	public static class Items {
      /*
          public static final ItemBlock mySpecialBlock = null; // itemblock for the block above
          public static final MySpecialItem mySpecialItem = null; // placeholder for special item below
      */
	}

	@Mod.EventBusSubscriber(modid = ZenSummoning.MOD_ID)
	public static class ObjectRegistryHandler {
		@SubscribeEvent
		public static void addItems(RegistryEvent.Register<Item> event) {
           /*
             event.getRegistry().register(new ItemBlock(Blocks.myBlock).setRegistryName(MOD_ID, "myBlock"));
             event.getRegistry().register(new MySpecialItem().setRegistryName(MOD_ID, "mySpecialItem"));
            */
			blocks.values().forEach(b -> items.put(EntryType.DEFAULT,
					new ItemBlock(b)
							.setRegistryName(Objects.requireNonNull(b.getRegistryName()))
							.setCreativeTab(ZenSummoning.CREATIVE_TAB))
			);
			items.values().forEach(event.getRegistry()::register);
		}

		@SubscribeEvent
		public static void addBlocks(RegistryEvent.Register<Block> event) {
			blocks.put(EntryType.NEEDS_DEFAULT_ITEMBLOCK, new BlockAltar());
			blocks.values().forEach(event.getRegistry()::register);
		}
	}
}
