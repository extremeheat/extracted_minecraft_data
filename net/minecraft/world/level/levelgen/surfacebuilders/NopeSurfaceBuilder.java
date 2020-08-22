package net.minecraft.world.level.levelgen.surfacebuilders;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class NopeSurfaceBuilder extends SurfaceBuilder {
   public NopeSurfaceBuilder(Function var1) {
      super(var1);
   }

   public void apply(Random var1, ChunkAccess var2, Biome var3, int var4, int var5, int var6, double var7, BlockState var9, BlockState var10, int var11, long var12, SurfaceBuilderBaseConfiguration var14) {
   }
}
