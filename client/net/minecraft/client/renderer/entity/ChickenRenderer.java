package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.ChickenRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Chicken;

public class ChickenRenderer extends AgeableMobRenderer<Chicken, ChickenRenderState, ChickenModel> {
   private static final ResourceLocation CHICKEN_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/chicken.png");

   public ChickenRenderer(EntityRendererProvider.Context var1) {
      super(var1, new ChickenModel(var1.bakeLayer(ModelLayers.CHICKEN)), new ChickenModel(var1.bakeLayer(ModelLayers.CHICKEN_BABY)), 0.3F);
   }

   public ResourceLocation getTextureLocation(ChickenRenderState var1) {
      return CHICKEN_LOCATION;
   }

   public ChickenRenderState createRenderState() {
      return new ChickenRenderState();
   }

   public void extractRenderState(Chicken var1, ChickenRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.flap = Mth.lerp(var3, var1.oFlap, var1.flap);
      var2.flapSpeed = Mth.lerp(var3, var1.oFlapSpeed, var1.flapSpeed);
   }
}
