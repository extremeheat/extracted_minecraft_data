package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockStaticLiquid extends BlockLiquid {
   protected BlockStaticLiquid(Material var1) {
      super(var1);
      this.func_149675_a(false);
      if (var1 == Material.field_151587_i) {
         this.func_149675_a(true);
      }

   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!this.func_176365_e(var1, var2, var3)) {
         this.func_176370_f(var1, var2, var3);
      }

   }

   private void func_176370_f(World var1, BlockPos var2, IBlockState var3) {
      BlockDynamicLiquid var4 = func_176361_a(this.field_149764_J);
      var1.func_180501_a(var2, var4.func_176223_P().func_177226_a(field_176367_b, var3.func_177229_b(field_176367_b)), 2);
      var1.func_175684_a(var2, var4, this.func_149738_a(var1));
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (this.field_149764_J == Material.field_151587_i) {
         if (var1.func_82736_K().func_82766_b("doFireTick")) {
            int var5 = var4.nextInt(3);
            if (var5 > 0) {
               BlockPos var6 = var2;

               for(int var7 = 0; var7 < var5; ++var7) {
                  var6 = var6.func_177982_a(var4.nextInt(3) - 1, 1, var4.nextInt(3) - 1);
                  Block var8 = var1.func_180495_p(var6).func_177230_c();
                  if (var8.field_149764_J == Material.field_151579_a) {
                     if (this.func_176369_e(var1, var6)) {
                        var1.func_175656_a(var6, Blocks.field_150480_ab.func_176223_P());
                        return;
                     }
                  } else if (var8.field_149764_J.func_76230_c()) {
                     return;
                  }
               }
            } else {
               for(int var9 = 0; var9 < 3; ++var9) {
                  BlockPos var10 = var2.func_177982_a(var4.nextInt(3) - 1, 0, var4.nextInt(3) - 1);
                  if (var1.func_175623_d(var10.func_177984_a()) && this.func_176368_m(var1, var10)) {
                     var1.func_175656_a(var10.func_177984_a(), Blocks.field_150480_ab.func_176223_P());
                  }
               }
            }

         }
      }
   }

   protected boolean func_176369_e(World var1, BlockPos var2) {
      EnumFacing[] var3 = EnumFacing.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EnumFacing var6 = var3[var5];
         if (this.func_176368_m(var1, var2.func_177972_a(var6))) {
            return true;
         }
      }

      return false;
   }

   private boolean func_176368_m(World var1, BlockPos var2) {
      return var1.func_180495_p(var2).func_177230_c().func_149688_o().func_76217_h();
   }
}
