package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;

public class SurfaceRelativeThresholdFilter extends PlacementFilter {
   public static final MapCodec<SurfaceRelativeThresholdFilter> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Heightmap.Types.CODEC.fieldOf("heightmap").forGetter((c) -> c.heightmap), Codec.INT.optionalFieldOf("min_inclusive", -2147483648).forGetter((c) -> c.minInclusive), Codec.INT.optionalFieldOf("max_inclusive", 2147483647).forGetter((c) -> c.maxInclusive)).apply(i, SurfaceRelativeThresholdFilter::new));
   private final Heightmap.Types heightmap;
   private final int minInclusive;
   private final int maxInclusive;

   private SurfaceRelativeThresholdFilter(final Heightmap.Types heightmap, final int minInclusive, final int maxInclusive) {
      super();
      this.heightmap = heightmap;
      this.minInclusive = minInclusive;
      this.maxInclusive = maxInclusive;
   }

   public static SurfaceRelativeThresholdFilter of(final Heightmap.Types heightmap, final int minInclusive, final int maxInclusive) {
      return new SurfaceRelativeThresholdFilter(heightmap, minInclusive, maxInclusive);
   }

   protected boolean shouldPlace(final PlacementContext context, final RandomSource random, final BlockPos origin) {
      long surfaceY = (long)context.getHeight(this.heightmap, origin.getX(), origin.getZ());
      long minY = surfaceY + (long)this.minInclusive;
      long maxY = surfaceY + (long)this.maxInclusive;
      return minY <= (long)origin.getY() && (long)origin.getY() <= maxY;
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.SURFACE_RELATIVE_THRESHOLD_FILTER;
   }
}
