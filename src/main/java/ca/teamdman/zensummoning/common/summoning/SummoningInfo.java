package ca.teamdman.zensummoning.common.summoning;

import ca.teamdman.zensummoning.ZenSummoning;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredientWithAmount;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.mojang.math.Vector3f;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
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
	private IIngredientWithAmount    catalyst   = null;
	private List<SummoningCondition> conditions = new LinkedList<>();
	private boolean                     consumeCatalyst = true;
	private List<MobInfo>               mobs            = new ArrayList<>();
	private Consumer<SummoningAttempt>  mutator         = (__) -> {
	};
	private List<IIngredientWithAmount> reagents        = new ArrayList<>();
	private String                      sound           = SoundEvents.EVOKER_PREPARE_WOLOLO.getRegistryName()
			.toString();
	private double                      weight          = 1;

	@ZenCodeType.Constructor
	public SummoningInfo() {
	}

	public static SummoningInfo fromNBT(CompoundTag compound) {
		SummoningInfo info = new SummoningInfo();
		ListTag       mobs = compound.getList("mobs", 10); // get ListNBT<CompoundTag>
		for (int i = 0; i < mobs.size(); i++) {
			CompoundTag mob = mobs.getCompound(i);
			info.addMob(new MobInfo(mob.getCompound("data"),
									ForgeRegistries.ENTITIES.getValue(new ResourceLocation(mob.getString("mob"))),
									new Vector3f(mob.getFloat("x"), mob.getFloat("y"), mob.getFloat("z")),
									new Vector3f(mob.getFloat("dx"), mob.getFloat("dy"), mob.getFloat("dz"))));
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
	 * <p>
	 * Same as constructor.
	 *
	 * @return new info
	 */
	@ZenCodeType.Method
	public static SummoningInfo create() {
		return new SummoningInfo();
	}

	public String getSound() {
		return sound;
	}

	/**
	 * Sets the sound played when a summon completes.
	 *
	 * @param sound sound resource location
	 * @return self
	 * @docParam sound "entity.evoker.prepare_wololo"
	 */
	@ZenCodeType.Method
	public SummoningInfo setSound(String sound) {
		this.sound = sound;
		return this;
	}

	public List<SummoningCondition> getConditions() {
		return conditions;
	}

	public Optional<String> getFailedConditionErrorMessage(SummoningAttempt attempt) {
		return conditions.stream()
				.filter(condition -> !condition.PREDICATE.test(attempt))
				.findFirst()
				.map(condition -> condition.FAILURE_MESSAGE);
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
	 * @param condition      condition
	 * @param failureMessage chat message on failure
	 * @return self
	 * @docParam condition Predicate for summoning to succeed
	 * @docParam failureMessage Chat message to show on failure
	 * @docParam jeiDescription Line to show in JEI preview
	 */
	@ZenCodeType.Method
	public SummoningInfo addCondition(Predicate<SummoningAttempt> condition, String failureMessage, String jeiDescription) {
		conditions.add(new SummoningCondition(condition, failureMessage, jeiDescription));
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

	public CompoundTag serializeNBT() {
		CompoundTag compound = new CompoundTag();
		ListTag     mobs     = new ListTag();
		for (MobInfo info : this.mobs) {
			CompoundTag mob = new CompoundTag();
			mob.putString("mob",
						  info.getMobId()
								  .toString());
			mob.put("data", info.getData());
			mob.putFloat("x",
					   info.getOffset()
							   .x());
			mob.putFloat("y",
					   info.getOffset()
							   .y());
			mob.putFloat("z",
					   info.getOffset()
							   .z());
			mob.putFloat("dx",
					   info.getSpread()
							   .x());
			mob.putFloat("dy",
					   info.getSpread()
							   .y());
			mob.putFloat("dz",
					   info.getSpread()
							   .z());
			mobs.add(mob);
		}
		compound.put("mobs", mobs);
		return compound;
	}

	public class SummoningCondition {
		public final String                      FAILURE_MESSAGE;
		public final String                      JEI_DESCRIPTION;
		public final Predicate<SummoningAttempt> PREDICATE;

		public SummoningCondition(Predicate<SummoningAttempt> PREDICATE, String FAILURE_MESSAGE, String JEI_DESCRIPTION) {
			this.PREDICATE = PREDICATE;
			this.FAILURE_MESSAGE = FAILURE_MESSAGE;
			this.JEI_DESCRIPTION = JEI_DESCRIPTION;
		}
	}

}
