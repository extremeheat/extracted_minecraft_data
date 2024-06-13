package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class BeeStingerLayer<T extends LivingEntity, M extends PlayerModel<T>> extends StuckInBodyLayer<T, M> {
   private static final ResourceLocation BEE_STINGER_LOCATION = new ResourceLocation("textures/entity/bee/bee_stinger.png");

   public BeeStingerLayer(LivingEntityRenderer<T, M> var1) {
      super(var1);
   }

   @Override
   protected int numStuck(T var1) {
      return var1.getStingerCount();
   }

   @Override
   protected void renderStuckItem(PoseStack var1, MultiBufferSource var2, int var3, Entity var4, float var5, float var6, float var7, float var8) {
      float var9 = Mth.sqrt(var5 * var5 + var7 * var7);
      float var10 = (float)(Math.atan2((double)var5, (double)var7) * 57.2957763671875);
      float var11 = (float)(Math.atan2((double)var6, (double)var9) * 57.2957763671875);
      var1.translate(0.0F, 0.0F, 0.0F);
      var1.mulPose(Axis.YP.rotationDegrees(var10 - 90.0F));
      var1.mulPose(Axis.ZP.rotationDegrees(var11));
      float var12 = 0.0F;
      float var13 = 0.125F;
      float var14 = 0.0F;
      float var15 = 0.0625F;
      float var16 = 0.03125F;
      var1.mulPose(Axis.XP.rotationDegrees(45.0F));
      var1.scale(0.03125F, 0.03125F, 0.03125F);
      var1.translate(2.5F, 0.0F, 0.0F);
      VertexConsumer var17 = var2.getBuffer(RenderType.entityCutoutNoCull(BEE_STINGER_LOCATION));

      for (int var18 = 0; var18 < 4; var18++) {
         var1.mulPose(Axis.XP.rotationDegrees(90.0F));
         PoseStack.Pose var19 = var1.last();
         vertex(var17, var19, -4.5F, -1, 0.0F, 0.0F, var3);
         vertex(var17, var19, 4.5F, -1, 0.125F, 0.0F, var3);
         vertex(var17, var19, 4.5F, 1, 0.125F, 0.0625F, var3);
         vertex(var17, var19, -4.5F, 1, 0.0F, 0.0625F, var3);
      }
   }

   private static void vertex(VertexConsumer var0, PoseStack.Pose var1, float var2, int var3, float var4, float var5, int var6) {
      var0.vertex(var1, var2, (float)var3, 0.0F)
         .color(255, 255, 255, 255)
         .uv(var4, var5)
         .overlayCoords(OverlayTexture.NO_OVERLAY)
         .uv2(var6)
         .normal(var1, 0.0F, 1.0F, 0.0F)
         .endVertex();
   }
}
