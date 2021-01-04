package net.minecraft.client.renderer.entity;

import javax.annotation.Nullable;
import net.minecraft.client.model.TurtleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Turtle;

public class TurtleRenderer extends MobRenderer<Turtle, TurtleModel<Turtle>> {
   private static final ResourceLocation TURTLE_LOCATION = new ResourceLocation("textures/entity/turtle/big_sea_turtle.png");

   public TurtleRenderer(EntityRenderDispatcher var1) {
      super(var1, new TurtleModel(0.0F), 0.7F);
   }

   public void render(Turtle var1, double var2, double var4, double var6, float var8, float var9) {
      if (var1.isBaby()) {
         this.shadowRadius *= 0.5F;
      }

      super.render((Mob)var1, var2, var4, var6, var8, var9);
   }

   @Nullable
   protected ResourceLocation getTextureLocation(Turtle var1) {
      return TURTLE_LOCATION;
   }
}
