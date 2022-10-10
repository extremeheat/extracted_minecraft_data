package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelLeashKnot;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.util.ResourceLocation;

public class RenderLeashKnot extends Render<EntityLeashKnot> {
   private static final ResourceLocation field_110802_a = new ResourceLocation("textures/entity/lead_knot.png");
   private final ModelLeashKnot field_110801_f = new ModelLeashKnot();

   public RenderLeashKnot(RenderManager var1) {
      super(var1);
   }

   public void func_76986_a(EntityLeashKnot var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.func_179094_E();
      GlStateManager.func_179129_p();
      GlStateManager.func_179109_b((float)var2, (float)var4, (float)var6);
      float var10 = 0.0625F;
      GlStateManager.func_179091_B();
      GlStateManager.func_179152_a(-1.0F, -1.0F, 1.0F);
      GlStateManager.func_179141_d();
      this.func_180548_c(var1);
      if (this.field_188301_f) {
         GlStateManager.func_179142_g();
         GlStateManager.func_187431_e(this.func_188298_c(var1));
      }

      this.field_110801_f.func_78088_a(var1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      if (this.field_188301_f) {
         GlStateManager.func_187417_n();
         GlStateManager.func_179119_h();
      }

      GlStateManager.func_179121_F();
      super.func_76986_a(var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityLeashKnot var1) {
      return field_110802_a;
   }
}
