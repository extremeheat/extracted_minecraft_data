package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;

class ReplaceablePredicate extends StateTestingPredicate {
   public static final MapCodec<ReplaceablePredicate> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return stateTestingCodec(var0).apply(var0, ReplaceablePredicate::new);
   });

   public ReplaceablePredicate(Vec3i var1) {
      super(var1);
   }

   protected boolean test(BlockState var1) {
      return var1.canBeReplaced();
   }

   public BlockPredicateType<?> type() {
      return BlockPredicateType.REPLACEABLE;
   }
}
