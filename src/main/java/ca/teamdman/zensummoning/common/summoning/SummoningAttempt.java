package ca.teamdman.zensummoning.common.summoning;

import ca.teamdman.zensummoning.ZenSummoning;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenRegister
@ZenClass(ZenSummoning.ZEN_PACKAGE + ".SummoningAttempt")
public class SummoningAttempt {
	private       String    message = "chat.zensummoning.success";
	private final IBlockPos pos;
	private       boolean   success = true;
	private final IWorld    world;

	public SummoningAttempt(IWorld world, IBlockPos pos) {
		this.world = world;
		this.pos = pos;
	}

	@ZenGetter("message")
	public String getMessage() {
		return message;
	}

	@ZenSetter("message")
	public void setMessage(String message) {
		this.message = message;
	}

	@ZenGetter("pos")
	public IBlockPos getPos() {
		return pos;
	}

	@ZenGetter("success")
	public boolean isSuccess() {
		return success;
	}

	@ZenSetter("success")
	public void setSuccess(boolean success) {
		this.success = success;
	}

	@ZenGetter("world")
	public IWorld getWorld() {
		return world;
	}

}
