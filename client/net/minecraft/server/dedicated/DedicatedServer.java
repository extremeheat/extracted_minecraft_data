package net.minecraft.server.dedicated;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.DefaultUncaughtExceptionHandlerWithName;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.ConsoleInput;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerInterface;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.gui.MinecraftServerGui;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.network.TextFilter;
import net.minecraft.server.network.TextFilterClient;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.server.rcon.RconConsoleSource;
import net.minecraft.server.rcon.thread.QueryThreadGs4;
import net.minecraft.server.rcon.thread.RconThread;
import net.minecraft.util.Mth;
import net.minecraft.util.monitoring.jmx.MinecraftServerStatistics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.slf4j.Logger;

public class DedicatedServer extends MinecraftServer implements ServerInterface {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int CONVERSION_RETRY_DELAY_MS = 5000;
   private static final int CONVERSION_RETRIES = 2;
   private final List<ConsoleInput> consoleInput = Collections.synchronizedList(Lists.newArrayList());
   @Nullable
   private QueryThreadGs4 queryThreadGs4;
   private final RconConsoleSource rconConsoleSource;
   @Nullable
   private RconThread rconThread;
   private final DedicatedServerSettings settings;
   @Nullable
   private MinecraftServerGui gui;
   @Nullable
   private final TextFilterClient textFilterClient;

   public DedicatedServer(
      Thread var1,
      LevelStorageSource.LevelStorageAccess var2,
      PackRepository var3,
      WorldStem var4,
      DedicatedServerSettings var5,
      DataFixer var6,
      Services var7,
      ChunkProgressListenerFactory var8
   ) {
      super(var1, var2, var3, var4, Proxy.NO_PROXY, var6, var7, var8);
      this.settings = var5;
      this.rconConsoleSource = new RconConsoleSource(this);
      this.textFilterClient = TextFilterClient.createFromConfig(var5.getProperties().textFilteringConfig);
   }

   @Override
   public boolean initServer() throws IOException {
      Thread var1 = new Thread("Server console handler") {
         @Override
         public void run() {
            BufferedReader var1 = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

            String var2;
            try {
               while(!DedicatedServer.this.isStopped() && DedicatedServer.this.isRunning() && (var2 = var1.readLine()) != null) {
                  DedicatedServer.this.handleConsoleInput(var2, DedicatedServer.this.createCommandSourceStack());
               }
            } catch (IOException var4) {
               DedicatedServer.LOGGER.error("Exception handling console input", var4);
            }
         }
      };
      var1.setDaemon(true);
      var1.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      var1.start();
      LOGGER.info("Starting minecraft server version {}", SharedConstants.getCurrentVersion().getName());
      if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
         LOGGER.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
      }

      LOGGER.info("Loading properties");
      DedicatedServerProperties var2 = this.settings.getProperties();
      if (this.isSingleplayer()) {
         this.setLocalIp("127.0.0.1");
      } else {
         this.setUsesAuthentication(var2.onlineMode);
         this.setPreventProxyConnections(var2.preventProxyConnections);
         this.setLocalIp(var2.serverIp);
      }

      this.setPvpAllowed(var2.pvp);
      this.setFlightAllowed(var2.allowFlight);
      this.setMotd(var2.motd);
      super.setPlayerIdleTimeout(var2.playerIdleTimeout.get());
      this.setEnforceWhitelist(var2.enforceWhitelist);
      this.worldData.setGameType(var2.gamemode);
      LOGGER.info("Default game type: {}", var2.gamemode);
      InetAddress var3 = null;
      if (!this.getLocalIp().isEmpty()) {
         var3 = InetAddress.getByName(this.getLocalIp());
      }

      if (this.getPort() < 0) {
         this.setPort(var2.serverPort);
      }

      this.initializeKeyPair();
      LOGGER.info("Starting Minecraft server on {}:{}", this.getLocalIp().isEmpty() ? "*" : this.getLocalIp(), this.getPort());

      try {
         this.getConnection().startTcpServerListener(var3, this.getPort());
      } catch (IOException var10) {
         LOGGER.warn("**** FAILED TO BIND TO PORT!");
         LOGGER.warn("The exception was: {}", var10.toString());
         LOGGER.warn("Perhaps a server is already running on that port?");
         return false;
      }

