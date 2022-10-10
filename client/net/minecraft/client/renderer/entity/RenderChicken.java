package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.ModelChicken;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderChicken extends RenderLiving<EntityChicken> {
   private static final ResourceLocation field_110920_a = new ResourceLocation("textures/entity/chicken.png");

   public RenderChicken(RenderManager var1) {
      super(var1, new ModelChicken(), 0.3F);
   }

   protected ResourceLocation func_110775_a(EntityChicken var1) {
      return field_110920_a;
   }

   protected float func_77044_a(EntityChicken var1, float var2) {
      float var3 = var1.field_70888_h + (var1.field_70886_e - var1.field_70888_h) * var2;
      float var4 = var1.field_70884_g + (var1.field_70883_f - var1.field_70884_g) * var2;
      return (MathHelper.func_76126_a(var3) + 1.0F) * var4;
   }

   // $FF: synthetic method
   protected float func_77044_a(EntityLivingBase var1, float var2) {
      return this.func_77044_a((EntityChicken)var1, var2);
   }
}
