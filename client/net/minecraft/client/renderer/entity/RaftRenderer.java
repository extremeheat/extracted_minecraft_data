package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.RaftModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.resources.ResourceLocation;

public class RaftRenderer extends AbstractBoatRenderer {
   private final EntityModel<BoatRenderState> model;
   private final ResourceLocation texture;

   public RaftRenderer(EntityRendererProvider.Context var1, ModelLayerLocation var2) {
      super(var1);
      this.texture = var2.model().withPath((var0) -> {
         return "textures/entity/" + var0 + ".png";
      });
      this.model = new RaftModel(var1.bakeLayer(var2));
   }

   protected EntityModel<BoatRenderState> model() {
      return this.model;
   }

   protected RenderType renderType() {
      return this.model.renderType(this.texture);
   }
}
