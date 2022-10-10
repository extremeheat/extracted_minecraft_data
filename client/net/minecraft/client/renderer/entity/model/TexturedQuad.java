package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;

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

   public void func_178765_a(BufferBuilder var1, float var2) {
      Vec3d var3 = this.field_78239_a[1].field_78243_a.func_72444_a(this.field_78239_a[0].field_78243_a);
      Vec3d var4 = this.field_78239_a[1].field_78243_a.func_72444_a(this.field_78239_a[2].field_78243_a);
      Vec3d var5 = var4.func_72431_c(var3).func_72432_b();
      float var6 = (float)var5.field_72450_a;
      float var7 = (float)var5.field_72448_b;
      float var8 = (float)var5.field_72449_c;
      if (this.field_78238_c) {
         var6 = -var6;
         var7 = -var7;
         var8 = -var8;
      }

      var1.func_181668_a(7, DefaultVertexFormats.field_181703_c);

      for(int var9 = 0; var9 < 4; ++var9) {
         PositionTextureVertex var10 = this.field_78239_a[var9];
         var1.func_181662_b(var10.field_78243_a.field_72450_a * (double)var2, var10.field_78243_a.field_72448_b * (double)var2, var10.field_78243_a.field_72449_c * (double)var2).func_187315_a((double)var10.field_78241_b, (double)var10.field_78242_c).func_181663_c(var6, var7, var8).func_181675_d();
      }

      Tessellator.func_178181_a().func_78381_a();
   }
}
