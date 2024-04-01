package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;

public class CloudPlacement extends PlacementModifier {
   public static final Codec<CloudPlacement> CODEC = Codec.unit(CloudPlacement::new);

   public CloudPlacement() {
      super();
   }

   @Override
   public Stream<BlockPos> getPositions(PlacementContext var1, RandomSource var2, BlockPos var3) {
      int var4 = var1.getHeight(Heightmap.Types.MOTION_BLOCKING, var3.getX(), var3.getZ());
      return var4 != var1.getMinBuildHeight() ? Stream.empty() : Stream.of(var3.above(var2.nextIntBetweenInclusive(105, 115)));
   }

   @Override
   public PlacementModifierType<?> type() {
      return PlacementModifierType.CLOUD;
   }
}
