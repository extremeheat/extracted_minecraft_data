package net.minecraft.client.gui.render.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.state.pip.GuiBookModelRenderState;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Quaternionfc;

public class GuiBookModelRenderer extends PictureInPictureRenderer<GuiBookModelRenderState> {
   public GuiBookModelRenderer(MultiBufferSource.BufferSource var1) {
      super(var1);
   }

   public Class<GuiBookModelRenderState> getRenderStateClass() {
      return GuiBookModelRenderState.class;
   }

   protected void renderToTexture(GuiBookModelRenderState var1, PoseStack var2) {
      Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
      var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F));
      var2.mulPose((Quaternionfc)Axis.XP.rotationDegrees(25.0F));
      float var3 = var1.open();
      var2.translate((1.0F - var3) * 0.2F, (1.0F - var3) * 0.1F, (1.0F - var3) * 0.25F);
      var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-(1.0F - var3) * 90.0F - 90.0F));
      var2.mulPose((Quaternionfc)Axis.XP.rotationDegrees(180.0F));
      float var4 = var1.flip();
      float var5 = Mth.clamp(Mth.frac(var4 + 0.25F) * 1.6F - 0.3F, 0.0F, 1.0F);
      float var6 = Mth.clamp(Mth.frac(var4 + 0.75F) * 1.6F - 0.3F, 0.0F, 1.0F);
      BookModel var7 = var1.bookModel();
      var7.setupAnim(new BookModel.State(0.0F, var5, var6, var3));
      Identifier var8 = var1.texture();
      VertexConsumer var9 = this.bufferSource.getBuffer(var7.renderType(var8));
      var7.renderToBuffer(var2, var9, 15728880, OverlayTexture.NO_OVERLAY);
   }

   protected float getTranslateY(int var1, int var2) {
      return (float)(17 * var2);
   }

   protected String getTextureLabel() {
      return "book model";
   }
}
