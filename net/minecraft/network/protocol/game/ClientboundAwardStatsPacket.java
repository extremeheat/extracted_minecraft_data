package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.io.IOException;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;

public class ClientboundAwardStatsPacket implements Packet {
   private Object2IntMap stats;

   public ClientboundAwardStatsPacket() {
   }

   public ClientboundAwardStatsPacket(Object2IntMap var1) {
      this.stats = var1;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleAwardStats(this);
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      int var2 = var1.readVarInt();
      this.stats = new Object2IntOpenHashMap(var2);

      for(int var3 = 0; var3 < var2; ++var3) {
         this.readStat((StatType)Registry.STAT_TYPE.byId(var1.readVarInt()), var1);
      }

   }

   private void readStat(StatType var1, FriendlyByteBuf var2) {
      int var3 = var2.readVarInt();
      int var4 = var2.readVarInt();
      this.stats.put(var1.get(var1.getRegistry().byId(var3)), var4);
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.stats.size());
      ObjectIterator var2 = this.stats.object2IntEntrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         Stat var4 = (Stat)var3.getKey();
         var1.writeVarInt(Registry.STAT_TYPE.getId(var4.getType()));
         var1.writeVarInt(this.getId(var4));
         var1.writeVarInt(var3.getIntValue());
      }

   }

   private int getId(Stat var1) {
      return var1.getType().getRegistry().getId(var1.getValue());
   }

   public Map getStats() {
      return this.stats;
   }
}
