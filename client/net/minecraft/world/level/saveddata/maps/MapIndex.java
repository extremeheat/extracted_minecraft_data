package net.minecraft.world.level.saveddata.maps;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class MapIndex extends SavedData {
   public static final String FILE_NAME = "idcounts";
   private final Object2IntMap<String> usedAuxIds = new Object2IntOpenHashMap();

   public MapIndex() {
      super();
      this.usedAuxIds.defaultReturnValue(-1);
   }

   public static MapIndex load(CompoundTag var0) {
      MapIndex var1 = new MapIndex();
      Iterator var2 = var0.getAllKeys().iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         if (var0.contains(var3, 99)) {
            var1.usedAuxIds.put(var3, var0.getInt(var3));
         }
      }

      return var1;
   }

   public CompoundTag save(CompoundTag var1) {
      ObjectIterator var2 = this.usedAuxIds.object2IntEntrySet().iterator();

      while(var2.hasNext()) {
         Object2IntMap.Entry var3 = (Object2IntMap.Entry)var2.next();
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
