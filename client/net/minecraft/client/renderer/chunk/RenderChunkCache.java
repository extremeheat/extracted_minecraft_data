package net.minecraft.client.renderer.chunk;

import java.util.Iterator;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.Heightmap;

public class RenderChunkCache implements IWorldReader {
   protected final int field_212400_a;
   protected final int field_212401_b;
   protected final BlockPos field_212402_c;
   protected final int field_212403_d;
   protected final int field_212404_e;
   protected final int field_212405_f;
   protected final Chunk[][] field_212406_g;
   protected final RenderChunkCache.Entry[] field_212407_h;
   protected final World field_212408_i;

   @Nullable
   public static RenderChunkCache func_212397_a(World var0, BlockPos var1, BlockPos var2, int var3) {
      int var4 = var1.func_177958_n() - var3 >> 4;
      int var5 = var1.func_177952_p() - var3 >> 4;
      int var6 = var2.func_177958_n() + var3 >> 4;
      int var7 = var2.func_177952_p() + var3 >> 4;
      Chunk[][] var8 = new Chunk[var6 - var4 + 1][var7 - var5 + 1];

      int var10;
      for(int var9 = var4; var9 <= var6; ++var9) {
         for(var10 = var5; var10 <= var7; ++var10) {
            var8[var9 - var4][var10 - var5] = var0.func_72964_e(var9, var10);
         }
      }

      boolean var13 = true;

      for(var10 = var1.func_177958_n() >> 4; var10 <= var2.func_177958_n() >> 4; ++var10) {
         for(int var11 = var1.func_177952_p() >> 4; var11 <= var2.func_177952_p() >> 4; ++var11) {
            Chunk var12 = var8[var10 - var4][var11 - var5];
            if (!var12.func_76606_c(var1.func_177956_o(), var2.func_177956_o())) {
               var13 = false;
            }
         }
      }

      if (var13) {
         return null;
      } else {
         boolean var14 = true;
         BlockPos var15 = var1.func_177982_a(-1, -1, -1);
         BlockPos var16 = var2.func_177982_a(1, 1, 1);
         return new RenderChunkCache(var0, var4, var5, var8, var15, var16);
      }
   }

   public RenderChunkCache(World var1, int var2, int var3, Chunk[][] var4, BlockPos var5, BlockPos var6) {
      super();
      this.field_212408_i = var1;
      this.field_212400_a = var2;
      this.field_212401_b = var3;
      this.field_212406_g = var4;
      this.field_212402_c = var5;
      this.field_212403_d = var6.func_177958_n() - var5.func_177958_n() + 1;
      this.field_212404_e = var6.func_177956_o() - var5.func_177956_o() + 1;
      this.field_212405_f = var6.func_177952_p() - var5.func_177952_p() + 1;
      this.field_212407_h = new RenderChunkCache.Entry[this.field_212403_d * this.field_212404_e * this.field_212405_f];

      BlockPos.MutableBlockPos var8;
      for(Iterator var7 = BlockPos.func_177975_b(var5, var6).iterator(); var7.hasNext(); this.field_212407_h[this.func_212398_a(var8)] = new RenderChunkCache.Entry(var1, var8)) {
         var8 = (BlockPos.MutableBlockPos)var7.next();
      }

   }

   protected int func_212398_a(BlockPos var1) {
      int var2 = var1.func_177958_n() - this.field_212402_c.func_177958_n();
      int var3 = var1.func_177956_o() - this.field_212402_c.func_177956_o();
      int var4 = var1.func_177952_p() - this.field_212402_c.func_177952_p();
      return var4 * this.field_212403_d * this.field_212404_e + var3 * this.field_212403_d + var2;
   }

   public IBlockState func_180495_p(BlockPos var1) {
      return this.field_212407_h[this.func_212398_a(var1)].field_212495_a;
   }

   public IFluidState func_204610_c(BlockPos var1) {
      return this.field_212407_h[this.func_212398_a(var1)].field_212496_b;
   }

   public Biome func_180494_b(BlockPos var1) {
      int var2 = (var1.func_177958_n() >> 4) - this.field_212400_a;
      int var3 = (var1.func_177952_p() >> 4) - this.field_212401_b;
      return this.field_212406_g[var2][var3].func_201600_k(var1);
   }

   private int func_212396_b(EnumLightType var1, BlockPos var2) {
      return this.field_212407_h[this.func_212398_a(var2)].func_212493_a(var1, var2);
   }

   public int func_175626_b(BlockPos var1, int var2) {
      int var3 = this.func_212396_b(EnumLightType.SKY, var1);
      int var4 = this.func_212396_b(EnumLightType.BLOCK, var1);
      if (var4 < var2) {
         var4 = var2;
      }

      return var3 << 20 | var4 << 4;
   }

   @Nullable
   public TileEntity func_175625_s(BlockPos var1) {
      return this.func_212399_a(var1, Chunk.EnumCreateEntityType.IMMEDIATE);
   }

