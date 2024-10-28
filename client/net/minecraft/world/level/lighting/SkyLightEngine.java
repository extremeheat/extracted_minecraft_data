package net.minecraft.world.level.lighting;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import org.jetbrains.annotations.VisibleForTesting;

public final class SkyLightEngine extends LightEngine<SkyLightSectionStorage.SkyDataLayerStorageMap, SkyLightSectionStorage> {
   private static final long REMOVE_TOP_SKY_SOURCE_ENTRY = LightEngine.QueueEntry.decreaseAllDirections(15);
   private static final long REMOVE_SKY_SOURCE_ENTRY;
   private static final long ADD_SKY_SOURCE_ENTRY;
   private final BlockPos.MutableBlockPos mutablePos;
   private final ChunkSkyLightSources emptyChunkSources;

   public SkyLightEngine(LightChunkGetter var1) {
      this(var1, new SkyLightSectionStorage(var1));
   }

   @VisibleForTesting
   protected SkyLightEngine(LightChunkGetter var1, SkyLightSectionStorage var2) {
      super(var1, var2);
      this.mutablePos = new BlockPos.MutableBlockPos();
      this.emptyChunkSources = new ChunkSkyLightSources(var1.getLevel());
   }

   private static boolean isSourceLevel(int var0) {
      return var0 == 15;
   }

   private int getLowestSourceY(int var1, int var2, int var3) {
      ChunkSkyLightSources var4 = this.getChunkSources(SectionPos.blockToSectionCoord(var1), SectionPos.blockToSectionCoord(var2));
      return var4 == null ? var3 : var4.getLowestSourceY(SectionPos.sectionRelative(var1), SectionPos.sectionRelative(var2));
   }

   @Nullable
   private ChunkSkyLightSources getChunkSources(int var1, int var2) {
      LightChunk var3 = this.chunkSource.getChunkForLighting(var1, var2);
      return var3 != null ? var3.getSkyLightSources() : null;
   }

   protected void checkNode(long var1) {
      int var3 = BlockPos.getX(var1);
      int var4 = BlockPos.getY(var1);
      int var5 = BlockPos.getZ(var1);
      long var6 = SectionPos.blockToSection(var1);
      int var8 = ((SkyLightSectionStorage)this.storage).lightOnInSection(var6) ? this.getLowestSourceY(var3, var5, 2147483647) : 2147483647;
      if (var8 != 2147483647) {
         this.updateSourcesInColumn(var3, var5, var8);
      }

      if (((SkyLightSectionStorage)this.storage).storingLightForSection(var6)) {
         boolean var9 = var4 >= var8;
         if (var9) {
            this.enqueueDecrease(var1, REMOVE_SKY_SOURCE_ENTRY);
            this.enqueueIncrease(var1, ADD_SKY_SOURCE_ENTRY);
         } else {
            int var10 = ((SkyLightSectionStorage)this.storage).getStoredLevel(var1);
            if (var10 > 0) {
               ((SkyLightSectionStorage)this.storage).setStoredLevel(var1, 0);
               this.enqueueDecrease(var1, LightEngine.QueueEntry.decreaseAllDirections(var10));
            } else {
               this.enqueueDecrease(var1, PULL_LIGHT_IN_ENTRY);
            }
         }

      }
   }

   private void updateSourcesInColumn(int var1, int var2, int var3) {
      int var4 = SectionPos.sectionToBlockCoord(((SkyLightSectionStorage)this.storage).getBottomSectionY());
      this.removeSourcesBelow(var1, var2, var3, var4);
      this.addSourcesAbove(var1, var2, var3, var4);
   }

   private void removeSourcesBelow(int var1, int var2, int var3, int var4) {
      if (var3 > var4) {
         int var5 = SectionPos.blockToSectionCoord(var1);
         int var6 = SectionPos.blockToSectionCoord(var2);
         int var7 = var3 - 1;

         for(int var8 = SectionPos.blockToSectionCoord(var7); ((SkyLightSectionStorage)this.storage).hasLightDataAtOrBelow(var8); --var8) {
            if (((SkyLightSectionStorage)this.storage).storingLightForSection(SectionPos.asLong(var5, var8, var6))) {
               int var9 = SectionPos.sectionToBlockCoord(var8);
               int var10 = var9 + 15;

               for(int var11 = Math.min(var10, var7); var11 >= var9; --var11) {
                  long var12 = BlockPos.asLong(var1, var11, var2);
                  if (!isSourceLevel(((SkyLightSectionStorage)this.storage).getStoredLevel(var12))) {
                     return;
                  }

                  ((SkyLightSectionStorage)this.storage).setStoredLevel(var12, 0);
                  this.enqueueDecrease(var12, var11 == var3 - 1 ? REMOVE_TOP_SKY_SOURCE_ENTRY : REMOVE_SKY_SOURCE_ENTRY);
               }
            }
         }

      }
   }

