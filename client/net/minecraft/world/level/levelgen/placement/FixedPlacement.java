package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.RandomSource;

public record FixedPlacement(List<BlockPos> positions) implements PlacementModifier {
   public static final MapCodec<FixedPlacement> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ExtraCodecs.nonEmptyList(BlockPos.CODEC.listOf()).fieldOf("positions").forGetter((c) -> c.positions)).apply(i, FixedPlacement::new));

   public FixedPlacement {
      super();
   }

   public static FixedPlacement of(final BlockPos... pos) {
      return new FixedPlacement(List.of(pos));
   }

   public void modify(final PlacementContext context, final RandomSource random, final BlockPos origin, final Consumer<BlockPos> output) {
      int chunkX = SectionPos.blockToSectionCoord(origin.getX());
      int chunkZ = SectionPos.blockToSectionCoord(origin.getZ());

      for(BlockPos position : this.positions) {
         if (isSameChunk(chunkX, chunkZ, position)) {
            output.accept(position);
         }
      }

   }

   public InclusiveRange<Integer> modifyXzDomain(final InclusiveRange<Integer> inputDomain) {
      int minInputSection = SectionPos.sectionToBlockCoord(SectionPos.blockToSectionCoord((Integer)inputDomain.minInclusive()));
      int maxInputSection = SectionPos.sectionToBlockCoord(SectionPos.blockToSectionCoord((Integer)inputDomain.maxInclusive()));
      int minInclusive = 2147483647;
      int maxInclusive = -2147483648;

      for(BlockPos position : this.positions) {
         int x = SectionPos.sectionRelative(position.getX());
         int z = SectionPos.sectionRelative(position.getZ());
         minInclusive = Math.min(minInputSection + Math.min(x, z), minInclusive);
         maxInclusive = Math.max(maxInputSection + Math.max(x, z), maxInclusive);
      }

      return new InclusiveRange<Integer>(minInclusive, maxInclusive);
   }

   private static boolean isSameChunk(final int chunkX, final int chunkZ, final BlockPos position) {
      return chunkX == SectionPos.blockToSectionCoord(position.getX()) && chunkZ == SectionPos.blockToSectionCoord(position.getZ());
   }

   public MapCodec<FixedPlacement> codec() {
      return CODEC;
   }
}
