package net.minecraft.server;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;
import net.minecraft.CrashReport;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.server.level.DerivedServerLevel;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.level.progress.LoggerChunkProgressListener;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.repository.UnopenedPack;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.tags.TagManager;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.Mth;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.Unit;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.profiling.GameProfiler;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.util.worldupdate.WorldUpgrader;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Snooper;
import net.minecraft.world.SnooperPopulator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ForcedChunksSavedData;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelConflictException;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SaveDataDirtyRunnable;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.ScoreboardSaveData;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinecraftServer extends ReentrantBlockableEventLoop<TickTask> implements SnooperPopulator, CommandSource, AutoCloseable, Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final File USERID_CACHE_FILE = new File("usercache.json");
   private static final CompletableFuture<Unit> DATA_RELOAD_INITIAL_TASK;
   public static final LevelSettings DEMO_SETTINGS;
   private final LevelStorageSource storageSource;
   private final Snooper snooper = new Snooper("server", this, Util.getMillis());
   private final File universe;
   private final List<Runnable> tickables = Lists.newArrayList();
   private final GameProfiler profiler = new GameProfiler(this::getTickCount);
   private final ServerConnectionListener connection;
   protected final ChunkProgressListenerFactory progressListenerFactory;
   private final ServerStatus status = new ServerStatus();
   private final Random random = new Random();
   private final DataFixer fixerUpper;
   private String localIp;
   private int port = -1;
   private final Map<DimensionType, ServerLevel> levels = Maps.newIdentityHashMap();
   private PlayerList playerList;
   private volatile boolean running = true;
   private boolean stopped;
   private int tickCount;
   protected final Proxy proxy;
   private boolean onlineMode;
   private boolean preventProxyConnections;
   private boolean animals;
   private boolean npcs;
   private boolean pvp;
   private boolean allowFlight;
   @Nullable
   private String motd;
   private int maxBuildHeight;
   private int playerIdleTimeout;
   public final long[] tickTimes = new long[100];
   @Nullable
   private KeyPair keyPair;
   @Nullable
   private String singleplayerName;
   private final String levelIdName;
   @Nullable
   private String levelName;
   private boolean isDemo;
   private boolean levelHasStartingBonusChest;
   private String resourcePack = "";
   private String resourcePackHash = "";
   private volatile boolean isReady;
   private long lastOverloadWarning;
   @Nullable
   private Component startupState;
   private boolean delayProfilerStart;
   private boolean forceGameType;
   @Nullable
   private final YggdrasilAuthenticationService authenticationService;
   private final MinecraftSessionService sessionService;
   private final GameProfileRepository profileRepository;
   private final GameProfileCache profileCache;
   private long lastServerStatus;
   protected final Thread serverThread = (Thread)Util.make(new Thread(this, "Server thread"), (var0) -> {
      var0.setUncaughtExceptionHandler((var0x, var1) -> {
         LOGGER.error(var1);
      });
   });
   private long nextTickTime = Util.getMillis();
   private long delayedTasksMaxNextTickTime;
   private boolean mayHaveDelayedTasks;
   private boolean hasWorldScreenshot;
   private final ReloadableResourceManager resources;
   private final PackRepository<UnopenedPack> packRepository;
   @Nullable
   private FolderRepositorySource folderPackSource;
   private final Commands commands;
   private final RecipeManager recipes;
   private final TagManager tags;
   private final ServerScoreboard scoreboard;
   private final CustomBossEvents customBossEvents;
   private final LootTables lootTables;
   private final ServerAdvancementManager advancements;
   private final ServerFunctionManager functions;
   private final FrameTimer frameTimer;
   private boolean enforceWhitelist;
   private boolean forceUpgrade;
   private boolean eraseCache;
   private float averageTickTime;
   private final Executor executor;
   @Nullable
   private String serverId;

   public MinecraftServer(File var1, Proxy var2, DataFixer var3, Commands var4, YggdrasilAuthenticationService var5, MinecraftSessionService var6, GameProfileRepository var7, GameProfileCache var8, ChunkProgressListenerFactory var9, String var10) {
      super("Server");
      this.resources = new SimpleReloadableResourceManager(PackType.SERVER_DATA, this.serverThread);
      this.packRepository = new PackRepository(UnopenedPack::new);
      this.recipes = new RecipeManager();
      this.tags = new TagManager();
      this.scoreboard = new ServerScoreboard(this);
      this.customBossEvents = new CustomBossEvents(this);
      this.lootTables = new LootTables();
      this.advancements = new ServerAdvancementManager();
      this.functions = new ServerFunctionManager(this);
      this.frameTimer = new FrameTimer();
      this.proxy = var2;
      this.commands = var4;
      this.authenticationService = var5;
      this.sessionService = var6;
      this.profileRepository = var7;
      this.profileCache = var8;
      this.universe = var1;
      this.connection = new ServerConnectionListener(this);
      this.progressListenerFactory = var9;
      this.storageSource = new LevelStorageSource(var1.toPath(), var1.toPath().resolve("../backups"), var3);
      this.fixerUpper = var3;
      this.resources.registerReloadListener(this.tags);
      this.resources.registerReloadListener(this.recipes);
      this.resources.registerReloadListener(this.lootTables);
      this.resources.registerReloadListener(this.functions);
      this.resources.registerReloadListener(this.advancements);
      this.executor = Util.backgroundExecutor();
      this.levelIdName = var10;
   }

   private void readScoreboard(DimensionDataStorage var1) {
      ScoreboardSaveData var2 = (ScoreboardSaveData)var1.computeIfAbsent(ScoreboardSaveData::new, "scoreboard");
      var2.setScoreboard(this.getScoreboard());
      this.getScoreboard().addDirtyListener(new SaveDataDirtyRunnable(var2));
   }

   protected abstract boolean initServer() throws IOException;

   protected void ensureLevelConversion(String var1) {
      if (this.getStorageSource().requiresConversion(var1)) {
         LOGGER.info("Converting map!");
         this.setServerStartupState(new TranslatableComponent("menu.convertingLevel", new Object[0]));
         this.getStorageSource().convertLevel(var1, new ProgressListener() {
            private long timeStamp = Util.getMillis();

            public void progressStartNoAbort(Component var1) {
            }

            public void progressStart(Component var1) {
            }

            public void progressStagePercentage(int var1) {
               if (Util.getMillis() - this.timeStamp >= 1000L) {
                  this.timeStamp = Util.getMillis();
                  MinecraftServer.LOGGER.info("Converting... {}%", var1);
               }

            }

            public void stop() {
            }

            public void progressStage(Component var1) {
            }
         });
      }

      if (this.forceUpgrade) {
         LOGGER.info("Forcing world upgrade!");
         LevelData var2 = this.getStorageSource().getDataTagFor(this.getLevelIdName());
         if (var2 != null) {
            WorldUpgrader var3 = new WorldUpgrader(this.getLevelIdName(), this.getStorageSource(), var2, this.eraseCache);
            Component var4 = null;

            while(!var3.isFinished()) {
               Component var5 = var3.getStatus();
               if (var4 != var5) {
                  var4 = var5;
                  LOGGER.info(var3.getStatus().getString());
               }

               int var6 = var3.getTotalChunks();
               if (var6 > 0) {
                  int var7 = var3.getConverted() + var3.getSkipped();
                  LOGGER.info("{}% completed ({} / {} chunks)...", Mth.floor((float)var7 / (float)var6 * 100.0F), var7, var6);
               }

               if (this.isStopped()) {
                  var3.cancel();
               } else {
                  try {
                     Thread.sleep(1000L);
                  } catch (InterruptedException var8) {
                  }
               }
            }
         }
      }

   }

   protected synchronized void setServerStartupState(Component var1) {
      this.startupState = var1;
   }

   protected void loadLevel(String var1, String var2, long var3, LevelType var5, JsonElement var6) {
      this.ensureLevelConversion(var1);
      this.setServerStartupState(new TranslatableComponent("menu.loadingLevel", new Object[0]));
      LevelStorage var7 = this.getStorageSource().selectLevel(var1, this);
      this.detectBundledResources(this.getLevelIdName(), var7);
      LevelData var9 = var7.prepareLevel();
      LevelSettings var8;
      if (var9 == null) {
         if (this.isDemo()) {
            var8 = DEMO_SETTINGS;
         } else {
            var8 = new LevelSettings(var3, this.getDefaultGameType(), this.canGenerateStructures(), this.isHardcore(), var5);
            var8.setLevelTypeOptions(var6);
            if (this.levelHasStartingBonusChest) {
               var8.enableStartingBonusItems();
            }
         }

         var9 = new LevelData(var8, var2);
      } else {
         var9.setLevelName(var2);
         var8 = new LevelSettings(var9);
      }

      this.loadDataPacks(var7.getFolder(), var9);
      ChunkProgressListener var10 = this.progressListenerFactory.create(11);
      this.createLevels(var7, var9, var8, var10);
      this.setDifficulty(this.getDefaultDifficulty(), true);
      this.prepareLevels(var10);
   }

   protected void createLevels(LevelStorage var1, LevelData var2, LevelSettings var3, ChunkProgressListener var4) {
      if (this.isDemo()) {
         var2.setLevelSettings(DEMO_SETTINGS);
      }

      ServerLevel var5 = new ServerLevel(this, this.executor, var1, var2, DimensionType.OVERWORLD, this.profiler, var4);
      this.levels.put(DimensionType.OVERWORLD, var5);
      this.readScoreboard(var5.getDataStorage());
      var5.getWorldBorder().readBorderData(var2);
      ServerLevel var6 = this.getLevel(DimensionType.OVERWORLD);
      if (!var2.isInitialized()) {
         try {
            var6.setInitialSpawn(var3);
            if (var2.getGeneratorType() == LevelType.DEBUG_ALL_BLOCK_STATES) {
               this.setupDebugLevel(var2);
            }

            var2.setInitialized(true);
         } catch (Throwable var11) {
            CrashReport var8 = CrashReport.forThrowable(var11, "Exception initializing level");

            try {
               var6.fillReportDetails(var8);
            } catch (Throwable var10) {
            }

            throw new ReportedException(var8);
         }

         var2.setInitialized(true);
      }

      this.getPlayerList().setLevel(var6);
      if (var2.getCustomBossEvents() != null) {
         this.getCustomBossEvents().load(var2.getCustomBossEvents());
      }

      Iterator var7 = DimensionType.getAllTypes().iterator();

      while(var7.hasNext()) {
         DimensionType var12 = (DimensionType)var7.next();
         if (var12 != DimensionType.OVERWORLD) {
            this.levels.put(var12, new DerivedServerLevel(var6, this, this.executor, var1, var12, this.profiler, var4));
         }
      }

   }

   private void setupDebugLevel(LevelData var1) {
      var1.setGenerateMapFeatures(false);
      var1.setAllowCommands(true);
      var1.setRaining(false);
      var1.setThundering(false);
      var1.setClearWeatherTime(1000000000);
      var1.setDayTime(6000L);
      var1.setGameType(GameType.SPECTATOR);
      var1.setHardcore(false);
      var1.setDifficulty(Difficulty.PEACEFUL);
      var1.setDifficultyLocked(true);
      ((GameRules.BooleanValue)var1.getGameRules().getRule(GameRules.RULE_DAYLIGHT)).set(false, this);
   }

   protected void loadDataPacks(File var1, LevelData var2) {
      this.packRepository.addSource(new ServerPacksSource());
      this.folderPackSource = new FolderRepositorySource(new File(var1, "datapacks"));
      this.packRepository.addSource(this.folderPackSource);
      this.packRepository.reload();
      ArrayList var3 = Lists.newArrayList();
      Iterator var4 = var2.getEnabledDataPacks().iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         UnopenedPack var6 = this.packRepository.getPack(var5);
         if (var6 != null) {
            var3.add(var6);
         } else {
            LOGGER.warn("Missing data pack {}", var5);
         }
      }

      this.packRepository.setSelected(var3);
      this.updateSelectedPacks(var2);
   }

   protected void prepareLevels(ChunkProgressListener var1) {
      this.setServerStartupState(new TranslatableComponent("menu.generatingTerrain", new Object[0]));
      ServerLevel var2 = this.getLevel(DimensionType.OVERWORLD);
      LOGGER.info("Preparing start region for dimension " + DimensionType.getName(var2.dimension.getType()));
      BlockPos var3 = var2.getSharedSpawnPos();
      var1.updateSpawnPos(new ChunkPos(var3));
      ServerChunkCache var4 = var2.getChunkSource();
      var4.getLightEngine().setTaskPerBatch(500);
      this.nextTickTime = Util.getMillis();
      var4.addRegionTicket(TicketType.START, new ChunkPos(var3), 11, Unit.INSTANCE);

      while(var4.getTickingGenerated() != 441) {
         this.nextTickTime = Util.getMillis() + 10L;
         this.waitUntilNextTick();
      }

      this.nextTickTime = Util.getMillis() + 10L;
      this.waitUntilNextTick();
      Iterator var5 = DimensionType.getAllTypes().iterator();

      while(true) {
         DimensionType var6;
         ForcedChunksSavedData var7;
         do {
            if (!var5.hasNext()) {
               this.nextTickTime = Util.getMillis() + 10L;
               this.waitUntilNextTick();
               var1.stop();
               var4.getLightEngine().setTaskPerBatch(5);
               return;
            }

            var6 = (DimensionType)var5.next();
            var7 = (ForcedChunksSavedData)this.getLevel(var6).getDataStorage().get(ForcedChunksSavedData::new, "chunks");
         } while(var7 == null);

         ServerLevel var8 = this.getLevel(var6);
         LongIterator var9 = var7.getChunks().iterator();

         while(var9.hasNext()) {
            long var10 = var9.nextLong();
            ChunkPos var12 = new ChunkPos(var10);
            var8.getChunkSource().updateChunkForced(var12, true);
         }
      }
   }

   protected void detectBundledResources(String var1, LevelStorage var2) {
      File var3 = new File(var2.getFolder(), "resources.zip");
      if (var3.isFile()) {
         try {
            this.setResourcePack("level://" + URLEncoder.encode(var1, StandardCharsets.UTF_8.toString()) + "/" + "resources.zip", "");
         } catch (UnsupportedEncodingException var5) {
            LOGGER.warn("Something went wrong url encoding {}", var1);
         }
      }

   }

   public abstract boolean canGenerateStructures();

   public abstract GameType getDefaultGameType();

   public abstract Difficulty getDefaultDifficulty();

   public abstract boolean isHardcore();

   public abstract int getOperatorUserPermissionLevel();

   public abstract int getFunctionCompilationLevel();

   public abstract boolean shouldRconBroadcast();

   public boolean saveAllChunks(boolean var1, boolean var2, boolean var3) {
      boolean var4 = false;

      for(Iterator var5 = this.getAllLevels().iterator(); var5.hasNext(); var4 = true) {
         ServerLevel var6 = (ServerLevel)var5.next();
         if (!var1) {
            LOGGER.info("Saving chunks for level '{}'/{}", var6.getLevelData().getLevelName(), DimensionType.getName(var6.dimension.getType()));
         }

         try {
            var6.save((ProgressListener)null, var2, var6.noSave && !var3);
         } catch (LevelConflictException var8) {
            LOGGER.warn(var8.getMessage());
         }
      }

      ServerLevel var9 = this.getLevel(DimensionType.OVERWORLD);
      LevelData var10 = var9.getLevelData();
      var9.getWorldBorder().saveWorldBorderData(var10);
      var10.setCustomBossEvents(this.getCustomBossEvents().save());
      var9.getLevelStorage().saveLevelData(var10, this.getPlayerList().getSingleplayerData());
      return var4;
   }

   public void close() {
      this.stopServer();
   }

   protected void stopServer() {
      LOGGER.info("Stopping server");
      if (this.getConnection() != null) {
         this.getConnection().stop();
      }

      if (this.playerList != null) {
         LOGGER.info("Saving players");
         this.playerList.saveAll();
         this.playerList.removeAll();
      }

      LOGGER.info("Saving worlds");
      Iterator var1 = this.getAllLevels().iterator();

      ServerLevel var2;
      while(var1.hasNext()) {
         var2 = (ServerLevel)var1.next();
         if (var2 != null) {
            var2.noSave = false;
         }
      }

      this.saveAllChunks(false, true, false);
      var1 = this.getAllLevels().iterator();

      while(var1.hasNext()) {
         var2 = (ServerLevel)var1.next();
         if (var2 != null) {
            try {
               var2.close();
            } catch (IOException var4) {
               LOGGER.error("Exception closing the level", var4);
            }
         }
      }

      if (this.snooper.isStarted()) {
         this.snooper.interrupt();
      }

   }

   public String getLocalIp() {
      return this.localIp;
   }

   public void setLocalIp(String var1) {
      this.localIp = var1;
   }

   public boolean isRunning() {
      return this.running;
   }

   public void halt(boolean var1) {
      this.running = false;
      if (var1) {
         try {
            this.serverThread.join();
         } catch (InterruptedException var3) {
            LOGGER.error("Error while shutting down", var3);
         }
      }

   }

   public void run() {
      try {
         if (this.initServer()) {
            this.nextTickTime = Util.getMillis();
            this.status.setDescription(new TextComponent(this.motd));
            this.status.setVersion(new ServerStatus.Version(SharedConstants.getCurrentVersion().getName(), SharedConstants.getCurrentVersion().getProtocolVersion()));
            this.updateStatusIcon(this.status);

            while(this.running) {
               long var1 = Util.getMillis() - this.nextTickTime;
               if (var1 > 2000L && this.nextTickTime - this.lastOverloadWarning >= 15000L) {
                  long var46 = var1 / 50L;
                  LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", var1, var46);
                  this.nextTickTime += var46 * 50L;
                  this.lastOverloadWarning = this.nextTickTime;
               }

               this.nextTickTime += 50L;
               if (this.delayProfilerStart) {
                  this.delayProfilerStart = false;
                  this.profiler.continuous().enable();
               }

               this.profiler.startTick();
               this.profiler.push("tick");
               this.tickServer(this::haveTime);
               this.profiler.popPush("nextTickWait");
               this.mayHaveDelayedTasks = true;
               this.delayedTasksMaxNextTickTime = Math.max(Util.getMillis() + 50L, this.nextTickTime);
               this.waitUntilNextTick();
               this.profiler.pop();
               this.profiler.endTick();
               this.isReady = true;
            }
         } else {
            this.onServerCrash((CrashReport)null);
         }
      } catch (Throwable var44) {
         LOGGER.error("Encountered an unexpected exception", var44);
         CrashReport var2;
         if (var44 instanceof ReportedException) {
            var2 = this.fillReport(((ReportedException)var44).getReport());
         } else {
            var2 = this.fillReport(new CrashReport("Exception in server tick loop", var44));
         }

         File var3 = new File(new File(this.getServerDirectory(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");
         if (var2.saveToFile(var3)) {
            LOGGER.error("This crash report has been saved to: {}", var3.getAbsolutePath());
         } else {
            LOGGER.error("We were unable to save this crash report to disk.");
         }

         this.onServerCrash(var2);
      } finally {
         try {
            this.stopped = true;
            this.stopServer();
         } catch (Throwable var42) {
            LOGGER.error("Exception stopping the server", var42);
         } finally {
            this.onServerExit();
         }

      }

   }

   private boolean haveTime() {
      return this.runningTask() || Util.getMillis() < (this.mayHaveDelayedTasks ? this.delayedTasksMaxNextTickTime : this.nextTickTime);
   }

   protected void waitUntilNextTick() {
      this.runAllTasks();
      this.managedBlock(() -> {
         return !this.haveTime();
      });
   }

   protected TickTask wrapRunnable(Runnable var1) {
      return new TickTask(this.tickCount, var1);
   }

   protected boolean shouldRun(TickTask var1) {
      return var1.getTick() + 3 < this.tickCount || this.haveTime();
   }

   public boolean pollTask() {
      boolean var1 = this.pollTaskInternal();
      this.mayHaveDelayedTasks = var1;
      return var1;
   }

   private boolean pollTaskInternal() {
      if (super.pollTask()) {
         return true;
      } else {
         if (this.haveTime()) {
            Iterator var1 = this.getAllLevels().iterator();

            while(var1.hasNext()) {
               ServerLevel var2 = (ServerLevel)var1.next();
               if (var2.getChunkSource().pollTask()) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public void updateStatusIcon(ServerStatus var1) {
      File var2 = this.getFile("server-icon.png");
      if (!var2.exists()) {
         var2 = this.getStorageSource().getFile(this.getLevelIdName(), "icon.png");
      }

      if (var2.isFile()) {
         ByteBuf var3 = Unpooled.buffer();

         try {
            BufferedImage var4 = ImageIO.read(var2);
            Validate.validState(var4.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
            Validate.validState(var4.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
            ImageIO.write(var4, "PNG", new ByteBufOutputStream(var3));
            ByteBuffer var5 = Base64.getEncoder().encode(var3.nioBuffer());
            var1.setFavicon("data:image/png;base64," + StandardCharsets.UTF_8.decode(var5));
         } catch (Exception var9) {
            LOGGER.error("Couldn't load server icon", var9);
         } finally {
            var3.release();
         }
      }

   }

   public boolean hasWorldScreenshot() {
      this.hasWorldScreenshot = this.hasWorldScreenshot || this.getWorldScreenshotFile().isFile();
      return this.hasWorldScreenshot;
   }

   public File getWorldScreenshotFile() {
      return this.getStorageSource().getFile(this.getLevelIdName(), "icon.png");
   }

   public File getServerDirectory() {
      return new File(".");
   }

   protected void onServerCrash(CrashReport var1) {
   }

   protected void onServerExit() {
   }

   protected void tickServer(BooleanSupplier var1) {
      long var2 = Util.getNanos();
      ++this.tickCount;
      this.tickChildren(var1);
      if (var2 - this.lastServerStatus >= 5000000000L) {
         this.lastServerStatus = var2;
         this.status.setPlayers(new ServerStatus.Players(this.getMaxPlayers(), this.getPlayerCount()));
         GameProfile[] var4 = new GameProfile[Math.min(this.getPlayerCount(), 12)];
         int var5 = Mth.nextInt(this.random, 0, this.getPlayerCount() - var4.length);

         for(int var6 = 0; var6 < var4.length; ++var6) {
            var4[var6] = ((ServerPlayer)this.playerList.getPlayers().get(var5 + var6)).getGameProfile();
         }

         Collections.shuffle(Arrays.asList(var4));
         this.status.getPlayers().setSample(var4);
      }

      if (this.tickCount % 6000 == 0) {
         LOGGER.debug("Autosave started");
         this.profiler.push("save");
         this.playerList.saveAll();
         this.saveAllChunks(true, false, false);
         this.profiler.pop();
         LOGGER.debug("Autosave finished");
      }

      this.profiler.push("snooper");
      if (!this.snooper.isStarted() && this.tickCount > 100) {
         this.snooper.start();
      }

      if (this.tickCount % 6000 == 0) {
         this.snooper.prepare();
      }

      this.profiler.pop();
      this.profiler.push("tallying");
      long var8 = this.tickTimes[this.tickCount % 100] = Util.getNanos() - var2;
      this.averageTickTime = this.averageTickTime * 0.8F + (float)var8 / 1000000.0F * 0.19999999F;
      long var9 = Util.getNanos();
      this.frameTimer.logFrameDuration(var9 - var2);
      this.profiler.pop();
   }

   protected void tickChildren(BooleanSupplier var1) {
      this.profiler.push("commandFunctions");
      this.getFunctions().tick();
      this.profiler.popPush("levels");
      Iterator var2 = this.getAllLevels().iterator();

      while(true) {
         ServerLevel var3;
         do {
            if (!var2.hasNext()) {
               this.profiler.popPush("connection");
               this.getConnection().tick();
               this.profiler.popPush("players");
               this.playerList.tick();
               this.profiler.popPush("server gui refresh");

               for(int var7 = 0; var7 < this.tickables.size(); ++var7) {
                  ((Runnable)this.tickables.get(var7)).run();
               }

               this.profiler.pop();
               return;
            }

            var3 = (ServerLevel)var2.next();
         } while(var3.dimension.getType() != DimensionType.OVERWORLD && !this.isNetherEnabled());

         this.profiler.push(() -> {
            return var3.getLevelData().getLevelName() + " " + Registry.DIMENSION_TYPE.getKey(var3.dimension.getType());
         });
         if (this.tickCount % 20 == 0) {
            this.profiler.push("timeSync");
            this.playerList.broadcastAll(new ClientboundSetTimePacket(var3.getGameTime(), var3.getDayTime(), var3.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)), var3.dimension.getType());
            this.profiler.pop();
         }

         this.profiler.push("tick");

         try {
            var3.tick(var1);
         } catch (Throwable var6) {
            CrashReport var5 = CrashReport.forThrowable(var6, "Exception ticking world");
            var3.fillReportDetails(var5);
            throw new ReportedException(var5);
         }

         this.profiler.pop();
         this.profiler.pop();
      }
   }

   public boolean isNetherEnabled() {
      return true;
   }

   public void addTickable(Runnable var1) {
      this.tickables.add(var1);
   }

   public static void main(String[] var0) {
      OptionParser var1 = new OptionParser();
      OptionSpecBuilder var2 = var1.accepts("nogui");
      OptionSpecBuilder var3 = var1.accepts("initSettings", "Initializes 'server.properties' and 'eula.txt', then quits");
      OptionSpecBuilder var4 = var1.accepts("demo");
      OptionSpecBuilder var5 = var1.accepts("bonusChest");
      OptionSpecBuilder var6 = var1.accepts("forceUpgrade");
      OptionSpecBuilder var7 = var1.accepts("eraseCache");
      AbstractOptionSpec var8 = var1.accepts("help").forHelp();
      ArgumentAcceptingOptionSpec var9 = var1.accepts("singleplayer").withRequiredArg();
      ArgumentAcceptingOptionSpec var10 = var1.accepts("universe").withRequiredArg().defaultsTo(".", new String[0]);
      ArgumentAcceptingOptionSpec var11 = var1.accepts("world").withRequiredArg();
      ArgumentAcceptingOptionSpec var12 = var1.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(-1, new Integer[0]);
      ArgumentAcceptingOptionSpec var13 = var1.accepts("serverId").withRequiredArg();
      NonOptionArgumentSpec var14 = var1.nonOptions();

      try {
         OptionSet var15 = var1.parse(var0);
         if (var15.has(var8)) {
            var1.printHelpOn(System.err);
            return;
         }

         Path var16 = Paths.get("server.properties");
         DedicatedServerSettings var17 = new DedicatedServerSettings(var16);
         var17.forceSave();
         Path var18 = Paths.get("eula.txt");
         Eula var19 = new Eula(var18);
         if (var15.has(var3)) {
            LOGGER.info("Initialized '" + var16.toAbsolutePath().toString() + "' and '" + var18.toAbsolutePath().toString() + "'");
            return;
         }

         if (!var19.hasAgreedToEULA()) {
            LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
            return;
         }

         Bootstrap.bootStrap();
         Bootstrap.validate();
         String var20 = (String)var15.valueOf(var10);
         YggdrasilAuthenticationService var21 = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
         MinecraftSessionService var22 = var21.createMinecraftSessionService();
         GameProfileRepository var23 = var21.createProfileRepository();
         GameProfileCache var24 = new GameProfileCache(var23, new File(var20, USERID_CACHE_FILE.getName()));
         String var25 = (String)Optional.ofNullable(var15.valueOf(var11)).orElse(var17.getProperties().levelName);
         final DedicatedServer var26 = new DedicatedServer(new File(var20), var17, DataFixers.getDataFixer(), var21, var22, var23, var24, LoggerChunkProgressListener::new, var25);
         var26.setSingleplayerName((String)var15.valueOf(var9));
         var26.setPort((Integer)var15.valueOf(var12));
         var26.setDemo(var15.has(var4));
         var26.setBonusChest(var15.has(var5));
         var26.forceUpgrade(var15.has(var6));
         var26.eraseCache(var15.has(var7));
         var26.setId((String)var15.valueOf(var13));
         boolean var27 = !var15.has(var2) && !var15.valuesOf(var14).contains("nogui");
         if (var27 && !GraphicsEnvironment.isHeadless()) {
            var26.showGui();
         }

         var26.forkAndRun();
         Thread var28 = new Thread("Server Shutdown Thread") {
            public void run() {
               var26.halt(true);
            }
         };
         var28.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
         Runtime.getRuntime().addShutdownHook(var28);
      } catch (Exception var29) {
         LOGGER.fatal("Failed to start the minecraft server", var29);
      }

   }

   protected void setId(String var1) {
      this.serverId = var1;
   }

   protected void forceUpgrade(boolean var1) {
      this.forceUpgrade = var1;
   }

   protected void eraseCache(boolean var1) {
      this.eraseCache = var1;
   }

   public void forkAndRun() {
      this.serverThread.start();
   }

   public boolean isShutdown() {
      return !this.serverThread.isAlive();
   }

   public File getFile(String var1) {
      return new File(this.getServerDirectory(), var1);
   }

   public void info(String var1) {
      LOGGER.info(var1);
   }

   public void warn(String var1) {
      LOGGER.warn(var1);
   }

   public ServerLevel getLevel(DimensionType var1) {
      return (ServerLevel)this.levels.get(var1);
   }

   public Iterable<ServerLevel> getAllLevels() {
      return this.levels.values();
   }

   public String getServerVersion() {
      return SharedConstants.getCurrentVersion().getName();
   }

   public int getPlayerCount() {
      return this.playerList.getPlayerCount();
   }

   public int getMaxPlayers() {
      return this.playerList.getMaxPlayers();
   }

   public String[] getPlayerNames() {
      return this.playerList.getPlayerNamesArray();
   }

   public boolean isDebugging() {
      return false;
   }

   public void error(String var1) {
      LOGGER.error(var1);
   }

   public void debug(String var1) {
      if (this.isDebugging()) {
         LOGGER.info(var1);
      }

   }

   public String getServerModName() {
      return "vanilla";
   }

   public CrashReport fillReport(CrashReport var1) {
      if (this.playerList != null) {
         var1.getSystemDetails().setDetail("Player Count", () -> {
            return this.playerList.getPlayerCount() + " / " + this.playerList.getMaxPlayers() + "; " + this.playerList.getPlayers();
         });
      }

      var1.getSystemDetails().setDetail("Data Packs", () -> {
         StringBuilder var1 = new StringBuilder();
         Iterator var2 = this.packRepository.getSelected().iterator();

         while(var2.hasNext()) {
            UnopenedPack var3 = (UnopenedPack)var2.next();
            if (var1.length() > 0) {
               var1.append(", ");
            }

            var1.append(var3.getId());
            if (!var3.getCompatibility().isCompatible()) {
               var1.append(" (incompatible)");
            }
         }

         return var1.toString();
      });
      if (this.serverId != null) {
         var1.getSystemDetails().setDetail("Server Id", () -> {
            return this.serverId;
         });
      }

      return var1;
   }

   public boolean isInitialized() {
      return this.universe != null;
   }

   public void sendMessage(Component var1) {
      LOGGER.info(var1.getString());
   }

   public KeyPair getKeyPair() {
      return this.keyPair;
   }

   public int getPort() {
      return this.port;
   }

   public void setPort(int var1) {
      this.port = var1;
   }

   public String getSingleplayerName() {
      return this.singleplayerName;
   }

   public void setSingleplayerName(String var1) {
      this.singleplayerName = var1;
   }

   public boolean isSingleplayer() {
      return this.singleplayerName != null;
   }

   public String getLevelIdName() {
      return this.levelIdName;
   }

   public void setLevelName(String var1) {
      this.levelName = var1;
   }

   public String getLevelName() {
      return this.levelName;
   }

   public void setKeyPair(KeyPair var1) {
      this.keyPair = var1;
   }

   public void setDifficulty(Difficulty var1, boolean var2) {
      Iterator var3 = this.getAllLevels().iterator();

      while(true) {
         ServerLevel var4;
         LevelData var5;
         do {
            if (!var3.hasNext()) {
               this.getPlayerList().getPlayers().forEach(this::sendDifficultyUpdate);
               return;
            }

            var4 = (ServerLevel)var3.next();
            var5 = var4.getLevelData();
         } while(!var2 && var5.isDifficultyLocked());

         if (var5.isHardcore()) {
            var5.setDifficulty(Difficulty.HARD);
            var4.setSpawnSettings(true, true);
         } else if (this.isSingleplayer()) {
            var5.setDifficulty(var1);
            var4.setSpawnSettings(var4.getDifficulty() != Difficulty.PEACEFUL, true);
         } else {
            var5.setDifficulty(var1);
            var4.setSpawnSettings(this.getSpawnMonsters(), this.animals);
         }
      }
   }

   public void setDifficultyLocked(boolean var1) {
      Iterator var2 = this.getAllLevels().iterator();

      while(var2.hasNext()) {
         ServerLevel var3 = (ServerLevel)var2.next();
         LevelData var4 = var3.getLevelData();
         var4.setDifficultyLocked(var1);
      }

      this.getPlayerList().getPlayers().forEach(this::sendDifficultyUpdate);
   }

   private void sendDifficultyUpdate(ServerPlayer var1) {
      LevelData var2 = var1.getLevel().getLevelData();
      var1.connection.send(new ClientboundChangeDifficultyPacket(var2.getDifficulty(), var2.isDifficultyLocked()));
   }

   protected boolean getSpawnMonsters() {
      return true;
   }

   public boolean isDemo() {
      return this.isDemo;
   }

   public void setDemo(boolean var1) {
      this.isDemo = var1;
   }

   public void setBonusChest(boolean var1) {
      this.levelHasStartingBonusChest = var1;
   }

   public LevelStorageSource getStorageSource() {
      return this.storageSource;
   }

   public String getResourcePack() {
      return this.resourcePack;
   }

   public String getResourcePackHash() {
      return this.resourcePackHash;
   }

   public void setResourcePack(String var1, String var2) {
      this.resourcePack = var1;
      this.resourcePackHash = var2;
   }

   public void populateSnooper(Snooper var1) {
      var1.setDynamicData("whitelist_enabled", false);
      var1.setDynamicData("whitelist_count", 0);
      if (this.playerList != null) {
         var1.setDynamicData("players_current", this.getPlayerCount());
         var1.setDynamicData("players_max", this.getMaxPlayers());
         var1.setDynamicData("players_seen", this.getLevel(DimensionType.OVERWORLD).getLevelStorage().getSeenPlayers().length);
      }

      var1.setDynamicData("uses_auth", this.onlineMode);
      var1.setDynamicData("gui_state", this.hasGui() ? "enabled" : "disabled");
      var1.setDynamicData("run_time", (Util.getMillis() - var1.getStartupTime()) / 60L * 1000L);
      var1.setDynamicData("avg_tick_ms", (int)(Mth.average(this.tickTimes) * 1.0E-6D));
      int var2 = 0;
      Iterator var3 = this.getAllLevels().iterator();

      while(var3.hasNext()) {
         ServerLevel var4 = (ServerLevel)var3.next();
         if (var4 != null) {
            LevelData var5 = var4.getLevelData();
            var1.setDynamicData("world[" + var2 + "][dimension]", var4.dimension.getType());
            var1.setDynamicData("world[" + var2 + "][mode]", var5.getGameType());
            var1.setDynamicData("world[" + var2 + "][difficulty]", var4.getDifficulty());
            var1.setDynamicData("world[" + var2 + "][hardcore]", var5.isHardcore());
            var1.setDynamicData("world[" + var2 + "][generator_name]", var5.getGeneratorType().getName());
            var1.setDynamicData("world[" + var2 + "][generator_version]", var5.getGeneratorType().getVersion());
            var1.setDynamicData("world[" + var2 + "][height]", this.maxBuildHeight);
            var1.setDynamicData("world[" + var2 + "][chunks_loaded]", var4.getChunkSource().getLoadedChunksCount());
            ++var2;
         }
      }

      var1.setDynamicData("worlds", var2);
   }

   public abstract boolean isDedicatedServer();

   public boolean usesAuthentication() {
      return this.onlineMode;
   }

   public void setUsesAuthentication(boolean var1) {
      this.onlineMode = var1;
   }

   public boolean getPreventProxyConnections() {
      return this.preventProxyConnections;
   }

   public void setPreventProxyConnections(boolean var1) {
      this.preventProxyConnections = var1;
   }

   public boolean isAnimals() {
      return this.animals;
   }

   public void setAnimals(boolean var1) {
      this.animals = var1;
   }

   public boolean isNpcsEnabled() {
      return this.npcs;
   }

   public abstract boolean isEpollEnabled();

   public void setNpcsEnabled(boolean var1) {
      this.npcs = var1;
   }

   public boolean isPvpAllowed() {
      return this.pvp;
   }

   public void setPvpAllowed(boolean var1) {
      this.pvp = var1;
   }

   public boolean isFlightAllowed() {
      return this.allowFlight;
   }

   public void setFlightAllowed(boolean var1) {
      this.allowFlight = var1;
   }

   public abstract boolean isCommandBlockEnabled();

   public String getMotd() {
      return this.motd;
   }

   public void setMotd(String var1) {
      this.motd = var1;
   }

   public int getMaxBuildHeight() {
      return this.maxBuildHeight;
   }

   public void setMaxBuildHeight(int var1) {
      this.maxBuildHeight = var1;
   }

   public boolean isStopped() {
      return this.stopped;
   }

   public PlayerList getPlayerList() {
      return this.playerList;
   }

   public void setPlayerList(PlayerList var1) {
      this.playerList = var1;
   }

   public abstract boolean isPublished();

   public void setDefaultGameMode(GameType var1) {
      Iterator var2 = this.getAllLevels().iterator();

      while(var2.hasNext()) {
         ServerLevel var3 = (ServerLevel)var2.next();
         var3.getLevelData().setGameType(var1);
      }

   }

   @Nullable
   public ServerConnectionListener getConnection() {
      return this.connection;
   }

   public boolean isReady() {
      return this.isReady;
   }

   public boolean hasGui() {
      return false;
   }

   public abstract boolean publishServer(GameType var1, boolean var2, int var3);

   public int getTickCount() {
      return this.tickCount;
   }

   public void delayStartProfiler() {
      this.delayProfilerStart = true;
   }

   public Snooper getSnooper() {
      return this.snooper;
   }

   public int getSpawnProtectionRadius() {
      return 16;
   }

   public boolean isUnderSpawnProtection(Level var1, BlockPos var2, Player var3) {
      return false;
   }

   public void setForceGameType(boolean var1) {
      this.forceGameType = var1;
   }

   public boolean getForceGameType() {
      return this.forceGameType;
   }

   public int getPlayerIdleTimeout() {
      return this.playerIdleTimeout;
   }

   public void setPlayerIdleTimeout(int var1) {
      this.playerIdleTimeout = var1;
   }

   public MinecraftSessionService getSessionService() {
      return this.sessionService;
   }

   public GameProfileRepository getProfileRepository() {
      return this.profileRepository;
   }

   public GameProfileCache getProfileCache() {
      return this.profileCache;
   }

   public ServerStatus getStatus() {
      return this.status;
   }

   public void invalidateStatus() {
      this.lastServerStatus = 0L;
   }

   public int getAbsoluteMaxWorldSize() {
      return 29999984;
   }

   public boolean scheduleExecutables() {
      return super.scheduleExecutables() && !this.isStopped();
   }

   public Thread getRunningThread() {
      return this.serverThread;
   }

   public int getCompressionThreshold() {
      return 256;
   }

   public long getNextTickTime() {
      return this.nextTickTime;
   }

   public DataFixer getFixerUpper() {
      return this.fixerUpper;
   }

   public int getSpawnRadius(@Nullable ServerLevel var1) {
      return var1 != null ? var1.getGameRules().getInt(GameRules.RULE_SPAWN_RADIUS) : 10;
   }

   public ServerAdvancementManager getAdvancements() {
      return this.advancements;
   }

   public ServerFunctionManager getFunctions() {
      return this.functions;
   }

   public void reloadResources() {
      if (!this.isSameThread()) {
         this.execute(this::reloadResources);
      } else {
         this.getPlayerList().saveAll();
         this.packRepository.reload();
         this.updateSelectedPacks(this.getLevel(DimensionType.OVERWORLD).getLevelData());
         this.getPlayerList().reloadResources();
      }
   }

   private void updateSelectedPacks(LevelData var1) {
      ArrayList var2 = Lists.newArrayList(this.packRepository.getSelected());
      Iterator var3 = this.packRepository.getAvailable().iterator();

      while(var3.hasNext()) {
         UnopenedPack var4 = (UnopenedPack)var3.next();
         if (!var1.getDisabledDataPacks().contains(var4.getId()) && !var2.contains(var4)) {
            LOGGER.info("Found new data pack {}, loading it automatically", var4.getId());
            var4.getDefaultPosition().insert(var2, var4, (var0) -> {
               return var0;
            }, false);
         }
      }

      this.packRepository.setSelected(var2);
      ArrayList var7 = Lists.newArrayList();
      this.packRepository.getSelected().forEach((var1x) -> {
         var7.add(var1x.open());
      });
      CompletableFuture var8 = this.resources.reload(this.executor, this, var7, DATA_RELOAD_INITIAL_TASK);
      this.managedBlock(var8::isDone);

      try {
         var8.get();
      } catch (Exception var6) {
         LOGGER.error("Failed to reload data packs", var6);
      }

      var1.getEnabledDataPacks().clear();
      var1.getDisabledDataPacks().clear();
      this.packRepository.getSelected().forEach((var1x) -> {
         var1.getEnabledDataPacks().add(var1x.getId());
      });
      this.packRepository.getAvailable().forEach((var2x) -> {
         if (!this.packRepository.getSelected().contains(var2x)) {
            var1.getDisabledDataPacks().add(var2x.getId());
         }

      });
   }

   public void kickUnlistedPlayers(CommandSourceStack var1) {
      if (this.isEnforceWhitelist()) {
         PlayerList var2 = var1.getServer().getPlayerList();
         UserWhiteList var3 = var2.getWhiteList();
         if (var3.isEnabled()) {
            ArrayList var4 = Lists.newArrayList(var2.getPlayers());
            Iterator var5 = var4.iterator();

            while(var5.hasNext()) {
               ServerPlayer var6 = (ServerPlayer)var5.next();
               if (!var3.isWhiteListed(var6.getGameProfile())) {
                  var6.connection.disconnect(new TranslatableComponent("multiplayer.disconnect.not_whitelisted", new Object[0]));
               }
            }

         }
      }
   }

   public ReloadableResourceManager getResources() {
      return this.resources;
   }

   public PackRepository<UnopenedPack> getPackRepository() {
      return this.packRepository;
   }

   public Commands getCommands() {
      return this.commands;
   }

   public CommandSourceStack createCommandSourceStack() {
      return new CommandSourceStack(this, this.getLevel(DimensionType.OVERWORLD) == null ? Vec3.ZERO : new Vec3(this.getLevel(DimensionType.OVERWORLD).getSharedSpawnPos()), Vec2.ZERO, this.getLevel(DimensionType.OVERWORLD), 4, "Server", new TextComponent("Server"), this, (Entity)null);
   }

   public boolean acceptsSuccess() {
      return true;
   }

   public boolean acceptsFailure() {
      return true;
   }

   public RecipeManager getRecipeManager() {
      return this.recipes;
   }

   public TagManager getTags() {
      return this.tags;
   }

   public ServerScoreboard getScoreboard() {
      return this.scoreboard;
   }

   public LootTables getLootTables() {
      return this.lootTables;
   }

   public GameRules getGameRules() {
      return this.getLevel(DimensionType.OVERWORLD).getGameRules();
   }

   public CustomBossEvents getCustomBossEvents() {
      return this.customBossEvents;
   }

   public boolean isEnforceWhitelist() {
      return this.enforceWhitelist;
   }

   public void setEnforceWhitelist(boolean var1) {
      this.enforceWhitelist = var1;
   }

   public float getAverageTickTime() {
      return this.averageTickTime;
   }

   public int getProfilePermissions(GameProfile var1) {
      if (this.getPlayerList().isOp(var1)) {
         ServerOpListEntry var2 = (ServerOpListEntry)this.getPlayerList().getOps().get(var1);
         if (var2 != null) {
            return var2.getLevel();
         } else if (this.isSingleplayerOwner(var1)) {
            return 4;
         } else if (this.isSingleplayer()) {
            return this.getPlayerList().isAllowCheatsForAllPlayers() ? 4 : 0;
         } else {
            return this.getOperatorUserPermissionLevel();
         }
      } else {
         return 0;
      }
   }

   public FrameTimer getFrameTimer() {
      return this.frameTimer;
   }

   public GameProfiler getProfiler() {
      return this.profiler;
   }

   public Executor getBackgroundTaskExecutor() {
      return this.executor;
   }

   public abstract boolean isSingleplayerOwner(GameProfile var1);

   public void saveDebugReport(Path var1) throws IOException {
      Path var2 = var1.resolve("levels");
      Iterator var3 = this.levels.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         ResourceLocation var5 = DimensionType.getName((DimensionType)var4.getKey());
         Path var6 = var2.resolve(var5.getNamespace()).resolve(var5.getPath());
         Files.createDirectories(var6);
         ((ServerLevel)var4.getValue()).saveDebugReport(var6);
      }

      this.dumpGameRules(var1.resolve("gamerules.txt"));
      this.dumpClasspath(var1.resolve("classpath.txt"));
      this.dumpCrashCategory(var1.resolve("example_crash.txt"));
      this.dumpMiscStats(var1.resolve("stats.txt"));
      this.dumpThreads(var1.resolve("threads.txt"));
   }

   private void dumpMiscStats(Path var1) throws IOException {
      BufferedWriter var2 = Files.newBufferedWriter(var1);
      Throwable var3 = null;

      try {
         var2.write(String.format("pending_tasks: %d\n", this.getPendingTasksCount()));
         var2.write(String.format("average_tick_time: %f\n", this.getAverageTickTime()));
         var2.write(String.format("tick_times: %s\n", Arrays.toString(this.tickTimes)));
         var2.write(String.format("queue: %s\n", Util.backgroundExecutor()));
      } catch (Throwable var12) {
         var3 = var12;
         throw var12;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var11) {
                  var3.addSuppressed(var11);
               }
            } else {
               var2.close();
            }
         }

      }

   }

   private void dumpCrashCategory(Path var1) throws IOException {
      CrashReport var2 = new CrashReport("Server dump", new Exception("dummy"));
      this.fillReport(var2);
      BufferedWriter var3 = Files.newBufferedWriter(var1);
      Throwable var4 = null;

      try {
         var3.write(var2.getFriendlyReport());
      } catch (Throwable var13) {
         var4 = var13;
         throw var13;
      } finally {
         if (var3 != null) {
            if (var4 != null) {
               try {
                  var3.close();
               } catch (Throwable var12) {
                  var4.addSuppressed(var12);
               }
            } else {
               var3.close();
            }
         }

      }

   }

   private void dumpGameRules(Path var1) throws IOException {
      BufferedWriter var2 = Files.newBufferedWriter(var1);
      Throwable var3 = null;

      try {
         final ArrayList var4 = Lists.newArrayList();
         final GameRules var5 = this.getGameRules();
         GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
            public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> var1, GameRules.Type<T> var2) {
               var4.add(String.format("%s=%s\n", var1.getId(), var5.getRule(var1).toString()));
            }
         });
         Iterator var6 = var4.iterator();

         while(var6.hasNext()) {
            String var7 = (String)var6.next();
            var2.write(var7);
         }
      } catch (Throwable var15) {
         var3 = var15;
         throw var15;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var14) {
                  var3.addSuppressed(var14);
               }
            } else {
               var2.close();
            }
         }

      }

   }

   private void dumpClasspath(Path var1) throws IOException {
      BufferedWriter var2 = Files.newBufferedWriter(var1);
      Throwable var3 = null;

      try {
         String var4 = System.getProperty("java.class.path");
         String var5 = System.getProperty("path.separator");
         Iterator var6 = Splitter.on(var5).split(var4).iterator();

         while(var6.hasNext()) {
            String var7 = (String)var6.next();
            var2.write(var7);
            var2.write("\n");
         }
      } catch (Throwable var15) {
         var3 = var15;
         throw var15;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var14) {
                  var3.addSuppressed(var14);
               }
            } else {
               var2.close();
            }
         }

      }

   }

   private void dumpThreads(Path var1) throws IOException {
      ThreadMXBean var2 = ManagementFactory.getThreadMXBean();
      ThreadInfo[] var3 = var2.dumpAllThreads(true, true);
      Arrays.sort(var3, Comparator.comparing(ThreadInfo::getThreadName));
      BufferedWriter var4 = Files.newBufferedWriter(var1);
      Throwable var5 = null;

      try {
         ThreadInfo[] var6 = var3;
         int var7 = var3.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            ThreadInfo var9 = var6[var8];
            var4.write(var9.toString());
            var4.write(10);
         }
      } catch (Throwable var17) {
         var5 = var17;
         throw var17;
      } finally {
         if (var4 != null) {
            if (var5 != null) {
               try {
                  var4.close();
               } catch (Throwable var16) {
                  var5.addSuppressed(var16);
               }
            } else {
               var4.close();
            }
         }

      }

   }

   // $FF: synthetic method
   public boolean shouldRun(Runnable var1) {
      return this.shouldRun((TickTask)var1);
   }

   // $FF: synthetic method
   public Runnable wrapRunnable(Runnable var1) {
      return this.wrapRunnable(var1);
   }

   static {
      DATA_RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);
      DEMO_SETTINGS = (new LevelSettings((long)"North Carolina".hashCode(), GameType.SURVIVAL, true, false, LevelType.NORMAL)).enableStartingBonusItems();
   }
}
