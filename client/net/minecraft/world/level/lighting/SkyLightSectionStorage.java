package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;

public class SkyLightSectionStorage extends LayerLightSectionStorage<SkyLightSectionStorage.SkyDataLayerStorageMap> {
   private static final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
   private final LongSet sectionsWithSources = new LongOpenHashSet();
   private final LongSet sectionsToAddSourcesTo = new LongOpenHashSet();
   private final LongSet sectionsToRemoveSourcesFrom = new LongOpenHashSet();
   private final LongSet columnsWithSkySources = new LongOpenHashSet();
   private volatile boolean hasSourceInconsistencies;

   protected SkyLightSectionStorage(LightChunkGetter var1) {
      super(LightLayer.SKY, var1, new SkyLightSectionStorage.SkyDataLayerStorageMap(new Long2ObjectOpenHashMap(), new Long2IntOpenHashMap(), 2147483647));
   }

   @Override
   protected int getLightValue(long var1) {
      return this.getLightValue(var1, false);
   }

   protected int getLightValue(long var1, boolean var3) {
      long var4 = SectionPos.blockToSection(var1);
      int var6 = SectionPos.y(var4);
      SkyLightSectionStorage.SkyDataLayerStorageMap var7 = var3 ? this.updatingSectionData : this.visibleSectionData;
      int var8 = var7.topSections.get(SectionPos.getZeroNode(var4));
      if (var8 != var7.currentLowestY && var6 < var8) {
         DataLayer var9 = this.getDataLayer(var7, var4);
         if (var9 == null) {
            for(var1 = BlockPos.getFlatIndex(var1); var9 == null; var9 = this.getDataLayer(var7, var4)) {
               if (++var6 >= var8) {
                  return 15;
               }

               var1 = BlockPos.offset(var1, 0, 16, 0);
               var4 = SectionPos.offset(var4, Direction.UP);
            }
         }

         return var9.get(
            SectionPos.sectionRelative(BlockPos.getX(var1)), SectionPos.sectionRelative(BlockPos.getY(var1)), SectionPos.sectionRelative(BlockPos.getZ(var1))
         );
      } else {
         return var3 && !this.lightOnInSection(var4) ? 0 : 15;
      }
   }

   @Override
   protected void onNodeAdded(long var1) {
      int var3 = SectionPos.y(var1);
      if (this.updatingSectionData.currentLowestY > var3) {
         this.updatingSectionData.currentLowestY = var3;
         this.updatingSectionData.topSections.defaultReturnValue(this.updatingSectionData.currentLowestY);
      }

      long var4 = SectionPos.getZeroNode(var1);
      int var6 = this.updatingSectionData.topSections.get(var4);
      if (var6 < var3 + 1) {
         this.updatingSectionData.topSections.put(var4, var3 + 1);
         if (this.columnsWithSkySources.contains(var4)) {
            this.queueAddSource(var1);
            if (var6 > this.updatingSectionData.currentLowestY) {
               long var7 = SectionPos.asLong(SectionPos.x(var1), var6 - 1, SectionPos.z(var1));
               this.queueRemoveSource(var7);
            }

            this.recheckInconsistencyFlag();
         }
      }
   }

   private void queueRemoveSource(long var1) {
      this.sectionsToRemoveSourcesFrom.add(var1);
      this.sectionsToAddSourcesTo.remove(var1);
   }

   private void queueAddSource(long var1) {
      this.sectionsToAddSourcesTo.add(var1);
      this.sectionsToRemoveSourcesFrom.remove(var1);
   }

   private void recheckInconsistencyFlag() {
      this.hasSourceInconsistencies = !this.sectionsToAddSourcesTo.isEmpty() || !this.sectionsToRemoveSourcesFrom.isEmpty();
   }

   @Override
   protected void onNodeRemoved(long var1) {
      long var3 = SectionPos.getZeroNode(var1);
      boolean var5 = this.columnsWithSkySources.contains(var3);
      if (var5) {
         this.queueRemoveSource(var1);
      }

      int var6 = SectionPos.y(var1);
      if (this.updatingSectionData.topSections.get(var3) == var6 + 1) {
         long var7;
         for(var7 = var1; !this.storingLightForSection(var7) && this.hasSectionsBelow(var6); var7 = SectionPos.offset(var7, Direction.DOWN)) {
            --var6;
         }

         if (this.storingLightForSection(var7)) {
            this.updatingSectionData.topSections.put(var3, var6 + 1);
            if (var5) {
               this.queueAddSource(var7);
            }
         } else {
            this.updatingSectionData.topSections.remove(var3);
         }
      }

      if (var5) {
         this.recheckInconsistencyFlag();
      }
   }

