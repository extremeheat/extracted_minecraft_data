package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.LlamaSpitModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.LlamaSpitRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.LlamaSpit;

public class LlamaSpitRenderer extends EntityRenderer<LlamaSpit, LlamaSpitRenderState> {
   private static final ResourceLocation LLAMA_SPIT_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/llama/spit.png");
   private final LlamaSpitModel model;

   public LlamaSpitRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.model = new LlamaSpitModel(var1.bakeLayer(ModelLayers.LLAMA_SPIT));
   }

   public void render(LlamaSpitRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      var2.pushPose();
      var2.translate(0.0F, 0.15F, 0.0F);
      var2.mulPose(Axis.YP.rotationDegrees(var1.yRot - 90.0F));
      var2.mulPose(Axis.ZP.rotationDegrees(var1.xRot));
      this.model.setupAnim(var1);
      VertexConsumer var5 = var3.getBuffer(this.model.renderType(LLAMA_SPIT_LOCATION));
      this.model.renderToBuffer(var2, var5, var4, OverlayTexture.NO_OVERLAY);
      var2.popPose();
      super.render(var1, var2, var3, var4);
   }

   public LlamaSpitRenderState createRenderState() {
      return new LlamaSpitRenderState();
   }

   public void extractRenderState(LlamaSpit var1, LlamaSpitRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.xRot = var1.getXRot(var3);
      var2.yRot = var1.getYRot(var3);
   }
}
