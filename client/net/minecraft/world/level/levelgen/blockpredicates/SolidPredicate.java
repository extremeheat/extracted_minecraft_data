package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;

@Deprecated
public class SolidPredicate extends StateTestingPredicate {
   public static final Codec<SolidPredicate> CODEC = RecordCodecBuilder.create(var0 -> stateTestingCodec(var0).apply(var0, SolidPredicate::new));

   public SolidPredicate(Vec3i var1) {
      super(var1);
   }

   @Override
   protected boolean test(BlockState var1) {
      return var1.isSolid();
   }

   @Override
   public BlockPredicateType<?> type() {
      return BlockPredicateType.SOLID;
   }
}
