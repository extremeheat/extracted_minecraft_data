package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.ModelCow;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.util.ResourceLocation;

public class RenderCow extends RenderLiving<EntityCow> {
   private static final ResourceLocation field_110833_a = new ResourceLocation("textures/entity/cow/cow.png");

   public RenderCow(RenderManager var1) {
      super(var1, new ModelCow(), 0.7F);
   }

   protected ResourceLocation func_110775_a(EntityCow var1) {
      return field_110833_a;
   }
}
