package net.minecraft.client.telemetry;

import java.time.Duration;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.telemetry.events.PerformanceMetricsEvent;
import net.minecraft.client.telemetry.events.WorldLoadEvent;
import net.minecraft.client.telemetry.events.WorldLoadTimesEvent;
import net.minecraft.client.telemetry.events.WorldUnloadEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;

public class WorldSessionTelemetryManager {
   private final UUID worldSessionId = UUID.randomUUID();
   private final TelemetryEventSender eventSender;
   private final WorldLoadEvent worldLoadEvent;
   private final WorldUnloadEvent worldUnloadEvent = new WorldUnloadEvent();
   private final PerformanceMetricsEvent performanceMetricsEvent;
   private final WorldLoadTimesEvent worldLoadTimesEvent;

   public WorldSessionTelemetryManager(TelemetryEventSender var1, boolean var2, @Nullable Duration var3, @Nullable String var4) {
      super();
      this.worldLoadEvent = new WorldLoadEvent(var4);
      this.performanceMetricsEvent = new PerformanceMetricsEvent();
      this.worldLoadTimesEvent = new WorldLoadTimesEvent(var2, var3);
      this.eventSender = var1.decorate((var1x) -> {
         this.worldLoadEvent.addProperties(var1x);
         var1x.put(TelemetryProperty.WORLD_SESSION_ID, this.worldSessionId);
      });
   }

   public void tick() {
      this.performanceMetricsEvent.tick(this.eventSender);
   }

   public void onPlayerInfoReceived(GameType var1, boolean var2) {
      this.worldLoadEvent.setGameMode(var1, var2);
      this.worldUnloadEvent.onPlayerInfoReceived();
      this.worldSessionStart();
   }

   public void onServerBrandReceived(String var1) {
      this.worldLoadEvent.setServerBrand(var1);
      this.worldSessionStart();
   }

   public void setTime(long var1) {
      this.worldUnloadEvent.setTime(var1);
   }

   public void worldSessionStart() {
      if (this.worldLoadEvent.send(this.eventSender)) {
         this.worldLoadTimesEvent.send(this.eventSender);
         this.performanceMetricsEvent.start();
      }

   }

   public void onDisconnect() {
      this.worldLoadEvent.send(this.eventSender);
      this.performanceMetricsEvent.stop();
      this.worldUnloadEvent.send(this.eventSender);
   }

   public void onAdvancementDone(Level var1, AdvancementHolder var2) {
      ResourceLocation var3 = var2.id();
      if (var2.value().sendsTelemetryEvent() && "minecraft".equals(var3.getNamespace())) {
         long var4 = var1.getGameTime();
         this.eventSender.send(TelemetryEventType.ADVANCEMENT_MADE, (var3x) -> {
            var3x.put(TelemetryProperty.ADVANCEMENT_ID, var3.toString());
            var3x.put(TelemetryProperty.ADVANCEMENT_GAME_TIME, var4);
         });
      }

   }
}
