package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelWitch;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItemWitch;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.ResourceLocation;

public class RenderWitch extends RenderLiving<EntityWitch> {
   private static final ResourceLocation field_110910_a = new ResourceLocation("textures/entity/witch.png");

   public RenderWitch(RenderManager var1) {
      super(var1, new ModelWitch(0.0F), 0.5F);
      this.func_177094_a(new LayerHeldItemWitch(this));
   }

   public void func_76986_a(EntityWitch var1, double var2, double var4, double var6, float var8, float var9) {
      ((ModelWitch)this.field_77045_g).field_82900_g = var1.func_70694_bm() != null;
      super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityWitch var1) {
      return field_110910_a;
   }

   public void func_82422_c() {
      GlStateManager.func_179109_b(0.0F, 0.1875F, 0.0F);
   }

   protected void func_77041_b(EntityWitch var1, float var2) {
      float var3 = 0.9375F;
      GlStateManager.func_179152_a(var3, var3, var3);
   }
}
