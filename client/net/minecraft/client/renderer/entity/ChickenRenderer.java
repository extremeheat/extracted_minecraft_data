package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Chicken;

public class ChickenRenderer extends MobRenderer<Chicken, ChickenModel<Chicken>> {
   private static final ResourceLocation CHICKEN_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/chicken.png");

   public ChickenRenderer(EntityRendererProvider.Context var1) {
      super(var1, new ChickenModel(var1.bakeLayer(ModelLayers.CHICKEN)), 0.3F);
   }

   public ResourceLocation getTextureLocation(Chicken var1) {
      return CHICKEN_LOCATION;
   }

   protected float getBob(Chicken var1, float var2) {
      float var3 = Mth.lerp(var2, var1.oFlap, var1.flap);
      float var4 = Mth.lerp(var2, var1.oFlapSpeed, var1.flapSpeed);
      return (Mth.sin(var3) + 1.0F) * var4;
   }
}
