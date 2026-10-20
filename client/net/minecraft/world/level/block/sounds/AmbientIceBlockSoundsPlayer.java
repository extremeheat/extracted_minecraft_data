package net.minecraft.world.level.block.sounds;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class AmbientIceBlockSoundsPlayer {
   private static final int IDLE_SOUND_CHANCE = 10000;
   private static final int SURROUNDING_BLOCKS_DISTANCE_HORIZONTAL_CHECK = 8;

   public AmbientIceBlockSoundsPlayer() {
      super();
   }

   public static void playAmbientIceSounds(final Level level, final BlockPos pos, final RandomSource random) {
      if (random.nextInt(10000) == 0 && shouldPlayAmbientIceSound(level, pos)) {
         level.playLocalSound((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvents.ICE_BLOCK_AMBIENT, SoundSource.BLOCKS, 1.0F, 1.0F, false);
      }

   }

   private static boolean shouldPlayAmbientIceSound(final Level level, final BlockPos pos) {
      return level.getBlockState(pos.below()).is(Blocks.WATER) && level.getBlockState(pos.above()).isAir() ? level.findBlocksIn(BlockPos.horizontalRays(pos, 8)).filterState((state) -> state.is(Blocks.ICE)).allMatched() : false;
   }
}
