package net.minecraft.world.level.block.sounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public class AmbientDesertBlockSoundsPlayer {
   private static final int IDLE_SOUND_CHANCE = 2100;
   private static final int DRY_GRASS_SOUND_CHANCE = 200;
   private static final int DEAD_BUSH_SOUND_CHANCE = 130;
   private static final int DEAD_BUSH_SOUND_BADLANDS_DECREASED_CHANCE = 3;
   private static final int SURROUNDING_BLOCKS_PLAY_SOUND_THRESHOLD = 3;
   private static final int SURROUNDING_BLOCKS_DISTANCE_HORIZONTAL_CHECK = 8;
   private static final int SURROUNDING_BLOCKS_DISTANCE_VERTICAL_CHECK = 5;
   private static final int HORIZONTAL_DIRECTIONS = 4;

   public AmbientDesertBlockSoundsPlayer() {
      super();
   }

   public static void playAmbientSandSounds(Level var0, BlockPos var1, RandomSource var2) {
      if (var0.getBlockState(var1.above()).is(Blocks.AIR)) {
         if (var2.nextInt(2100) == 0 && shouldPlayAmbientSandSound(var0, var1)) {
            var0.playLocalSound((double)var1.getX(), (double)var1.getY(), (double)var1.getZ(), SoundEvents.SAND_IDLE, SoundSource.AMBIENT, 1.0F, 1.0F, false);
         }

      }
   }

   public static void playAmbientDryGrassSounds(Level var0, BlockPos var1, RandomSource var2) {
      if (var2.nextInt(200) == 0 && shouldPlayDesertDryVegetationBlockSounds(var0, var1.below())) {
         var0.playPlayerSound(SoundEvents.DRY_GRASS, SoundSource.AMBIENT, 1.0F, 1.0F);
      }

   }

   public static void playAmbientDeadBushSounds(Level var0, BlockPos var1, RandomSource var2) {
      if (var2.nextInt(130) == 0) {
         BlockState var3 = var0.getBlockState(var1.below());
         if ((var3.is(Blocks.RED_SAND) || var3.is(BlockTags.TERRACOTTA)) && var2.nextInt(3) != 0) {
            return;
         }

         if (shouldPlayDesertDryVegetationBlockSounds(var0, var1.below())) {
            var0.playLocalSound((double)var1.getX(), (double)var1.getY(), (double)var1.getZ(), SoundEvents.DEAD_BUSH_IDLE, SoundSource.AMBIENT, 1.0F, 1.0F, false);
         }
      }

   }

   public static boolean shouldPlayDesertDryVegetationBlockSounds(Level var0, BlockPos var1) {
      return var0.getBlockState(var1).is(BlockTags.TRIGGERS_AMBIENT_DESERT_DRY_VEGETATION_BLOCK_SOUNDS) && var0.getBlockState(var1.below()).is(BlockTags.TRIGGERS_AMBIENT_DESERT_DRY_VEGETATION_BLOCK_SOUNDS);
   }

   private static boolean shouldPlayAmbientSandSound(Level var0, BlockPos var1) {
      int var2 = 0;
      int var3 = 0;
      BlockPos.MutableBlockPos var4 = var1.mutable();

      for(Direction var6 : Direction.Plane.HORIZONTAL) {
         var4.set(var1).move(var6, 8);
         if (columnContainsTriggeringBlock(var0, var4) && var2++ >= 3) {
            return true;
         }

         ++var3;
         int var7 = 4 - var3;
         int var8 = var7 + var2;
         boolean var9 = var8 >= 3;
         if (!var9) {
            return false;
         }
      }

      return false;
   }

   private static boolean columnContainsTriggeringBlock(Level var0, BlockPos.MutableBlockPos var1) {
      int var2 = var0.getHeight(Heightmap.Types.WORLD_SURFACE, var1) - 1;
      if (Math.abs(var2 - var1.getY()) > 5) {
         var1.move(Direction.UP, 6);
         BlockState var6 = var0.getBlockState(var1);
         var1.move(Direction.DOWN);

         for(int var4 = 0; var4 < 10; ++var4) {
            BlockState var5 = var0.getBlockState(var1);
            if (var6.isAir() && canTriggerAmbientDesertSandSounds(var5)) {
               return true;
            }

            var6 = var5;
            var1.move(Direction.DOWN);
         }

         return false;
      } else {
         boolean var3 = var0.getBlockState(var1.setY(var2 + 1)).isAir();
         return var3 && canTriggerAmbientDesertSandSounds(var0.getBlockState(var1.setY(var2)));
      }
   }

   private static boolean canTriggerAmbientDesertSandSounds(BlockState var0) {
      return var0.is(BlockTags.TRIGGERS_AMBIENT_DESERT_SAND_BLOCK_SOUNDS);
   }
}
