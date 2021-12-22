package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;

public class SolidPredicate extends StateTestingPredicate {
   public static final Codec<SolidPredicate> CODEC = RecordCodecBuilder.create((var0) -> {
      return stateTestingCodec(var0).apply(var0, SolidPredicate::new);
   });

   public SolidPredicate(Vec3i var1) {
      super(var1);
   }

   protected boolean test(BlockState var1) {
      return var1.getMaterial().isSolid();
   }

   public BlockPredicateType<?> type() {
      return BlockPredicateType.SOLID;
   }
}
