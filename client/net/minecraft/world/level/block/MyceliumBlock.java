package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class MyceliumBlock extends SpreadingSnowyDirtBlock {
   public MyceliumBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      super.animateTick(var1, var2, var3, var4);
      if (var4.nextInt(10) == 0) {
         var2.addParticle(ParticleTypes.MYCELIUM, (double)var3.getX() + var4.nextDouble(), (double)var3.getY() + 1.1D, (double)var3.getZ() + var4.nextDouble(), 0.0D, 0.0D, 0.0D);
      }

   }
}
