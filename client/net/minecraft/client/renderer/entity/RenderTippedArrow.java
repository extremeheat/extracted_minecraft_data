package net.minecraft.client.renderer.entity;

import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.util.ResourceLocation;

public class RenderTippedArrow extends RenderArrow<EntityTippedArrow> {
   public static final ResourceLocation field_188305_a = new ResourceLocation("textures/entity/projectiles/arrow.png");
   public static final ResourceLocation field_188306_b = new ResourceLocation("textures/entity/projectiles/tipped_arrow.png");

   public RenderTippedArrow(RenderManager var1) {
      super(var1);
   }

   protected ResourceLocation func_110775_a(EntityTippedArrow var1) {
      return var1.func_184557_n() > 0 ? field_188306_b : field_188305_a;
   }
}
