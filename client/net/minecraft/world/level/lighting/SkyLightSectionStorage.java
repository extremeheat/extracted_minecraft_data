package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;

public class SkyLightSectionStorage extends LayerLightSectionStorage<SkyLightSectionStorage.SkyDataLayerStorageMap> {
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
      }
   }

   @Override
   protected void onNodeRemoved(long var1) {
      long var3 = SectionPos.getZeroNode(var1);
      int var5 = SectionPos.y(var1);
      if (this.updatingSectionData.topSections.get(var3) == var5 + 1) {
         long var6;
         for(var6 = var1; !this.storingLightForSection(var6) && this.hasLightDataAtOrBelow(var5); var6 = SectionPos.offset(var6, Direction.DOWN)) {
            --var5;
         }

         if (this.storingLightForSection(var6)) {
            this.updatingSectionData.topSections.put(var3, var5 + 1);
         } else {
            this.updatingSectionData.topSections.remove(var3);
         }
      }
   }

   @Override
   protected DataLayer createDataLayer(long var1) {
      DataLayer var3 = (DataLayer)this.queuedSections.get(var1);
      if (var3 != null) {
         return var3;
      } else {
         int var4 = this.updatingSectionData.topSections.get(SectionPos.getZeroNode(var1));
         if (var4 != this.updatingSectionData.currentLowestY && SectionPos.y(var1) < var4) {
            long var5 = SectionPos.offset(var1, Direction.UP);

            DataLayer var7;
            while((var7 = this.getDataLayer(var5, true)) == null) {
               var5 = SectionPos.offset(var5, Direction.UP);
            }

            return repeatFirstLayer(var7);
         } else {
            return this.lightOnInSection(var1) ? new DataLayer(15) : new DataLayer();
         }
      }
   }

   private static DataLayer repeatFirstLayer(DataLayer var0) {
      if (var0.isDefinitelyHomogenous()) {
         return var0.copy();
      } else {
         byte[] var1 = var0.getData();
         byte[] var2 = new byte[2048];

         for(int var3 = 0; var3 < 16; ++var3) {
            System.arraycopy(var1, 0, var2, var3 * 128, 128);
         }

         return new DataLayer(var2);
      }
   }

   protected boolean hasLightDataAtOrBelow(int var1) {
      return var1 >= this.updatingSectionData.currentLowestY;
   }

   protected boolean isAboveData(long var1) {
      long var3 = SectionPos.getZeroNode(var1);
      int var5 = this.updatingSectionData.topSections.get(var3);
      return var5 == this.updatingSectionData.currentLowestY || SectionPos.y(var1) >= var5;
   }

   protected int getTopSectionY(long var1) {
      return this.updatingSectionData.topSections.get(var1);
   }

   protected int getBottomSectionY() {
      return this.updatingSectionData.currentLowestY;
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
