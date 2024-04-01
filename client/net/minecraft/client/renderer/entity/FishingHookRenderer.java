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
import net.minecraft.world.item.Item;
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
         Vec3 var10 = getPlayerHandPos(var7, var3, Items.FISHING_ROD, this.entityRenderDispatcher);
         double var11 = Mth.lerp((double)var3, var1.xo, var1.getX());
         double var13 = Mth.lerp((double)var3, var1.yo, var1.getY()) + 0.25;
         double var15 = Mth.lerp((double)var3, var1.zo, var1.getZ());
         float var17 = (float)(var10.x - var11);
         float var18 = (float)(var10.y - var13);
         float var19 = (float)(var10.z - var15);
         VertexConsumer var20 = var5.getBuffer(RenderType.lineStrip());
         PoseStack.Pose var21 = var4.last();
         boolean var22 = true;

         for(int var23 = 0; var23 <= 16; ++var23) {
            stringVertex(var17, var18, var19, var20, var21, fraction(var23, 16), fraction(var23 + 1, 16));
         }

         var4.popPose();
         super.render(var1, var2, var3, var4, var5, var6);
      }
   }

   public static Vec3 getPlayerHandPos(Player var0, float var1, Item var2, EntityRenderDispatcher var3) {
      int var4 = var0.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
      ItemStack var5 = var0.getMainHandItem();
      if (!var5.is(var2)) {
         var4 = -var4;
      }

      float var6 = var0.getAttackAnim(var1);
      float var7 = Mth.sin(Mth.sqrt(var6) * 3.1415927F);
      float var8 = Mth.lerp(var1, var0.yBodyRotO, var0.yBodyRot) * 0.017453292F;
      double var9 = (double)Mth.sin(var8);
      double var11 = (double)Mth.cos(var8);
      double var13 = (double)var4 * 0.35;
      double var15 = 0.8;
      if ((var3.options == null || var3.options.getCameraType().isFirstPerson()) && var0 == Minecraft.getInstance().player) {
         double var20 = 960.0 / (double)var3.options.fov().get().intValue();
         Vec3 var19 = var3.camera.getNearPlane().getPointOnPlane((float)var4 * 0.525F, -0.1F);
         var19 = var19.scale(var20);
         var19 = var19.yRot(var7 * 0.5F);
         var19 = var19.xRot(-var7 * 0.7F);
         return new Vec3(
            Mth.lerp((double)var1, var0.xo, var0.getX()) + var19.x,
            Mth.lerp((double)var1, var0.yo, var0.getY()) + var19.y + (double)var0.getEyeHeight(),
            Mth.lerp((double)var1, var0.zo, var0.getZ()) + var19.z
         );
      } else {
         float var17 = var0.isCrouching() ? -0.1875F : 0.0F;
         return new Vec3(
            Mth.lerp((double)var1, var0.xo, var0.getX()) - var11 * var13 - var9 * 0.8,
            var0.yo + (double)var0.getEyeHeight() + (var0.getY() - var0.yo) * (double)var1 - 0.45 + (double)var17,
            Mth.lerp((double)var1, var0.zo, var0.getZ()) - var9 * var13 + var11 * 0.8
         );
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
