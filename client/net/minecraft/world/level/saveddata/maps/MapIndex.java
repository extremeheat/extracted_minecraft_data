package net.minecraft.world.level.saveddata.maps;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.core.HolderLookup;
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

   public static MapIndex load(CompoundTag var0, HolderLookup.Provider var1) {
      MapIndex var2 = new MapIndex();

      for (String var4 : var0.getAllKeys()) {
         if (var0.contains(var4, 99)) {
            var2.usedAuxIds.put(var4, var0.getInt(var4));
         }
      }

      return var2;
   }

   @Override
   public CompoundTag save(CompoundTag var1, HolderLookup.Provider var2) {
      ObjectIterator var3 = this.usedAuxIds.object2IntEntrySet().iterator();

      while (var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         var1.putInt((String)var4.getKey(), var4.getIntValue());
      }

      return var1;
   }

   public MapId getFreeAuxValueForMap() {
      int var1 = this.usedAuxIds.getInt("map") + 1;
      this.usedAuxIds.put("map", var1);
      this.setDirty();
      return new MapId(var1);
   }
}
