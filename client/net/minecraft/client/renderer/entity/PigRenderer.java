package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.client.renderer.entity.state.PigRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Pig;

public class PigRenderer extends AgeableMobRenderer<Pig, PigRenderState, PigModel> {
   private static final ResourceLocation PIG_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/pig/pig.png");

   public PigRenderer(EntityRendererProvider.Context var1) {
      super(var1, new PigModel(var1.bakeLayer(ModelLayers.PIG)), new PigModel(var1.bakeLayer(ModelLayers.PIG_BABY)), 0.7F);
      this.addLayer(
         new SaddleLayer<>(
            this,
            new PigModel(var1.bakeLayer(ModelLayers.PIG_SADDLE)),
            new PigModel(var1.bakeLayer(ModelLayers.PIG_BABY_SADDLE)),
            ResourceLocation.withDefaultNamespace("textures/entity/pig/pig_saddle.png")
         )
      );
   }

   public ResourceLocation getTextureLocation(PigRenderState var1) {
      return PIG_LOCATION;
   }

   public PigRenderState createRenderState() {
      return new PigRenderState();
   }

   public void extractRenderState(Pig var1, PigRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isSaddled = var1.isSaddled();
   }
}
