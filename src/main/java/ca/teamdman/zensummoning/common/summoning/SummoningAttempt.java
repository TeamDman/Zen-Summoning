package ca.teamdman.zensummoning.common.summoning;

import ca.teamdman.zensummoning.ZenSummoning;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.openzen.zencode.java.ZenCodeType;
import org.openzen.zencode.java.ZenCodeType.Getter;
import org.openzen.zencode.java.ZenCodeType.Setter;

import javax.annotation.Nullable;

@ZenRegister
@ZenCodeType.Name(ZenSummoning.ZEN_PACKAGE + ".SummoningAttempt")
@Document("mods/zensummoning/SummoningAttempt")
public class SummoningAttempt {
	private final BlockPos pos;
	private final World    world;
	private       String   message = "chat.zensummoning.success";
	private final ServerPlayerEntity summoner;
	private       boolean  success = true;

	public SummoningAttempt(World world, BlockPos pos, @Nullable ServerPlayerEntity summoner) {
		this.world = world;
		this.pos = pos;
		this.summoner = summoner;
	}

	/**
	 * The [unlocalized] message to be displayed to the player.
	 *
	 * @return self
	 */
	@ZenCodeType.Getter("message")
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the [unlocalized] message to be displayed to the player.
	 *
	 * @param message unlocalized string
	 * @docParam message "Can't summon in the rain!"
	 */
	@Setter("message")
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Position of the altar.
	 *
	 * @return altar position
	 */
	@Getter("pos")
	public BlockPos getPos() {
		return pos;
	}

	/**
	 * Gets the person who activated the summoning
	 *
	 * @return nullable player
	 */
	@Getter("summoner")
	@ZenCodeType.Nullable
	public PlayerEntity getSummoner() {
		return summoner;
	}

	/**
	 * Whether or not the summoning will proceed.
	 *
	 * @return proceed
	 */
	@Getter("success")
	public boolean isSuccess() {
		return success;
	}

	/**
	 * Sets if the summoning should proceed or not.
	 *
	 * @param success proceed
	 * @docParam success false
	 */
	@Setter("success")
	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * World that the summoning is occurring in.
	 * @return world
	 */
	@Getter("world")
	public World getWorld() {
		return world;
	}

}
