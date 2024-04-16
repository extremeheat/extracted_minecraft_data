package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
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
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;

public abstract class LayerLightSectionStorage<M extends DataLayerStorageMap<M>> {
   private final LightLayer layer;
   protected final LightChunkGetter chunkSource;
   protected final Long2ByteMap sectionStates = new Long2ByteOpenHashMap();
   private final LongSet columnsWithSources = new LongOpenHashSet();
   protected volatile M visibleSectionData;
   protected final M updatingSectionData;
   protected final LongSet changedSections = new LongOpenHashSet();
   protected final LongSet sectionsAffectedByLightUpdates = new LongOpenHashSet();
   protected final Long2ObjectMap<DataLayer> queuedSections = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap());
   private final LongSet columnsToRetainQueuedDataFor = new LongOpenHashSet();
   private final LongSet toRemove = new LongOpenHashSet();
   protected volatile boolean hasInconsistencies;

   protected LayerLightSectionStorage(LightLayer var1, LightChunkGetter var2, M var3) {
      super();
      this.layer = var1;
      this.chunkSource = var2;
      this.updatingSectionData = (M)var3;
      this.visibleSectionData = (M)var3.copy();
      this.visibleSectionData.disableCache();
      this.sectionStates.defaultReturnValue((byte)0);
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
   protected DataLayer getDataLayerToWrite(long var1) {
      DataLayer var3 = this.updatingSectionData.getLayer(var1);
      if (var3 == null) {
         return null;
      } else {
         if (this.changedSections.add(var1)) {
            var3 = var3.copy();
            this.updatingSectionData.setLayer(var1, var3);
            this.updatingSectionData.clearCache();
         }

         return var3;
      }
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
      return var5.get(
         SectionPos.sectionRelative(BlockPos.getX(var1)), SectionPos.sectionRelative(BlockPos.getY(var1)), SectionPos.sectionRelative(BlockPos.getZ(var1))
      );
   }

   protected void setStoredLevel(long var1, int var3) {
      long var4 = SectionPos.blockToSection(var1);
      DataLayer var6;
      if (this.changedSections.add(var4)) {
         var6 = this.updatingSectionData.copyDataLayer(var4);
      } else {
         var6 = this.getDataLayer(var4, true);
      }

      var6.set(
         SectionPos.sectionRelative(BlockPos.getX(var1)),
         SectionPos.sectionRelative(BlockPos.getY(var1)),
         SectionPos.sectionRelative(BlockPos.getZ(var1)),
         var3
      );
      SectionPos.aroundAndAtBlockPos(var1, this.sectionsAffectedByLightUpdates::add);
   }

   protected void markSectionAndNeighborsAsAffected(long var1) {
      int var3 = SectionPos.x(var1);
      int var4 = SectionPos.y(var1);
      int var5 = SectionPos.z(var1);

      for (int var6 = -1; var6 <= 1; var6++) {
         for (int var7 = -1; var7 <= 1; var7++) {
            for (int var8 = -1; var8 <= 1; var8++) {
               this.sectionsAffectedByLightUpdates.add(SectionPos.asLong(var3 + var7, var4 + var8, var5 + var6));
            }
         }
      }
   }

   protected DataLayer createDataLayer(long var1) {
      DataLayer var3 = (DataLayer)this.queuedSections.get(var1);
      return var3 != null ? var3 : new DataLayer();
   }

   protected boolean hasInconsistencies() {
      return this.hasInconsistencies;
   }

   protected void markNewInconsistencies(LightEngine<M, ?> var1) {
      if (this.hasInconsistencies) {
         this.hasInconsistencies = false;
         LongIterator var2 = this.toRemove.iterator();

         while (var2.hasNext()) {
            long var3 = (Long)var2.next();
            DataLayer var5 = (DataLayer)this.queuedSections.remove(var3);
            DataLayer var6 = this.updatingSectionData.removeLayer(var3);
            if (this.columnsToRetainQueuedDataFor.contains(SectionPos.getZeroNode(var3))) {
               if (var5 != null) {
                  this.queuedSections.put(var3, var5);
               } else if (var6 != null) {
                  this.queuedSections.put(var3, var6);
               }
            }
         }

         this.updatingSectionData.clearCache();
         var2 = this.toRemove.iterator();

         while (var2.hasNext()) {
            long var9 = (Long)var2.next();
            this.onNodeRemoved(var9);
            this.changedSections.add(var9);
         }

         this.toRemove.clear();
         ObjectIterator var8 = Long2ObjectMaps.fastIterator(this.queuedSections);

         while (var8.hasNext()) {
            Entry var10 = (Entry)var8.next();
            long var4 = var10.getLongKey();
            if (this.storingLightForSection(var4)) {
               DataLayer var11 = (DataLayer)var10.getValue();
               if (this.updatingSectionData.getLayer(var4) != var11) {
                  this.updatingSectionData.setLayer(var4, var11);
                  this.changedSections.add(var4);
               }

               var8.remove();
            }
         }

         this.updatingSectionData.clearCache();
      }
   }

   protected void onNodeAdded(long var1) {
   }

   protected void onNodeRemoved(long var1) {
   }

   protected void setLightEnabled(long var1, boolean var3) {
      if (var3) {
         this.columnsWithSources.add(var1);
      } else {
         this.columnsWithSources.remove(var1);
      }
   }

   protected boolean lightOnInSection(long var1) {
      long var3 = SectionPos.getZeroNode(var1);
      return this.columnsWithSources.contains(var3);
   }

   public void retainData(long var1, boolean var3) {
      if (var3) {
         this.columnsToRetainQueuedDataFor.add(var1);
      } else {
         this.columnsToRetainQueuedDataFor.remove(var1);
      }
   }

   protected void queueSectionData(long var1, @Nullable DataLayer var3) {
      if (var3 != null) {
         this.queuedSections.put(var1, var3);
         this.hasInconsistencies = true;
      } else {
         this.queuedSections.remove(var1);
      }
   }

   protected void updateSectionStatus(long var1, boolean var3) {
      byte var4 = this.sectionStates.get(var1);
      byte var5 = LayerLightSectionStorage.SectionState.hasData(var4, !var3);
      if (var4 != var5) {
         this.putSectionState(var1, var5);
         int var6 = var3 ? -1 : 1;

         for (int var7 = -1; var7 <= 1; var7++) {
            for (int var8 = -1; var8 <= 1; var8++) {
               for (int var9 = -1; var9 <= 1; var9++) {
                  if (var7 != 0 || var8 != 0 || var9 != 0) {
                     long var10 = SectionPos.offset(var1, var7, var8, var9);
                     byte var12 = this.sectionStates.get(var10);
                     this.putSectionState(
                        var10, LayerLightSectionStorage.SectionState.neighborCount(var12, LayerLightSectionStorage.SectionState.neighborCount(var12) + var6)
                     );
                  }
               }
            }
         }
      }
   }

   protected void putSectionState(long var1, byte var3) {
      if (var3 != 0) {
         if (this.sectionStates.put(var1, var3) == 0) {
            this.initializeSection(var1);
         }
      } else if (this.sectionStates.remove(var1) != 0) {
         this.removeSection(var1);
      }
   }

   private void initializeSection(long var1) {
      if (!this.toRemove.remove(var1)) {
         this.updatingSectionData.setLayer(var1, this.createDataLayer(var1));
         this.changedSections.add(var1);
         this.onNodeAdded(var1);
         this.markSectionAndNeighborsAsAffected(var1);
         this.hasInconsistencies = true;
      }
   }

   private void removeSection(long var1) {
      this.toRemove.add(var1);
      this.hasInconsistencies = true;
   }

   protected void swapSectionMap() {
      if (!this.changedSections.isEmpty()) {
         DataLayerStorageMap var1 = this.updatingSectionData.copy();
         var1.disableCache();
         this.visibleSectionData = (M)var1;
         this.changedSections.clear();
      }

      if (!this.sectionsAffectedByLightUpdates.isEmpty()) {
         LongIterator var4 = this.sectionsAffectedByLightUpdates.iterator();

         while (var4.hasNext()) {
            long var2 = var4.nextLong();
            this.chunkSource.onLightUpdate(this.layer, SectionPos.of(var2));
         }

         this.sectionsAffectedByLightUpdates.clear();
      }
   }

   public LayerLightSectionStorage.SectionType getDebugSectionType(long var1) {
      return LayerLightSectionStorage.SectionState.type(this.sectionStates.get(var1));
   }

   protected static class SectionState {
      public static final byte EMPTY = 0;
      private static final int MIN_NEIGHBORS = 0;
      private static final int MAX_NEIGHBORS = 26;
      private static final byte HAS_DATA_BIT = 32;
      private static final byte NEIGHBOR_COUNT_BITS = 31;

      protected SectionState() {
         super();
      }

      public static byte hasData(byte var0, boolean var1) {
         return (byte)(var1 ? var0 | 32 : var0 & -33);
      }

      public static byte neighborCount(byte var0, int var1) {
         if (var1 >= 0 && var1 <= 26) {
            return (byte)(var0 & -32 | var1 & 31);
         } else {
            throw new IllegalArgumentException("Neighbor count was not within range [0; 26]");
         }
      }

      public static boolean hasData(byte var0) {
         return (var0 & 32) != 0;
      }

      public static int neighborCount(byte var0) {
         return var0 & 31;
      }

      public static LayerLightSectionStorage.SectionType type(byte var0) {
         if (var0 == 0) {
            return LayerLightSectionStorage.SectionType.EMPTY;
         } else {
            return hasData(var0) ? LayerLightSectionStorage.SectionType.LIGHT_AND_DATA : LayerLightSectionStorage.SectionType.LIGHT_ONLY;
         }
      }
   }

   public static enum SectionType {
      EMPTY("2"),
      LIGHT_ONLY("1"),
      LIGHT_AND_DATA("0");

      private final String display;

      private SectionType(final String nullxx) {
         this.display = nullxx;
      }

      public String display() {
         return this.display;
      }
   }
}
