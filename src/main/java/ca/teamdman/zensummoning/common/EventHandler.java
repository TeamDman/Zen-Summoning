package ca.teamdman.zensummoning.common;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EventHandler {
	@SubscribeEvent
	public static void onBlockRightClicked(PlayerInteractEvent.RightClickBlock event) {
		//noinspection ConstantConditions // registrar set to final null but forge is spicy
		if (event.getWorld().getBlockState(event.getPos()).getBlock() == Registrar.Blocks.ALTAR) {
			event.setUseBlock(Event.Result.ALLOW);
		}
	}
}
