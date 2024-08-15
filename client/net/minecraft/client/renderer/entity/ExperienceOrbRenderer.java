package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.ExperienceOrbRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;

public class ExperienceOrbRenderer extends EntityRenderer<ExperienceOrb, ExperienceOrbRenderState> {
   private static final ResourceLocation EXPERIENCE_ORB_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/experience_orb.png");
   private static final RenderType RENDER_TYPE = RenderType.itemEntityTranslucentCull(EXPERIENCE_ORB_LOCATION);

   public ExperienceOrbRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.shadowRadius = 0.15F;
      this.shadowStrength = 0.75F;
   }

   protected int getBlockLightLevel(ExperienceOrb var1, BlockPos var2) {
      return Mth.clamp(super.getBlockLightLevel(var1, var2) + 7, 0, 15);
   }

   public void render(ExperienceOrbRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      var2.pushPose();
      int var5 = var1.icon;
      float var6 = (float)(var5 % 4 * 16 + 0) / 64.0F;
      float var7 = (float)(var5 % 4 * 16 + 16) / 64.0F;
      float var8 = (float)(var5 / 4 * 16 + 0) / 64.0F;
      float var9 = (float)(var5 / 4 * 16 + 16) / 64.0F;
      float var10 = 1.0F;
      float var11 = 0.5F;
      float var12 = 0.25F;
      float var13 = 255.0F;
      float var14 = var1.ageInTicks / 2.0F;
      int var15 = (int)((Mth.sin(var14 + 0.0F) + 1.0F) * 0.5F * 255.0F);
      short var16 = 255;
      int var17 = (int)((Mth.sin(var14 + 4.1887903F) + 1.0F) * 0.1F * 255.0F);
      var2.translate(0.0F, 0.1F, 0.0F);
      var2.mulPose(this.entityRenderDispatcher.cameraOrientation());
      float var18 = 0.3F;
      var2.scale(0.3F, 0.3F, 0.3F);
      VertexConsumer var19 = var3.getBuffer(RENDER_TYPE);
      PoseStack.Pose var20 = var2.last();
      vertex(var19, var20, -0.5F, -0.25F, var15, 255, var17, var6, var9, var4);
      vertex(var19, var20, 0.5F, -0.25F, var15, 255, var17, var7, var9, var4);
      vertex(var19, var20, 0.5F, 0.75F, var15, 255, var17, var7, var8, var4);
      vertex(var19, var20, -0.5F, 0.75F, var15, 255, var17, var6, var8, var4);
      var2.popPose();
      super.render(var1, var2, var3, var4);
   }

   private static void vertex(VertexConsumer var0, PoseStack.Pose var1, float var2, float var3, int var4, int var5, int var6, float var7, float var8, int var9) {
      var0.addVertex(var1, var2, var3, 0.0F)
         .setColor(var4, var5, var6, 128)
         .setUv(var7, var8)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(var9)
         .setNormal(var1, 0.0F, 1.0F, 0.0F);
   }

   public ResourceLocation getTextureLocation(ExperienceOrbRenderState var1) {
      return EXPERIENCE_ORB_LOCATION;
   }

   public ExperienceOrbRenderState createRenderState() {
      return new ExperienceOrbRenderState();
   }

   public void extractRenderState(ExperienceOrb var1, ExperienceOrbRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.icon = var1.getIcon();
   }
}
