package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.PigModel;
import net.minecraft.client.renderer.entity.layers.PigSaddleLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Pig;

public class PigRenderer extends MobRenderer<Pig, PigModel<Pig>> {
   private static final ResourceLocation PIG_LOCATION = new ResourceLocation("textures/entity/pig/pig.png");

   public PigRenderer(EntityRenderDispatcher var1) {
      super(var1, new PigModel(), 0.7F);
      this.addLayer(new PigSaddleLayer(this));
   }

   protected ResourceLocation getTextureLocation(Pig var1) {
      return PIG_LOCATION;
   }
}
