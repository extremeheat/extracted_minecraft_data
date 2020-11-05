package net.minecraft.server.dedicated;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.DefaultUncaughtExceptionHandlerWithName;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ConsoleInput;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerInterface;
import net.minecraft.server.ServerResources;
import net.minecraft.server.gui.MinecraftServerGui;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.network.TextFilter;
import net.minecraft.server.network.TextFilterClient;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.rcon.RconConsoleSource;
import net.minecraft.server.rcon.thread.QueryThreadGs4;
import net.minecraft.server.rcon.thread.RconThread;
import net.minecraft.util.Mth;
import net.minecraft.util.monitoring.jmx.MinecraftServerStatistics;
import net.minecraft.world.Snooper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DedicatedServer extends MinecraftServer implements ServerInterface {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Pattern SHA1 = Pattern.compile("^[a-fA-F0-9]{40}$");
   private final List<ConsoleInput> consoleInput = Collections.synchronizedList(Lists.newArrayList());
   private QueryThreadGs4 queryThreadGs4;
   private final RconConsoleSource rconConsoleSource;
   private RconThread rconThread;
   private final DedicatedServerSettings settings;
   @Nullable
   private MinecraftServerGui gui;
   @Nullable
   private final TextFilterClient textFilterClient;

   public DedicatedServer(Thread var1, RegistryAccess.RegistryHolder var2, LevelStorageSource.LevelStorageAccess var3, PackRepository var4, ServerResources var5, WorldData var6, DedicatedServerSettings var7, DataFixer var8, MinecraftSessionService var9, GameProfileRepository var10, GameProfileCache var11, ChunkProgressListenerFactory var12) {
      super(var1, var2, var3, var6, var4, Proxy.NO_PROXY, var8, var5, var9, var10, var11, var12);
      this.settings = var7;
      this.rconConsoleSource = new RconConsoleSource(this);
      this.textFilterClient = null;
   }

   public boolean initServer() throws IOException {
      Thread var1 = new Thread("Server console handler") {
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
      LOGGER.info("Starting minecraft server version " + SharedConstants.getCurrentVersion().getName());
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
      this.setResourcePack(var2.resourcePack, this.getPackHash());
      this.setMotd(var2.motd);
      this.setForceGameType(var2.forceGameMode);
      super.setPlayerIdleTimeout((Integer)var2.playerIdleTimeout.get());
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
         LOGGER.warn("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
         LOGGER.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
      }

      if (this.convertOldUsers()) {
         this.getProfileCache().save();
      }

      if (!OldUsersConverter.serverReadyAfterUserconversion(this)) {
         return false;
      } else {
         this.setPlayerList(new DedicatedPlayerList(this, this.registryHolder, this.playerDataStorage));
         long var4 = Util.getNanos();
         this.setMaxBuildHeight(var2.maxBuildHeight);
         SkullBlockEntity.setProfileCache(this.getProfileCache());
         SkullBlockEntity.setSessionService(this.getSessionService());
         GameProfileCache.setUsesAuthentication(this.usesAuthentication());
         LOGGER.info("Preparing level \"{}\"", this.getLevelIdName());
         this.loadLevel();
         long var6 = Util.getNanos() - var4;
         String var8 = String.format(Locale.ROOT, "%.3fs", (double)var6 / 1.0E9D);
         LOGGER.info("Done ({})! For help, type \"help\"", var8);
         if (var2.announcePlayerAchievements != null) {
            ((GameRules.BooleanValue)this.getGameRules().getRule(GameRules.RULE_ANNOUNCE_ADVANCEMENTS)).set(var2.announcePlayerAchievements, this);
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

         Items.AIR.fillItemCategory(CreativeModeTab.TAB_SEARCH, NonNullList.create());
         if (var2.enableJmxMonitoring) {
            MinecraftServerStatistics.registerJmxMonitoring(this);
         }

         return true;
      }
   }

   public boolean isSpawningAnimals() {
      return this.getProperties().spawnAnimals && super.isSpawningAnimals();
   }

   public boolean isSpawningMonsters() {
      return this.settings.getProperties().spawnMonsters && super.isSpawningMonsters();
   }

   public boolean areNpcsEnabled() {
      return this.settings.getProperties().spawnNpcs && super.areNpcsEnabled();
   }

   public String getPackHash() {
      DedicatedServerProperties var1 = this.settings.getProperties();
      String var2;
      if (!var1.resourcePackSha1.isEmpty()) {
         var2 = var1.resourcePackSha1;
         if (!Strings.isNullOrEmpty(var1.resourcePackHash)) {
            LOGGER.warn("resource-pack-hash is deprecated and found along side resource-pack-sha1. resource-pack-hash will be ignored.");
         }
      } else if (!Strings.isNullOrEmpty(var1.resourcePackHash)) {
         LOGGER.warn("resource-pack-hash is deprecated. Please use resource-pack-sha1 instead.");
         var2 = var1.resourcePackHash;
      } else {
         var2 = "";
      }

      if (!var2.isEmpty() && !SHA1.matcher(var2).matches()) {
         LOGGER.warn("Invalid sha1 for ressource-pack-sha1");
      }

      if (!var1.resourcePack.isEmpty() && var2.isEmpty()) {
         LOGGER.warn("You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack.");
      }

      return var2;
   }

   public DedicatedServerProperties getProperties() {
      return this.settings.getProperties();
   }

   public void forceDifficulty() {
      this.setDifficulty(this.getProperties().difficulty, true);
   }

   public boolean isHardcore() {
      return this.getProperties().hardcore;
   }

   public CrashReport fillReport(CrashReport var1) {
      var1 = super.fillReport(var1);
      var1.getSystemDetails().setDetail("Is Modded", () -> {
         return (String)this.getModdedStatus().orElse("Unknown (can't tell)");
      });
      var1.getSystemDetails().setDetail("Type", () -> {
         return "Dedicated Server (map_server.txt)";
      });
      return var1;
   }

   public Optional<String> getModdedStatus() {
      String var1 = this.getServerModName();
      return !"vanilla".equals(var1) ? Optional.of("Definitely; Server brand changed to '" + var1 + "'") : Optional.empty();
   }

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

   public void tickChildren(BooleanSupplier var1) {
      super.tickChildren(var1);
      this.handleConsoleInputs();
   }

   public boolean isNetherEnabled() {
      return this.getProperties().allowNether;
   }

   public void populateSnooper(Snooper var1) {
      var1.setDynamicData("whitelist_enabled", this.getPlayerList().isUsingWhitelist());
      var1.setDynamicData("whitelist_count", this.getPlayerList().getWhiteListNames().length);
      super.populateSnooper(var1);
   }

   public void handleConsoleInput(String var1, CommandSourceStack var2) {
      this.consoleInput.add(new ConsoleInput(var1, var2));
   }

   public void handleConsoleInputs() {
      while(!this.consoleInput.isEmpty()) {
         ConsoleInput var1 = (ConsoleInput)this.consoleInput.remove(0);
         this.getCommands().performCommand(var1.source, var1.msg);
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

   public boolean publishServer(GameType var1, boolean var2, int var3) {
      return false;
   }

   public boolean isCommandBlockEnabled() {
      return this.getProperties().enableCommandBlock;
   }

   public int getSpawnProtectionRadius() {
      return this.getProperties().spawnProtection;
   }

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

   public boolean repliesToStatus() {
      return this.getProperties().enableStatus;
   }

   public int getOperatorUserPermissionLevel() {
      return this.getProperties().opPermissionLevel;
   }

   public int getFunctionCompilationLevel() {
      return this.getProperties().functionPermissionLevel;
   }

   public void setPlayerIdleTimeout(int var1) {
      super.setPlayerIdleTimeout(var1);
      this.settings.update((var2) -> {
         return (DedicatedServerProperties)var2.playerIdleTimeout.update(this.registryAccess(), var1);
      });
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

   protected boolean convertOldUsers() {
      boolean var2 = false;

      int var1;
      for(var1 = 0; !var2 && var1 <= 2; ++var1) {
         if (var1 > 0) {
            LOGGER.warn("Encountered a problem while converting the user banlist, retrying in a few seconds");
            this.waitForRetry();
         }

         var2 = OldUsersConverter.convertUserBanlist(this);
      }

      boolean var3 = false;

      for(var1 = 0; !var3 && var1 <= 2; ++var1) {
         if (var1 > 0) {
            LOGGER.warn("Encountered a problem while converting the ip banlist, retrying in a few seconds");
            this.waitForRetry();
         }

         var3 = OldUsersConverter.convertIpBanlist(this);
      }

      boolean var4 = false;

      for(var1 = 0; !var4 && var1 <= 2; ++var1) {
         if (var1 > 0) {
            LOGGER.warn("Encountered a problem while converting the op list, retrying in a few seconds");
            this.waitForRetry();
         }

         var4 = OldUsersConverter.convertOpsList(this);
      }

      boolean var5 = false;

      for(var1 = 0; !var5 && var1 <= 2; ++var1) {
         if (var1 > 0) {
            LOGGER.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
            this.waitForRetry();
         }

         var5 = OldUsersConverter.convertWhiteList(this);
      }

      boolean var6 = false;

      for(var1 = 0; !var6 && var1 <= 2; ++var1) {
         if (var1 > 0) {
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

   public String getPluginNames() {
      return "";
   }

   public String runCommand(String var1) {
      this.rconConsoleSource.prepareForCommand();
      this.executeBlocking(() -> {
         this.getCommands().performCommand(this.rconConsoleSource.createCommandSourceStack(), var1);
      });
      return this.rconConsoleSource.getCommandResponse();
   }

   public void storeUsingWhiteList(boolean var1) {
      this.settings.update((var2) -> {
         return (DedicatedServerProperties)var2.whiteList.update(this.registryAccess(), var1);
      });
   }

   public void stopServer() {
      super.stopServer();
      Util.shutdownExecutors();
   }

   public boolean isSingleplayerOwner(GameProfile var1) {
      return false;
   }

   public int getScaledTrackingDistance(int var1) {
      return this.getProperties().entityBroadcastRangePercentage * var1 / 100;
   }

   public String getLevelIdName() {
      return this.storageSource.getLevelId();
   }

   public boolean forceSynchronousWrites() {
      return this.settings.getProperties().syncChunkWrites;
   }

   @Nullable
   public TextFilter createTextFilterForPlayer(ServerPlayer var1) {
      return this.textFilterClient != null ? this.textFilterClient.createContext(var1.getGameProfile()) : null;
   }

   // $FF: synthetic method
   public PlayerList getPlayerList() {
      return this.getPlayerList();
   }
}
