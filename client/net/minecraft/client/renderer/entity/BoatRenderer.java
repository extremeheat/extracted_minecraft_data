package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.UnaryOperator;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;

public class BoatRenderer extends AbstractBoatRenderer {
   private final Model.Simple waterPatchModel;
   private final Identifier texture;
   private final EntityModel<BoatRenderState> model;

   public BoatRenderer(final EntityRendererProvider.Context context, final ModelLayerLocation modelId) {
      super(context);
      this.texture = modelId.model().withPath((UnaryOperator)((p) -> "textures/entity/" + p + ".png"));
      this.waterPatchModel = new Model.Simple(context.bakeLayer(ModelLayers.BOAT_WATER_PATCH), (t) -> RenderTypes.waterMask());
      this.model = new BoatModel(context.bakeLayer(modelId));
   }

   protected EntityModel<BoatRenderState> model() {
      return this.model;
   }

   protected RenderType renderType() {
      return this.model.renderType(this.texture);
   }

   protected void submitTypeAdditions(final BoatRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords) {
      if (!state.isUnderWater) {
         submitNodeCollector.submitModel(this.waterPatchModel, Unit.INSTANCE, poseStack, this.waterPatchModel.renderType(this.texture), lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      }

   }
}
