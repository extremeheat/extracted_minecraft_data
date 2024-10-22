package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.GoatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.GoatRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.goat.Goat;

public class GoatRenderer extends AgeableMobRenderer<Goat, GoatRenderState, GoatModel> {
   private static final ResourceLocation GOAT_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/goat/goat.png");

   public GoatRenderer(EntityRendererProvider.Context var1) {
      super(var1, new GoatModel(var1.bakeLayer(ModelLayers.GOAT)), new GoatModel(var1.bakeLayer(ModelLayers.GOAT_BABY)), 0.7F);
   }

   public ResourceLocation getTextureLocation(GoatRenderState var1) {
      return GOAT_LOCATION;
   }

   public GoatRenderState createRenderState() {
      return new GoatRenderState();
   }

   public void extractRenderState(Goat var1, GoatRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.hasLeftHorn = var1.hasLeftHorn();
      var2.hasRightHorn = var1.hasRightHorn();
      var2.rammingXHeadRot = var1.getRammingXHeadRot();
   }
}
