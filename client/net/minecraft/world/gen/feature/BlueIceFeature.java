package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class BlueIceFeature extends Feature<NoFeatureConfig> {
   public BlueIceFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoFeatureConfig var5) {
      if (var4.func_177956_o() > var1.func_181545_F() - 1) {
         return false;
      } else if (var1.func_180495_p(var4).func_177230_c() != Blocks.field_150355_j && var1.func_180495_p(var4.func_177977_b()).func_177230_c() != Blocks.field_150355_j) {
         return false;
      } else {
         boolean var6 = false;
         EnumFacing[] var7 = EnumFacing.values();
         int var8 = var7.length;

         int var9;
         for(var9 = 0; var9 < var8; ++var9) {
            EnumFacing var10 = var7[var9];
            if (var10 != EnumFacing.DOWN && var1.func_180495_p(var4.func_177972_a(var10)).func_177230_c() == Blocks.field_150403_cj) {
               var6 = true;
               break;
            }
         }

         if (!var6) {
            return false;
         } else {
            var1.func_180501_a(var4, Blocks.field_205164_gk.func_176223_P(), 2);

            for(int var18 = 0; var18 < 200; ++var18) {
               var8 = var3.nextInt(5) - var3.nextInt(6);
               var9 = 3;
               if (var8 < 2) {
                  var9 += var8 / 2;
               }

               if (var9 >= 1) {
                  BlockPos var19 = var4.func_177982_a(var3.nextInt(var9) - var3.nextInt(var9), var8, var3.nextInt(var9) - var3.nextInt(var9));
                  IBlockState var11 = var1.func_180495_p(var19);
                  Block var12 = var11.func_177230_c();
                  if (var11.func_185904_a() == Material.field_151579_a || var12 == Blocks.field_150355_j || var12 == Blocks.field_150403_cj || var12 == Blocks.field_150432_aD) {
                     EnumFacing[] var13 = EnumFacing.values();
                     int var14 = var13.length;

                     for(int var15 = 0; var15 < var14; ++var15) {
                        EnumFacing var16 = var13[var15];
                        Block var17 = var1.func_180495_p(var19.func_177972_a(var16)).func_177230_c();
                        if (var17 == Blocks.field_205164_gk) {
                           var1.func_180501_a(var19, Blocks.field_205164_gk.func_176223_P(), 2);
                           break;
                        }
                     }
                  }
               }
            }

            return true;
         }
      }
   }
}
