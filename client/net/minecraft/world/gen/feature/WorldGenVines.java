package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class WorldGenVines extends WorldGenerator {
   public WorldGenVines() {
      super();
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      for(; var3.func_177956_o() < 128; var3 = var3.func_177984_a()) {
         if (var1.func_175623_d(var3)) {
            EnumFacing[] var4 = EnumFacing.Plane.HORIZONTAL.func_179516_a();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               EnumFacing var7 = var4[var6];
               if (Blocks.field_150395_bd.func_176198_a(var1, var3, var7)) {
                  IBlockState var8 = Blocks.field_150395_bd.func_176223_P().func_177226_a(BlockVine.field_176273_b, var7 == EnumFacing.NORTH).func_177226_a(BlockVine.field_176278_M, var7 == EnumFacing.EAST).func_177226_a(BlockVine.field_176279_N, var7 == EnumFacing.SOUTH).func_177226_a(BlockVine.field_176280_O, var7 == EnumFacing.WEST);
                  var1.func_180501_a(var3, var8, 2);
                  break;
               }
            }
         } else {
            var3 = var3.func_177982_a(var2.nextInt(4) - var2.nextInt(4), 0, var2.nextInt(4) - var2.nextInt(4));
         }
      }

      return true;
   }
}
