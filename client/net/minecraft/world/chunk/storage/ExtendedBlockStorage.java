package net.minecraft.world.chunk.storage;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.NibbleArray;

public class ExtendedBlockStorage {
   private int field_76684_a;
   private int field_76682_b;
   private int field_76683_c;
   private char[] field_177488_d;
   private NibbleArray field_76679_g;
   private NibbleArray field_76685_h;

   public ExtendedBlockStorage(int var1, boolean var2) {
      super();
      this.field_76684_a = var1;
      this.field_177488_d = new char[4096];
      this.field_76679_g = new NibbleArray();
      if (var2) {
         this.field_76685_h = new NibbleArray();
      }

   }

   public IBlockState func_177485_a(int var1, int var2, int var3) {
      IBlockState var4 = (IBlockState)Block.field_176229_d.func_148745_a(this.field_177488_d[var2 << 8 | var3 << 4 | var1]);
      return var4 != null ? var4 : Blocks.field_150350_a.func_176223_P();
   }

   public void func_177484_a(int var1, int var2, int var3, IBlockState var4) {
      IBlockState var5 = this.func_177485_a(var1, var2, var3);
      Block var6 = var5.func_177230_c();
      Block var7 = var4.func_177230_c();
      if (var6 != Blocks.field_150350_a) {
         --this.field_76682_b;
         if (var6.func_149653_t()) {
            --this.field_76683_c;
         }
      }

      if (var7 != Blocks.field_150350_a) {
         ++this.field_76682_b;
         if (var7.func_149653_t()) {
            ++this.field_76683_c;
         }
      }

      this.field_177488_d[var2 << 8 | var3 << 4 | var1] = (char)Block.field_176229_d.func_148747_b(var4);
   }

   public Block func_150819_a(int var1, int var2, int var3) {
      return this.func_177485_a(var1, var2, var3).func_177230_c();
   }

   public int func_76665_b(int var1, int var2, int var3) {
      IBlockState var4 = this.func_177485_a(var1, var2, var3);
      return var4.func_177230_c().func_176201_c(var4);
   }

   public boolean func_76663_a() {
      return this.field_76682_b == 0;
   }

   public boolean func_76675_b() {
      return this.field_76683_c > 0;
   }

   public int func_76662_d() {
      return this.field_76684_a;
   }

   public void func_76657_c(int var1, int var2, int var3, int var4) {
      this.field_76685_h.func_76581_a(var1, var2, var3, var4);
   }

   public int func_76670_c(int var1, int var2, int var3) {
      return this.field_76685_h.func_76582_a(var1, var2, var3);
   }

   public void func_76677_d(int var1, int var2, int var3, int var4) {
      this.field_76679_g.func_76581_a(var1, var2, var3, var4);
   }

   public int func_76674_d(int var1, int var2, int var3) {
      return this.field_76679_g.func_76582_a(var1, var2, var3);
   }

   public void func_76672_e() {
      this.field_76682_b = 0;
      this.field_76683_c = 0;

      for(int var1 = 0; var1 < 16; ++var1) {
         for(int var2 = 0; var2 < 16; ++var2) {
            for(int var3 = 0; var3 < 16; ++var3) {
               Block var4 = this.func_150819_a(var1, var2, var3);
               if (var4 != Blocks.field_150350_a) {
                  ++this.field_76682_b;
                  if (var4.func_149653_t()) {
                     ++this.field_76683_c;
                  }
               }
            }
         }
      }

   }

   public char[] func_177487_g() {
      return this.field_177488_d;
   }

   public void func_177486_a(char[] var1) {
      this.field_177488_d = var1;
   }

   public NibbleArray func_76661_k() {
      return this.field_76679_g;
   }

   public NibbleArray func_76671_l() {
      return this.field_76685_h;
   }

   public void func_76659_c(NibbleArray var1) {
      this.field_76679_g = var1;
   }

   public void func_76666_d(NibbleArray var1) {
      this.field_76685_h = var1;
   }
}
