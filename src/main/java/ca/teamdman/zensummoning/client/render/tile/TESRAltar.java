package ca.teamdman.zensummoning.client.render.tile;

import ca.teamdman.zensummoning.common.tiles.TileAltar;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class TESRAltar extends TileEntitySpecialRenderer<TileAltar> {
	private ImmutableList<ItemStack> stacks;

	@Override
	public void render(TileAltar te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y+1.5, z);
		GlStateManager.scale(0.1,0.1,0.1);

		super.render(te, x, y, z, partialTicks, destroyStage, alpha);

		if (te.renderTick++ % 20 == 0) {
			stacks = te.getStacks();
		}
		if (stacks != null && !stacks.isEmpty()) {
			RenderHelper.enableStandardItemLighting();
			for (ItemStack stack : stacks) {

				Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
			}
			RenderHelper.disableStandardItemLighting();
		}
	}

}
