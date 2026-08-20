package net.minecraft.world.level.blockscan;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;

public class BlockScanUtils {
   public BlockScanUtils() {
      super();
   }

   static boolean findBlocks(final LevelReader level, final BlockPos from, final BlockPos to, final Predicate<BlockState> predicate, final BlockStateConsumer consumer) {
      int minX = Math.min(from.getX(), to.getX());
      int minY = Math.min(from.getY(), to.getY());
      int minZ = Math.min(from.getZ(), to.getZ());
      int maxX = Math.max(from.getX(), to.getX());
      int maxY = Math.max(from.getY(), to.getY());
      int maxZ = Math.max(from.getZ(), to.getZ());
      return sectionBasedScan(level, minX, minY, minZ, maxX, maxY, maxZ, predicate, consumer);
   }

   private static boolean sectionBasedScan(final LevelReader level, final int minX, final int minY, final int minZ, final int maxX, final int maxY, final int maxZ, final Predicate<BlockState> predicate, final BlockStateConsumer consumer) {
      int minSectionX = SectionPos.blockToSectionCoord(minX);
      int minSectionY = Math.max(level.getMinSectionY(), SectionPos.blockToSectionCoord(minY));
      int minSectionZ = SectionPos.blockToSectionCoord(minZ);
      int maxSectionX = SectionPos.blockToSectionCoord(maxX);
      int maxSectionY = Math.min(level.getMaxSectionY(), SectionPos.blockToSectionCoord(maxY));
      int maxSectionZ = SectionPos.blockToSectionCoord(maxZ);

      for(int x = minSectionX; x <= maxSectionX; ++x) {
         for(int z = minSectionZ; z <= maxSectionZ; ++z) {
            ChunkAccess chunk = level.getChunk(x, z);

            for(int y = minSectionY; y <= maxSectionY; ++y) {
               LevelChunkSection section = chunk.getSection(chunk.getSectionIndexFromSectionY(y));
               if (section.maybeHas(predicate)) {
                  int sectionOriginX = SectionPos.sectionToBlockCoord(x);
                  int sectionOriginY = SectionPos.sectionToBlockCoord(y);
                  int sectionOriginZ = SectionPos.sectionToBlockCoord(z);
                  int fromX = SectionPos.sectionRelative(Math.max(sectionOriginX, minX));
                  int fromY = SectionPos.sectionRelative(Math.max(sectionOriginY, minY));
                  int fromZ = SectionPos.sectionRelative(Math.max(sectionOriginZ, minZ));
                  int toX = SectionPos.sectionRelative(Math.min(SectionPos.sectionToBlockCoord(x, 15), maxX));
                  int toY = SectionPos.sectionRelative(Math.min(SectionPos.sectionToBlockCoord(y, 15), maxY));
                  int toZ = SectionPos.sectionRelative(Math.min(SectionPos.sectionToBlockCoord(z, 15), maxZ));
                  BlockPos origin = new BlockPos(sectionOriginX, sectionOriginY, sectionOriginZ);
                  if (findBlocksInSection(section, origin, fromX, fromY, fromZ, toX, toY, toZ, predicate, consumer)) {
                     return true;
                  }
               }
            }
         }
      }

      boolean belowWorld = minY < level.getMinY();
      boolean aboveWorld = maxY > level.getMaxY();
      if ((belowWorld || aboveWorld) && predicate.test(Blocks.AIR.defaultBlockState())) {
         if (belowWorld) {
            for(BlockPos pos : BlockPos.betweenClosed(minX, minY, minZ, maxX, level.getMinY() - 1, maxZ)) {
               if (consumer.apply(pos, Blocks.AIR.defaultBlockState()).shouldAbort()) {
                  return true;
               }
            }
         }

         if (aboveWorld) {
            for(BlockPos pos : BlockPos.betweenClosed(minX, level.getMaxY() + 1, minZ, maxX, maxY, maxZ)) {
               if (consumer.apply(pos, Blocks.AIR.defaultBlockState()).shouldAbort()) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static boolean findBlocksInSection(final LevelChunkSection section, final BlockPos origin, final int fromX, final int fromY, final int fromZ, final int toX, final int toY, final int toZ, final Predicate<BlockState> predicate, final BlockStateConsumer consumer) {
      BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

      for(int y = fromY; y <= toY; ++y) {
         for(int z = fromZ; z <= toZ; ++z) {
            for(int x = fromX; x <= toX; ++x) {
               BlockState blockState = section.getBlockState(x, y, z);
               if (predicate.test(blockState)) {
                  mutablePos.setWithOffset(origin, x, y, z);
                  if (consumer.apply(mutablePos, blockState).shouldAbort()) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   static boolean findBlocksWithCache(final LevelReader level, final Iterable<BlockPos> positions, final Predicate<BlockState> predicate, final BlockStateConsumer consumer) {
      FilteredSectionCache sectionCache = new FilteredSectionCache(level, predicate);

      for(BlockPos pos : positions) {
         BlockState state = sectionCache.getBlockState(pos);
         if (state != null && consumer.apply(pos, state).shouldAbort()) {
            return true;
         }
      }

      return false;
   }
}
