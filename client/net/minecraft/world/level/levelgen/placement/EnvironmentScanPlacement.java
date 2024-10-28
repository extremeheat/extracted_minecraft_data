package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

public class EnvironmentScanPlacement extends PlacementModifier {
   private final Direction directionOfSearch;
   private final BlockPredicate targetCondition;
   private final BlockPredicate allowedSearchCondition;
   private final int maxSteps;
   public static final MapCodec<EnvironmentScanPlacement> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Direction.VERTICAL_CODEC.fieldOf("direction_of_search").forGetter((var0x) -> {
         return var0x.directionOfSearch;
      }), BlockPredicate.CODEC.fieldOf("target_condition").forGetter((var0x) -> {
         return var0x.targetCondition;
      }), BlockPredicate.CODEC.optionalFieldOf("allowed_search_condition", BlockPredicate.alwaysTrue()).forGetter((var0x) -> {
         return var0x.allowedSearchCondition;
      }), Codec.intRange(1, 32).fieldOf("max_steps").forGetter((var0x) -> {
         return var0x.maxSteps;
      })).apply(var0, EnvironmentScanPlacement::new);
   });

   private EnvironmentScanPlacement(Direction var1, BlockPredicate var2, BlockPredicate var3, int var4) {
      super();
      this.directionOfSearch = var1;
      this.targetCondition = var2;
      this.allowedSearchCondition = var3;
      this.maxSteps = var4;
   }

   public static EnvironmentScanPlacement scanningFor(Direction var0, BlockPredicate var1, BlockPredicate var2, int var3) {
      return new EnvironmentScanPlacement(var0, var1, var2, var3);
   }

   public static EnvironmentScanPlacement scanningFor(Direction var0, BlockPredicate var1, int var2) {
      return scanningFor(var0, var1, BlockPredicate.alwaysTrue(), var2);
   }

   public Stream<BlockPos> getPositions(PlacementContext var1, RandomSource var2, BlockPos var3) {
      BlockPos.MutableBlockPos var4 = var3.mutable();
      WorldGenLevel var5 = var1.getLevel();
      if (!this.allowedSearchCondition.test(var5, var4)) {
         return Stream.of();
      } else {
         int var6 = 0;

         while(true) {
            if (var6 < this.maxSteps) {
               if (this.targetCondition.test(var5, var4)) {
                  return Stream.of(var4);
               }

               var4.move(this.directionOfSearch);
               if (var5.isOutsideBuildHeight(var4.getY())) {
                  return Stream.of();
               }

               if (this.allowedSearchCondition.test(var5, var4)) {
                  ++var6;
                  continue;
               }
            }

            if (this.targetCondition.test(var5, var4)) {
               return Stream.of(var4);
            }

            return Stream.of();
         }
      }
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.ENVIRONMENT_SCAN;
   }
}
