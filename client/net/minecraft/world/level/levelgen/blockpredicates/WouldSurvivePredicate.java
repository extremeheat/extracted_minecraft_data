package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

public class WouldSurvivePredicate implements BlockPredicate {
   public static final Codec<WouldSurvivePredicate> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               Vec3i.offsetCodec(16).optionalFieldOf("offset", Vec3i.ZERO).forGetter(var0x -> var0x.offset),
               BlockState.CODEC.fieldOf("state").forGetter(var0x -> var0x.state)
            )
            .apply(var0, WouldSurvivePredicate::new)
   );
   private final Vec3i offset;
   private final BlockState state;

   protected WouldSurvivePredicate(Vec3i var1, BlockState var2) {
      super();
      this.offset = var1;
      this.state = var2;
   }

   public boolean test(WorldGenLevel var1, BlockPos var2) {
      return this.state.canSurvive(var1, var2.offset(this.offset));
   }

   @Override
   public BlockPredicateType<?> type() {
      return BlockPredicateType.WOULD_SURVIVE;
   }
}
