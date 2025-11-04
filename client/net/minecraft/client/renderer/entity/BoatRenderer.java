package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.UnaryOperator;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
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

   public BoatRenderer(EntityRendererProvider.Context var1, ModelLayerLocation var2) {
      super(var1);
      this.texture = var2.model().withPath((UnaryOperator)((var0) -> "textures/entity/" + var0 + ".png"));
      this.waterPatchModel = new Model.Simple(var1.bakeLayer(ModelLayers.BOAT_WATER_PATCH), (var0) -> RenderTypes.waterMask());
      this.model = new BoatModel(var1.bakeLayer(var2));
   }

   protected EntityModel<BoatRenderState> model() {
      return this.model;
   }

   protected RenderType renderType() {
      return this.model.renderType(this.texture);
   }

   protected void submitTypeAdditions(BoatRenderState var1, PoseStack var2, SubmitNodeCollector var3, int var4) {
      if (!var1.isUnderWater) {
         var3.submitModel(this.waterPatchModel, Unit.INSTANCE, var2, this.waterPatchModel.renderType(this.texture), var4, OverlayTexture.NO_OVERLAY, var1.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      }

   }
}
