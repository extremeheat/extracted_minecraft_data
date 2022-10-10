package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class BigRedMushroomFeature extends Feature<NoFeatureConfig> {
   public BigRedMushroomFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoFeatureConfig var5) {
      int var6 = var3.nextInt(3) + 4;
      if (var3.nextInt(12) == 0) {
         var6 *= 2;
      }

      int var7 = var4.func_177956_o();
      if (var7 >= 1 && var7 + var6 + 1 < 256) {
         Block var8 = var1.func_180495_p(var4.func_177977_b()).func_177230_c();
         if (!Block.func_196245_f(var8) && var8 != Blocks.field_196658_i && var8 != Blocks.field_150391_bh) {
            return false;
         } else {
            BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();

            int var12;
            for(int var10 = 0; var10 <= var6; ++var10) {
               byte var11 = 0;
               if (var10 < var6 && var10 >= var6 - 3) {
                  var11 = 2;
               } else if (var10 == var6) {
                  var11 = 1;
               }

               for(var12 = -var11; var12 <= var11; ++var12) {
                  for(int var13 = -var11; var13 <= var11; ++var13) {
                     IBlockState var14 = var1.func_180495_p(var9.func_189533_g(var4).func_196234_d(var12, var10, var13));
                     if (!var14.func_196958_f() && !var14.func_203425_a(BlockTags.field_206952_E)) {
                        return false;
                     }
                  }
               }
            }

            IBlockState var22 = (IBlockState)Blocks.field_150419_aX.func_176223_P().func_206870_a(BlockHugeMushroom.field_196460_A, false);

            for(int var23 = var6 - 3; var23 <= var6; ++var23) {
               var12 = var23 < var6 ? 2 : 1;
               boolean var25 = false;

               for(int var26 = -var12; var26 <= var12; ++var26) {
                  for(int var15 = -var12; var15 <= var12; ++var15) {
                     boolean var16 = var26 == -var12;
                     boolean var17 = var26 == var12;
                     boolean var18 = var15 == -var12;
                     boolean var19 = var15 == var12;
                     boolean var20 = var16 || var17;
                     boolean var21 = var18 || var19;
                     if (var23 >= var6 || var20 != var21) {
                        var9.func_189533_g(var4).func_196234_d(var26, var23, var15);
                        if (!var1.func_180495_p(var9).func_200015_d(var1, var9)) {
                           this.func_202278_a(var1, var9, (IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)var22.func_206870_a(BlockHugeMushroom.field_196465_z, var23 >= var6 - 1)).func_206870_a(BlockHugeMushroom.field_196464_y, var26 < 0)).func_206870_a(BlockHugeMushroom.field_196461_b, var26 > 0)).func_206870_a(BlockHugeMushroom.field_196459_a, var15 < 0)).func_206870_a(BlockHugeMushroom.field_196463_c, var15 > 0));
                        }
                     }
                  }
               }
            }

            IBlockState var24 = (IBlockState)((IBlockState)Blocks.field_196706_do.func_176223_P().func_206870_a(BlockHugeMushroom.field_196465_z, false)).func_206870_a(BlockHugeMushroom.field_196460_A, false);

            for(var12 = 0; var12 < var6; ++var12) {
               var9.func_189533_g(var4).func_189534_c(EnumFacing.UP, var12);
               if (!var1.func_180495_p(var9).func_200015_d(var1, var9)) {
                  this.func_202278_a(var1, var9, var24);
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }
}
