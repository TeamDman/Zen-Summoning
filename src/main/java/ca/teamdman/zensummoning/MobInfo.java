package ca.teamdman.zensummoning;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.mc1120.data.NBTConverter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass(ZenSummoning.ZEN_PACKAGE + ".MobInfo")
@ZenRegister
public class MobInfo {
	private int              count  = 0;
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
		this.spread = new BlockPos(x, y, z);
		return this;
	}
}
