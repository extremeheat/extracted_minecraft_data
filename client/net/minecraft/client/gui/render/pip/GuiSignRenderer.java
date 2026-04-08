package net.minecraft.client.gui.render.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.state.gui.pip.GuiSignRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;

public class GuiSignRenderer extends PictureInPictureRenderer<GuiSignRenderState> {
   private final SpriteGetter sprites;

   public GuiSignRenderer(final MultiBufferSource.BufferSource bufferSource, final SpriteGetter sprites) {
      super(bufferSource);
      this.sprites = sprites;
   }

   public Class<GuiSignRenderState> getRenderStateClass() {
      return GuiSignRenderState.class;
   }

   protected void renderToTexture(final GuiSignRenderState renderState, final PoseStack poseStack) {
      Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_FLAT);
      poseStack.translate(0.0F, -0.75F, 0.0F);
      SpriteId sprite = Sheets.getSignSprite(renderState.woodType());
      Model.Simple model = renderState.signModel();
      VertexConsumer buffer = sprite.buffer(this.sprites, this.bufferSource, model.renderType());
      model.renderToBuffer(poseStack, buffer, 15728880, OverlayTexture.NO_OVERLAY);
   }

   protected String getTextureLabel() {
      return "sign";
   }
}