   private void addSourcesAbove(int var1, int var2, int var3, int var4) {
      int var5 = SectionPos.blockToSectionCoord(var1);
      int var6 = SectionPos.blockToSectionCoord(var2);
      int var7 = Math.max(Math.max(this.getLowestSourceY(var1 - 1, var2, -2147483648), this.getLowestSourceY(var1 + 1, var2, -2147483648)), Math.max(this.getLowestSourceY(var1, var2 - 1, -2147483648), this.getLowestSourceY(var1, var2 + 1, -2147483648)));
      int var8 = Math.max(var3, var4);

      for(long var9 = SectionPos.asLong(var5, SectionPos.blockToSectionCoord(var8), var6); !((SkyLightSectionStorage)this.storage).isAboveData(var9); var9 = SectionPos.offset(var9, Direction.UP)) {
         if (((SkyLightSectionStorage)this.storage).storingLightForSection(var9)) {
            int var11 = SectionPos.sectionToBlockCoord(SectionPos.y(var9));
            int var12 = var11 + 15;

            for(int var13 = Math.max(var11, var8); var13 <= var12; ++var13) {
               long var14 = BlockPos.asLong(var1, var13, var2);
               if (isSourceLevel(((SkyLightSectionStorage)this.storage).getStoredLevel(var14))) {
                  return;
               }

               ((SkyLightSectionStorage)this.storage).setStoredLevel(var14, 15);
               if (var13 < var7 || var13 == var3) {
                  this.enqueueIncrease(var14, ADD_SKY_SOURCE_ENTRY);
               }
            }
         }
      }

   }