      if (!this.usesAuthentication()) {
         LOGGER.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
         LOGGER.warn("The server will make no attempt to authenticate usernames. Beware.");
         LOGGER.warn(
            "While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose."
         );
         LOGGER.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
      }

      if (this.convertOldUsers()) {
         this.getProfileCache().save();
      }

      if (!OldUsersConverter.serverReadyAfterUserconversion(this)) {
         return false;
      } else {
         this.setPlayerList(new DedicatedPlayerList(this, this.registries(), this.playerDataStorage));
         long var4 = Util.getNanos();
         SkullBlockEntity.setup(this.services, this);
         GameProfileCache.setUsesAuthentication(this.usesAuthentication());
         LOGGER.info("Preparing level \"{}\"", this.getLevelIdName());
         this.loadLevel();
         long var6 = Util.getNanos() - var4;
         String var8 = String.format(Locale.ROOT, "%.3fs", (double)var6 / 1.0E9);
         LOGGER.info("Done ({})! For help, type \"help\"", var8);
         if (var2.announcePlayerAchievements != null) {
            this.getGameRules().getRule(GameRules.RULE_ANNOUNCE_ADVANCEMENTS).set(var2.announcePlayerAchievements, this);
         }

         if (var2.enableQuery) {
            LOGGER.info("Starting GS4 status listener");
            this.queryThreadGs4 = QueryThreadGs4.create(this);
         }

         if (var2.enableRcon) {
            LOGGER.info("Starting remote control listener");
            this.rconThread = RconThread.create(this);
         }

         if (this.getMaxTickLength() > 0L) {
            Thread var9 = new Thread(new ServerWatchdog(this));
            var9.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandlerWithName(LOGGER));
            var9.setName("Server Watchdog");
            var9.setDaemon(true);
            var9.start();
         }

         if (var2.enableJmxMonitoring) {
            MinecraftServerStatistics.registerJmxMonitoring(this);
            LOGGER.info("JMX monitoring enabled");
         }

