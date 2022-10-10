package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class CoralMushroomFeature extends CoralFeature {
   public CoralMushroomFeature() {
      super();
   }

   protected boolean func_204623_a(IWorld var1, Random var2, BlockPos var3, IBlockState var4) {
      int var5 = var2.nextInt(3) + 3;
      int var6 = var2.nextInt(3) + 3;
      int var7 = var2.nextInt(3) + 3;
      int var8 = var2.nextInt(3) + 1;
      BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos(var3);

      for(int var10 = 0; var10 <= var6; ++var10) {
         for(int var11 = 0; var11 <= var5; ++var11) {
            for(int var12 = 0; var12 <= var7; ++var12) {
               var9.func_181079_c(var10 + var3.func_177958_n(), var11 + var3.func_177956_o(), var12 + var3.func_177952_p());
               var9.func_189534_c(EnumFacing.DOWN, var8);
               if ((var10 != 0 && var10 != var6 || var11 != 0 && var11 != var5) && (var12 != 0 && var12 != var7 || var11 != 0 && var11 != var5) && (var10 != 0 && var10 != var6 || var12 != 0 && var12 != var7) && (var10 == 0 || var10 == var6 || var11 == 0 || var11 == var5 || var12 == 0 || var12 == var7) && var2.nextFloat() >= 0.1F && !this.func_204624_b(var1, var2, var9, var4)) {
               }
            }
         }
      }

      return true;
   }
}
