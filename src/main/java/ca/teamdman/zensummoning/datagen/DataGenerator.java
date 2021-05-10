package ca.teamdman.zensummoning.datagen;

import ca.teamdman.zensummoning.ZenSummoning;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DatagenModLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid= ZenSummoning.MOD_ID)
public class DataGenerator {

	@SubscribeEvent
	public static void onGather(GatherDataEvent e) {
		if (!DatagenModLoader.isRunningDataGen())
			return;

		net.minecraft.data.DataGenerator generator = e.getGenerator();
		if (e.includeServer()) {
			generator.addProvider(new LootTableProvider(generator));
		}
	}
}
