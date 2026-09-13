package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.ResourceLocation;

public class RenderPig extends RenderLiving {
   private static final ResourceLocation field_110888_a = new ResourceLocation("textures/entity/pig/pig_saddle.png");
   private static final ResourceLocation field_110887_f = new ResourceLocation("textures/entity/pig/pig.png");

   public RenderPig(ModelBase var1, ModelBase var2, float var3) {
      super(var1, var3);
      this.func_77042_a(var2);
   }

   protected int func_77032_a(EntityPig var1, int var2, float var3) {
      if (var2 == 0 && var1.func_70901_n()) {
         this.func_110776_a(field_110888_a);
         return 1;
      } else {
         return -1;
      }
   }

   protected ResourceLocation func_110775_a(EntityPig var1) {
      return field_110887_f;
   }
}
