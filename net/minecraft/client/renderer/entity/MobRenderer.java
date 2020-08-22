package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.level.LightLayer;

public abstract class MobRenderer extends LivingEntityRenderer {
   public MobRenderer(EntityRenderDispatcher var1, EntityModel var2, float var3) {
      super(var1, var2, var3);
   }

   protected boolean shouldShowName(Mob var1) {
      return super.shouldShowName((LivingEntity)var1) && (var1.shouldShowName() || var1.hasCustomName() && var1 == this.entityRenderDispatcher.crosshairPickEntity);
   }

   public boolean shouldRender(Mob var1, Frustum var2, double var3, double var5, double var7) {
      if (super.shouldRender(var1, var2, var3, var5, var7)) {
         return true;
      } else {
         Entity var9 = var1.getLeashHolder();
         return var9 != null ? var2.isVisible(var9.getBoundingBoxForCulling()) : false;
      }
   }

   public void render(Mob var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      super.render((LivingEntity)var1, var2, var3, var4, var5, var6);
      Entity var7 = var1.getLeashHolder();
      if (var7 != null) {
         this.renderLeash(var1, var3, var4, var5, var7);
      }
   }

   private void renderLeash(Mob var1, float var2, PoseStack var3, MultiBufferSource var4, Entity var5) {
      var3.pushPose();
      double var6 = (double)(Mth.lerp(var2 * 0.5F, var5.yRot, var5.yRotO) * 0.017453292F);
      double var8 = (double)(Mth.lerp(var2 * 0.5F, var5.xRot, var5.xRotO) * 0.017453292F);
      double var10 = Math.cos(var6);
      double var12 = Math.sin(var6);
      double var14 = Math.sin(var8);
      if (var5 instanceof HangingEntity) {
         var10 = 0.0D;
         var12 = 0.0D;
         var14 = -1.0D;
      }

      double var16 = Math.cos(var8);
      double var18 = Mth.lerp((double)var2, var5.xo, var5.getX()) - var10 * 0.7D - var12 * 0.5D * var16;
      double var20 = Mth.lerp((double)var2, var5.yo + (double)var5.getEyeHeight() * 0.7D, var5.getY() + (double)var5.getEyeHeight() * 0.7D) - var14 * 0.5D - 0.25D;
      double var22 = Mth.lerp((double)var2, var5.zo, var5.getZ()) - var12 * 0.7D + var10 * 0.5D * var16;
      double var24 = (double)(Mth.lerp(var2, var1.yBodyRot, var1.yBodyRotO) * 0.017453292F) + 1.5707963267948966D;
      var10 = Math.cos(var24) * (double)var1.getBbWidth() * 0.4D;
      var12 = Math.sin(var24) * (double)var1.getBbWidth() * 0.4D;
      double var26 = Mth.lerp((double)var2, var1.xo, var1.getX()) + var10;
      double var28 = Mth.lerp((double)var2, var1.yo, var1.getY());
      double var30 = Mth.lerp((double)var2, var1.zo, var1.getZ()) + var12;
      var3.translate(var10, -(1.6D - (double)var1.getBbHeight()) * 0.5D, var12);
      float var32 = (float)(var18 - var26);
      float var33 = (float)(var20 - var28);
      float var34 = (float)(var22 - var30);
      float var35 = 0.025F;
      VertexConsumer var36 = var4.getBuffer(RenderType.leash());
      Matrix4f var37 = var3.last().pose();
      float var38 = Mth.fastInvSqrt(var32 * var32 + var34 * var34) * 0.025F / 2.0F;
      float var39 = var34 * var38;
      float var40 = var32 * var38;
      int var41 = this.getBlockLightLevel(var1, var2);
      int var42 = this.entityRenderDispatcher.getRenderer(var5).getBlockLightLevel(var5, var2);
      int var43 = var1.level.getBrightness(LightLayer.SKY, new BlockPos(var1.getEyePosition(var2)));
      int var44 = var1.level.getBrightness(LightLayer.SKY, new BlockPos(var5.getEyePosition(var2)));
      renderSide(var36, var37, var32, var33, var34, var41, var42, var43, var44, 0.025F, 0.025F, var39, var40);
      renderSide(var36, var37, var32, var33, var34, var41, var42, var43, var44, 0.025F, 0.0F, var39, var40);
      var3.popPose();
   }

   public static void renderSide(VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, int var5, int var6, int var7, int var8, float var9, float var10, float var11, float var12) {
      boolean var13 = true;

      for(int var14 = 0; var14 < 24; ++var14) {
         float var15 = (float)var14 / 23.0F;
         int var16 = (int)Mth.lerp(var15, (float)var5, (float)var6);
         int var17 = (int)Mth.lerp(var15, (float)var7, (float)var8);
         int var18 = LightTexture.pack(var16, var17);
         addVertexPair(var0, var1, var18, var2, var3, var4, var9, var10, 24, var14, false, var11, var12);
         addVertexPair(var0, var1, var18, var2, var3, var4, var9, var10, 24, var14 + 1, true, var11, var12);
      }

   }

   public static void addVertexPair(VertexConsumer var0, Matrix4f var1, int var2, float var3, float var4, float var5, float var6, float var7, int var8, int var9, boolean var10, float var11, float var12) {
      float var13 = 0.5F;
      float var14 = 0.4F;
      float var15 = 0.3F;
      if (var9 % 2 == 0) {
         var13 *= 0.7F;
         var14 *= 0.7F;
         var15 *= 0.7F;
      }

      float var16 = (float)var9 / (float)var8;
      float var17 = var3 * var16;
      float var18 = var4 * (var16 * var16 + var16) * 0.5F + ((float)var8 - (float)var9) / ((float)var8 * 0.75F) + 0.125F;
      float var19 = var5 * var16;
      if (!var10) {
         var0.vertex(var1, var17 + var11, var18 + var6 - var7, var19 - var12).color(var13, var14, var15, 1.0F).uv2(var2).endVertex();
      }

      var0.vertex(var1, var17 - var11, var18 + var7, var19 + var12).color(var13, var14, var15, 1.0F).uv2(var2).endVertex();
      if (var10) {
         var0.vertex(var1, var17 + var11, var18 + var6 - var7, var19 - var12).color(var13, var14, var15, 1.0F).uv2(var2).endVertex();
      }

   }

   // $FF: synthetic method
   protected boolean shouldShowName(LivingEntity var1) {
      return this.shouldShowName((Mob)var1);
   }
}
