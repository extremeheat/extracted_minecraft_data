package net.minecraft.client.telemetry.events;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.telemetry.TelemetryEventSender;
import net.minecraft.client.telemetry.TelemetryEventType;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.client.telemetry.TelemetryPropertyMap;
import net.minecraft.world.level.GameType;

public class WorldLoadEvent {
   private boolean eventSent;
   @Nullable
   private TelemetryProperty.GameMode gameMode;
   @Nullable
   private String serverBrand;
   @Nullable
   private final String minigameName;

   public WorldLoadEvent(@Nullable String var1) {
      super();
      this.minigameName = var1;
   }

   public void addProperties(TelemetryPropertyMap.Builder var1) {
      if (this.serverBrand != null) {
         var1.put(TelemetryProperty.SERVER_MODDED, !this.serverBrand.equals("vanilla"));
      }

      var1.put(TelemetryProperty.SERVER_TYPE, this.getServerType());
   }

   private TelemetryProperty.ServerType getServerType() {
      ServerData var1 = Minecraft.getInstance().getCurrentServer();
      if (var1 != null && var1.isRealm()) {
         return TelemetryProperty.ServerType.REALM;
      } else {
         return Minecraft.getInstance().hasSingleplayerServer() ? TelemetryProperty.ServerType.LOCAL : TelemetryProperty.ServerType.OTHER;
      }
   }

   public boolean send(TelemetryEventSender var1) {
      if (!this.eventSent && this.gameMode != null && this.serverBrand != null) {
         this.eventSent = true;
         var1.send(TelemetryEventType.WORLD_LOADED, var1x -> {
            var1x.put(TelemetryProperty.GAME_MODE, this.gameMode);
            if (this.minigameName != null) {
               var1x.put(TelemetryProperty.REALMS_MAP_CONTENT, this.minigameName);
            }
         });
         return true;
      } else {
         return false;
      }
   }

   public void setGameMode(GameType var1, boolean var2) {
      this.gameMode = switch (var1) {
         case SURVIVAL -> var2 ? TelemetryProperty.GameMode.HARDCORE : TelemetryProperty.GameMode.SURVIVAL;
         case CREATIVE -> TelemetryProperty.GameMode.CREATIVE;
         case ADVENTURE -> TelemetryProperty.GameMode.ADVENTURE;
         case SPECTATOR -> TelemetryProperty.GameMode.SPECTATOR;
         default -> throw new MatchException(null, null);
      };
   }

   public void setServerBrand(String var1) {
      this.serverBrand = var1;
   }
}
