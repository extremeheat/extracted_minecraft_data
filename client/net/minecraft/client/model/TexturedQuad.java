package net.minecraft.client.model;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Vec3;

public class TexturedQuad {
   public PositionTextureVertex[] field_78239_a;
   public int field_78237_b;
   private boolean field_78238_c;

   public TexturedQuad(PositionTextureVertex[] var1) {
      super();
      this.field_78239_a = var1;
      this.field_78237_b = var1.length;
   }

   public TexturedQuad(PositionTextureVertex[] var1, int var2, int var3, int var4, int var5, float var6, float var7) {
      this(var1);
      float var8 = 0.0F / var6;
      float var9 = 0.0F / var7;
      var1[0] = var1[0].func_78240_a((float)var4 / var6 - var8, (float)var3 / var7 + var9);
      var1[1] = var1[1].func_78240_a((float)var2 / var6 + var8, (float)var3 / var7 + var9);
      var1[2] = var1[2].func_78240_a((float)var2 / var6 + var8, (float)var5 / var7 - var9);
      var1[3] = var1[3].func_78240_a((float)var4 / var6 - var8, (float)var5 / var7 - var9);
   }

   public void func_78235_a() {
      PositionTextureVertex[] var1 = new PositionTextureVertex[this.field_78239_a.length];

      for(int var2 = 0; var2 < this.field_78239_a.length; ++var2) {
         var1[var2] = this.field_78239_a[this.field_78239_a.length - var2 - 1];
      }

      this.field_78239_a = var1;
   }

   public void func_78236_a(Tessellator var1, float var2) {
      Vec3 var3 = this.field_78239_a[1].field_78243_a.func_72444_a(this.field_78239_a[0].field_78243_a);
      Vec3 var4 = this.field_78239_a[1].field_78243_a.func_72444_a(this.field_78239_a[2].field_78243_a);
      Vec3 var5 = var4.func_72431_c(var3).func_72432_b();
      var1.func_78382_b();
      if (this.field_78238_c) {
         var1.func_78375_b(-((float)var5.field_72450_a), -((float)var5.field_72448_b), -((float)var5.field_72449_c));
      } else {
         var1.func_78375_b((float)var5.field_72450_a, (float)var5.field_72448_b, (float)var5.field_72449_c);
      }

      for(int var6 = 0; var6 < 4; ++var6) {
         PositionTextureVertex var7 = this.field_78239_a[var6];
         var1.func_78374_a(
            (double)((float)var7.field_78243_a.field_72450_a * var2),
            (double)((float)var7.field_78243_a.field_72448_b * var2),
            (double)((float)var7.field_78243_a.field_72449_c * var2),
            (double)var7.field_78241_b,
            (double)var7.field_78242_c
         );
      }

      var1.func_78381_a();
   }
}
