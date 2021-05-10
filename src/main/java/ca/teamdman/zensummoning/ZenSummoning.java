package ca.teamdman.zensummoning;

import ca.teamdman.zensummoning.client.ClientProxy;
import ca.teamdman.zensummoning.common.Proxy;
import ca.teamdman.zensummoning.common.ServerProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

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
	}

}