         return true;
      }
   }

   @Override
   public boolean isSpawningAnimals() {
      return this.getProperties().spawnAnimals && super.isSpawningAnimals();
   }

   @Override
   public boolean isSpawningMonsters() {
      return this.settings.getProperties().spawnMonsters && super.isSpawningMonsters();
   }

   @Override
   public boolean areNpcsEnabled() {
      return this.settings.getProperties().spawnNpcs && super.areNpcsEnabled();
   }

   @Override
   public DedicatedServerProperties getProperties() {
      return this.settings.getProperties();
   }

   @Override
   public void forceDifficulty() {
      this.setDifficulty(this.getProperties().difficulty, true);
   }

   @Override
   public boolean isHardcore() {
      return this.getProperties().hardcore;
   }

   @Override
   public SystemReport fillServerSystemReport(SystemReport var1) {
      var1.setDetail("Is Modded", () -> this.getModdedStatus().fullDescription());
      var1.setDetail("Type", () -> "Dedicated Server (map_server.txt)");
      return var1;
   }

   @Override
   public void dumpServerProperties(Path var1) throws IOException {
      DedicatedServerProperties var2 = this.getProperties();

      try (BufferedWriter var3 = Files.newBufferedWriter(var1)) {
         var3.write(String.format(Locale.ROOT, "sync-chunk-writes=%s%n", var2.syncChunkWrites));
         var3.write(String.format(Locale.ROOT, "gamemode=%s%n", var2.gamemode));
         var3.write(String.format(Locale.ROOT, "spawn-monsters=%s%n", var2.spawnMonsters));
         var3.write(String.format(Locale.ROOT, "entity-broadcast-range-percentage=%d%n", var2.entityBroadcastRangePercentage));
         var3.write(String.format(Locale.ROOT, "max-world-size=%d%n", var2.maxWorldSize));
         var3.write(String.format(Locale.ROOT, "spawn-npcs=%s%n", var2.spawnNpcs));
         var3.write(String.format(Locale.ROOT, "view-distance=%d%n", var2.viewDistance));
         var3.write(String.format(Locale.ROOT, "simulation-distance=%d%n", var2.simulationDistance));
         var3.write(String.format(Locale.ROOT, "spawn-animals=%s%n", var2.spawnAnimals));
         var3.write(String.format(Locale.ROOT, "generate-structures=%s%n", var2.worldOptions.generateStructures()));
         var3.write(String.format(Locale.ROOT, "use-native=%s%n", var2.useNativeTransport));
         var3.write(String.format(Locale.ROOT, "rate-limit=%d%n", var2.rateLimitPacketsPerSecond));
      }
   }

   @Override
   public void onServerExit() {
      if (this.textFilterClient != null) {
         this.textFilterClient.close();
      }

      if (this.gui != null) {
         this.gui.close();
      }

      if (this.rconThread != null) {
         this.rconThread.stop();
      }

      if (this.queryThreadGs4 != null) {
         this.queryThreadGs4.stop();
      }
   }

   @Override
   public void tickChildren(BooleanSupplier var1) {
      super.tickChildren(var1);
      this.handleConsoleInputs();
   }

   @Override
   public boolean isNetherEnabled() {
      return this.getProperties().allowNether;
   }

   public void handleConsoleInput(String var1, CommandSourceStack var2) {
      this.consoleInput.add(new ConsoleInput(var1, var2));
   }

   public void handleConsoleInputs() {
      while(!this.consoleInput.isEmpty()) {
         ConsoleInput var1 = this.consoleInput.remove(0);
         this.getCommands().performPrefixedCommand(var1.source, var1.msg);
      }
   }

   @Override
   public boolean isDedicatedServer() {
      return true;
   }

   @Override
   public int getRateLimitPacketsPerSecond() {
      return this.getProperties().rateLimitPacketsPerSecond;
   }

   @Override
   public boolean isEpollEnabled() {
      return this.getProperties().useNativeTransport;
   }

   public DedicatedPlayerList getPlayerList() {
      return (DedicatedPlayerList)super.getPlayerList();
   }

   @Override
   public boolean isPublished() {
      return true;
   }

   @Override
   public String getServerIp() {
      return this.getLocalIp();
   }

   @Override
   public int getServerPort() {
      return this.getPort();
   }

   @Override
   public String getServerName() {
      return this.getMotd();
   }

   public void showGui() {
      if (this.gui == null) {
         this.gui = MinecraftServerGui.showFrameFor(this);
      }
   }

   @Override
   public boolean hasGui() {
      return this.gui != null;
   }

   @Override
   public boolean isCommandBlockEnabled() {
      return this.getProperties().enableCommandBlock;
   }

   @Override
   public int getSpawnProtectionRadius() {
      return this.getProperties().spawnProtection;
   }

   @Override
   public boolean isUnderSpawnProtection(ServerLevel var1, BlockPos var2, Player var3) {
      if (var1.dimension() != Level.OVERWORLD) {
         return false;
      } else if (this.getPlayerList().getOps().isEmpty()) {
         return false;
      } else if (this.getPlayerList().isOp(var3.getGameProfile())) {
         return false;
      } else if (this.getSpawnProtectionRadius() <= 0) {
         return false;
      } else {
         BlockPos var4 = var1.getSharedSpawnPos();
         int var5 = Mth.abs(var2.getX() - var4.getX());
         int var6 = Mth.abs(var2.getZ() - var4.getZ());
         int var7 = Math.max(var5, var6);
         return var7 <= this.getSpawnProtectionRadius();
      }
   }

   @Override
   public boolean repliesToStatus() {
      return this.getProperties().enableStatus;
   }

   @Override
   public boolean hidesOnlinePlayers() {
      return this.getProperties().hideOnlinePlayers;
   }

   @Override
   public int getOperatorUserPermissionLevel() {
      return this.getProperties().opPermissionLevel;
   }

   @Override
   public int getFunctionCompilationLevel() {
      return this.getProperties().functionPermissionLevel;
   }

   @Override
   public void setPlayerIdleTimeout(int var1) {
      super.setPlayerIdleTimeout(var1);
      this.settings.update(var2 -> var2.playerIdleTimeout.update(this.registryAccess(), var1));
   }

   @Override
   public boolean shouldRconBroadcast() {
      return this.getProperties().broadcastRconToOps;
   }

   @Override
   public boolean shouldInformAdmins() {
      return this.getProperties().broadcastConsoleToOps;
   }

   @Override
   public int getAbsoluteMaxWorldSize() {
      return this.getProperties().maxWorldSize;
   }

   @Override
   public int getCompressionThreshold() {
      return this.getProperties().networkCompressionThreshold;
   }

   @Override
   public boolean enforceSecureProfile() {
      DedicatedServerProperties var1 = this.getProperties();
      return var1.enforceSecureProfile && var1.onlineMode && this.services.profileKeySignatureValidator() != null;
   }

   protected boolean convertOldUsers() {
      boolean var2 = false;

      for(int var1 = 0; !var2 && var1 <= 2; ++var1) {
         if (var1 > 0) {
            LOGGER.warn("Encountered a problem while converting the user banlist, retrying in a few seconds");
            this.waitForRetry();
         }

         var2 = OldUsersConverter.convertUserBanlist(this);
      }

      boolean var3 = false;

      for(int var7 = 0; !var3 && var7 <= 2; ++var7) {
         if (var7 > 0) {
            LOGGER.warn("Encountered a problem while converting the ip banlist, retrying in a few seconds");
            this.waitForRetry();
         }

         var3 = OldUsersConverter.convertIpBanlist(this);
      }

      boolean var4 = false;

      for(int var8 = 0; !var4 && var8 <= 2; ++var8) {
         if (var8 > 0) {
            LOGGER.warn("Encountered a problem while converting the op list, retrying in a few seconds");
            this.waitForRetry();
         }

         var4 = OldUsersConverter.convertOpsList(this);
      }

      boolean var5 = false;

      for(int var9 = 0; !var5 && var9 <= 2; ++var9) {
         if (var9 > 0) {
            LOGGER.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
            this.waitForRetry();
         }

         var5 = OldUsersConverter.convertWhiteList(this);
      }

      boolean var6 = false;

      for(int var10 = 0; !var6 && var10 <= 2; ++var10) {
         if (var10 > 0) {
            LOGGER.warn("Encountered a problem while converting the player save files, retrying in a few seconds");
            this.waitForRetry();
         }

         var6 = OldUsersConverter.convertPlayers(this);
      }

      return var2 || var3 || var4 || var5 || var6;
   }

   private void waitForRetry() {
      try {
         Thread.sleep(5000L);
      } catch (InterruptedException var2) {
      }
   }

   public long getMaxTickLength() {
      return this.getProperties().maxTickTime;
   }

   @Override
   public int getMaxChainedNeighborUpdates() {
      return this.getProperties().maxChainedNeighborUpdates;
   }

   @Override
   public String getPluginNames() {
      return "";
   }

   @Override
   public String runCommand(String var1) {
      this.rconConsoleSource.prepareForCommand();
      this.executeBlocking(() -> this.getCommands().performPrefixedCommand(this.rconConsoleSource.createCommandSourceStack(), var1));
      return this.rconConsoleSource.getCommandResponse();
   }

   public void storeUsingWhiteList(boolean var1) {
      this.settings.update(var2 -> var2.whiteList.update(this.registryAccess(), var1));
   }

   @Override
   public void stopServer() {
      super.stopServer();
      Util.shutdownExecutors();
      SkullBlockEntity.clear();
   }

   @Override
   public boolean isSingleplayerOwner(GameProfile var1) {
      return false;
   }

   @Override
   public int getScaledTrackingDistance(int var1) {
      return this.getProperties().entityBroadcastRangePercentage * var1 / 100;
   }

   @Override
   public String getLevelIdName() {
      return this.storageSource.getLevelId();
   }

   @Override
   public boolean forceSynchronousWrites() {
      return this.settings.getProperties().syncChunkWrites;
   }

   @Override
   public TextFilter createTextFilterForPlayer(ServerPlayer var1) {
      return this.textFilterClient != null ? this.textFilterClient.createContext(var1.getGameProfile()) : TextFilter.DUMMY;
   }

   @Nullable
   @Override
   public GameType getForcedGameType() {
      return this.settings.getProperties().forceGameMode ? this.worldData.getGameType() : null;
   }

   @Override
   public Optional<MinecraftServer.ServerResourcePackInfo> getServerResourcePack() {
      return this.settings.getProperties().serverResourcePackInfo;
   }
}
