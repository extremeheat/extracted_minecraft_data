package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.AbstractArrow;

public abstract class ArrowRenderer<T extends AbstractArrow> extends EntityRenderer<T> {
   public ArrowRenderer(EntityRenderDispatcher var1) {
      super(var1);
   }

   public void render(T var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      var4.pushPose();
      var4.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(var3, var1.yRotO, var1.yRot) - 90.0F));
      var4.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(var3, var1.xRotO, var1.xRot)));
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
         var4.mulPose(Vector3f.ZP.rotationDegrees(var18));
      }

      var4.mulPose(Vector3f.XP.rotationDegrees(45.0F));
      var4.scale(0.05625F, 0.05625F, 0.05625F);
      var4.translate(-4.0D, 0.0D, 0.0D);
      VertexConsumer var23 = var5.getBuffer(RenderType.entityCutout(this.getTextureLocation(var1)));
      PoseStack.Pose var19 = var4.last();
      Matrix4f var20 = var19.pose();
      Matrix3f var21 = var19.normal();
      this.vertex(var20, var21, var23, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, var6);
      this.vertex(var20, var21, var23, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, var6);
      this.vertex(var20, var21, var23, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, var6);
      this.vertex(var20, var21, var23, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, var6);
      this.vertex(var20, var21, var23, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, var6);
      this.vertex(var20, var21, var23, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, var6);
      this.vertex(var20, var21, var23, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, var6);
      this.vertex(var20, var21, var23, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, var6);

      for(int var22 = 0; var22 < 4; ++var22) {
         var4.mulPose(Vector3f.XP.rotationDegrees(90.0F));
         this.vertex(var20, var21, var23, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, var6);
         this.vertex(var20, var21, var23, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, var6);
         this.vertex(var20, var21, var23, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, var6);
         this.vertex(var20, var21, var23, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, var6);
      }

      var4.popPose();
      super.render(var1, var2, var3, var4, var5, var6);
   }

   public void vertex(Matrix4f var1, Matrix3f var2, VertexConsumer var3, int var4, int var5, int var6, float var7, float var8, int var9, int var10, int var11, int var12) {
      var3.vertex(var1, (float)var4, (float)var5, (float)var6).color(255, 255, 255, 255).uv(var7, var8).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(var12).normal(var2, (float)var9, (float)var11, (float)var10).endVertex();
   }
}