   @Override
   protected void enableLightSources(long var1, boolean var3) {
      this.runAllUpdates();
      if (var3 && this.columnsWithSkySources.add(var1)) {
         int var4 = this.updatingSectionData.topSections.get(var1);
         if (var4 != this.updatingSectionData.currentLowestY) {
            long var5 = SectionPos.asLong(SectionPos.x(var1), var4 - 1, SectionPos.z(var1));
            this.queueAddSource(var5);
            this.recheckInconsistencyFlag();
         }
      } else if (!var3) {
         this.columnsWithSkySources.remove(var1);
      }
   }

   @Override
   protected boolean hasInconsistencies() {
      return super.hasInconsistencies() || this.hasSourceInconsistencies;
   }

   @Override
   protected DataLayer createDataLayer(long var1) {
      DataLayer var3 = (DataLayer)this.queuedSections.get(var1);
      if (var3 != null) {
         return var3;
      } else {
         long var4 = SectionPos.offset(var1, Direction.UP);
         int var6 = this.updatingSectionData.topSections.get(SectionPos.getZeroNode(var1));
         if (var6 != this.updatingSectionData.currentLowestY && SectionPos.y(var4) < var6) {
            DataLayer var7;
            while((var7 = this.getDataLayer(var4, true)) == null) {
               var4 = SectionPos.offset(var4, Direction.UP);
            }

            return repeatFirstLayer(var7);
         } else {
            return new DataLayer();
         }
      }
   }

   private static DataLayer repeatFirstLayer(DataLayer var0) {
      if (var0.isEmpty()) {
         return new DataLayer();
      } else {
         byte[] var1 = var0.getData();
         byte[] var2 = new byte[2048];

         for(int var3 = 0; var3 < 16; ++var3) {
            System.arraycopy(var1, 0, var2, var3 * 128, 128);
         }

         return new DataLayer(var2);
      }
   }

