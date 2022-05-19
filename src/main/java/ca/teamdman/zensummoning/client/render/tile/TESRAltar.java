package ca.teamdman.zensummoning.client.render.tile;

import ca.teamdman.zensummoning.common.tiles.TileAltar;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;

public class TESRAltar implements BlockEntityRenderer<TileAltar> {
	public TESRAltar(BlockEntityRendererProvider.Context context) {

	}

	@Override
	public void render(TileAltar te, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		ImmutableList<ItemStack> stacks = te.getClientStacks();
		if (stacks == null || stacks.isEmpty()) {
			return;
		}

		int   count  = stacks.size();
		float scale = 1 - 1f / te.TIME_TO_SPAWN * (te.renderTick + partialTicks);
		matrixStack.pushPose();
		//		matrixStack.translate(te.getPos().getX(), te.getPos().getY(), te.getPos().getZ());
		matrixStack.translate(0.5,0,0.5);
		Vector3f verticalAxis = new Vector3f(0, 1, 0); // axis to rotate around
		Vector3f depthAxis    = new Vector3f(1,0,0);
		matrixStack.mulPose(verticalAxis.rotation(te.getLevel().getGameTime()/100f));
		for (ItemStack stack : stacks) {
			matrixStack.mulPose(verticalAxis.rotationDegrees(360f / count));
			if (te.isSummoning()) {
				matrixStack.translate(0, 1.2 * (1 - scale), 0);
			}
			matrixStack.pushPose();
			matrixStack.translate(1 + count / 15f, 0, 0);
			matrixStack.mulPose(verticalAxis.rotationDegrees(90));
			matrixStack.mulPose(depthAxis.rotationDegrees(-90));
			matrixStack.scale(scale, scale, scale);
			Minecraft.getInstance()
					.getItemRenderer()
					.renderStatic(stack, ItemTransforms.TransformType.GROUND, combinedLightIn, combinedOverlayIn, matrixStack, bufferIn,
								  (int) te.getBlockPos().asLong());
//					.renderItem(stack, ItemCameraTransforms.TransformType.GROUND, combinedLightIn, combinedOverlayIn, matrixStack, bufferIn);
			matrixStack.popPose();
		}

		matrixStack.popPose();
	}
}
