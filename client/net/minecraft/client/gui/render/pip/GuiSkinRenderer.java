package net.minecraft.client.gui.render.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.state.pip.GuiSkinRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix4fStack;
import org.joml.Quaternionfc;

public class GuiSkinRenderer extends PictureInPictureRenderer<GuiSkinRenderState> {
   public GuiSkinRenderer(MultiBufferSource.BufferSource var1) {
      super(var1);
   }

   public Class<GuiSkinRenderState> getRenderStateClass() {
      return GuiSkinRenderState.class;
   }

   protected void renderToTexture(GuiSkinRenderState var1, PoseStack var2) {
      Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.PLAYER_SKIN);
      int var3 = Minecraft.getInstance().getWindow().getGuiScale();
      Matrix4fStack var4 = RenderSystem.getModelViewStack();
      var4.pushMatrix();
      float var5 = var1.scale() * (float)var3;
      var4.rotateAround(Axis.XP.rotationDegrees(var1.rotationX()), 0.0F, var5 * -var1.pivotY(), 0.0F);
      var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-var1.rotationY()));
      var2.translate(0.0F, -1.6010001F, 0.0F);
      RenderType var6 = var1.playerModel().renderType(var1.texture());
      var1.playerModel().renderToBuffer(var2, this.bufferSource.getBuffer(var6), 15728880, OverlayTexture.NO_OVERLAY);
      this.bufferSource.endBatch();
      var4.popMatrix();
   }

   protected String getTextureLabel() {
      return "player skin";
   }
}