   @Override
   protected void markNewInconsistencies(LayerLightEngine<SkyLightSectionStorage.SkyDataLayerStorageMap, ?> var1, boolean var2, boolean var3) {
      super.markNewInconsistencies(var1, var2, var3);
      if (var2) {
         if (!this.sectionsToAddSourcesTo.isEmpty()) {
            LongIterator var4 = this.sectionsToAddSourcesTo.iterator();

            while(var4.hasNext()) {
               long var5 = var4.next();
               int var7 = this.getLevel(var5);
               if (var7 != 2 && !this.sectionsToRemoveSourcesFrom.contains(var5) && this.sectionsWithSources.add(var5)) {
                  if (var7 == 1) {
                     this.clearQueuedSectionBlocks(var1, var5);
                     if (this.changedSections.add(var5)) {
                        this.updatingSectionData.copyDataLayer(var5);
                     }

                     Arrays.fill(this.getDataLayer(var5, true).getData(), (byte)-1);
                     int var26 = SectionPos.sectionToBlockCoord(SectionPos.x(var5));
                     int var28 = SectionPos.sectionToBlockCoord(SectionPos.y(var5));
                     int var30 = SectionPos.sectionToBlockCoord(SectionPos.z(var5));

                     for(Direction var14 : HORIZONTALS) {
                        long var15 = SectionPos.offset(var5, var14);
                        if ((
                              this.sectionsToRemoveSourcesFrom.contains(var15)
                                 || !this.sectionsWithSources.contains(var15) && !this.sectionsToAddSourcesTo.contains(var15)
                           )
                           && this.storingLightForSection(var15)) {
                           for(int var17 = 0; var17 < 16; ++var17) {
                              for(int var18 = 0; var18 < 16; ++var18) {
                                 long var19;
                                 long var21;
                                 switch(var14) {
                                    case NORTH:
                                       var19 = BlockPos.asLong(var26 + var17, var28 + var18, var30);
                                       var21 = BlockPos.asLong(var26 + var17, var28 + var18, var30 - 1);
                                       break;
                                    case SOUTH:
                                       var19 = BlockPos.asLong(var26 + var17, var28 + var18, var30 + 16 - 1);
                                       var21 = BlockPos.asLong(var26 + var17, var28 + var18, var30 + 16);
                                       break;
                                    case WEST:
                                       var19 = BlockPos.asLong(var26, var28 + var17, var30 + var18);
                                       var21 = BlockPos.asLong(var26 - 1, var28 + var17, var30 + var18);
                                       break;
                                    default:
                                       var19 = BlockPos.asLong(var26 + 16 - 1, var28 + var17, var30 + var18);
                                       var21 = BlockPos.asLong(var26 + 16, var28 + var17, var30 + var18);
                                 }

                                 var1.checkEdge(var19, var21, var1.computeLevelFromNeighbor(var19, var21, 0), true);
                              }
                           }
                        }
                     }

                     for(int var31 = 0; var31 < 16; ++var31) {
                        for(int var32 = 0; var32 < 16; ++var32) {
                           long var33 = BlockPos.asLong(
                              SectionPos.sectionToBlockCoord(SectionPos.x(var5), var31),
                              SectionPos.sectionToBlockCoord(SectionPos.y(var5)),
                              SectionPos.sectionToBlockCoord(SectionPos.z(var5), var32)
                           );
                           long var34 = BlockPos.asLong(
                              SectionPos.sectionToBlockCoord(SectionPos.x(var5), var31),
                              SectionPos.sectionToBlockCoord(SectionPos.y(var5)) - 1,
                              SectionPos.sectionToBlockCoord(SectionPos.z(var5), var32)
                           );
                           var1.checkEdge(var33, var34, var1.computeLevelFromNeighbor(var33, var34, 0), true);
                        }
                     }
                  } else {
                     for(int var8 = 0; var8 < 16; ++var8) {
                        for(int var9 = 0; var9 < 16; ++var9) {
                           long var10 = BlockPos.asLong(
                              SectionPos.sectionToBlockCoord(SectionPos.x(var5), var8),
                              SectionPos.sectionToBlockCoord(SectionPos.y(var5), 15),
                              SectionPos.sectionToBlockCoord(SectionPos.z(var5), var9)
                           );
                           var1.checkEdge(9223372036854775807L, var10, 0, true);
                        }
                     }
                  }
               }
            }
         }

         this.sectionsToAddSourcesTo.clear();
         if (!this.sectionsToRemoveSourcesFrom.isEmpty()) {
            LongIterator var23 = this.sectionsToRemoveSourcesFrom.iterator();

            while(var23.hasNext()) {
               long var24 = var23.next();
               if (this.sectionsWithSources.remove(var24) && this.storingLightForSection(var24)) {
                  for(int var25 = 0; var25 < 16; ++var25) {
                     for(int var27 = 0; var27 < 16; ++var27) {
                        long var29 = BlockPos.asLong(
                           SectionPos.sectionToBlockCoord(SectionPos.x(var24), var25),
                           SectionPos.sectionToBlockCoord(SectionPos.y(var24), 15),
                           SectionPos.sectionToBlockCoord(SectionPos.z(var24), var27)
                        );
                        var1.checkEdge(9223372036854775807L, var29, 15, false);
                     }
                  }
               }
            }
         }

         this.sectionsToRemoveSourcesFrom.clear();
         this.hasSourceInconsistencies = false;
      }
   }

   protected boolean hasSectionsBelow(int var1) {
      return var1 >= this.updatingSectionData.currentLowestY;
   }

   protected boolean isAboveData(long var1) {
      long var3 = SectionPos.getZeroNode(var1);
      int var5 = this.updatingSectionData.topSections.get(var3);
      return var5 == this.updatingSectionData.currentLowestY || SectionPos.y(var1) >= var5;
   }

   protected boolean lightOnInSection(long var1) {
      long var3 = SectionPos.getZeroNode(var1);
      return this.columnsWithSkySources.contains(var3);
   }

   protected static final class SkyDataLayerStorageMap extends DataLayerStorageMap<SkyLightSectionStorage.SkyDataLayerStorageMap> {
      int currentLowestY;
      final Long2IntOpenHashMap topSections;

      public SkyDataLayerStorageMap(Long2ObjectOpenHashMap<DataLayer> var1, Long2IntOpenHashMap var2, int var3) {
         super(var1);
         this.topSections = var2;
         var2.defaultReturnValue(var3);
         this.currentLowestY = var3;
      }

      public SkyLightSectionStorage.SkyDataLayerStorageMap copy() {
         return new SkyLightSectionStorage.SkyDataLayerStorageMap(this.map.clone(), this.topSections.clone(), this.currentLowestY);
      }
   }
}
