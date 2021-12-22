package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.GuardianModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class GuardianRenderer extends MobRenderer<Guardian, GuardianModel> {
   private static final ResourceLocation GUARDIAN_LOCATION = new ResourceLocation("textures/entity/guardian.png");
   private static final ResourceLocation GUARDIAN_BEAM_LOCATION = new ResourceLocation("textures/entity/guardian_beam.png");
   private static final RenderType BEAM_RENDER_TYPE;

   public GuardianRenderer(EntityRendererProvider.Context var1) {
      this(var1, 0.5F, ModelLayers.GUARDIAN);
   }

   protected GuardianRenderer(EntityRendererProvider.Context var1, float var2, ModelLayerLocation var3) {
      super(var1, new GuardianModel(var1.bakeLayer(var3)), var2);
   }

   public boolean shouldRender(Guardian var1, Frustum var2, double var3, double var5, double var7) {
      if (super.shouldRender((Mob)var1, var2, var3, var5, var7)) {
         return true;
      } else {
         if (var1.hasActiveAttackTarget()) {
            LivingEntity var9 = var1.getActiveAttackTarget();
            if (var9 != null) {
               Vec3 var10 = this.getPosition(var9, (double)var9.getBbHeight() * 0.5D, 1.0F);
               Vec3 var11 = this.getPosition(var1, (double)var1.getEyeHeight(), 1.0F);
               return var2.isVisible(new AABB(var11.field_414, var11.field_415, var11.field_416, var10.field_414, var10.field_415, var10.field_416));
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
      super.render((Mob)var1, var2, var3, var4, var5, var6);
      LivingEntity var7 = var1.getActiveAttackTarget();
      if (var7 != null) {
         float var8 = var1.getAttackAnimationScale(var3);
         float var9 = (float)var1.level.getGameTime() + var3;
         float var10 = var9 * 0.5F % 1.0F;
         float var11 = var1.getEyeHeight();
         var4.pushPose();
         var4.translate(0.0D, (double)var11, 0.0D);
         Vec3 var12 = this.getPosition(var7, (double)var7.getBbHeight() * 0.5D, var3);
         Vec3 var13 = this.getPosition(var1, (double)var11, var3);
         Vec3 var14 = var12.subtract(var13);
         float var15 = (float)(var14.length() + 1.0D);
         var14 = var14.normalize();
         float var16 = (float)Math.acos(var14.field_415);
         float var17 = (float)Math.atan2(var14.field_416, var14.field_414);
         var4.mulPose(Vector3f.field_292.rotationDegrees((1.5707964F - var17) * 57.295776F));
         var4.mulPose(Vector3f.field_290.rotationDegrees(var16 * 57.295776F));
         boolean var18 = true;
         float var19 = var9 * 0.05F * -1.5F;
         float var20 = var8 * var8;
         int var21 = 64 + (int)(var20 * 191.0F);
         int var22 = 32 + (int)(var20 * 191.0F);
         int var23 = 128 - (int)(var20 * 64.0F);
         float var24 = 0.2F;
         float var25 = 0.282F;
         float var26 = Mth.cos(var19 + 2.3561945F) * 0.282F;
         float var27 = Mth.sin(var19 + 2.3561945F) * 0.282F;
         float var28 = Mth.cos(var19 + 0.7853982F) * 0.282F;
         float var29 = Mth.sin(var19 + 0.7853982F) * 0.282F;
         float var30 = Mth.cos(var19 + 3.926991F) * 0.282F;
         float var31 = Mth.sin(var19 + 3.926991F) * 0.282F;
         float var32 = Mth.cos(var19 + 5.4977875F) * 0.282F;
         float var33 = Mth.sin(var19 + 5.4977875F) * 0.282F;
         float var34 = Mth.cos(var19 + 3.1415927F) * 0.2F;
         float var35 = Mth.sin(var19 + 3.1415927F) * 0.2F;
         float var36 = Mth.cos(var19 + 0.0F) * 0.2F;
         float var37 = Mth.sin(var19 + 0.0F) * 0.2F;
         float var38 = Mth.cos(var19 + 1.5707964F) * 0.2F;
         float var39 = Mth.sin(var19 + 1.5707964F) * 0.2F;
         float var40 = Mth.cos(var19 + 4.712389F) * 0.2F;
         float var41 = Mth.sin(var19 + 4.712389F) * 0.2F;
         float var43 = 0.0F;
         float var44 = 0.4999F;
         float var45 = -1.0F + var10;
         float var46 = var15 * 2.5F + var45;
         VertexConsumer var47 = var5.getBuffer(BEAM_RENDER_TYPE);
         PoseStack.Pose var48 = var4.last();
         Matrix4f var49 = var48.pose();
         Matrix3f var50 = var48.normal();
         vertex(var47, var49, var50, var34, var15, var35, var21, var22, var23, 0.4999F, var46);
         vertex(var47, var49, var50, var34, 0.0F, var35, var21, var22, var23, 0.4999F, var45);
         vertex(var47, var49, var50, var36, 0.0F, var37, var21, var22, var23, 0.0F, var45);
         vertex(var47, var49, var50, var36, var15, var37, var21, var22, var23, 0.0F, var46);
         vertex(var47, var49, var50, var38, var15, var39, var21, var22, var23, 0.4999F, var46);
         vertex(var47, var49, var50, var38, 0.0F, var39, var21, var22, var23, 0.4999F, var45);
         vertex(var47, var49, var50, var40, 0.0F, var41, var21, var22, var23, 0.0F, var45);
         vertex(var47, var49, var50, var40, var15, var41, var21, var22, var23, 0.0F, var46);
         float var51 = 0.0F;
         if (var1.tickCount % 2 == 0) {
            var51 = 0.5F;
         }

         vertex(var47, var49, var50, var26, var15, var27, var21, var22, var23, 0.5F, var51 + 0.5F);
         vertex(var47, var49, var50, var28, var15, var29, var21, var22, var23, 1.0F, var51 + 0.5F);
         vertex(var47, var49, var50, var32, var15, var33, var21, var22, var23, 1.0F, var51);
         vertex(var47, var49, var50, var30, var15, var31, var21, var22, var23, 0.5F, var51);
         var4.popPose();
      }

   }

   private static void vertex(VertexConsumer var0, Matrix4f var1, Matrix3f var2, float var3, float var4, float var5, int var6, int var7, int var8, float var9, float var10) {
      var0.vertex(var1, var3, var4, var5).color(var6, var7, var8, 255).method_7(var9, var10).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(var2, 0.0F, 1.0F, 0.0F).endVertex();
   }

   public ResourceLocation getTextureLocation(Guardian var1) {
      return GUARDIAN_LOCATION;
   }

   static {
      BEAM_RENDER_TYPE = RenderType.entityCutoutNoCull(GUARDIAN_BEAM_LOCATION);
   }
}
