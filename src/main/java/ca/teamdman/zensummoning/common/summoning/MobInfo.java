package ca.teamdman.zensummoning.common.summoning;

import ca.teamdman.zensummoning.ZenSummoning;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.data.IData;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ForgeRegistries;
import org.openzen.zencode.java.ZenCodeType;
import org.openzen.zencode.java.ZenCodeType.Method;

@ZenCodeType.Name(ZenSummoning.ZEN_PACKAGE + ".MobInfo")
@ZenRegister
public class MobInfo {
	private int              count  = 1;
	private CompoundNBT      data   = new CompoundNBT();
	private ResourceLocation mob    = new ResourceLocation("");
	private BlockPos         offset = new BlockPos(0.5, 0, 0.5);
	private BlockPos         spread = new BlockPos(0, 0, 0);

	private MobInfo() {
	}

	MobInfo(CompoundNBT data, ResourceLocation mob, BlockPos offset, BlockPos spread) {
		this.data = data;
		this.mob = mob;
		this.offset = offset;
		this.spread = spread;
	}

	@Method
	public static MobInfo create() {
		return new MobInfo();
	}

	public BlockPos getSpread() {
		return spread;
	}

	public int getCount() {
		return count;
	}

	@Method
	public MobInfo setCount(int count) {
		this.count = count;
		return this;
	}

	public CompoundNBT getData() {
		return data;
	}

	@Method
	public MobInfo setData(IData data) {
		this.data = (CompoundNBT) data.getInternal();
		return this;
	}

	public ResourceLocation getMobId() {
		return mob;
	}

	public EntityType<?> getEntityType() {
		return ForgeRegistries.ENTITIES.getValue(mob);
	}

	@Method
	public MobInfo setMob(String mob) {
		this.mob = new ResourceLocation(mob);
		return this;
	}

	public BlockPos getOffset() {
		return offset;
	}

	@Method
	public MobInfo setOffset(int x, int y, int z) {
		this.offset = new BlockPos(x, y, z);
		return this;
	}

	@Method
	public MobInfo setSpread(int x, int y, int z) {
		if (x < 0 || y < 0 || z < 0)
			System.out.println("Spread operates as a distance factor on each x,y,z plane, distances shouldn't be negative.");
		this.spread = new BlockPos(Math.abs(x), Math.abs(y), Math.abs(z));
		return this;
	}
}
