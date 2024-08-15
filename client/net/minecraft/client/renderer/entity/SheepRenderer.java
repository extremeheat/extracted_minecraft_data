package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SheepWoolLayer;
import net.minecraft.client.renderer.entity.state.SheepRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;

public class SheepRenderer extends AgeableMobRenderer<Sheep, SheepRenderState, SheepModel> {
   private static final ResourceLocation SHEEP_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/sheep/sheep.png");

   public SheepRenderer(EntityRendererProvider.Context var1) {
      super(var1, new SheepModel(var1.bakeLayer(ModelLayers.SHEEP)), new SheepModel(var1.bakeLayer(ModelLayers.SHEEP_BABY)), 0.7F);
      this.addLayer(new SheepWoolLayer(this, var1.getModelSet()));
   }

   public ResourceLocation getTextureLocation(SheepRenderState var1) {
      return SHEEP_LOCATION;
   }

   public SheepRenderState createRenderState() {
      return new SheepRenderState();
   }

   public void extractRenderState(Sheep var1, SheepRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.headEatAngleScale = var1.getHeadEatAngleScale(var3);
      var2.headEatPositionScale = var1.getHeadEatPositionScale(var3);
      var2.isSheared = var1.isSheared();
      var2.woolColor = var1.getColor();
      var2.id = var1.getId();
   }
}
