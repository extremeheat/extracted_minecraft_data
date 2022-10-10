package net.minecraft.world;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.Heightmap;

public class Region implements IWorldReader {
   protected int field_72818_a;
   protected int field_72816_b;
   protected Chunk[][] field_72817_c;
   protected boolean field_72814_d;
   protected World field_72815_e;

   public Region(World var1, BlockPos var2, BlockPos var3, int var4) {
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

   @Nullable
   public TileEntity func_175625_s(BlockPos var1) {
      return this.func_190300_a(var1, Chunk.EnumCreateEntityType.IMMEDIATE);
   }

   @Nullable
   public TileEntity func_190300_a(BlockPos var1, Chunk.EnumCreateEntityType var2) {
      int var3 = (var1.func_177958_n() >> 4) - this.field_72818_a;
      int var4 = (var1.func_177952_p() >> 4) - this.field_72816_b;
      return this.field_72817_c[var3][var4].func_177424_a(var1, var2);
   }

   public int func_175626_b(BlockPos var1, int var2) {
      int var3 = this.func_175629_a(EnumLightType.SKY, var1);
      int var4 = this.func_175629_a(EnumLightType.BLOCK, var1);
      if (var4 < var2) {
         var4 = var2;
      }

      return var3 << 20 | var4 << 4;
   }

   public float func_205052_D(BlockPos var1) {
      return this.field_72815_e.field_73011_w.func_177497_p()[this.func_201696_r(var1)];
   }

   public int func_205049_d(BlockPos var1, int var2) {
      if (this.func_180495_p(var1).func_200130_c(this, var1)) {
         int var3 = 0;
         EnumFacing[] var4 = EnumFacing.values();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            EnumFacing var7 = var4[var6];
            int var8 = this.func_201669_a(var1.func_177972_a(var7), var2);
            if (var8 > var3) {
               var3 = var8;
            }

            if (var3 >= 15) {
               return var3;
            }
         }

         return var3;
      } else {
         return this.func_201669_a(var1, var2);
      }
   }

   public Dimension func_201675_m() {
      return this.field_72815_e.func_201675_m();
   }

   public int func_201669_a(BlockPos var1, int var2) {
      if (var1.func_177958_n() >= -30000000 && var1.func_177952_p() >= -30000000 && var1.func_177958_n() < 30000000 && var1.func_177952_p() <= 30000000) {
         if (var1.func_177956_o() < 0) {
            return 0;
         } else {
            int var3;
            if (var1.func_177956_o() >= 256) {
               var3 = 15 - var2;
               if (var3 < 0) {
                  var3 = 0;
               }

               return var3;
            } else {
               var3 = (var1.func_177958_n() >> 4) - this.field_72818_a;
               int var4 = (var1.func_177952_p() >> 4) - this.field_72816_b;
               return this.field_72817_c[var3][var4].func_177443_a(var1, var2);
            }
         }
      } else {
         return 15;
      }
   }

   public boolean func_175680_a(int var1, int var2, boolean var3) {
      return this.func_205054_a(var1, var2);
   }

   public boolean func_175678_i(BlockPos var1) {
      return false;
   }

   public boolean func_205054_a(int var1, int var2) {
      int var3 = var1 - this.field_72818_a;
      int var4 = var2 - this.field_72816_b;
      return var3 >= 0 && var3 < this.field_72817_c.length && var4 >= 0 && var4 < this.field_72817_c[var3].length;
   }

   public int func_201676_a(Heightmap.Type var1, int var2, int var3) {
      throw new RuntimeException("NOT IMPLEMENTED!");
   }

   public WorldBorder func_175723_af() {
      return this.field_72815_e.func_175723_af();
   }

   public boolean func_195585_a(@Nullable Entity var1, VoxelShape var2) {
      throw new RuntimeException("This method should never be called here. No entity logic inside Region");
   }

   @Nullable
   public EntityPlayer func_190525_a(double var1, double var3, double var5, double var7, Predicate<Entity> var9) {
      throw new RuntimeException("This method should never be called here. No entity logic inside Region");
   }

   public IBlockState func_180495_p(BlockPos var1) {
      if (var1.func_177956_o() >= 0 && var1.func_177956_o() < 256) {
         int var2 = (var1.func_177958_n() >> 4) - this.field_72818_a;
         int var3 = (var1.func_177952_p() >> 4) - this.field_72816_b;
         if (var2 >= 0 && var2 < this.field_72817_c.length && var3 >= 0 && var3 < this.field_72817_c[var2].length) {
            Chunk var4 = this.field_72817_c[var2][var3];
            if (var4 != null) {
               return var4.func_180495_p(var1);
            }
         }
      }

      return Blocks.field_150350_a.func_176223_P();
   }

   public IFluidState func_204610_c(BlockPos var1) {
      if (var1.func_177956_o() >= 0 && var1.func_177956_o() < 256) {
         int var2 = (var1.func_177958_n() >> 4) - this.field_72818_a;
         int var3 = (var1.func_177952_p() >> 4) - this.field_72816_b;
         if (var2 >= 0 && var2 < this.field_72817_c.length && var3 >= 0 && var3 < this.field_72817_c[var2].length) {
            Chunk var4 = this.field_72817_c[var2][var3];
            if (var4 != null) {
               return var4.func_204610_c(var1);
            }
         }
      }

      return Fluids.field_204541_a.func_207188_f();
   }

   public int func_175657_ab() {
      return 0;
   }

   public Biome func_180494_b(BlockPos var1) {
      int var2 = (var1.func_177958_n() >> 4) - this.field_72818_a;
      int var3 = (var1.func_177952_p() >> 4) - this.field_72816_b;
      return this.field_72817_c[var2][var3].func_201600_k(var1);
   }

   private int func_175629_a(EnumLightType var1, BlockPos var2) {
      if (var1 == EnumLightType.SKY && !this.field_72815_e.func_201675_m().func_191066_m()) {
         return 0;
      } else if (var2.func_177956_o() >= 0 && var2.func_177956_o() < 256) {
         int var3;
         if (this.func_180495_p(var2).func_200130_c(this, var2)) {
            var3 = 0;
            EnumFacing[] var9 = EnumFacing.values();
            int var5 = var9.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               EnumFacing var7 = var9[var6];
               int var8 = this.func_175642_b(var1, var2.func_177972_a(var7));
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
      return this.func_180495_p(var1).func_196958_f();
   }

   public int func_175642_b(EnumLightType var1, BlockPos var2) {
      if (var2.func_177956_o() >= 0 && var2.func_177956_o() < 256) {
         int var3 = (var2.func_177958_n() >> 4) - this.field_72818_a;
         int var4 = (var2.func_177952_p() >> 4) - this.field_72816_b;
         return this.field_72817_c[var3][var4].func_177413_a(var1, var2);
      } else {
         return var1.field_77198_c;
      }
   }

   public int func_175627_a(BlockPos var1, EnumFacing var2) {
      return this.func_180495_p(var1).func_185893_b(this, var1, var2);
   }

   public boolean func_201670_d() {
      throw new RuntimeException("Not yet implemented");
   }

   public int func_181545_F() {
      throw new RuntimeException("Not yet implemented");
   }
}
