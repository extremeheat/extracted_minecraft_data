package net.minecraft.client.server;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.TheGame;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.PlayerList;
import net.minecraft.stats.Stats;
import net.minecraft.util.ModCheck;
import net.minecraft.util.debugchart.LocalSampleLogger;
import net.minecraft.util.debugchart.SampleLogger;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
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

   public IntegratedServer(Thread var1, Minecraft var2, LevelStorageSource.LevelStorageAccess var3, Services var4) {
      super(var1, var3, var2.getProxy(), var2.getFixerUpper(), var4);
      this.setSingleplayerProfile(var2.getGameProfile());
      this.setDemo(var2.isDemo());
      this.minecraft = var2;
   }

   public boolean initServer() throws IOException {
      LOGGER.info("Starting integrated minecraft server version {}", SharedConstants.getCurrentVersion().getName());
      this.setUsesAuthentication(true);
      this.setPvpAllowed(true);
      this.setFlightAllowed(true);
      this.initializeKeyPair();
      return true;
   }

   public TheGame initGame(PackRepository var1, WorldStem var2, ChunkProgressListenerFactory var3) {
      TheGame var4 = TheGame.create(this, var1, var2, this.storageSource, var3, (var1x) -> new IntegratedPlayerList(var1x, this.playerDataStorage));
      GameProfile var5 = this.getSingleplayerProfile();
      String var6 = var4.getWorldData().getLevelName();
      this.setMotd(var5 != null ? var5.getName() + " - " + var6 : var6);
      this.previousSimulationDistance = 0;
      return var4;
   }

   public boolean isPaused() {
      return this.paused;
   }

   public void tickServer(TheGame var1, BooleanSupplier var2) {
      boolean var3 = this.paused;
      this.paused = Minecraft.getInstance().isPaused();
      ProfilerFiller var4 = Profiler.get();
      if (!var3 && this.paused) {
         var4.push("autoSave");
         LOGGER.info("Saving and pausing game...");
         this.saveEverything(var1, false, false, false);
         var4.pop();
      }

      boolean var5 = Minecraft.getInstance().getConnection() != null;
      if (var5 && this.paused) {
         this.tickPaused(var1);
      } else {
         if (var3 && !this.paused) {
            var1.forceTimeSynchronization();
         }

         super.tickServer(var1, var2);
         int var6 = Math.max(2, (Integer)this.minecraft.options.renderDistance().get());
         PlayerList var7 = var1.playerList();
         if (var6 != var7.getViewDistance()) {
            LOGGER.info("Changing view distance to {}, from {}", var6, var7.getViewDistance());
            var7.setViewDistance(var6);
         }

         int var8 = Math.max(2, (Integer)this.minecraft.options.simulationDistance().get());
         if (var8 != this.previousSimulationDistance) {
            LOGGER.info("Changing simulation distance to {}, from {}", var8, this.previousSimulationDistance);
            var7.setSimulationDistance(var8);
            this.previousSimulationDistance = var8;
         }

      }
   }

   protected LocalSampleLogger getTickTimeLoggerIfEnabled() {
      return this.minecraft.getDebugOverlay().getTickTimeLogger();
   }

   private void tickPaused(TheGame var1) {
      for(ServerPlayer var3 : var1.playerList().getPlayers()) {
         var3.awardStat(Stats.TOTAL_WORLD_TIME);
      }

   }

   public boolean shouldRconBroadcast() {
      return true;
   }

   public boolean shouldInformAdmins() {
      return true;
   }

   public Path getServerDirectory() {
      return this.minecraft.gameDirectory.toPath();
   }

   public boolean isDedicatedServer() {
      return false;
   }

   public int getRateLimitPacketsPerSecond() {
      return 0;
   }

   public boolean isEpollEnabled() {
      return false;
   }

   public void onServerCrash(CrashReport var1) {
      this.minecraft.delayCrashRaw(var1);
   }

   public SystemReport fillServerSystemReport(SystemReport var1) {
      var1.setDetail("Type", "Integrated Server (map_client.txt)");
      var1.setDetail("Is Modded", (Supplier)(() -> this.getModdedStatus().fullDescription()));
      Minecraft var10002 = this.minecraft;
      Objects.requireNonNull(var10002);
      var1.setDetail("Launched Version", var10002::getLaunchedVersion);
      return var1;
   }

   public ModCheck getModdedStatus() {
      return Minecraft.checkModStatus().merge(super.getModdedStatus());
   }

   public boolean publishServer(TheGame var1, @Nullable GameType var2, boolean var3, int var4) {
      try {
         this.minecraft.prepareForMultiplayer();
         this.minecraft.getConnection().prepareKeyPair();
         this.getConnection().startTcpServerListener((InetAddress)null, var4);
         LOGGER.info("Started serving on {}", var4);
         this.publishedPort = var4;
         this.lanPinger = new LanServerPinger(this.getMotd(), "" + var4);
         this.lanPinger.start();
         this.publishedGameType = var2;
         PlayerList var5 = var1.playerList();
         var5.setAllowCommandsForAllPlayers(var3);
         int var6 = this.getProfilePermissions(this.minecraft.player.getGameProfile());
         this.minecraft.player.setPermissionLevel(var6);

         for(ServerPlayer var8 : var5.getPlayers()) {
            var1.getCommands().sendCommands(var8);
         }

         return true;
      } catch (IOException var9) {
         return false;
      }
   }

   public void stopServer(TheGame var1) {
      super.stopServer(var1);
      if (this.lanPinger != null) {
         this.lanPinger.interrupt();
         this.lanPinger = null;
      }

   }

   public void halt(boolean var1) {
      this.executeBlocking(() -> {
         TheGame var1 = this.theGame();
         if (var1 != null) {
            PlayerList var2 = var1.playerList();

            for(ServerPlayer var5 : Lists.newArrayList(var2.getPlayers())) {
               if (!var5.getUUID().equals(this.uuid)) {
                  var2.remove(var5);
               }
            }
         }

      });
      super.halt(var1);
      if (this.lanPinger != null) {
         this.lanPinger.interrupt();
         this.lanPinger = null;
      }

   }

   public boolean isPublished() {
      return this.publishedPort > -1;
   }

   public int getPort() {
      return this.publishedPort;
   }

   public void setDefaultGameType(TheGame var1, GameType var2) {
      super.setDefaultGameType(var1, var2);
      this.publishedGameType = null;
   }

   public boolean isCommandBlockEnabled() {
      return true;
   }

   public int getOperatorUserPermissionLevel() {
      return 2;
   }

   public int getFunctionCompilationLevel() {
      return 2;
   }

   public void setUUID(UUID var1) {
      this.uuid = var1;
   }

   public boolean isSingleplayerOwner(GameProfile var1) {
      return this.getSingleplayerProfile() != null && var1.getName().equalsIgnoreCase(this.getSingleplayerProfile().getName());
   }

   public int getScaledTrackingDistance(int var1) {
      return (int)((Double)this.minecraft.options.entityDistanceScaling().get() * (double)var1);
   }

   public boolean forceSynchronousWrites() {
      return this.minecraft.options.syncWrites;
   }

   @Nullable
   public GameType getForcedGameType(TheGame var1) {
      return this.isPublished() && !this.isHardcore(var1) ? (GameType)MoreObjects.firstNonNull(this.publishedGameType, var1.getWorldData().getGameType()) : null;
   }

   public boolean saveEverything(TheGame var1, boolean var2, boolean var3, boolean var4) {
      boolean var5 = super.saveEverything(var1, var2, var3, var4);
      this.warnOnLowDiskSpace();
      return var5;
   }

   private void warnOnLowDiskSpace() {
      if (this.storageSource.checkForLowDiskSpace()) {
         this.minecraft.execute(() -> SystemToast.onLowDiskSpace(this.minecraft));
      }

   }

   public void reportChunkLoadFailure(Throwable var1, RegionStorageInfo var2, ChunkPos var3) {
      super.reportChunkLoadFailure(var1, var2, var3);
      this.warnOnLowDiskSpace();
      this.minecraft.execute(() -> SystemToast.onChunkLoadFailure(this.minecraft, var3));
   }

   public void reportChunkSaveFailure(Throwable var1, RegionStorageInfo var2, ChunkPos var3) {
      super.reportChunkSaveFailure(var1, var2, var3);
      this.warnOnLowDiskSpace();
      this.minecraft.execute(() -> SystemToast.onChunkSaveFailure(this.minecraft, var3));
   }

   // $FF: synthetic method
   public SampleLogger getTickTimeLoggerIfEnabled() {
      return this.getTickTimeLoggerIfEnabled();
   }
}
