package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
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
   private static final RenderType RENDER_TYPE = RenderType.entityCutout(TEXTURE_LOCATION);
   private static final double VIEW_BOBBING_SCALE = 960.0;

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
         var4.mulPose(Axis.YP.rotationDegrees(180.0F));
         PoseStack.Pose var8 = var4.last();
         VertexConsumer var9 = var5.getBuffer(RENDER_TYPE);
         vertex(var9, var8, var6, 0.0F, 0, 0, 1);
         vertex(var9, var8, var6, 1.0F, 0, 1, 1);
         vertex(var9, var8, var6, 1.0F, 1, 1, 0);
         vertex(var9, var8, var6, 0.0F, 1, 0, 0);
         var4.popPose();
         int var10 = var7.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
         ItemStack var11 = var7.getMainHandItem();
         if (!var11.is(Items.FISHING_ROD)) {
            var10 = -var10;
         }

         float var12 = var7.getAttackAnim(var3);
         float var13 = Mth.sin(Mth.sqrt(var12) * 3.1415927F);
         float var14 = Mth.lerp(var3, var7.yBodyRotO, var7.yBodyRot) * 0.017453292F;
         double var15 = (double)Mth.sin(var14);
         double var17 = (double)Mth.cos(var14);
         double var19 = (double)var10 * 0.35;
         double var21 = 0.8;
         double var23;
         double var25;
         double var27;
         float var29;
         if ((this.entityRenderDispatcher.options == null || this.entityRenderDispatcher.options.getCameraType().isFirstPerson())
            && var7 == Minecraft.getInstance().player) {
            double var30 = 960.0 / (double)this.entityRenderDispatcher.options.fov().get().intValue();
            Vec3 var32 = this.entityRenderDispatcher.camera.getNearPlane().getPointOnPlane((float)var10 * 0.525F, -0.1F);
            var32 = var32.scale(var30);
            var32 = var32.yRot(var13 * 0.5F);
            var32 = var32.xRot(-var13 * 0.7F);
            var23 = Mth.lerp((double)var3, var7.xo, var7.getX()) + var32.x;
            var25 = Mth.lerp((double)var3, var7.yo, var7.getY()) + var32.y;
            var27 = Mth.lerp((double)var3, var7.zo, var7.getZ()) + var32.z;
            var29 = var7.getEyeHeight();
         } else {
            var23 = Mth.lerp((double)var3, var7.xo, var7.getX()) - var17 * var19 - var15 * 0.8;
            var25 = var7.yo + (double)var7.getEyeHeight() + (var7.getY() - var7.yo) * (double)var3 - 0.45;
            var27 = Mth.lerp((double)var3, var7.zo, var7.getZ()) - var15 * var19 + var17 * 0.8;
            var29 = var7.isCrouching() ? -0.1875F : 0.0F;
         }

         double var43 = Mth.lerp((double)var3, var1.xo, var1.getX());
         double var47 = Mth.lerp((double)var3, var1.yo, var1.getY()) + 0.25;
         double var34 = Mth.lerp((double)var3, var1.zo, var1.getZ());
         float var36 = (float)(var23 - var43);
         float var37 = (float)(var25 - var47) + var29;
         float var38 = (float)(var27 - var34);
         VertexConsumer var39 = var5.getBuffer(RenderType.lineStrip());
         PoseStack.Pose var40 = var4.last();
         byte var41 = 16;

         for (int var42 = 0; var42 <= 16; var42++) {
            stringVertex(var36, var37, var38, var39, var40, fraction(var42, 16), fraction(var42 + 1, 16));
         }

         var4.popPose();
         super.render(var1, var2, var3, var4, var5, var6);
      }
   }

   private static float fraction(int var0, int var1) {
      return (float)var0 / (float)var1;
   }

   private static void vertex(VertexConsumer var0, PoseStack.Pose var1, int var2, float var3, int var4, int var5, int var6) {
      var0.vertex(var1, var3 - 0.5F, (float)var4 - 0.5F, 0.0F)
         .color(255, 255, 255, 255)
         .uv((float)var5, (float)var6)
         .overlayCoords(OverlayTexture.NO_OVERLAY)
         .uv2(var2)
         .normal(var1, 0.0F, 1.0F, 0.0F)
         .endVertex();
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
      var3.vertex(var4, var7, var8, var9).color(0, 0, 0, 255).normal(var4, var10, var11, var12).endVertex();
   }

   public ResourceLocation getTextureLocation(FishingHook var1) {
      return TEXTURE_LOCATION;
   }
}
