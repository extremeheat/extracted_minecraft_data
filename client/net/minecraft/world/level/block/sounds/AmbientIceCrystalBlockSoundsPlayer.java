package net.minecraft.world.level.block.sounds;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class AmbientIceCrystalBlockSoundsPlayer {
   private static final int IDLE_SOUND_CHANCE = 250;

   public AmbientIceCrystalBlockSoundsPlayer() {
      super();
   }

   public static void playAmbientIceCrystalSounds(final Level level, final BlockPos pos, final RandomSource random) {
      if (random.nextInt(250) == 0 && shouldPlayAmbientIceCrystalSound(level, pos)) {
         level.playLocalSound((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvents.ICE_CRYSTAL_AMBIENT, SoundSource.AMBIENT, 1.0F, 1.0F, false);
      }

   }

   private static boolean shouldPlayAmbientIceCrystalSound(final Level level, final BlockPos pos) {
      return level.getBlockState(pos.below()).is(Blocks.PACKED_ICE);
   }
}
