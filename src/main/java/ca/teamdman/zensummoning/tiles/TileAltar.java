package ca.teamdman.zensummoning.tiles;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

public class TileAltar extends TileEntity implements ITickable {
	private final AxisAlignedBB succ = new AxisAlignedBB(-2,-2,-2,2,2,2).offset(this.pos);
	private final ItemStackHandler inventory = new ItemStackHandler(64);


	@Override
	public void update() {
		if (this.world != null && !this.world.isRemote && this.world.getTotalWorldTime() % 20 == 0) {
			List<EntityItem> drops = this.world.getEntitiesWithinAABB(EntityItem.class, succ, EntitySelectors.IS_ALIVE);
			drops.forEach(System.out::println);
		}
	}
}
