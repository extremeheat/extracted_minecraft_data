package net.minecraft.world.level.lighting;

import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import org.jspecify.annotations.Nullable;

public final class SkyLightEngine extends LightEngine<SkyLightSectionStorage.SkyDataLayerStorageMap, SkyLightSectionStorage> {
   private static final long REMOVE_TOP_SKY_SOURCE_ENTRY = LightEngine.QueueEntry.decreaseAllDirections(15);
   private static final long REMOVE_SKY_SOURCE_ENTRY;
   private static final long ADD_SKY_SOURCE_ENTRY;
   private final BlockPos.MutableBlockPos mutablePos;
   private final ChunkSkyLightSources emptyChunkSources;

   public SkyLightEngine(final LightChunkGetter chunkSource) {
      this(chunkSource, new SkyLightSectionStorage(chunkSource));
   }

   @VisibleForTesting
   protected SkyLightEngine(final LightChunkGetter chunkSource, final SkyLightSectionStorage storage) {
      super(chunkSource, storage);
      this.mutablePos = new BlockPos.MutableBlockPos();
      this.emptyChunkSources = new ChunkSkyLightSources(chunkSource.getLevel());
   }

   private static boolean isSourceLevel(final int value) {
      return value == 15;
   }

   private int getLowestSourceY(final int x, final int z, final int defaultValue) {
      ChunkSkyLightSources sources = this.getChunkSources(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
      return sources == null ? defaultValue : sources.getLowestSourceY(SectionPos.sectionRelative(x), SectionPos.sectionRelative(z));
   }

   private @Nullable ChunkSkyLightSources getChunkSources(final int chunkX, final int chunkZ) {
      LightChunk chunk = this.chunkSource.getChunkForLighting(chunkX, chunkZ);
      return chunk != null ? chunk.getSkyLightSources() : null;
   }

   protected void checkNode(final long blockNode) {
      int x = BlockPos.getX(blockNode);
      int y = BlockPos.getY(blockNode);
      int z = BlockPos.getZ(blockNode);
      long sectionNode = SectionPos.blockToSection(blockNode);
      int lowestSourceY = ((SkyLightSectionStorage)this.storage).lightOnInSection(sectionNode) ? this.getLowestSourceY(x, z, 2147483647) : 2147483647;
      if (lowestSourceY != 2147483647) {
         this.updateSourcesInColumn(x, z, lowestSourceY);
      }

      if (((SkyLightSectionStorage)this.storage).storingLightForSection(sectionNode)) {
         boolean isSource = y >= lowestSourceY;
         if (isSource) {
            this.enqueueDecrease(blockNode, REMOVE_SKY_SOURCE_ENTRY);
            this.enqueueIncrease(blockNode, ADD_SKY_SOURCE_ENTRY);
         } else {
            int oldLevel = ((SkyLightSectionStorage)this.storage).getStoredLevel(blockNode);
            if (oldLevel > 0) {
               ((SkyLightSectionStorage)this.storage).setStoredLevel(blockNode, 0);
               this.enqueueDecrease(blockNode, LightEngine.QueueEntry.decreaseAllDirections(oldLevel));
            } else {
               this.enqueueDecrease(blockNode, PULL_LIGHT_IN_ENTRY);
            }
         }

      }
   }

   private void updateSourcesInColumn(final int x, final int z, final int lowestSourceY) {
      int worldBottomY = SectionPos.sectionToBlockCoord(((SkyLightSectionStorage)this.storage).getBottomSectionY());
      this.removeSourcesBelow(x, z, lowestSourceY, worldBottomY);
      this.addSourcesAbove(x, z, lowestSourceY, worldBottomY);
   }

   private void removeSourcesBelow(final int x, final int z, final int lowestSourceY, final int worldBottomY) {
      if (lowestSourceY > worldBottomY) {
         int sectionX = SectionPos.blockToSectionCoord(x);
         int sectionZ = SectionPos.blockToSectionCoord(z);
         int startY = lowestSourceY - 1;

         for(int sectionY = SectionPos.blockToSectionCoord(startY); ((SkyLightSectionStorage)this.storage).hasLightDataAtOrBelow(sectionY); --sectionY) {
            if (((SkyLightSectionStorage)this.storage).storingLightForSection(SectionPos.asLong(sectionX, sectionY, sectionZ))) {
               int sectionBottomY = SectionPos.sectionToBlockCoord(sectionY);
               int sectionTopY = sectionBottomY + 15;

               for(int y = Math.min(sectionTopY, startY); y >= sectionBottomY; --y) {
                  long blockNode = BlockPos.asLong(x, y, z);
                  if (!isSourceLevel(((SkyLightSectionStorage)this.storage).getStoredLevel(blockNode))) {
                     return;
                  }

                  ((SkyLightSectionStorage)this.storage).setStoredLevel(blockNode, 0);
                  this.enqueueDecrease(blockNode, y == lowestSourceY - 1 ? REMOVE_TOP_SKY_SOURCE_ENTRY : REMOVE_SKY_SOURCE_ENTRY);
               }
            }
         }

      }
   }

   private void addSourcesAbove(final int x, final int z, final int lowestSourceY, final int worldBottomY) {
      int sectionX = SectionPos.blockToSectionCoord(x);
      int sectionZ = SectionPos.blockToSectionCoord(z);
      int neighborLowestSourceY = Math.max(Math.max(this.getLowestSourceY(x - 1, z, -2147483648), this.getLowestSourceY(x + 1, z, -2147483648)), Math.max(this.getLowestSourceY(x, z - 1, -2147483648), this.getLowestSourceY(x, z + 1, -2147483648)));
      int startY = Math.max(lowestSourceY, worldBottomY);

      for(long sectionNode = SectionPos.asLong(sectionX, SectionPos.blockToSectionCoord(startY), sectionZ); !((SkyLightSectionStorage)this.storage).isAboveData(sectionNode); sectionNode = SectionPos.offset(sectionNode, Direction.UP)) {
         if (((SkyLightSectionStorage)this.storage).storingLightForSection(sectionNode)) {
            int sectionBottomY = SectionPos.sectionToBlockCoord(SectionPos.y(sectionNode));
            int sectionTopY = sectionBottomY + 15;

            for(int y = Math.max(sectionBottomY, startY); y <= sectionTopY; ++y) {
               long blockNode = BlockPos.asLong(x, y, z);
               if (isSourceLevel(((SkyLightSectionStorage)this.storage).getStoredLevel(blockNode))) {
                  return;
               }

               ((SkyLightSectionStorage)this.storage).setStoredLevel(blockNode, 15);
               if (y < neighborLowestSourceY || y == lowestSourceY) {
                  this.enqueueIncrease(blockNode, ADD_SKY_SOURCE_ENTRY);
               }
            }
         }
      }

   }

   protected void propagateIncrease(final long fromNode, final long increaseData, final int fromLevel) {
      BlockState fromState = null;
      int emptySectionsBelow = this.countEmptySectionsBelowIfAtBorder(fromNode);

      for(Direction propagationDirection : PROPAGATION_DIRECTIONS) {
         if (LightEngine.QueueEntry.shouldPropagateInDirection(increaseData, propagationDirection)) {
            long toNode = BlockPos.offset(fromNode, propagationDirection);
            if (((SkyLightSectionStorage)this.storage).storingLightForSection(SectionPos.blockToSection(toNode))) {
               int toLevel = ((SkyLightSectionStorage)this.storage).getStoredLevel(toNode);
               int maxPossibleNewToLevel = fromLevel - 1;
               if (maxPossibleNewToLevel > toLevel) {
                  this.mutablePos.set(toNode);
                  BlockState toState = this.getState(this.mutablePos);
                  int newToLevel = fromLevel - this.getOpacity(toState);
                  if (newToLevel > toLevel) {
                     if (fromState == null) {
                        fromState = LightEngine.QueueEntry.isFromEmptyShape(increaseData) ? Blocks.AIR.defaultBlockState() : this.getState(this.mutablePos.set(fromNode));
                     }

                     if (!this.shapeOccludes(fromState, toState, propagationDirection)) {
                        ((SkyLightSectionStorage)this.storage).setStoredLevel(toNode, newToLevel);
                        if (newToLevel > 1) {
                           this.enqueueIncrease(toNode, LightEngine.QueueEntry.increaseSkipOneDirection(newToLevel, isEmptyShape(toState), propagationDirection.getOpposite()));
                        }

                        this.propagateFromEmptySections(toNode, propagationDirection, newToLevel, true, emptySectionsBelow);
                     }
                  }
               }
            }
         }
      }

   }

   protected void propagateDecrease(final long fromNode, final long decreaseData) {
      int emptySectionsBelow = this.countEmptySectionsBelowIfAtBorder(fromNode);
      int oldFromLevel = LightEngine.QueueEntry.getFromLevel(decreaseData);

      for(Direction propagationDirection : PROPAGATION_DIRECTIONS) {
         if (LightEngine.QueueEntry.shouldPropagateInDirection(decreaseData, propagationDirection)) {
            long toNode = BlockPos.offset(fromNode, propagationDirection);
            if (((SkyLightSectionStorage)this.storage).storingLightForSection(SectionPos.blockToSection(toNode))) {
               int toLevel = ((SkyLightSectionStorage)this.storage).getStoredLevel(toNode);
               if (toLevel != 0) {
                  if (toLevel <= oldFromLevel - 1) {
                     ((SkyLightSectionStorage)this.storage).setStoredLevel(toNode, 0);
                     this.enqueueDecrease(toNode, LightEngine.QueueEntry.decreaseSkipOneDirection(toLevel, propagationDirection.getOpposite()));
                     this.propagateFromEmptySections(toNode, propagationDirection, toLevel, false, emptySectionsBelow);
                  } else {
                     this.enqueueIncrease(toNode, LightEngine.QueueEntry.increaseOnlyOneDirection(toLevel, false, propagationDirection.getOpposite()));
                  }
               }
            }
         }
      }

   }

   private int countEmptySectionsBelowIfAtBorder(final long blockNode) {
      int y = BlockPos.getY(blockNode);
      int localY = SectionPos.sectionRelative(y);
      if (localY != 0) {
         return 0;
      } else {
         int x = BlockPos.getX(blockNode);
         int z = BlockPos.getZ(blockNode);
         int localX = SectionPos.sectionRelative(x);
         int localZ = SectionPos.sectionRelative(z);
         if (localX != 0 && localX != 15 && localZ != 0 && localZ != 15) {
            return 0;
         } else {
            int sectionX = SectionPos.blockToSectionCoord(x);
            int sectionY = SectionPos.blockToSectionCoord(y);
            int sectionZ = SectionPos.blockToSectionCoord(z);

            int emptySectionsBelow;
            for(emptySectionsBelow = 0; !((SkyLightSectionStorage)this.storage).storingLightForSection(SectionPos.asLong(sectionX, sectionY - emptySectionsBelow - 1, sectionZ)) && ((SkyLightSectionStorage)this.storage).hasLightDataAtOrBelow(sectionY - emptySectionsBelow - 1); ++emptySectionsBelow) {
            }

            return emptySectionsBelow;
         }
      }
   }

   private void propagateFromEmptySections(final long toNode, final Direction propagationDirection, final int toLevel, final boolean increase, final int emptySectionsBelow) {
      if (emptySectionsBelow != 0) {
         int x = BlockPos.getX(toNode);
         int z = BlockPos.getZ(toNode);
         if (crossedSectionEdge(propagationDirection, SectionPos.sectionRelative(x), SectionPos.sectionRelative(z))) {
            int y = BlockPos.getY(toNode);
            int sectionX = SectionPos.blockToSectionCoord(x);
            int sectionZ = SectionPos.blockToSectionCoord(z);
            int sectionY = SectionPos.blockToSectionCoord(y) - 1;
            int bottomSectionY = sectionY - emptySectionsBelow + 1;

            while(sectionY >= bottomSectionY) {
               if (!((SkyLightSectionStorage)this.storage).storingLightForSection(SectionPos.asLong(sectionX, sectionY, sectionZ))) {
                  --sectionY;
               } else {
                  int sectionMinY = SectionPos.sectionToBlockCoord(sectionY);

                  for(int localY = 15; localY >= 0; --localY) {
                     long blockNode = BlockPos.asLong(x, sectionMinY + localY, z);
                     if (increase) {
                        ((SkyLightSectionStorage)this.storage).setStoredLevel(blockNode, toLevel);
                        if (toLevel > 1) {
                           this.enqueueIncrease(blockNode, LightEngine.QueueEntry.increaseSkipOneDirection(toLevel, true, propagationDirection.getOpposite()));
                        }
                     } else {
                        ((SkyLightSectionStorage)this.storage).setStoredLevel(blockNode, 0);
                        this.enqueueDecrease(blockNode, LightEngine.QueueEntry.decreaseSkipOneDirection(toLevel, propagationDirection.getOpposite()));
                     }
                  }

                  --sectionY;
               }
            }

         }
      }
   }

   private static boolean crossedSectionEdge(final Direction propagationDirection, final int x, final int z) {
      boolean var10000;
      switch (propagationDirection) {
         case NORTH -> var10000 = z == 15;
         case SOUTH -> var10000 = z == 0;
         case WEST -> var10000 = x == 15;
         case EAST -> var10000 = x == 0;
         default -> var10000 = false;
      }

      return var10000;
   }

   public void setLightEnabled(final ChunkPos pos, final boolean enable) {
      super.setLightEnabled(pos, enable);
      if (enable) {
         ChunkSkyLightSources sources = (ChunkSkyLightSources)Objects.requireNonNullElse(this.getChunkSources(pos.x(), pos.z()), this.emptyChunkSources);
         int highestNonSourceY = sources.getHighestLowestSourceY() - 1;
         int lowestFullySourceSectionY = SectionPos.blockToSectionCoord(highestNonSourceY) + 1;
         long zeroNode = SectionPos.getZeroNode(pos.x(), pos.z());
         int topSectionY = ((SkyLightSectionStorage)this.storage).getTopSectionY(zeroNode);
         int bottomSectionY = Math.max(((SkyLightSectionStorage)this.storage).getBottomSectionY(), lowestFullySourceSectionY);

         for(int sectionY = topSectionY - 1; sectionY >= bottomSectionY; --sectionY) {
            DataLayer dataLayer = ((SkyLightSectionStorage)this.storage).getDataLayerToWrite(SectionPos.asLong(pos.x(), sectionY, pos.z()));
            if (dataLayer != null && dataLayer.isEmpty()) {
               dataLayer.fill(15);
            }
         }
      }

   }

   public void propagateLightSources(final ChunkPos pos) {
      long zeroNode = SectionPos.getZeroNode(pos.x(), pos.z());
      ((SkyLightSectionStorage)this.storage).setLightEnabled(zeroNode, true);
      ChunkSkyLightSources sources = (ChunkSkyLightSources)Objects.requireNonNullElse(this.getChunkSources(pos.x(), pos.z()), this.emptyChunkSources);
      ChunkSkyLightSources northSources = (ChunkSkyLightSources)Objects.requireNonNullElse(this.getChunkSources(pos.x(), pos.z() - 1), this.emptyChunkSources);
      ChunkSkyLightSources southSources = (ChunkSkyLightSources)Objects.requireNonNullElse(this.getChunkSources(pos.x(), pos.z() + 1), this.emptyChunkSources);
      ChunkSkyLightSources westSources = (ChunkSkyLightSources)Objects.requireNonNullElse(this.getChunkSources(pos.x() - 1, pos.z()), this.emptyChunkSources);
      ChunkSkyLightSources eastSources = (ChunkSkyLightSources)Objects.requireNonNullElse(this.getChunkSources(pos.x() + 1, pos.z()), this.emptyChunkSources);
      int topSectionY = ((SkyLightSectionStorage)this.storage).getTopSectionY(zeroNode);
      int bottomSectionY = ((SkyLightSectionStorage)this.storage).getBottomSectionY();
      int sectionMinX = SectionPos.sectionToBlockCoord(pos.x());
      int sectionMinZ = SectionPos.sectionToBlockCoord(pos.z());

      for(int sectionY = topSectionY - 1; sectionY >= bottomSectionY; --sectionY) {
         long sectionNode = SectionPos.asLong(pos.x(), sectionY, pos.z());
         DataLayer dataLayer = ((SkyLightSectionStorage)this.storage).getDataLayerToWrite(sectionNode);
         if (dataLayer != null) {
            int sectionMinY = SectionPos.sectionToBlockCoord(sectionY);
            int sectionMaxY = sectionMinY + 15;
            boolean sourcesBelow = false;

            for(int z = 0; z < 16; ++z) {
               for(int x = 0; x < 16; ++x) {
                  int lowestSourceY = sources.getLowestSourceY(x, z);
                  if (lowestSourceY <= sectionMaxY) {
                     int northLowestSourceY = z == 0 ? northSources.getLowestSourceY(x, 15) : sources.getLowestSourceY(x, z - 1);
                     int southLowestSourceY = z == 15 ? southSources.getLowestSourceY(x, 0) : sources.getLowestSourceY(x, z + 1);
                     int westLowestSourceY = x == 0 ? westSources.getLowestSourceY(15, z) : sources.getLowestSourceY(x - 1, z);
                     int eastLowestSourceY = x == 15 ? eastSources.getLowestSourceY(0, z) : sources.getLowestSourceY(x + 1, z);
                     int neighborLowestSourceY = Math.max(Math.max(northLowestSourceY, southLowestSourceY), Math.max(westLowestSourceY, eastLowestSourceY));

                     for(int y = sectionMaxY; y >= Math.max(sectionMinY, lowestSourceY); --y) {
                        dataLayer.set(x, SectionPos.sectionRelative(y), z, 15);
                        if (y == lowestSourceY || y < neighborLowestSourceY) {
                           long blockNode = BlockPos.asLong(sectionMinX + x, y, sectionMinZ + z);
                           this.enqueueIncrease(blockNode, LightEngine.QueueEntry.increaseSkySourceInDirections(y == lowestSourceY, y < northLowestSourceY, y < southLowestSourceY, y < westLowestSourceY, y < eastLowestSourceY));
                        }
                     }

                     if (lowestSourceY < sectionMinY) {
                        sourcesBelow = true;
                     }
                  }
               }
            }

            if (!sourcesBelow) {
               break;
            }
         }
      }

   }

   static {
      REMOVE_SKY_SOURCE_ENTRY = LightEngine.QueueEntry.decreaseSkipOneDirection(15, Direction.UP);
      ADD_SKY_SOURCE_ENTRY = LightEngine.QueueEntry.increaseSkipOneDirection(15, false, Direction.UP);
   }
}
