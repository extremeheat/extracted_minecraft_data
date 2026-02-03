package net.minecraft.world.level.lighting;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;

public final class BlockLightEngine extends LightEngine<BlockLightSectionStorage.BlockDataLayerStorageMap, BlockLightSectionStorage> {
   private final BlockPos.MutableBlockPos mutablePos;

   public BlockLightEngine(final LightChunkGetter chunkSource) {
      this(chunkSource, new BlockLightSectionStorage(chunkSource));
   }

   @VisibleForTesting
   public BlockLightEngine(final LightChunkGetter chunkSource, final BlockLightSectionStorage storage) {
      super(chunkSource, storage);
      this.mutablePos = new BlockPos.MutableBlockPos();
   }

   protected void checkNode(final long blockNode) {
      long sectionNode = SectionPos.blockToSection(blockNode);
      if (((BlockLightSectionStorage)this.storage).storingLightForSection(sectionNode)) {
         BlockState state = this.getState(this.mutablePos.set(blockNode));
         int lightEmission = this.getEmission(blockNode, state);
         int oldLevel = ((BlockLightSectionStorage)this.storage).getStoredLevel(blockNode);
         if (lightEmission < oldLevel) {
            ((BlockLightSectionStorage)this.storage).setStoredLevel(blockNode, 0);
            this.enqueueDecrease(blockNode, LightEngine.QueueEntry.decreaseAllDirections(oldLevel));
         } else {
            this.enqueueDecrease(blockNode, PULL_LIGHT_IN_ENTRY);
         }

         if (lightEmission > 0) {
            this.enqueueIncrease(blockNode, LightEngine.QueueEntry.increaseLightFromEmission(lightEmission, isEmptyShape(state)));
         }

      }
   }

   protected void propagateIncrease(final long fromNode, final long increaseData, final int fromLevel) {
      BlockState fromState = null;

      for(Direction propagationDirection : PROPAGATION_DIRECTIONS) {
         if (LightEngine.QueueEntry.shouldPropagateInDirection(increaseData, propagationDirection)) {
            long toNode = BlockPos.offset(fromNode, propagationDirection);
            if (((BlockLightSectionStorage)this.storage).storingLightForSection(SectionPos.blockToSection(toNode))) {
               int toLevel = ((BlockLightSectionStorage)this.storage).getStoredLevel(toNode);
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
                        ((BlockLightSectionStorage)this.storage).setStoredLevel(toNode, newToLevel);
                        if (newToLevel > 1) {
                           this.enqueueIncrease(toNode, LightEngine.QueueEntry.increaseSkipOneDirection(newToLevel, isEmptyShape(toState), propagationDirection.getOpposite()));
                        }
                     }
                  }
               }
            }
         }
      }

   }

   protected void propagateDecrease(final long fromNode, final long decreaseData) {
      int oldFromLevel = LightEngine.QueueEntry.getFromLevel(decreaseData);

      for(Direction propagationDirection : PROPAGATION_DIRECTIONS) {
         if (LightEngine.QueueEntry.shouldPropagateInDirection(decreaseData, propagationDirection)) {
            long toNode = BlockPos.offset(fromNode, propagationDirection);
            if (((BlockLightSectionStorage)this.storage).storingLightForSection(SectionPos.blockToSection(toNode))) {
               int toLevel = ((BlockLightSectionStorage)this.storage).getStoredLevel(toNode);
               if (toLevel != 0) {
                  if (toLevel <= oldFromLevel - 1) {
                     BlockState toState = this.getState(this.mutablePos.set(toNode));
                     int toEmission = this.getEmission(toNode, toState);
                     ((BlockLightSectionStorage)this.storage).setStoredLevel(toNode, 0);
                     if (toEmission < toLevel) {
                        this.enqueueDecrease(toNode, LightEngine.QueueEntry.decreaseSkipOneDirection(toLevel, propagationDirection.getOpposite()));
                     }

                     if (toEmission > 0) {
                        this.enqueueIncrease(toNode, LightEngine.QueueEntry.increaseLightFromEmission(toEmission, isEmptyShape(toState)));
                     }
                  } else {
                     this.enqueueIncrease(toNode, LightEngine.QueueEntry.increaseOnlyOneDirection(toLevel, false, propagationDirection.getOpposite()));
                  }
               }
            }
         }
      }

   }

   private int getEmission(final long blockNode, final BlockState state) {
      int emission = state.getLightEmission();
      return emission > 0 && ((BlockLightSectionStorage)this.storage).lightOnInSection(SectionPos.blockToSection(blockNode)) ? emission : 0;
   }

   public void propagateLightSources(final ChunkPos pos) {
      this.setLightEnabled(pos, true);
      LightChunk chunk = this.chunkSource.getChunkForLighting(pos.x(), pos.z());
      if (chunk != null) {
         chunk.findBlockLightSources((lightPos, state) -> {
            int lightEmission = state.getLightEmission();
            this.enqueueIncrease(lightPos.asLong(), LightEngine.QueueEntry.increaseLightFromEmission(lightEmission, isEmptyShape(state)));
         });
      }

   }
}