   @Nullable
   public TileEntity func_212399_a(BlockPos var1, Chunk.EnumCreateEntityType var2) {
      int var3 = (var1.func_177958_n() >> 4) - this.field_212400_a;
      int var4 = (var1.func_177952_p() >> 4) - this.field_212401_b;
      return this.field_212406_g[var3][var4].func_177424_a(var1, var2);
   }

   public float func_205052_D(BlockPos var1) {
      return this.field_212408_i.field_73011_w.func_177497_p()[this.func_201696_r(var1)];
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
      return this.field_212408_i.func_201675_m();
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
               var3 = (var1.func_177958_n() >> 4) - this.field_212400_a;
               int var4 = (var1.func_177952_p() >> 4) - this.field_212401_b;
               return this.field_212406_g[var3][var4].func_177443_a(var1, var2);
            }
         }
      } else {
         return 15;
      }
   }

   public boolean func_175680_a(int var1, int var2, boolean var3) {
      return this.func_212395_a(var1, var2);
   }

   public boolean func_175678_i(BlockPos var1) {
      return false;
   }

   public boolean func_212395_a(int var1, int var2) {
      int var3 = var1 - this.field_212400_a;
      int var4 = var2 - this.field_212401_b;
      return var3 >= 0 && var3 < this.field_212406_g.length && var4 >= 0 && var4 < this.field_212406_g[var3].length;
   }

   public int func_201676_a(Heightmap.Type var1, int var2, int var3) {
      throw new RuntimeException("NOT IMPLEMENTED!");
   }

   public WorldBorder func_175723_af() {
      return this.field_212408_i.func_175723_af();
   }

   public boolean func_195585_a(@Nullable Entity var1, VoxelShape var2) {
      throw new RuntimeException("This method should never be called here. No entity logic inside Region");
   }

   @Nullable
   public EntityPlayer func_190525_a(double var1, double var3, double var5, double var7, Predicate<Entity> var9) {
      throw new RuntimeException("This method should never be called here. No entity logic inside Region");
   }

   public int func_175657_ab() {
      return 0;
   }

   public boolean func_175623_d(BlockPos var1) {
      return this.func_180495_p(var1).func_196958_f();
   }

   public int func_175642_b(EnumLightType var1, BlockPos var2) {
      if (var2.func_177956_o() >= 0 && var2.func_177956_o() < 256) {
         int var3 = (var2.func_177958_n() >> 4) - this.field_212400_a;
         int var4 = (var2.func_177952_p() >> 4) - this.field_212401_b;
         return this.field_212406_g[var3][var4].func_177413_a(var1, var2);
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

   public class Entry {
      protected final IBlockState field_212495_a;
      protected final IFluidState field_212496_b;
      private int[] field_212498_d;

      protected Entry(World var2, BlockPos var3) {
         super();
         this.field_212495_a = var2.func_180495_p(var3);
         this.field_212496_b = var2.func_204610_c(var3);
      }

      protected int func_212493_a(EnumLightType var1, BlockPos var2) {
         if (this.field_212498_d == null) {
            this.func_212492_a(var2);
         }

         return this.field_212498_d[var1.ordinal()];
      }

      private void func_212492_a(BlockPos var1) {
         this.field_212498_d = new int[EnumLightType.values().length];
         EnumLightType[] var2 = EnumLightType.values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            EnumLightType var5 = var2[var4];
            this.field_212498_d[var5.ordinal()] = this.func_212494_b(var5, var1);
         }

      }

      private int func_212494_b(EnumLightType var1, BlockPos var2) {
         if (var1 == EnumLightType.SKY && !RenderChunkCache.this.field_212408_i.func_201675_m().func_191066_m()) {
            return 0;
         } else if (var2.func_177956_o() >= 0 && var2.func_177956_o() < 256) {
            int var3;
            if (this.field_212495_a.func_200130_c(RenderChunkCache.this, var2)) {
               var3 = 0;
               EnumFacing[] var9 = EnumFacing.values();
               int var5 = var9.length;

               for(int var6 = 0; var6 < var5; ++var6) {
                  EnumFacing var7 = var9[var6];
                  int var8 = RenderChunkCache.this.func_175642_b(var1, var2.func_177972_a(var7));
                  if (var8 > var3) {
                     var3 = var8;
                  }

                  if (var3 >= 15) {
                     return var3;
                  }
               }

               return var3;
            } else {
               var3 = (var2.func_177958_n() >> 4) - RenderChunkCache.this.field_212400_a;
               int var4 = (var2.func_177952_p() >> 4) - RenderChunkCache.this.field_212401_b;
               return RenderChunkCache.this.field_212406_g[var3][var4].func_177413_a(var1, var2);
            }
         } else {
            return var1.field_77198_c;
         }
      }
   }
}
