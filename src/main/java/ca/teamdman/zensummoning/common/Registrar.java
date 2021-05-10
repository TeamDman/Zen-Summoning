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
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = ZenSummoning.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registrar {
	public static       DeferredRegister<Block> BLOCKS      = DeferredRegister.create(ForgeRegistries.BLOCKS, ZenSummoning.MOD_ID);
	public static       RegistryObject<Block>   ALTAR_BLOCK = BLOCKS.register("altar", BlockAltar::new);
	public static final ItemGroup               group       = new ItemGroup(-1, "zensummoning") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ALTAR_BLOCK.get());
		}
	};
	public static DeferredRegister<Item>                    ITEMS      = DeferredRegister.create(ForgeRegistries.ITEMS, ZenSummoning.MOD_ID);
	public static RegistryObject<Item>                      ALTAR_ITEM = ITEMS.register("altar", () -> new BlockItem(ALTAR_BLOCK.get(), new Item.Properties().group(group)));
	public static DeferredRegister<TileEntityType<?>>       TILES      = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ZenSummoning.MOD_ID);
	public static RegistryObject<TileEntityType<TileAltar>> ALTAR_TILE = TILES.register("altar",
																						() -> TileEntityType.Builder.create(TileAltar::new, ALTAR_BLOCK.get())
																													.build(null));

}
