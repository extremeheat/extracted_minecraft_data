package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenTallGrass extends WorldGenerator {
   private final IBlockState field_175907_a;

   public WorldGenTallGrass(BlockTallGrass.EnumType var1) {
      super();
      this.field_175907_a = Blocks.field_150329_H.func_176223_P().func_177226_a(BlockTallGrass.field_176497_a, var1);
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      Block var4;
      while(((var4 = var1.func_180495_p(var3).func_177230_c()).func_149688_o() == Material.field_151579_a || var4.func_149688_o() == Material.field_151584_j) && var3.func_177956_o() > 0) {
         var3 = var3.func_177977_b();
      }

      for(int var5 = 0; var5 < 128; ++var5) {
         BlockPos var6 = var3.func_177982_a(var2.nextInt(8) - var2.nextInt(8), var2.nextInt(4) - var2.nextInt(4), var2.nextInt(8) - var2.nextInt(8));
         if (var1.func_175623_d(var6) && Blocks.field_150329_H.func_180671_f(var1, var6, this.field_175907_a)) {
            var1.func_180501_a(var6, this.field_175907_a, 2);
         }
      }

      return true;
   }
}
