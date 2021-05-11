package ca.teamdman.zensummoning.common.summoning;

import ca.teamdman.zensummoning.ZenSummoning;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.data.IData;
import com.blamejared.crafttweaker.impl.entity.MCEntityType;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.openzen.zencode.java.ZenCodeType;
import org.openzen.zencode.java.ZenCodeType.Method;

@ZenCodeType.Name(ZenSummoning.ZEN_PACKAGE + ".MobInfo")
@ZenRegister
@Document("mods/zensummoning/MobInfo")
public class MobInfo {
	private int          count  = 1;
	private CompoundNBT  data   = new CompoundNBT();
	private MCEntityType mob    = null;
	private BlockPos     offset = new BlockPos(0.5, 0, 0.5);
	private BlockPos     spread = new BlockPos(0, 0, 0);

	@ZenCodeType.Constructor
	public MobInfo() {
	}

	MobInfo(CompoundNBT data, MCEntityType mob, BlockPos offset, BlockPos spread) {
		this.data = data;
		this.mob = mob;
		this.offset = offset;
		this.spread = spread;
	}

	/**
	 * Creates a new MobInfo with default values.
	 * See other methods for adding more customization.
	 *
	 * Same as constructor.
	 *
	 * @return new MobInfo
	 */
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

	/**
	 * Sets the quantity of the mob to be spawned.
	 *
	 * @param count quantity
	 * @return self
	 * @docParam count 12
	 */
	@Method
	public MobInfo setCount(int count) {
		this.count = count;
		return this;
	}

	public CompoundNBT getData() {
		return data;
	}

	/**
	 * Sets the NBT data of the mobs to be spawned.
	 *
	 * @param data NBT
	 * @return self
	 * @docParam data {
	 * "Health":200,
	 * "Attributes":[
	 * {"Name":"generic.maxHealth", "Base":200},
	 * {"Name":"generic.movementSpeed", "Base":0.3},
	 * {"Name":"generic.attackDamage", "Base":6}
	 * ],
	 * "CustomName":"A Lost Soul",
	 * "PersistenceRequired":1,
	 * "CustomNameVisible":1
	 * }
	 */
	@Method
	public MobInfo setData(IData data) {
		this.data = (CompoundNBT) data.getInternal();
		return this;
	}

	public ResourceLocation getMobId() {
		return mob.getInternal()
				.getRegistryName();
	}

	public EntityType<?> getEntityType() {
		return mob.getInternal();
	}

	/**
	 * Sets the mob to be spawned.
	 *
	 * @param mob resource location
	 * @return self
	 * @docParam mob "minecraft:zombie_villager"
	 */
	@Method
	public MobInfo setMob(MCEntityType mob) {
		this.mob = mob;
		return this;
	}

	public BlockPos getOffset() {
		return offset;
	}

	/**
	 * Sets the offset from the altar where the mobs will be spawned.
	 *
	 * @param x x
	 * @param y y
	 * @param z z
	 * @return self
	 * @docParam x 1
	 * @docParam y 3
	 * @docParam z 1
	 */
	@Method
	public MobInfo setOffset(int x, int y, int z) {
		this.offset = new BlockPos(x, y, z);
		return this;
	}

	/**
	 * Sets the random spread for spawning the mob.
	 * Values shouldn't be negative.
	 *
	 * @param x x spread
	 * @param y y spread
	 * @param z z spread
	 * @return self
	 * @docParam x 3
	 * @docParam y 3
	 * @docParam z 3
	 */
	@Method
	public MobInfo setSpread(int x, int y, int z) {
		if (x < 0 || y < 0 || z < 0)
			System.out.println("Spread operates as a distance factor on each x,y,z plane, distances shouldn't be negative.");
		this.spread = new BlockPos(Math.abs(x), Math.abs(y), Math.abs(z));
		return this;
	}
}
