package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ExperienceOrbRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import org.joml.Quaternionfc;

public class ExperienceOrbRenderer extends EntityRenderer<ExperienceOrb, ExperienceOrbRenderState> {
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

   public void submit(ExperienceOrbRenderState var1, PoseStack var2, SubmitNodeCollector var3) {
      var2.pushPose();
      int var4 = var1.icon;
      float var5 = (float)(var4 % 4 * 16 + 0) / 64.0F;
      float var6 = (float)(var4 % 4 * 16 + 16) / 64.0F;
      float var7 = (float)(var4 / 4 * 16 + 0) / 64.0F;
      float var8 = (float)(var4 / 4 * 16 + 16) / 64.0F;
      float var9 = 1.0F;
      float var10 = 0.5F;
      float var11 = 0.25F;
      float var12 = 255.0F;
      float var13 = var1.ageInTicks / 2.0F;
      int var14 = (int)((Mth.sin(var13 + 0.0F) + 1.0F) * 0.5F * 255.0F);
      boolean var15 = true;
      int var16 = (int)((Mth.sin(var13 + 4.1887903F) + 1.0F) * 0.1F * 255.0F);
      var2.translate(0.0F, 0.1F, 0.0F);
      var2.mulPose((Quaternionfc)this.entityRenderDispatcher.cameraOrientation());
      float var17 = 0.3F;
      var2.scale(0.3F, 0.3F, 0.3F);
      var3.submitCustomGeometry(var2, RENDER_TYPE, (var7x, var8x) -> {
         vertex(var8x, var7x, -0.5F, -0.25F, var14, 255, var16, var5, var8, var1.lightCoords);
         vertex(var8x, var7x, 0.5F, -0.25F, var14, 255, var16, var6, var8, var1.lightCoords);
         vertex(var8x, var7x, 0.5F, 0.75F, var14, 255, var16, var6, var7, var1.lightCoords);
         vertex(var8x, var7x, -0.5F, 0.75F, var14, 255, var16, var5, var7, var1.lightCoords);
      });
      var2.popPose();
      super.submit(var1, var2, var3);
   }

   private static void vertex(VertexConsumer var0, PoseStack.Pose var1, float var2, float var3, int var4, int var5, int var6, float var7, float var8, int var9) {
      var0.addVertex(var1, var2, var3, 0.0F).setColor(var4, var5, var6, 128).setUv(var7, var8).setOverlay(OverlayTexture.NO_OVERLAY).setLight(var9).setNormal(var1, 0.0F, 1.0F, 0.0F);
   }

   public ExperienceOrbRenderState createRenderState() {
      return new ExperienceOrbRenderState();
   }

   public void extractRenderState(ExperienceOrb var1, ExperienceOrbRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.icon = var1.getIcon();
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }

   static {
      RENDER_TYPE = RenderType.itemEntityTranslucentCull(EXPERIENCE_ORB_LOCATION);
   }
}
