package ca.teamdman.zensummoning.client.render.tile;

import ca.teamdman.zensummoning.common.tiles.TileAltar;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;

public class TESRAltar extends TileEntitySpecialRenderer<TileAltar> {

	@Override
	public void render(TileAltar te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		super.render(te, x, y, z, partialTicks, destroyStage, alpha);

		ImmutableList<ItemStack> stacks = te.getClientStacks();
		if (stacks != null && !stacks.isEmpty()) {
			int     dist       = stacks.size();
			float   scale      = 1 - 1f / te.TIME_TO_SPAWN * (te.renderTick + partialTicks);
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5, y + 0.1, z + 0.5);
			GlStateManager.rotate((int) getWorld().getTotalWorldTime(), 0, 1, 0);
			for (ItemStack stack : stacks) {
				GlStateManager.rotate(360f / dist, 0, 1, 0);
				if (te.isSpawning()) {
					GlStateManager.translate(0, 0.3f / te.TIME_TO_SPAWN * (te.renderTick + partialTicks), 0);
				}
				GlStateManager.pushMatrix();
				GlStateManager.translate(1 + dist / 15f, 0, 0);
				GlStateManager.rotate(90, 1, 0, 0);
				GlStateManager.rotate(-90, 0, 0, 1);
				GlStateManager.scale(scale,scale,scale);
				Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
				GlStateManager.popMatrix();
			}

			GlStateManager.popMatrix();
		}

	}

}
