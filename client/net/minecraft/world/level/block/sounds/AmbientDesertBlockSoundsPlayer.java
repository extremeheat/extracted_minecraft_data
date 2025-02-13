package net.minecraft.world.level.block.sounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public class AmbientDesertBlockSoundsPlayer {
   private static final int IDLE_SOUND_CHANCE = 1600;
   private static final int WIND_SOUND_CHANCE = 10000;
   private static final int SURROUNDING_BLOCKS_PLAY_SOUND_THRESHOLD = 3;
   private static final int SURROUNDING_BLOCKS_DISTANCE_CHECK = 8;

   public AmbientDesertBlockSoundsPlayer() {
      super();
   }

   public static void playAmbientBlockSounds(BlockState var0, Level var1, BlockPos var2, RandomSource var3) {
      if (var0.is(BlockTags.PLAYS_AMBIENT_DESERT_BLOCK_SOUNDS) && var1.canSeeSky(var2.above())) {
         if (var3.nextInt(1600) == 0 && shouldPlayAmbientSound(var1, var2)) {
            var1.playLocalSound((double)var2.getX(), (double)var2.getY(), (double)var2.getZ(), SoundEvents.SAND_IDLE, SoundSource.AMBIENT, 1.0F, 1.0F, false);
         }

         if (var3.nextInt(10000) == 0 && isInAmbientSoundBiome(var1.getBiome(var2)) && shouldPlayAmbientSound(var1, var2)) {
            var1.playPlayerSound(SoundEvents.SAND_WIND, SoundSource.AMBIENT, 1.0F, 1.0F);
         }

      }
   }

   private static boolean isInAmbientSoundBiome(Holder<Biome> var0) {
      return var0.is(Biomes.DESERT) || var0.is(BiomeTags.IS_BADLANDS);
   }

   private static boolean shouldPlayAmbientSound(Level var0, BlockPos var1) {
      int var2 = 0;

      for(Direction var4 : Direction.Plane.HORIZONTAL) {
         BlockPos var5 = var1.relative((Direction)var4, 8);
         BlockState var6 = var0.getBlockState(var5.atY(var0.getHeight(Heightmap.Types.WORLD_SURFACE, var5) - 1));
         if (var6.is(BlockTags.PLAYS_AMBIENT_DESERT_BLOCK_SOUNDS)) {
            ++var2;
            if (var2 >= 3) {
               return true;
            }
         }
      }

      return false;
   }
}
