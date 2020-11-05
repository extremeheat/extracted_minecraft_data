package net.minecraft.client.server;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import net.minecraft.CrashReport;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerResources;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Snooper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IntegratedServer extends MinecraftServer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft minecraft;
   private boolean paused;
   private int publishedPort = -1;
   private LanServerPinger lanPinger;
   private UUID uuid;

   public IntegratedServer(Thread var1, Minecraft var2, RegistryAccess.RegistryHolder var3, LevelStorageSource.LevelStorageAccess var4, PackRepository var5, ServerResources var6, WorldData var7, MinecraftSessionService var8, GameProfileRepository var9, GameProfileCache var10, ChunkProgressListenerFactory var11) {
      super(var1, var3, var4, var7, var5, var2.getProxy(), var2.getFixerUpper(), var6, var8, var9, var10, var11);
      this.setSingleplayerName(var2.getUser().getName());
      this.setDemo(var2.isDemo());
      this.setMaxBuildHeight(256);
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
      this.setMotd(this.getSingleplayerName() + " - " + this.getWorldData().getLevelName());
      return true;
   }

   public void tickServer(BooleanSupplier var1) {
      boolean var2 = this.paused;
      this.paused = Minecraft.getInstance().getConnection() != null && Minecraft.getInstance().isPaused();
      ProfilerFiller var3 = this.getProfiler();
      if (!var2 && this.paused) {
         var3.push("autoSave");
         LOGGER.info("Saving and pausing game...");
         this.getPlayerList().saveAll();
         this.saveAllChunks(false, false, false);
         var3.pop();
      }

      if (!this.paused) {
         super.tickServer(var1);
         int var4 = Math.max(2, this.minecraft.options.renderDistance + -1);
         if (var4 != this.getPlayerList().getViewDistance()) {
            LOGGER.info("Changing view distance to {}, from {}", var4, this.getPlayerList().getViewDistance());
            this.getPlayerList().setViewDistance(var4);
         }

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
      this.minecraft.delayCrash(var1);
   }

   public CrashReport fillReport(CrashReport var1) {
      var1 = super.fillReport(var1);
      var1.getSystemDetails().setDetail("Type", (Object)"Integrated Server (map_client.txt)");
      var1.getSystemDetails().setDetail("Is Modded", () -> {
         return (String)this.getModdedStatus().orElse("Probably not. Jar signature remains and both client + server brands are untouched.");
      });
      return var1;
   }

   public Optional<String> getModdedStatus() {
      String var1 = ClientBrandRetriever.getClientModName();
      if (!var1.equals("vanilla")) {
         return Optional.of("Definitely; Client brand changed to '" + var1 + "'");
      } else {
         var1 = this.getServerModName();
         if (!"vanilla".equals(var1)) {
            return Optional.of("Definitely; Server brand changed to '" + var1 + "'");
         } else {
            return Minecraft.class.getSigners() == null ? Optional.of("Very likely; Jar signature invalidated") : Optional.empty();
         }
      }
   }

   public void populateSnooper(Snooper var1) {
      super.populateSnooper(var1);
      var1.setDynamicData("snooper_partner", this.minecraft.getSnooper().getToken());
   }

   public boolean publishServer(GameType var1, boolean var2, int var3) {
      try {
         this.getConnection().startTcpServerListener((InetAddress)null, var3);
         LOGGER.info("Started serving on {}", var3);
         this.publishedPort = var3;
         this.lanPinger = new LanServerPinger(this.getMotd(), var3 + "");
         this.lanPinger.start();
         this.getPlayerList().setOverrideGameMode(var1);
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
      this.getPlayerList().setOverrideGameMode(var1);
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
}
