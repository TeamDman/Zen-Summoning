package ca.teamdman.zensummoning.common.summoning;

import ca.teamdman.zensummoning.ZenSummoning;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.openzen.zencode.java.ZenCodeType;
import org.openzen.zencode.java.ZenCodeType.Getter;
import org.openzen.zencode.java.ZenCodeType.Setter;

import javax.annotation.Nullable;

@ZenRegister
@ZenCodeType.Name(ZenSummoning.ZEN_PACKAGE + ".SummoningAttempt")
@Document("mods/zensummoning/SummoningAttempt")
public class SummoningAttempt {
	private final BlockPos pos;
	private final Level    level;
	private       String   message = "chat.zensummoning.success";
	private final ServerPlayer summoner;
	private       boolean  success = true;

	public SummoningAttempt(Level level, BlockPos pos, @Nullable ServerPlayer summoner) {
		this.level = level;
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
	public Player getSummoner() {
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
	 * Level that the summoning is occurring in.
	 * @return world
	 */
	@Getter("world")
	public Level getLevel() {
		return level;
	}

}
