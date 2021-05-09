package ca.teamdman.zensummoning.client.render.tile;

import ca.teamdman.zensummoning.common.tiles.TileAltar;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;

public class TESRAltar extends TileEntityRenderer<TileAltar> {
	public TESRAltar(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(TileAltar te, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		ImmutableList<ItemStack> stacks = te.getClientStacks();
		if (stacks != null && !stacks.isEmpty()) {
			int   dist  = stacks.size();
			float scale = 1 - 1f / te.TIME_TO_SPAWN * (te.renderTick + partialTicks);
			GlStateManager.pushMatrix();
			GlStateManager.translated(te.getPos()
										.getX() + 0.5,
									  te.getPos()
										.getY() + 0.1,
									  te.getPos()
										.getZ() + 0.5);
			GlStateManager.rotatef((int) te.getWorld()
										   .getGameTime(), 0, 1, 0);
			for (ItemStack stack : stacks) {
				GlStateManager.rotatef(360f / dist, 0, 1, 0);
				if (te.isSummoning()) {
					GlStateManager.translated(0, 1.2 * (1 - scale), 0);
				}
				GlStateManager.pushMatrix();
				GlStateManager.translatef(1 + dist / 15f, 0, 0);
				GlStateManager.rotatef(90, 1, 0, 0);
				GlStateManager.rotatef(-90, 0, 0, 1);
				GlStateManager.scalef(scale, scale, scale);
				Minecraft.getInstance()
						 .getItemRenderer()
						 .renderItem(stack, ItemCameraTransforms.TransformType.GROUND, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
				GlStateManager.popMatrix();
			}

			GlStateManager.popMatrix();
		}
	}
}
