package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelOcelot;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.util.ResourceLocation;

public class RenderOcelot extends RenderLiving<EntityOcelot> {
   private static final ResourceLocation field_110877_a = new ResourceLocation("textures/entity/cat/black.png");
   private static final ResourceLocation field_110875_f = new ResourceLocation("textures/entity/cat/ocelot.png");
   private static final ResourceLocation field_110876_g = new ResourceLocation("textures/entity/cat/red.png");
   private static final ResourceLocation field_110878_h = new ResourceLocation("textures/entity/cat/siamese.png");

   public RenderOcelot(RenderManager var1) {
      super(var1, new ModelOcelot(), 0.4F);
   }

   protected ResourceLocation func_110775_a(EntityOcelot var1) {
      switch(var1.func_70913_u()) {
      case 0:
      default:
         return field_110875_f;
      case 1:
         return field_110877_a;
      case 2:
         return field_110876_g;
      case 3:
         return field_110878_h;
      }
   }

   protected void func_77041_b(EntityOcelot var1, float var2) {
      super.func_77041_b(var1, var2);
      if (var1.func_70909_n()) {
         GlStateManager.func_179152_a(0.8F, 0.8F, 0.8F);
      }

   }
}
