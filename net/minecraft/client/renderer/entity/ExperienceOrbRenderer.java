package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;

public class ExperienceOrbRenderer extends EntityRenderer {
   private static final ResourceLocation EXPERIENCE_ORB_LOCATION = new ResourceLocation("textures/entity/experience_orb.png");
   private static final RenderType RENDER_TYPE;

   public ExperienceOrbRenderer(EntityRenderDispatcher var1) {
      super(var1);
      this.shadowRadius = 0.15F;
      this.shadowStrength = 0.75F;
   }

   protected int getBlockLightLevel(ExperienceOrb var1, float var2) {
      return Mth.clamp(super.getBlockLightLevel(var1, var2) + 7, 0, 15);
   }

   public void render(ExperienceOrb var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      var4.pushPose();
      int var7 = var1.getIcon();
      float var8 = (float)(var7 % 4 * 16 + 0) / 64.0F;
      float var9 = (float)(var7 % 4 * 16 + 16) / 64.0F;
      float var10 = (float)(var7 / 4 * 16 + 0) / 64.0F;
      float var11 = (float)(var7 / 4 * 16 + 16) / 64.0F;
      float var12 = 1.0F;
      float var13 = 0.5F;
      float var14 = 0.25F;
      float var15 = 255.0F;
      float var16 = ((float)var1.tickCount + var3) / 2.0F;
      int var17 = (int)((Mth.sin(var16 + 0.0F) + 1.0F) * 0.5F * 255.0F);
      boolean var18 = true;
      int var19 = (int)((Mth.sin(var16 + 4.1887903F) + 1.0F) * 0.1F * 255.0F);
      var4.translate(0.0D, 0.10000000149011612D, 0.0D);
      var4.mulPose(this.entityRenderDispatcher.cameraOrientation());
      var4.mulPose(Vector3f.YP.rotationDegrees(180.0F));
      float var20 = 0.3F;
      var4.scale(0.3F, 0.3F, 0.3F);
      VertexConsumer var21 = var5.getBuffer(RENDER_TYPE);
      PoseStack.Pose var22 = var4.last();
      Matrix4f var23 = var22.pose();
      Matrix3f var24 = var22.normal();
      vertex(var21, var23, var24, -0.5F, -0.25F, var17, 255, var19, var8, var11, var6);
      vertex(var21, var23, var24, 0.5F, -0.25F, var17, 255, var19, var9, var11, var6);
      vertex(var21, var23, var24, 0.5F, 0.75F, var17, 255, var19, var9, var10, var6);
      vertex(var21, var23, var24, -0.5F, 0.75F, var17, 255, var19, var8, var10, var6);
      var4.popPose();
      super.render(var1, var2, var3, var4, var5, var6);
   }

   private static void vertex(VertexConsumer var0, Matrix4f var1, Matrix3f var2, float var3, float var4, int var5, int var6, int var7, float var8, float var9, int var10) {
      var0.vertex(var1, var3, var4, 0.0F).color(var5, var6, var7, 128).uv(var8, var9).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(var10).normal(var2, 0.0F, 1.0F, 0.0F).endVertex();
   }

   public ResourceLocation getTextureLocation(ExperienceOrb var1) {
      return EXPERIENCE_ORB_LOCATION;
   }

   static {
      RENDER_TYPE = RenderType.entityTranslucent(EXPERIENCE_ORB_LOCATION);
   }
}
