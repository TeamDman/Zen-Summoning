package ca.teamdman.zensummoning.common;

import ca.teamdman.zensummoning.ZenSummoning;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EventHandler {
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public static void onBlockRightClicked(PlayerInteractEvent.RightClickBlock event) {
		if (event.getWorld().getBlockState(event.getPos()).getBlock() == Registrar.Blocks.ALTAR) {
			ZenSummoning.log("onBlockRightClicked event found");
			event.setUseBlock(Event.Result.ALLOW);
			event.setUseItem(Event.Result.DENY);
			event.setCanceled(false);
		}
	}
}
