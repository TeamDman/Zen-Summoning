package ca.teamdman.zensummoning.common.summoning;

import ca.teamdman.zensummoning.ZenSummoning;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.mc1120.data.NBTConverter;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Random;

@ZenClass(ZenSummoning.ZEN_PACKAGE + ".MobInfo")
@ZenRegister
public class MobInfo {
	private int              count  = 1;
	private NBTTagCompound   data   = new NBTTagCompound();
	private ResourceLocation mob    = new ResourceLocation("");
	private BlockPos         offset = new BlockPos(0.5, 0, 0.5);
	private BlockPos         spread = new BlockPos(0, 0, 0);

	private MobInfo() {
	}

	MobInfo(NBTTagCompound data, ResourceLocation mob, BlockPos offset, BlockPos spread) {
		this.data = data;
		this.mob = mob;
		this.offset = offset;
		this.spread = spread;
	}

	private void validateEgg() {
		if (!EntityList.ENTITY_EGGS.containsKey(mob)) {
			Random                   r     = new Random(mob.hashCode());
			EntityList.EntityEggInfo egg   = new EntityList.EntityEggInfo(mob, r.nextInt(0xFFFFFF), r.nextInt(0xFFFFFF));
			EntityEntry              entry = ForgeRegistries.ENTITIES.getValue(mob);
			if (entry != null)
				entry.setEgg(egg);
			EntityList.ENTITY_EGGS.put(mob, egg);
		}
	}

	@ZenMethod
	public static MobInfo create() {
		return new MobInfo();
	}

	public BlockPos getSpread() {
		return spread;
	}

	public int getCount() {
		return count;
	}

	@ZenMethod
	public MobInfo setCount(int count) {
		this.count = count;
		return this;
	}

	public NBTTagCompound getData() {
		return data;
	}

	@ZenMethod
	public MobInfo setData(IData data) {
		this.data = (NBTTagCompound) NBTConverter.from(data);
		return this;
	}

	public ResourceLocation getMob() {
		return mob;
	}

	@ZenMethod
	public MobInfo setMob(String mob) {
		this.mob = new ResourceLocation(mob);
		validateEgg();
		return this;
	}

	public BlockPos getOffset() {
		return offset;
	}

	@ZenMethod
	public MobInfo setOffset(int x, int y, int z) {
		this.offset = new BlockPos(x, y, z);
		return this;
	}

	@ZenMethod
	public MobInfo setSpread(int x, int y, int z) {
		if (x < 0 || y < 0 || z < 0)
			System.out.println("Spread operates as a distance factor on each x,y,z plane, distances shouldn't be negative.");
		this.spread = new BlockPos(Math.abs(x), Math.abs(y), Math.abs(z));
		return this;
	}
}
