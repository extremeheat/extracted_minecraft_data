package net.minecraft.network.play.server;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;

public class S37PacketStatistics extends Packet {
   private Map field_148976_a;

   public S37PacketStatistics() {
      super();
   }

   public S37PacketStatistics(Map var1) {
      super();
      this.field_148976_a = var1;
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147293_a(this);
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      int var2 = var1.func_150792_a();
      this.field_148976_a = Maps.newHashMap();

      for(int var3 = 0; var3 < var2; ++var3) {
         StatBase var4 = StatList.func_151177_a(var1.func_150789_c(32767));
         int var5 = var1.func_150792_a();
         if (var4 != null) {
            this.field_148976_a.put(var4, var5);
         }
      }
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.func_150787_b(this.field_148976_a.size());

      for(Entry var3 : this.field_148976_a.entrySet()) {
         var1.func_150785_a(((StatBase)var3.getKey()).field_75975_e);
         var1.func_150787_b(var3.getValue());
      }
   }

   @Override
   public String func_148835_b() {
      return String.format("count=%d", this.field_148976_a.size());
   }

   public Map func_148974_c() {
      return this.field_148976_a;
   }
}
