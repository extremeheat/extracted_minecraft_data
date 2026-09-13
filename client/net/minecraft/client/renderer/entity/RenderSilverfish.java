package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelSilverfish;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.util.ResourceLocation;

public class RenderSilverfish extends RenderLiving {
   private static final ResourceLocation field_110882_a = new ResourceLocation("textures/entity/silverfish.png");

   public RenderSilverfish() {
      super(new ModelSilverfish(), 0.3F);
   }

   protected float func_77037_a(EntitySilverfish var1) {
      return 180.0F;
   }

   public void func_76986_a(EntitySilverfish var1, double var2, double var4, double var6, float var8, float var9) {
      super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntitySilverfish var1) {
      return field_110882_a;
   }

   protected int func_77032_a(EntitySilverfish var1, int var2, float var3) {
      return -1;
   }
}
