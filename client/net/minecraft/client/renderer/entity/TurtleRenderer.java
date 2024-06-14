package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.TurtleModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Turtle;

public class TurtleRenderer extends MobRenderer<Turtle, TurtleModel<Turtle>> {
   private static final ResourceLocation TURTLE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/turtle/big_sea_turtle.png");

   public TurtleRenderer(EntityRendererProvider.Context var1) {
      super(var1, new TurtleModel<>(var1.bakeLayer(ModelLayers.TURTLE)), 0.7F);
   }

   protected float getShadowRadius(Turtle var1) {
      float var2 = super.getShadowRadius(var1);
      return var1.isBaby() ? var2 * 0.83F : var2;
   }

   public ResourceLocation getTextureLocation(Turtle var1) {
      return TURTLE_LOCATION;
   }
}
