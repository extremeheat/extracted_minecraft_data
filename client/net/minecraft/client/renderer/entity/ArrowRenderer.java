package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.AbstractArrow;

public abstract class ArrowRenderer<T extends AbstractArrow> extends EntityRenderer<T> {
   public ArrowRenderer(EntityRendererProvider.Context var1) {
      super(var1);
   }

   public void render(T var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      var4.pushPose();
      var4.mulPose(Axis.YP.rotationDegrees(Mth.lerp(var3, var1.yRotO, var1.getYRot()) - 90.0F));
      var4.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(var3, var1.xRotO, var1.getXRot())));
      boolean var7 = false;
      float var8 = 0.0F;
      float var9 = 0.5F;
      float var10 = 0.0F;
      float var11 = 0.15625F;
      float var12 = 0.0F;
      float var13 = 0.15625F;
      float var14 = 0.15625F;
      float var15 = 0.3125F;
      float var16 = 0.05625F;
      float var17 = (float)var1.shakeTime - var3;
      if (var17 > 0.0F) {
         float var18 = -Mth.sin(var17 * 3.0F) * var17;
         var4.mulPose(Axis.ZP.rotationDegrees(var18));
      }

      var4.mulPose(Axis.XP.rotationDegrees(45.0F));
      var4.scale(0.05625F, 0.05625F, 0.05625F);
      var4.translate(-4.0F, 0.0F, 0.0F);
      VertexConsumer var21 = var5.getBuffer(RenderType.entityCutout(this.getTextureLocation(var1)));
      PoseStack.Pose var19 = var4.last();
      this.vertex(var19, var21, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, var6);
      this.vertex(var19, var21, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, var6);
      this.vertex(var19, var21, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, var6);
      this.vertex(var19, var21, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, var6);
      this.vertex(var19, var21, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, var6);
      this.vertex(var19, var21, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, var6);
      this.vertex(var19, var21, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, var6);
      this.vertex(var19, var21, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, var6);

      for(int var20 = 0; var20 < 4; ++var20) {
         var4.mulPose(Axis.XP.rotationDegrees(90.0F));
         this.vertex(var19, var21, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, var6);
         this.vertex(var19, var21, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, var6);
         this.vertex(var19, var21, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, var6);
         this.vertex(var19, var21, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, var6);
      }

      var4.popPose();
      super.render(var1, var2, var3, var4, var5, var6);
   }

   public void vertex(PoseStack.Pose var1, VertexConsumer var2, int var3, int var4, int var5, float var6, float var7, int var8, int var9, int var10, int var11) {
      var2.addVertex(var1, (float)var3, (float)var4, (float)var5).setColor(-1).setUv(var6, var7).setOverlay(OverlayTexture.NO_OVERLAY).setLight(var11).setNormal(var1, (float)var8, (float)var10, (float)var9);
   }
}
