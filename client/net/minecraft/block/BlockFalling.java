package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockFalling extends Block {
   public static boolean field_149832_M;

   public BlockFalling() {
      super(Material.field_151595_p);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public BlockFalling(Material var1) {
      super(var1);
   }

   public void func_176213_c(World var1, BlockPos var2, IBlockState var3) {
      var1.func_175684_a(var2, this, this.func_149738_a(var1));
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      var1.func_175684_a(var2, this, this.func_149738_a(var1));
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (!var1.field_72995_K) {
         this.func_176503_e(var1, var2);
      }

   }

   private void func_176503_e(World var1, BlockPos var2) {
      if (func_180685_d(var1, var2.func_177977_b()) && var2.func_177956_o() >= 0) {
         byte var3 = 32;
         if (!field_149832_M && var1.func_175707_a(var2.func_177982_a(-var3, -var3, -var3), var2.func_177982_a(var3, var3, var3))) {
            if (!var1.field_72995_K) {
               EntityFallingBlock var5 = new EntityFallingBlock(var1, (double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o(), (double)var2.func_177952_p() + 0.5D, var1.func_180495_p(var2));
               this.func_149829_a(var5);
               var1.func_72838_d(var5);
            }
         } else {
            var1.func_175698_g(var2);

            BlockPos var4;
            for(var4 = var2.func_177977_b(); func_180685_d(var1, var4) && var4.func_177956_o() > 0; var4 = var4.func_177977_b()) {
            }

            if (var4.func_177956_o() > 0) {
               var1.func_175656_a(var4.func_177984_a(), this.func_176223_P());
            }
         }

      }
   }

   protected void func_149829_a(EntityFallingBlock var1) {
   }

   public int func_149738_a(World var1) {
      return 2;
   }

   public static boolean func_180685_d(World var0, BlockPos var1) {
      Block var2 = var0.func_180495_p(var1).func_177230_c();
      Material var3 = var2.field_149764_J;
      return var2 == Blocks.field_150480_ab || var3 == Material.field_151579_a || var3 == Material.field_151586_h || var3 == Material.field_151587_i;
   }

   public void func_176502_a_(World var1, BlockPos var2) {
   }
}
