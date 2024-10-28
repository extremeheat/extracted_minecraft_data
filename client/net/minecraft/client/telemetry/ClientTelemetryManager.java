package net.minecraft.client.telemetry;

import com.google.common.base.Suppliers;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.authlib.minecraft.UserApiService;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;

public class ClientTelemetryManager implements AutoCloseable {
   private static final AtomicInteger THREAD_COUNT = new AtomicInteger(1);
   private static final Executor EXECUTOR = Executors.newSingleThreadExecutor((var0) -> {
      Thread var1 = new Thread(var0);
      var1.setName("Telemetry-Sender-#" + THREAD_COUNT.getAndIncrement());
      return var1;
   });
   private final Minecraft minecraft;
   private final UserApiService userApiService;
   private final TelemetryPropertyMap deviceSessionProperties;
   private final Path logDirectory;
   private final CompletableFuture<Optional<TelemetryLogManager>> logManager;
   private final Supplier<TelemetryEventSender> outsideSessionSender = Suppliers.memoize(this::createEventSender);

   public ClientTelemetryManager(Minecraft var1, UserApiService var2, User var3) {
      super();
      this.minecraft = var1;
      this.userApiService = var2;
      TelemetryPropertyMap.Builder var4 = TelemetryPropertyMap.builder();
      var3.getXuid().ifPresent((var1x) -> {
         var4.put(TelemetryProperty.USER_ID, var1x);
      });
      var3.getClientId().ifPresent((var1x) -> {
         var4.put(TelemetryProperty.CLIENT_ID, var1x);
      });
      var4.put(TelemetryProperty.MINECRAFT_SESSION_ID, UUID.randomUUID());
      var4.put(TelemetryProperty.GAME_VERSION, SharedConstants.getCurrentVersion().getId());
      var4.put(TelemetryProperty.OPERATING_SYSTEM, Util.getPlatform().telemetryName());
      var4.put(TelemetryProperty.PLATFORM, System.getProperty("os.name"));
      var4.put(TelemetryProperty.CLIENT_MODDED, Minecraft.checkModStatus().shouldReportAsModified());
      var4.putIfNotNull(TelemetryProperty.LAUNCHER_NAME, Minecraft.getLauncherBrand());
      this.deviceSessionProperties = var4.build();
      this.logDirectory = var1.gameDirectory.toPath().resolve("logs/telemetry");
      this.logManager = TelemetryLogManager.open(this.logDirectory);
   }

   public WorldSessionTelemetryManager createWorldSessionManager(boolean var1, @Nullable Duration var2, @Nullable String var3) {
      return new WorldSessionTelemetryManager(this.createEventSender(), var1, var2, var3);
   }

   public TelemetryEventSender getOutsideSessionSender() {
      return (TelemetryEventSender)this.outsideSessionSender.get();
   }

   private TelemetryEventSender createEventSender() {
      if (!this.minecraft.allowsTelemetry()) {
         return TelemetryEventSender.DISABLED;
      } else {
         TelemetrySession var1 = this.userApiService.newTelemetrySession(EXECUTOR);
         if (!var1.isEnabled()) {
            return TelemetryEventSender.DISABLED;
         } else {
            CompletableFuture var2 = this.logManager.thenCompose((var0) -> {
               return (CompletionStage)var0.map(TelemetryLogManager::openLogger).orElseGet(() -> {
                  return CompletableFuture.completedFuture(Optional.empty());
               });
            });
            return (var3, var4) -> {
               if (!var3.isOptIn() || Minecraft.getInstance().telemetryOptInExtra()) {
                  TelemetryPropertyMap.Builder var5 = TelemetryPropertyMap.builder();
                  var5.putAll(this.deviceSessionProperties);
                  var5.put(TelemetryProperty.EVENT_TIMESTAMP_UTC, Instant.now());
                  var5.put(TelemetryProperty.OPT_IN, var3.isOptIn());
                  var4.accept(var5);
                  TelemetryEventInstance var6 = new TelemetryEventInstance(var3, var5.build());
                  var2.thenAccept((var2x) -> {
                     if (!var2x.isEmpty()) {
                        ((TelemetryEventLogger)var2x.get()).log(var6);
                        var6.export(var1).send();
                     }
                  });
               }
            };
         }
      }
   }

   public Path getLogDirectory() {
      return this.logDirectory;
   }

   public void close() {
      this.logManager.thenAccept((var0) -> {
         var0.ifPresent(TelemetryLogManager::close);
      });
   }
}
