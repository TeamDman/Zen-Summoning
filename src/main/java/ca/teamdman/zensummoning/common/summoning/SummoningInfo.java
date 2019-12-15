package ca.teamdman.zensummoning.common.summoning;

import ca.teamdman.zensummoning.ZenSummoning;
import ca.teamdman.zensummoning.common.Mutator;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IngredientUnknown;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ZenClass(ZenSummoning.ZEN_PACKAGE + ".SummoningInfo")
@ZenRegister
public class SummoningInfo {
	private IIngredient               catalyst        = IngredientUnknown.INSTANCE;
	private boolean                   consumeCatalyst = true;
	private List<MobInfo>             mobs            = new ArrayList<>();
	private Mutator<SummoningAttempt> mutator         = (__) -> {
	};
	private List<IIngredient>         reagents        = new ArrayList<>();
	private double weight = 1;

	private SummoningInfo() {
	}

	public static SummoningInfo fromNBT(NBTTagCompound compound) {
		SummoningInfo info = new SummoningInfo();
		NBTTagList    mobs = compound.getTagList("mobs", 10); // get NBTTagList<NBTTagCompound>
		for (int i = 0; i < mobs.tagCount(); i++) {
			NBTTagCompound mob = mobs.getCompoundTagAt(i);
			info.addMob(new MobInfo(
					mob.getCompoundTag("data"),
					new ResourceLocation(mob.getString("mob")),
					new BlockPos(mob.getInteger("x"), mob.getInteger("y"), mob.getInteger("z")),
					new BlockPos(mob.getInteger("dx"), mob.getInteger("dy"), mob.getInteger("dz"))
			));
		}
		return info;
	}

	@ZenMethod
	public SummoningInfo addMob(MobInfo info) {
		this.mobs.add(info);
		return this;
	}

	@ZenMethod
	public static SummoningInfo create() {
		return new SummoningInfo();
	}

	public double getWeight() {
		return weight;
	}

	@ZenMethod
	public void setWeight(double weight) {
		this.weight = weight;
	}

	public IIngredient getCatalyst() {
		return catalyst;
	}

	@ZenMethod
	public SummoningInfo setCatalyst(IIngredient ingredient) {
		this.catalyst = ingredient;
		return this;
	}

	@ZenMethod
	public SummoningInfo setConsumeCatalyst(boolean value) {
		this.consumeCatalyst = value;
		return this;
	}

	public boolean isCatalystConsumed() {
		return this.consumeCatalyst;
	}


	public List<MobInfo> getMobs() {
		return Collections.unmodifiableList(mobs);
	}

	public List<IIngredient> getReagents() {
		return Collections.unmodifiableList(reagents);
	}

	@ZenMethod
	public SummoningInfo setReagents(IIngredient[] reagents) {
		this.reagents.clear();
		Collections.addAll(this.reagents, reagents);
		return this;
	}

	public Mutator<SummoningAttempt> getMutator() {
		return mutator;
	}

	@ZenMethod
	public SummoningInfo setMutator(Mutator<SummoningAttempt> mutator) {
		this.mutator = mutator;
		return this;
	}

	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		NBTTagList     mobs     = new NBTTagList();
		for (MobInfo info : this.mobs) {
			NBTTagCompound mob = new NBTTagCompound();
			mob.setString("mob", info.getMob().toString());
			mob.setTag("data", info.getData());
			mob.setInteger("x", info.getOffset().getX());
			mob.setInteger("y", info.getOffset().getY());
			mob.setInteger("z", info.getOffset().getZ());
			mob.setInteger("dx", info.getSpread().getX());
			mob.setInteger("dy", info.getSpread().getY());
			mob.setInteger("dz", info.getSpread().getZ());
			mobs.appendTag(mob);
		}
		compound.setTag("mobs", mobs);
		return compound;
	}

}
