package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.LlamaSpitModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.LlamaSpit;

public class LlamaSpitRenderer extends EntityRenderer<LlamaSpit> {
   private static final ResourceLocation LLAMA_SPIT_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/llama/spit.png");
   private final LlamaSpitModel<LlamaSpit> model;

   public LlamaSpitRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.model = new LlamaSpitModel(var1.bakeLayer(ModelLayers.LLAMA_SPIT));
   }

   public void render(LlamaSpit var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      var4.pushPose();
      var4.translate(0.0F, 0.15F, 0.0F);
      var4.mulPose(Axis.YP.rotationDegrees(Mth.lerp(var3, var1.yRotO, var1.getYRot()) - 90.0F));
      var4.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(var3, var1.xRotO, var1.getXRot())));
      this.model.setupAnim(var1, var3, 0.0F, -0.1F, 0.0F, 0.0F);
      VertexConsumer var7 = var5.getBuffer(this.model.renderType(LLAMA_SPIT_LOCATION));
      this.model.renderToBuffer(var4, var7, var6, OverlayTexture.NO_OVERLAY);
      var4.popPose();
      super.render(var1, var2, var3, var4, var5, var6);
   }

   public ResourceLocation getTextureLocation(LlamaSpit var1) {
      return LLAMA_SPIT_LOCATION;
   }
}
