package ca.teamdman.zensummoning;

import ca.teamdman.zensummoning.common.CommonProxy;
import ca.teamdman.zensummoning.common.tiles.TileAltar;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@SuppressWarnings("WeakerAccess")
@Mod(modid = ZenSummoning.MOD_ID, name = ZenSummoning.MOD_NAME, version = ZenSummoning.VERSION, dependencies = "required-after:crafttweaker")
public class ZenSummoning {

	public static final String       CLIENT_PROXY = "ca.teamdman.zensummoning.client.ClientProxy";
	public static final String       COMMON_PROXY = "ca.teamdman.zensummoning.common.CommonProxy";
	public static final String       MOD_ID       = "zensummoning";
	public static final String 		 ZEN_PACKAGE  = "mods." + MOD_ID;
	public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(MOD_ID) {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(net.minecraft.init.Blocks.LIT_PUMPKIN);
		}
	};
	public static final String       JEI_CATEGORY = MOD_ID + ":altar";
	public static final String       MOD_NAME     = "Zen Summoning";
	public static final String       VERSION      = "@VERSION@";
	@Mod.Instance(MOD_ID)
	public static       ZenSummoning INSTANCE;
	@SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY)
	public static       CommonProxy  proxy;
	public static boolean debug = false;

	public static void log(String... args) {
		if (debug)
			System.out.println("[ZenSummoning]\t" + String.join("\t", args));
	}

	/**
	 * This is the first initialization event. Register tile entities here.
	 * The registry events below will have fired prior to entry to this method.
	 */
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
		GameRegistry.registerTileEntity(TileAltar.class, new ResourceLocation(MOD_ID, "altar"));
	}

}
