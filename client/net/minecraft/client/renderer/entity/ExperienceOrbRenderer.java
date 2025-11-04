package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ExperienceOrbRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import org.joml.Quaternionfc;

public class ExperienceOrbRenderer extends EntityRenderer<ExperienceOrb, ExperienceOrbRenderState> {
   private static final Identifier EXPERIENCE_ORB_LOCATION = Identifier.withDefaultNamespace("textures/entity/experience_orb.png");
   private static final RenderType RENDER_TYPE;

   public ExperienceOrbRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.shadowRadius = 0.15F;
      this.shadowStrength = 0.75F;
   }

   protected int getBlockLightLevel(ExperienceOrb var1, BlockPos var2) {
      return Mth.clamp(super.getBlockLightLevel(var1, var2) + 7, 0, 15);
   }

   public void submit(ExperienceOrbRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
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
      int var15 = (int)((Mth.sin((double)(var14 + 0.0F)) + 1.0F) * 0.5F * 255.0F);
      boolean var16 = true;
      int var17 = (int)((Mth.sin((double)(var14 + 4.1887903F)) + 1.0F) * 0.1F * 255.0F);
      var2.translate(0.0F, 0.1F, 0.0F);
      var2.mulPose((Quaternionfc)var4.orientation);
      float var18 = 0.3F;
      var2.scale(0.3F, 0.3F, 0.3F);
      var3.submitCustomGeometry(var2, RENDER_TYPE, (var7x, var8x) -> {
         vertex(var8x, var7x, -0.5F, -0.25F, var15, 255, var17, var6, var9, var1.lightCoords);
         vertex(var8x, var7x, 0.5F, -0.25F, var15, 255, var17, var7, var9, var1.lightCoords);
         vertex(var8x, var7x, 0.5F, 0.75F, var15, 255, var17, var7, var8, var1.lightCoords);
         vertex(var8x, var7x, -0.5F, 0.75F, var15, 255, var17, var6, var8, var1.lightCoords);
      });
      var2.popPose();
      super.submit(var1, var2, var3, var4);
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
      RENDER_TYPE = RenderTypes.itemEntityTranslucentCull(EXPERIENCE_ORB_LOCATION);
   }
}
