package net.minecraft.server;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.Proxy;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.Util;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.gametest.framework.GameTestTicker;
import net.minecraft.network.chat.ChatDecorator;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.server.level.DemoMode;
import net.minecraft.server.level.PlayerRespawnLogic;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.server.network.TextFilter;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.util.ModCheck;
import net.minecraft.util.Mth;
import net.minecraft.util.NativeModuleLister;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SignatureValidator;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.debugchart.RemoteDebugSampleType;
import net.minecraft.util.debugchart.SampleLogger;
import net.minecraft.util.debugchart.TpsDebugDimensions;
import net.minecraft.util.profiling.EmptyProfileResults;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.ResultField;
import net.minecraft.util.profiling.SingleTickProfiler;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.util.profiling.metrics.profiling.ActiveMetricsRecorder;
import net.minecraft.util.profiling.metrics.profiling.InactiveMetricsRecorder;
import net.minecraft.util.profiling.metrics.profiling.MetricsRecorder;
import net.minecraft.util.profiling.metrics.profiling.ServerMetricsSamplersProvider;
import net.minecraft.util.profiling.metrics.storage.MetricsPersister;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.Difficulty;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.VillageSiege;
import net.minecraft.world.entity.npc.CatSpawner;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.ForcedChunksSavedData;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.CommandStorage;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public abstract class MinecraftServer extends ReentrantBlockableEventLoop<TickTask> implements ServerInfo, CommandSource, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String VANILLA_BRAND = "vanilla";
   private static final float AVERAGE_TICK_TIME_SMOOTHING = 0.8F;
   private static final int TICK_STATS_SPAN = 100;
   private static final long OVERLOADED_THRESHOLD_NANOS;
   private static final int OVERLOADED_TICKS_THRESHOLD = 20;
   private static final long OVERLOADED_WARNING_INTERVAL_NANOS;
   private static final int OVERLOADED_TICKS_WARNING_INTERVAL = 100;
   private static final long STATUS_EXPIRE_TIME_NANOS;
   private static final long PREPARE_LEVELS_DEFAULT_DELAY_NANOS;
   private static final int MAX_STATUS_PLAYER_SAMPLE = 12;
   private static final int SPAWN_POSITION_SEARCH_RADIUS = 5;
   private static final int AUTOSAVE_INTERVAL = 6000;
   private static final int MIMINUM_AUTOSAVE_TICKS = 100;
   private static final int MAX_TICK_LATENCY = 3;
   public static final int ABSOLUTE_MAX_WORLD_SIZE = 29999984;
   public static final LevelSettings DEMO_SETTINGS;
   public static final GameProfile ANONYMOUS_PLAYER_PROFILE;
   protected final LevelStorageSource.LevelStorageAccess storageSource;
   protected final PlayerDataStorage playerDataStorage;
   private final List<Runnable> tickables = Lists.newArrayList();
   private MetricsRecorder metricsRecorder;
   private ProfilerFiller profiler;
   private Consumer<ProfileResults> onMetricsRecordingStopped;
   private Consumer<Path> onMetricsRecordingFinished;
   private boolean willStartRecordingMetrics;
   @Nullable
   private TimeProfiler debugCommandProfiler;
   private boolean debugCommandProfilerDelayStart;
   private final ServerConnectionListener connection;
   private final ChunkProgressListenerFactory progressListenerFactory;
   @Nullable
   private ServerStatus status;
   @Nullable
   private ServerStatus.Favicon statusIcon;
   private final RandomSource random;
   private final DataFixer fixerUpper;
   private String localIp;
   private int port;
   private final LayeredRegistryAccess<RegistryLayer> registries;
   private final Map<ResourceKey<Level>, ServerLevel> levels;
   private PlayerList playerList;
   private volatile boolean running;
   private boolean stopped;
   private int tickCount;
   private int ticksUntilAutosave;
   protected final Proxy proxy;
   private boolean onlineMode;
   private boolean preventProxyConnections;
   private boolean pvp;
   private boolean allowFlight;
   @Nullable
   private String motd;
   private int playerIdleTimeout;
   private final long[] tickTimesNanos;
   private long aggregatedTickTimesNanos;
   @Nullable
   private KeyPair keyPair;
   @Nullable
   private GameProfile singleplayerProfile;
   private boolean isDemo;
   private volatile boolean isReady;
   private long lastOverloadWarningNanos;
   protected final Services services;
   private long lastServerStatus;
   private final Thread serverThread;
   private long lastTickNanos;
   private long taskExecutionStartNanos;
   private long idleTimeNanos;
   private long nextTickTimeNanos;
   private long delayedTasksMaxNextTickTimeNanos;
   private boolean mayHaveDelayedTasks;
   private final PackRepository packRepository;
   private final ServerScoreboard scoreboard;
   @Nullable
   private CommandStorage commandStorage;
   private final CustomBossEvents customBossEvents;
   private final ServerFunctionManager functionManager;
   private boolean enforceWhitelist;
   private float smoothedTickTimeMillis;
   private final Executor executor;
   @Nullable
   private String serverId;
   private ReloadableResources resources;
   private final StructureTemplateManager structureTemplateManager;
   private final ServerTickRateManager tickRateManager;
   protected final WorldData worldData;
   private volatile boolean isSaving;

   public static <S extends MinecraftServer> S spin(Function<Thread, S> var0) {
      AtomicReference var1 = new AtomicReference();
      Thread var2 = new Thread(() -> {
         ((MinecraftServer)var1.get()).runServer();
      }, "Server thread");
      var2.setUncaughtExceptionHandler((var0x, var1x) -> {
         LOGGER.error("Uncaught exception in server thread", var1x);
      });
      if (Runtime.getRuntime().availableProcessors() > 4) {
         var2.setPriority(8);
      }

      MinecraftServer var3 = (MinecraftServer)var0.apply(var2);
      var1.set(var3);
      var2.start();
      return var3;
   }

   public MinecraftServer(Thread var1, LevelStorageSource.LevelStorageAccess var2, PackRepository var3, WorldStem var4, Proxy var5, DataFixer var6, Services var7, ChunkProgressListenerFactory var8) {
      super("Server");
      this.metricsRecorder = InactiveMetricsRecorder.INSTANCE;
      this.profiler = this.metricsRecorder.getProfiler();
      this.onMetricsRecordingStopped = (var1x) -> {
         this.stopRecordingMetrics();
      };
      this.onMetricsRecordingFinished = (var0) -> {
      };
      this.random = RandomSource.create();
      this.port = -1;
      this.levels = Maps.newLinkedHashMap();
      this.running = true;
      this.ticksUntilAutosave = 6000;
      this.tickTimesNanos = new long[100];
      this.aggregatedTickTimesNanos = 0L;
      this.lastTickNanos = Util.getNanos();
      this.taskExecutionStartNanos = Util.getNanos();
      this.nextTickTimeNanos = Util.getNanos();
      this.scoreboard = new ServerScoreboard(this);
      this.customBossEvents = new CustomBossEvents();
      this.registries = var4.registries();
      this.worldData = var4.worldData();
      if (!this.registries.compositeAccess().registryOrThrow(Registries.LEVEL_STEM).containsKey(LevelStem.OVERWORLD)) {
         throw new IllegalStateException("Missing Overworld dimension data");
      } else {
         this.proxy = var5;
         this.packRepository = var3;
         this.resources = new ReloadableResources(var4.resourceManager(), var4.dataPackResources());
         this.services = var7;
         if (var7.profileCache() != null) {
            var7.profileCache().setExecutor(this);
         }

         this.connection = new ServerConnectionListener(this);
         this.tickRateManager = new ServerTickRateManager(this);
         this.progressListenerFactory = var8;
         this.storageSource = var2;
         this.playerDataStorage = var2.createPlayerStorage();
         this.fixerUpper = var6;
         this.functionManager = new ServerFunctionManager(this, this.resources.managers.getFunctionLibrary());
         HolderLookup.RegistryLookup var9 = this.registries.compositeAccess().registryOrThrow(Registries.BLOCK).asLookup().filterFeatures(this.worldData.enabledFeatures());
         this.structureTemplateManager = new StructureTemplateManager(var4.resourceManager(), var2, var6, var9);
         this.serverThread = var1;
         this.executor = Util.backgroundExecutor();
      }
   }

   private void readScoreboard(DimensionDataStorage var1) {
      var1.computeIfAbsent(this.getScoreboard().dataFactory(), "scoreboard");
   }

   protected abstract boolean initServer() throws IOException;

   protected void loadLevel() {
      if (!JvmProfiler.INSTANCE.isRunning()) {
      }

      boolean var1 = false;
      ProfiledDuration var2 = JvmProfiler.INSTANCE.onWorldLoadedStarted();
      this.worldData.setModdedInfo(this.getServerModName(), this.getModdedStatus().shouldReportAsModified());
      ChunkProgressListener var3 = this.progressListenerFactory.create(this.worldData.getGameRules().getInt(GameRules.RULE_SPAWN_CHUNK_RADIUS));
      this.createLevels(var3);
      this.forceDifficulty();
      this.prepareLevels(var3);
      if (var2 != null) {
         var2.finish();
      }

      if (var1) {
         try {
            JvmProfiler.INSTANCE.stop();
         } catch (Throwable var5) {
            LOGGER.warn("Failed to stop JFR profiling", var5);
         }
      }

   }

   protected void forceDifficulty() {
   }

   protected void createLevels(ChunkProgressListener var1) {
      ServerLevelData var2 = this.worldData.overworldData();
      boolean var3 = this.worldData.isDebugWorld();
      Registry var4 = this.registries.compositeAccess().registryOrThrow(Registries.LEVEL_STEM);
      WorldOptions var5 = this.worldData.worldGenOptions();
      long var6 = var5.seed();
      long var8 = BiomeManager.obfuscateSeed(var6);
      ImmutableList var10 = ImmutableList.of(new PhantomSpawner(), new PatrolSpawner(), new CatSpawner(), new VillageSiege(), new WanderingTraderSpawner(var2));
      LevelStem var11 = (LevelStem)var4.get(LevelStem.OVERWORLD);
      ServerLevel var12 = new ServerLevel(this, this.executor, this.storageSource, var2, Level.OVERWORLD, var11, var1, var3, var8, var10, true, (RandomSequences)null);
      this.levels.put(Level.OVERWORLD, var12);
      DimensionDataStorage var13 = var12.getDataStorage();
      this.readScoreboard(var13);
      this.commandStorage = new CommandStorage(var13);
      WorldBorder var14 = var12.getWorldBorder();
      if (!var2.isInitialized()) {
         try {
            setInitialSpawn(var12, var2, var5.generateBonusChest(), var3);
            var2.setInitialized(true);
            if (var3) {
               this.setupDebugLevel(this.worldData);
            }
         } catch (Throwable var23) {
            CrashReport var16 = CrashReport.forThrowable(var23, "Exception initializing level");

            try {
               var12.fillReportDetails(var16);
            } catch (Throwable var22) {
            }

            throw new ReportedException(var16);
         }

         var2.setInitialized(true);
      }

      this.getPlayerList().addWorldborderListener(var12);
      if (this.worldData.getCustomBossEvents() != null) {
         this.getCustomBossEvents().load(this.worldData.getCustomBossEvents(), this.registryAccess());
      }

      RandomSequences var15 = var12.getRandomSequences();
      Iterator var24 = var4.entrySet().iterator();

      while(var24.hasNext()) {
         Map.Entry var17 = (Map.Entry)var24.next();
         ResourceKey var18 = (ResourceKey)var17.getKey();
         if (var18 != LevelStem.OVERWORLD) {
            ResourceKey var19 = ResourceKey.create(Registries.DIMENSION, var18.location());
            DerivedLevelData var20 = new DerivedLevelData(this.worldData, var2);
            ServerLevel var21 = new ServerLevel(this, this.executor, this.storageSource, var20, var19, (LevelStem)var17.getValue(), var1, var3, var8, ImmutableList.of(), false, var15);
            var14.addListener(new BorderChangeListener.DelegateBorderChangeListener(var21.getWorldBorder()));
            this.levels.put(var19, var21);
         }
      }

      var14.applySettings(var2.getWorldBorder());
   }

   private static void setInitialSpawn(ServerLevel var0, ServerLevelData var1, boolean var2, boolean var3) {
      if (var3) {
         var1.setSpawn(BlockPos.ZERO.above(80), 0.0F);
      } else {
         ServerChunkCache var4 = var0.getChunkSource();
         ChunkPos var5 = new ChunkPos(var4.randomState().sampler().findSpawnPosition());
         int var6 = var4.getGenerator().getSpawnHeight(var0);
         if (var6 < var0.getMinBuildHeight()) {
            BlockPos var7 = var5.getWorldPosition();
            var6 = var0.getHeight(Heightmap.Types.WORLD_SURFACE, var7.getX() + 8, var7.getZ() + 8);
         }

         var1.setSpawn(var5.getWorldPosition().offset(8, var6, 8), 0.0F);
         int var13 = 0;
         int var8 = 0;
         int var9 = 0;
         int var10 = -1;

         for(int var11 = 0; var11 < Mth.square(11); ++var11) {
            if (var13 >= -5 && var13 <= 5 && var8 >= -5 && var8 <= 5) {
               BlockPos var12 = PlayerRespawnLogic.getSpawnPosInChunk(var0, new ChunkPos(var5.x + var13, var5.z + var8));
               if (var12 != null) {
                  var1.setSpawn(var12, 0.0F);
                  break;
               }
            }

            if (var13 == var8 || var13 < 0 && var13 == -var8 || var13 > 0 && var13 == 1 - var8) {
               int var14 = var9;
               var9 = -var10;
               var10 = var14;
            }

            var13 += var9;
            var8 += var10;
         }

         if (var2) {
            var0.registryAccess().registry(Registries.CONFIGURED_FEATURE).flatMap((var0x) -> {
               return var0x.getHolder(MiscOverworldFeatures.BONUS_CHEST);
            }).ifPresent((var3x) -> {
               ((ConfiguredFeature)var3x.value()).place(var0, var4.getGenerator(), var0.random, var1.getSpawnPos());
            });
         }

      }
   }

   private void setupDebugLevel(WorldData var1) {
      var1.setDifficulty(Difficulty.PEACEFUL);
      var1.setDifficultyLocked(true);
      ServerLevelData var2 = var1.overworldData();
      var2.setRaining(false);
      var2.setThundering(false);
      var2.setClearWeatherTime(1000000000);
      var2.setDayTime(6000L);
      var2.setGameType(GameType.SPECTATOR);
   }

   private void prepareLevels(ChunkProgressListener var1) {
      ServerLevel var2 = this.overworld();
      LOGGER.info("Preparing start region for dimension {}", var2.dimension().location());
      BlockPos var3 = var2.getSharedSpawnPos();
      var1.updateSpawnPos(new ChunkPos(var3));
      ServerChunkCache var4 = var2.getChunkSource();
      this.nextTickTimeNanos = Util.getNanos();
      var2.setDefaultSpawnPos(var3, var2.getSharedSpawnAngle());
      int var5 = this.getGameRules().getInt(GameRules.RULE_SPAWN_CHUNK_RADIUS);
      int var6 = var5 > 0 ? Mth.square(ChunkProgressListener.calculateDiameter(var5)) : 0;

      while(var4.getTickingGenerated() < var6) {
         this.nextTickTimeNanos = Util.getNanos() + PREPARE_LEVELS_DEFAULT_DELAY_NANOS;
         this.waitUntilNextTick();
      }

      this.nextTickTimeNanos = Util.getNanos() + PREPARE_LEVELS_DEFAULT_DELAY_NANOS;
      this.waitUntilNextTick();
      Iterator var7 = this.levels.values().iterator();

      while(true) {
         ServerLevel var8;
         ForcedChunksSavedData var9;
         do {
            if (!var7.hasNext()) {
               this.nextTickTimeNanos = Util.getNanos() + PREPARE_LEVELS_DEFAULT_DELAY_NANOS;
               this.waitUntilNextTick();
               var1.stop();
               this.updateMobSpawningFlags();
               return;
            }

            var8 = (ServerLevel)var7.next();
            var9 = (ForcedChunksSavedData)var8.getDataStorage().get(ForcedChunksSavedData.factory(), "chunks");
         } while(var9 == null);

         LongIterator var10 = var9.getChunks().iterator();

         while(var10.hasNext()) {
            long var11 = var10.nextLong();
            ChunkPos var13 = new ChunkPos(var11);
            var8.getChunkSource().updateChunkForced(var13, true);
         }
      }
   }

   public GameType getDefaultGameType() {
      return this.worldData.getGameType();
   }

   public boolean isHardcore() {
      return this.worldData.isHardcore();
   }

   public abstract int getOperatorUserPermissionLevel();

   public abstract int getFunctionCompilationLevel();

   public abstract boolean shouldRconBroadcast();

   public boolean saveAllChunks(boolean var1, boolean var2, boolean var3) {
      boolean var4 = false;

      for(Iterator var5 = this.getAllLevels().iterator(); var5.hasNext(); var4 = true) {
         ServerLevel var6 = (ServerLevel)var5.next();
         if (!var1) {
            LOGGER.info("Saving chunks for level '{}'/{}", var6, var6.dimension().location());
         }

         var6.save((ProgressListener)null, var2, var6.noSave && !var3);
      }

      ServerLevel var9 = this.overworld();
      ServerLevelData var10 = this.worldData.overworldData();
      var10.setWorldBorder(var9.getWorldBorder().createSettings());
      this.worldData.setCustomBossEvents(this.getCustomBossEvents().save(this.registryAccess()));
      this.storageSource.saveDataTag(this.registryAccess(), this.worldData, this.getPlayerList().getSingleplayerData());
      if (var2) {
         Iterator var7 = this.getAllLevels().iterator();

         while(var7.hasNext()) {
            ServerLevel var8 = (ServerLevel)var7.next();
            LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", var8.getChunkSource().chunkMap.getStorageName());
         }

         LOGGER.info("ThreadedAnvilChunkStorage: All dimensions are saved");
      }

      return var4;
   }

   public boolean saveEverything(boolean var1, boolean var2, boolean var3) {
      boolean var4;
      try {
         this.isSaving = true;
         this.getPlayerList().saveAll();
         var4 = this.saveAllChunks(var1, var2, var3);
      } finally {
         this.isSaving = false;
      }

      return var4;
   }

   public void close() {
      this.stopServer();
   }

   public void stopServer() {
      if (this.metricsRecorder.isRecording()) {
         this.cancelRecordingMetrics();
      }

      LOGGER.info("Stopping server");
      this.getConnection().stop();
      this.isSaving = true;
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

      while(this.levels.values().stream().anyMatch((var0) -> {
         return var0.getChunkSource().chunkMap.hasWork();
      })) {
         this.nextTickTimeNanos = Util.getNanos() + TimeUtil.NANOSECONDS_PER_MILLISECOND;
         var1 = this.getAllLevels().iterator();

         while(var1.hasNext()) {
            var2 = (ServerLevel)var1.next();
            var2.getChunkSource().removeTicketsOnClosing();
            var2.getChunkSource().tick(() -> {
               return true;
            }, false);
         }

         this.waitUntilNextTick();
      }

      this.saveAllChunks(false, true, false);
      var1 = this.getAllLevels().iterator();

      while(var1.hasNext()) {
         var2 = (ServerLevel)var1.next();
         if (var2 != null) {
            try {
               var2.close();
            } catch (IOException var5) {
               LOGGER.error("Exception closing the level", var5);
            }
         }
      }

      this.isSaving = false;
      this.resources.close();

      try {
         this.storageSource.close();
      } catch (IOException var4) {
         LOGGER.error("Failed to unlock level {}", this.storageSource.getLevelId(), var4);
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

   protected void runServer() {
      try {
         if (!this.initServer()) {
            throw new IllegalStateException("Failed to initialize server");
         }

         this.nextTickTimeNanos = Util.getNanos();
         this.statusIcon = (ServerStatus.Favicon)this.loadStatusIcon().orElse((Object)null);
         this.status = this.buildServerStatus();

         while(this.running) {
            long var1;
            if (!this.isPaused() && this.tickRateManager.isSprinting() && this.tickRateManager.checkShouldSprintThisTick()) {
               var1 = 0L;
               this.nextTickTimeNanos = Util.getNanos();
               this.lastOverloadWarningNanos = this.nextTickTimeNanos;
            } else {
               var1 = this.tickRateManager.nanosecondsPerTick();
               long var48 = Util.getNanos() - this.nextTickTimeNanos;
               if (var48 > OVERLOADED_THRESHOLD_NANOS + 20L * var1 && this.nextTickTimeNanos - this.lastOverloadWarningNanos >= OVERLOADED_WARNING_INTERVAL_NANOS + 100L * var1) {
                  long var5 = var48 / var1;
                  LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", var48 / TimeUtil.NANOSECONDS_PER_MILLISECOND, var5);
                  this.nextTickTimeNanos += var5 * var1;
                  this.lastOverloadWarningNanos = this.nextTickTimeNanos;
               }
            }

            boolean var49 = var1 == 0L;
            if (this.debugCommandProfilerDelayStart) {
               this.debugCommandProfilerDelayStart = false;
               this.debugCommandProfiler = new TimeProfiler(Util.getNanos(), this.tickCount);
            }

            this.nextTickTimeNanos += var1;
            this.startMetricsRecordingTick();
            this.profiler.push("tick");
            this.tickServer(var49 ? () -> {
               return false;
            } : this::haveTime);
            this.profiler.popPush("nextTickWait");
            this.mayHaveDelayedTasks = true;
            this.delayedTasksMaxNextTickTimeNanos = Math.max(Util.getNanos() + var1, this.nextTickTimeNanos);
            this.startMeasuringTaskExecutionTime();
            this.waitUntilNextTick();
            this.finishMeasuringTaskExecutionTime();
            if (var49) {
               this.tickRateManager.endTickWork();
            }

            this.profiler.pop();
            this.logFullTickTime();
            this.endMetricsRecordingTick();
            this.isReady = true;
            JvmProfiler.INSTANCE.onServerTick(this.smoothedTickTimeMillis);
         }
      } catch (Throwable var46) {
         LOGGER.error("Encountered an unexpected exception", var46);
         CrashReport var2 = constructOrExtractCrashReport(var46);
         this.fillSystemReport(var2.getSystemReport());
         File var3 = new File(new File(this.getServerDirectory(), "crash-reports"), "crash-" + Util.getFilenameFormattedDateTime() + "-server.txt");
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
         } catch (Throwable var44) {
            LOGGER.error("Exception stopping the server", var44);
         } finally {
            if (this.services.profileCache() != null) {
               this.services.profileCache().clearExecutor();
            }

            this.onServerExit();
         }

      }

   }

   private void logFullTickTime() {
      long var1 = Util.getNanos();
      if (this.isTickTimeLoggingEnabled()) {
         this.getTickTimeLogger().logSample(var1 - this.lastTickNanos);
      }

      this.lastTickNanos = var1;
   }

   private void startMeasuringTaskExecutionTime() {
      if (this.isTickTimeLoggingEnabled()) {
         this.taskExecutionStartNanos = Util.getNanos();
         this.idleTimeNanos = 0L;
      }

   }

   private void finishMeasuringTaskExecutionTime() {
      if (this.isTickTimeLoggingEnabled()) {
         SampleLogger var1 = this.getTickTimeLogger();
         var1.logPartialSample(Util.getNanos() - this.taskExecutionStartNanos - this.idleTimeNanos, TpsDebugDimensions.SCHEDULED_TASKS.ordinal());
         var1.logPartialSample(this.idleTimeNanos, TpsDebugDimensions.IDLE.ordinal());
      }

   }

   private static CrashReport constructOrExtractCrashReport(Throwable var0) {
      ReportedException var1 = null;

      for(Throwable var2 = var0; var2 != null; var2 = var2.getCause()) {
         if (var2 instanceof ReportedException var3) {
            var1 = var3;
         }
      }

      CrashReport var4;
      if (var1 != null) {
         var4 = var1.getReport();
         if (var1 != var0) {
            var4.addCategory("Wrapped in").setDetailError("Wrapping exception", var0);
         }
      } else {
         var4 = new CrashReport("Exception in server tick loop", var0);
      }

      return var4;
   }

   private boolean haveTime() {
      return this.runningTask() || Util.getNanos() < (this.mayHaveDelayedTasks ? this.delayedTasksMaxNextTickTimeNanos : this.nextTickTimeNanos);
   }

   protected void waitUntilNextTick() {
      this.runAllTasks();
      this.managedBlock(() -> {
         return !this.haveTime();
      });
   }

   public void waitForTasks() {
      boolean var1 = this.isTickTimeLoggingEnabled();
      long var2 = var1 ? Util.getNanos() : 0L;
      super.waitForTasks();
      if (var1) {
         this.idleTimeNanos += Util.getNanos() - var2;
      }

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
         if (this.tickRateManager.isSprinting() || this.haveTime()) {
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

   protected void doRunTask(TickTask var1) {
      this.getProfiler().incrementCounter("runTask");
      super.doRunTask(var1);
   }

   private Optional<ServerStatus.Favicon> loadStatusIcon() {
      Optional var1 = Optional.of(this.getFile("server-icon.png").toPath()).filter((var0) -> {
         return Files.isRegularFile(var0, new LinkOption[0]);
      }).or(() -> {
         return this.storageSource.getIconFile().filter((var0) -> {
            return Files.isRegularFile(var0, new LinkOption[0]);
         });
      });
      return var1.flatMap((var0) -> {
         try {
            BufferedImage var1 = ImageIO.read(var0.toFile());
            Preconditions.checkState(var1.getWidth() == 64, "Must be 64 pixels wide");
            Preconditions.checkState(var1.getHeight() == 64, "Must be 64 pixels high");
            ByteArrayOutputStream var2 = new ByteArrayOutputStream();
            ImageIO.write(var1, "PNG", var2);
            return Optional.of(new ServerStatus.Favicon(var2.toByteArray()));
         } catch (Exception var3) {
            LOGGER.error("Couldn't load server icon", var3);
            return Optional.empty();
         }
      });
   }

   public Optional<Path> getWorldScreenshotFile() {
      return this.storageSource.getIconFile();
   }

   public File getServerDirectory() {
      return new File(".");
   }

   public void onServerCrash(CrashReport var1) {
   }

   public void onServerExit() {
   }

   public boolean isPaused() {
      return false;
   }

   public void tickServer(BooleanSupplier var1) {
      long var2 = Util.getNanos();
      ++this.tickCount;
      this.tickRateManager.tick();
      this.tickChildren(var1);
      if (var2 - this.lastServerStatus >= STATUS_EXPIRE_TIME_NANOS) {
         this.lastServerStatus = var2;
         this.status = this.buildServerStatus();
      }

      --this.ticksUntilAutosave;
      if (this.ticksUntilAutosave <= 0) {
         this.ticksUntilAutosave = this.computeNextAutosaveInterval();
         LOGGER.debug("Autosave started");
         this.profiler.push("save");
         this.saveEverything(true, false, false);
         this.profiler.pop();
         LOGGER.debug("Autosave finished");
      }

      this.profiler.push("tallying");
      long var4 = Util.getNanos() - var2;
      int var6 = this.tickCount % 100;
      this.aggregatedTickTimesNanos -= this.tickTimesNanos[var6];
      this.aggregatedTickTimesNanos += var4;
      this.tickTimesNanos[var6] = var4;
      this.smoothedTickTimeMillis = this.smoothedTickTimeMillis * 0.8F + (float)var4 / (float)TimeUtil.NANOSECONDS_PER_MILLISECOND * 0.19999999F;
      this.logTickMethodTime(var2);
      this.profiler.pop();
   }

   private void logTickMethodTime(long var1) {
      if (this.isTickTimeLoggingEnabled()) {
         this.getTickTimeLogger().logPartialSample(Util.getNanos() - var1, TpsDebugDimensions.TICK_SERVER_METHOD.ordinal());
      }

   }

   private int computeNextAutosaveInterval() {
      float var1;
      if (this.tickRateManager.isSprinting()) {
         long var2 = this.getAverageTickTimeNanos() + 1L;
         var1 = (float)TimeUtil.NANOSECONDS_PER_SECOND / (float)var2;
      } else {
         var1 = this.tickRateManager.tickrate();
      }

      boolean var4 = true;
      return Math.max(100, (int)(var1 * 300.0F));
   }

   public void onTickRateChanged() {
      int var1 = this.computeNextAutosaveInterval();
      if (var1 < this.ticksUntilAutosave) {
         this.ticksUntilAutosave = var1;
      }

   }

   protected abstract SampleLogger getTickTimeLogger();

   public abstract boolean isTickTimeLoggingEnabled();

   private ServerStatus buildServerStatus() {
      ServerStatus.Players var1 = this.buildPlayerStatus();
      return new ServerStatus(Component.nullToEmpty(this.motd), Optional.of(var1), Optional.of(ServerStatus.Version.current()), Optional.ofNullable(this.statusIcon), this.enforceSecureProfile());
   }

   private ServerStatus.Players buildPlayerStatus() {
      List var1 = this.playerList.getPlayers();
      int var2 = this.getMaxPlayers();
      if (this.hidesOnlinePlayers()) {
         return new ServerStatus.Players(var2, var1.size(), List.of());
      } else {
         int var3 = Math.min(var1.size(), 12);
         ObjectArrayList var4 = new ObjectArrayList(var3);
         int var5 = Mth.nextInt(this.random, 0, var1.size() - var3);

         for(int var6 = 0; var6 < var3; ++var6) {
            ServerPlayer var7 = (ServerPlayer)var1.get(var5 + var6);
            var4.add(var7.allowsListing() ? var7.getGameProfile() : ANONYMOUS_PLAYER_PROFILE);
         }

         Util.shuffle(var4, this.random);
         return new ServerStatus.Players(var2, var1.size(), var4);
      }
   }

   public void tickChildren(BooleanSupplier var1) {
      this.getPlayerList().getPlayers().forEach((var0) -> {
         var0.connection.suspendFlushing();
      });
      this.profiler.push("commandFunctions");
      this.getFunctions().tick();
      this.profiler.popPush("levels");
      Iterator var2 = this.getAllLevels().iterator();

      while(var2.hasNext()) {
         ServerLevel var3 = (ServerLevel)var2.next();
         this.profiler.push(() -> {
            String var10000 = String.valueOf(var3);
            return var10000 + " " + String.valueOf(var3.dimension().location());
         });
         if (this.tickCount % 20 == 0) {
            this.profiler.push("timeSync");
            this.synchronizeTime(var3);
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

      this.profiler.popPush("connection");
      this.getConnection().tick();
      this.profiler.popPush("players");
      this.playerList.tick();
      if (SharedConstants.IS_RUNNING_IN_IDE && this.tickRateManager.runsNormally()) {
         GameTestTicker.SINGLETON.tick();
      }

      this.profiler.popPush("server gui refresh");

      for(int var7 = 0; var7 < this.tickables.size(); ++var7) {
         ((Runnable)this.tickables.get(var7)).run();
      }

      this.profiler.popPush("send chunks");
      var2 = this.playerList.getPlayers().iterator();

      while(var2.hasNext()) {
         ServerPlayer var8 = (ServerPlayer)var2.next();
         var8.connection.chunkSender.sendNextChunks(var8);
         var8.connection.resumeFlushing();
      }

      this.profiler.pop();
   }

   private void synchronizeTime(ServerLevel var1) {
      this.playerList.broadcastAll(new ClientboundSetTimePacket(var1.getGameTime(), var1.getDayTime(), var1.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)), var1.dimension());
   }

   public void forceTimeSynchronization() {
      this.profiler.push("timeSync");
      Iterator var1 = this.getAllLevels().iterator();

      while(var1.hasNext()) {
         ServerLevel var2 = (ServerLevel)var1.next();
         this.synchronizeTime(var2);
      }

      this.profiler.pop();
   }

   public boolean isNetherEnabled() {
      return true;
   }

   public void addTickable(Runnable var1) {
      this.tickables.add(var1);
   }

   protected void setId(String var1) {
      this.serverId = var1;
   }

   public boolean isShutdown() {
      return !this.serverThread.isAlive();
   }

   public File getFile(String var1) {
      return new File(this.getServerDirectory(), var1);
   }

   public final ServerLevel overworld() {
      return (ServerLevel)this.levels.get(Level.OVERWORLD);
   }

   @Nullable
   public ServerLevel getLevel(ResourceKey<Level> var1) {
      return (ServerLevel)this.levels.get(var1);
   }

   public Set<ResourceKey<Level>> levelKeys() {
      return this.levels.keySet();
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

   @DontObfuscate
   public String getServerModName() {
      return "vanilla";
   }

   public SystemReport fillSystemReport(SystemReport var1) {
      var1.setDetail("Server Running", () -> {
         return Boolean.toString(this.running);
      });
      if (this.playerList != null) {
         var1.setDetail("Player Count", () -> {
            int var10000 = this.playerList.getPlayerCount();
            return "" + var10000 + " / " + this.playerList.getMaxPlayers() + "; " + String.valueOf(this.playerList.getPlayers());
         });
      }

      var1.setDetail("Data Packs", () -> {
         return (String)this.packRepository.getSelectedPacks().stream().map((var0) -> {
            String var10000 = var0.getId();
            return var10000 + (var0.getCompatibility().isCompatible() ? "" : " (incompatible)");
         }).collect(Collectors.joining(", "));
      });
      var1.setDetail("Enabled Feature Flags", () -> {
         return (String)FeatureFlags.REGISTRY.toNames(this.worldData.enabledFeatures()).stream().map(ResourceLocation::toString).collect(Collectors.joining(", "));
      });
      var1.setDetail("World Generation", () -> {
         return this.worldData.worldGenSettingsLifecycle().toString();
      });
      var1.setDetail("World Seed", () -> {
         return String.valueOf(this.worldData.worldGenOptions().seed());
      });
      if (this.serverId != null) {
         var1.setDetail("Server Id", () -> {
            return this.serverId;
         });
      }

      return this.fillServerSystemReport(var1);
   }

   public abstract SystemReport fillServerSystemReport(SystemReport var1);

   public ModCheck getModdedStatus() {
      return ModCheck.identify("vanilla", this::getServerModName, "Server", MinecraftServer.class);
   }

   public void sendSystemMessage(Component var1) {
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

   @Nullable
   public GameProfile getSingleplayerProfile() {
      return this.singleplayerProfile;
   }

   public void setSingleplayerProfile(@Nullable GameProfile var1) {
      this.singleplayerProfile = var1;
   }

   public boolean isSingleplayer() {
      return this.singleplayerProfile != null;
   }

   protected void initializeKeyPair() {
      LOGGER.info("Generating keypair");

      try {
         this.keyPair = Crypt.generateKeyPair();
      } catch (CryptException var2) {
         throw new IllegalStateException("Failed to generate key pair", var2);
      }
   }

   public void setDifficulty(Difficulty var1, boolean var2) {
      if (var2 || !this.worldData.isDifficultyLocked()) {
         this.worldData.setDifficulty(this.worldData.isHardcore() ? Difficulty.HARD : var1);
         this.updateMobSpawningFlags();
         this.getPlayerList().getPlayers().forEach(this::sendDifficultyUpdate);
      }
   }

   public int getScaledTrackingDistance(int var1) {
      return var1;
   }

   private void updateMobSpawningFlags() {
      Iterator var1 = this.getAllLevels().iterator();

      while(var1.hasNext()) {
         ServerLevel var2 = (ServerLevel)var1.next();
         var2.setSpawnSettings(this.isSpawningMonsters(), this.isSpawningAnimals());
      }

   }

   public void setDifficultyLocked(boolean var1) {
      this.worldData.setDifficultyLocked(var1);
      this.getPlayerList().getPlayers().forEach(this::sendDifficultyUpdate);
   }

   private void sendDifficultyUpdate(ServerPlayer var1) {
      LevelData var2 = var1.level().getLevelData();
      var1.connection.send(new ClientboundChangeDifficultyPacket(var2.getDifficulty(), var2.isDifficultyLocked()));
   }

   public boolean isSpawningMonsters() {
      return this.worldData.getDifficulty() != Difficulty.PEACEFUL;
   }

   public boolean isDemo() {
      return this.isDemo;
   }

   public void setDemo(boolean var1) {
      this.isDemo = var1;
   }

   public Optional<ServerResourcePackInfo> getServerResourcePack() {
      return Optional.empty();
   }

   public boolean isResourcePackRequired() {
      return this.getServerResourcePack().filter(ServerResourcePackInfo::isRequired).isPresent();
   }

   public abstract boolean isDedicatedServer();

   public abstract int getRateLimitPacketsPerSecond();

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

   public boolean isSpawningAnimals() {
      return true;
   }

   public boolean areNpcsEnabled() {
      return true;
   }

   public abstract boolean isEpollEnabled();

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

   public void setDefaultGameType(GameType var1) {
      this.worldData.setGameType(var1);
   }

   public ServerConnectionListener getConnection() {
      return this.connection;
   }

   public boolean isReady() {
      return this.isReady;
   }

   public boolean hasGui() {
      return false;
   }

   public boolean publishServer(@Nullable GameType var1, boolean var2, int var3) {
      return false;
   }

   public int getTickCount() {
      return this.tickCount;
   }

   public int getSpawnProtectionRadius() {
      return 16;
   }

   public boolean isUnderSpawnProtection(ServerLevel var1, BlockPos var2, Player var3) {
      return false;
   }

   public boolean repliesToStatus() {
      return true;
   }

   public boolean hidesOnlinePlayers() {
      return false;
   }

   public Proxy getProxy() {
      return this.proxy;
   }

   public int getPlayerIdleTimeout() {
      return this.playerIdleTimeout;
   }

   public void setPlayerIdleTimeout(int var1) {
      this.playerIdleTimeout = var1;
   }

   public MinecraftSessionService getSessionService() {
      return this.services.sessionService();
   }

   @Nullable
   public SignatureValidator getProfileKeySignatureValidator() {
      return this.services.profileKeySignatureValidator();
   }

   public GameProfileRepository getProfileRepository() {
      return this.services.profileRepository();
   }

   @Nullable
   public GameProfileCache getProfileCache() {
      return this.services.profileCache();
   }

   @Nullable
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

   public void executeIfPossible(Runnable var1) {
      if (this.isStopped()) {
         throw new RejectedExecutionException("Server already shutting down");
      } else {
         super.executeIfPossible(var1);
      }
   }

   public Thread getRunningThread() {
      return this.serverThread;
   }

   public int getCompressionThreshold() {
      return 256;
   }

   public boolean enforceSecureProfile() {
      return false;
   }

   public long getNextTickTime() {
      return this.nextTickTimeNanos;
   }

   public DataFixer getFixerUpper() {
      return this.fixerUpper;
   }

   public int getSpawnRadius(@Nullable ServerLevel var1) {
      return var1 != null ? var1.getGameRules().getInt(GameRules.RULE_SPAWN_RADIUS) : 10;
   }

   public ServerAdvancementManager getAdvancements() {
      return this.resources.managers.getAdvancements();
   }

   public ServerFunctionManager getFunctions() {
      return this.functionManager;
   }

   public CompletableFuture<Void> reloadResources(Collection<String> var1) {
      CompletableFuture var2 = CompletableFuture.supplyAsync(() -> {
         Stream var10000 = var1.stream();
         PackRepository var10001 = this.packRepository;
         Objects.requireNonNull(var10001);
         return (ImmutableList)var10000.map(var10001::getPack).filter(Objects::nonNull).map(Pack::open).collect(ImmutableList.toImmutableList());
      }, this).thenCompose((var1x) -> {
         MultiPackResourceManager var2 = new MultiPackResourceManager(PackType.SERVER_DATA, var1x);
         return ReloadableServerResources.loadResources(var2, this.registries, this.worldData.enabledFeatures(), this.isDedicatedServer() ? Commands.CommandSelection.DEDICATED : Commands.CommandSelection.INTEGRATED, this.getFunctionCompilationLevel(), this.executor, this).whenComplete((var1, var2x) -> {
            if (var2x != null) {
               var2.close();
            }

         }).thenApply((var1) -> {
            return new ReloadableResources(var2, var1);
         });
      }).thenAcceptAsync((var2x) -> {
         this.resources.close();
         this.resources = var2x;
         this.packRepository.setSelected(var1);
         WorldDataConfiguration var3 = new WorldDataConfiguration(getSelectedPacks(this.packRepository), this.worldData.enabledFeatures());
         this.worldData.setDataConfiguration(var3);
         this.resources.managers.updateRegistryTags();
         this.getPlayerList().saveAll();
         this.getPlayerList().reloadResources();
         this.functionManager.replaceLibrary(this.resources.managers.getFunctionLibrary());
         this.structureTemplateManager.onResourceManagerReload(this.resources.resourceManager);
      }, this);
      if (this.isSameThread()) {
         Objects.requireNonNull(var2);
         this.managedBlock(var2::isDone);
      }

      return var2;
   }

   public static WorldDataConfiguration configurePackRepository(PackRepository var0, DataPackConfig var1, boolean var2, FeatureFlagSet var3) {
      var0.reload();
      if (var2) {
         var0.setSelected(Collections.singleton("vanilla"));
         return WorldDataConfiguration.DEFAULT;
      } else {
         LinkedHashSet var4 = Sets.newLinkedHashSet();
         Iterator var5 = var1.getEnabled().iterator();

         while(var5.hasNext()) {
            String var6 = (String)var5.next();
            if (var0.isAvailable(var6)) {
               var4.add(var6);
            } else {
               LOGGER.warn("Missing data pack {}", var6);
            }
         }

         var5 = var0.getAvailablePacks().iterator();

         while(true) {
            String var7;
            FeatureFlagSet var8;
            Pack var12;
            do {
               if (!var5.hasNext()) {
                  if (var4.isEmpty()) {
                     LOGGER.info("No datapacks selected, forcing vanilla");
                     var4.add("vanilla");
                  }

                  var0.setSelected(var4);
                  DataPackConfig var11 = getSelectedPacks(var0);
                  FeatureFlagSet var13 = var0.getRequestedFeatureFlags();
                  return new WorldDataConfiguration(var11, var13);
               }

               var12 = (Pack)var5.next();
               var7 = var12.getId();
               var8 = var12.getRequestedFeatures();
               FeatureFlagSet var9 = var0.getRequestedFeatureFlags();
               if (var12.getPackSource() == PackSource.FEATURE && !var8.isEmpty() && var8.isSubsetOf(var9) && !var4.contains(var7)) {
                  LOGGER.info("Found feature pack for requested feature, forcing to enabled");
                  var4.add(var7);
                  break;
               }
            } while(var1.getDisabled().contains(var7));

            boolean var10 = var4.contains(var7);
            if (!var10 && var12.getPackSource().shouldAddAutomatically()) {
               if (var8.isSubsetOf(var3)) {
                  LOGGER.info("Found new data pack {}, loading it automatically", var7);
                  var4.add(var7);
               } else {
                  LOGGER.info("Found new data pack {}, but can't load it due to missing features {}", var7, FeatureFlags.printMissingFlags(var3, var8));
               }
            }

            if (var10 && !var8.isSubsetOf(var3)) {
               LOGGER.warn("Pack {} requires features {} that are not enabled for this world, disabling pack.", var7, FeatureFlags.printMissingFlags(var3, var8));
               var4.remove(var7);
            }
         }
      }
   }

   private static DataPackConfig getSelectedPacks(PackRepository var0) {
      Collection var1 = var0.getSelectedIds();
      ImmutableList var2 = ImmutableList.copyOf(var1);
      List var3 = (List)var0.getAvailableIds().stream().filter((var1x) -> {
         return !var1.contains(var1x);
      }).collect(ImmutableList.toImmutableList());
      return new DataPackConfig(var2, var3);
   }

   public void kickUnlistedPlayers(CommandSourceStack var1) {
      if (this.isEnforceWhitelist()) {
         PlayerList var2 = var1.getServer().getPlayerList();
         UserWhiteList var3 = var2.getWhiteList();
         ArrayList var4 = Lists.newArrayList(var2.getPlayers());
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            ServerPlayer var6 = (ServerPlayer)var5.next();
            if (!var3.isWhiteListed(var6.getGameProfile())) {
               var6.connection.disconnect(Component.translatable("multiplayer.disconnect.not_whitelisted"));
            }
         }

      }
   }

   public PackRepository getPackRepository() {
      return this.packRepository;
   }

   public Commands getCommands() {
      return this.resources.managers.getCommands();
   }

   public CommandSourceStack createCommandSourceStack() {
      ServerLevel var1 = this.overworld();
      return new CommandSourceStack(this, var1 == null ? Vec3.ZERO : Vec3.atLowerCornerOf(var1.getSharedSpawnPos()), Vec2.ZERO, var1, 4, "Server", Component.literal("Server"), this, (Entity)null);
   }

   public boolean acceptsSuccess() {
      return true;
   }

   public boolean acceptsFailure() {
      return true;
   }

   public abstract boolean shouldInformAdmins();

   public RecipeManager getRecipeManager() {
      return this.resources.managers.getRecipeManager();
   }

   public ServerScoreboard getScoreboard() {
      return this.scoreboard;
   }

   public CommandStorage getCommandStorage() {
      if (this.commandStorage == null) {
         throw new NullPointerException("Called before server init");
      } else {
         return this.commandStorage;
      }
   }

   public GameRules getGameRules() {
      return this.overworld().getGameRules();
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

   public float getCurrentSmoothedTickTime() {
      return this.smoothedTickTimeMillis;
   }

   public ServerTickRateManager tickRateManager() {
      return this.tickRateManager;
   }

   public long getAverageTickTimeNanos() {
      return this.aggregatedTickTimesNanos / (long)Math.min(100, Math.max(this.tickCount, 1));
   }

   public long[] getTickTimesNanos() {
      return this.tickTimesNanos;
   }

   public int getProfilePermissions(GameProfile var1) {
      if (this.getPlayerList().isOp(var1)) {
         ServerOpListEntry var2 = (ServerOpListEntry)this.getPlayerList().getOps().get(var1);
         if (var2 != null) {
            return var2.getLevel();
         } else if (this.isSingleplayerOwner(var1)) {
            return 4;
         } else if (this.isSingleplayer()) {
            return this.getPlayerList().isAllowCommandsForAllPlayers() ? 4 : 0;
         } else {
            return this.getOperatorUserPermissionLevel();
         }
      } else {
         return 0;
      }
   }

   public ProfilerFiller getProfiler() {
      return this.profiler;
   }

   public abstract boolean isSingleplayerOwner(GameProfile var1);

   public void dumpServerProperties(Path var1) throws IOException {
   }

   private void saveDebugReport(Path var1) {
      Path var2 = var1.resolve("levels");

      try {
         Iterator var3 = this.levels.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry var4 = (Map.Entry)var3.next();
            ResourceLocation var5 = ((ResourceKey)var4.getKey()).location();
            Path var6 = var2.resolve(var5.getNamespace()).resolve(var5.getPath());
            Files.createDirectories(var6);
            ((ServerLevel)var4.getValue()).saveDebugReport(var6);
         }

         this.dumpGameRules(var1.resolve("gamerules.txt"));
         this.dumpClasspath(var1.resolve("classpath.txt"));
         this.dumpMiscStats(var1.resolve("stats.txt"));
         this.dumpThreads(var1.resolve("threads.txt"));
         this.dumpServerProperties(var1.resolve("server.properties.txt"));
         this.dumpNativeModules(var1.resolve("modules.txt"));
      } catch (IOException var7) {
         LOGGER.warn("Failed to save debug report", var7);
      }

   }

   private void dumpMiscStats(Path var1) throws IOException {
      BufferedWriter var2 = Files.newBufferedWriter(var1);

      try {
         ((Writer)var2).write(String.format(Locale.ROOT, "pending_tasks: %d\n", this.getPendingTasksCount()));
         ((Writer)var2).write(String.format(Locale.ROOT, "average_tick_time: %f\n", this.getCurrentSmoothedTickTime()));
         ((Writer)var2).write(String.format(Locale.ROOT, "tick_times: %s\n", Arrays.toString(this.tickTimesNanos)));
         ((Writer)var2).write(String.format(Locale.ROOT, "queue: %s\n", Util.backgroundExecutor()));
      } catch (Throwable var6) {
         if (var2 != null) {
            try {
               ((Writer)var2).close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (var2 != null) {
         ((Writer)var2).close();
      }

   }

   private void dumpGameRules(Path var1) throws IOException {
      BufferedWriter var2 = Files.newBufferedWriter(var1);

      try {
         final ArrayList var3 = Lists.newArrayList();
         final GameRules var4 = this.getGameRules();
         GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor(this) {
            public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> var1, GameRules.Type<T> var2) {
               var3.add(String.format(Locale.ROOT, "%s=%s\n", var1.getId(), var4.getRule(var1)));
            }
         });
         Iterator var5 = var3.iterator();

         while(var5.hasNext()) {
            String var6 = (String)var5.next();
            ((Writer)var2).write(var6);
         }
      } catch (Throwable var8) {
         if (var2 != null) {
            try {
               ((Writer)var2).close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (var2 != null) {
         ((Writer)var2).close();
      }

   }

   private void dumpClasspath(Path var1) throws IOException {
      BufferedWriter var2 = Files.newBufferedWriter(var1);

      try {
         String var3 = System.getProperty("java.class.path");
         String var4 = System.getProperty("path.separator");
         Iterator var5 = Splitter.on(var4).split(var3).iterator();

         while(var5.hasNext()) {
            String var6 = (String)var5.next();
            ((Writer)var2).write(var6);
            ((Writer)var2).write("\n");
         }
      } catch (Throwable var8) {
         if (var2 != null) {
            try {
               ((Writer)var2).close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (var2 != null) {
         ((Writer)var2).close();
      }

   }

   private void dumpThreads(Path var1) throws IOException {
      ThreadMXBean var2 = ManagementFactory.getThreadMXBean();
      ThreadInfo[] var3 = var2.dumpAllThreads(true, true);
      Arrays.sort(var3, Comparator.comparing(ThreadInfo::getThreadName));
      BufferedWriter var4 = Files.newBufferedWriter(var1);

      try {
         ThreadInfo[] var5 = var3;
         int var6 = var3.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            ThreadInfo var8 = var5[var7];
            ((Writer)var4).write(var8.toString());
            ((Writer)var4).write(10);
         }
      } catch (Throwable var10) {
         if (var4 != null) {
            try {
               ((Writer)var4).close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }
         }

         throw var10;
      }

      if (var4 != null) {
         ((Writer)var4).close();
      }

   }

   private void dumpNativeModules(Path var1) throws IOException {
      BufferedWriter var2 = Files.newBufferedWriter(var1);

      label50: {
         try {
            label51: {
               ArrayList var3;
               try {
                  var3 = Lists.newArrayList(NativeModuleLister.listModules());
               } catch (Throwable var7) {
                  LOGGER.warn("Failed to list native modules", var7);
                  break label51;
               }

               var3.sort(Comparator.comparing((var0) -> {
                  return var0.name;
               }));
               Iterator var4 = var3.iterator();

               while(true) {
                  if (!var4.hasNext()) {
                     break label50;
                  }

                  NativeModuleLister.NativeModuleInfo var5 = (NativeModuleLister.NativeModuleInfo)var4.next();
                  ((Writer)var2).write(var5.toString());
                  ((Writer)var2).write(10);
               }
            }
         } catch (Throwable var8) {
            if (var2 != null) {
               try {
                  ((Writer)var2).close();
               } catch (Throwable var6) {
                  var8.addSuppressed(var6);
               }
            }

            throw var8;
         }

         if (var2 != null) {
            ((Writer)var2).close();
         }

         return;
      }

      if (var2 != null) {
         ((Writer)var2).close();
      }

   }

   private void startMetricsRecordingTick() {
      if (this.willStartRecordingMetrics) {
         this.metricsRecorder = ActiveMetricsRecorder.createStarted(new ServerMetricsSamplersProvider(Util.timeSource, this.isDedicatedServer()), Util.timeSource, Util.ioPool(), new MetricsPersister("server"), this.onMetricsRecordingStopped, (var1) -> {
            this.executeBlocking(() -> {
               this.saveDebugReport(var1.resolve("server"));
            });
            this.onMetricsRecordingFinished.accept(var1);
         });
         this.willStartRecordingMetrics = false;
      }

      this.profiler = SingleTickProfiler.decorateFiller(this.metricsRecorder.getProfiler(), SingleTickProfiler.createTickProfiler("Server"));
      this.metricsRecorder.startTick();
      this.profiler.startTick();
   }

   public void endMetricsRecordingTick() {
      this.profiler.endTick();
      this.metricsRecorder.endTick();
   }

   public boolean isRecordingMetrics() {
      return this.metricsRecorder.isRecording();
   }

   public void startRecordingMetrics(Consumer<ProfileResults> var1, Consumer<Path> var2) {
      this.onMetricsRecordingStopped = (var2x) -> {
         this.stopRecordingMetrics();
         var1.accept(var2x);
      };
      this.onMetricsRecordingFinished = var2;
      this.willStartRecordingMetrics = true;
   }

   public void stopRecordingMetrics() {
      this.metricsRecorder = InactiveMetricsRecorder.INSTANCE;
   }

   public void finishRecordingMetrics() {
      this.metricsRecorder.end();
   }

   public void cancelRecordingMetrics() {
      this.metricsRecorder.cancel();
      this.profiler = this.metricsRecorder.getProfiler();
   }

   public Path getWorldPath(LevelResource var1) {
      return this.storageSource.getLevelPath(var1);
   }

   public boolean forceSynchronousWrites() {
      return true;
   }

   public StructureTemplateManager getStructureManager() {
      return this.structureTemplateManager;
   }

   public WorldData getWorldData() {
      return this.worldData;
   }

   public RegistryAccess.Frozen registryAccess() {
      return this.registries.compositeAccess();
   }

   public LayeredRegistryAccess<RegistryLayer> registries() {
      return this.registries;
   }

   public ReloadableServerRegistries.Holder reloadableRegistries() {
      return this.resources.managers.fullRegistries();
   }

   public TextFilter createTextFilterForPlayer(ServerPlayer var1) {
      return TextFilter.DUMMY;
   }

   public ServerPlayerGameMode createGameModeForPlayer(ServerPlayer var1) {
      return (ServerPlayerGameMode)(this.isDemo() ? new DemoMode(var1) : new ServerPlayerGameMode(var1));
   }

   @Nullable
   public GameType getForcedGameType() {
      return null;
   }

   public ResourceManager getResourceManager() {
      return this.resources.resourceManager;
   }

   public boolean isCurrentlySaving() {
      return this.isSaving;
   }

   public boolean isTimeProfilerRunning() {
      return this.debugCommandProfilerDelayStart || this.debugCommandProfiler != null;
   }

   public void startTimeProfiler() {
      this.debugCommandProfilerDelayStart = true;
   }

   public ProfileResults stopTimeProfiler() {
      if (this.debugCommandProfiler == null) {
         return EmptyProfileResults.EMPTY;
      } else {
         ProfileResults var1 = this.debugCommandProfiler.stop(Util.getNanos(), this.tickCount);
         this.debugCommandProfiler = null;
         return var1;
      }
   }

   public int getMaxChainedNeighborUpdates() {
      return 1000000;
   }

   public void logChatMessage(Component var1, ChatType.Bound var2, @Nullable String var3) {
      String var4 = var2.decorate(var1).getString();
      if (var3 != null) {
         LOGGER.info("[{}] {}", var3, var4);
      } else {
         LOGGER.info("{}", var4);
      }

   }

   public ChatDecorator getChatDecorator() {
      return ChatDecorator.PLAIN;
   }

   public boolean logIPs() {
      return true;
   }

   public void subscribeToDebugSample(ServerPlayer var1, RemoteDebugSampleType var2) {
   }

   public boolean acceptsTransfers() {
      return false;
   }

   public void reportChunkLoadFailure(ChunkPos var1) {
   }

   public void reportChunkSaveFailure(ChunkPos var1) {
   }

   // $FF: synthetic method
   public void doRunTask(Runnable var1) {
      this.doRunTask((TickTask)var1);
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
      OVERLOADED_THRESHOLD_NANOS = 20L * TimeUtil.NANOSECONDS_PER_SECOND / 20L;
      OVERLOADED_WARNING_INTERVAL_NANOS = 10L * TimeUtil.NANOSECONDS_PER_SECOND;
      STATUS_EXPIRE_TIME_NANOS = 5L * TimeUtil.NANOSECONDS_PER_SECOND;
      PREPARE_LEVELS_DEFAULT_DELAY_NANOS = 10L * TimeUtil.NANOSECONDS_PER_MILLISECOND;
      DEMO_SETTINGS = new LevelSettings("Demo World", GameType.SURVIVAL, false, Difficulty.NORMAL, false, new GameRules(), WorldDataConfiguration.DEFAULT);
      ANONYMOUS_PLAYER_PROFILE = new GameProfile(Util.NIL_UUID, "Anonymous Player");
   }

   private static record ReloadableResources(CloseableResourceManager resourceManager, ReloadableServerResources managers) implements AutoCloseable {
      final CloseableResourceManager resourceManager;
      final ReloadableServerResources managers;

      ReloadableResources(CloseableResourceManager var1, ReloadableServerResources var2) {
         super();
         this.resourceManager = var1;
         this.managers = var2;
      }

      public void close() {
         this.resourceManager.close();
      }

      public CloseableResourceManager resourceManager() {
         return this.resourceManager;
      }

      public ReloadableServerResources managers() {
         return this.managers;
      }
   }

   private static class TimeProfiler {
      final long startNanos;
      final int startTick;

      TimeProfiler(long var1, int var3) {
         super();
         this.startNanos = var1;
         this.startTick = var3;
      }

      ProfileResults stop(final long var1, final int var3) {
         return new ProfileResults() {
            public List<ResultField> getTimes(String var1x) {
               return Collections.emptyList();
            }

            public boolean saveResults(Path var1x) {
               return false;
            }

            public long getStartTimeNano() {
               return TimeProfiler.this.startNanos;
            }

            public int getStartTimeTicks() {
               return TimeProfiler.this.startTick;
            }

            public long getEndTimeNano() {
               return var1;
            }

            public int getEndTimeTicks() {
               return var3;
            }

            public String getProfilerResults() {
               return "";
            }
         };
      }
   }

   public static record ServerResourcePackInfo(UUID id, String url, String hash, boolean isRequired, @Nullable Component prompt) {
      public ServerResourcePackInfo(UUID var1, String var2, String var3, boolean var4, @Nullable Component var5) {
         super();
         this.id = var1;
         this.url = var2;
         this.hash = var3;
         this.isRequired = var4;
         this.prompt = var5;
      }

      public UUID id() {
         return this.id;
      }

      public String url() {
         return this.url;
      }

      public String hash() {
         return this.hash;
      }

      public boolean isRequired() {
         return this.isRequired;
      }

      @Nullable
      public Component prompt() {
         return this.prompt;
      }
   }
}
