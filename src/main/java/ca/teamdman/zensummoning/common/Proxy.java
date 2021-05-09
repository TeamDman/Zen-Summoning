package ca.teamdman.zensummoning.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public interface Proxy {
	default void fillItemGroup(ItemGroup group, Item... items) {};
}
