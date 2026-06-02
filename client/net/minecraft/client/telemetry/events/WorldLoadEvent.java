package net.minecraft.client.telemetry.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.telemetry.TelemetryEventSender;
import net.minecraft.client.telemetry.TelemetryEventType;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.client.telemetry.TelemetryPropertyMap;
import net.minecraft.world.level.GameType;
import org.jspecify.annotations.Nullable;

public class WorldLoadEvent {
   private boolean eventSent;
   private TelemetryProperty.@Nullable GameMode gameMode;
   private @Nullable String serverBrand;
   private final @Nullable String minigameName;

   public WorldLoadEvent(final @Nullable String minigameName) {
      super();
      this.minigameName = minigameName;
   }

   public void addProperties(final TelemetryPropertyMap.Builder properties) {
      if (this.serverBrand != null) {
         properties.put(TelemetryProperty.SERVER_MODDED, !this.serverBrand.equals("vanilla"));
      }

      properties.put(TelemetryProperty.SERVER_TYPE, this.getServerType());
   }

   private TelemetryProperty.ServerType getServerType() {
      ServerData server = Minecraft.getInstance().getCurrentServer();
      if (server != null && server.isRealm()) {
         return TelemetryProperty.ServerType.REALM;
      } else {
         return Minecraft.getInstance().hasSingleplayerServer() ? TelemetryProperty.ServerType.LOCAL : TelemetryProperty.ServerType.OTHER;
      }
   }

   public boolean send(final TelemetryEventSender eventSender, final boolean lastChance) {
      if (!this.eventSent && this.gameMode != null && (this.serverBrand != null || lastChance)) {
         this.eventSent = true;
         eventSender.send(TelemetryEventType.WORLD_LOADED, (properties) -> {
            properties.put(TelemetryProperty.GAME_MODE, this.gameMode);
            if (this.minigameName != null) {
               properties.put(TelemetryProperty.REALMS_MAP_CONTENT, this.minigameName);
            }

         });
         return true;
      } else {
         return false;
      }
   }

   public boolean wasSent() {
      return this.eventSent;
   }

   public void setGameMode(final GameType type, final boolean hardcore) {
      TelemetryProperty.GameMode var10001;
      switch (type) {
         case SURVIVAL -> var10001 = hardcore ? TelemetryProperty.GameMode.HARDCORE : TelemetryProperty.GameMode.SURVIVAL;
         case CREATIVE -> var10001 = TelemetryProperty.GameMode.CREATIVE;
         case ADVENTURE -> var10001 = TelemetryProperty.GameMode.ADVENTURE;
         case SPECTATOR -> var10001 = TelemetryProperty.GameMode.SPECTATOR;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      this.gameMode = var10001;
   }

   public void setServerBrand(final String serverBrand) {
      this.serverBrand = serverBrand;
   }
}
