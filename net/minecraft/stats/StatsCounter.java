package net.minecraft.stats;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.entity.player.Player;

public class StatsCounter {
   protected final Object2IntMap stats = Object2IntMaps.synchronize(new Object2IntOpenHashMap());

   public StatsCounter() {
      this.stats.defaultReturnValue(0);
   }

   public void increment(Player var1, Stat var2, int var3) {
      this.setValue(var1, var2, this.getValue(var2) + var3);
   }

   public void setValue(Player var1, Stat var2, int var3) {
      this.stats.put(var2, var3);
   }

   public int getValue(StatType var1, Object var2) {
      return var1.contains(var2) ? this.getValue(var1.get(var2)) : 0;
   }

   public int getValue(Stat var1) {
      return this.stats.getInt(var1);
   }
}
