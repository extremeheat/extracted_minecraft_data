package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
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
import net.minecraft.world.World;

public class BlockNetherWart extends BlockBush {
   public static final PropertyInteger field_176486_a = PropertyInteger.func_177719_a("age", 0, 3);

   protected BlockNetherWart() {
      super(Material.field_151585_k, MapColor.field_151645_D);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176486_a, 0));
      this.func_149675_a(true);
      float var1 = 0.5F;
      this.func_149676_a(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, 0.25F, 0.5F + var1);
      this.func_149647_a((CreativeTabs)null);
   }

   protected boolean func_149854_a(Block var1) {
      return var1 == Blocks.field_150425_aM;
   }

   public boolean func_180671_f(World var1, BlockPos var2, IBlockState var3) {
      return this.func_149854_a(var1.func_180495_p(var2.func_177977_b()).func_177230_c());
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      int var5 = (Integer)var3.func_177229_b(field_176486_a);
      if (var5 < 3 && var4.nextInt(10) == 0) {
         var3 = var3.func_177226_a(field_176486_a, var5 + 1);
         var1.func_180501_a(var2, var3, 2);
      }

      super.func_180650_b(var1, var2, var3, var4);
   }

   public void func_180653_a(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      if (!var1.field_72995_K) {
         int var6 = 1;
         if ((Integer)var3.func_177229_b(field_176486_a) >= 3) {
            var6 = 2 + var1.field_73012_v.nextInt(3);
            if (var5 > 0) {
               var6 += var1.field_73012_v.nextInt(var5 + 1);
            }
         }

         for(int var7 = 0; var7 < var6; ++var7) {
            func_180635_a(var1, var2, new ItemStack(Items.field_151075_bm));
         }

      }
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return null;
   }

   public int func_149745_a(Random var1) {
      return 0;
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return Items.field_151075_bm;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176486_a, var1);
   }

   public int func_176201_c(IBlockState var1) {
      return (Integer)var1.func_177229_b(field_176486_a);
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176486_a});
   }
}
