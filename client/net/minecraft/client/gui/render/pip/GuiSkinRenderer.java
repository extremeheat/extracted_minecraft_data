package net.minecraft.client.gui.render.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.gui.pip.GuiSkinRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix4fStack;
import org.joml.Quaternionfc;

public class GuiSkinRenderer extends PictureInPictureRenderer<GuiSkinRenderState> {
   public GuiSkinRenderer(final MultiBufferSource.BufferSource bufferSource) {
      super(bufferSource);
   }

   public Class<GuiSkinRenderState> getRenderStateClass() {
      return GuiSkinRenderState.class;
   }

   protected void renderToTexture(final GuiSkinRenderState skinState, final PoseStack modelStack) {
      Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.PLAYER_SKIN);
      int guiScale = Minecraft.getInstance().gameRenderer.getGameRenderState().windowRenderState.guiScale;
      Matrix4fStack viewStack = RenderSystem.getModelViewStack();
      viewStack.pushMatrix();
      float scale = skinState.scale() * (float)guiScale;
      viewStack.rotateAround(Axis.XP.rotationDegrees(skinState.rotationX()), 0.0F, scale * -skinState.pivotY(), 0.0F);
      modelStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-skinState.rotationY()));
      modelStack.translate(0.0F, -1.6010001F, 0.0F);
      RenderType skinRenderType = skinState.playerModel().renderType(skinState.texture());
      skinState.playerModel().renderToBuffer(modelStack, this.bufferSource.getBuffer(skinRenderType), 15728880, OverlayTexture.NO_OVERLAY);
      this.bufferSource.endBatch();
      viewStack.popMatrix();
   }

   protected String getTextureLabel() {
      return "player skin";
   }
}
