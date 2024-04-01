package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.GuardianModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class GuardianRenderer extends MobRenderer<Guardian, GuardianModel> {
   private static final ResourceLocation GUARDIAN_LOCATION = new ResourceLocation("textures/entity/guardian.png");
   private static final ResourceLocation TOXIFIN_LOCATION = new ResourceLocation("textures/entity/toxifin.png");
   private static final ResourceLocation GUARDIAN_BEAM_LOCATION = new ResourceLocation("textures/entity/guardian_beam.png");
   public static final ResourceLocation TOXIFIN_BEAM_LOCATION = new ResourceLocation("textures/entity/toxifin_beam.png");

   public GuardianRenderer(EntityRendererProvider.Context var1, ModelLayerLocation var2) {
      this(var1, 0.5F, var2);
   }

   protected GuardianRenderer(EntityRendererProvider.Context var1, float var2, ModelLayerLocation var3) {
      super(var1, new GuardianModel(var1.bakeLayer(var3)), var2);
   }

   public boolean shouldRender(Guardian var1, Frustum var2, double var3, double var5, double var7) {
      if (super.shouldRender(var1, var2, var3, var5, var7)) {
         return true;
      } else {
         if (var1.hasActiveAttackTarget()) {
            LivingEntity var9 = var1.getActiveAttackTarget();
            if (var9 != null) {
               Vec3 var10 = this.getPosition(var9, (double)var9.getBbHeight() * 0.5, 1.0F);
               Vec3 var11 = this.getPosition(var1, (double)var1.getEyeHeight(), 1.0F);
               return var2.isVisible(new AABB(var11.x, var11.y, var11.z, var10.x, var10.y, var10.z));
            }
         }

         return false;
      }
   }

   private Vec3 getPosition(LivingEntity var1, double var2, float var4) {
      double var5 = Mth.lerp((double)var4, var1.xOld, var1.getX());
      double var7 = Mth.lerp((double)var4, var1.yOld, var1.getY()) + var2;
      double var9 = Mth.lerp((double)var4, var1.zOld, var1.getZ());
      return new Vec3(var5, var7, var9);
   }

   public void render(Guardian var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      super.render(var1, var2, var3, var4, var5, var6);
      LivingEntity var7 = var1.getActiveAttackTarget();
      boolean var8 = var1.isToxic();
      if (var7 != null) {
         float var9 = var1.getAttackAnimationScale(var3);
         float var10 = var1.getClientSideAttackTime() + var3;
         float var11 = var10 * (var8 ? 0.15F : 0.5F) % 1.0F;
         float var12 = var1.getEyeHeight();
         var4.pushPose();
         var4.translate(0.0F, var12, 0.0F);
         Vec3 var13 = this.getPosition(var7, (double)var7.getBbHeight() * 0.5, var3);
         Vec3 var14 = this.getPosition(var1, (double)var12, var3);
         Vec3 var15 = var13.subtract(var14);
         float var16 = (float)(var15.length() + (var8 ? 0.1 : 1.0));
         var15 = var15.normalize();
         float var17 = (float)Math.acos(var15.y);
         float var18 = (float)Math.atan2(var15.z, var15.x);
         var4.mulPose(Axis.YP.rotationDegrees((1.5707964F - var18) * 57.295776F));
         var4.mulPose(Axis.XP.rotationDegrees(var17 * 57.295776F));
         boolean var19 = true;
         float var20 = var10 * 0.05F * -1.5F;
         float var21 = var9 * var9;
         int var22;
         int var23;
         int var24;
         if (var8) {
            var22 = 255 - (int)(var21 * 127.0F);
            var23 = 255;
            var24 = 255 - (int)(var21 * 127.0F);
         } else {
            var22 = 64 + (int)(var21 * 191.0F);
            var23 = 32 + (int)(var21 * 191.0F);
            var24 = 128 - (int)(var21 * 64.0F);
         }

         float var25 = 0.2F;
         float var26 = 0.282F;
         float var27 = Mth.cos(var20 + 2.3561945F) * 0.282F;
         float var28 = Mth.sin(var20 + 2.3561945F) * 0.282F;
         float var29 = Mth.cos(var20 + 0.7853982F) * 0.282F;
         float var30 = Mth.sin(var20 + 0.7853982F) * 0.282F;
         float var31 = Mth.cos(var20 + 3.926991F) * 0.282F;
         float var32 = Mth.sin(var20 + 3.926991F) * 0.282F;
         float var33 = Mth.cos(var20 + 5.4977875F) * 0.282F;
         float var34 = Mth.sin(var20 + 5.4977875F) * 0.282F;
         float var35 = Mth.cos(var20 + 3.1415927F) * 0.2F;
         float var36 = Mth.sin(var20 + 3.1415927F) * 0.2F;
         float var37 = Mth.cos(var20 + 0.0F) * 0.2F;
         float var38 = Mth.sin(var20 + 0.0F) * 0.2F;
         float var39 = Mth.cos(var20 + 1.5707964F) * 0.2F;
         float var40 = Mth.sin(var20 + 1.5707964F) * 0.2F;
         float var41 = Mth.cos(var20 + 4.712389F) * 0.2F;
         float var42 = Mth.sin(var20 + 4.712389F) * 0.2F;
         float var44 = 0.0F;
         float var45 = 0.4999F;
         float var46 = -1.0F + var11;
         float var47 = var16 * 2.5F + var46;
         VertexConsumer var48 = var5.getBuffer(RenderType.entityCutoutNoCull(var8 ? TOXIFIN_BEAM_LOCATION : GUARDIAN_BEAM_LOCATION));
         PoseStack.Pose var49 = var4.last();
         vertex(var48, var49, var35, var16, var36, var22, var23, var24, 0.4999F, var47);
         vertex(var48, var49, var35, 0.0F, var36, var22, var23, var24, 0.4999F, var46);
         vertex(var48, var49, var37, 0.0F, var38, var22, var23, var24, 0.0F, var46);
         vertex(var48, var49, var37, var16, var38, var22, var23, var24, 0.0F, var47);
         vertex(var48, var49, var39, var16, var40, var22, var23, var24, 0.4999F, var47);
         vertex(var48, var49, var39, 0.0F, var40, var22, var23, var24, 0.4999F, var46);
         vertex(var48, var49, var41, 0.0F, var42, var22, var23, var24, 0.0F, var46);
         vertex(var48, var49, var41, var16, var42, var22, var23, var24, 0.0F, var47);
         float var50 = 0.0F;
         if (!var8 && var1.tickCount % 2 == 0) {
            var50 = 0.5F;
         }

         vertex(var48, var49, var27, var16, var28, var22, var23, var24, 0.5F, var50 + 0.5F);
         vertex(var48, var49, var29, var16, var30, var22, var23, var24, 1.0F, var50 + 0.5F);
         vertex(var48, var49, var33, var16, var34, var22, var23, var24, 1.0F, var50);
         vertex(var48, var49, var31, var16, var32, var22, var23, var24, 0.5F, var50);
         var4.popPose();
      }
   }

   private static void vertex(
      VertexConsumer var0, PoseStack.Pose var1, float var2, float var3, float var4, int var5, int var6, int var7, float var8, float var9
   ) {
      var0.vertex(var1, var2, var3, var4)
         .color(var5, var6, var7, 255)
         .uv(var8, var9)
         .overlayCoords(OverlayTexture.NO_OVERLAY)
         .uv2(15728880)
         .normal(var1, 0.0F, 1.0F, 0.0F)
         .endVertex();
   }

   public ResourceLocation getTextureLocation(Guardian var1) {
      return var1.isToxic() ? TOXIFIN_LOCATION : GUARDIAN_LOCATION;
   }
}
