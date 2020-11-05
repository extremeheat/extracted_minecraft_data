package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SheepModel;
import net.minecraft.client.renderer.entity.layers.SheepFurLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;

public class SheepRenderer extends MobRenderer<Sheep, SheepModel<Sheep>> {
   private static final ResourceLocation SHEEP_LOCATION = new ResourceLocation("textures/entity/sheep/sheep.png");

   public SheepRenderer(EntityRenderDispatcher var1) {
      super(var1, new SheepModel(), 0.7F);
      this.addLayer(new SheepFurLayer(this));
   }

   public ResourceLocation getTextureLocation(Sheep var1) {
      return SHEEP_LOCATION;
   }
}
