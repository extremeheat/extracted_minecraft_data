package net.minecraft.world.chunk.storage;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.NibbleArray;

public class ExtendedBlockStorage {
   private int field_76684_a;
   private int field_76682_b;
   private int field_76683_c;
   private byte[] field_76680_d;
   private NibbleArray field_76681_e;
   private NibbleArray field_76678_f;
   private NibbleArray field_76679_g;
   private NibbleArray field_76685_h;

   public ExtendedBlockStorage(int var1, boolean var2) {
      super();
      this.field_76684_a = var1;
      this.field_76680_d = new byte[4096];
      this.field_76678_f = new NibbleArray(this.field_76680_d.length, 4);
      this.field_76679_g = new NibbleArray(this.field_76680_d.length, 4);
      if (var2) {
         this.field_76685_h = new NibbleArray(this.field_76680_d.length, 4);
      }
   }

   public Block func_150819_a(int var1, int var2, int var3) {
      int var4 = this.field_76680_d[var2 << 8 | var3 << 4 | var1] & 255;
      if (this.field_76681_e != null) {
         var4 |= this.field_76681_e.func_76582_a(var1, var2, var3) << 8;
      }

      return Block.func_149729_e(var4);
   }

   public void func_150818_a(int var1, int var2, int var3, Block var4) {
      int var5 = this.field_76680_d[var2 << 8 | var3 << 4 | var1] & 255;
      if (this.field_76681_e != null) {
         var5 |= this.field_76681_e.func_76582_a(var1, var2, var3) << 8;
      }

      Block var6 = Block.func_149729_e(var5);
      if (var6 != Blocks.field_150350_a) {
         --this.field_76682_b;
         if (var6.func_149653_t()) {
            --this.field_76683_c;
         }
      }

      if (var4 != Blocks.field_150350_a) {
         ++this.field_76682_b;
         if (var4.func_149653_t()) {
            ++this.field_76683_c;
         }
      }

      int var7 = Block.func_149682_b(var4);
      this.field_76680_d[var2 << 8 | var3 << 4 | var1] = (byte)(var7 & 0xFF);
      if (var7 > 255) {
         if (this.field_76681_e == null) {
            this.field_76681_e = new NibbleArray(this.field_76680_d.length, 4);
         }

         this.field_76681_e.func_76581_a(var1, var2, var3, (var7 & 3840) >> 8);
      } else if (this.field_76681_e != null) {
         this.field_76681_e.func_76581_a(var1, var2, var3, 0);
      }
   }

   public int func_76665_b(int var1, int var2, int var3) {
      return this.field_76678_f.func_76582_a(var1, var2, var3);
   }

   public void func_76654_b(int var1, int var2, int var3, int var4) {
      this.field_76678_f.func_76581_a(var1, var2, var3, var4);
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

   public byte[] func_76658_g() {
      return this.field_76680_d;
   }

   public void func_76676_h() {
      this.field_76681_e = null;
   }

   public NibbleArray func_76660_i() {
      return this.field_76681_e;
   }

   public NibbleArray func_76669_j() {
      return this.field_76678_f;
   }

   public NibbleArray func_76661_k() {
      return this.field_76679_g;
   }

   public NibbleArray func_76671_l() {
      return this.field_76685_h;
   }

   public void func_76664_a(byte[] var1) {
      this.field_76680_d = var1;
   }

   public void func_76673_a(NibbleArray var1) {
      this.field_76681_e = var1;
   }

   public void func_76668_b(NibbleArray var1) {
      this.field_76678_f = var1;
   }

   public void func_76659_c(NibbleArray var1) {
      this.field_76679_g = var1;
   }

   public void func_76666_d(NibbleArray var1) {
      this.field_76685_h = var1;
   }

   public NibbleArray func_76667_m() {
      this.field_76681_e = new NibbleArray(this.field_76680_d.length, 4);
      return this.field_76681_e;
   }
}
