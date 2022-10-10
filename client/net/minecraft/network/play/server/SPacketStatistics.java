package net.minecraft.network.play.server;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.io.IOException;
import java.util.Map;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.util.registry.IRegistry;

public class SPacketStatistics implements Packet<INetHandlerPlayClient> {
   private Object2IntMap<Stat<?>> field_148976_a;

   public SPacketStatistics() {
      super();
   }

   public SPacketStatistics(Object2IntMap<Stat<?>> var1) {
      super();
      this.field_148976_a = var1;
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147293_a(this);
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      int var2 = var1.func_150792_a();
      this.field_148976_a = new Object2IntOpenHashMap(var2);

      for(int var3 = 0; var3 < var2; ++var3) {
         this.func_197684_a((StatType)IRegistry.field_212634_w.func_148754_a(var1.func_150792_a()), var1);
      }

   }

   private <T> void func_197684_a(StatType<T> var1, PacketBuffer var2) {
      int var3 = var2.func_150792_a();
      int var4 = var2.func_150792_a();
      this.field_148976_a.put(var1.func_199076_b(var1.func_199080_a().func_148754_a(var3)), var4);
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_148976_a.size());
      ObjectIterator var2 = this.field_148976_a.object2IntEntrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         Stat var4 = (Stat)var3.getKey();
         var1.func_150787_b(IRegistry.field_212634_w.func_148757_b(var4.func_197921_a()));
         var1.func_150787_b(this.func_197683_a(var4));
         var1.func_150787_b(var3.getIntValue());
      }

   }

   private <T> int func_197683_a(Stat<T> var1) {
      return var1.func_197921_a().func_199080_a().func_148757_b(var1.func_197920_b());
   }

   public Map<Stat<?>, Integer> func_148974_c() {
      return this.field_148976_a;
   }
}
