package ca.teamdman.zensummoning.common.summoning;

import ca.teamdman.zensummoning.ZenSummoning;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredientWithAmount;
import com.blamejared.crafttweaker.impl.entity.MCEntityType;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ForgeRegistries;
import org.openzen.zencode.java.ZenCodeType;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

@ZenCodeType.Name(ZenSummoning.ZEN_PACKAGE + ".SummoningInfo")
@ZenRegister
@Document("mods/zensummoning/SummoningInfo")
public class SummoningInfo {
	//	private IIngredientWithAmount               catalyst        = IngredientUnknown.INSTANCE;
	private IIngredientWithAmount       catalyst        = null;
	private boolean                     consumeCatalyst = true;
	private List<MobInfo>               mobs            = new ArrayList<>();
	private Consumer<SummoningAttempt>  mutator         = (__) -> {
	};
	private List<IIngredientWithAmount> reagents        = new ArrayList<>();
	private double                      weight          = 1;
	private Map<Predicate<SummoningAttempt>, String> conditions = new LinkedHashMap<>();

	@ZenCodeType.Constructor
	public SummoningInfo() {
	}

	public Optional<String> getFailedConditionErrorMessage(SummoningAttempt attempt) {
		return conditions.entrySet().stream()
				.filter(e -> !e.getKey().test(attempt))
				.findFirst()
				.map(Map.Entry::getValue);
	}

	public static SummoningInfo fromNBT(CompoundNBT compound) {
		SummoningInfo info = new SummoningInfo();
		ListNBT       mobs = compound.getList("mobs", 10); // get ListNBT<CompoundNBT>
		for (int i = 0; i < mobs.size(); i++) {
			CompoundNBT mob = mobs.getCompound(i);
			info.addMob(new MobInfo(mob.getCompound("data"),
									new MCEntityType(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(mob.getString("mob")))),
									new BlockPos(mob.getInt("x"), mob.getInt("y"), mob.getInt("z")),
									new BlockPos(mob.getInt("dx"), mob.getInt("dy"), mob.getInt("dz"))));
		}
		return info;
	}

	/**
	 * Adds a new mob to the summoning.
	 * Returns itself for builder pattern.
	 *
	 * @param info mob to add
	 * @return self SummoningInfo for builder pattern
	 * @docParam info MobInfo.create().setMob("minecraft:zombie")
	 */
	@ZenCodeType.Method
	public SummoningInfo addMob(MobInfo info) {
		this.mobs.add(info);
		return this;
	}

	/**
	 * Creates a new SummoningInfo with default values.
	 * See other methods for adding more customization.
	 *
	 * Same as constructor.
	 *
	 * @return new info
	 */
	@ZenCodeType.Method
	public static SummoningInfo create() {
		return new SummoningInfo();
	}

	public double getWeight() {
		return weight;
	}

	/**
	 * Sets the weight that this summoning has.
	 * Summonings with the same catalyst and reagents are determined randomly using this.
	 *
	 * @param weight
	 * @return self
	 * @docParam weight 3
	 */
	@ZenCodeType.Method
	public SummoningInfo setWeight(double weight) {
		this.weight = weight;
		return this;
	}

	/**
	 * Adds an additional condition for the summoning to work.
	 * This can be used to require a gamestage (or deny one I guess)
	 *
	 * @param condition condition
	 * @param failureMessage chat message on failure
	 * @return self
	 * @docParam condition Predicate for summoning to succeed
	 * @docParam failureMessage Chat message to show on failure
	 */
	@ZenCodeType.Method
	public SummoningInfo addCondition(Predicate<SummoningAttempt> condition, String failureMessage) {
		conditions.put(condition, failureMessage);
		return this;
	}

	public IIngredientWithAmount getCatalyst() {
		return catalyst;
	}

	/**
	 * Sets the catalyst that will be used to start the ritual.
	 *
	 * @param ingredient catalyst
	 * @return self
	 * @docParam ingredient <item:minecraft:stick> * 2
	 */
	@ZenCodeType.Method
	public SummoningInfo setCatalyst(IIngredientWithAmount ingredient) {
		this.catalyst = ingredient;
		return this;
	}

	/**
	 * Determines whether or not the catalyst will be consumed.
	 *
	 * @param value consumed
	 * @return self
	 * @docParam value false
	 */
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

	/**
	 * Sets the ingredients to be required to start the summon.
	 *
	 * @param reagents ingredient list
	 * @return self
	 * @docParam reagents [<item:minecraft:stone>, <item:minecraft:egg>*12]
	 */
	@ZenCodeType.Method
	public SummoningInfo setReagents(IIngredientWithAmount[] reagents) {
		this.reagents.clear();
		Collections.addAll(this.reagents, reagents);
		return this;
	}

	public Consumer<SummoningAttempt> getMutator() {
		return mutator;
	}

	/**
	 * Custom callback to determine if a summon should be allowed.
	 *
	 * @param mutator callback
	 * @return self
	 * @docParam mutator (attempt as SummoningAttempt) => {
	 * if (attempt.world.raining) {
	 * attempt.success = false;
	 * attempt.message = "Can't summon this in the rain!";
	 * } else {
	 * attempt.message = "Good Luck!";
	 * }
	 * }
	 */
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
