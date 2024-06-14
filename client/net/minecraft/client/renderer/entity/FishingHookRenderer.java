package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
   private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/fishing_hook.png");
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
         PoseStack.Pose var8 = var4.last();
         VertexConsumer var9 = var5.getBuffer(RENDER_TYPE);
         vertex(var9, var8, var6, 0.0F, 0, 0, 1);
         vertex(var9, var8, var6, 1.0F, 0, 1, 1);
         vertex(var9, var8, var6, 1.0F, 1, 1, 0);
         vertex(var9, var8, var6, 0.0F, 1, 0, 0);
         var4.popPose();
         float var10 = var7.getAttackAnim(var3);
         float var11 = Mth.sin(Mth.sqrt(var10) * 3.1415927F);
         Vec3 var12 = this.getPlayerHandPos(var7, var11, var3);
         Vec3 var13 = var1.getPosition(var3).add(0.0, 0.25, 0.0);
         float var14 = (float)(var12.x - var13.x);
         float var15 = (float)(var12.y - var13.y);
         float var16 = (float)(var12.z - var13.z);
         VertexConsumer var17 = var5.getBuffer(RenderType.lineStrip());
         PoseStack.Pose var18 = var4.last();
         byte var19 = 16;

         for (int var20 = 0; var20 <= 16; var20++) {
            stringVertex(var14, var15, var16, var17, var18, fraction(var20, 16), fraction(var20 + 1, 16));
         }

         var4.popPose();
         super.render(var1, var2, var3, var4, var5, var6);
      }
   }

   private Vec3 getPlayerHandPos(Player var1, float var2, float var3) {
      int var4 = var1.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
      ItemStack var5 = var1.getMainHandItem();
      if (!var5.is(Items.FISHING_ROD)) {
         var4 = -var4;
      }

      if (this.entityRenderDispatcher.options.getCameraType().isFirstPerson() && var1 == Minecraft.getInstance().player) {
         double var17 = 960.0 / (double)this.entityRenderDispatcher.options.fov().get().intValue();
         Vec3 var8 = this.entityRenderDispatcher
            .camera
            .getNearPlane()
            .getPointOnPlane((float)var4 * 0.525F, -0.1F)
            .scale(var17)
            .yRot(var2 * 0.5F)
            .xRot(-var2 * 0.7F);
         return var1.getEyePosition(var3).add(var8);
      } else {
         float var6 = Mth.lerp(var3, var1.yBodyRotO, var1.yBodyRot) * 0.017453292F;
         double var7 = (double)Mth.sin(var6);
         double var9 = (double)Mth.cos(var6);
         float var11 = var1.getScale();
         double var12 = (double)var4 * 0.35 * (double)var11;
         double var14 = 0.8 * (double)var11;
         float var16 = var1.isCrouching() ? -0.1875F : 0.0F;
         return var1.getEyePosition(var3).add(-var9 * var12 - var7 * var14, (double)var16 - 0.45 * (double)var11, -var7 * var12 + var9 * var14);
      }
   }

   private static float fraction(int var0, int var1) {
      return (float)var0 / (float)var1;
   }

   private static void vertex(VertexConsumer var0, PoseStack.Pose var1, int var2, float var3, int var4, int var5, int var6) {
      var0.addVertex(var1, var3 - 0.5F, (float)var4 - 0.5F, 0.0F)
         .setColor(-1)
         .setUv((float)var5, (float)var6)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(var2)
         .setNormal(var1, 0.0F, 1.0F, 0.0F);
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
      var3.addVertex(var4, var7, var8, var9).setColor(-16777216).setNormal(var4, var10, var11, var12);
   }

   public ResourceLocation getTextureLocation(FishingHook var1) {
      return TEXTURE_LOCATION;
   }
}
