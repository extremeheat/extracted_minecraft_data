package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;

public record BelowHeightmapPredicate(Heightmap.Types heightmap) implements BlockPredicate {
   public static final MapCodec<BelowHeightmapPredicate> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Heightmap.Types.CODEC.fieldOf("heightmap").forGetter(BelowHeightmapPredicate::heightmap)).apply(i, BelowHeightmapPredicate::new));

   public BelowHeightmapPredicate {
      super();
   }

   public boolean test(final LevelAccessor level, final BlockPos origin) {
      return origin.getY() < level.getHeight(this.heightmap, origin.getX(), origin.getZ());
   }

   public BlockPredicateType<?> type() {
      return BlockPredicateType.BELOW_HEIGHTMAP;
   }
}
