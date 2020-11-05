package net.minecraft.server.level;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.stream.Stream;

public final class PlayerMap {
   private final Object2BooleanMap<ServerPlayer> players = new Object2BooleanOpenHashMap();

   public PlayerMap() {
      super();
   }

   public Stream<ServerPlayer> getPlayers(long var1) {
      return this.players.keySet().stream();
   }

   public void addPlayer(long var1, ServerPlayer var3, boolean var4) {
      this.players.put(var3, var4);
   }

   public void removePlayer(long var1, ServerPlayer var3) {
      this.players.removeBoolean(var3);
   }

   public void ignorePlayer(ServerPlayer var1) {
      this.players.replace(var1, true);
   }

   public void unIgnorePlayer(ServerPlayer var1) {
      this.players.replace(var1, false);
   }

   public boolean ignoredOrUnknown(ServerPlayer var1) {
      return this.players.getOrDefault(var1, true);
   }

   public boolean ignored(ServerPlayer var1) {
      return this.players.getBoolean(var1);
   }

   public void updatePlayer(long var1, long var3, ServerPlayer var5) {
   }
}
