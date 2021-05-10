package ca.teamdman.zensummoning;

import ca.teamdman.zensummoning.client.ClientProxy;
import ca.teamdman.zensummoning.client.render.tile.TESRAltar;
import ca.teamdman.zensummoning.common.Proxy;
import ca.teamdman.zensummoning.common.Registrar;
import ca.teamdman.zensummoning.common.ServerProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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
	public static final Proxy            PROXY        = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);
	public static final String           VERSION      = "@VERSION@";
	public static final String           ZEN_PACKAGE  = "mods." + MOD_ID;

	public ZenSummoning() {
		IEventBus bus = FMLJavaModLoadingContext.get()
														.getModEventBus();
		bus.addListener(this::onClientSetup);
		Registrar.BLOCKS.register(bus);
		Registrar.ITEMS.register(bus);
		Registrar.TILES.register(bus);
	}

	public void onClientSetup(FMLClientSetupEvent e) {
		ClientRegistry.bindTileEntityRenderer(Registrar.ALTAR_TILE.get(), TESRAltar::new);
	}
}
