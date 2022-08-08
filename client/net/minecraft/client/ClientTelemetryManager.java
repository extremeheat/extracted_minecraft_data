package net.minecraft.client;

import com.mojang.authlib.minecraft.TelemetryEvent;
import com.mojang.authlib.minecraft.TelemetryPropertyContainer;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.authlib.minecraft.UserApiService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.WorldVersion;
import net.minecraft.util.TelemetryConstants;
import net.minecraft.world.level.GameType;

public class ClientTelemetryManager {
   private static final AtomicInteger THREAD_COUNT = new AtomicInteger(1);
   private static final Executor EXECUTOR = Executors.newSingleThreadExecutor((var0) -> {
      Thread var1 = new Thread(var0);
      var1.setName("Telemetry-Sender-#" + THREAD_COUNT.getAndIncrement());
      return var1;
   });
   private final Minecraft minecraft;
   private final TelemetrySession telemetrySession;
   private boolean worldLoadEventSent;
   @Nullable
   private PlayerInfo playerInfo;
   @Nullable
   private String serverBrand;

   public ClientTelemetryManager(Minecraft var1, UserApiService var2, Optional<String> var3, Optional<String> var4, UUID var5) {
      super();
      this.minecraft = var1;
      if (!SharedConstants.IS_RUNNING_IN_IDE) {
         this.telemetrySession = var2.newTelemetrySession(EXECUTOR);
         TelemetryPropertyContainer var6 = this.telemetrySession.globalProperties();
         addOptionalProperty("UserId", var3, var6);
         addOptionalProperty("ClientId", var4, var6);
         var6.addProperty("deviceSessionId", var5.toString());
         var6.addProperty("WorldSessionId", UUID.randomUUID().toString());
         this.telemetrySession.eventSetupFunction((var0) -> {
            var0.addProperty("eventTimestampUtc", TelemetryConstants.TIMESTAMP_FORMATTER.format(Instant.now()));
         });
      } else {
         this.telemetrySession = TelemetrySession.DISABLED;
      }

   }

   private static void addOptionalProperty(String var0, Optional<String> var1, TelemetryPropertyContainer var2) {
      var1.ifPresentOrElse((var2x) -> {
         var2.addProperty(var0, var2x);
      }, () -> {
         var2.addNullProperty(var0);
      });
   }

   public void onPlayerInfoReceived(GameType var1, boolean var2) {
      this.playerInfo = new PlayerInfo(var1, var2);
      if (this.serverBrand != null) {
         this.sendWorldLoadEvent(this.playerInfo);
      }

   }

   public void onServerBrandReceived(String var1) {
      this.serverBrand = var1;
      if (this.playerInfo != null) {
         this.sendWorldLoadEvent(this.playerInfo);
      }

   }

   private void sendWorldLoadEvent(PlayerInfo var1) {
      if (!this.worldLoadEventSent) {
         this.worldLoadEventSent = true;
         if (this.telemetrySession.isEnabled()) {
            TelemetryEvent var2 = this.telemetrySession.createNewEvent("WorldLoaded");
            WorldVersion var3 = SharedConstants.getCurrentVersion();
            var2.addProperty("build_display_name", var3.getId());
            var2.addProperty("clientModded", Minecraft.checkModStatus().shouldReportAsModified());
            if (this.serverBrand != null) {
               var2.addProperty("serverModded", !this.serverBrand.equals("vanilla"));
            } else {
               var2.addNullProperty("serverModded");
            }

            var2.addProperty("server_type", this.getServerType());
            var2.addProperty("BuildPlat", Util.getPlatform().telemetryName());
            var2.addProperty("Plat", System.getProperty("os.name"));
            var2.addProperty("javaVersion", System.getProperty("java.version"));
            var2.addProperty("PlayerGameMode", var1.getGameModeId());
            var2.send();
         }
      }
   }

   private String getServerType() {
      if (this.minecraft.isConnectedToRealms()) {
         return "realm";
      } else {
         return this.minecraft.hasSingleplayerServer() ? "local" : "server";
      }
   }

   public void onDisconnect() {
      if (this.playerInfo != null) {
         this.sendWorldLoadEvent(this.playerInfo);
      }

   }

   static record PlayerInfo(GameType a, boolean b) {
      private final GameType gameType;
      private final boolean hardcore;

      PlayerInfo(GameType var1, boolean var2) {
         super();
         this.gameType = var1;
         this.hardcore = var2;
      }

      public int getGameModeId() {
         if (this.hardcore && this.gameType == GameType.SURVIVAL) {
            return 99;
         } else {
            byte var10000;
            switch (this.gameType) {
               case SURVIVAL:
                  var10000 = 0;
                  break;
               case CREATIVE:
                  var10000 = 1;
                  break;
               case ADVENTURE:
                  var10000 = 2;
                  break;
               case SPECTATOR:
                  var10000 = 6;
                  break;
               default:
                  throw new IncompatibleClassChangeError();
            }

            return var10000;
         }
      }

      public GameType gameType() {
         return this.gameType;
      }

      public boolean hardcore() {
         return this.hardcore;
      }
   }
}
