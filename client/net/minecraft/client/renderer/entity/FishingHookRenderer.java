package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class FishingHookRenderer extends EntityRenderer<FishingHook> {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/fishing_hook.png");
   private static final RenderType RENDER_TYPE;
   private static final double VIEW_BOBBING_SCALE = 960.0D;

   public FishingHookRenderer(EntityRendererProvider.Context var1) {
      super(var1);
   }

   public void render(FishingHook var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      Player var7 = var1.getPlayerOwner();
      if (var7 != null) {
         var4.pushPose();
         var4.pushPose();
         var4.scale(0.5F, 0.5F, 0.5F);
         var4.mulPose(this.entityRenderDispatcher.cameraOrientation());
         var4.mulPose(Vector3f.field_292.rotationDegrees(180.0F));
         PoseStack.Pose var8 = var4.last();
         Matrix4f var9 = var8.pose();
         Matrix3f var10 = var8.normal();
         VertexConsumer var11 = var5.getBuffer(RENDER_TYPE);
         vertex(var11, var9, var10, var6, 0.0F, 0, 0, 1);
         vertex(var11, var9, var10, var6, 1.0F, 0, 1, 1);
         vertex(var11, var9, var10, var6, 1.0F, 1, 1, 0);
         vertex(var11, var9, var10, var6, 0.0F, 1, 0, 0);
         var4.popPose();
         int var12 = var7.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
         ItemStack var13 = var7.getMainHandItem();
         if (!var13.method_87(Items.FISHING_ROD)) {
            var12 = -var12;
         }

         float var14 = var7.getAttackAnim(var3);
         float var15 = Mth.sin(Mth.sqrt(var14) * 3.1415927F);
         float var16 = Mth.lerp(var3, var7.yBodyRotO, var7.yBodyRot) * 0.017453292F;
         double var17 = (double)Mth.sin(var16);
         double var19 = (double)Mth.cos(var16);
         double var21 = (double)var12 * 0.35D;
         double var23 = 0.8D;
         double var25;
         double var27;
         double var29;
         float var31;
         double var32;
         if ((this.entityRenderDispatcher.options == null || this.entityRenderDispatcher.options.getCameraType().isFirstPerson()) && var7 == Minecraft.getInstance().player) {
            var32 = 960.0D / this.entityRenderDispatcher.options.fov;
            Vec3 var34 = this.entityRenderDispatcher.camera.getNearPlane().getPointOnPlane((float)var12 * 0.525F, -0.1F);
            var34 = var34.scale(var32);
            var34 = var34.yRot(var15 * 0.5F);
            var34 = var34.xRot(-var15 * 0.7F);
            var25 = Mth.lerp((double)var3, var7.xo, var7.getX()) + var34.field_414;
            var27 = Mth.lerp((double)var3, var7.yo, var7.getY()) + var34.field_415;
            var29 = Mth.lerp((double)var3, var7.zo, var7.getZ()) + var34.field_416;
            var31 = var7.getEyeHeight();
         } else {
            var25 = Mth.lerp((double)var3, var7.xo, var7.getX()) - var19 * var21 - var17 * 0.8D;
            var27 = var7.yo + (double)var7.getEyeHeight() + (var7.getY() - var7.yo) * (double)var3 - 0.45D;
            var29 = Mth.lerp((double)var3, var7.zo, var7.getZ()) - var17 * var21 + var19 * 0.8D;
            var31 = var7.isCrouching() ? -0.1875F : 0.0F;
         }

         var32 = Mth.lerp((double)var3, var1.xo, var1.getX());
         double var45 = Mth.lerp((double)var3, var1.yo, var1.getY()) + 0.25D;
         double var36 = Mth.lerp((double)var3, var1.zo, var1.getZ());
         float var38 = (float)(var25 - var32);
         float var39 = (float)(var27 - var45) + var31;
         float var40 = (float)(var29 - var36);
         VertexConsumer var41 = var5.getBuffer(RenderType.lineStrip());
         PoseStack.Pose var42 = var4.last();
         boolean var43 = true;

         for(int var44 = 0; var44 <= 16; ++var44) {
            stringVertex(var38, var39, var40, var41, var42, fraction(var44, 16), fraction(var44 + 1, 16));
         }

         var4.popPose();
         super.render(var1, var2, var3, var4, var5, var6);
      }
   }

   private static float fraction(int var0, int var1) {
      return (float)var0 / (float)var1;
   }

   private static void vertex(VertexConsumer var0, Matrix4f var1, Matrix3f var2, int var3, float var4, int var5, int var6, int var7) {
      var0.vertex(var1, var4 - 0.5F, (float)var5 - 0.5F, 0.0F).color(255, 255, 255, 255).method_7((float)var6, (float)var7).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(var3).normal(var2, 0.0F, 1.0F, 0.0F).endVertex();
   }

   private static void stringVertex(float var0, float var1, float var2, VertexConsumer var3, PoseStack.Pose var4, float var5, float var6) {
      float var7 = var0 * var5;
      float var8 = var1 * (var5 * var5 + var5) * 0.5F + 0.25F;
      float var9 = var2 * var5;
      float var10 = var0 * var6 - var7;
      float var11 = var1 * (var6 * var6 + var6) * 0.5F + 0.25F - var8;
      float var12 = var2 * var6 - var9;
      float var13 = Mth.sqrt(var10 * var10 + var11 * var11 + var12 * var12);
      var10 /= var13;
      var11 /= var13;
      var12 /= var13;
      var3.vertex(var4.pose(), var7, var8, var9).color(0, 0, 0, 255).normal(var4.normal(), var10, var11, var12).endVertex();
   }

   public ResourceLocation getTextureLocation(FishingHook var1) {
      return TEXTURE_LOCATION;
   }

   static {
      RENDER_TYPE = RenderType.entityCutout(TEXTURE_LOCATION);
   }
}
