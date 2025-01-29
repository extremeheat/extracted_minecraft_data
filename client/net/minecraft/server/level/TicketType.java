package net.minecraft.server.level;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public record TicketType(long timeout, boolean persist, TicketUse use) {
   public static final long NO_TIMEOUT = 0L;
   public static final TicketType START;
   public static final TicketType DRAGON;
   public static final TicketType PLAYER_LOADING;
   public static final TicketType PLAYER_SIMULATION;
   public static final TicketType FORCED;
   public static final TicketType PORTAL;
   public static final TicketType ENDER_PEARL;
   public static final TicketType UNKNOWN;

   public TicketType(long var1, boolean var3, TicketUse var4) {
      super();
      this.timeout = var1;
      this.persist = var3;
      this.use = var4;
   }

   private static TicketType register(String var0, long var1, boolean var3, TicketUse var4) {
      return (TicketType)Registry.register(BuiltInRegistries.TICKET_TYPE, (String)var0, new TicketType(var1, var3, var4));
   }

   public boolean doesLoad() {
      return this.use == TicketType.TicketUse.LOADING || this.use == TicketType.TicketUse.LOADING_AND_SIMULATION;
   }

   public boolean doesSimulate() {
      return this.use == TicketType.TicketUse.SIMULATION || this.use == TicketType.TicketUse.LOADING_AND_SIMULATION;
   }

   public boolean hasTimeout() {
      return this.timeout != 0L;
   }

   static {
      START = register("start", 0L, false, TicketType.TicketUse.LOADING_AND_SIMULATION);
      DRAGON = register("dragon", 0L, false, TicketType.TicketUse.LOADING_AND_SIMULATION);
      PLAYER_LOADING = register("player_loading", 0L, false, TicketType.TicketUse.LOADING);
      PLAYER_SIMULATION = register("player_simulation", 0L, false, TicketType.TicketUse.SIMULATION);
      FORCED = register("forced", 0L, true, TicketType.TicketUse.LOADING_AND_SIMULATION);
      PORTAL = register("portal", 300L, true, TicketType.TicketUse.LOADING_AND_SIMULATION);
      ENDER_PEARL = register("ender_pearl", 40L, false, TicketType.TicketUse.LOADING_AND_SIMULATION);
      UNKNOWN = register("unknown", 1L, false, TicketType.TicketUse.LOADING);
   }

   public static enum TicketUse {
      LOADING,
      SIMULATION,
      LOADING_AND_SIMULATION;

      private TicketUse() {
      }

      // $FF: synthetic method
      private static TicketUse[] $values() {
         return new TicketUse[]{LOADING, SIMULATION, LOADING_AND_SIMULATION};
      }
   }
}
