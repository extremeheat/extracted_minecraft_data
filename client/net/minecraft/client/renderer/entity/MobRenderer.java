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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

public abstract class MobRenderer<T extends Mob, M extends EntityModel<T>> extends LivingEntityRenderer<T, M> {
   public static final int LEASH_RENDER_STEPS = 24;

   public MobRenderer(EntityRendererProvider.Context var1, M var2, float var3) {
      super(var1, (M)var2, var3);
   }

   protected boolean shouldShowName(T var1) {
      return super.shouldShowName((T)var1) && (var1.shouldShowName() || var1.hasCustomName() && var1 == this.entityRenderDispatcher.crosshairPickEntity);
   }

   public boolean shouldRender(T var1, Frustum var2, double var3, double var5, double var7) {
      if (super.shouldRender((T)var1, var2, var3, var5, var7)) {
         return true;
      } else {
         Entity var9 = var1.getLeashHolder();
         return var9 != null ? var2.isVisible(var9.getBoundingBoxForCulling()) : false;
      }
   }

   public void render(T var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      super.render((T)var1, var2, var3, var4, var5, var6);
      Entity var7 = var1.getLeashHolder();
      if (var7 != null) {
         this.renderLeash((T)var1, var3, var4, var5, var7);
      }
   }

   private <E extends Entity> void renderLeash(T var1, float var2, PoseStack var3, MultiBufferSource var4, E var5) {
      var3.pushPose();
      Vec3 var6 = var5.getRopeHoldPosition(var2);
      double var7 = (double)(Mth.lerp(var2, var1.yBodyRotO, var1.yBodyRot) * 0.017453292F) + 1.5707963267948966;
      Vec3 var9 = var1.getLeashOffset();
      double var10 = Math.cos(var7) * var9.z + Math.sin(var7) * var9.x;
      double var12 = Math.sin(var7) * var9.z - Math.cos(var7) * var9.x;
      double var14 = Mth.lerp((double)var2, var1.xo, var1.getX()) + var10;
      double var16 = Mth.lerp((double)var2, var1.yo, var1.getY()) + var9.y;
      double var18 = Mth.lerp((double)var2, var1.zo, var1.getZ()) + var12;
      var3.translate(var10, var9.y, var12);
      float var20 = (float)(var6.x - var14);
      float var21 = (float)(var6.y - var16);
      float var22 = (float)(var6.z - var18);
      float var23 = 0.025F;
      VertexConsumer var24 = var4.getBuffer(RenderType.leash());
      Matrix4f var25 = var3.last().pose();
      float var26 = Mth.fastInvSqrt(var20 * var20 + var22 * var22) * 0.025F / 2.0F;
      float var27 = var22 * var26;
      float var28 = var20 * var26;
      BlockPos var29 = new BlockPos(var1.getEyePosition(var2));
      BlockPos var30 = new BlockPos(var5.getEyePosition(var2));
      int var31 = this.getBlockLightLevel((T)var1, var29);
      int var32 = this.entityRenderDispatcher.getRenderer(var5).getBlockLightLevel(var5, var30);
      int var33 = var1.level.getBrightness(LightLayer.SKY, var29);
      int var34 = var1.level.getBrightness(LightLayer.SKY, var30);

      for(int var35 = 0; var35 <= 24; ++var35) {
         addVertexPair(var24, var25, var20, var21, var22, var31, var32, var33, var34, 0.025F, 0.025F, var27, var28, var35, false);
      }

      for(int var36 = 24; var36 >= 0; --var36) {
         addVertexPair(var24, var25, var20, var21, var22, var31, var32, var33, var34, 0.025F, 0.0F, var27, var28, var36, true);
      }

      var3.popPose();
   }

   private static void addVertexPair(
      VertexConsumer var0,
      Matrix4f var1,
      float var2,
      float var3,
      float var4,
      int var5,
      int var6,
      int var7,
      int var8,
      float var9,
      float var10,
      float var11,
      float var12,
      int var13,
      boolean var14
   ) {
      float var15 = (float)var13 / 24.0F;
      int var16 = (int)Mth.lerp(var15, (float)var5, (float)var6);
      int var17 = (int)Mth.lerp(var15, (float)var7, (float)var8);
      int var18 = LightTexture.pack(var16, var17);
      float var19 = var13 % 2 == (var14 ? 1 : 0) ? 0.7F : 1.0F;
      float var20 = 0.5F * var19;
      float var21 = 0.4F * var19;
      float var22 = 0.3F * var19;
      float var23 = var2 * var15;
      float var24 = var3 > 0.0F ? var3 * var15 * var15 : var3 - var3 * (1.0F - var15) * (1.0F - var15);
      float var25 = var4 * var15;
      var0.vertex(var1, var23 - var11, var24 + var10, var25 + var12).color(var20, var21, var22, 1.0F).uv2(var18).endVertex();
      var0.vertex(var1, var23 + var11, var24 + var9 - var10, var25 - var12).color(var20, var21, var22, 1.0F).uv2(var18).endVertex();
   }
}
