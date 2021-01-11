package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;

public abstract class BlockLiquid extends Block {
   public static final PropertyInteger field_176367_b = PropertyInteger.func_177719_a("level", 0, 15);

   protected BlockLiquid(Material var1) {
      super(var1);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176367_b, 0));
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      this.func_149675_a(true);
   }

   public boolean func_176205_b(IBlockAccess var1, BlockPos var2) {
      return this.field_149764_J != Material.field_151587_i;
   }

   public int func_180662_a(IBlockAccess var1, BlockPos var2, int var3) {
      return this.field_149764_J == Material.field_151586_h ? BiomeColorHelper.func_180288_c(var1, var2) : 16777215;
   }

   public static float func_149801_b(int var0) {
      if (var0 >= 8) {
         var0 = 0;
      }

      return (float)(var0 + 1) / 9.0F;
   }

   protected int func_176362_e(IBlockAccess var1, BlockPos var2) {
      return var1.func_180495_p(var2).func_177230_c().func_149688_o() == this.field_149764_J ? (Integer)var1.func_180495_p(var2).func_177229_b(field_176367_b) : -1;
   }

   protected int func_176366_f(IBlockAccess var1, BlockPos var2) {
      int var3 = this.func_176362_e(var1, var2);
      return var3 >= 8 ? 0 : var3;
   }

   public boolean func_149686_d() {
      return false;
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_176209_a(IBlockState var1, boolean var2) {
      return var2 && (Integer)var1.func_177229_b(field_176367_b) == 0;
   }

   public boolean func_176212_b(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      Material var4 = var1.func_180495_p(var2).func_177230_c().func_149688_o();
      if (var4 == this.field_149764_J) {
         return false;
      } else if (var3 == EnumFacing.UP) {
         return true;
      } else {
         return var4 == Material.field_151588_w ? false : super.func_176212_b(var1, var2, var3);
      }
   }

   public boolean func_176225_a(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      if (var1.func_180495_p(var2).func_177230_c().func_149688_o() == this.field_149764_J) {
         return false;
      } else {
         return var3 == EnumFacing.UP ? true : super.func_176225_a(var1, var2, var3);
      }
   }

   public boolean func_176364_g(IBlockAccess var1, BlockPos var2) {
      for(int var3 = -1; var3 <= 1; ++var3) {
         for(int var4 = -1; var4 <= 1; ++var4) {
            IBlockState var5 = var1.func_180495_p(var2.func_177982_a(var3, 0, var4));
            Block var6 = var5.func_177230_c();
            Material var7 = var6.func_149688_o();
            if (var7 != this.field_149764_J && !var6.func_149730_j()) {
               return true;
            }
         }
      }

      return false;
   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      return null;
   }

   public int func_149645_b() {
      return 1;
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return null;
   }

   public int func_149745_a(Random var1) {
      return 0;
   }

   protected Vec3 func_180687_h(IBlockAccess var1, BlockPos var2) {
      Vec3 var3 = new Vec3(0.0D, 0.0D, 0.0D);
      int var4 = this.func_176366_f(var1, var2);
      Iterator var5 = EnumFacing.Plane.HORIZONTAL.iterator();

      EnumFacing var6;
      BlockPos var7;
      while(var5.hasNext()) {
         var6 = (EnumFacing)var5.next();
         var7 = var2.func_177972_a(var6);
         int var8 = this.func_176366_f(var1, var7);
         int var9;
         if (var8 < 0) {
            if (!var1.func_180495_p(var7).func_177230_c().func_149688_o().func_76230_c()) {
               var8 = this.func_176366_f(var1, var7.func_177977_b());
               if (var8 >= 0) {
                  var9 = var8 - (var4 - 8);
                  var3 = var3.func_72441_c((double)((var7.func_177958_n() - var2.func_177958_n()) * var9), (double)((var7.func_177956_o() - var2.func_177956_o()) * var9), (double)((var7.func_177952_p() - var2.func_177952_p()) * var9));
               }
            }
         } else if (var8 >= 0) {
            var9 = var8 - var4;
            var3 = var3.func_72441_c((double)((var7.func_177958_n() - var2.func_177958_n()) * var9), (double)((var7.func_177956_o() - var2.func_177956_o()) * var9), (double)((var7.func_177952_p() - var2.func_177952_p()) * var9));
         }
      }

      if ((Integer)var1.func_180495_p(var2).func_177229_b(field_176367_b) >= 8) {
         var5 = EnumFacing.Plane.HORIZONTAL.iterator();

         do {
            if (!var5.hasNext()) {
               return var3.func_72432_b();
            }

            var6 = (EnumFacing)var5.next();
            var7 = var2.func_177972_a(var6);
         } while(!this.func_176212_b(var1, var7, var6) && !this.func_176212_b(var1, var7.func_177984_a(), var6));

         var3 = var3.func_72432_b().func_72441_c(0.0D, -6.0D, 0.0D);
      }

      return var3.func_72432_b();
   }

   public Vec3 func_176197_a(World var1, BlockPos var2, Entity var3, Vec3 var4) {
      return var4.func_178787_e(this.func_180687_h(var1, var2));
   }

   public int func_149738_a(World var1) {
      if (this.field_149764_J == Material.field_151586_h) {
         return 5;
      } else if (this.field_149764_J == Material.field_151587_i) {
         return var1.field_73011_w.func_177495_o() ? 10 : 30;
      } else {
         return 0;
      }
   }

   public int func_176207_c(IBlockAccess var1, BlockPos var2) {
      int var3 = var1.func_175626_b(var2, 0);
      int var4 = var1.func_175626_b(var2.func_177984_a(), 0);
      int var5 = var3 & 255;
      int var6 = var4 & 255;
      int var7 = var3 >> 16 & 255;
      int var8 = var4 >> 16 & 255;
      return (var5 > var6 ? var5 : var6) | (var7 > var8 ? var7 : var8) << 16;
   }

   public EnumWorldBlockLayer func_180664_k() {
      return this.field_149764_J == Material.field_151586_h ? EnumWorldBlockLayer.TRANSLUCENT : EnumWorldBlockLayer.SOLID;
   }

   public void func_180655_c(World var1, BlockPos var2, IBlockState var3, Random var4) {
      double var5 = (double)var2.func_177958_n();
      double var7 = (double)var2.func_177956_o();
      double var9 = (double)var2.func_177952_p();
      if (this.field_149764_J == Material.field_151586_h) {
         int var11 = (Integer)var3.func_177229_b(field_176367_b);
         if (var11 > 0 && var11 < 8) {
            if (var4.nextInt(64) == 0) {
               var1.func_72980_b(var5 + 0.5D, var7 + 0.5D, var9 + 0.5D, "liquid.water", var4.nextFloat() * 0.25F + 0.75F, var4.nextFloat() * 1.0F + 0.5F, false);
            }
         } else if (var4.nextInt(10) == 0) {
            var1.func_175688_a(EnumParticleTypes.SUSPENDED, var5 + (double)var4.nextFloat(), var7 + (double)var4.nextFloat(), var9 + (double)var4.nextFloat(), 0.0D, 0.0D, 0.0D);
         }
      }

      if (this.field_149764_J == Material.field_151587_i && var1.func_180495_p(var2.func_177984_a()).func_177230_c().func_149688_o() == Material.field_151579_a && !var1.func_180495_p(var2.func_177984_a()).func_177230_c().func_149662_c()) {
         if (var4.nextInt(100) == 0) {
            double var18 = var5 + (double)var4.nextFloat();
            double var13 = var7 + this.field_149756_F;
            double var15 = var9 + (double)var4.nextFloat();
            var1.func_175688_a(EnumParticleTypes.LAVA, var18, var13, var15, 0.0D, 0.0D, 0.0D);
            var1.func_72980_b(var18, var13, var15, "liquid.lavapop", 0.2F + var4.nextFloat() * 0.2F, 0.9F + var4.nextFloat() * 0.15F, false);
         }

         if (var4.nextInt(200) == 0) {
            var1.func_72980_b(var5, var7, var9, "liquid.lava", 0.2F + var4.nextFloat() * 0.2F, 0.9F + var4.nextFloat() * 0.15F, false);
         }
      }

      if (var4.nextInt(10) == 0 && World.func_175683_a(var1, var2.func_177977_b())) {
         Material var19 = var1.func_180495_p(var2.func_177979_c(2)).func_177230_c().func_149688_o();
         if (!var19.func_76230_c() && !var19.func_76224_d()) {
            double var12 = var5 + (double)var4.nextFloat();
            double var14 = var7 - 1.05D;
            double var16 = var9 + (double)var4.nextFloat();
            if (this.field_149764_J == Material.field_151586_h) {
               var1.func_175688_a(EnumParticleTypes.DRIP_WATER, var12, var14, var16, 0.0D, 0.0D, 0.0D);
            } else {
               var1.func_175688_a(EnumParticleTypes.DRIP_LAVA, var12, var14, var16, 0.0D, 0.0D, 0.0D);
            }
         }
      }

   }

   public static double func_180689_a(IBlockAccess var0, BlockPos var1, Material var2) {
      Vec3 var3 = func_176361_a(var2).func_180687_h(var0, var1);
      return var3.field_72450_a == 0.0D && var3.field_72449_c == 0.0D ? -1000.0D : MathHelper.func_181159_b(var3.field_72449_c, var3.field_72450_a) - 1.5707963267948966D;
   }

   public void func_176213_c(World var1, BlockPos var2, IBlockState var3) {
      this.func_176365_e(var1, var2, var3);
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      this.func_176365_e(var1, var2, var3);
   }

   public boolean func_176365_e(World var1, BlockPos var2, IBlockState var3) {
      if (this.field_149764_J == Material.field_151587_i) {
         boolean var4 = false;
         EnumFacing[] var5 = EnumFacing.values();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            EnumFacing var8 = var5[var7];
            if (var8 != EnumFacing.DOWN && var1.func_180495_p(var2.func_177972_a(var8)).func_177230_c().func_149688_o() == Material.field_151586_h) {
               var4 = true;
               break;
            }
         }

         if (var4) {
            Integer var9 = (Integer)var3.func_177229_b(field_176367_b);
            if (var9 == 0) {
               var1.func_175656_a(var2, Blocks.field_150343_Z.func_176223_P());
               this.func_180688_d(var1, var2);
               return true;
            }

            if (var9 <= 4) {
               var1.func_175656_a(var2, Blocks.field_150347_e.func_176223_P());
               this.func_180688_d(var1, var2);
               return true;
            }
         }
      }

      return false;
   }

   protected void func_180688_d(World var1, BlockPos var2) {
      double var3 = (double)var2.func_177958_n();
      double var5 = (double)var2.func_177956_o();
      double var7 = (double)var2.func_177952_p();
      var1.func_72908_a(var3 + 0.5D, var5 + 0.5D, var7 + 0.5D, "random.fizz", 0.5F, 2.6F + (var1.field_73012_v.nextFloat() - var1.field_73012_v.nextFloat()) * 0.8F);

      for(int var9 = 0; var9 < 8; ++var9) {
         var1.func_175688_a(EnumParticleTypes.SMOKE_LARGE, var3 + Math.random(), var5 + 1.2D, var7 + Math.random(), 0.0D, 0.0D, 0.0D);
      }

   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176367_b, var1);
   }

   public int func_176201_c(IBlockState var1) {
      return (Integer)var1.func_177229_b(field_176367_b);
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176367_b});
   }

   public static BlockDynamicLiquid func_176361_a(Material var0) {
      if (var0 == Material.field_151586_h) {
         return Blocks.field_150358_i;
      } else if (var0 == Material.field_151587_i) {
         return Blocks.field_150356_k;
      } else {
         throw new IllegalArgumentException("Invalid material");
      }
   }

   public static BlockStaticLiquid func_176363_b(Material var0) {
      if (var0 == Material.field_151586_h) {
         return Blocks.field_150355_j;
      } else if (var0 == Material.field_151587_i) {
         return Blocks.field_150353_l;
      } else {
         throw new IllegalArgumentException("Invalid material");
      }
   }
}
