package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class LiquidsFeature extends Feature<LiquidsConfig> {
   public LiquidsFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, LiquidsConfig var5) {
      if (!Block.func_196252_e(var1.func_180495_p(var4.func_177984_a()).func_177230_c())) {
         return false;
      } else if (!Block.func_196252_e(var1.func_180495_p(var4.func_177977_b()).func_177230_c())) {
         return false;
      } else {
         IBlockState var6 = var1.func_180495_p(var4);
         if (!var6.func_196958_f() && !Block.func_196252_e(var6.func_177230_c())) {
            return false;
         } else {
            int var7 = 0;
            int var8 = 0;
            if (Block.func_196252_e(var1.func_180495_p(var4.func_177976_e()).func_177230_c())) {
               ++var8;
            }

            if (Block.func_196252_e(var1.func_180495_p(var4.func_177974_f()).func_177230_c())) {
               ++var8;
            }

            if (Block.func_196252_e(var1.func_180495_p(var4.func_177978_c()).func_177230_c())) {
               ++var8;
            }

            if (Block.func_196252_e(var1.func_180495_p(var4.func_177968_d()).func_177230_c())) {
               ++var8;
            }

            int var9 = 0;
            if (var1.func_175623_d(var4.func_177976_e())) {
               ++var9;
            }

            if (var1.func_175623_d(var4.func_177974_f())) {
               ++var9;
            }

            if (var1.func_175623_d(var4.func_177978_c())) {
               ++var9;
            }

            if (var1.func_175623_d(var4.func_177968_d())) {
               ++var9;
            }

            if (var8 == 3 && var9 == 1) {
               var1.func_180501_a(var4, var5.field_202459_a.func_207188_f().func_206883_i(), 2);
               var1.func_205219_F_().func_205360_a(var4, var5.field_202459_a, 0);
               ++var7;
            }

            return var7 > 0;
         }
      }
   }
}
