package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;

public class ClientboundAwardStatsPacket implements Packet<ClientGamePacketListener> {
   private final Object2IntMap<Stat<?>> stats;

   public ClientboundAwardStatsPacket(Object2IntMap<Stat<?>> var1) {
      super();
      this.stats = var1;
   }

   public ClientboundAwardStatsPacket(FriendlyByteBuf var1) {
      super();
      this.stats = (Object2IntMap)var1.readMap(Object2IntOpenHashMap::new, (var0) -> {
         int var1 = var0.readVarInt();
         int var2 = var0.readVarInt();
         return readStatCap((StatType)Registry.STAT_TYPE.byId(var1), var2);
      }, FriendlyByteBuf::readVarInt);
   }

   private static <T> Stat<T> readStatCap(StatType<T> var0, int var1) {
      return var0.get(var0.getRegistry().byId(var1));
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleAwardStats(this);
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeMap(this.stats, (var1x, var2) -> {
         var1x.writeVarInt(Registry.STAT_TYPE.getId(var2.getType()));
         var1x.writeVarInt(this.getStatIdCap(var2));
      }, FriendlyByteBuf::writeVarInt);
   }

   private <T> int getStatIdCap(Stat<T> var1) {
      return var1.getType().getRegistry().getId(var1.getValue());
   }

   public Map<Stat<?>, Integer> getStats() {
      return this.stats;
   }
}
