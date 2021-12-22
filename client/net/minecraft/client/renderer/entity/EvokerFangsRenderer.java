package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.EvokerFangsModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.EvokerFangs;

public class EvokerFangsRenderer extends EntityRenderer<EvokerFangs> {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/illager/evoker_fangs.png");
   private final EvokerFangsModel<EvokerFangs> model;

   public EvokerFangsRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.model = new EvokerFangsModel(var1.bakeLayer(ModelLayers.EVOKER_FANGS));
   }

   public void render(EvokerFangs var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      float var7 = var1.getAnimationProgress(var3);
      if (var7 != 0.0F) {
         float var8 = 2.0F;
         if (var7 > 0.9F) {
            var8 = (float)((double)var8 * ((1.0D - (double)var7) / 0.10000000149011612D));
         }

         var4.pushPose();
         var4.mulPose(Vector3f.field_292.rotationDegrees(90.0F - var1.getYRot()));
         var4.scale(-var8, -var8, var8);
         float var9 = 0.03125F;
         var4.translate(0.0D, -0.6259999871253967D, 0.0D);
         var4.scale(0.5F, 0.5F, 0.5F);
         this.model.setupAnim(var1, var7, 0.0F, 0.0F, var1.getYRot(), var1.getXRot());
         VertexConsumer var10 = var5.getBuffer(this.model.renderType(TEXTURE_LOCATION));
         this.model.renderToBuffer(var4, var10, var6, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
         var4.popPose();
         super.render(var1, var2, var3, var4, var5, var6);
      }
   }

   public ResourceLocation getTextureLocation(EvokerFangs var1) {
      return TEXTURE_LOCATION;
   }
}
