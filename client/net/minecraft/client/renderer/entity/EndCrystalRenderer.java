package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EndCrystalModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.state.EndCrystalRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.phys.Vec3;

public class EndCrystalRenderer extends EntityRenderer<EndCrystal, EndCrystalRenderState> {
   private static final ResourceLocation END_CRYSTAL_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/end_crystal/end_crystal.png");
   private static final RenderType RENDER_TYPE;
   private final EndCrystalModel model;

   public EndCrystalRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.shadowRadius = 0.5F;
      this.model = new EndCrystalModel(var1.bakeLayer(ModelLayers.END_CRYSTAL));
   }

   public void render(EndCrystalRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      var2.pushPose();
      var2.scale(2.0F, 2.0F, 2.0F);
      var2.translate(0.0F, -0.5F, 0.0F);
      this.model.setupAnim(var1);
      this.model.renderToBuffer(var2, var3.getBuffer(RENDER_TYPE), var4, OverlayTexture.NO_OVERLAY);
      var2.popPose();
      Vec3 var5 = var1.beamOffset;
      if (var5 != null) {
         float var6 = getY(var1.ageInTicks);
         float var7 = (float)var5.x;
         float var8 = (float)var5.y;
         float var9 = (float)var5.z;
         var2.translate(var5);
         EnderDragonRenderer.renderCrystalBeams(-var7, -var8 + var6, -var9, var1.ageInTicks, var2, var3, var4);
      }

      super.render(var1, var2, var3, var4);
   }

   public static float getY(float var0) {
      float var1 = Mth.sin(var0 * 0.2F) / 2.0F + 0.5F;
      var1 = (var1 * var1 + var1) * 0.4F;
      return var1 - 1.4F;
   }

   public EndCrystalRenderState createRenderState() {
      return new EndCrystalRenderState();
   }

   public void extractRenderState(EndCrystal var1, EndCrystalRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.ageInTicks = (float)var1.time + var3;
      var2.showsBottom = var1.showsBottom();
      BlockPos var4 = var1.getBeamTarget();
      if (var4 != null) {
         var2.beamOffset = Vec3.atCenterOf(var4).subtract(var1.getPosition(var3));
      } else {
         var2.beamOffset = null;
      }

   }

   public boolean shouldRender(EndCrystal var1, Frustum var2, double var3, double var5, double var7) {
      return super.shouldRender(var1, var2, var3, var5, var7) || var1.getBeamTarget() != null;
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }

   static {
      RENDER_TYPE = RenderType.entityCutoutNoCull(END_CRYSTAL_LOCATION);
   }
}
