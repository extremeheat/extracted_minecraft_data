package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public abstract class StateTestingPredicate implements BlockPredicate {
   protected final Vec3i offset;

   protected static <P extends StateTestingPredicate> Products.P1<RecordCodecBuilder.Mu<P>, Vec3i> stateTestingCodec(final RecordCodecBuilder.Instance<P> instance) {
      return instance.group(Vec3i.offsetCodec(16).optionalFieldOf("offset", Vec3i.ZERO).forGetter((c) -> c.offset));
   }

   protected StateTestingPredicate(final Vec3i offset) {
      super();
      this.offset = offset;
   }

   public final boolean test(final LevelAccessor level, final BlockPos origin) {
      return this.test(level.getBlockState(origin.offset(this.offset)));
   }

   protected abstract boolean test(final BlockState state);
}
