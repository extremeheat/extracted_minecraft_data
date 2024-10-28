package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.world.level.chunk.DataLayer;

public abstract class DataLayerStorageMap<M extends DataLayerStorageMap<M>> {
   private static final int CACHE_SIZE = 2;
   private final long[] lastSectionKeys = new long[2];
   private final DataLayer[] lastSections = new DataLayer[2];
   private boolean cacheEnabled;
   protected final Long2ObjectOpenHashMap<DataLayer> map;

   protected DataLayerStorageMap(Long2ObjectOpenHashMap<DataLayer> var1) {
      super();
      this.map = var1;
      this.clearCache();
      this.cacheEnabled = true;
   }

   public abstract M copy();

   public DataLayer copyDataLayer(long var1) {
      DataLayer var3 = ((DataLayer)this.map.get(var1)).copy();
      this.map.put(var1, var3);
      this.clearCache();
      return var3;
   }

   public boolean hasLayer(long var1) {
      return this.map.containsKey(var1);
   }

   @Nullable
   public DataLayer getLayer(long var1) {
      if (this.cacheEnabled) {
         for(int var3 = 0; var3 < 2; ++var3) {
            if (var1 == this.lastSectionKeys[var3]) {
               return this.lastSections[var3];
            }
         }
      }

      DataLayer var5 = (DataLayer)this.map.get(var1);
      if (var5 == null) {
         return null;
      } else {
         if (this.cacheEnabled) {
            for(int var4 = 1; var4 > 0; --var4) {
               this.lastSectionKeys[var4] = this.lastSectionKeys[var4 - 1];
               this.lastSections[var4] = this.lastSections[var4 - 1];
            }

            this.lastSectionKeys[0] = var1;
            this.lastSections[0] = var5;
         }

         return var5;
      }
   }

   @Nullable
   public DataLayer removeLayer(long var1) {
      return (DataLayer)this.map.remove(var1);
   }

   public void setLayer(long var1, DataLayer var3) {
      this.map.put(var1, var3);
   }

   public void clearCache() {
      for(int var1 = 0; var1 < 2; ++var1) {
         this.lastSectionKeys[var1] = 9223372036854775807L;
         this.lastSections[var1] = null;
      }

   }

   public void disableCache() {
      this.cacheEnabled = false;
   }
}
