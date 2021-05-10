package ca.teamdman.zensummoning.client.render.tile;

import ca.teamdman.zensummoning.common.tiles.TileAltar;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

public class TESRAltar extends TileEntityRenderer<TileAltar> {
	public TESRAltar(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(TileAltar te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		ImmutableList<ItemStack> stacks = te.getClientStacks();
		if (stacks == null || stacks.isEmpty()) {
			return;
		}

		int   dist  = stacks.size() - 1;
		float scale = 1 - 1f / te.TIME_TO_SPAWN * (te.renderTick + partialTicks);
		matrixStack.push();
		matrixStack.translate(0.5,0,0.5);
		Vector3f verticalAxis = new Vector3f(0,1,0); // axis to rotate around
		Vector3f depthAxis = new Vector3f(1,0,0);
		matrixStack.rotate(verticalAxis.rotation(te.getWorld().getGameTime()/100f));
		for (ItemStack stack : stacks) {
			matrixStack.rotate(verticalAxis.rotation(360f / dist));
			if (te.isSummoning()) {
				matrixStack.translate(0, 1.2 * (1 - scale), 0);
			}
			matrixStack.push();
			matrixStack.translate(1 + dist / 15f, 0, 0);
			matrixStack.rotate(verticalAxis.rotationDegrees(90));
			matrixStack.rotate(depthAxis.rotationDegrees(-90));
			matrixStack.scale(scale, scale, scale);
			Minecraft.getInstance()
					 .getItemRenderer()
					 .renderItem(stack, ItemCameraTransforms.TransformType.GROUND, combinedLightIn, combinedOverlayIn, matrixStack, bufferIn);
			matrixStack.pop();
		}

		matrixStack.pop();
	}
}
