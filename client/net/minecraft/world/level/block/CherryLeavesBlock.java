package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class CherryLeavesBlock extends LeavesBlock {
   public static final MapCodec<CherryLeavesBlock> CODEC = simpleCodec(CherryLeavesBlock::new);

   public MapCodec<CherryLeavesBlock> codec() {
      return CODEC;
   }

   public CherryLeavesBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      super.animateTick(var1, var2, var3, var4);
      if (var4.nextInt(10) == 0) {
         BlockPos var5 = var3.below();
         BlockState var6 = var2.getBlockState(var5);
         if (!isFaceFull(var6.getCollisionShape(var2, var5), Direction.UP)) {
            ParticleUtils.spawnParticleBelow(var2, var3, var4, ParticleTypes.CHERRY_LEAVES);
         }
      }
   }
}
