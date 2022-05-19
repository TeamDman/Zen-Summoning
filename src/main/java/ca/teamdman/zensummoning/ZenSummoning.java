package ca.teamdman.zensummoning;

import ca.teamdman.zensummoning.common.Registrar;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@SuppressWarnings("WeakerAccess")
//@Mod(modid = ZenSummoning.MOD_ID, name = ZenSummoning.MOD_NAME, version = ZenSummoning.VERSION, dependencies = "required-after:crafttweaker")
@Mod(ZenSummoning.MOD_ID)
public class ZenSummoning {

	public static final String           CLIENT_PROXY = "ca.teamdman.zensummoning.client.ClientProxy";
	public static final String           COMMON_PROXY = "ca.teamdman.zensummoning.common.CommonProxy";
	public static final String           MOD_ID       = "zensummoning";
	public static final ResourceLocation JEI_CATEGORY = new ResourceLocation(MOD_ID, "altar");
	public static final String           MOD_NAME     = "Zen Summoning";
	public static final String           VERSION      = "@VERSION@";
	public static final String           ZEN_PACKAGE  = "mods." + MOD_ID;

	public ZenSummoning() {
		IEventBus bus = FMLJavaModLoadingContext.get()
														.getModEventBus();
		Registrar.BLOCKS.register(bus);
		Registrar.ITEMS.register(bus);
		Registrar.TILES.register(bus);
	}
}
