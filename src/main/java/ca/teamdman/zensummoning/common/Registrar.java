package ca.teamdman.zensummoning.common;

import ca.teamdman.zensummoning.ZenSummoning;
import ca.teamdman.zensummoning.common.blocks.BlockAltar;
import ca.teamdman.zensummoning.common.tiles.TileAltar;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = ZenSummoning.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registrar {
	public static final CreativeModeTab         group  = new CreativeModeTab(ZenSummoning.MOD_ID) {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ALTAR_BLOCK.get());
		}
	};

	public static       DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ZenSummoning.MOD_ID);
	public static       RegistryObject<Block>   ALTAR_BLOCK = BLOCKS.register("altar", BlockAltar::new);

	public static DeferredRegister<Item>              ITEMS      = DeferredRegister.create(ForgeRegistries.ITEMS, ZenSummoning.MOD_ID);
	public static RegistryObject<Item>                            ALTAR_ITEM = ITEMS.register("altar", () -> new BlockItem(ALTAR_BLOCK.get(), new Item.Properties().tab(group)));
	public static DeferredRegister<BlockEntityType<?>>            TILES      = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, ZenSummoning.MOD_ID);
	public static       RegistryObject<BlockEntityType<TileAltar>> ALTAR_TILE = TILES.register("altar",
																							   () -> BlockEntityType.Builder.of(TileAltar::new, ALTAR_BLOCK.get())
																									   .build(null));

}
