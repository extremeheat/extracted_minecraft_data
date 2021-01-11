package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelSquid extends ModelBase {
   ModelRenderer field_78202_a;
   ModelRenderer[] field_78201_b = new ModelRenderer[8];

   public ModelSquid() {
      super();
      byte var1 = -16;
      this.field_78202_a = new ModelRenderer(this, 0, 0);
      this.field_78202_a.func_78789_a(-6.0F, -8.0F, -6.0F, 12, 16, 12);
      ModelRenderer var10000 = this.field_78202_a;
      var10000.field_78797_d += (float)(24 + var1);

      for(int var2 = 0; var2 < this.field_78201_b.length; ++var2) {
         this.field_78201_b[var2] = new ModelRenderer(this, 48, 0);
         double var3 = (double)var2 * 3.141592653589793D * 2.0D / (double)this.field_78201_b.length;
         float var5 = (float)Math.cos(var3) * 5.0F;
         float var6 = (float)Math.sin(var3) * 5.0F;
         this.field_78201_b[var2].func_78789_a(-1.0F, 0.0F, -1.0F, 2, 18, 2);
         this.field_78201_b[var2].field_78800_c = var5;
         this.field_78201_b[var2].field_78798_e = var6;
         this.field_78201_b[var2].field_78797_d = (float)(31 + var1);
         var3 = (double)var2 * 3.141592653589793D * -2.0D / (double)this.field_78201_b.length + 1.5707963267948966D;
         this.field_78201_b[var2].field_78796_g = (float)var3;
      }

   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      ModelRenderer[] var8 = this.field_78201_b;
      int var9 = var8.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         ModelRenderer var11 = var8[var10];
         var11.field_78795_f = var3;
      }

   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
      this.field_78202_a.func_78785_a(var7);

      for(int var8 = 0; var8 < this.field_78201_b.length; ++var8) {
         this.field_78201_b[var8].func_78785_a(var7);
      }

   }
}
