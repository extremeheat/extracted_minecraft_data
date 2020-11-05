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
   private static final Direction[] HORIZONTALS;
   private final LongSet sectionsWithSources = new LongOpenHashSet();
   private final LongSet sectionsToAddSourcesTo = new LongOpenHashSet();
   private final LongSet sectionsToRemoveSourcesFrom = new LongOpenHashSet();
   private final LongSet columnsWithSkySources = new LongOpenHashSet();
   private volatile boolean hasSourceInconsistencies;

   protected SkyLightSectionStorage(LightChunkGetter var1) {
      super(LightLayer.SKY, var1, new SkyLightSectionStorage.SkyDataLayerStorageMap(new Long2ObjectOpenHashMap(), new Long2IntOpenHashMap(), 2147483647));
   }

   protected int getLightValue(long var1) {
      long var3 = SectionPos.blockToSection(var1);
      int var5 = SectionPos.y(var3);
      SkyLightSectionStorage.SkyDataLayerStorageMap var6 = (SkyLightSectionStorage.SkyDataLayerStorageMap)this.visibleSectionData;
      int var7 = var6.topSections.get(SectionPos.getZeroNode(var3));
      if (var7 != var6.currentLowestY && var5 < var7) {
         DataLayer var8 = this.getDataLayer(var6, var3);
         if (var8 == null) {
            for(var1 = BlockPos.getFlatIndex(var1); var8 == null; var8 = this.getDataLayer(var6, var3)) {
               var3 = SectionPos.offset(var3, Direction.UP);
               ++var5;
               if (var5 >= var7) {
                  return 15;
               }

               var1 = BlockPos.offset(var1, 0, 16, 0);
            }
         }

         return var8.get(SectionPos.sectionRelative(BlockPos.getX(var1)), SectionPos.sectionRelative(BlockPos.getY(var1)), SectionPos.sectionRelative(BlockPos.getZ(var1)));
      } else {
         return 15;
      }
   }

   protected void onNodeAdded(long var1) {
      int var3 = SectionPos.y(var1);
      if (((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY > var3) {
         ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY = var3;
         ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.defaultReturnValue(((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY);
      }

      long var4 = SectionPos.getZeroNode(var1);
      int var6 = ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(var4);
      if (var6 < var3 + 1) {
         ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.put(var4, var3 + 1);
         if (this.columnsWithSkySources.contains(var4)) {
            this.queueAddSource(var1);
            if (var6 > ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY) {
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

   protected void onNodeRemoved(long var1) {
      long var3 = SectionPos.getZeroNode(var1);
      boolean var5 = this.columnsWithSkySources.contains(var3);
      if (var5) {
         this.queueRemoveSource(var1);
      }

      int var6 = SectionPos.y(var1);
      if (((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(var3) == var6 + 1) {
         long var7;
         for(var7 = var1; !this.storingLightForSection(var7) && this.hasSectionsBelow(var6); var7 = SectionPos.offset(var7, Direction.DOWN)) {
            --var6;
         }

         if (this.storingLightForSection(var7)) {
            ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.put(var3, var6 + 1);
            if (var5) {
               this.queueAddSource(var7);
            }
         } else {
            ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.remove(var3);
         }
      }

      if (var5) {
         this.recheckInconsistencyFlag();
      }

   }

   protected void enableLightSources(long var1, boolean var3) {
      this.runAllUpdates();
      if (var3 && this.columnsWithSkySources.add(var1)) {
         int var4 = ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(var1);
         if (var4 != ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY) {
            long var5 = SectionPos.asLong(SectionPos.x(var1), var4 - 1, SectionPos.z(var1));
            this.queueAddSource(var5);
            this.recheckInconsistencyFlag();
         }
      } else if (!var3) {
         this.columnsWithSkySources.remove(var1);
      }

   }

   protected boolean hasInconsistencies() {
      return super.hasInconsistencies() || this.hasSourceInconsistencies;
   }

   protected DataLayer createDataLayer(long var1) {
      DataLayer var3 = (DataLayer)this.queuedSections.get(var1);
      if (var3 != null) {
         return var3;
      } else {
         long var4 = SectionPos.offset(var1, Direction.UP);
         int var6 = ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(SectionPos.getZeroNode(var1));
         if (var6 != ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY && SectionPos.y(var4) < var6) {
            DataLayer var7;
            while((var7 = this.getDataLayer(var4, true)) == null) {
               var4 = SectionPos.offset(var4, Direction.UP);
            }

            return new DataLayer((new FlatDataLayer(var7, 0)).getData());
         } else {
            return new DataLayer();
         }
      }
   }

   protected void markNewInconsistencies(LayerLightEngine<SkyLightSectionStorage.SkyDataLayerStorageMap, ?> var1, boolean var2, boolean var3) {
      super.markNewInconsistencies(var1, var2, var3);
      if (var2) {
         LongIterator var4;
         long var5;
         int var7;
         int var8;
         if (!this.sectionsToAddSourcesTo.isEmpty()) {
            var4 = this.sectionsToAddSourcesTo.iterator();

            label160:
            while(true) {
               while(true) {
                  do {
                     do {
                        do {
                           if (!var4.hasNext()) {
                              break label160;
                           }

                           var5 = (Long)var4.next();
                           var7 = this.getLevel(var5);
                        } while(var7 == 2);
                     } while(this.sectionsToRemoveSourcesFrom.contains(var5));
                  } while(!this.sectionsWithSources.add(var5));

                  int var9;
                  if (var7 == 1) {
                     this.clearQueuedSectionBlocks(var1, var5);
                     if (this.changedSections.add(var5)) {
                        ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).copyDataLayer(var5);
                     }

                     Arrays.fill(this.getDataLayer(var5, true).getData(), (byte)-1);
                     var8 = SectionPos.sectionToBlockCoord(SectionPos.x(var5));
                     var9 = SectionPos.sectionToBlockCoord(SectionPos.y(var5));
                     int var24 = SectionPos.sectionToBlockCoord(SectionPos.z(var5));
                     Direction[] var11 = HORIZONTALS;
                     int var12 = var11.length;

                     long var15;
                     for(int var13 = 0; var13 < var12; ++var13) {
                        Direction var14 = var11[var13];
                        var15 = SectionPos.offset(var5, var14);
                        if ((this.sectionsToRemoveSourcesFrom.contains(var15) || !this.sectionsWithSources.contains(var15) && !this.sectionsToAddSourcesTo.contains(var15)) && this.storingLightForSection(var15)) {
                           for(int var17 = 0; var17 < 16; ++var17) {
                              for(int var18 = 0; var18 < 16; ++var18) {
                                 long var19;
                                 long var21;
                                 switch(var14) {
                                 case NORTH:
                                    var19 = BlockPos.asLong(var8 + var17, var9 + var18, var24);
                                    var21 = BlockPos.asLong(var8 + var17, var9 + var18, var24 - 1);
                                    break;
                                 case SOUTH:
                                    var19 = BlockPos.asLong(var8 + var17, var9 + var18, var24 + 16 - 1);
                                    var21 = BlockPos.asLong(var8 + var17, var9 + var18, var24 + 16);
                                    break;
                                 case WEST:
                                    var19 = BlockPos.asLong(var8, var9 + var17, var24 + var18);
                                    var21 = BlockPos.asLong(var8 - 1, var9 + var17, var24 + var18);
                                    break;
                                 default:
                                    var19 = BlockPos.asLong(var8 + 16 - 1, var9 + var17, var24 + var18);
                                    var21 = BlockPos.asLong(var8 + 16, var9 + var17, var24 + var18);
                                 }

                                 var1.checkEdge(var19, var21, var1.computeLevelFromNeighbor(var19, var21, 0), true);
                              }
                           }
                        }
                     }

                     for(int var25 = 0; var25 < 16; ++var25) {
                        for(var12 = 0; var12 < 16; ++var12) {
                           long var26 = BlockPos.asLong(SectionPos.sectionToBlockCoord(SectionPos.x(var5)) + var25, SectionPos.sectionToBlockCoord(SectionPos.y(var5)), SectionPos.sectionToBlockCoord(SectionPos.z(var5)) + var12);
                           var15 = BlockPos.asLong(SectionPos.sectionToBlockCoord(SectionPos.x(var5)) + var25, SectionPos.sectionToBlockCoord(SectionPos.y(var5)) - 1, SectionPos.sectionToBlockCoord(SectionPos.z(var5)) + var12);
                           var1.checkEdge(var26, var15, var1.computeLevelFromNeighbor(var26, var15, 0), true);
                        }
                     }
                  } else {
                     for(var8 = 0; var8 < 16; ++var8) {
                        for(var9 = 0; var9 < 16; ++var9) {
                           long var10 = BlockPos.asLong(SectionPos.sectionToBlockCoord(SectionPos.x(var5)) + var8, SectionPos.sectionToBlockCoord(SectionPos.y(var5)) + 16 - 1, SectionPos.sectionToBlockCoord(SectionPos.z(var5)) + var9);
                           var1.checkEdge(9223372036854775807L, var10, 0, true);
                        }
                     }
                  }
               }
            }
         }

         this.sectionsToAddSourcesTo.clear();
         if (!this.sectionsToRemoveSourcesFrom.isEmpty()) {
            var4 = this.sectionsToRemoveSourcesFrom.iterator();

            label90:
            while(true) {
               do {
                  do {
                     if (!var4.hasNext()) {
                        break label90;
                     }

                     var5 = (Long)var4.next();
                  } while(!this.sectionsWithSources.remove(var5));
               } while(!this.storingLightForSection(var5));

               for(var7 = 0; var7 < 16; ++var7) {
                  for(var8 = 0; var8 < 16; ++var8) {
                     long var23 = BlockPos.asLong(SectionPos.sectionToBlockCoord(SectionPos.x(var5)) + var7, SectionPos.sectionToBlockCoord(SectionPos.y(var5)) + 16 - 1, SectionPos.sectionToBlockCoord(SectionPos.z(var5)) + var8);
                     var1.checkEdge(9223372036854775807L, var23, 15, false);
                  }
               }
            }
         }

         this.sectionsToRemoveSourcesFrom.clear();
         this.hasSourceInconsistencies = false;
      }
   }

   protected boolean hasSectionsBelow(int var1) {
      return var1 >= ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY;
   }

   protected boolean hasLightSource(long var1) {
      int var3 = BlockPos.getY(var1);
      if ((var3 & 15) != 15) {
         return false;
      } else {
         long var4 = SectionPos.blockToSection(var1);
         long var6 = SectionPos.getZeroNode(var4);
         if (!this.columnsWithSkySources.contains(var6)) {
            return false;
         } else {
            int var8 = ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(var6);
            return SectionPos.sectionToBlockCoord(var8) == var3 + 16;
         }
      }
   }

   protected boolean isAboveData(long var1) {
      long var3 = SectionPos.getZeroNode(var1);
      int var5 = ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(var3);
      return var5 == ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY || SectionPos.y(var1) >= var5;
   }

   protected boolean lightOnInSection(long var1) {
      long var3 = SectionPos.getZeroNode(var1);
      return this.columnsWithSkySources.contains(var3);
   }

   static {
      HORIZONTALS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
   }

   public static final class SkyDataLayerStorageMap extends DataLayerStorageMap<SkyLightSectionStorage.SkyDataLayerStorageMap> {
      private int currentLowestY;
      private final Long2IntOpenHashMap topSections;

      public SkyDataLayerStorageMap(Long2ObjectOpenHashMap<DataLayer> var1, Long2IntOpenHashMap var2, int var3) {
         super(var1);
         this.topSections = var2;
         var2.defaultReturnValue(var3);
         this.currentLowestY = var3;
      }

      public SkyLightSectionStorage.SkyDataLayerStorageMap copy() {
         return new SkyLightSectionStorage.SkyDataLayerStorageMap(this.map.clone(), this.topSections.clone(), this.currentLowestY);
      }

      // $FF: synthetic method
      public DataLayerStorageMap copy() {
         return this.copy();
      }
   }
}
