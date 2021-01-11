package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneTorch extends BlockTorch {
   private static Map<World, List<BlockRedstoneTorch.Toggle>> field_150112_b = Maps.newHashMap();
   private final boolean field_150113_a;

   private boolean func_176598_a(World var1, BlockPos var2, boolean var3) {
      if (!field_150112_b.containsKey(var1)) {
         field_150112_b.put(var1, Lists.newArrayList());
      }

      List var4 = (List)field_150112_b.get(var1);
      if (var3) {
         var4.add(new BlockRedstoneTorch.Toggle(var2, var1.func_82737_E()));
      }

      int var5 = 0;

      for(int var6 = 0; var6 < var4.size(); ++var6) {
         BlockRedstoneTorch.Toggle var7 = (BlockRedstoneTorch.Toggle)var4.get(var6);
         if (var7.field_180111_a.equals(var2)) {
            ++var5;
            if (var5 >= 8) {
               return true;
            }
         }
      }

      return false;
   }

   protected BlockRedstoneTorch(boolean var1) {
      super();
      this.field_150113_a = var1;
      this.func_149675_a(true);
      this.func_149647_a((CreativeTabs)null);
   }

   public int func_149738_a(World var1) {
      return 2;
   }

   public void func_176213_c(World var1, BlockPos var2, IBlockState var3) {
      if (this.field_150113_a) {
         EnumFacing[] var4 = EnumFacing.values();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            EnumFacing var7 = var4[var6];
            var1.func_175685_c(var2.func_177972_a(var7), this);
         }
      }

   }

   public void func_180663_b(World var1, BlockPos var2, IBlockState var3) {
      if (this.field_150113_a) {
         EnumFacing[] var4 = EnumFacing.values();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            EnumFacing var7 = var4[var6];
            var1.func_175685_c(var2.func_177972_a(var7), this);
         }
      }

   }

   public int func_180656_a(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return this.field_150113_a && var3.func_177229_b(field_176596_a) != var4 ? 15 : 0;
   }

   private boolean func_176597_g(World var1, BlockPos var2, IBlockState var3) {
      EnumFacing var4 = ((EnumFacing)var3.func_177229_b(field_176596_a)).func_176734_d();
      return var1.func_175709_b(var2.func_177972_a(var4), var4);
   }

   public void func_180645_a(World var1, BlockPos var2, IBlockState var3, Random var4) {
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      boolean var5 = this.func_176597_g(var1, var2, var3);
      List var6 = (List)field_150112_b.get(var1);

      while(var6 != null && !var6.isEmpty() && var1.func_82737_E() - ((BlockRedstoneTorch.Toggle)var6.get(0)).field_150844_d > 60L) {
         var6.remove(0);
      }

      if (this.field_150113_a) {
         if (var5) {
            var1.func_180501_a(var2, Blocks.field_150437_az.func_176223_P().func_177226_a(field_176596_a, var3.func_177229_b(field_176596_a)), 3);
            if (this.func_176598_a(var1, var2, true)) {
               var1.func_72908_a((double)((float)var2.func_177958_n() + 0.5F), (double)((float)var2.func_177956_o() + 0.5F), (double)((float)var2.func_177952_p() + 0.5F), "random.fizz", 0.5F, 2.6F + (var1.field_73012_v.nextFloat() - var1.field_73012_v.nextFloat()) * 0.8F);

               for(int var7 = 0; var7 < 5; ++var7) {
                  double var8 = (double)var2.func_177958_n() + var4.nextDouble() * 0.6D + 0.2D;
                  double var10 = (double)var2.func_177956_o() + var4.nextDouble() * 0.6D + 0.2D;
                  double var12 = (double)var2.func_177952_p() + var4.nextDouble() * 0.6D + 0.2D;
                  var1.func_175688_a(EnumParticleTypes.SMOKE_NORMAL, var8, var10, var12, 0.0D, 0.0D, 0.0D);
               }

               var1.func_175684_a(var2, var1.func_180495_p(var2).func_177230_c(), 160);
            }
         }
      } else if (!var5 && !this.func_176598_a(var1, var2, false)) {
         var1.func_180501_a(var2, Blocks.field_150429_aA.func_176223_P().func_177226_a(field_176596_a, var3.func_177229_b(field_176596_a)), 3);
      }

   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!this.func_176592_e(var1, var2, var3)) {
         if (this.field_150113_a == this.func_176597_g(var1, var2, var3)) {
            var1.func_175684_a(var2, this, this.func_149738_a(var1));
         }

      }
   }

   public int func_176211_b(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return var4 == EnumFacing.DOWN ? this.func_180656_a(var1, var2, var3, var4) : 0;
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Item.func_150898_a(Blocks.field_150429_aA);
   }

   public boolean func_149744_f() {
      return true;
   }

   public void func_180655_c(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (this.field_150113_a) {
         double var5 = (double)var2.func_177958_n() + 0.5D + (var4.nextDouble() - 0.5D) * 0.2D;
         double var7 = (double)var2.func_177956_o() + 0.7D + (var4.nextDouble() - 0.5D) * 0.2D;
         double var9 = (double)var2.func_177952_p() + 0.5D + (var4.nextDouble() - 0.5D) * 0.2D;
         EnumFacing var11 = (EnumFacing)var3.func_177229_b(field_176596_a);
         if (var11.func_176740_k().func_176722_c()) {
            EnumFacing var12 = var11.func_176734_d();
            double var13 = 0.27D;
            var5 += 0.27D * (double)var12.func_82601_c();
            var7 += 0.22D;
            var9 += 0.27D * (double)var12.func_82599_e();
         }

         var1.func_175688_a(EnumParticleTypes.REDSTONE, var5, var7, var9, 0.0D, 0.0D, 0.0D);
      }
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return Item.func_150898_a(Blocks.field_150429_aA);
   }

   public boolean func_149667_c(Block var1) {
      return var1 == Blocks.field_150437_az || var1 == Blocks.field_150429_aA;
   }

   static class Toggle {
      BlockPos field_180111_a;
      long field_150844_d;

      public Toggle(BlockPos var1, long var2) {
         super();
         this.field_180111_a = var1;
         this.field_150844_d = var2;
      }
   }
}
