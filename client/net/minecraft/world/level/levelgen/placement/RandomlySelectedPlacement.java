package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
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

   public MapCodec<RandomlySelectedPlacement> codec() {
      return CODEC;
   }
}
