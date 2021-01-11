package net.minecraft.world;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public class ChunkCache implements IBlockAccess {
   protected int field_72818_a;
   protected int field_72816_b;
   protected Chunk[][] field_72817_c;
   protected boolean field_72814_d;
   protected World field_72815_e;

   public ChunkCache(World var1, BlockPos var2, BlockPos var3, int var4) {
      super();
      this.field_72815_e = var1;
      this.field_72818_a = var2.func_177958_n() - var4 >> 4;
      this.field_72816_b = var2.func_177952_p() - var4 >> 4;
      int var5 = var3.func_177958_n() + var4 >> 4;
      int var6 = var3.func_177952_p() + var4 >> 4;
      this.field_72817_c = new Chunk[var5 - this.field_72818_a + 1][var6 - this.field_72816_b + 1];
      this.field_72814_d = true;

      int var7;
      int var8;
      for(var7 = this.field_72818_a; var7 <= var5; ++var7) {
         for(var8 = this.field_72816_b; var8 <= var6; ++var8) {
            this.field_72817_c[var7 - this.field_72818_a][var8 - this.field_72816_b] = var1.func_72964_e(var7, var8);
         }
      }

      for(var7 = var2.func_177958_n() >> 4; var7 <= var3.func_177958_n() >> 4; ++var7) {
         for(var8 = var2.func_177952_p() >> 4; var8 <= var3.func_177952_p() >> 4; ++var8) {
            Chunk var9 = this.field_72817_c[var7 - this.field_72818_a][var8 - this.field_72816_b];
            if (var9 != null && !var9.func_76606_c(var2.func_177956_o(), var3.func_177956_o())) {
               this.field_72814_d = false;
            }
         }
      }

   }

   public boolean func_72806_N() {
      return this.field_72814_d;
   }

   public TileEntity func_175625_s(BlockPos var1) {
      int var2 = (var1.func_177958_n() >> 4) - this.field_72818_a;
      int var3 = (var1.func_177952_p() >> 4) - this.field_72816_b;
      return this.field_72817_c[var2][var3].func_177424_a(var1, Chunk.EnumCreateEntityType.IMMEDIATE);
   }

   public int func_175626_b(BlockPos var1, int var2) {
      int var3 = this.func_175629_a(EnumSkyBlock.SKY, var1);
      int var4 = this.func_175629_a(EnumSkyBlock.BLOCK, var1);
      if (var4 < var2) {
         var4 = var2;
      }

      return var3 << 20 | var4 << 4;
   }

   public IBlockState func_180495_p(BlockPos var1) {
      if (var1.func_177956_o() >= 0 && var1.func_177956_o() < 256) {
         int var2 = (var1.func_177958_n() >> 4) - this.field_72818_a;
         int var3 = (var1.func_177952_p() >> 4) - this.field_72816_b;
         if (var2 >= 0 && var2 < this.field_72817_c.length && var3 >= 0 && var3 < this.field_72817_c[var2].length) {
            Chunk var4 = this.field_72817_c[var2][var3];
            if (var4 != null) {
               return var4.func_177435_g(var1);
            }
         }
      }

      return Blocks.field_150350_a.func_176223_P();
   }

   public BiomeGenBase func_180494_b(BlockPos var1) {
      return this.field_72815_e.func_180494_b(var1);
   }

   private int func_175629_a(EnumSkyBlock var1, BlockPos var2) {
      if (var1 == EnumSkyBlock.SKY && this.field_72815_e.field_73011_w.func_177495_o()) {
         return 0;
      } else if (var2.func_177956_o() >= 0 && var2.func_177956_o() < 256) {
         int var3;
         if (this.func_180495_p(var2).func_177230_c().func_149710_n()) {
            var3 = 0;
            EnumFacing[] var9 = EnumFacing.values();
            int var5 = var9.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               EnumFacing var7 = var9[var6];
               int var8 = this.func_175628_b(var1, var2.func_177972_a(var7));
               if (var8 > var3) {
                  var3 = var8;
               }

               if (var3 >= 15) {
                  return var3;
               }
            }

            return var3;
         } else {
            var3 = (var2.func_177958_n() >> 4) - this.field_72818_a;
            int var4 = (var2.func_177952_p() >> 4) - this.field_72816_b;
            return this.field_72817_c[var3][var4].func_177413_a(var1, var2);
         }
      } else {
         return var1.field_77198_c;
      }
   }

   public boolean func_175623_d(BlockPos var1) {
      return this.func_180495_p(var1).func_177230_c().func_149688_o() == Material.field_151579_a;
   }

   public int func_175628_b(EnumSkyBlock var1, BlockPos var2) {
      if (var2.func_177956_o() >= 0 && var2.func_177956_o() < 256) {
         int var3 = (var2.func_177958_n() >> 4) - this.field_72818_a;
         int var4 = (var2.func_177952_p() >> 4) - this.field_72816_b;
         return this.field_72817_c[var3][var4].func_177413_a(var1, var2);
      } else {
         return var1.field_77198_c;
      }
   }

   public int func_175627_a(BlockPos var1, EnumFacing var2) {
      IBlockState var3 = this.func_180495_p(var1);
      return var3.func_177230_c().func_176211_b(this, var1, var3, var2);
   }

   public WorldType func_175624_G() {
      return this.field_72815_e.func_175624_G();
   }
}
