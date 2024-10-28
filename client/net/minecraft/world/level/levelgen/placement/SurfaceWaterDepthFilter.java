package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;

public class SurfaceWaterDepthFilter extends PlacementFilter {
   public static final MapCodec<SurfaceWaterDepthFilter> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.INT.fieldOf("max_water_depth").forGetter((var0x) -> {
         return var0x.maxWaterDepth;
      })).apply(var0, SurfaceWaterDepthFilter::new);
   });
   private final int maxWaterDepth;

   private SurfaceWaterDepthFilter(int var1) {
      super();
      this.maxWaterDepth = var1;
   }

   public static SurfaceWaterDepthFilter forMaxDepth(int var0) {
      return new SurfaceWaterDepthFilter(var0);
   }

   protected boolean shouldPlace(PlacementContext var1, RandomSource var2, BlockPos var3) {
      int var4 = var1.getHeight(Heightmap.Types.OCEAN_FLOOR, var3.getX(), var3.getZ());
      int var5 = var1.getHeight(Heightmap.Types.WORLD_SURFACE, var3.getX(), var3.getZ());
      return var5 - var4 <= this.maxWaterDepth;
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.SURFACE_WATER_DEPTH_FILTER;
   }
}
