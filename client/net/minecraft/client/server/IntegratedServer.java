package net.minecraft.client.server;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.stats.Stats;
import net.minecraft.util.ModCheck;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.ProfileKeyPair;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.slf4j.Logger;

public class IntegratedServer extends MinecraftServer {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MIN_SIM_DISTANCE = 2;
   private final Minecraft minecraft;
   private boolean paused = true;
   private int publishedPort = -1;
   @Nullable
   private GameType publishedGameType;
   @Nullable
   private LanServerPinger lanPinger;
   @Nullable
   private UUID uuid;
   private int previousSimulationDistance = 0;

   public IntegratedServer(
      Thread var1,
      Minecraft var2,
      LevelStorageSource.LevelStorageAccess var3,
      PackRepository var4,
      WorldStem var5,
      Services var6,
      ChunkProgressListenerFactory var7
   ) {
      super(var1, var3, var4, var5, var2.getProxy(), var2.getFixerUpper(), var6, var7);
      this.setSingleplayerProfile(var2.getGameProfile());
      this.setDemo(var2.isDemo());
      this.setPlayerList(new IntegratedPlayerList(this, this.registries(), this.playerDataStorage));
      this.minecraft = var2;
   }

   @Override
   public boolean initServer() {
      LOGGER.info("Starting integrated minecraft server version {}", SharedConstants.getCurrentVersion().getName());
      this.setUsesAuthentication(true);
      this.setPvpAllowed(true);
      this.setFlightAllowed(true);
      this.initializeKeyPair();
      this.loadLevel();
      GameProfile var1 = this.getSingleplayerProfile();
      String var2 = this.getWorldData().getLevelName();
      this.setMotd(var1 != null ? var1.getName() + " - " + var2 : var2);
      return true;
   }

   @Override
   public boolean isPaused() {
      return this.paused;
   }

   @Override
   public void tickServer(BooleanSupplier var1) {
      boolean var2 = this.paused;
      this.paused = Minecraft.getInstance().isPaused();
      ProfilerFiller var3 = this.getProfiler();
      if (!var2 && this.paused) {
         var3.push("autoSave");
         LOGGER.info("Saving and pausing game...");
         this.saveEverything(false, false, false);
         var3.pop();
      }

      boolean var4 = Minecraft.getInstance().getConnection() != null;
      if (var4 && this.paused) {
         this.tickPaused();
      } else {
         if (var2 && !this.paused) {
            this.forceTimeSynchronization();
         }

         super.tickServer(var1);
         int var5 = Math.max(2, this.minecraft.options.renderDistance().get());
         if (var5 != this.getPlayerList().getViewDistance()) {
            LOGGER.info("Changing view distance to {}, from {}", var5, this.getPlayerList().getViewDistance());
            this.getPlayerList().setViewDistance(var5);
         }

         int var6 = Math.max(2, this.minecraft.options.simulationDistance().get());
         if (var6 != this.previousSimulationDistance) {
            LOGGER.info("Changing simulation distance to {}, from {}", var6, this.previousSimulationDistance);
            this.getPlayerList().setSimulationDistance(var6);
            this.previousSimulationDistance = var6;
         }
      }
   }

   @Override
   public void logTickTime(long var1) {
      this.minecraft.getDebugOverlay().logTickDuration(var1);
   }

   private void tickPaused() {
      for(ServerPlayer var2 : this.getPlayerList().getPlayers()) {
         var2.awardStat(Stats.TOTAL_WORLD_TIME);
      }
   }

   @Override
   public boolean shouldRconBroadcast() {
      return true;
   }

   @Override
   public boolean shouldInformAdmins() {
      return true;
   }

   @Override
   public File getServerDirectory() {
      return this.minecraft.gameDirectory;
   }

   @Override
   public boolean isDedicatedServer() {
      return false;
   }

   @Override
   public int getRateLimitPacketsPerSecond() {
      return 0;
   }

