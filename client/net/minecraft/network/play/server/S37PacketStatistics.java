package net.minecraft.network.play.server;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;

public class S37PacketStatistics implements Packet<INetHandlerPlayClient> {
   private Map<StatBase, Integer> field_148976_a;

   public S37PacketStatistics() {
      super();
   }

   public S37PacketStatistics(Map<StatBase, Integer> var1) {
      super();
      this.field_148976_a = var1;
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147293_a(this);
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
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

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_148976_a.size());
      Iterator var2 = this.field_148976_a.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         var1.func_180714_a(((StatBase)var3.getKey()).field_75975_e);
         var1.func_150787_b((Integer)var3.getValue());
      }

   }

   public Map<StatBase, Integer> func_148974_c() {
      return this.field_148976_a;
   }
}
