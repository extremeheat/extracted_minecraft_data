package net.minecraft.block;

import com.google.common.cache.LoadingCache;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPortal extends BlockBreakable {
   public static final PropertyEnum<EnumFacing.Axis> field_176550_a;

   public BlockPortal() {
      super(Material.field_151567_E, false);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176550_a, EnumFacing.Axis.X));
      this.func_149675_a(true);
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      super.func_180650_b(var1, var2, var3, var4);
      if (var1.field_73011_w.func_76569_d() && var1.func_82736_K().func_82766_b("doMobSpawning") && var4.nextInt(2000) < var1.func_175659_aa().func_151525_a()) {
         int var5 = var2.func_177956_o();

         BlockPos var6;
         for(var6 = var2; !World.func_175683_a(var1, var6) && var6.func_177956_o() > 0; var6 = var6.func_177977_b()) {
         }

         if (var5 > 0 && !var1.func_180495_p(var6.func_177984_a()).func_177230_c().func_149721_r()) {
            Entity var7 = ItemMonsterPlacer.func_77840_a(var1, 57, (double)var6.func_177958_n() + 0.5D, (double)var6.func_177956_o() + 1.1D, (double)var6.func_177952_p() + 0.5D);
            if (var7 != null) {
               var7.field_71088_bW = var7.func_82147_ab();
            }
         }
      }

   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      return null;
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      EnumFacing.Axis var3 = (EnumFacing.Axis)var1.func_180495_p(var2).func_177229_b(field_176550_a);
      float var4 = 0.125F;
      float var5 = 0.125F;
      if (var3 == EnumFacing.Axis.X) {
         var4 = 0.5F;
      }

      if (var3 == EnumFacing.Axis.Z) {
         var5 = 0.5F;
      }

      this.func_149676_a(0.5F - var4, 0.0F, 0.5F - var5, 0.5F + var4, 1.0F, 0.5F + var5);
   }

   public static int func_176549_a(EnumFacing.Axis var0) {
      if (var0 == EnumFacing.Axis.X) {
         return 1;
      } else {
         return var0 == EnumFacing.Axis.Z ? 2 : 0;
      }
   }

   public boolean func_149686_d() {
      return false;
   }

   public boolean func_176548_d(World var1, BlockPos var2) {
      BlockPortal.Size var3 = new BlockPortal.Size(var1, var2, EnumFacing.Axis.X);
      if (var3.func_150860_b() && var3.field_150864_e == 0) {
         var3.func_150859_c();
         return true;
      } else {
         BlockPortal.Size var4 = new BlockPortal.Size(var1, var2, EnumFacing.Axis.Z);
         if (var4.func_150860_b() && var4.field_150864_e == 0) {
            var4.func_150859_c();
            return true;
         } else {
            return false;
         }
      }
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      EnumFacing.Axis var5 = (EnumFacing.Axis)var3.func_177229_b(field_176550_a);
      BlockPortal.Size var6;
      if (var5 == EnumFacing.Axis.X) {
         var6 = new BlockPortal.Size(var1, var2, EnumFacing.Axis.X);
         if (!var6.func_150860_b() || var6.field_150864_e < var6.field_150868_h * var6.field_150862_g) {
            var1.func_175656_a(var2, Blocks.field_150350_a.func_176223_P());
         }
      } else if (var5 == EnumFacing.Axis.Z) {
         var6 = new BlockPortal.Size(var1, var2, EnumFacing.Axis.Z);
         if (!var6.func_150860_b() || var6.field_150864_e < var6.field_150868_h * var6.field_150862_g) {
            var1.func_175656_a(var2, Blocks.field_150350_a.func_176223_P());
         }
      }

   }

   public boolean func_176225_a(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      EnumFacing.Axis var4 = null;
      IBlockState var5 = var1.func_180495_p(var2);
      if (var1.func_180495_p(var2).func_177230_c() == this) {
         var4 = (EnumFacing.Axis)var5.func_177229_b(field_176550_a);
         if (var4 == null) {
            return false;
         }

         if (var4 == EnumFacing.Axis.Z && var3 != EnumFacing.EAST && var3 != EnumFacing.WEST) {
            return false;
         }

         if (var4 == EnumFacing.Axis.X && var3 != EnumFacing.SOUTH && var3 != EnumFacing.NORTH) {
            return false;
         }
      }

      boolean var6 = var1.func_180495_p(var2.func_177976_e()).func_177230_c() == this && var1.func_180495_p(var2.func_177985_f(2)).func_177230_c() != this;
      boolean var7 = var1.func_180495_p(var2.func_177974_f()).func_177230_c() == this && var1.func_180495_p(var2.func_177965_g(2)).func_177230_c() != this;
      boolean var8 = var1.func_180495_p(var2.func_177978_c()).func_177230_c() == this && var1.func_180495_p(var2.func_177964_d(2)).func_177230_c() != this;
      boolean var9 = var1.func_180495_p(var2.func_177968_d()).func_177230_c() == this && var1.func_180495_p(var2.func_177970_e(2)).func_177230_c() != this;
      boolean var10 = var6 || var7 || var4 == EnumFacing.Axis.X;
      boolean var11 = var8 || var9 || var4 == EnumFacing.Axis.Z;
      if (var10 && var3 == EnumFacing.WEST) {
         return true;
      } else if (var10 && var3 == EnumFacing.EAST) {
         return true;
      } else if (var11 && var3 == EnumFacing.NORTH) {
         return true;
      } else {
         return var11 && var3 == EnumFacing.SOUTH;
      }
   }

   public int func_149745_a(Random var1) {
      return 0;
   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.TRANSLUCENT;
   }

   public void func_180634_a(World var1, BlockPos var2, IBlockState var3, Entity var4) {
      if (var4.field_70154_o == null && var4.field_70153_n == null) {
         var4.func_181015_d(var2);
      }

   }

   public void func_180655_c(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (var4.nextInt(100) == 0) {
         var1.func_72980_b((double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 0.5D, (double)var2.func_177952_p() + 0.5D, "portal.portal", 0.5F, var4.nextFloat() * 0.4F + 0.8F, false);
      }

      for(int var5 = 0; var5 < 4; ++var5) {
         double var6 = (double)((float)var2.func_177958_n() + var4.nextFloat());
         double var8 = (double)((float)var2.func_177956_o() + var4.nextFloat());
         double var10 = (double)((float)var2.func_177952_p() + var4.nextFloat());
         double var12 = ((double)var4.nextFloat() - 0.5D) * 0.5D;
         double var14 = ((double)var4.nextFloat() - 0.5D) * 0.5D;
         double var16 = ((double)var4.nextFloat() - 0.5D) * 0.5D;
         int var18 = var4.nextInt(2) * 2 - 1;
         if (var1.func_180495_p(var2.func_177976_e()).func_177230_c() != this && var1.func_180495_p(var2.func_177974_f()).func_177230_c() != this) {
            var6 = (double)var2.func_177958_n() + 0.5D + 0.25D * (double)var18;
            var12 = (double)(var4.nextFloat() * 2.0F * (float)var18);
         } else {
            var10 = (double)var2.func_177952_p() + 0.5D + 0.25D * (double)var18;
            var16 = (double)(var4.nextFloat() * 2.0F * (float)var18);
         }

         var1.func_175688_a(EnumParticleTypes.PORTAL, var6, var8, var10, var12, var14, var16);
      }

   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return null;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176550_a, (var1 & 3) == 2 ? EnumFacing.Axis.Z : EnumFacing.Axis.X);
   }

   public int func_176201_c(IBlockState var1) {
      return func_176549_a((EnumFacing.Axis)var1.func_177229_b(field_176550_a));
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176550_a});
   }

   public BlockPattern.PatternHelper func_181089_f(World var1, BlockPos var2) {
      EnumFacing.Axis var3 = EnumFacing.Axis.Z;
      BlockPortal.Size var4 = new BlockPortal.Size(var1, var2, EnumFacing.Axis.X);
      LoadingCache var5 = BlockPattern.func_181627_a(var1, true);
      if (!var4.func_150860_b()) {
         var3 = EnumFacing.Axis.X;
         var4 = new BlockPortal.Size(var1, var2, EnumFacing.Axis.Z);
      }

      if (!var4.func_150860_b()) {
         return new BlockPattern.PatternHelper(var2, EnumFacing.NORTH, EnumFacing.UP, var5, 1, 1, 1);
      } else {
         int[] var6 = new int[EnumFacing.AxisDirection.values().length];
         EnumFacing var7 = var4.field_150866_c.func_176735_f();
         BlockPos var8 = var4.field_150861_f.func_177981_b(var4.func_181100_a() - 1);
         EnumFacing.AxisDirection[] var9 = EnumFacing.AxisDirection.values();
         int var10 = var9.length;

         int var11;
         for(var11 = 0; var11 < var10; ++var11) {
            EnumFacing.AxisDirection var12 = var9[var11];
            BlockPattern.PatternHelper var13 = new BlockPattern.PatternHelper(var7.func_176743_c() == var12 ? var8 : var8.func_177967_a(var4.field_150866_c, var4.func_181101_b() - 1), EnumFacing.func_181076_a(var12, var3), EnumFacing.UP, var5, var4.func_181101_b(), var4.func_181100_a(), 1);

            for(int var14 = 0; var14 < var4.func_181101_b(); ++var14) {
               for(int var15 = 0; var15 < var4.func_181100_a(); ++var15) {
                  BlockWorldState var16 = var13.func_177670_a(var14, var15, 1);
                  if (var16.func_177509_a() != null && var16.func_177509_a().func_177230_c().func_149688_o() != Material.field_151579_a) {
                     ++var6[var12.ordinal()];
                  }
               }
            }
         }

         EnumFacing.AxisDirection var17 = EnumFacing.AxisDirection.POSITIVE;
         EnumFacing.AxisDirection[] var18 = EnumFacing.AxisDirection.values();
         var11 = var18.length;

         for(int var19 = 0; var19 < var11; ++var19) {
            EnumFacing.AxisDirection var20 = var18[var19];
            if (var6[var20.ordinal()] < var6[var17.ordinal()]) {
               var17 = var20;
            }
         }

         return new BlockPattern.PatternHelper(var7.func_176743_c() == var17 ? var8 : var8.func_177967_a(var4.field_150866_c, var4.func_181101_b() - 1), EnumFacing.func_181076_a(var17, var3), EnumFacing.UP, var5, var4.func_181101_b(), var4.func_181100_a(), 1);
      }
   }

   static {
      field_176550_a = PropertyEnum.func_177706_a("axis", EnumFacing.Axis.class, EnumFacing.Axis.X, EnumFacing.Axis.Z);
   }

   public static class Size {
      private final World field_150867_a;
      private final EnumFacing.Axis field_150865_b;
      private final EnumFacing field_150866_c;
      private final EnumFacing field_150863_d;
      private int field_150864_e = 0;
      private BlockPos field_150861_f;
      private int field_150862_g;
      private int field_150868_h;

      public Size(World var1, BlockPos var2, EnumFacing.Axis var3) {
         super();
         this.field_150867_a = var1;
         this.field_150865_b = var3;
         if (var3 == EnumFacing.Axis.X) {
            this.field_150863_d = EnumFacing.EAST;
            this.field_150866_c = EnumFacing.WEST;
         } else {
            this.field_150863_d = EnumFacing.NORTH;
            this.field_150866_c = EnumFacing.SOUTH;
         }

         for(BlockPos var4 = var2; var2.func_177956_o() > var4.func_177956_o() - 21 && var2.func_177956_o() > 0 && this.func_150857_a(var1.func_180495_p(var2.func_177977_b()).func_177230_c()); var2 = var2.func_177977_b()) {
         }

         int var5 = this.func_180120_a(var2, this.field_150863_d) - 1;
         if (var5 >= 0) {
            this.field_150861_f = var2.func_177967_a(this.field_150863_d, var5);
            this.field_150868_h = this.func_180120_a(this.field_150861_f, this.field_150866_c);
            if (this.field_150868_h < 2 || this.field_150868_h > 21) {
               this.field_150861_f = null;
               this.field_150868_h = 0;
            }
         }

         if (this.field_150861_f != null) {
            this.field_150862_g = this.func_150858_a();
         }

      }

      protected int func_180120_a(BlockPos var1, EnumFacing var2) {
         int var3;
         for(var3 = 0; var3 < 22; ++var3) {
            BlockPos var4 = var1.func_177967_a(var2, var3);
            if (!this.func_150857_a(this.field_150867_a.func_180495_p(var4).func_177230_c()) || this.field_150867_a.func_180495_p(var4.func_177977_b()).func_177230_c() != Blocks.field_150343_Z) {
               break;
            }
         }

         Block var5 = this.field_150867_a.func_180495_p(var1.func_177967_a(var2, var3)).func_177230_c();
         return var5 == Blocks.field_150343_Z ? var3 : 0;
      }

      public int func_181100_a() {
         return this.field_150862_g;
      }

      public int func_181101_b() {
         return this.field_150868_h;
      }

      protected int func_150858_a() {
         int var1;
         label56:
         for(this.field_150862_g = 0; this.field_150862_g < 21; ++this.field_150862_g) {
            for(var1 = 0; var1 < this.field_150868_h; ++var1) {
               BlockPos var2 = this.field_150861_f.func_177967_a(this.field_150866_c, var1).func_177981_b(this.field_150862_g);
               Block var3 = this.field_150867_a.func_180495_p(var2).func_177230_c();
               if (!this.func_150857_a(var3)) {
                  break label56;
               }

               if (var3 == Blocks.field_150427_aO) {
                  ++this.field_150864_e;
               }

               if (var1 == 0) {
                  var3 = this.field_150867_a.func_180495_p(var2.func_177972_a(this.field_150863_d)).func_177230_c();
                  if (var3 != Blocks.field_150343_Z) {
                     break label56;
                  }
               } else if (var1 == this.field_150868_h - 1) {
                  var3 = this.field_150867_a.func_180495_p(var2.func_177972_a(this.field_150866_c)).func_177230_c();
                  if (var3 != Blocks.field_150343_Z) {
                     break label56;
                  }
               }
            }
         }

         for(var1 = 0; var1 < this.field_150868_h; ++var1) {
            if (this.field_150867_a.func_180495_p(this.field_150861_f.func_177967_a(this.field_150866_c, var1).func_177981_b(this.field_150862_g)).func_177230_c() != Blocks.field_150343_Z) {
               this.field_150862_g = 0;
               break;
            }
         }

         if (this.field_150862_g <= 21 && this.field_150862_g >= 3) {
            return this.field_150862_g;
         } else {
            this.field_150861_f = null;
            this.field_150868_h = 0;
            this.field_150862_g = 0;
            return 0;
         }
      }

      protected boolean func_150857_a(Block var1) {
         return var1.field_149764_J == Material.field_151579_a || var1 == Blocks.field_150480_ab || var1 == Blocks.field_150427_aO;
      }

      public boolean func_150860_b() {
         return this.field_150861_f != null && this.field_150868_h >= 2 && this.field_150868_h <= 21 && this.field_150862_g >= 3 && this.field_150862_g <= 21;
      }

      public void func_150859_c() {
         for(int var1 = 0; var1 < this.field_150868_h; ++var1) {
            BlockPos var2 = this.field_150861_f.func_177967_a(this.field_150866_c, var1);

            for(int var3 = 0; var3 < this.field_150862_g; ++var3) {
               this.field_150867_a.func_180501_a(var2.func_177981_b(var3), Blocks.field_150427_aO.func_176223_P().func_177226_a(BlockPortal.field_176550_a, this.field_150865_b), 2);
            }
         }

      }
   }
}
