package net.minecraft.server.dedicated;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import io.netty.handler.ssl.SslContext;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;
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
import net.minecraft.server.ServerLinks;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.gui.MinecraftServerGui;
import net.minecraft.server.jsonrpc.JsonRpcNotificationService;
import net.minecraft.server.jsonrpc.ManagementServer;
import net.minecraft.server.jsonrpc.internalapi.MinecraftApi;
import net.minecraft.server.jsonrpc.security.AuthenticationHandler;
import net.minecraft.server.jsonrpc.security.JsonRpcSslContextProvider;
import net.minecraft.server.jsonrpc.security.SecurityConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.LoggingLevelLoadListener;
import net.minecraft.server.network.ServerTextFilter;
import net.minecraft.server.network.TextFilter;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.rcon.RconConsoleSource;
import net.minecraft.server.rcon.thread.QueryThreadGs4;
import net.minecraft.server.rcon.thread.RconThread;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debugchart.RemoteDebugSampleType;
import net.minecraft.util.debugchart.RemoteSampleLogger;
import net.minecraft.util.debugchart.SampleLogger;
import net.minecraft.util.debugchart.TpsDebugDimensions;
import net.minecraft.util.monitoring.jmx.MinecraftServerStatistics;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.LevelData;
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
   private final ServerTextFilter serverTextFilter;
   @Nullable
   private RemoteSampleLogger tickTimeLogger;
   private boolean isTickTimeLoggingEnabled;
   private final ServerLinks serverLinks;
   private final Map<String, String> codeOfConductTexts;
   @Nullable
   private ManagementServer jsonRpcServer;
   private long lastHeartbeat;

   public DedicatedServer(Thread var1, LevelStorageSource.LevelStorageAccess var2, PackRepository var3, WorldStem var4, DedicatedServerSettings var5, DataFixer var6, Services var7) {
      super(var1, var2, var3, var4, Proxy.NO_PROXY, var6, var7, LoggingLevelLoadListener.forDedicatedServer());
      this.settings = var5;
      this.rconConsoleSource = new RconConsoleSource(this);
      this.serverTextFilter = ServerTextFilter.createFromConfig(var5.getProperties());
      this.serverLinks = createServerLinks(var5);
      if (var5.getProperties().codeOfConduct) {
         this.codeOfConductTexts = readCodeOfConducts();
      } else {
         this.codeOfConductTexts = Map.of();
      }

   }

   private static Map<String, String> readCodeOfConducts() {
      Path var0 = Path.of("codeofconduct");
      if (!Files.isDirectory(var0, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
         throw new IllegalArgumentException("Code of Conduct folder does not exist: " + String.valueOf(var0));
      } else {
         try {
            ImmutableMap.Builder var1 = ImmutableMap.builder();
            Stream var2 = Files.list(var0);

            try {
               for(Path var4 : var2.toList()) {
                  String var5 = var4.getFileName().toString();
                  if (var5.endsWith(".txt")) {
                     String var6 = var5.substring(0, var5.length() - 4).toLowerCase(Locale.ROOT);
                     if (!var4.toRealPath().getParent().equals(var0.toAbsolutePath())) {
                        throw new IllegalArgumentException("Failed to read Code of Conduct file \"" + var5 + "\" because it links to a file outside the allowed directory");
                     }

                     try {
                        String var7 = String.join("\n", Files.readAllLines(var4, StandardCharsets.UTF_8));
                        var1.put(var6, StringUtil.stripColor(var7));
                     } catch (IOException var9) {
                        throw new IllegalArgumentException("Failed to read Code of Conduct file " + var5, var9);
                     }
                  }
               }
            } catch (Throwable var10) {
               if (var2 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var8) {
                     var10.addSuppressed(var8);
                  }
               }

               throw var10;
            }

            if (var2 != null) {
               var2.close();
            }

            return var1.build();
         } catch (IOException var11) {
            throw new IllegalArgumentException("Failed to read Code of Conduct folder", var11);
         }
      }
   }

   private SslContext createSslContext() {
      try {
         return JsonRpcSslContextProvider.createFrom(this.getProperties().managementServerTlsKeystore, this.getProperties().managementServerTlsKeystorePassword);
      } catch (Exception var2) {
         JsonRpcSslContextProvider.printInstructions();
         throw new IllegalStateException("Failed to configure TLS for the server management protocol", var2);
      }
   }

   public boolean initServer() throws IOException {
      int var1 = this.getProperties().managementServerPort;
      if (this.getProperties().managementServerEnabled) {
         String var2 = this.settings.getProperties().managementServerSecret;
         if (!SecurityConfig.isValid(var2)) {
            throw new IllegalStateException("Invalid management server secret, must be 40 alphanumeric characters");
         }

         String var3 = this.getProperties().managementServerHost;
         HostAndPort var4 = HostAndPort.fromParts(var3, var1);
         SecurityConfig var5 = new SecurityConfig(var2);
         AuthenticationHandler var6 = new AuthenticationHandler(var5);
         LOGGER.info("Starting json RPC server on {}", var4);
         this.jsonRpcServer = new ManagementServer(var4, var6);
         MinecraftApi var7 = MinecraftApi.of(this);
         var7.notificationManager().registerService(new JsonRpcNotificationService(var7, this.jsonRpcServer));
         if (this.getProperties().managementServerTlsEnabled) {
            SslContext var8 = this.createSslContext();
            this.jsonRpcServer.startWithTls(var7, var8);
         } else {
            this.jsonRpcServer.startWithoutTls(var7);
         }
      }

      Thread var12 = new Thread("Server console handler") {
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
      var12.setDaemon(true);
      var12.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      var12.start();
      LOGGER.info("Starting minecraft server version {}", SharedConstants.getCurrentVersion().name());
      if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
         LOGGER.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
      }

      LOGGER.info("Loading properties");
      DedicatedServerProperties var13 = this.settings.getProperties();
      if (this.isSingleplayer()) {
         this.setLocalIp("127.0.0.1");
      } else {
         this.setUsesAuthentication(var13.onlineMode);
         this.setPreventProxyConnections(var13.preventProxyConnections);
         this.setLocalIp(var13.serverIp);
      }

      this.worldData.setGameType(var13.gameMode.get());
      LOGGER.info("Default game type: {}", var13.gameMode.get());
      InetAddress var14 = null;
      if (!this.getLocalIp().isEmpty()) {
         var14 = InetAddress.getByName(this.getLocalIp());
      }

      if (this.getPort() < 0) {
         this.setPort(var13.serverPort);
      }

      this.initializeKeyPair();
      LOGGER.info("Starting Minecraft server on {}:{}", this.getLocalIp().isEmpty() ? "*" : this.getLocalIp(), this.getPort());

      try {
         this.getConnection().startTcpServerListener(var14, this.getPort());
      } catch (IOException var11) {
         LOGGER.warn("**** FAILED TO BIND TO PORT!");
         LOGGER.warn("The exception was: {}", var11.toString());
         LOGGER.warn("Perhaps a server is already running on that port?");
         return false;
      }

      if (!this.usesAuthentication()) {
         LOGGER.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
         LOGGER.warn("The server will make no attempt to authenticate usernames. Beware.");
         LOGGER.warn("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
         LOGGER.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
      }

      if (this.convertOldUsers()) {
         this.services.nameToIdCache().save();
      }

      if (!OldUsersConverter.serverReadyAfterUserconversion(this)) {
         return false;
      } else {
         this.setPlayerList(new DedicatedPlayerList(this, this.registries(), this.playerDataStorage));
         this.tickTimeLogger = new RemoteSampleLogger(TpsDebugDimensions.values().length, this.debugSubscribers(), RemoteDebugSampleType.TICK_TIME);
         long var15 = Util.getNanos();
         this.services.nameToIdCache().resolveOfflineUsers(!this.usesAuthentication());
         LOGGER.info("Preparing level \"{}\"", this.getLevelIdName());
         this.loadLevel();
         long var16 = Util.getNanos() - var15;
         String var9 = String.format(Locale.ROOT, "%.3fs", (double)var16 / 1.0E9);
         LOGGER.info("Done ({})! For help, type \"help\"", var9);
         if (var13.announcePlayerAchievements != null) {
            ((GameRules.BooleanValue)this.getGameRules().getRule(GameRules.RULE_ANNOUNCE_ADVANCEMENTS)).set(var13.announcePlayerAchievements, this);
         }

         if (var13.enableQuery) {
            LOGGER.info("Starting GS4 status listener");
            this.queryThreadGs4 = QueryThreadGs4.create(this);
         }

         if (var13.enableRcon) {
            LOGGER.info("Starting remote control listener");
            this.rconThread = RconThread.create(this);
         }

         if (this.getMaxTickLength() > 0L) {
            Thread var10 = new Thread(new ServerWatchdog(this));
            var10.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandlerWithName(LOGGER));
            var10.setName("Server Watchdog");
            var10.setDaemon(true);
            var10.start();
         }

         if (var13.enableJmxMonitoring) {
            MinecraftServerStatistics.registerJmxMonitoring(this);
            LOGGER.info("JMX monitoring enabled");
         }

         this.notificationManager().serverStarted();
         return true;
      }
   }

   public boolean isEnforceWhitelist() {
      return (Boolean)this.settings.getProperties().enforceWhitelist.get();
   }

   public void setEnforceWhitelist(boolean var1) {
      this.settings.update((var2) -> (DedicatedServerProperties)var2.enforceWhitelist.update(this.registryAccess(), var1));
   }

   public boolean isUsingWhitelist() {
      return (Boolean)this.settings.getProperties().whiteList.get();
   }

   public void setUsingWhitelist(boolean var1) {
      this.settings.update((var2) -> (DedicatedServerProperties)var2.whiteList.update(this.registryAccess(), var1));
   }

   public void tickServer(BooleanSupplier var1) {
      super.tickServer(var1);
      if (this.jsonRpcServer != null) {
         this.jsonRpcServer.tick();
      }

      long var2 = Util.getMillis();
      int var4 = this.statusHeartbeatInterval();
      if (var4 > 0) {
         long var5 = (long)var4 * TimeUtil.MILLISECONDS_PER_SECOND;
         if (var2 - this.lastHeartbeat >= var5) {
            this.lastHeartbeat = var2;
            this.notificationManager().statusHeartbeat();
         }
      }

   }

   public boolean saveAllChunks(boolean var1, boolean var2, boolean var3) {
      this.notificationManager().serverSaveStarted();
      boolean var4 = super.saveAllChunks(var1, var2, var3);
      this.notificationManager().serverSaveCompleted();
      return var4;
   }

   public boolean allowFlight() {
      return (Boolean)this.settings.getProperties().allowFlight.get();
   }

   public void setAllowFlight(boolean var1) {
      this.settings.update((var2) -> (DedicatedServerProperties)var2.allowFlight.update(this.registryAccess(), var1));
   }

   public DedicatedServerProperties getProperties() {
      return this.settings.getProperties();
   }

   public void setDifficulty(Difficulty var1) {
      this.settings.update((var2) -> (DedicatedServerProperties)var2.difficulty.update(this.registryAccess(), var1));
      this.forceDifficulty();
   }

   public void forceDifficulty() {
      this.setDifficulty(this.getProperties().difficulty.get(), true);
   }

   public int viewDistance() {
      return (Integer)this.settings.getProperties().viewDistance.get();
   }

   public void setViewDistance(int var1) {
      this.settings.update((var2) -> (DedicatedServerProperties)var2.viewDistance.update(this.registryAccess(), var1));
      this.getPlayerList().setViewDistance(var1);
   }

   public int simulationDistance() {
      return (Integer)this.settings.getProperties().simulationDistance.get();
   }

   public void setSimulationDistance(int var1) {
      this.settings.update((var2) -> (DedicatedServerProperties)var2.simulationDistance.update(this.registryAccess(), var1));
      this.getPlayerList().setSimulationDistance(var1);
   }

   public SystemReport fillServerSystemReport(SystemReport var1) {
      var1.setDetail("Is Modded", (Supplier)(() -> this.getModdedStatus().fullDescription()));
      var1.setDetail("Type", (Supplier)(() -> "Dedicated Server (map_server.txt)"));
      return var1;
   }

   public void dumpServerProperties(Path var1) throws IOException {
      DedicatedServerProperties var2 = this.getProperties();
      BufferedWriter var3 = Files.newBufferedWriter(var1);

      try {
         ((Writer)var3).write(String.format(Locale.ROOT, "sync-chunk-writes=%s%n", var2.syncChunkWrites));
         ((Writer)var3).write(String.format(Locale.ROOT, "gamemode=%s%n", var2.gameMode.get()));
         ((Writer)var3).write(String.format(Locale.ROOT, "entity-broadcast-range-percentage=%d%n", var2.entityBroadcastRangePercentage.get()));
         ((Writer)var3).write(String.format(Locale.ROOT, "max-world-size=%d%n", var2.maxWorldSize));
         ((Writer)var3).write(String.format(Locale.ROOT, "view-distance=%d%n", var2.viewDistance.get()));
         ((Writer)var3).write(String.format(Locale.ROOT, "simulation-distance=%d%n", var2.simulationDistance.get()));
         ((Writer)var3).write(String.format(Locale.ROOT, "generate-structures=%s%n", var2.worldOptions.generateStructures()));
         ((Writer)var3).write(String.format(Locale.ROOT, "use-native=%s%n", var2.useNativeTransport));
         ((Writer)var3).write(String.format(Locale.ROOT, "rate-limit=%d%n", var2.rateLimitPacketsPerSecond));
      } catch (Throwable var7) {
         if (var3 != null) {
            try {
               ((Writer)var3).close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (var3 != null) {
         ((Writer)var3).close();
      }

   }

   public void onServerExit() {
      if (this.serverTextFilter != null) {
         this.serverTextFilter.close();
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

      if (this.jsonRpcServer != null) {
         try {
            this.jsonRpcServer.stop(true);
         } catch (InterruptedException var2) {
            LOGGER.error("Interrupted while stopping the management server", var2);
         }
      }

   }

   public void tickConnection() {
      super.tickConnection();
      this.handleConsoleInputs();
   }

   public void handleConsoleInput(String var1, CommandSourceStack var2) {
      this.consoleInput.add(new ConsoleInput(var1, var2));
   }

   public void handleConsoleInputs() {
      while(!this.consoleInput.isEmpty()) {
         ConsoleInput var1 = (ConsoleInput)this.consoleInput.remove(0);
         this.getCommands().performPrefixedCommand(var1.source, var1.msg);
      }

   }

   public boolean isDedicatedServer() {
      return true;
   }

   public int getRateLimitPacketsPerSecond() {
      return this.getProperties().rateLimitPacketsPerSecond;
   }

   public boolean isEpollEnabled() {
      return this.getProperties().useNativeTransport;
   }

   public DedicatedPlayerList getPlayerList() {
      return (DedicatedPlayerList)super.getPlayerList();
   }

   public int getMaxPlayers() {
      return (Integer)this.settings.getProperties().maxPlayers.get();
   }

   public void setMaxPlayers(int var1) {
      this.settings.update((var2) -> (DedicatedServerProperties)var2.maxPlayers.update(this.registryAccess(), var1));
   }

   public boolean isPublished() {
      return true;
   }

   public String getServerIp() {
      return this.getLocalIp();
   }

   public int getServerPort() {
      return this.getPort();
   }

   public String getServerName() {
      return this.getMotd();
   }

   public void showGui() {
      if (this.gui == null) {
         this.gui = MinecraftServerGui.showFrameFor(this);
      }

   }

   public boolean hasGui() {
      return this.gui != null;
   }

   public int spawnProtectionRadius() {
      return (Integer)this.getProperties().spawnProtection.get();
   }

   public void setSpawnProtectionRadius(int var1) {
      this.settings.update((var2) -> (DedicatedServerProperties)var2.spawnProtection.update(this.registryAccess(), var1));
   }

   public boolean isUnderSpawnProtection(ServerLevel var1, BlockPos var2, Player var3) {
      LevelData.RespawnData var4 = var1.getRespawnData();
      if (var1.dimension() != var4.dimension()) {
         return false;
      } else if (this.getPlayerList().getOps().isEmpty()) {
         return false;
      } else if (this.getPlayerList().isOp(var3.nameAndId())) {
         return false;
      } else if (this.spawnProtectionRadius() <= 0) {
         return false;
      } else {
         BlockPos var5 = var4.pos();
         int var6 = Mth.abs(var2.getX() - var5.getX());
         int var7 = Mth.abs(var2.getZ() - var5.getZ());
         int var8 = Math.max(var6, var7);
         return var8 <= this.spawnProtectionRadius();
      }
   }

   public boolean repliesToStatus() {
      return (Boolean)this.getProperties().enableStatus.get();
   }

   public void setRepliesToStatus(boolean var1) {
      this.settings.update((var2) -> (DedicatedServerProperties)var2.enableStatus.update(this.registryAccess(), var1));
   }

   public boolean hidesOnlinePlayers() {
      return (Boolean)this.getProperties().hideOnlinePlayers.get();
   }

   public void setHidesOnlinePlayers(boolean var1) {
      this.settings.update((var2) -> (DedicatedServerProperties)var2.hideOnlinePlayers.update(this.registryAccess(), var1));
   }

   public int operatorUserPermissionLevel() {
      return (Integer)this.getProperties().opPermissionLevel.get();
   }

   public void setOperatorUserPermissionLevel(int var1) {
      this.settings.update((var2) -> (DedicatedServerProperties)var2.opPermissionLevel.update(this.registryAccess(), var1));
   }

   public int getFunctionCompilationLevel() {
      return this.getProperties().functionPermissionLevel;
   }

   public int playerIdleTimeout() {
      return (Integer)this.settings.getProperties().playerIdleTimeout.get();
   }

   public void setPlayerIdleTimeout(int var1) {
      this.settings.update((var2) -> (DedicatedServerProperties)var2.playerIdleTimeout.update(this.registryAccess(), var1));
   }

   public int statusHeartbeatInterval() {
      return (Integer)this.settings.getProperties().statusHeartbeatInterval.get();
   }

   public void setStatusHeartbeatInterval(int var1) {
      this.settings.update((var2) -> (DedicatedServerProperties)var2.statusHeartbeatInterval.update(this.registryAccess(), var1));
   }

   public String getMotd() {
      return this.settings.getProperties().motd.get();
   }

   public void setMotd(String var1) {
      this.settings.update((var2) -> (DedicatedServerProperties)var2.motd.update(this.registryAccess(), var1));
   }

   public boolean shouldRconBroadcast() {
      return this.getProperties().broadcastRconToOps;
   }

   public boolean shouldInformAdmins() {
      return this.getProperties().broadcastConsoleToOps;
   }

   public int getAbsoluteMaxWorldSize() {
      return this.getProperties().maxWorldSize;
   }

   public int getCompressionThreshold() {
      return this.getProperties().networkCompressionThreshold;
   }

   public boolean enforceSecureProfile() {
      DedicatedServerProperties var1 = this.getProperties();
      return var1.enforceSecureProfile && var1.onlineMode && this.services.canValidateProfileKeys();
   }

   public boolean logIPs() {
      return this.getProperties().logIPs;
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

   public int getMaxChainedNeighborUpdates() {
      return this.getProperties().maxChainedNeighborUpdates;
   }

   public String getPluginNames() {
      return "";
   }

   public String runCommand(String var1) {
      this.rconConsoleSource.prepareForCommand();
      this.executeBlocking(() -> this.getCommands().performPrefixedCommand(this.rconConsoleSource.createCommandSourceStack(), var1));
      return this.rconConsoleSource.getCommandResponse();
   }

   public void stopServer() {
      this.notificationManager().serverShuttingDown();
      super.stopServer();
      Util.shutdownExecutors();
   }

   public boolean isSingleplayerOwner(NameAndId var1) {
      return false;
   }

   public int getScaledTrackingDistance(int var1) {
      return this.entityBroadcastRangePercentage() * var1 / 100;
   }

   public int entityBroadcastRangePercentage() {
      return (Integer)this.getProperties().entityBroadcastRangePercentage.get();
   }

   public void setEntityBroadcastRangePercentage(int var1) {
      this.settings.update((var2) -> (DedicatedServerProperties)var2.entityBroadcastRangePercentage.update(this.registryAccess(), var1));
   }

   public String getLevelIdName() {
      return this.storageSource.getLevelId();
   }

   public boolean forceSynchronousWrites() {
      return this.settings.getProperties().syncChunkWrites;
   }

   public TextFilter createTextFilterForPlayer(ServerPlayer var1) {
      return this.serverTextFilter != null ? this.serverTextFilter.createContext(var1.getGameProfile()) : TextFilter.DUMMY;
   }

   @Nullable
   public GameType getForcedGameType() {
      return this.forceGameMode() ? this.worldData.getGameType() : null;
   }

   public boolean forceGameMode() {
      return (Boolean)this.settings.getProperties().forceGameMode.get();
   }

   public void setForceGameMode(boolean var1) {
      this.settings.update((var2) -> (DedicatedServerProperties)var2.forceGameMode.update(this.registryAccess(), var1));
      this.enforceGameTypeForPlayers(this.getForcedGameType());
   }

   public GameType gameMode() {
      return this.getProperties().gameMode.get();
   }

   public void setGameMode(GameType var1) {
      this.settings.update((var2) -> (DedicatedServerProperties)var2.gameMode.update(this.registryAccess(), var1));
      this.worldData.setGameType(this.gameMode());
      this.enforceGameTypeForPlayers(this.getForcedGameType());
   }

   public Optional<MinecraftServer.ServerResourcePackInfo> getServerResourcePack() {
      return this.settings.getProperties().serverResourcePackInfo;
   }

   public void endMetricsRecordingTick() {
      super.endMetricsRecordingTick();
      this.isTickTimeLoggingEnabled = this.debugSubscribers().hasAnySubscriberFor(DebugSubscriptions.DEDICATED_SERVER_TICK_TIME);
   }

   public SampleLogger getTickTimeLogger() {
      return this.tickTimeLogger;
   }

   public boolean isTickTimeLoggingEnabled() {
      return this.isTickTimeLoggingEnabled;
   }

   public boolean acceptsTransfers() {
      return (Boolean)this.settings.getProperties().acceptsTransfers.get();
   }

   public void setAcceptsTransfers(boolean var1) {
      this.settings.update((var2) -> (DedicatedServerProperties)var2.acceptsTransfers.update(this.registryAccess(), var1));
   }

   public ServerLinks serverLinks() {
      return this.serverLinks;
   }

   public int pauseWhenEmptySeconds() {
      return (Integer)this.settings.getProperties().pauseWhenEmptySeconds.get();
   }

   public void setPauseWhenEmptySeconds(int var1) {
      this.settings.update((var2) -> (DedicatedServerProperties)var2.pauseWhenEmptySeconds.update(this.registryAccess(), var1));
   }

   private static ServerLinks createServerLinks(DedicatedServerSettings var0) {
      Optional var1 = parseBugReportLink(var0.getProperties());
      return (ServerLinks)var1.map((var0x) -> new ServerLinks(List.of(ServerLinks.KnownLinkType.BUG_REPORT.create(var0x)))).orElse(ServerLinks.EMPTY);
   }

   private static Optional<URI> parseBugReportLink(DedicatedServerProperties var0) {
      String var1 = var0.bugReportLink;
      if (var1.isEmpty()) {
         return Optional.empty();
      } else {
         try {
            return Optional.of(Util.parseAndValidateUntrustedUri(var1));
         } catch (Exception var3) {
            LOGGER.warn("Failed to parse bug link {}", var1, var3);
            return Optional.empty();
         }
      }
   }

   public Map<String, String> getCodeOfConducts() {
      return this.codeOfConductTexts;
   }

   // $FF: synthetic method
   public PlayerList getPlayerList() {
      return this.getPlayerList();
   }
}
