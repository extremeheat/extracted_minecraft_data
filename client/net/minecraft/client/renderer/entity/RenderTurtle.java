package net.minecraft.client.renderer.entity;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.model.ModelTurtle;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityTurtle;
import net.minecraft.util.ResourceLocation;

public class RenderTurtle extends RenderLiving<EntityTurtle> {
   private static final ResourceLocation field_203091_a = new ResourceLocation("textures/entity/turtle/big_sea_turtle.png");

   public RenderTurtle(RenderManager var1) {
      super(var1, new ModelTurtle(0.0F), 0.35F);
   }

   public void func_76986_a(EntityTurtle var1, double var2, double var4, double var6, float var8, float var9) {
      if (var1.func_70631_g_()) {
         this.field_76989_e *= 0.5F;
      }

      super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
   }

   @Nullable
   protected ResourceLocation func_110775_a(EntityTurtle var1) {
      return field_203091_a;
   }
}
