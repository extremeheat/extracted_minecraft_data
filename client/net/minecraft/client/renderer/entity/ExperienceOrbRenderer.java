package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;

public class ExperienceOrbRenderer extends EntityRenderer<ExperienceOrb> {
   private static final ResourceLocation EXPERIENCE_ORB_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/experience_orb.png");
   private static final RenderType RENDER_TYPE;

   public ExperienceOrbRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.shadowRadius = 0.15F;
      this.shadowStrength = 0.75F;
   }

   protected int getBlockLightLevel(ExperienceOrb var1, BlockPos var2) {
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
      var4.translate(0.0F, 0.1F, 0.0F);
      var4.mulPose(this.entityRenderDispatcher.cameraOrientation());
      float var20 = 0.3F;
      var4.scale(0.3F, 0.3F, 0.3F);
      VertexConsumer var21 = var5.getBuffer(RENDER_TYPE);
      PoseStack.Pose var22 = var4.last();
      vertex(var21, var22, -0.5F, -0.25F, var17, 255, var19, var8, var11, var6);
      vertex(var21, var22, 0.5F, -0.25F, var17, 255, var19, var9, var11, var6);
      vertex(var21, var22, 0.5F, 0.75F, var17, 255, var19, var9, var10, var6);
      vertex(var21, var22, -0.5F, 0.75F, var17, 255, var19, var8, var10, var6);
      var4.popPose();
      super.render(var1, var2, var3, var4, var5, var6);
   }

   private static void vertex(VertexConsumer var0, PoseStack.Pose var1, float var2, float var3, int var4, int var5, int var6, float var7, float var8, int var9) {
      var0.addVertex(var1, var2, var3, 0.0F).setColor(var4, var5, var6, 128).setUv(var7, var8).setOverlay(OverlayTexture.NO_OVERLAY).setLight(var9).setNormal(var1, 0.0F, 1.0F, 0.0F);
   }

   public ResourceLocation getTextureLocation(ExperienceOrb var1) {
      return EXPERIENCE_ORB_LOCATION;
   }

   static {
      RENDER_TYPE = RenderType.itemEntityTranslucentCull(EXPERIENCE_ORB_LOCATION);
   }
}
