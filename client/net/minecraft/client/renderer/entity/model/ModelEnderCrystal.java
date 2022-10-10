package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class ModelEnderCrystal extends ModelBase {
   private final ModelRenderer field_78230_a;
   private final ModelRenderer field_78228_b = new ModelRenderer(this, "glass");
   private final ModelRenderer field_78229_c;

   public ModelEnderCrystal(float var1, boolean var2) {
      super();
      this.field_78228_b.func_78784_a(0, 0).func_78789_a(-4.0F, -4.0F, -4.0F, 8, 8, 8);
      this.field_78230_a = new ModelRenderer(this, "cube");
      this.field_78230_a.func_78784_a(32, 0).func_78789_a(-4.0F, -4.0F, -4.0F, 8, 8, 8);
      if (var2) {
         this.field_78229_c = new ModelRenderer(this, "base");
         this.field_78229_c.func_78784_a(0, 16).func_78789_a(-6.0F, 0.0F, -6.0F, 12, 4, 12);
      } else {
         this.field_78229_c = null;
      }

   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      GlStateManager.func_179094_E();
      GlStateManager.func_179152_a(2.0F, 2.0F, 2.0F);
      GlStateManager.func_179109_b(0.0F, -0.5F, 0.0F);
      if (this.field_78229_c != null) {
         this.field_78229_c.func_78785_a(var7);
      }

      GlStateManager.func_179114_b(var3, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179109_b(0.0F, 0.8F + var4, 0.0F);
      GlStateManager.func_179114_b(60.0F, 0.7071F, 0.0F, 0.7071F);
      this.field_78228_b.func_78785_a(var7);
      float var8 = 0.875F;
      GlStateManager.func_179152_a(0.875F, 0.875F, 0.875F);
      GlStateManager.func_179114_b(60.0F, 0.7071F, 0.0F, 0.7071F);
      GlStateManager.func_179114_b(var3, 0.0F, 1.0F, 0.0F);
      this.field_78228_b.func_78785_a(var7);
      GlStateManager.func_179152_a(0.875F, 0.875F, 0.875F);
      GlStateManager.func_179114_b(60.0F, 0.7071F, 0.0F, 0.7071F);
      GlStateManager.func_179114_b(var3, 0.0F, 1.0F, 0.0F);
      this.field_78230_a.func_78785_a(var7);
      GlStateManager.func_179121_F();
   }
}
