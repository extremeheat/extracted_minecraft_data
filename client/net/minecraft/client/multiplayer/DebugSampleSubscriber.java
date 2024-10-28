package net.minecraft.client.multiplayer;

import java.util.EnumMap;
import net.minecraft.Util;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.network.protocol.game.ServerboundDebugSampleSubscriptionPacket;
import net.minecraft.util.debugchart.RemoteDebugSampleType;

public class DebugSampleSubscriber {
   public static final int REQUEST_INTERVAL_MS = 5000;
   private final ClientPacketListener connection;
   private final DebugScreenOverlay debugScreenOverlay;
   private final EnumMap<RemoteDebugSampleType, Long> lastRequested;

   public DebugSampleSubscriber(ClientPacketListener var1, DebugScreenOverlay var2) {
      super();
      this.debugScreenOverlay = var2;
      this.connection = var1;
      this.lastRequested = new EnumMap(RemoteDebugSampleType.class);
   }

   public void tick() {
      if (this.debugScreenOverlay.showFpsCharts()) {
         this.sendSubscriptionRequestIfNeeded(RemoteDebugSampleType.TICK_TIME);
      }

   }

   private void sendSubscriptionRequestIfNeeded(RemoteDebugSampleType var1) {
      long var2 = Util.getMillis();
      if (var2 > (Long)this.lastRequested.getOrDefault(var1, 0L) + 5000L) {
         this.connection.send(new ServerboundDebugSampleSubscriptionPacket(var1));
         this.lastRequested.put(var1, var2);
      }

   }
}
