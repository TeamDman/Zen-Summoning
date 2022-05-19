package ca.teamdman.zensummoning.common;

import ca.teamdman.zensummoning.ZenSummoning;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ZenSummoning.MOD_ID, bus= Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandlers {
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
}
