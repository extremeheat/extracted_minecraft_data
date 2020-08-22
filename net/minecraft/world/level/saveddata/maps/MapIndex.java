package net.minecraft.world.level.saveddata.maps;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Iterator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class MapIndex extends SavedData {
   private final Object2IntMap usedAuxIds = new Object2IntOpenHashMap();

   public MapIndex() {
      super("idcounts");
      this.usedAuxIds.defaultReturnValue(-1);
   }

   public void load(CompoundTag var1) {
      this.usedAuxIds.clear();
      Iterator var2 = var1.getAllKeys().iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         if (var1.contains(var3, 99)) {
            this.usedAuxIds.put(var3, var1.getInt(var3));
         }
      }

   }

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