   protected void propagateIncrease(long var1, long var3, int var5) {
      BlockState var6 = null;
      int var7 = this.countEmptySectionsBelowIfAtBorder(var1);
      Direction[] var8 = PROPAGATION_DIRECTIONS;
      int var9 = var8.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         Direction var11 = var8[var10];
         if (LightEngine.QueueEntry.shouldPropagateInDirection(var3, var11)) {
            long var12 = BlockPos.offset(var1, var11);
            if (((SkyLightSectionStorage)this.storage).storingLightForSection(SectionPos.blockToSection(var12))) {
               int var14 = ((SkyLightSectionStorage)this.storage).getStoredLevel(var12);
               int var15 = var5 - 1;
               if (var15 > var14) {
                  this.mutablePos.set(var12);
                  BlockState var16 = this.getState(this.mutablePos);
                  int var17 = var5 - this.getOpacity(var16, this.mutablePos);
                  if (var17 > var14) {
                     if (var6 == null) {
                        var6 = LightEngine.QueueEntry.isFromEmptyShape(var3) ? Blocks.AIR.defaultBlockState() : this.getState(this.mutablePos.set(var1));
                     }

                     if (!this.shapeOccludes(var1, var6, var12, var16, var11)) {
                        ((SkyLightSectionStorage)this.storage).setStoredLevel(var12, var17);
                        if (var17 > 1) {
                           this.enqueueIncrease(var12, LightEngine.QueueEntry.increaseSkipOneDirection(var17, isEmptyShape(var16), var11.getOpposite()));
                        }

                        this.propagateFromEmptySections(var12, var11, var17, true, var7);
                     }
                  }
               }
            }
         }
      }

   }

   protected void propagateDecrease(long var1, long var3) {
      int var5 = this.countEmptySectionsBelowIfAtBorder(var1);
      int var6 = LightEngine.QueueEntry.getFromLevel(var3);
      Direction[] var7 = PROPAGATION_DIRECTIONS;
      int var8 = var7.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         Direction var10 = var7[var9];
         if (LightEngine.QueueEntry.shouldPropagateInDirection(var3, var10)) {
            long var11 = BlockPos.offset(var1, var10);
            if (((SkyLightSectionStorage)this.storage).storingLightForSection(SectionPos.blockToSection(var11))) {
               int var13 = ((SkyLightSectionStorage)this.storage).getStoredLevel(var11);
               if (var13 != 0) {
                  if (var13 <= var6 - 1) {
                     ((SkyLightSectionStorage)this.storage).setStoredLevel(var11, 0);
                     this.enqueueDecrease(var11, LightEngine.QueueEntry.decreaseSkipOneDirection(var13, var10.getOpposite()));
                     this.propagateFromEmptySections(var11, var10, var13, false, var5);
                  } else {
                     this.enqueueIncrease(var11, LightEngine.QueueEntry.increaseOnlyOneDirection(var13, false, var10.getOpposite()));
                  }
               }
            }
         }
      }

   }

   private int countEmptySectionsBelowIfAtBorder(long var1) {
      int var3 = BlockPos.getY(var1);
      int var4 = SectionPos.sectionRelative(var3);
      if (var4 != 0) {
         return 0;
      } else {
         int var5 = BlockPos.getX(var1);
         int var6 = BlockPos.getZ(var1);
         int var7 = SectionPos.sectionRelative(var5);
         int var8 = SectionPos.sectionRelative(var6);
         if (var7 != 0 && var7 != 15 && var8 != 0 && var8 != 15) {
            return 0;
         } else {
            int var9 = SectionPos.blockToSectionCoord(var5);
            int var10 = SectionPos.blockToSectionCoord(var3);
            int var11 = SectionPos.blockToSectionCoord(var6);

            int var12;
            for(var12 = 0; !((SkyLightSectionStorage)this.storage).storingLightForSection(SectionPos.asLong(var9, var10 - var12 - 1, var11)) && ((SkyLightSectionStorage)this.storage).hasLightDataAtOrBelow(var10 - var12 - 1); ++var12) {
            }

            return var12;
         }
      }
   }

   private void propagateFromEmptySections(long var1, Direction var3, int var4, boolean var5, int var6) {
      if (var6 != 0) {
         int var7 = BlockPos.getX(var1);
         int var8 = BlockPos.getZ(var1);
         if (crossedSectionEdge(var3, SectionPos.sectionRelative(var7), SectionPos.sectionRelative(var8))) {
            int var9 = BlockPos.getY(var1);
            int var10 = SectionPos.blockToSectionCoord(var7);
            int var11 = SectionPos.blockToSectionCoord(var8);
            int var12 = SectionPos.blockToSectionCoord(var9) - 1;
            int var13 = var12 - var6 + 1;

            while(true) {
               while(var12 >= var13) {
                  if (!((SkyLightSectionStorage)this.storage).storingLightForSection(SectionPos.asLong(var10, var12, var11))) {
                     --var12;
                  } else {
                     int var14 = SectionPos.sectionToBlockCoord(var12);

                     for(int var15 = 15; var15 >= 0; --var15) {
                        long var16 = BlockPos.asLong(var7, var14 + var15, var8);
                        if (var5) {
                           ((SkyLightSectionStorage)this.storage).setStoredLevel(var16, var4);
                           if (var4 > 1) {
                              this.enqueueIncrease(var16, LightEngine.QueueEntry.increaseSkipOneDirection(var4, true, var3.getOpposite()));
                           }
                        } else {
                           ((SkyLightSectionStorage)this.storage).setStoredLevel(var16, 0);
                           this.enqueueDecrease(var16, LightEngine.QueueEntry.decreaseSkipOneDirection(var4, var3.getOpposite()));
                        }
                     }

                     --var12;
                  }
               }

               return;
            }
         }
      }
   }

   private static boolean crossedSectionEdge(Direction var0, int var1, int var2) {
      boolean var10000;
      switch (var0) {
         case NORTH -> var10000 = var2 == 15;
         case SOUTH -> var10000 = var2 == 0;
         case WEST -> var10000 = var1 == 15;
         case EAST -> var10000 = var1 == 0;
         default -> var10000 = false;
      }

      return var10000;
   }

   public void setLightEnabled(ChunkPos var1, boolean var2) {
      super.setLightEnabled(var1, var2);
      if (var2) {
         ChunkSkyLightSources var3 = (ChunkSkyLightSources)Objects.requireNonNullElse(this.getChunkSources(var1.x, var1.z), this.emptyChunkSources);
         int var4 = var3.getHighestLowestSourceY() - 1;
         int var5 = SectionPos.blockToSectionCoord(var4) + 1;
         long var6 = SectionPos.getZeroNode(var1.x, var1.z);
         int var8 = ((SkyLightSectionStorage)this.storage).getTopSectionY(var6);
         int var9 = Math.max(((SkyLightSectionStorage)this.storage).getBottomSectionY(), var5);

         for(int var10 = var8 - 1; var10 >= var9; --var10) {
            DataLayer var11 = ((SkyLightSectionStorage)this.storage).getDataLayerToWrite(SectionPos.asLong(var1.x, var10, var1.z));
            if (var11 != null && var11.isEmpty()) {
               var11.fill(15);
            }
         }
      }

   }

   public void propagateLightSources(ChunkPos var1) {
      long var2 = SectionPos.getZeroNode(var1.x, var1.z);
      ((SkyLightSectionStorage)this.storage).setLightEnabled(var2, true);
      ChunkSkyLightSources var4 = (ChunkSkyLightSources)Objects.requireNonNullElse(this.getChunkSources(var1.x, var1.z), this.emptyChunkSources);
      ChunkSkyLightSources var5 = (ChunkSkyLightSources)Objects.requireNonNullElse(this.getChunkSources(var1.x, var1.z - 1), this.emptyChunkSources);
      ChunkSkyLightSources var6 = (ChunkSkyLightSources)Objects.requireNonNullElse(this.getChunkSources(var1.x, var1.z + 1), this.emptyChunkSources);
      ChunkSkyLightSources var7 = (ChunkSkyLightSources)Objects.requireNonNullElse(this.getChunkSources(var1.x - 1, var1.z), this.emptyChunkSources);
      ChunkSkyLightSources var8 = (ChunkSkyLightSources)Objects.requireNonNullElse(this.getChunkSources(var1.x + 1, var1.z), this.emptyChunkSources);
      int var9 = ((SkyLightSectionStorage)this.storage).getTopSectionY(var2);
      int var10 = ((SkyLightSectionStorage)this.storage).getBottomSectionY();
      int var11 = SectionPos.sectionToBlockCoord(var1.x);
      int var12 = SectionPos.sectionToBlockCoord(var1.z);

      for(int var13 = var9 - 1; var13 >= var10; --var13) {
         long var14 = SectionPos.asLong(var1.x, var13, var1.z);
         DataLayer var16 = ((SkyLightSectionStorage)this.storage).getDataLayerToWrite(var14);
         if (var16 != null) {
            int var17 = SectionPos.sectionToBlockCoord(var13);
            int var18 = var17 + 15;
            boolean var19 = false;

            for(int var20 = 0; var20 < 16; ++var20) {
               for(int var21 = 0; var21 < 16; ++var21) {
                  int var22 = var4.getLowestSourceY(var21, var20);
                  if (var22 <= var18) {
                     int var23 = var20 == 0 ? var5.getLowestSourceY(var21, 15) : var4.getLowestSourceY(var21, var20 - 1);
                     int var24 = var20 == 15 ? var6.getLowestSourceY(var21, 0) : var4.getLowestSourceY(var21, var20 + 1);
                     int var25 = var21 == 0 ? var7.getLowestSourceY(15, var20) : var4.getLowestSourceY(var21 - 1, var20);
                     int var26 = var21 == 15 ? var8.getLowestSourceY(0, var20) : var4.getLowestSourceY(var21 + 1, var20);
                     int var27 = Math.max(Math.max(var23, var24), Math.max(var25, var26));

                     for(int var28 = var18; var28 >= Math.max(var17, var22); --var28) {
                        var16.set(var21, SectionPos.sectionRelative(var28), var20, 15);
                        if (var28 == var22 || var28 < var27) {
                           long var29 = BlockPos.asLong(var11 + var21, var28, var12 + var20);
                           this.enqueueIncrease(var29, LightEngine.QueueEntry.increaseSkySourceInDirections(var28 == var22, var28 < var23, var28 < var24, var28 < var25, var28 < var26));
                        }
                     }

                     if (var22 < var17) {
                        var19 = true;
                     }
                  }
               }
            }

            if (!var19) {
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
