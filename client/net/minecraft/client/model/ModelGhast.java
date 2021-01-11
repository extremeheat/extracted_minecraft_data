package net.minecraft.client.model;

import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelGhast extends ModelBase {
   ModelRenderer field_78128_a;
   ModelRenderer[] field_78127_b = new ModelRenderer[9];

   public ModelGhast() {
      super();
      byte var1 = -16;
      this.field_78128_a = new ModelRenderer(this, 0, 0);
      this.field_78128_a.func_78789_a(-8.0F, -8.0F, -8.0F, 16, 16, 16);
      ModelRenderer var10000 = this.field_78128_a;
      var10000.field_78797_d += (float)(24 + var1);
      Random var2 = new Random(1660L);

      for(int var3 = 0; var3 < this.field_78127_b.length; ++var3) {
         this.field_78127_b[var3] = new ModelRenderer(this, 0, 0);
         float var4 = (((float)(var3 % 3) - (float)(var3 / 3 % 2) * 0.5F + 0.25F) / 2.0F * 2.0F - 1.0F) * 5.0F;
         float var5 = ((float)(var3 / 3) / 2.0F * 2.0F - 1.0F) * 5.0F;
         int var6 = var2.nextInt(7) + 8;
         this.field_78127_b[var3].func_78789_a(-1.0F, 0.0F, -1.0F, 2, var6, 2);
         this.field_78127_b[var3].field_78800_c = var4;
         this.field_78127_b[var3].field_78798_e = var5;
         this.field_78127_b[var3].field_78797_d = (float)(31 + var1);
      }

   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      for(int var8 = 0; var8 < this.field_78127_b.length; ++var8) {
         this.field_78127_b[var8].field_78795_f = 0.2F * MathHelper.func_76126_a(var3 * 0.3F + (float)var8) + 0.4F;
      }

   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b(0.0F, 0.6F, 0.0F);
      this.field_78128_a.func_78785_a(var7);
      ModelRenderer[] var8 = this.field_78127_b;
      int var9 = var8.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         ModelRenderer var11 = var8[var10];
         var11.func_78785_a(var7);
      }

      GlStateManager.func_179121_F();
   }
}
