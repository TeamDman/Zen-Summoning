package ca.teamdman.zensummoning.common.summoning;

import ca.teamdman.zensummoning.ZenSummoning;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredientWithAmount;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.openzen.zencode.java.ZenCodeType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@ZenCodeType.Name(ZenSummoning.ZEN_PACKAGE + ".SummoningInfo")
@ZenRegister
public class SummoningInfo {
	//	private IIngredientWithAmount               catalyst        = IngredientUnknown.INSTANCE;
	private IIngredientWithAmount       catalyst        = null;
	private boolean                     consumeCatalyst = true;
	private List<MobInfo>               mobs            = new ArrayList<>();
	private Consumer<SummoningAttempt>  mutator         = (__) -> {
	};
	private List<IIngredientWithAmount> reagents        = new ArrayList<>();
	private double                      weight          = 1;

	private SummoningInfo() {
	}

	public static SummoningInfo fromNBT(CompoundNBT compound) {
		SummoningInfo info = new SummoningInfo();
		ListNBT       mobs = compound.getList("mobs", 10); // get ListNBT<CompoundNBT>
		for (int i = 0; i < mobs.size(); i++) {
			CompoundNBT mob = mobs.getCompound(i);
			info.addMob(new MobInfo(mob.getCompound("data"), new ResourceLocation(mob.getString("mob")), new BlockPos(mob.getInt("x"), mob.getInt("y"), mob.getInt("z")), new BlockPos(mob.getInt("dx"), mob.getInt("dy"), mob.getInt("dz"))));
		}
		return info;
	}

	@ZenCodeType.Method
	public SummoningInfo addMob(MobInfo info) {
		this.mobs.add(info);
		return this;
	}

	@ZenCodeType.Method
	public static SummoningInfo create() {
		return new SummoningInfo();
	}

	public double getWeight() {
		return weight;
	}

	@ZenCodeType.Method
	public SummoningInfo setWeight(double weight) {
		this.weight = weight;
		return this;
	}

	public IIngredientWithAmount getCatalyst() {
		return catalyst;
	}

	@ZenCodeType.Method
	public SummoningInfo setCatalyst(IIngredientWithAmount ingredient) {
		this.catalyst = ingredient;
		return this;
	}

	@ZenCodeType.Method
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

	public List<IIngredientWithAmount> getReagents() {
		return Collections.unmodifiableList(reagents);
	}

	@ZenCodeType.Method
	public SummoningInfo setReagents(IIngredientWithAmount[] reagents) {
		this.reagents.clear();
		Collections.addAll(this.reagents, reagents);
		return this;
	}

	public Consumer<SummoningAttempt> getMutator() {
		return mutator;
	}

	@ZenCodeType.Method
	public SummoningInfo setMutator(Consumer<SummoningAttempt> mutator) {
		this.mutator = mutator;
		return this;
	}

	public CompoundNBT serializeNBT() {
		CompoundNBT compound = new CompoundNBT();
		ListNBT     mobs     = new ListNBT();
		for (MobInfo info : this.mobs) {
			CompoundNBT mob = new CompoundNBT();
			mob.putString("mob",
						  info.getMobId()
							  .toString());
			mob.put("data", info.getData());
			mob.putInt("x",
					   info.getOffset()
						   .getX());
			mob.putInt("y",
					   info.getOffset()
						   .getY());
			mob.putInt("z",
					   info.getOffset()
						   .getZ());
			mob.putInt("dx",
					   info.getSpread()
						   .getX());
			mob.putInt("dy",
					   info.getSpread()
						   .getY());
			mob.putInt("dz",
					   info.getSpread()
						   .getZ());
			mobs.add(mob);
		}
		compound.put("mobs", mobs);
		return compound;
	}

}
