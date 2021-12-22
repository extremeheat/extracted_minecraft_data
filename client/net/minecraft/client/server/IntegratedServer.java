package net.minecraft.client.server;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerResources;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.stats.Stats;
import net.minecraft.util.ModCheck;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IntegratedServer extends MinecraftServer {
   private static final Logger LOGGER = LogManager.getLogger();
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

   public IntegratedServer(Thread var1, Minecraft var2, RegistryAccess.RegistryHolder var3, LevelStorageSource.LevelStorageAccess var4, PackRepository var5, ServerResources var6, WorldData var7, MinecraftSessionService var8, GameProfileRepository var9, GameProfileCache var10, ChunkProgressListenerFactory var11) {
      super(var1, var3, var4, var7, var5, var2.getProxy(), var2.getFixerUpper(), var6, var8, var9, var10, var11);
      this.setSingleplayerName(var2.getUser().getName());
      this.setDemo(var2.isDemo());
      this.setPlayerList(new IntegratedPlayerList(this, this.registryHolder, this.playerDataStorage));
      this.minecraft = var2;
   }

   public boolean initServer() {
      LOGGER.info("Starting integrated minecraft server version {}", SharedConstants.getCurrentVersion().getName());
      this.setUsesAuthentication(true);
      this.setPvpAllowed(true);
      this.setFlightAllowed(true);
      this.initializeKeyPair();
      this.loadLevel();
      String var10001 = this.getSingleplayerName();
      this.setMotd(var10001 + " - " + this.getWorldData().getLevelName());
      return true;
   }

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
         super.tickServer(var1);
         int var5 = Math.max(2, this.minecraft.options.renderDistance);
         if (var5 != this.getPlayerList().getViewDistance()) {
            LOGGER.info("Changing view distance to {}, from {}", var5, this.getPlayerList().getViewDistance());
            this.getPlayerList().setViewDistance(var5);
         }

         int var6 = Math.max(2, this.minecraft.options.simulationDistance);
         if (var6 != this.previousSimulationDistance) {
            LOGGER.info("Changing simulation distance to {}, from {}", var6, this.previousSimulationDistance);
            this.getPlayerList().setSimulationDistance(var6);
            this.previousSimulationDistance = var6;
         }

      }
   }

   private void tickPaused() {
      Iterator var1 = this.getPlayerList().getPlayers().iterator();

      while(var1.hasNext()) {
         ServerPlayer var2 = (ServerPlayer)var1.next();
         var2.awardStat(Stats.TOTAL_WORLD_TIME);
      }

   }

   public boolean shouldRconBroadcast() {
      return true;
   }

   public boolean shouldInformAdmins() {
      return true;
   }

   public File getServerDirectory() {
      return this.minecraft.gameDirectory;
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
      this.minecraft.delayCrash(() -> {
         return var1;
      });
   }

   public SystemReport fillServerSystemReport(SystemReport var1) {
      var1.setDetail("Type", "Integrated Server (map_client.txt)");
      var1.setDetail("Is Modded", () -> {
         return this.getModdedStatus().fullDescription();
      });
      return var1;
   }

   public ModCheck getModdedStatus() {
      return Minecraft.checkModStatus().merge(super.getModdedStatus());
   }

   public boolean publishServer(@Nullable GameType var1, boolean var2, int var3) {
      try {
         this.minecraft.prepareForMultiplayer();
         this.getConnection().startTcpServerListener((InetAddress)null, var3);
         LOGGER.info("Started serving on {}", var3);
         this.publishedPort = var3;
         this.lanPinger = new LanServerPinger(this.getMotd(), var3.makeConcatWithConstants<invokedynamic>(var3));
         this.lanPinger.start();
         this.publishedGameType = var1;
         this.getPlayerList().setAllowCheatsForAllPlayers(var2);
         int var4 = this.getProfilePermissions(this.minecraft.player.getGameProfile());
         this.minecraft.player.setPermissionLevel(var4);
         Iterator var5 = this.getPlayerList().getPlayers().iterator();

         while(var5.hasNext()) {
            ServerPlayer var6 = (ServerPlayer)var5.next();
            this.getCommands().sendCommands(var6);
         }

         return true;
      } catch (IOException var7) {
         return false;
      }
   }

   public void stopServer() {
      super.stopServer();
      if (this.lanPinger != null) {
         this.lanPinger.interrupt();
         this.lanPinger = null;
      }

   }

   public void halt(boolean var1) {
      this.executeBlocking(() -> {
         ArrayList var1 = Lists.newArrayList(this.getPlayerList().getPlayers());
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            ServerPlayer var3 = (ServerPlayer)var2.next();
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

   public boolean isPublished() {
      return this.publishedPort > -1;
   }

   public int getPort() {
      return this.publishedPort;
   }

   public void setDefaultGameType(GameType var1) {
      super.setDefaultGameType(var1);
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
      return var1.getName().equalsIgnoreCase(this.getSingleplayerName());
   }

   public int getScaledTrackingDistance(int var1) {
      return (int)(this.minecraft.options.entityDistanceScaling * (float)var1);
   }

   public boolean forceSynchronousWrites() {
      return this.minecraft.options.syncWrites;
   }

   @Nullable
   public GameType getForcedGameType() {
      return this.isPublished() ? (GameType)MoreObjects.firstNonNull(this.publishedGameType, this.worldData.getGameType()) : null;
   }
}
