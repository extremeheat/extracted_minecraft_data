package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.leash.LeashKnotModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;

public class LeashKnotRenderer extends EntityRenderer<LeashFenceKnotEntity, EntityRenderState> {
   private static final Identifier KNOT_LOCATION = Identifier.withDefaultNamespace("textures/entity/lead_knot.png");
   private final LeashKnotModel model;

   public LeashKnotRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.model = new LeashKnotModel(var1.bakeLayer(ModelLayers.LEASH_KNOT));
   }

   public void submit(EntityRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      var2.pushPose();
      var2.scale(-1.0F, -1.0F, 1.0F);
      var3.submitModel(this.model, var1, var2, this.model.renderType(KNOT_LOCATION), var1.lightCoords, OverlayTexture.NO_OVERLAY, var1.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      var2.popPose();
      super.submit(var1, var2, var3, var4);
   }

   public EntityRenderState createRenderState() {
      return new EntityRenderState();
   }
}
