package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerSlimeGel;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.ResourceLocation;

public class RenderSlime extends RenderLiving<EntitySlime> {
   private static final ResourceLocation field_110897_a = new ResourceLocation("textures/entity/slime/slime.png");

   public RenderSlime(RenderManager var1, ModelBase var2, float var3) {
      super(var1, var2, var3);
      this.func_177094_a(new LayerSlimeGel(this));
   }

   public void func_76986_a(EntitySlime var1, double var2, double var4, double var6, float var8, float var9) {
      this.field_76989_e = 0.25F * (float)var1.func_70809_q();
      super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
   }

   protected void func_77041_b(EntitySlime var1, float var2) {
      float var3 = (float)var1.func_70809_q();
      float var4 = (var1.field_70812_c + (var1.field_70811_b - var1.field_70812_c) * var2) / (var3 * 0.5F + 1.0F);
      float var5 = 1.0F / (var4 + 1.0F);
      GlStateManager.func_179152_a(var5 * var3, 1.0F / var5 * var3, var5 * var3);
   }

   protected ResourceLocation func_110775_a(EntitySlime var1) {
      return field_110897_a;
   }
}
