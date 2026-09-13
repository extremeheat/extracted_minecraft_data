package net.minecraft.world;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public class ChunkCache implements IBlockAccess {
   private int field_72818_a;
   private int field_72816_b;
   private Chunk[][] field_72817_c;
   private boolean field_72814_d;
   private World field_72815_e;

   public ChunkCache(World var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      super();
      this.field_72815_e = var1;
      this.field_72818_a = var2 - var8 >> 4;
      this.field_72816_b = var4 - var8 >> 4;
      int var9 = var5 + var8 >> 4;
      int var10 = var7 + var8 >> 4;
      this.field_72817_c = new Chunk[var9 - this.field_72818_a + 1][var10 - this.field_72816_b + 1];
      this.field_72814_d = true;

      for(int var11 = this.field_72818_a; var11 <= var9; ++var11) {
         for(int var12 = this.field_72816_b; var12 <= var10; ++var12) {
            Chunk var13 = var1.func_72964_e(var11, var12);
            if (var13 != null) {
               this.field_72817_c[var11 - this.field_72818_a][var12 - this.field_72816_b] = var13;
            }
         }
      }

      for(int var14 = var2 >> 4; var14 <= var5 >> 4; ++var14) {
         for(int var15 = var4 >> 4; var15 <= var7 >> 4; ++var15) {
            Chunk var16 = this.field_72817_c[var14 - this.field_72818_a][var15 - this.field_72816_b];
            if (var16 != null && !var16.func_76606_c(var3, var6)) {
               this.field_72814_d = false;
            }
         }
      }
   }

   @Override
   public boolean func_72806_N() {
      return this.field_72814_d;
   }

   @Override
   public Block func_147439_a(int var1, int var2, int var3) {
      Block var4 = Blocks.field_150350_a;
      if (var2 >= 0 && var2 < 256) {
         int var5 = (var1 >> 4) - this.field_72818_a;
         int var6 = (var3 >> 4) - this.field_72816_b;
         if (var5 >= 0 && var5 < this.field_72817_c.length && var6 >= 0 && var6 < this.field_72817_c[var5].length) {
            Chunk var7 = this.field_72817_c[var5][var6];
            if (var7 != null) {
               var4 = var7.func_150810_a(var1 & 15, var2, var3 & 15);
            }
         }
      }

      return var4;
   }

   @Override
   public TileEntity func_147438_o(int var1, int var2, int var3) {
      int var4 = (var1 >> 4) - this.field_72818_a;
      int var5 = (var3 >> 4) - this.field_72816_b;
      return this.field_72817_c[var4][var5].func_150806_e(var1 & 15, var2, var3 & 15);
   }

   @Override
   public int func_72802_i(int var1, int var2, int var3, int var4) {
      int var5 = this.func_72810_a(EnumSkyBlock.Sky, var1, var2, var3);
      int var6 = this.func_72810_a(EnumSkyBlock.Block, var1, var2, var3);
      if (var6 < var4) {
         var6 = var4;
      }

      return var5 << 20 | var6 << 4;
   }

   @Override
   public int func_72805_g(int var1, int var2, int var3) {
      if (var2 < 0) {
         return 0;
      } else if (var2 >= 256) {
         return 0;
      } else {
         int var4 = (var1 >> 4) - this.field_72818_a;
         int var5 = (var3 >> 4) - this.field_72816_b;
         return this.field_72817_c[var4][var5].func_76628_c(var1 & 15, var2, var3 & 15);
      }
   }

   @Override
   public BiomeGenBase func_72807_a(int var1, int var2) {
      return this.field_72815_e.func_72807_a(var1, var2);
   }

   @Override
   public boolean func_147437_c(int var1, int var2, int var3) {
      return this.func_147439_a(var1, var2, var3).func_149688_o() == Material.field_151579_a;
   }

   public int func_72810_a(EnumSkyBlock var1, int var2, int var3, int var4) {
      if (var3 < 0) {
         var3 = 0;
      }

      if (var3 >= 256) {
         var3 = 255;
      }

      if (var3 < 0 || var3 >= 256 || var2 < -30000000 || var4 < -30000000 || var2 >= 30000000 || var4 > 30000000) {
         return var1.field_77198_c;
      } else if (var1 == EnumSkyBlock.Sky && this.field_72815_e.field_73011_w.field_76576_e) {
         return 0;
      } else if (this.func_147439_a(var2, var3, var4).func_149710_n()) {
         int var10 = this.func_72812_b(var1, var2, var3 + 1, var4);
         int var11 = this.func_72812_b(var1, var2 + 1, var3, var4);
         int var7 = this.func_72812_b(var1, var2 - 1, var3, var4);
         int var8 = this.func_72812_b(var1, var2, var3, var4 + 1);
         int var9 = this.func_72812_b(var1, var2, var3, var4 - 1);
         if (var11 > var10) {
            var10 = var11;
         }

         if (var7 > var10) {
            var10 = var7;
         }

         if (var8 > var10) {
            var10 = var8;
         }

         if (var9 > var10) {
            var10 = var9;
         }

         return var10;
      } else {
         int var5 = (var2 >> 4) - this.field_72818_a;
         int var6 = (var4 >> 4) - this.field_72816_b;
         return this.field_72817_c[var5][var6].func_76614_a(var1, var2 & 15, var3, var4 & 15);
      }
   }

   public int func_72812_b(EnumSkyBlock var1, int var2, int var3, int var4) {
      if (var3 < 0) {
         var3 = 0;
      }

      if (var3 >= 256) {
         var3 = 255;
      }

      if (var3 >= 0 && var3 < 256 && var2 >= -30000000 && var4 >= -30000000 && var2 < 30000000 && var4 <= 30000000) {
         int var5 = (var2 >> 4) - this.field_72818_a;
         int var6 = (var4 >> 4) - this.field_72816_b;
         return this.field_72817_c[var5][var6].func_76614_a(var1, var2 & 15, var3, var4 & 15);
      } else {
         return var1.field_77198_c;
      }
   }

   @Override
   public int func_72800_K() {
      return 256;
   }

   @Override
   public int func_72879_k(int var1, int var2, int var3, int var4) {
      return this.func_147439_a(var1, var2, var3).func_149748_c(this, var1, var2, var3, var4);
   }
}
