package ca.teamdman.zensummoning.common.summoning;

import ca.teamdman.zensummoning.ZenSummoning;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.openzen.zencode.java.ZenCodeType;
import org.openzen.zencode.java.ZenCodeType.Getter;
import org.openzen.zencode.java.ZenCodeType.Setter;

@ZenRegister
@ZenCodeType.Name(ZenSummoning.ZEN_PACKAGE + ".SummoningAttempt")
public class SummoningAttempt {
	private final BlockPos pos;
	private final World   world;
	private       String   message = "chat.zensummoning.success";
	private       boolean  success = true;

	public SummoningAttempt(World world, BlockPos pos) {
		this.world = world;
		this.pos = pos;
	}

	@ZenCodeType.Getter("message")
	public String getMessage() {
		return message;
	}

	@Setter("message")
	public void setMessage(String message) {
		this.message = message;
	}

	@Getter("pos")
	public BlockPos getPos() {
		return pos;
	}

	@Getter("success")
	public boolean isSuccess() {
		return success;
	}

	@Setter("success")
	public void setSuccess(boolean success) {
		this.success = success;
	}

	@Getter("world")
	public World getWorld() {
		return world;
	}

}
