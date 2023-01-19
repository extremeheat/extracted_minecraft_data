package net.minecraft.client.telemetry.events;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.telemetry.TelemetryEventSender;
import net.minecraft.client.telemetry.TelemetryEventType;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.client.telemetry.TelemetryPropertyMap;
import net.minecraft.world.level.GameType;

public class WorldLoadEvent {
   private boolean eventSent;
   @Nullable
   private TelemetryProperty.GameMode gameMode = null;
   @Nullable
   private String serverBrand;

   public WorldLoadEvent() {
      super();
   }

   public void addProperties(TelemetryPropertyMap.Builder var1) {
      if (this.serverBrand != null) {
         var1.put(TelemetryProperty.SERVER_MODDED, !this.serverBrand.equals("vanilla"));
      }

      var1.put(TelemetryProperty.SERVER_TYPE, this.getServerType());
   }

   private TelemetryProperty.ServerType getServerType() {
      if (Minecraft.getInstance().isConnectedToRealms()) {
         return TelemetryProperty.ServerType.REALM;
      } else {
         return Minecraft.getInstance().hasSingleplayerServer() ? TelemetryProperty.ServerType.LOCAL : TelemetryProperty.ServerType.OTHER;
      }
   }

   public boolean send(TelemetryEventSender var1) {
      if (!this.eventSent && this.gameMode != null && this.serverBrand != null) {
         this.eventSent = true;
         var1.send(TelemetryEventType.WORLD_LOADED, var1x -> var1x.put(TelemetryProperty.GAME_MODE, this.gameMode));
         return true;
      } else {
         return false;
      }
   }

   public void setGameMode(GameType var1, boolean var2) {
      this.gameMode = switch(var1) {
         case SURVIVAL -> var2 ? TelemetryProperty.GameMode.HARDCORE : TelemetryProperty.GameMode.SURVIVAL;
         case CREATIVE -> TelemetryProperty.GameMode.CREATIVE;
         case ADVENTURE -> TelemetryProperty.GameMode.ADVENTURE;
         case SPECTATOR -> TelemetryProperty.GameMode.SPECTATOR;
      };
   }

   public void setServerBrand(String var1) {
      this.serverBrand = var1;
   }
}
