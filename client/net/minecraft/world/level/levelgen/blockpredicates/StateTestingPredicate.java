package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

public abstract class StateTestingPredicate implements BlockPredicate {
   protected final Vec3i offset;

   protected static <P extends StateTestingPredicate> Products.P1<RecordCodecBuilder.Mu<P>, Vec3i> stateTestingCodec(RecordCodecBuilder.Instance<P> var0) {
      return var0.group(Vec3i.offsetCodec(16).optionalFieldOf("offset", Vec3i.ZERO).forGetter((var0x) -> {
         return var0x.offset;
      }));
   }

   protected StateTestingPredicate(Vec3i var1) {
      super();
      this.offset = var1;
   }

   public final boolean test(WorldGenLevel var1, BlockPos var2) {
      return this.test(var1.getBlockState(var2.offset(this.offset)));
   }

   protected abstract boolean test(BlockState var1);

   // $FF: synthetic method
   public boolean test(Object var1, Object var2) {
      return this.test((WorldGenLevel)var1, (BlockPos)var2);
   }
}
