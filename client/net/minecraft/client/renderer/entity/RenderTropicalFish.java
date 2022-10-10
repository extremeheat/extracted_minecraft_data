package net.minecraft.client.renderer.entity;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerTropicalFishPattern;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelTropicalFishA;
import net.minecraft.client.renderer.entity.model.ModelTropicalFishB;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityTropicalFish;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderTropicalFish extends RenderLiving<EntityTropicalFish> {
   private final ModelTropicalFishA field_204246_a = new ModelTropicalFishA();
   private final ModelTropicalFishB field_204247_j = new ModelTropicalFishB();

   public RenderTropicalFish(RenderManager var1) {
      super(var1, new ModelTropicalFishA(), 0.15F);
      this.func_177094_a(new LayerTropicalFishPattern(this));
   }

   @Nullable
   protected ResourceLocation func_110775_a(EntityTropicalFish var1) {
      return var1.func_204218_dG();
   }

   public void func_76986_a(EntityTropicalFish var1, double var2, double var4, double var6, float var8, float var9) {
      this.field_77045_g = (ModelBase)(var1.func_204217_dE() == 0 ? this.field_204246_a : this.field_204247_j);
      float[] var10 = var1.func_204219_dC();
      GlStateManager.func_179124_c(var10[0], var10[1], var10[2]);
      super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
   }

   protected void func_77043_a(EntityTropicalFish var1, float var2, float var3, float var4) {
      super.func_77043_a(var1, var2, var3, var4);
      float var5 = 4.3F * MathHelper.func_76126_a(0.6F * var2);
      GlStateManager.func_179114_b(var5, 0.0F, 1.0F, 0.0F);
      if (!var1.func_70090_H()) {
         GlStateManager.func_179109_b(0.2F, 0.1F, 0.0F);
         GlStateManager.func_179114_b(90.0F, 0.0F, 0.0F, 1.0F);
      }

   }
}
