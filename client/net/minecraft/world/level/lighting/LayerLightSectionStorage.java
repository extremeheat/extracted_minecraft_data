package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.SectionTracker;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;

public abstract class LayerLightSectionStorage<M extends DataLayerStorageMap<M>> extends SectionTracker {
   protected static final DataLayer EMPTY_DATA = new DataLayer();
   private static final Direction[] DIRECTIONS = Direction.values();
   private final LightLayer layer;
   private final LightChunkGetter chunkSource;
   protected final LongSet dataSectionSet = new LongOpenHashSet();
   protected final LongSet toMarkNoData = new LongOpenHashSet();
   protected final LongSet toMarkData = new LongOpenHashSet();
   protected volatile M visibleSectionData;
   protected final M updatingSectionData;
   protected final LongSet changedSections = new LongOpenHashSet();
   protected final LongSet sectionsAffectedByLightUpdates = new LongOpenHashSet();
   protected final Long2ObjectMap<DataLayer> queuedSections = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap());
   private final LongSet untrustedSections = new LongOpenHashSet();
   private final LongSet columnsToRetainQueuedDataFor = new LongOpenHashSet();
   private final LongSet toRemove = new LongOpenHashSet();
   protected volatile boolean hasToRemove;

   protected LayerLightSectionStorage(LightLayer var1, LightChunkGetter var2, M var3) {
      super(3, 16, 256);
      this.layer = var1;
      this.chunkSource = var2;
      this.updatingSectionData = var3;
      this.visibleSectionData = var3.copy();
      this.visibleSectionData.disableCache();
   }

   protected boolean storingLightForSection(long var1) {
      return this.getDataLayer(var1, true) != null;
   }

   @Nullable
   protected DataLayer getDataLayer(long var1, boolean var3) {
      return this.getDataLayer(var3 ? this.updatingSectionData : this.visibleSectionData, var1);
   }

   @Nullable
   protected DataLayer getDataLayer(M var1, long var2) {
      return var1.getLayer(var2);
   }

   @Nullable
   public DataLayer getDataLayerData(long var1) {
      DataLayer var3 = (DataLayer)this.queuedSections.get(var1);
      return var3 != null ? var3 : this.getDataLayer(var1, false);
   }

   protected abstract int getLightValue(long var1);

   protected int getStoredLevel(long var1) {
      long var3 = SectionPos.blockToSection(var1);
      DataLayer var5 = this.getDataLayer(var3, true);
      return var5.get(SectionPos.sectionRelative(BlockPos.getX(var1)), SectionPos.sectionRelative(BlockPos.getY(var1)), SectionPos.sectionRelative(BlockPos.getZ(var1)));
   }

   protected void setStoredLevel(long var1, int var3) {
      long var4 = SectionPos.blockToSection(var1);
      if (this.changedSections.add(var4)) {
         this.updatingSectionData.copyDataLayer(var4);
      }

      DataLayer var6 = this.getDataLayer(var4, true);
      var6.set(SectionPos.sectionRelative(BlockPos.getX(var1)), SectionPos.sectionRelative(BlockPos.getY(var1)), SectionPos.sectionRelative(BlockPos.getZ(var1)), var3);

      for(int var7 = -1; var7 <= 1; ++var7) {
         for(int var8 = -1; var8 <= 1; ++var8) {
            for(int var9 = -1; var9 <= 1; ++var9) {
               this.sectionsAffectedByLightUpdates.add(SectionPos.blockToSection(BlockPos.offset(var1, var8, var9, var7)));
            }
         }
      }

   }

   protected int getLevel(long var1) {
      if (var1 == 9223372036854775807L) {
         return 2;
      } else if (this.dataSectionSet.contains(var1)) {
         return 0;
      } else {
         return !this.toRemove.contains(var1) && this.updatingSectionData.hasLayer(var1) ? 1 : 2;
      }
   }

   protected int getLevelFromSource(long var1) {
      if (this.toMarkNoData.contains(var1)) {
         return 2;
      } else {
         return !this.dataSectionSet.contains(var1) && !this.toMarkData.contains(var1) ? 2 : 0;
      }
   }

   protected void setLevel(long var1, int var3) {
      int var4 = this.getLevel(var1);
      if (var4 != 0 && var3 == 0) {
         this.dataSectionSet.add(var1);
         this.toMarkData.remove(var1);
      }

      if (var4 == 0 && var3 != 0) {
         this.dataSectionSet.remove(var1);
         this.toMarkNoData.remove(var1);
      }

      if (var4 >= 2 && var3 != 2) {
         if (this.toRemove.contains(var1)) {
            this.toRemove.remove(var1);
         } else {
            this.updatingSectionData.setLayer(var1, this.createDataLayer(var1));
            this.changedSections.add(var1);
            this.onNodeAdded(var1);

            for(int var5 = -1; var5 <= 1; ++var5) {
               for(int var6 = -1; var6 <= 1; ++var6) {
                  for(int var7 = -1; var7 <= 1; ++var7) {
                     this.sectionsAffectedByLightUpdates.add(SectionPos.blockToSection(BlockPos.offset(var1, var6, var7, var5)));
                  }
               }
            }
         }
      }

      if (var4 != 2 && var3 >= 2) {
         this.toRemove.add(var1);
      }

      this.hasToRemove = !this.toRemove.isEmpty();
   }

   protected DataLayer createDataLayer(long var1) {
      DataLayer var3 = (DataLayer)this.queuedSections.get(var1);
      return var3 != null ? var3 : new DataLayer();
   }

   protected void clearQueuedSectionBlocks(LayerLightEngine<?, ?> var1, long var2) {
      if (var1.getQueueSize() < 8192) {
         var1.removeIf((var2x) -> {
            return SectionPos.blockToSection(var2x) == var2;
         });
      } else {
         int var4 = SectionPos.sectionToBlockCoord(SectionPos.x(var2));
         int var5 = SectionPos.sectionToBlockCoord(SectionPos.y(var2));
         int var6 = SectionPos.sectionToBlockCoord(SectionPos.z(var2));

         for(int var7 = 0; var7 < 16; ++var7) {
            for(int var8 = 0; var8 < 16; ++var8) {
               for(int var9 = 0; var9 < 16; ++var9) {
                  long var10 = BlockPos.asLong(var4 + var7, var5 + var8, var6 + var9);
                  var1.removeFromQueue(var10);
               }
            }
         }

      }
   }

   protected boolean hasInconsistencies() {
      return this.hasToRemove;
   }

   protected void markNewInconsistencies(LayerLightEngine<M, ?> var1, boolean var2, boolean var3) {
      if (this.hasInconsistencies() || !this.queuedSections.isEmpty()) {
         LongIterator var4 = this.toRemove.iterator();

         long var5;
         DataLayer var8;
         while(var4.hasNext()) {
            var5 = (Long)var4.next();
            this.clearQueuedSectionBlocks(var1, var5);
            DataLayer var7 = (DataLayer)this.queuedSections.remove(var5);
            var8 = this.updatingSectionData.removeLayer(var5);
            if (this.columnsToRetainQueuedDataFor.contains(SectionPos.getZeroNode(var5))) {
               if (var7 != null) {
                  this.queuedSections.put(var5, var7);
               } else if (var8 != null) {
                  this.queuedSections.put(var5, var8);
               }
            }
         }

         this.updatingSectionData.clearCache();
         var4 = this.toRemove.iterator();

         while(var4.hasNext()) {
            var5 = (Long)var4.next();
            this.onNodeRemoved(var5);
         }

         this.toRemove.clear();
         this.hasToRemove = false;
         ObjectIterator var9 = this.queuedSections.long2ObjectEntrySet().iterator();

         long var6;
         Entry var10;
         while(var9.hasNext()) {
            var10 = (Entry)var9.next();
            var6 = var10.getLongKey();
            if (this.storingLightForSection(var6)) {
               var8 = (DataLayer)var10.getValue();
               if (this.updatingSectionData.getLayer(var6) != var8) {
                  this.clearQueuedSectionBlocks(var1, var6);
                  this.updatingSectionData.setLayer(var6, var8);
                  this.changedSections.add(var6);
               }
            }
         }

         this.updatingSectionData.clearCache();
         if (!var3) {
            var4 = this.queuedSections.keySet().iterator();

            while(var4.hasNext()) {
               var5 = (Long)var4.next();
               this.checkEdgesForSection(var1, var5);
            }
         } else {
            var4 = this.untrustedSections.iterator();

            while(var4.hasNext()) {
               var5 = (Long)var4.next();
               this.checkEdgesForSection(var1, var5);
            }
         }

         this.untrustedSections.clear();
         var9 = this.queuedSections.long2ObjectEntrySet().iterator();

         while(var9.hasNext()) {
            var10 = (Entry)var9.next();
            var6 = var10.getLongKey();
            if (this.storingLightForSection(var6)) {
               var9.remove();
            }
         }

      }
   }

   private void checkEdgesForSection(LayerLightEngine<M, ?> var1, long var2) {
      if (this.storingLightForSection(var2)) {
         int var4 = SectionPos.sectionToBlockCoord(SectionPos.x(var2));
         int var5 = SectionPos.sectionToBlockCoord(SectionPos.y(var2));
         int var6 = SectionPos.sectionToBlockCoord(SectionPos.z(var2));
         Direction[] var7 = DIRECTIONS;
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            Direction var10 = var7[var9];
            long var11 = SectionPos.offset(var2, var10);
            if (!this.queuedSections.containsKey(var11) && this.storingLightForSection(var11)) {
               for(int var13 = 0; var13 < 16; ++var13) {
                  for(int var14 = 0; var14 < 16; ++var14) {
                     long var15;
                     long var17;
                     switch(var10) {
                     case DOWN:
                        var15 = BlockPos.asLong(var4 + var14, var5, var6 + var13);
                        var17 = BlockPos.asLong(var4 + var14, var5 - 1, var6 + var13);
                        break;
                     case UP:
                        var15 = BlockPos.asLong(var4 + var14, var5 + 16 - 1, var6 + var13);
                        var17 = BlockPos.asLong(var4 + var14, var5 + 16, var6 + var13);
                        break;
                     case NORTH:
                        var15 = BlockPos.asLong(var4 + var13, var5 + var14, var6);
                        var17 = BlockPos.asLong(var4 + var13, var5 + var14, var6 - 1);
                        break;
                     case SOUTH:
                        var15 = BlockPos.asLong(var4 + var13, var5 + var14, var6 + 16 - 1);
                        var17 = BlockPos.asLong(var4 + var13, var5 + var14, var6 + 16);
                        break;
                     case WEST:
                        var15 = BlockPos.asLong(var4, var5 + var13, var6 + var14);
                        var17 = BlockPos.asLong(var4 - 1, var5 + var13, var6 + var14);
                        break;
                     default:
                        var15 = BlockPos.asLong(var4 + 16 - 1, var5 + var13, var6 + var14);
                        var17 = BlockPos.asLong(var4 + 16, var5 + var13, var6 + var14);
                     }

                     var1.checkEdge(var15, var17, var1.computeLevelFromNeighbor(var15, var17, var1.getLevel(var15)), false);
                     var1.checkEdge(var17, var15, var1.computeLevelFromNeighbor(var17, var15, var1.getLevel(var17)), false);
                  }
               }
            }
         }

      }
   }

   protected void onNodeAdded(long var1) {
   }

   protected void onNodeRemoved(long var1) {
   }

   protected void enableLightSources(long var1, boolean var3) {
   }

   public void retainData(long var1, boolean var3) {
      if (var3) {
         this.columnsToRetainQueuedDataFor.add(var1);
      } else {
         this.columnsToRetainQueuedDataFor.remove(var1);
      }

   }

   protected void queueSectionData(long var1, @Nullable DataLayer var3, boolean var4) {
      if (var3 != null) {
         this.queuedSections.put(var1, var3);
         if (!var4) {
            this.untrustedSections.add(var1);
         }
      } else {
         this.queuedSections.remove(var1);
      }

   }

   protected void updateSectionStatus(long var1, boolean var3) {
      boolean var4 = this.dataSectionSet.contains(var1);
      if (!var4 && !var3) {
         this.toMarkData.add(var1);
         this.checkEdge(9223372036854775807L, var1, 0, true);
      }

      if (var4 && var3) {
         this.toMarkNoData.add(var1);
         this.checkEdge(9223372036854775807L, var1, 2, false);
      }

   }

   protected void runAllUpdates() {
      if (this.hasWork()) {
         this.runUpdates(2147483647);
      }

   }

   protected void swapSectionMap() {
      if (!this.changedSections.isEmpty()) {
         DataLayerStorageMap var1 = this.updatingSectionData.copy();
         var1.disableCache();
         this.visibleSectionData = var1;
         this.changedSections.clear();
      }

      if (!this.sectionsAffectedByLightUpdates.isEmpty()) {
         LongIterator var4 = this.sectionsAffectedByLightUpdates.iterator();

         while(var4.hasNext()) {
            long var2 = var4.nextLong();
            this.chunkSource.onLightUpdate(this.layer, SectionPos.of(var2));
         }

         this.sectionsAffectedByLightUpdates.clear();
      }

   }
}
