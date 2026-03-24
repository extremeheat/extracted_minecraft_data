package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.references.BlockIds;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class MyceliumBlock extends SpreadingSnowyBlock {
   public static final MapCodec<MyceliumBlock> CODEC = simpleCodec(MyceliumBlock::new);

   public MapCodec<MyceliumBlock> codec() {
      return CODEC;
   }

   public MyceliumBlock(final BlockBehaviour.Properties properties) {
      super(properties, BlockIds.DIRT);
   }

   public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
      super.animateTick(state, level, pos, random);
      if (random.nextInt(10) == 0) {
         level.addParticle(ParticleTypes.MYCELIUM, (double)pos.getX() + random.nextDouble(), (double)pos.getY() + 1.1, (double)pos.getZ() + random.nextDouble(), 0.0, 0.0, 0.0);
      }

   }
}
