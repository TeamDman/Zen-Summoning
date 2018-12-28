package ca.teamdman.zensummoning;

import ca.teamdman.zensummoning.common.CommonProxy;
import ca.teamdman.zensummoning.common.tiles.TileAltar;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = ZenSummoning.MOD_ID, name = ZenSummoning.MOD_NAME, version = ZenSummoning.VERSION)
public class ZenSummoning {

	public static final String MOD_ID   = "zensummoning";
	public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(MOD_ID) {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(net.minecraft.init.Blocks.LIT_PUMPKIN);
		}
	};
	public static final String MOD_NAME = "ZenSummoning";
	public static final String VERSION  = "1.0.0";
	public static final String CLIENT_PROXY = "ca.teamdman.zensummoning.client.ClientProxy";
	public static final String COMMON_PROXY = "ca.teamdman.zensummoning.common.CommonProxy";

	@Mod.Instance(MOD_ID)
	public static ZenSummoning INSTANCE;
	@SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY)
	public static CommonProxy  proxy;

	/**
	 * This is the first initialization event. Register tile entities here.
	 * The registry events below will have fired prior to entry to this method.
	 */
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
		GameRegistry.registerTileEntity(TileAltar.class, new ResourceLocation(MOD_ID, "altar"));
	}

	/**
	 * This is the second initialization event. Register custom recipes
	 */
	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {

	}

	/**
	 * This is the final initialization event. Register actions from other mods here
	 */
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}
}
