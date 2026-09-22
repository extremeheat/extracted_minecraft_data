package net.minecraft.world.level.levelgen.placement;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;

public record PlacementContext(VerticalAnchor.Context verticalAnchorContext, WorldGenLevel level, ChunkGenerator generator, Optional<PlacedFeature> topFeature) {
   public PlacementContext(final WorldGenLevel level, final ChunkGenerator generator, final Optional<PlacedFeature> topFeature) {
      this(VerticalAnchor.Context.from(generator, level), level, generator, topFeature);
   }

   public PlacementContext {
      super();
   }

   public int getHeight(final Heightmap.Types type, final int x, final int z) {
      return this.level.getHeight(type, x, z);
   }

   public BlockState getBlockState(final BlockPos pos) {
      return this.level.getBlockState(pos);
   }

   public int getMinY() {
      return this.level.getMinY();
   }
}
