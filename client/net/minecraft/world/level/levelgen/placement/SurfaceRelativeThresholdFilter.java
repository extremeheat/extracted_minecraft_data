package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;

public class SurfaceRelativeThresholdFilter extends PlacementFilter {
   public static final MapCodec<SurfaceRelativeThresholdFilter> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Heightmap.Types.CODEC.fieldOf("heightmap").forGetter((var0x) -> {
         return var0x.heightmap;
      }), Codec.INT.optionalFieldOf("min_inclusive", -2147483648).forGetter((var0x) -> {
         return var0x.minInclusive;
      }), Codec.INT.optionalFieldOf("max_inclusive", 2147483647).forGetter((var0x) -> {
         return var0x.maxInclusive;
      })).apply(var0, SurfaceRelativeThresholdFilter::new);
   });
   private final Heightmap.Types heightmap;
   private final int minInclusive;
   private final int maxInclusive;

   private SurfaceRelativeThresholdFilter(Heightmap.Types var1, int var2, int var3) {
      super();
      this.heightmap = var1;
      this.minInclusive = var2;
      this.maxInclusive = var3;
   }

   public static SurfaceRelativeThresholdFilter of(Heightmap.Types var0, int var1, int var2) {
      return new SurfaceRelativeThresholdFilter(var0, var1, var2);
   }

   protected boolean shouldPlace(PlacementContext var1, RandomSource var2, BlockPos var3) {
      long var4 = (long)var1.getHeight(this.heightmap, var3.getX(), var3.getZ());
      long var6 = var4 + (long)this.minInclusive;
      long var8 = var4 + (long)this.maxInclusive;
      return var6 <= (long)var3.getY() && (long)var3.getY() <= var8;
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.SURFACE_RELATIVE_THRESHOLD_FILTER;
   }
}
