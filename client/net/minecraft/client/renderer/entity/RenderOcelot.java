package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderOcelot extends RenderLiving {
   private static final ResourceLocation field_110877_a = new ResourceLocation("textures/entity/cat/black.png");
   private static final ResourceLocation field_110875_f = new ResourceLocation("textures/entity/cat/ocelot.png");
   private static final ResourceLocation field_110876_g = new ResourceLocation("textures/entity/cat/red.png");
   private static final ResourceLocation field_110878_h = new ResourceLocation("textures/entity/cat/siamese.png");

   public RenderOcelot(ModelBase var1, float var2) {
      super(var1, var2);
   }

   public void func_76986_a(EntityOcelot var1, double var2, double var4, double var6, float var8, float var9) {
      super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
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
         GL11.glScalef(0.8F, 0.8F, 0.8F);
      }
   }
}
