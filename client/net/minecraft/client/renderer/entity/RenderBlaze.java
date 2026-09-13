package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBlaze;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.util.ResourceLocation;

public class RenderBlaze extends RenderLiving {
   private static final ResourceLocation field_110837_a = new ResourceLocation("textures/entity/blaze.png");
   private int field_77068_a = ((ModelBlaze)this.field_77045_g).func_78104_a();

   public RenderBlaze() {
      super(new ModelBlaze(), 0.5F);
   }

   public void func_76986_a(EntityBlaze var1, double var2, double var4, double var6, float var8, float var9) {
      int var10 = ((ModelBlaze)this.field_77045_g).func_78104_a();
      if (var10 != this.field_77068_a) {
         this.field_77068_a = var10;
         this.field_77045_g = new ModelBlaze();
      }

      super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityBlaze var1) {
      return field_110837_a;
   }
}
