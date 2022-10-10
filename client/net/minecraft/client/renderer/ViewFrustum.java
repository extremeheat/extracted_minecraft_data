package net.minecraft.client.renderer;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ViewFrustum {
   protected final WorldRenderer field_178169_a;
   protected final World field_178167_b;
   protected int field_178168_c;
   protected int field_178165_d;
   protected int field_178166_e;
   public RenderChunk[] field_178164_f;

   public ViewFrustum(World var1, int var2, WorldRenderer var3, IRenderChunkFactory var4) {
      super();
      this.field_178169_a = var3;
      this.field_178167_b = var1;
      this.func_178159_a(var2);
      this.func_178158_a(var4);
   }

   protected void func_178158_a(IRenderChunkFactory var1) {
      int var2 = this.field_178165_d * this.field_178168_c * this.field_178166_e;
      this.field_178164_f = new RenderChunk[var2];

      for(int var3 = 0; var3 < this.field_178165_d; ++var3) {
         for(int var4 = 0; var4 < this.field_178168_c; ++var4) {
            for(int var5 = 0; var5 < this.field_178166_e; ++var5) {
               int var6 = this.func_212478_a(var3, var4, var5);
               this.field_178164_f[var6] = var1.create(this.field_178167_b, this.field_178169_a);
               this.field_178164_f[var6].func_189562_a(var3 * 16, var4 * 16, var5 * 16);
            }
         }
      }

   }

   public void func_178160_a() {
      RenderChunk[] var1 = this.field_178164_f;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         RenderChunk var4 = var1[var3];
         var4.func_178566_a();
      }

   }

   private int func_212478_a(int var1, int var2, int var3) {
      return (var3 * this.field_178168_c + var2) * this.field_178165_d + var1;
   }

   protected void func_178159_a(int var1) {
      int var2 = var1 * 2 + 1;
      this.field_178165_d = var2;
      this.field_178168_c = 16;
      this.field_178166_e = var2;
   }

   public void func_178163_a(double var1, double var3) {
      int var5 = MathHelper.func_76128_c(var1) - 8;
      int var6 = MathHelper.func_76128_c(var3) - 8;
      int var7 = this.field_178165_d * 16;

      for(int var8 = 0; var8 < this.field_178165_d; ++var8) {
         int var9 = this.func_178157_a(var5, var7, var8);

         for(int var10 = 0; var10 < this.field_178166_e; ++var10) {
            int var11 = this.func_178157_a(var6, var7, var10);

            for(int var12 = 0; var12 < this.field_178168_c; ++var12) {
               int var13 = var12 * 16;
               RenderChunk var14 = this.field_178164_f[this.func_212478_a(var8, var12, var10)];
               var14.func_189562_a(var9, var13, var11);
            }
         }
      }

   }

   private int func_178157_a(int var1, int var2, int var3) {
      int var4 = var3 * 16;
      int var5 = var4 - var1 + var2 / 2;
      if (var5 < 0) {
         var5 -= var2 - 1;
      }

      return var4 - var5 / var2 * var2;
   }

   public void func_187474_a(int var1, int var2, int var3, int var4, int var5, int var6, boolean var7) {
      int var8 = MathHelper.func_76137_a(var1, 16);
      int var9 = MathHelper.func_76137_a(var2, 16);
      int var10 = MathHelper.func_76137_a(var3, 16);
      int var11 = MathHelper.func_76137_a(var4, 16);
      int var12 = MathHelper.func_76137_a(var5, 16);
      int var13 = MathHelper.func_76137_a(var6, 16);

      for(int var14 = var8; var14 <= var11; ++var14) {
         int var15 = MathHelper.func_180184_b(var14, this.field_178165_d);

         for(int var16 = var9; var16 <= var12; ++var16) {
            int var17 = MathHelper.func_180184_b(var16, this.field_178168_c);

            for(int var18 = var10; var18 <= var13; ++var18) {
               int var19 = MathHelper.func_180184_b(var18, this.field_178166_e);
               RenderChunk var20 = this.field_178164_f[this.func_212478_a(var15, var17, var19)];
               var20.func_178575_a(var7);
            }
         }
      }

   }

   @Nullable
   protected RenderChunk func_178161_a(BlockPos var1) {
      int var2 = MathHelper.func_76137_a(var1.func_177958_n(), 16);
      int var3 = MathHelper.func_76137_a(var1.func_177956_o(), 16);
      int var4 = MathHelper.func_76137_a(var1.func_177952_p(), 16);
      if (var3 >= 0 && var3 < this.field_178168_c) {
         var2 = MathHelper.func_180184_b(var2, this.field_178165_d);
         var4 = MathHelper.func_180184_b(var4, this.field_178166_e);
         return this.field_178164_f[this.func_212478_a(var2, var3, var4)];
      } else {
         return null;
      }
   }
}
