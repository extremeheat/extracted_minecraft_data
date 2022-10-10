package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.LayerStrayClothing;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.util.ResourceLocation;

public class RenderStray extends RenderSkeleton {
   private static final ResourceLocation field_190084_m = new ResourceLocation("textures/entity/skeleton/stray.png");

   public RenderStray(RenderManager var1) {
      super(var1);
      this.func_177094_a(new LayerStrayClothing(this));
   }

   protected ResourceLocation func_110775_a(AbstractSkeleton var1) {
      return field_190084_m;
   }
}
