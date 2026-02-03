package net.minecraft.client.renderer.entity;

import java.util.function.UnaryOperator;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.object.boat.RaftModel;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

public class RaftRenderer extends AbstractBoatRenderer {
   private final EntityModel<BoatRenderState> model;
   private final Identifier texture;

   public RaftRenderer(final EntityRendererProvider.Context context, final ModelLayerLocation modelId) {
      super(context);
      this.texture = modelId.model().withPath((UnaryOperator)((p) -> "textures/entity/" + p + ".png"));
      this.model = new RaftModel(context.bakeLayer(modelId));
   }

   protected EntityModel<BoatRenderState> model() {
      return this.model;
   }

   protected RenderType renderType() {
      return this.model.renderType(this.texture);
   }
}
