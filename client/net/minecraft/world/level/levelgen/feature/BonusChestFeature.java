package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class BonusChestFeature extends Feature<NoneFeatureConfiguration> {
   public BonusChestFeature(final Codec<NoneFeatureConfiguration> codec) {
      super(codec);
   }

   public boolean place(final FeaturePlaceContext<NoneFeatureConfiguration> context) {
      RandomSource random = context.random();
      WorldGenLevel level = context.level();
      ChunkPos chunkPos = ChunkPos.containing(context.origin());
      IntArrayList xPoses = Util.toShuffledList(IntStream.rangeClosed(chunkPos.getMinBlockX(), chunkPos.getMaxBlockX()), random);
      IntArrayList zPoses = Util.toShuffledList(IntStream.rangeClosed(chunkPos.getMinBlockZ(), chunkPos.getMaxBlockZ()), random);
      BlockPos.MutableBlockPos mutPos = new BlockPos.MutableBlockPos();
      IntListIterator var8 = xPoses.iterator();

      while(var8.hasNext()) {
         Integer x = (Integer)var8.next();
         IntListIterator var10 = zPoses.iterator();

         while(var10.hasNext()) {
            Integer z = (Integer)var10.next();
            mutPos.set(x, 0, z);
            BlockPos chestPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, mutPos);
            if (level.isEmptyBlock(chestPos) || level.getBlockState(chestPos).getCollisionShape(level, chestPos).isEmpty()) {
               level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 2);
               RandomizableContainer.setBlockEntityLootTable(level, random, chestPos, BuiltInLootTables.SPAWN_BONUS_CHEST);
               BlockState torch = Blocks.TORCH.defaultBlockState();

               for(Direction direction : Direction.Plane.HORIZONTAL) {
                  BlockPos torchPos = chestPos.relative(direction);
                  if (torch.canSurvive(level, torchPos)) {
                     level.setBlock(torchPos, torch, 2);
                  }
               }

               return true;
            }
         }
      }

      return false;
   }
}
