package net.minecraft.world.level.saveddata.maps;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

public class MapIndex extends SavedData {
   public static final String FILE_NAME = "idcounts";
   private final Object2IntMap<String> usedAuxIds = new Object2IntOpenHashMap();

   public static SavedData.Factory<MapIndex> factory() {
      return new SavedData.Factory<>(MapIndex::new, MapIndex::load, DataFixTypes.SAVED_DATA_MAP_INDEX);
   }

   public MapIndex() {
      super();
      this.usedAuxIds.defaultReturnValue(-1);
   }

   public static MapIndex load(CompoundTag var0) {
      MapIndex var1 = new MapIndex();

      for(String var3 : var0.getAllKeys()) {
         if (var0.contains(var3, 99)) {
            var1.usedAuxIds.put(var3, var0.getInt(var3));
         }
      }

      return var1;
   }

   @Override
   public CompoundTag save(CompoundTag var1) {
      ObjectIterator var2 = this.usedAuxIds.object2IntEntrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         var1.putInt((String)var3.getKey(), var3.getIntValue());
      }

      return var1;
   }

   public int getFreeAuxValueForMap() {
      int var1 = this.usedAuxIds.getInt("map") + 1;
      this.usedAuxIds.put("map", var1);
      this.setDirty();
      return var1;
   }
}
