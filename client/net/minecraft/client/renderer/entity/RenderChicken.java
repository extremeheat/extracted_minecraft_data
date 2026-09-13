package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderChicken extends RenderLiving {
   private static final ResourceLocation field_110920_a = new ResourceLocation("textures/entity/chicken.png");

   public RenderChicken(ModelBase var1, float var2) {
      super(var1, var2);
   }

   public void func_76986_a(EntityChicken var1, double var2, double var4, double var6, float var8, float var9) {
      super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityChicken var1) {
      return field_110920_a;
   }

   protected float func_77044_a(EntityChicken var1, float var2) {
      float var3 = var1.field_70888_h + (var1.field_70886_e - var1.field_70888_h) * var2;
      float var4 = var1.field_70884_g + (var1.field_70883_f - var1.field_70884_g) * var2;
      return (MathHelper.func_76126_a(var3) + 1.0F) * var4;
   }
}
