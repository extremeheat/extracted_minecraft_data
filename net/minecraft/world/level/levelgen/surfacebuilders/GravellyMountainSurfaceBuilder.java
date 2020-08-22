package net.minecraft.world.level.levelgen.surfacebuilders;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class GravellyMountainSurfaceBuilder extends SurfaceBuilder {
   public GravellyMountainSurfaceBuilder(Function var1) {
      super(var1);
   }

   public void apply(Random var1, ChunkAccess var2, Biome var3, int var4, int var5, int var6, double var7, BlockState var9, BlockState var10, int var11, long var12, SurfaceBuilderBaseConfiguration var14) {
      if (var7 >= -1.0D && var7 <= 2.0D) {
         if (var7 > 1.0D) {
            SurfaceBuilder.DEFAULT.apply(var1, var2, var3, var4, var5, var6, var7, var9, var10, var11, var12, SurfaceBuilder.CONFIG_STONE);
         } else {
            SurfaceBuilder.DEFAULT.apply(var1, var2, var3, var4, var5, var6, var7, var9, var10, var11, var12, SurfaceBuilder.CONFIG_GRASS);
         }
      } else {
         SurfaceBuilder.DEFAULT.apply(var1, var2, var3, var4, var5, var6, var7, var9, var10, var11, var12, SurfaceBuilder.CONFIG_GRAVEL);
      }

   }
}
