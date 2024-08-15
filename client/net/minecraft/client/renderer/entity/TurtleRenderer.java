package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.TurtleModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.TurtleRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Turtle;

public class TurtleRenderer extends AgeableMobRenderer<Turtle, TurtleRenderState, TurtleModel> {
   private static final ResourceLocation TURTLE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/turtle/big_sea_turtle.png");

   public TurtleRenderer(EntityRendererProvider.Context var1) {
      super(var1, new TurtleModel(var1.bakeLayer(ModelLayers.TURTLE)), new TurtleModel(var1.bakeLayer(ModelLayers.TURTLE_BABY)), 0.7F);
   }

   protected float getShadowRadius(TurtleRenderState var1) {
      float var2 = super.getShadowRadius(var1);
      return var1.isBaby ? var2 * 0.83F : var2;
   }

   public TurtleRenderState createRenderState() {
      return new TurtleRenderState();
   }

   public void extractRenderState(Turtle var1, TurtleRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isOnLand = !var1.isInWater() && var1.onGround();
      var2.isLayingEgg = var1.isLayingEgg();
      var2.hasEgg = !var1.isBaby() && var1.hasEgg();
   }

   public ResourceLocation getTextureLocation(TurtleRenderState var1) {
      return TURTLE_LOCATION;
   }
}
