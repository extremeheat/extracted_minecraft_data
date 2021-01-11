package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelSlime extends ModelBase {
   ModelRenderer field_78200_a;
   ModelRenderer field_78198_b;
   ModelRenderer field_78199_c;
   ModelRenderer field_78197_d;

   public ModelSlime(int var1) {
      super();
      this.field_78200_a = new ModelRenderer(this, 0, var1);
      this.field_78200_a.func_78789_a(-4.0F, 16.0F, -4.0F, 8, 8, 8);
      if (var1 > 0) {
         this.field_78200_a = new ModelRenderer(this, 0, var1);
         this.field_78200_a.func_78789_a(-3.0F, 17.0F, -3.0F, 6, 6, 6);
         this.field_78198_b = new ModelRenderer(this, 32, 0);
         this.field_78198_b.func_78789_a(-3.25F, 18.0F, -3.5F, 2, 2, 2);
         this.field_78199_c = new ModelRenderer(this, 32, 4);
         this.field_78199_c.func_78789_a(1.25F, 18.0F, -3.5F, 2, 2, 2);
         this.field_78197_d = new ModelRenderer(this, 32, 8);
         this.field_78197_d.func_78789_a(0.0F, 21.0F, -3.5F, 1, 1, 1);
      }

   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
      this.field_78200_a.func_78785_a(var7);
      if (this.field_78198_b != null) {
         this.field_78198_b.func_78785_a(var7);
         this.field_78199_c.func_78785_a(var7);
         this.field_78197_d.func_78785_a(var7);
      }

   }
}
