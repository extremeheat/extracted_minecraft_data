package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.util.ResourceLocation;

public class RenderCow extends RenderLiving {
   private static final ResourceLocation field_110833_a = new ResourceLocation("textures/entity/cow/cow.png");

   public RenderCow(ModelBase var1, float var2) {
      super(var1, var2);
   }

   protected ResourceLocation func_110775_a(EntityCow var1) {
      return field_110833_a;
   }
}
