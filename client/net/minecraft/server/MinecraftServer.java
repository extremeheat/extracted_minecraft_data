package net.minecraft.server;

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
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
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
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.gametest.framework.GameTestTicker;
import net.minecraft.network.chat.ChatDecorator;
import net.minecraft.network.chat.ChatSender;
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
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.server.network.TextFilter;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.ModCheck;
import net.minecraft.util.Mth;
import net.minecraft.util.NativeModuleLister;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SignatureValidator;
import net.minecraft.util.Unit;
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
import net.minecraft.world.entity.ai.village.VillageSiege;
import net.minecraft.world.entity.npc.CatSpawner;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.ForcedChunksSavedData;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import net.minecraft.world.level.levelgen.WorldGenSettings;
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
import net.minecraft.world.level.storage.loot.ItemModifierManager;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public abstract class MinecraftServer extends ReentrantBlockableEventLoop<TickTask> implements CommandSource, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String VANILLA_BRAND = "vanilla";
   private static final float AVERAGE_TICK_TIME_SMOOTHING = 0.8F;
   private static final int TICK_STATS_SPAN = 100;
   public static final int MS_PER_TICK = 50;
   private static final int OVERLOADED_THRESHOLD = 2000;
   private static final int OVERLOADED_WARNING_INTERVAL = 15000;
   private static final long STATUS_EXPIRE_TIME_NS = 5000000000L;
   private static final int MAX_STATUS_PLAYER_SAMPLE = 12;
   public static final int START_CHUNK_RADIUS = 11;
   private static final int START_TICKING_CHUNK_COUNT = 441;
   private static final int AUTOSAVE_INTERVAL = 6000;
   private static final int MAX_TICK_LATENCY = 3;
   public static final int ABSOLUTE_MAX_WORLD_SIZE = 29999984;
   public static final LevelSettings DEMO_SETTINGS = new LevelSettings(
      "Demo World", GameType.SURVIVAL, false, Difficulty.NORMAL, false, new GameRules(), DataPackConfig.DEFAULT
   );
   private static final long DELAYED_TASKS_TICK_EXTENSION = 50L;
   public static final GameProfile ANONYMOUS_PLAYER_PROFILE = new GameProfile(Util.NIL_UUID, "Anonymous Player");
   protected final LevelStorageSource.LevelStorageAccess storageSource;
   protected final PlayerDataStorage playerDataStorage;
   private final List<Runnable> tickables = Lists.newArrayList();
   private MetricsRecorder metricsRecorder = InactiveMetricsRecorder.INSTANCE;
   private ProfilerFiller profiler = this.metricsRecorder.getProfiler();
   private Consumer<ProfileResults> onMetricsRecordingStopped = var1x -> this.stopRecordingMetrics();
   private Consumer<Path> onMetricsRecordingFinished = var0 -> {
   };
   private boolean willStartRecordingMetrics;
   @Nullable
   private MinecraftServer.TimeProfiler debugCommandProfiler;
   private boolean debugCommandProfilerDelayStart;
   private final ServerConnectionListener connection;
   private final ChunkProgressListenerFactory progressListenerFactory;
   private final ServerStatus status = new ServerStatus();
   private final RandomSource random = RandomSource.create();
   private final DataFixer fixerUpper;
   private String localIp;
   private int port = -1;
   private final RegistryAccess.Frozen registryHolder;
   private final Map<ResourceKey<Level>, ServerLevel> levels = Maps.newLinkedHashMap();
   private PlayerList playerList;
   private volatile boolean running = true;
   private boolean stopped;
   private int tickCount;
   protected final Proxy proxy;
   private boolean onlineMode;
   private boolean preventProxyConnections;
   private boolean pvp;
   private boolean allowFlight;
   @Nullable
   private String motd;
   private int playerIdleTimeout;
   public final long[] tickTimes = new long[100];
   @Nullable
   private KeyPair keyPair;
   @Nullable
   private GameProfile singleplayerProfile;
   private boolean isDemo;
   private volatile boolean isReady;
   private long lastOverloadWarning;
   protected final Services services;
   private long lastServerStatus;
   private final Thread serverThread;
   private long nextTickTime = Util.getMillis();
   private long delayedTasksMaxNextTickTime;
   private boolean mayHaveDelayedTasks;
   private final PackRepository packRepository;
   private final ServerScoreboard scoreboard = new ServerScoreboard(this);
   @Nullable
   private CommandStorage commandStorage;
   private final CustomBossEvents customBossEvents = new CustomBossEvents();
   private final ServerFunctionManager functionManager;
   private final FrameTimer frameTimer = new FrameTimer();
   private boolean enforceWhitelist;
   private float averageTickTime;
   private final Executor executor;
   @Nullable
   private String serverId;
   private MinecraftServer.ReloadableResources resources;
   private final StructureTemplateManager structureTemplateManager;
   protected final WorldData worldData;
   private volatile boolean isSaving;

   public static <S extends MinecraftServer> S spin(Function<Thread, S> var0) {
      AtomicReference var1 = new AtomicReference();
      Thread var2 = new Thread(() -> ((MinecraftServer)var1.get()).runServer(), "Server thread");
      var2.setUncaughtExceptionHandler((var0x, var1x) -> LOGGER.error("Uncaught exception in server thread", var1x));
      if (Runtime.getRuntime().availableProcessors() > 4) {
         var2.setPriority(8);
      }

      MinecraftServer var3 = (MinecraftServer)var0.apply(var2);
      var1.set(var3);
      var2.start();
      return (S)var3;
   }

   public MinecraftServer(
      Thread var1,
      LevelStorageSource.LevelStorageAccess var2,
      PackRepository var3,
      WorldStem var4,
      Proxy var5,
      DataFixer var6,
      Services var7,
      ChunkProgressListenerFactory var8
   ) {
      super("Server");
      this.registryHolder = var4.registryAccess();
      this.worldData = var4.worldData();
      if (!this.worldData.worldGenSettings().dimensions().containsKey(LevelStem.OVERWORLD)) {
         throw new IllegalStateException("Missing Overworld dimension data");
      } else {
         this.proxy = var5;
         this.packRepository = var3;
         this.resources = new MinecraftServer.ReloadableResources(var4.resourceManager(), var4.dataPackResources());
         this.services = var7;
         if (var7.profileCache() != null) {
            var7.profileCache().setExecutor(this);
         }

         this.connection = new ServerConnectionListener(this);
         this.progressListenerFactory = var8;
         this.storageSource = var2;
         this.playerDataStorage = var2.createPlayerStorage();
         this.fixerUpper = var6;
         this.functionManager = new ServerFunctionManager(this, this.resources.managers.getFunctionLibrary());
         this.structureTemplateManager = new StructureTemplateManager(var4.resourceManager(), var2, var6);
         this.serverThread = var1;
         this.executor = Util.backgroundExecutor();
      }
   }

   private void readScoreboard(DimensionDataStorage var1) {
      var1.computeIfAbsent(this.getScoreboard()::createData, this.getScoreboard()::createData, "scoreboard");
   }

   protected abstract boolean initServer() throws IOException;

   protected void loadLevel() {
      if (!JvmProfiler.INSTANCE.isRunning()) {
      }

      boolean var1 = false;
      ProfiledDuration var2 = JvmProfiler.INSTANCE.onWorldLoadedStarted();
      this.worldData.setModdedInfo(this.getServerModName(), this.getModdedStatus().shouldReportAsModified());
      ChunkProgressListener var3 = this.progressListenerFactory.create(11);
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
      WorldGenSettings var3 = this.worldData.worldGenSettings();
      boolean var4 = var3.isDebug();
      long var5 = var3.seed();
      long var7 = BiomeManager.obfuscateSeed(var5);
      ImmutableList var9 = ImmutableList.of(new PhantomSpawner(), new PatrolSpawner(), new CatSpawner(), new VillageSiege(), new WanderingTraderSpawner(var2));
      Registry var10 = var3.dimensions();
      LevelStem var11 = var10.get(LevelStem.OVERWORLD);
      ServerLevel var12 = new ServerLevel(this, this.executor, this.storageSource, var2, Level.OVERWORLD, var11, var1, var4, var7, var9, true);
      this.levels.put(Level.OVERWORLD, var12);
      DimensionDataStorage var13 = var12.getDataStorage();
      this.readScoreboard(var13);
      this.commandStorage = new CommandStorage(var13);
      WorldBorder var14 = var12.getWorldBorder();
      if (!var2.isInitialized()) {
         try {
            setInitialSpawn(var12, var2, var3.generateBonusChest(), var4);
            var2.setInitialized(true);
            if (var4) {
               this.setupDebugLevel(this.worldData);
            }
         } catch (Throwable var22) {
            CrashReport var16 = CrashReport.forThrowable(var22, "Exception initializing level");

            try {
               var12.fillReportDetails(var16);
            } catch (Throwable var21) {
            }

            throw new ReportedException(var16);
         }

         var2.setInitialized(true);
      }

      this.getPlayerList().addWorldborderListener(var12);
      if (this.worldData.getCustomBossEvents() != null) {
         this.getCustomBossEvents().load(this.worldData.getCustomBossEvents());
      }

      for(Entry var23 : var10.entrySet()) {
         ResourceKey var17 = (ResourceKey)var23.getKey();
         if (var17 != LevelStem.OVERWORLD) {
            ResourceKey var18 = ResourceKey.create(Registry.DIMENSION_REGISTRY, var17.location());
            DerivedLevelData var19 = new DerivedLevelData(this.worldData, var2);
            ServerLevel var20 = new ServerLevel(
               this, this.executor, this.storageSource, var19, var18, (LevelStem)var23.getValue(), var1, var4, var7, ImmutableList.of(), false
            );
            var14.addListener(new BorderChangeListener.DelegateBorderChangeListener(var20.getWorldBorder()));
            this.levels.put(var18, var20);
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
         int var14 = 0;
         int var8 = 0;
         int var9 = 0;
         int var10 = -1;
         boolean var11 = true;

         for(int var12 = 0; var12 < Mth.square(11); ++var12) {
            if (var14 >= -5 && var14 <= 5 && var8 >= -5 && var8 <= 5) {
               BlockPos var13 = PlayerRespawnLogic.getSpawnPosInChunk(var0, new ChunkPos(var5.x + var14, var5.z + var8));
               if (var13 != null) {
                  var1.setSpawn(var13, 0.0F);
                  break;
               }
            }

            if (var14 == var8 || var14 < 0 && var14 == -var8 || var14 > 0 && var14 == 1 - var8) {
               int var16 = var9;
               var9 = -var10;
               var10 = var16;
            }

            var14 += var9;
            var8 += var10;
         }

         if (var2) {
            ConfiguredFeature var15 = MiscOverworldFeatures.BONUS_CHEST.value();
            var15.place(var0, var4.getGenerator(), var0.random, new BlockPos(var1.getXSpawn(), var1.getYSpawn(), var1.getZSpawn()));
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
      var4.getLightEngine().setTaskPerBatch(500);
      this.nextTickTime = Util.getMillis();
      var4.addRegionTicket(TicketType.START, new ChunkPos(var3), 11, Unit.INSTANCE);

      while(var4.getTickingGenerated() != 441) {
         this.nextTickTime = Util.getMillis() + 10L;
         this.waitUntilNextTick();
      }

      this.nextTickTime = Util.getMillis() + 10L;
      this.waitUntilNextTick();

      for(ServerLevel var6 : this.levels.values()) {
         ForcedChunksSavedData var7 = var6.getDataStorage().get(ForcedChunksSavedData::load, "chunks");
         if (var7 != null) {
            LongIterator var8 = var7.getChunks().iterator();

            while(var8.hasNext()) {
               long var9 = var8.nextLong();
               ChunkPos var11 = new ChunkPos(var9);
               var6.getChunkSource().updateChunkForced(var11, true);
            }
         }
      }

      this.nextTickTime = Util.getMillis() + 10L;
      this.waitUntilNextTick();
      var1.stop();
      var4.getLightEngine().setTaskPerBatch(5);
      this.updateMobSpawningFlags();
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

      for(ServerLevel var6 : this.getAllLevels()) {
         if (!var1) {
            LOGGER.info("Saving chunks for level '{}'/{}", var6, var6.dimension().location());
         }

         var6.save(null, var2, var6.noSave && !var3);
         var4 = true;
      }

      ServerLevel var9 = this.overworld();
      ServerLevelData var10 = this.worldData.overworldData();
      var10.setWorldBorder(var9.getWorldBorder().createSettings());
      this.worldData.setCustomBossEvents(this.getCustomBossEvents().save());
      this.storageSource.saveDataTag(this.registryAccess(), this.worldData, this.getPlayerList().getSingleplayerData());
      if (var2) {
         for(ServerLevel var8 : this.getAllLevels()) {
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

   @Override
   public void close() {
      this.stopServer();
   }

   public void stopServer() {
      if (this.metricsRecorder.isRecording()) {
         this.cancelRecordingMetrics();
      }

      LOGGER.info("Stopping server");
      if (this.getConnection() != null) {
         this.getConnection().stop();
      }

      this.isSaving = true;
      if (this.playerList != null) {
         LOGGER.info("Saving players");
         this.playerList.saveAll();
         this.playerList.removeAll();
      }

      LOGGER.info("Saving worlds");

      for(ServerLevel var2 : this.getAllLevels()) {
         if (var2 != null) {
            var2.noSave = false;
         }
      }

      while(this.levels.values().stream().anyMatch(var0 -> var0.getChunkSource().chunkMap.hasWork())) {
         this.nextTickTime = Util.getMillis() + 1L;

         for(ServerLevel var8 : this.getAllLevels()) {
            var8.getChunkSource().removeTicketsOnClosing();
            var8.getChunkSource().tick(() -> true, false);
         }

         this.waitUntilNextTick();
      }

      this.saveAllChunks(false, true, false);

      for(ServerLevel var9 : this.getAllLevels()) {
         if (var9 != null) {
            try {
               var9.close();
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

         this.nextTickTime = Util.getMillis();
         this.status.setDescription(Component.literal(this.motd));
         this.status
            .setVersion(new ServerStatus.Version(SharedConstants.getCurrentVersion().getName(), SharedConstants.getCurrentVersion().getProtocolVersion()));
         this.status.setPreviewsChat(this.previewsChat());
         this.updateStatusIcon(this.status);

         while(this.running) {
            long var1 = Util.getMillis() - this.nextTickTime;
            if (var1 > 2000L && this.nextTickTime - this.lastOverloadWarning >= 15000L) {
               long var46 = var1 / 50L;
               LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", var1, var46);
               this.nextTickTime += var46 * 50L;
               this.lastOverloadWarning = this.nextTickTime;
            }

            if (this.debugCommandProfilerDelayStart) {
               this.debugCommandProfilerDelayStart = false;
               this.debugCommandProfiler = new MinecraftServer.TimeProfiler(Util.getNanos(), this.tickCount);
            }

            this.nextTickTime += 50L;
            this.startMetricsRecordingTick();
            this.profiler.push("tick");
            this.tickServer(this::haveTime);
            this.profiler.popPush("nextTickWait");
            this.mayHaveDelayedTasks = true;
            this.delayedTasksMaxNextTickTime = Math.max(Util.getMillis() + 50L, this.nextTickTime);
            this.waitUntilNextTick();
            this.profiler.pop();
            this.endMetricsRecordingTick();
            this.isReady = true;
            JvmProfiler.INSTANCE.onServerTick(this.averageTickTime);
         }
      } catch (Throwable var44) {
         LOGGER.error("Encountered an unexpected exception", var44);
         CrashReport var2 = constructOrExtractCrashReport(var44);
         this.fillSystemReport(var2.getSystemReport());
         File var3 = new File(
            new File(this.getServerDirectory(), "crash-reports"), "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-server.txt"
         );
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
            if (this.services.profileCache() != null) {
               this.services.profileCache().clearExecutor();
            }

            this.onServerExit();
         }
      }
   }

   private static CrashReport constructOrExtractCrashReport(Throwable var0) {
      Object var1 = null;

      for(Throwable var2 = var0; var2 != null; var2 = var2.getCause()) {
         if (var2 instanceof ReportedException var3) {
            var1 = var3;
         }
      }

      CrashReport var4;
      if (var1 != null) {
         var4 = ((ReportedException)var1).getReport();
         if (var1 != var0) {
            var4.addCategory("Wrapped in").setDetailError("Wrapping exception", var0);
         }
      } else {
         var4 = new CrashReport("Exception in server tick loop", var0);
      }

      return var4;
   }

   private boolean haveTime() {
      return this.runningTask() || Util.getMillis() < (this.mayHaveDelayedTasks ? this.delayedTasksMaxNextTickTime : this.nextTickTime);
   }

   protected void waitUntilNextTick() {
      this.runAllTasks();
      this.managedBlock(() -> !this.haveTime());
   }

   protected TickTask wrapRunnable(Runnable var1) {
      return new TickTask(this.tickCount, var1);
   }

   protected boolean shouldRun(TickTask var1) {
      return var1.getTick() + 3 < this.tickCount || this.haveTime();
   }

   @Override
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
            for(ServerLevel var2 : this.getAllLevels()) {
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

   private void updateStatusIcon(ServerStatus var1) {
      Optional var2 = Optional.of(this.getFile("server-icon.png")).filter(File::isFile);
      if (!var2.isPresent()) {
         var2 = this.storageSource.getIconFile().map(Path::toFile).filter(File::isFile);
      }

      var2.ifPresent(var1x -> {
         try {
            BufferedImage var2x = ImageIO.read(var1x);
            Validate.validState(var2x.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
            Validate.validState(var2x.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
            ByteArrayOutputStream var3 = new ByteArrayOutputStream();
            ImageIO.write(var2x, "PNG", var3);
            byte[] var4 = Base64.getEncoder().encode(var3.toByteArray());
            var1.setFavicon("data:image/png;base64," + new String(var4, StandardCharsets.UTF_8));
         } catch (Exception var5) {
            LOGGER.error("Couldn't load server icon", var5);
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

   public void tickServer(BooleanSupplier var1) {
      long var2 = Util.getNanos();
      ++this.tickCount;
      this.tickChildren(var1);
      if (var2 - this.lastServerStatus >= 5000000000L) {
         this.lastServerStatus = var2;
         this.status.setPlayers(new ServerStatus.Players(this.getMaxPlayers(), this.getPlayerCount()));
         if (!this.hidesOnlinePlayers()) {
            GameProfile[] var4 = new GameProfile[Math.min(this.getPlayerCount(), 12)];
            int var5 = Mth.nextInt(this.random, 0, this.getPlayerCount() - var4.length);

            for(int var6 = 0; var6 < var4.length; ++var6) {
               ServerPlayer var7 = this.playerList.getPlayers().get(var5 + var6);
               if (var7.allowsListing()) {
                  var4[var6] = var7.getGameProfile();
               } else {
                  var4[var6] = ANONYMOUS_PLAYER_PROFILE;
               }
            }

            Collections.shuffle(Arrays.asList(var4));
            this.status.getPlayers().setSample(var4);
         }
      }

      if (this.tickCount % 6000 == 0) {
         LOGGER.debug("Autosave started");
         this.profiler.push("save");
         this.saveEverything(true, false, false);
         this.profiler.pop();
         LOGGER.debug("Autosave finished");
      }

      this.profiler.push("tallying");
      long var8 = this.tickTimes[this.tickCount % 100] = Util.getNanos() - var2;
      this.averageTickTime = this.averageTickTime * 0.8F + (float)var8 / 1000000.0F * 0.19999999F;
      long var9 = Util.getNanos();
      this.frameTimer.logFrameDuration(var9 - var2);
      this.profiler.pop();
   }

   public void tickChildren(BooleanSupplier var1) {
      this.profiler.push("commandFunctions");
      this.getFunctions().tick();
      this.profiler.popPush("levels");

      for(ServerLevel var3 : this.getAllLevels()) {
         this.profiler.push(() -> var3 + " " + var3.dimension().location());
         if (this.tickCount % 20 == 0) {
            this.profiler.push("timeSync");
            this.playerList
               .broadcastAll(
                  new ClientboundSetTimePacket(var3.getGameTime(), var3.getDayTime(), var3.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)),
                  var3.dimension()
               );
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
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         GameTestTicker.SINGLETON.tick();
      }

      this.profiler.popPush("server gui refresh");

      for(int var7 = 0; var7 < this.tickables.size(); ++var7) {
         this.tickables.get(var7).run();
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
      return this.levels.get(Level.OVERWORLD);
   }

   @Nullable
   public ServerLevel getLevel(ResourceKey<Level> var1) {
      return this.levels.get(var1);
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
      var1.setDetail("Server Running", () -> Boolean.toString(this.running));
      if (this.playerList != null) {
         var1.setDetail("Player Count", () -> this.playerList.getPlayerCount() + " / " + this.playerList.getMaxPlayers() + "; " + this.playerList.getPlayers());
      }

      var1.setDetail("Data Packs", () -> {
         StringBuilder var1x = new StringBuilder();

         for(Pack var3 : this.packRepository.getSelectedPacks()) {
            if (var1x.length() > 0) {
               var1x.append(", ");
            }

            var1x.append(var3.getId());
            if (!var3.getCompatibility().isCompatible()) {
               var1x.append(" (incompatible)");
            }
         }

         return var1x.toString();
      });
      var1.setDetail("World Generation", () -> this.worldData.worldGenSettingsLifecycle().toString());
      if (this.serverId != null) {
         var1.setDetail("Server Id", () -> this.serverId);
      }

      return this.fillServerSystemReport(var1);
   }

   public abstract SystemReport fillServerSystemReport(SystemReport var1);

   public ModCheck getModdedStatus() {
      return ModCheck.identify("vanilla", this::getServerModName, "Server", MinecraftServer.class);
   }

   @Override
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
      for(ServerLevel var2 : this.getAllLevels()) {
         var2.setSpawnSettings(this.isSpawningMonsters(), this.isSpawningAnimals());
      }
   }

   public void setDifficultyLocked(boolean var1) {
      this.worldData.setDifficultyLocked(var1);
      this.getPlayerList().getPlayers().forEach(this::sendDifficultyUpdate);
   }

   private void sendDifficultyUpdate(ServerPlayer var1) {
      LevelData var2 = var1.getLevel().getLevelData();
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

   public Optional<MinecraftServer.ServerResourcePackInfo> getServerResourcePack() {
      return Optional.empty();
   }

   public boolean isResourcePackRequired() {
      return this.getServerResourcePack().filter(MinecraftServer.ServerResourcePackInfo::isRequired).isPresent();
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

   public boolean previewsChat() {
      return false;
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

   public SignatureValidator getServiceSignatureValidator() {
      return this.services.serviceSignatureValidator();
   }

   public GameProfileRepository getProfileRepository() {
      return this.services.profileRepository();
   }

   public GameProfileCache getProfileCache() {
      return this.services.profileCache();
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

   @Override
   public boolean scheduleExecutables() {
      return super.scheduleExecutables() && !this.isStopped();
   }

   @Override
   public void executeIfPossible(Runnable var1) {
      if (this.isStopped()) {
         throw new RejectedExecutionException("Server already shutting down");
      } else {
         super.executeIfPossible(var1);
      }
   }

   @Override
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
      return this.nextTickTime;
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
      RegistryAccess.Frozen var2 = this.registryAccess();
      CompletableFuture var3 = CompletableFuture.supplyAsync(
            () -> var1.stream().map(this.packRepository::getPack).filter(Objects::nonNull).map(Pack::open).collect(ImmutableList.toImmutableList()), this
         )
         .thenCompose(
            var2x -> {
               MultiPackResourceManager var3x = new MultiPackResourceManager(PackType.SERVER_DATA, var2x);
               return ReloadableServerResources.loadResources(
                     var3x,
                     var2,
                     this.isDedicatedServer() ? Commands.CommandSelection.DEDICATED : Commands.CommandSelection.INTEGRATED,
                     this.getFunctionCompilationLevel(),
                     this.executor,
                     this
                  )
                  .whenComplete((var1xx, var2xx) -> {
                     if (var2xx != null) {
                        var3x.close();
                     }
                  })
                  .thenApply(var1xx -> new MinecraftServer.ReloadableResources(var3x, var1xx));
            }
         )
         .thenAcceptAsync(var2x -> {
            this.resources.close();
            this.resources = var2x;
            this.packRepository.setSelected(var1);
            this.worldData.setDataPackConfig(getSelectedPacks(this.packRepository));
            this.resources.managers.updateRegistryTags(this.registryAccess());
            this.getPlayerList().saveAll();
            this.getPlayerList().reloadResources();
            this.functionManager.replaceLibrary(this.resources.managers.getFunctionLibrary());
            this.structureTemplateManager.onResourceManagerReload(this.resources.resourceManager);
         }, this);
      if (this.isSameThread()) {
         this.managedBlock(var3::isDone);
      }

      return var3;
   }

   public static DataPackConfig configurePackRepository(PackRepository var0, DataPackConfig var1, boolean var2) {
      var0.reload();
      if (var2) {
         var0.setSelected(Collections.singleton("vanilla"));
         return DataPackConfig.DEFAULT;
      } else {
         LinkedHashSet var3 = Sets.newLinkedHashSet();

         for(String var5 : var1.getEnabled()) {
            if (var0.isAvailable(var5)) {
               var3.add(var5);
            } else {
               LOGGER.warn("Missing data pack {}", var5);
            }
         }

         for(Pack var8 : var0.getAvailablePacks()) {
            String var6 = var8.getId();
            if (!var1.getDisabled().contains(var6) && !var3.contains(var6)) {
               LOGGER.info("Found new data pack {}, loading it automatically", var6);
               var3.add(var6);
            }
         }

         if (var3.isEmpty()) {
            LOGGER.info("No datapacks selected, forcing vanilla");
            var3.add("vanilla");
         }

         var0.setSelected(var3);
         return getSelectedPacks(var0);
      }
   }

   private static DataPackConfig getSelectedPacks(PackRepository var0) {
      Collection var1 = var0.getSelectedIds();
      ImmutableList var2 = ImmutableList.copyOf(var1);
      List var3 = var0.getAvailableIds().stream().filter(var1x -> !var1.contains(var1x)).collect(ImmutableList.toImmutableList());
      return new DataPackConfig(var2, var3);
   }

   public void kickUnlistedPlayers(CommandSourceStack var1) {
      if (this.isEnforceWhitelist()) {
         PlayerList var2 = var1.getServer().getPlayerList();
         UserWhiteList var3 = var2.getWhiteList();

         for(ServerPlayer var6 : Lists.newArrayList(var2.getPlayers())) {
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
      return new CommandSourceStack(
         this,
         var1 == null ? Vec3.ZERO : Vec3.atLowerCornerOf(var1.getSharedSpawnPos()),
         Vec2.ZERO,
         var1,
         4,
         "Server",
         Component.literal("Server"),
         this,
         null
      );
   }

   @Override
   public boolean acceptsSuccess() {
      return true;
   }

   @Override
   public boolean acceptsFailure() {
      return true;
   }

   @Override
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

   public LootTables getLootTables() {
      return this.resources.managers.getLootTables();
   }

   public PredicateManager getPredicateManager() {
      return this.resources.managers.getPredicateManager();
   }

   public ItemModifierManager getItemModifierManager() {
      return this.resources.managers.getItemModifierManager();
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

   public float getAverageTickTime() {
      return this.averageTickTime;
   }

   public int getProfilePermissions(GameProfile var1) {
      if (this.getPlayerList().isOp(var1)) {
         ServerOpListEntry var2 = this.getPlayerList().getOps().get(var1);
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

   public ProfilerFiller getProfiler() {
      return this.profiler;
   }

   public abstract boolean isSingleplayerOwner(GameProfile var1);

   public void dumpServerProperties(Path var1) throws IOException {
   }

   private void saveDebugReport(Path var1) {
      Path var2 = var1.resolve("levels");

      try {
         for(Entry var4 : this.levels.entrySet()) {
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
      try (BufferedWriter var2 = Files.newBufferedWriter(var1)) {
         var2.write(String.format("pending_tasks: %d\n", this.getPendingTasksCount()));
         var2.write(String.format("average_tick_time: %f\n", this.getAverageTickTime()));
         var2.write(String.format("tick_times: %s\n", Arrays.toString(this.tickTimes)));
         var2.write(String.format("queue: %s\n", Util.backgroundExecutor()));
      }
   }

   private void dumpGameRules(Path var1) throws IOException {
      try (BufferedWriter var2 = Files.newBufferedWriter(var1)) {
         final ArrayList var3 = Lists.newArrayList();
         final GameRules var4 = this.getGameRules();
         GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
            @Override
            public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> var1, GameRules.Type<T> var2) {
               var3.add(String.format("%s=%s\n", var1.getId(), var4.getRule(var1)));
            }
         });

         for(String var6 : var3) {
            var2.write(var6);
         }
      }
   }

   private void dumpClasspath(Path var1) throws IOException {
      try (BufferedWriter var2 = Files.newBufferedWriter(var1)) {
         String var3 = System.getProperty("java.class.path");
         String var4 = System.getProperty("path.separator");

         for(String var6 : Splitter.on(var4).split(var3)) {
            var2.write(var6);
            var2.write("\n");
         }
      }
   }

   private void dumpThreads(Path var1) throws IOException {
      ThreadMXBean var2 = ManagementFactory.getThreadMXBean();
      ThreadInfo[] var3 = var2.dumpAllThreads(true, true);
      Arrays.sort(var3, Comparator.comparing(ThreadInfo::getThreadName));

      try (BufferedWriter var4 = Files.newBufferedWriter(var1)) {
         for(ThreadInfo var8 : var3) {
            var4.write(var8.toString());
            var4.write(10);
         }
      }
   }

   private void dumpNativeModules(Path var1) throws IOException {
      try (BufferedWriter var2 = Files.newBufferedWriter(var1)) {
         ArrayList var3;
         try {
            var3 = Lists.newArrayList(NativeModuleLister.listModules());
         } catch (Throwable var7) {
            LOGGER.warn("Failed to list native modules", var7);
            return;
         }

         var3.sort(Comparator.comparing(var0 -> var0.name));

         for(NativeModuleLister.NativeModuleInfo var5 : var3) {
            var2.write(var5.toString());
            var2.write(10);
         }
      }
   }

   private void startMetricsRecordingTick() {
      if (this.willStartRecordingMetrics) {
         this.metricsRecorder = ActiveMetricsRecorder.createStarted(
            new ServerMetricsSamplersProvider(Util.timeSource, this.isDedicatedServer()),
            Util.timeSource,
            Util.ioPool(),
            new MetricsPersister("server"),
            this.onMetricsRecordingStopped,
            var1 -> {
               this.executeBlocking(() -> this.saveDebugReport(var1.resolve("server")));
               this.onMetricsRecordingFinished.accept(var1);
            }
         );
         this.willStartRecordingMetrics = false;
      }

      this.profiler = SingleTickProfiler.decorateFiller(this.metricsRecorder.getProfiler(), SingleTickProfiler.createTickProfiler("Server"));
      this.metricsRecorder.startTick();
      this.profiler.startTick();
   }

   private void endMetricsRecordingTick() {
      this.profiler.endTick();
      this.metricsRecorder.endTick();
   }

   public boolean isRecordingMetrics() {
      return this.metricsRecorder.isRecording();
   }

   public void startRecordingMetrics(Consumer<ProfileResults> var1, Consumer<Path> var2) {
      this.onMetricsRecordingStopped = var2x -> {
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
      return this.registryHolder;
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

   public void logMessageFrom(ChatSender var1, Component var2) {
      LOGGER.info(Component.translatable("chat.type.text", var1.name(), var2).getString());
   }

   public ChatDecorator getChatDecorator() {
      return ChatDecorator.PLAIN;
   }

   static record ReloadableResources(CloseableResourceManager a, ReloadableServerResources b) implements AutoCloseable {
      final CloseableResourceManager resourceManager;
      final ReloadableServerResources managers;

      ReloadableResources(CloseableResourceManager var1, ReloadableServerResources var2) {
         super();
         this.resourceManager = var1;
         this.managers = var2;
      }

      @Override
      public void close() {
         this.resourceManager.close();
      }
   }

   public static record ServerResourcePackInfo(String a, String b, boolean c, @Nullable Component d) {
      private final String url;
      private final String hash;
      private final boolean isRequired;
      @Nullable
      private final Component prompt;

      public ServerResourcePackInfo(String var1, String var2, boolean var3, @Nullable Component var4) {
         super();
         this.url = var1;
         this.hash = var2;
         this.isRequired = var3;
         this.prompt = var4;
      }
   }

   static class TimeProfiler {
      final long startNanos;
      final int startTick;

      TimeProfiler(long var1, int var3) {
         super();
         this.startNanos = var1;
         this.startTick = var3;
      }

      ProfileResults stop(final long var1, final int var3) {
         return new ProfileResults() {
            @Override
            public List<ResultField> getTimes(String var1x) {
               return Collections.emptyList();
            }

            @Override
            public boolean saveResults(Path var1x) {
               return false;
            }

            @Override
            public long getStartTimeNano() {
               return TimeProfiler.this.startNanos;
            }

            @Override
            public int getStartTimeTicks() {
               return TimeProfiler.this.startTick;
            }

            @Override
            public long getEndTimeNano() {
               return var1;
            }

            @Override
            public int getEndTimeTicks() {
               return var3;
            }

            @Override
            public String getProfilerResults() {
               return "";
            }
         };
      }
   }
}
