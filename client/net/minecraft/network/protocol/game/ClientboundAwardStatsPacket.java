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
      this.stats = var1.readMap(Object2IntOpenHashMap::new, var1x -> {
         StatType var2 = var1x.readById(Registry.STAT_TYPE);
         return readStatCap(var1, var2);
      }, FriendlyByteBuf::readVarInt);
   }

   private static <T> Stat<T> readStatCap(FriendlyByteBuf var0, StatType<T> var1) {
      return var1.get(var0.readById(var1.getRegistry()));
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleAwardStats(this);
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeMap(this.stats, ClientboundAwardStatsPacket::writeStatCap, FriendlyByteBuf::writeVarInt);
   }

   private static <T> void writeStatCap(FriendlyByteBuf var0, Stat<T> var1) {
      var0.writeId(Registry.STAT_TYPE, var1.getType());
      var0.writeId(var1.getType().getRegistry(), (T)var1.getValue());
   }

   public Map<Stat<?>, Integer> getStats() {
      return this.stats;
   }
}
