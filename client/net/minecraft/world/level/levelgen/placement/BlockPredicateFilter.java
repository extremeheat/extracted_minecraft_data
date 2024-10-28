package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

public class BlockPredicateFilter extends PlacementFilter {
   public static final MapCodec<BlockPredicateFilter> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(BlockPredicate.CODEC.fieldOf("predicate").forGetter((var0x) -> {
         return var0x.predicate;
      })).apply(var0, BlockPredicateFilter::new);
   });
   private final BlockPredicate predicate;

   private BlockPredicateFilter(BlockPredicate var1) {
      super();
      this.predicate = var1;
   }

   public static BlockPredicateFilter forPredicate(BlockPredicate var0) {
      return new BlockPredicateFilter(var0);
   }

   protected boolean shouldPlace(PlacementContext var1, RandomSource var2, BlockPos var3) {
      return this.predicate.test(var1.getLevel(), var3);
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.BLOCK_PREDICATE_FILTER;
   }
}
