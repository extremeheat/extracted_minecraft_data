package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.effects.EvokerFangsModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.EvokerFangsRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.projectile.EvokerFangs;
import org.joml.Quaternionfc;

public class EvokerFangsRenderer extends EntityRenderer<EvokerFangs, EvokerFangsRenderState> {
   private static final Identifier TEXTURE_LOCATION = Identifier.withDefaultNamespace("textures/entity/illager/evoker_fangs.png");
   private final EvokerFangsModel model;

   public EvokerFangsRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.model = new EvokerFangsModel(var1.bakeLayer(ModelLayers.EVOKER_FANGS));
   }

   public void submit(EvokerFangsRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      float var5 = var1.biteProgress;
      if (var5 != 0.0F) {
         var2.pushPose();
         var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(90.0F - var1.yRot));
         var2.scale(-1.0F, -1.0F, 1.0F);
         var2.translate(0.0F, -1.501F, 0.0F);
         var3.submitModel(this.model, var1, var2, this.model.renderType(TEXTURE_LOCATION), var1.lightCoords, OverlayTexture.NO_OVERLAY, var1.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
         var2.popPose();
         super.submit(var1, var2, var3, var4);
      }
   }

   public EvokerFangsRenderState createRenderState() {
      return new EvokerFangsRenderState();
   }

   public void extractRenderState(EvokerFangs var1, EvokerFangsRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.yRot = var1.getYRot();
      var2.biteProgress = var1.getAnimationProgress(var3);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
