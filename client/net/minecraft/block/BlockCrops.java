package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockCrops extends BlockBush implements IGrowable {
   public static final PropertyInteger field_176488_a = PropertyInteger.func_177719_a("age", 0, 7);

   protected BlockCrops() {
      super();
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176488_a, 0));
      this.func_149675_a(true);
      float var1 = 0.5F;
      this.func_149676_a(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, 0.25F, 0.5F + var1);
      this.func_149647_a((CreativeTabs)null);
      this.func_149711_c(0.0F);
      this.func_149672_a(field_149779_h);
      this.func_149649_H();
   }

   protected boolean func_149854_a(Block var1) {
      return var1 == Blocks.field_150458_ak;
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      super.func_180650_b(var1, var2, var3, var4);
      if (var1.func_175671_l(var2.func_177984_a()) >= 9) {
         int var5 = (Integer)var3.func_177229_b(field_176488_a);
         if (var5 < 7) {
            float var6 = func_180672_a(this, var1, var2);
            if (var4.nextInt((int)(25.0F / var6) + 1) == 0) {
               var1.func_180501_a(var2, var3.func_177226_a(field_176488_a, var5 + 1), 2);
            }
         }
      }

   }

   public void func_176487_g(World var1, BlockPos var2, IBlockState var3) {
      int var4 = (Integer)var3.func_177229_b(field_176488_a) + MathHelper.func_76136_a(var1.field_73012_v, 2, 5);
      if (var4 > 7) {
         var4 = 7;
      }

      var1.func_180501_a(var2, var3.func_177226_a(field_176488_a, var4), 2);
   }

   protected static float func_180672_a(Block var0, World var1, BlockPos var2) {
      float var3 = 1.0F;
      BlockPos var4 = var2.func_177977_b();

      for(int var5 = -1; var5 <= 1; ++var5) {
         for(int var6 = -1; var6 <= 1; ++var6) {
            float var7 = 0.0F;
            IBlockState var8 = var1.func_180495_p(var4.func_177982_a(var5, 0, var6));
            if (var8.func_177230_c() == Blocks.field_150458_ak) {
               var7 = 1.0F;
               if ((Integer)var8.func_177229_b(BlockFarmland.field_176531_a) > 0) {
                  var7 = 3.0F;
               }
            }

            if (var5 != 0 || var6 != 0) {
               var7 /= 4.0F;
            }

            var3 += var7;
         }
      }

      BlockPos var12 = var2.func_177978_c();
      BlockPos var13 = var2.func_177968_d();
      BlockPos var15 = var2.func_177976_e();
      BlockPos var14 = var2.func_177974_f();
      boolean var9 = var0 == var1.func_180495_p(var15).func_177230_c() || var0 == var1.func_180495_p(var14).func_177230_c();
      boolean var10 = var0 == var1.func_180495_p(var12).func_177230_c() || var0 == var1.func_180495_p(var13).func_177230_c();
      if (var9 && var10) {
         var3 /= 2.0F;
      } else {
         boolean var11 = var0 == var1.func_180495_p(var15.func_177978_c()).func_177230_c() || var0 == var1.func_180495_p(var14.func_177978_c()).func_177230_c() || var0 == var1.func_180495_p(var14.func_177968_d()).func_177230_c() || var0 == var1.func_180495_p(var15.func_177968_d()).func_177230_c();
         if (var11) {
            var3 /= 2.0F;
         }
      }

      return var3;
   }

   public boolean func_180671_f(World var1, BlockPos var2, IBlockState var3) {
      return (var1.func_175699_k(var2) >= 8 || var1.func_175678_i(var2)) && this.func_149854_a(var1.func_180495_p(var2.func_177977_b()).func_177230_c());
   }

   protected Item func_149866_i() {
      return Items.field_151014_N;
   }

   protected Item func_149865_P() {
      return Items.field_151015_O;
   }

   public void func_180653_a(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      super.func_180653_a(var1, var2, var3, var4, 0);
      if (!var1.field_72995_K) {
         int var6 = (Integer)var3.func_177229_b(field_176488_a);
         if (var6 >= 7) {
            int var7 = 3 + var5;

            for(int var8 = 0; var8 < var7; ++var8) {
               if (var1.field_73012_v.nextInt(15) <= var6) {
                  func_180635_a(var1, var2, new ItemStack(this.func_149866_i(), 1, 0));
               }
            }
         }

      }
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return (Integer)var1.func_177229_b(field_176488_a) == 7 ? this.func_149865_P() : this.func_149866_i();
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return this.func_149866_i();
   }

   public boolean func_176473_a(World var1, BlockPos var2, IBlockState var3, boolean var4) {
      return (Integer)var3.func_177229_b(field_176488_a) < 7;
   }

   public boolean func_180670_a(World var1, Random var2, BlockPos var3, IBlockState var4) {
      return true;
   }

   public void func_176474_b(World var1, Random var2, BlockPos var3, IBlockState var4) {
      this.func_176487_g(var1, var3, var4);
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176488_a, var1);
   }

   public int func_176201_c(IBlockState var1) {
      return (Integer)var1.func_177229_b(field_176488_a);
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176488_a});
   }
}
