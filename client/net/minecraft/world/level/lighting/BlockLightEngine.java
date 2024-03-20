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
   private final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

   public BlockLightEngine(LightChunkGetter var1) {
      this(var1, new BlockLightSectionStorage(var1));
   }

   @VisibleForTesting
   public BlockLightEngine(LightChunkGetter var1, BlockLightSectionStorage var2) {
      super(var1, var2);
   }

   @Override
   protected void checkNode(long var1) {
      long var3 = SectionPos.blockToSection(var1);
      if (this.storage.storingLightForSection(var3)) {
         BlockState var5 = this.getState(this.mutablePos.set(var1));
         int var6 = this.getEmission(var1, var5);
         int var7 = this.storage.getStoredLevel(var1);
         if (var6 < var7) {
            this.storage.setStoredLevel(var1, 0);
            this.enqueueDecrease(var1, LightEngine.QueueEntry.decreaseAllDirections(var7));
         } else {
            this.enqueueDecrease(var1, PULL_LIGHT_IN_ENTRY);
         }

         if (var6 > 0) {
            this.enqueueIncrease(var1, LightEngine.QueueEntry.increaseLightFromEmission(var6, isEmptyShape(var5)));
         }
      }
   }

   @Override
   protected void propagateIncrease(long var1, long var3, int var5) {
      BlockState var6 = null;

      for(Direction var10 : PROPAGATION_DIRECTIONS) {
         if (LightEngine.QueueEntry.shouldPropagateInDirection(var3, var10)) {
            long var11 = BlockPos.offset(var1, var10);
            if (this.storage.storingLightForSection(SectionPos.blockToSection(var11))) {
               int var13 = this.storage.getStoredLevel(var11);
               int var14 = var5 - 1;
               if (var14 > var13) {
                  this.mutablePos.set(var11);
                  BlockState var15 = this.getState(this.mutablePos);
                  int var16 = var5 - this.getOpacity(var15, this.mutablePos);
                  if (var16 > var13) {
                     if (var6 == null) {
                        var6 = LightEngine.QueueEntry.isFromEmptyShape(var3) ? Blocks.AIR.defaultBlockState() : this.getState(this.mutablePos.set(var1));
                     }

                     if (!this.shapeOccludes(var1, var6, var11, var15, var10)) {
                        this.storage.setStoredLevel(var11, var16);
                        if (var16 > 1) {
                           this.enqueueIncrease(var11, LightEngine.QueueEntry.increaseSkipOneDirection(var16, isEmptyShape(var15), var10.getOpposite()));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   protected void propagateDecrease(long var1, long var3) {
      int var5 = LightEngine.QueueEntry.getFromLevel(var3);

      for(Direction var9 : PROPAGATION_DIRECTIONS) {
         if (LightEngine.QueueEntry.shouldPropagateInDirection(var3, var9)) {
            long var10 = BlockPos.offset(var1, var9);
            if (this.storage.storingLightForSection(SectionPos.blockToSection(var10))) {
               int var12 = this.storage.getStoredLevel(var10);
               if (var12 != 0) {
                  if (var12 <= var5 - 1) {
                     BlockState var13 = this.getState(this.mutablePos.set(var10));
                     int var14 = this.getEmission(var10, var13);
                     this.storage.setStoredLevel(var10, 0);
                     if (var14 < var12) {
                        this.enqueueDecrease(var10, LightEngine.QueueEntry.decreaseSkipOneDirection(var12, var9.getOpposite()));
                     }

                     if (var14 > 0) {
                        this.enqueueIncrease(var10, LightEngine.QueueEntry.increaseLightFromEmission(var14, isEmptyShape(var13)));
                     }
                  } else {
                     this.enqueueIncrease(var10, LightEngine.QueueEntry.increaseOnlyOneDirection(var12, false, var9.getOpposite()));
                  }
               }
            }
         }
      }
   }

   private int getEmission(long var1, BlockState var3) {
      int var4 = var3.getLightEmission();
      return var4 > 0 && this.storage.lightOnInSection(SectionPos.blockToSection(var1)) ? var4 : 0;
   }

   @Override
   public void propagateLightSources(ChunkPos var1) {
      this.setLightEnabled(var1, true);
      LightChunk var2 = this.chunkSource.getChunkForLighting(var1.x, var1.z);
      if (var2 != null) {
         var2.findBlockLightSources((var1x, var2x) -> {
            int var3 = var2x.getLightEmission();
            this.enqueueIncrease(var1x.asLong(), LightEngine.QueueEntry.increaseLightFromEmission(var3, isEmptyShape(var2x)));
         });
      }
   }
}