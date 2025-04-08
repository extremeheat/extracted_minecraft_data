package net.minecraft.client.gui.render.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.render.state.pip.GuiSkinRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Quaternionfc;

public class GuiSkinRenderer extends PictureInPictureRenderer<GuiSkinRenderState> {
   public GuiSkinRenderer(MultiBufferSource.BufferSource var1) {
      super(var1);
   }

   public Class<GuiSkinRenderState> getRenderStateClass() {
      return GuiSkinRenderState.class;
   }

   protected void renderToTexture(GuiSkinRenderState var1, PoseStack var2) {
      Lighting.setupForEntityInInventory(Axis.XP.rotationDegrees(var1.rotationX()));
      var2.translate(0.0F, -0.0625F, 0.0F);
      var2.rotateAround(Axis.XP.rotationDegrees(-var1.rotationX()), 0.0F, var1.pivotY(), 0.0F);
      var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-var1.rotationY()));
      var2.translate(0.0F, -1.501F, 0.0F);
      RenderType var3 = var1.playerModel().renderType(var1.texture());
      var1.playerModel().renderToBuffer(var2, this.bufferSource.getBuffer(var3), 15728880, OverlayTexture.NO_OVERLAY);
   }

   protected String getTextureLabel() {
      return "player skin";
   }
}
