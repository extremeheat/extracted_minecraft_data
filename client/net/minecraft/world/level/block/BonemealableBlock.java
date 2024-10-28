package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public interface BonemealableBlock {
   boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3);

   boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4);

   void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4);

   default BlockPos getParticlePos(BlockPos var1) {
      BlockPos var10000;
      switch (this.getType().ordinal()) {
         case 0 -> var10000 = var1.above();
         case 1 -> var10000 = var1;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   default Type getType() {
      return BonemealableBlock.Type.GROWER;
   }

   public static enum Type {
      NEIGHBOR_SPREADER,
      GROWER;

      private Type() {
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{NEIGHBOR_SPREADER, GROWER};
      }
   }
}
