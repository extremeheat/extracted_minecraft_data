package net.minecraft.client.gui.render.pip;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.state.pip.GuiSignRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;

public class GuiSignRenderer extends PictureInPictureRenderer<GuiSignRenderState> {
   private final MaterialSet materials;

   public GuiSignRenderer(MultiBufferSource.BufferSource var1, MaterialSet var2) {
      super(var1);
      this.materials = var2;
   }

   public Class<GuiSignRenderState> getRenderStateClass() {
      return GuiSignRenderState.class;
   }

   protected void renderToTexture(GuiSignRenderState var1, PoseStack var2) {
      Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_FLAT);
      var2.translate(0.0F, -0.75F, 0.0F);
      Material var3 = Sheets.getSignMaterial(var1.woodType());
      Model.Simple var4 = var1.signModel();
      MaterialSet var10001 = this.materials;
      MultiBufferSource.BufferSource var10002 = this.bufferSource;
      Objects.requireNonNull(var4);
      VertexConsumer var5 = var3.buffer(var10001, var10002, var4::renderType);
      var4.renderToBuffer(var2, var5, 15728880, OverlayTexture.NO_OVERLAY);
   }

   protected String getTextureLabel() {
      return "sign";
   }
}
