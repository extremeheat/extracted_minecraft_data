package net.minecraft.client.gui.render.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.gui.pip.GuiSignRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.util.Unit;

public class GuiSignRenderer extends PictureInPictureRenderer<GuiSignRenderState> {
   private final SpriteGetter sprites;

   public GuiSignRenderer(final SpriteGetter sprites) {
      super();
      this.sprites = sprites;
   }

   public Class<GuiSignRenderState> getRenderStateClass() {
      return GuiSignRenderState.class;
   }

   protected void renderToTexture(final GuiSignRenderState renderState, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector) {
      Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_FLAT);
      poseStack.translate(0.0F, -0.75F, 0.0F);
      SpriteId sprite = Sheets.getSignSprite(renderState.woodType());
      submitNodeCollector.submitModel(renderState.signModel(), Unit.INSTANCE, poseStack, 15728880, OverlayTexture.NO_OVERLAY, -1, sprite, this.sprites, 0, (ModelFeatureRenderer.CrumblingOverlay)null);
   }

   protected String getTextureLabel() {
      return "sign";
   }
}
