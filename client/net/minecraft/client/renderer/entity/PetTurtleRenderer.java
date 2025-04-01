package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.TurtleModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.TurtleRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.pets.PetTurtle;

public class PetTurtleRenderer extends MobRenderer<PetTurtle, TurtleRenderState, TurtleModel> {
   private static final ResourceLocation TURTLE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/turtle/big_sea_turtle.png");

   public PetTurtleRenderer(EntityRendererProvider.Context var1) {
      super(var1, new TurtleModel(var1.bakeLayer(ModelLayers.TURTLE)), 0.7F);
   }

   public TurtleRenderState createRenderState() {
      return new TurtleRenderState();
   }

   public void extractRenderState(PetTurtle var1, TurtleRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isOnLand = !var1.isInWater() && var1.onGround();
   }

   public ResourceLocation getTextureLocation(TurtleRenderState var1) {
      return TURTLE_LOCATION;
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((TurtleRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
