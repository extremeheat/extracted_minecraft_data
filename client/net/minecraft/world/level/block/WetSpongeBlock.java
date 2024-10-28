package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WetSpongeBlock extends Block {
   public static final MapCodec<WetSpongeBlock> CODEC = simpleCodec(WetSpongeBlock::new);

   public MapCodec<WetSpongeBlock> codec() {
      return CODEC;
   }

   protected WetSpongeBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (var2.dimensionType().ultraWarm()) {
         var2.setBlock(var3, Blocks.SPONGE.defaultBlockState(), 3);
         var2.levelEvent(2009, var3, 0);
         var2.playSound((Player)null, (BlockPos)var3, SoundEvents.WET_SPONGE_DRIES, SoundSource.BLOCKS, 1.0F, (1.0F + var2.getRandom().nextFloat() * 0.2F) * 0.7F);
      }

   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      Direction var5 = Direction.getRandom(var4);
      if (var5 != Direction.UP) {
         BlockPos var6 = var3.relative(var5);
         BlockState var7 = var2.getBlockState(var6);
         if (!var1.canOcclude() || !var7.isFaceSturdy(var2, var6, var5.getOpposite())) {
            double var8 = (double)var3.getX();
            double var10 = (double)var3.getY();
            double var12 = (double)var3.getZ();
            if (var5 == Direction.DOWN) {
               var10 -= 0.05;
               var8 += var4.nextDouble();
               var12 += var4.nextDouble();
            } else {
               var10 += var4.nextDouble() * 0.8;
               if (var5.getAxis() == Direction.Axis.X) {
                  var12 += var4.nextDouble();
                  if (var5 == Direction.EAST) {
                     ++var8;
                  } else {
                     var8 += 0.05;
                  }
               } else {
                  var8 += var4.nextDouble();
                  if (var5 == Direction.SOUTH) {
                     ++var12;
                  } else {
                     var12 += 0.05;
                  }
               }
            }

            var2.addParticle(ParticleTypes.DRIPPING_WATER, var8, var10, var12, 0.0, 0.0, 0.0);
         }
      }
   }
}
