package ca.teamdman.zensummoning.common;

import ca.teamdman.zensummoning.client.render.tile.TESRAltar;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
class EventHandler {
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public static void onBlockRightClicked(PlayerInteractEvent.RightClickBlock event) {
		if (event.getWorld()
				 .getBlockState(event.getPos())
				 .getBlock() == Registrar.ALTAR_BLOCK.get()) {
			event.setUseBlock(Event.Result.ALLOW);
			event.setUseItem(Event.Result.DENY);
			event.setCanceled(false);
		}
	}

	@SubscribeEvent
	public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(Registrar.ALTAR_TILE.get(), TESRAltar::new);
	}
}
