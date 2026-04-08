package net.minecraft.client.gui.render.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.state.gui.pip.GuiBookModelRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Quaternionfc;

public class GuiBookModelRenderer extends PictureInPictureRenderer<GuiBookModelRenderState> {
   public GuiBookModelRenderer(final MultiBufferSource.BufferSource bufferSource) {
      super(bufferSource);
   }

   public Class<GuiBookModelRenderState> getRenderStateClass() {
      return GuiBookModelRenderState.class;
   }

   protected void renderToTexture(final GuiBookModelRenderState bookModelState, final PoseStack poseStack) {
      Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
      poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F));
      poseStack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(25.0F));
      float open = bookModelState.open();
      poseStack.translate((1.0F - open) * 0.2F, (1.0F - open) * 0.1F, (1.0F - open) * 0.25F);
      poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-(1.0F - open) * 90.0F - 90.0F));
      poseStack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(180.0F));
      float flip = bookModelState.flip();
      float pageFlip1 = Mth.clamp(Mth.frac(flip + 0.25F) * 1.6F - 0.3F, 0.0F, 1.0F);
      float pageFlip2 = Mth.clamp(Mth.frac(flip + 0.75F) * 1.6F - 0.3F, 0.0F, 1.0F);
      BookModel bookModel = bookModelState.bookModel();
      bookModel.setupAnim(BookModel.State.forAnimation(0.0F, pageFlip1, pageFlip2, open));
      Identifier texture = bookModelState.texture();
      VertexConsumer buffer = this.bufferSource.getBuffer(bookModel.renderType(texture));
      bookModel.renderToBuffer(poseStack, buffer, 15728880, OverlayTexture.NO_OVERLAY);
   }

   protected float getTranslateY(final int height, final int guiScale) {
      return (float)(17 * guiScale);
   }

   protected String getTextureLabel() {
      return "book model";
   }
}
