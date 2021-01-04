package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class WetSpongeBlock extends Block {
   protected WetSpongeBlock(Block.Properties var1) {
      super(var1);
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      Direction var5 = Direction.getRandomFace(var4);
      if (var5 != Direction.UP) {
         BlockPos var6 = var3.relative(var5);
         BlockState var7 = var2.getBlockState(var6);
         if (!var1.canOcclude() || !var7.isFaceSturdy(var2, var6, var5.getOpposite())) {
            double var8 = (double)var3.getX();
            double var10 = (double)var3.getY();
            double var12 = (double)var3.getZ();
            if (var5 == Direction.DOWN) {
               var10 -= 0.05D;
               var8 += var4.nextDouble();
               var12 += var4.nextDouble();
            } else {
               var10 += var4.nextDouble() * 0.8D;
               if (var5.getAxis() == Direction.Axis.X) {
                  var12 += var4.nextDouble();
                  if (var5 == Direction.EAST) {
                     ++var8;
                  } else {
                     var8 += 0.05D;
                  }
               } else {
                  var8 += var4.nextDouble();
                  if (var5 == Direction.SOUTH) {
                     ++var12;
                  } else {
                     var12 += 0.05D;
                  }
               }
            }

            var2.addParticle(ParticleTypes.DRIPPING_WATER, var8, var10, var12, 0.0D, 0.0D, 0.0D);
         }
      }
   }
}