   @Override
   public boolean isEpollEnabled() {
      return false;
   }

   @Override
   public void onServerCrash(CrashReport var1) {
      this.minecraft.delayCrashRaw(var1);
   }

   @Override
   public SystemReport fillServerSystemReport(SystemReport var1) {
      var1.setDetail("Type", "Integrated Server (map_client.txt)");
      var1.setDetail("Is Modded", () -> this.getModdedStatus().fullDescription());
      var1.setDetail("Launched Version", this.minecraft::getLaunchedVersion);
      return var1;
   }

   @Override
   public ModCheck getModdedStatus() {
      return Minecraft.checkModStatus().merge(super.getModdedStatus());
   }

   @Override
   public boolean publishServer(@Nullable GameType var1, boolean var2, int var3) {
      try {
         this.minecraft.prepareForMultiplayer();
         this.minecraft.getProfileKeyPairManager().prepareKeyPair().thenAcceptAsync(var1x -> var1x.ifPresent(var1xx -> {
               ClientPacketListener var2xx = this.minecraft.getConnection();
               if (var2xx != null) {
                  var2xx.setKeyPair(var1xx);
               }
            }), this.minecraft);
         this.getConnection().startTcpServerListener(null, var3);
         LOGGER.info("Started serving on {}", var3);
         this.publishedPort = var3;
         this.lanPinger = new LanServerPinger(this.getMotd(), var3 + "");
         this.lanPinger.start();
         this.publishedGameType = var1;
         this.getPlayerList().setAllowCheatsForAllPlayers(var2);
         int var4 = this.getProfilePermissions(this.minecraft.player.getGameProfile());
         this.minecraft.player.setPermissionLevel(var4);

         for(ServerPlayer var6 : this.getPlayerList().getPlayers()) {
            this.getCommands().sendCommands(var6);
         }

         return true;
      } catch (IOException var7) {
         return false;
      }
   }

   @Override
   public void stopServer() {
      super.stopServer();
      if (this.lanPinger != null) {
         this.lanPinger.interrupt();
         this.lanPinger = null;
      }
   }

   @Override
   public void halt(boolean var1) {
      this.executeBlocking(() -> {
         for(ServerPlayer var3 : Lists.newArrayList(this.getPlayerList().getPlayers())) {
            if (!var3.getUUID().equals(this.uuid)) {
               this.getPlayerList().remove(var3);
            }
         }
      });
      super.halt(var1);
      if (this.lanPinger != null) {
         this.lanPinger.interrupt();
         this.lanPinger = null;
      }
   }

   @Override
   public boolean isPublished() {
      return this.publishedPort > -1;
   }

   @Override
   public int getPort() {
      return this.publishedPort;
   }

   @Override
   public void setDefaultGameType(GameType var1) {
      super.setDefaultGameType(var1);
      this.publishedGameType = null;
   }

   @Override
   public boolean isCommandBlockEnabled() {
      return true;
   }

   @Override
   public int getOperatorUserPermissionLevel() {
      return 2;
   }

   @Override
   public int getFunctionCompilationLevel() {
      return 2;
   }

   public void setUUID(UUID var1) {
      this.uuid = var1;
   }

   @Override
   public boolean isSingleplayerOwner(GameProfile var1) {
      return this.getSingleplayerProfile() != null && var1.getName().equalsIgnoreCase(this.getSingleplayerProfile().getName());
   }

   @Override
   public int getScaledTrackingDistance(int var1) {
      return (int)(this.minecraft.options.entityDistanceScaling().get() * (double)var1);
   }

   @Override
   public boolean forceSynchronousWrites() {
      return this.minecraft.options.syncWrites;
   }

   @Nullable
   @Override
   public GameType getForcedGameType() {
      return this.isPublished() ? (GameType)MoreObjects.firstNonNull(this.publishedGameType, this.worldData.getGameType()) : null;
   }
}
