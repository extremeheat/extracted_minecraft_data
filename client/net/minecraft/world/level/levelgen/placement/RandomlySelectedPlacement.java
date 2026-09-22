package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;

public record RandomlySelectedPlacement(List<PlacementModifier> placements) implements PlacementModifier {
   public static final MapCodec<RandomlySelectedPlacement> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ExtraCodecs.nonEmptyList(PlacementModifier.CODEC.listOf()).fieldOf("placements").forGetter(RandomlySelectedPlacement::placements)).apply(i, RandomlySelectedPlacement::new));

   public RandomlySelectedPlacement(final PlacementModifier... placements) {
      this(List.of(placements));
   }

   public RandomlySelectedPlacement {
      super();
   }

   public void modify(final PlacementContext context, final RandomSource random, final BlockPos origin, final Consumer<BlockPos> output) {
      ((PlacementModifier)Util.getRandom(this.placements, random)).modify(context, random, origin, output);
   }

   public InclusiveRange<Integer> modifyXzDomain(final InclusiveRange<Integer> inputDomain) {
      int minInclusive = 2147483647;
      int maxInclusive = -2147483648;

      for(PlacementModifier placement : this.placements) {
         InclusiveRange<Integer> placementDomain = placement.modifyXzDomain(inputDomain);
         minInclusive = Math.min((Integer)placementDomain.minInclusive(), minInclusive);
         maxInclusive = Math.max((Integer)placementDomain.maxInclusive(), maxInclusive);
      }

      return new InclusiveRange<Integer>(minInclusive, maxInclusive);
   }

   public MapCodec<RandomlySelectedPlacement> codec() {
      return CODEC;
   }
}
