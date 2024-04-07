package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class CryingObsidianBlock extends Block {
   public static final MapCodec<CryingObsidianBlock> CODEC = simpleCodec(CryingObsidianBlock::new);

   @Override
   public MapCodec<CryingObsidianBlock> codec() {
      return CODEC;
   }

   public CryingObsidianBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Override
   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (var4.nextInt(5) == 0) {
         Direction var5 = Direction.getRandom(var4);
         if (var5 != Direction.UP) {
            BlockPos var6 = var3.relative(var5);
            BlockState var7 = var2.getBlockState(var6);
            if (!var1.canOcclude() || !var7.isFaceSturdy(var2, var6, var5.getOpposite())) {
               double var8 = var5.getStepX() == 0 ? var4.nextDouble() : 0.5 + (double)var5.getStepX() * 0.6;
               double var10 = var5.getStepY() == 0 ? var4.nextDouble() : 0.5 + (double)var5.getStepY() * 0.6;
               double var12 = var5.getStepZ() == 0 ? var4.nextDouble() : 0.5 + (double)var5.getStepZ() * 0.6;
               var2.addParticle(
                  ParticleTypes.DRIPPING_OBSIDIAN_TEAR, (double)var3.getX() + var8, (double)var3.getY() + var10, (double)var3.getZ() + var12, 0.0, 0.0, 0.0
               );
            }
         }
      }
   }
}
