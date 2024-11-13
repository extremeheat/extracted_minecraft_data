package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.UnaryOperator;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class BoatRenderer extends AbstractBoatRenderer {
   private final Model waterPatchModel;
   private final ResourceLocation texture;
   private final EntityModel<BoatRenderState> model;

   public BoatRenderer(EntityRendererProvider.Context var1, ModelLayerLocation var2) {
      super(var1);
      this.texture = var2.model().withPath((UnaryOperator)((var0) -> "textures/entity/" + var0 + ".png"));
      this.waterPatchModel = new Model.Simple(var1.bakeLayer(ModelLayers.BOAT_WATER_PATCH), (var0) -> RenderType.waterMask());
      this.model = new BoatModel(var1.bakeLayer(var2));
   }

   protected EntityModel<BoatRenderState> model() {
      return this.model;
   }

   protected RenderType renderType() {
      return this.model.renderType(this.texture);
   }

   protected void renderTypeAdditions(BoatRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      if (!var1.isUnderWater) {
         this.waterPatchModel.renderToBuffer(var2, var3.getBuffer(this.waterPatchModel.renderType(this.texture)), var4, OverlayTexture.NO_OVERLAY);
      }

   }
}
