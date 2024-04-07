package net.minecraft.server.level;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.Set;

public final class PlayerMap {
   private final Object2BooleanMap<ServerPlayer> players = new Object2BooleanOpenHashMap();

   public PlayerMap() {
      super();
   }

   public Set<ServerPlayer> getAllPlayers() {
      return this.players.keySet();
   }

   public void addPlayer(ServerPlayer var1, boolean var2) {
      this.players.put(var1, var2);
   }

   public void removePlayer(ServerPlayer var1) {
      this.players.removeBoolean(var1);
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
}
