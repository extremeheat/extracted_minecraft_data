package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.GuardianModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.state.GuardianRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class GuardianRenderer extends MobRenderer<Guardian, GuardianRenderState, GuardianModel> {
   private static final ResourceLocation GUARDIAN_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/guardian.png");
   private static final ResourceLocation GUARDIAN_BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/guardian_beam.png");
   private static final RenderType BEAM_RENDER_TYPE = RenderType.entityCutoutNoCull(GUARDIAN_BEAM_LOCATION);

   public GuardianRenderer(EntityRendererProvider.Context var1) {
      this(var1, 0.5F, ModelLayers.GUARDIAN);
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

   public void render(GuardianRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      super.render(var1, var2, var3, var4);
      Vec3 var5 = var1.attackTargetPosition;
      if (var5 != null) {
         float var6 = var1.attackTime * 0.5F % 1.0F;
         var2.pushPose();
         var2.translate(0.0F, var1.eyeHeight, 0.0F);
         renderBeam(var2, var3.getBuffer(BEAM_RENDER_TYPE), var5.subtract(var1.eyePosition), var1.attackTime, var1.scale, var6);
         var2.popPose();
      }
   }

   private static void renderBeam(PoseStack var0, VertexConsumer var1, Vec3 var2, float var3, float var4, float var5) {
      float var6 = (float)(var2.length() + 1.0);
      var2 = var2.normalize();
      float var7 = (float)Math.acos(var2.y);
      float var8 = 1.5707964F - (float)Math.atan2(var2.z, var2.x);
      var0.mulPose(Axis.YP.rotationDegrees(var8 * 57.295776F));
      var0.mulPose(Axis.XP.rotationDegrees(var7 * 57.295776F));
      float var9 = var3 * 0.05F * -1.5F;
      float var10 = var4 * var4;
      int var11 = 64 + (int)(var10 * 191.0F);
      int var12 = 32 + (int)(var10 * 191.0F);
      int var13 = 128 - (int)(var10 * 64.0F);
      float var14 = 0.2F;
      float var15 = 0.282F;
      float var16 = Mth.cos(var9 + 2.3561945F) * 0.282F;
      float var17 = Mth.sin(var9 + 2.3561945F) * 0.282F;
      float var18 = Mth.cos(var9 + 0.7853982F) * 0.282F;
      float var19 = Mth.sin(var9 + 0.7853982F) * 0.282F;
      float var20 = Mth.cos(var9 + 3.926991F) * 0.282F;
      float var21 = Mth.sin(var9 + 3.926991F) * 0.282F;
      float var22 = Mth.cos(var9 + 5.4977875F) * 0.282F;
      float var23 = Mth.sin(var9 + 5.4977875F) * 0.282F;
      float var24 = Mth.cos(var9 + 3.1415927F) * 0.2F;
      float var25 = Mth.sin(var9 + 3.1415927F) * 0.2F;
      float var26 = Mth.cos(var9 + 0.0F) * 0.2F;
      float var27 = Mth.sin(var9 + 0.0F) * 0.2F;
      float var28 = Mth.cos(var9 + 1.5707964F) * 0.2F;
      float var29 = Mth.sin(var9 + 1.5707964F) * 0.2F;
      float var30 = Mth.cos(var9 + 4.712389F) * 0.2F;
      float var31 = Mth.sin(var9 + 4.712389F) * 0.2F;
      float var33 = 0.0F;
      float var34 = 0.4999F;
      float var35 = -1.0F + var5;
      float var36 = var35 + var6 * 2.5F;
      PoseStack.Pose var37 = var0.last();
      vertex(var1, var37, var24, var6, var25, var11, var12, var13, 0.4999F, var36);
      vertex(var1, var37, var24, 0.0F, var25, var11, var12, var13, 0.4999F, var35);
      vertex(var1, var37, var26, 0.0F, var27, var11, var12, var13, 0.0F, var35);
      vertex(var1, var37, var26, var6, var27, var11, var12, var13, 0.0F, var36);
      vertex(var1, var37, var28, var6, var29, var11, var12, var13, 0.4999F, var36);
      vertex(var1, var37, var28, 0.0F, var29, var11, var12, var13, 0.4999F, var35);
      vertex(var1, var37, var30, 0.0F, var31, var11, var12, var13, 0.0F, var35);
      vertex(var1, var37, var30, var6, var31, var11, var12, var13, 0.0F, var36);
      float var38 = Mth.floor(var3) % 2 == 0 ? 0.5F : 0.0F;
      vertex(var1, var37, var16, var6, var17, var11, var12, var13, 0.5F, var38 + 0.5F);
      vertex(var1, var37, var18, var6, var19, var11, var12, var13, 1.0F, var38 + 0.5F);
      vertex(var1, var37, var22, var6, var23, var11, var12, var13, 1.0F, var38);
      vertex(var1, var37, var20, var6, var21, var11, var12, var13, 0.5F, var38);
   }

   private static void vertex(
      VertexConsumer var0, PoseStack.Pose var1, float var2, float var3, float var4, int var5, int var6, int var7, float var8, float var9
   ) {
      var0.addVertex(var1, var2, var3, var4)
         .setColor(var5, var6, var7, 255)
         .setUv(var8, var9)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(var1, 0.0F, 1.0F, 0.0F);
   }

   public ResourceLocation getTextureLocation(GuardianRenderState var1) {
      return GUARDIAN_LOCATION;
   }

   public GuardianRenderState createRenderState() {
      return new GuardianRenderState();
   }

   public void extractRenderState(Guardian var1, GuardianRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.spikesAnimation = var1.getSpikesAnimation(var3);
      var2.tailAnimation = var1.getTailAnimation(var3);
      var2.eyePosition = var1.getEyePosition(var3);
      Entity var4 = getEntityToLookAt(var1);
      if (var4 != null) {
         var2.lookDirection = var1.getViewVector(var3);
         var2.lookAtPosition = var4.getEyePosition(var3);
      } else {
         var2.lookDirection = null;
         var2.lookAtPosition = null;
      }

      LivingEntity var5 = var1.getActiveAttackTarget();
      if (var5 != null) {
         var2.attackScale = var1.getAttackAnimationScale(var3);
         var2.attackTime = var1.getClientSideAttackTime() + var3;
         var2.attackTargetPosition = this.getPosition(var5, (double)var5.getBbHeight() * 0.5, var3);
      } else {
         var2.attackTargetPosition = null;
      }
   }

   @Nullable
   private static Entity getEntityToLookAt(Guardian var0) {
      Entity var1 = Minecraft.getInstance().getCameraEntity();
      return (Entity)(var0.hasActiveAttackTarget() ? var0.getActiveAttackTarget() : var1);
   }
}
