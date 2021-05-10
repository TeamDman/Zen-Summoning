package ca.teamdman.zensummoning.client;

import ca.teamdman.zensummoning.common.Proxy;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientProxy implements Proxy {


	@Override
	public void fillItemGroup(ItemGroup group, Item... items) {
		group.fill(Arrays.stream(items)
						 .map(ItemStack::new)
						 .collect(NonNullList::create, NonNullList::add, NonNullList::addAll));
	}

}
