package net.minecraft.world.level.levelgen.surfacebuilders;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class DefaultSurfaceBuilder extends SurfaceBuilder {
   public DefaultSurfaceBuilder(Function var1) {
      super(var1);
   }

   public void apply(Random var1, ChunkAccess var2, Biome var3, int var4, int var5, int var6, double var7, BlockState var9, BlockState var10, int var11, long var12, SurfaceBuilderBaseConfiguration var14) {
      this.apply(var1, var2, var3, var4, var5, var6, var7, var9, var10, var14.getTopMaterial(), var14.getUnderMaterial(), var14.getUnderwaterMaterial(), var11);
   }

   protected void apply(Random var1, ChunkAccess var2, Biome var3, int var4, int var5, int var6, double var7, BlockState var9, BlockState var10, BlockState var11, BlockState var12, BlockState var13, int var14) {
      BlockState var15 = var11;
      BlockState var16 = var12;
      BlockPos.MutableBlockPos var17 = new BlockPos.MutableBlockPos();
      int var18 = -1;
      int var19 = (int)(var7 / 3.0D + 3.0D + var1.nextDouble() * 0.25D);
      int var20 = var4 & 15;
      int var21 = var5 & 15;

      for(int var22 = var6; var22 >= 0; --var22) {
         var17.set(var20, var22, var21);
         BlockState var23 = var2.getBlockState(var17);
         if (var23.isAir()) {
            var18 = -1;
         } else if (var23.getBlock() == var9.getBlock()) {
            if (var18 == -1) {
               if (var19 <= 0) {
                  var15 = Blocks.AIR.defaultBlockState();
                  var16 = var9;
               } else if (var22 >= var14 - 4 && var22 <= var14 + 1) {
                  var15 = var11;
                  var16 = var12;
               }

               if (var22 < var14 && (var15 == null || var15.isAir())) {
                  if (var3.getTemperature(var17.set(var4, var22, var5)) < 0.15F) {
                     var15 = Blocks.ICE.defaultBlockState();
                  } else {
                     var15 = var10;
                  }

                  var17.set(var20, var22, var21);
               }

               var18 = var19;
               if (var22 >= var14 - 1) {
                  var2.setBlockState(var17, var15, false);
               } else if (var22 < var14 - 7 - var19) {
                  var15 = Blocks.AIR.defaultBlockState();
                  var16 = var9;
                  var2.setBlockState(var17, var13, false);
               } else {
                  var2.setBlockState(var17, var16, false);
               }
            } else if (var18 > 0) {
               --var18;
               var2.setBlockState(var17, var16, false);
               if (var18 == 0 && var16.getBlock() == Blocks.SAND && var19 > 1) {
                  var18 = var1.nextInt(4) + Math.max(0, var22 - 63);
                  var16 = var16.getBlock() == Blocks.RED_SAND ? Blocks.RED_SANDSTONE.defaultBlockState() : Blocks.SANDSTONE.defaultBlockState();
               }
            }
         }
      }

   }
}
