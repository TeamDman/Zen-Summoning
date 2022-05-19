package ca.teamdman.zensummoning.common;

import ca.teamdman.zensummoning.ZenSummoning;
import ca.teamdman.zensummoning.client.render.tile.TESRAltar;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ZenSummoning.MOD_ID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class ModEventHandlers {
	@SubscribeEvent
	public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(Registrar.ALTAR_TILE.get(), TESRAltar::new);
	}
}
