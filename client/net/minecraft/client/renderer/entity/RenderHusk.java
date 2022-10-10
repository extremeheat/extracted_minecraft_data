package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;

public class RenderHusk extends RenderZombie {
   private static final ResourceLocation field_190086_r = new ResourceLocation("textures/entity/zombie/husk.png");

   public RenderHusk(RenderManager var1) {
      super(var1);
   }

   protected void func_77041_b(EntityZombie var1, float var2) {
      float var3 = 1.0625F;
      GlStateManager.func_179152_a(1.0625F, 1.0625F, 1.0625F);
      super.func_77041_b(var1, var2);
   }

   protected ResourceLocation func_110775_a(EntityZombie var1) {
      return field_190086_r;
   }
}
