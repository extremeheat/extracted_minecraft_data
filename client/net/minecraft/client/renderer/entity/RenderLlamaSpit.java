package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelLlamaSpit;
import net.minecraft.entity.projectile.EntityLlamaSpit;
import net.minecraft.util.ResourceLocation;

public class RenderLlamaSpit extends Render<EntityLlamaSpit> {
   private static final ResourceLocation field_191333_a = new ResourceLocation("textures/entity/llama/spit.png");
   private final ModelLlamaSpit field_191334_f = new ModelLlamaSpit();

   public RenderLlamaSpit(RenderManager var1) {
      super(var1);
   }

   public void func_76986_a(EntityLlamaSpit var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b((float)var2, (float)var4 + 0.15F, (float)var6);
      GlStateManager.func_179114_b(var1.field_70126_B + (var1.field_70177_z - var1.field_70126_B) * var9 - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(var1.field_70127_C + (var1.field_70125_A - var1.field_70127_C) * var9, 0.0F, 0.0F, 1.0F);
      this.func_180548_c(var1);
      if (this.field_188301_f) {
         GlStateManager.func_179142_g();
         GlStateManager.func_187431_e(this.func_188298_c(var1));
      }

      this.field_191334_f.func_78088_a(var1, var9, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      if (this.field_188301_f) {
         GlStateManager.func_187417_n();
         GlStateManager.func_179119_h();
      }

      GlStateManager.func_179121_F();
      super.func_76986_a(var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityLlamaSpit var1) {
      return field_191333_a;
   }
}
