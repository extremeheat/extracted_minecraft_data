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
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.LongIterator;
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
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.worldgen.Features;
import net.minecraft.gametest.framework.GameTestTicker;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.server.level.PlayerRespawnLogic;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.server.network.TextFilter;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagContainer;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.Mth;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ContinuousProfiler;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.SingleTickProfiler;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Snooper;
import net.minecraft.world.SnooperPopulator;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.saveddata.SaveDataDirtyRunnable;
import net.minecraft.world.level.storage.CommandStorage;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.ScoreboardSaveData;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinecraftServer extends ReentrantBlockableEventLoop<TickTask> implements SnooperPopulator, CommandSource, AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final File USERID_CACHE_FILE = new File("usercache.json");
   public static final LevelSettings DEMO_SETTINGS;
   protected final LevelStorageSource.LevelStorageAccess storageSource;
   protected final PlayerDataStorage playerDataStorage;
   private final Snooper snooper = new Snooper("server", this, Util.getMillis());
   private final List<Runnable> tickables = Lists.newArrayList();
   private final ContinuousProfiler continousProfiler;
   private ProfilerFiller profiler;
   private final ServerConnectionListener connection;
   private final ChunkProgressListenerFactory progressListenerFactory;
   private final ServerStatus status;
   private final Random random;
   private final DataFixer fixerUpper;
   private String localIp;
   private int port;
   protected final RegistryAccess.RegistryHolder registryHolder;
   private final Map<ResourceKey<Level>, ServerLevel> levels;
   private PlayerList playerList;
   private volatile boolean running;
   private boolean stopped;
   private int tickCount;
   protected final Proxy proxy;
   private boolean onlineMode;
   private boolean preventProxyConnections;
   private boolean pvp;
   private boolean allowFlight;
   @Nullable
   private String motd;
   private int maxBuildHeight;
   private int playerIdleTimeout;
   public final long[] tickTimes;
   @Nullable
   private KeyPair keyPair;
   @Nullable
   private String singleplayerName;
   private boolean isDemo;
   private String resourcePack;
   private String resourcePackHash;
   private volatile boolean isReady;
   private long lastOverloadWarning;
   private boolean delayProfilerStart;
   private boolean forceGameType;
   private final MinecraftSessionService sessionService;
   private final GameProfileRepository profileRepository;
   private final GameProfileCache profileCache;
   private long lastServerStatus;
   private final Thread serverThread;
   private long nextTickTime;
   private long delayedTasksMaxNextTickTime;
   private boolean mayHaveDelayedTasks;
   private boolean hasWorldScreenshot;
   private final PackRepository packRepository;
   private final ServerScoreboard scoreboard;
   @Nullable
   private CommandStorage commandStorage;
   private final CustomBossEvents customBossEvents;
   private final ServerFunctionManager functionManager;
   private final FrameTimer frameTimer;
   private boolean enforceWhitelist;
   private float averageTickTime;
   private final Executor executor;
   @Nullable
   private String serverId;
   private ServerResources resources;
   private final StructureManager structureManager;
   protected final WorldData worldData;

   public static <S extends MinecraftServer> S spin(Function<Thread, S> var0) {
      AtomicReference var1 = new AtomicReference();
      Thread var2 = new Thread(() -> {
         ((MinecraftServer)var1.get()).runServer();
      }, "Server thread");
      var2.setUncaughtExceptionHandler((var0x, var1x) -> {
         LOGGER.error(var1x);
      });
      MinecraftServer var3 = (MinecraftServer)var0.apply(var2);
      var1.set(var3);
      var2.start();
      return var3;
   }

   public MinecraftServer(Thread var1, RegistryAccess.RegistryHolder var2, LevelStorageSource.LevelStorageAccess var3, WorldData var4, PackRepository var5, Proxy var6, DataFixer var7, ServerResources var8, MinecraftSessionService var9, GameProfileRepository var10, GameProfileCache var11, ChunkProgressListenerFactory var12) {
      super("Server");
      this.continousProfiler = new ContinuousProfiler(Util.timeSource, this::getTickCount);
      this.profiler = InactiveProfiler.INSTANCE;
      this.status = new ServerStatus();
      this.random = new Random();
      this.port = -1;
      this.levels = Maps.newLinkedHashMap();
      this.running = true;
      this.tickTimes = new long[100];
      this.resourcePack = "";
      this.resourcePackHash = "";
      this.nextTickTime = Util.getMillis();
      this.scoreboard = new ServerScoreboard(this);
      this.customBossEvents = new CustomBossEvents();
      this.frameTimer = new FrameTimer();
      this.registryHolder = var2;
      this.worldData = var4;
      this.proxy = var6;
      this.packRepository = var5;
      this.resources = var8;
      this.sessionService = var9;
      this.profileRepository = var10;
      this.profileCache = var11;
      this.connection = new ServerConnectionListener(this);
      this.progressListenerFactory = var12;
      this.storageSource = var3;
      this.playerDataStorage = var3.createPlayerStorage();
      this.fixerUpper = var7;
      this.functionManager = new ServerFunctionManager(this, var8.getFunctionLibrary());
      this.structureManager = new StructureManager(var8.getResourceManager(), var3, var7);
      this.serverThread = var1;
      this.executor = Util.backgroundExecutor();
   }

   private void readScoreboard(DimensionDataStorage var1) {
      ScoreboardSaveData var2 = (ScoreboardSaveData)var1.computeIfAbsent(ScoreboardSaveData::new, "scoreboard");
      var2.setScoreboard(this.getScoreboard());
      this.getScoreboard().addDirtyListener(new SaveDataDirtyRunnable(var2));
   }

   protected abstract boolean initServer() throws IOException;

   public static void convertFromRegionFormatIfNeeded(LevelStorageSource.LevelStorageAccess var0) {
      if (var0.requiresConversion()) {
         LOGGER.info("Converting map!");
         var0.convertLevel(new ProgressListener() {
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

   }

   protected void loadLevel() {
      this.detectBundledResources();
      this.worldData.setModdedInfo(this.getServerModName(), this.getModdedStatus().isPresent());
      ChunkProgressListener var1 = this.progressListenerFactory.create(11);
      this.createLevels(var1);
      this.forceDifficulty();
      this.prepareLevels(var1);
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
      MappedRegistry var10 = var3.dimensions();
      LevelStem var12 = (LevelStem)var10.get(LevelStem.OVERWORLD);
      Object var11;
      DimensionType var13;
      if (var12 == null) {
         var13 = (DimensionType)this.registryHolder.dimensionTypes().getOrThrow(DimensionType.OVERWORLD_LOCATION);
         var11 = WorldGenSettings.makeDefaultOverworld(this.registryHolder.registryOrThrow(Registry.BIOME_REGISTRY), this.registryHolder.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY), (new Random()).nextLong());
      } else {
         var13 = var12.type();
         var11 = var12.generator();
      }

      ServerLevel var14 = new ServerLevel(this, this.executor, this.storageSource, var2, Level.OVERWORLD, var13, var1, (ChunkGenerator)var11, var4, var7, var9, true);
      this.levels.put(Level.OVERWORLD, var14);
      DimensionDataStorage var15 = var14.getDataStorage();
      this.readScoreboard(var15);
      this.commandStorage = new CommandStorage(var15);
      WorldBorder var16 = var14.getWorldBorder();
      var16.applySettings(var2.getWorldBorder());
      if (!var2.isInitialized()) {
         try {
            setInitialSpawn(var14, var2, var3.generateBonusChest(), var4, true);
            var2.setInitialized(true);
            if (var4) {
               this.setupDebugLevel(this.worldData);
            }
         } catch (Throwable var26) {
            CrashReport var18 = CrashReport.forThrowable(var26, "Exception initializing level");

            try {
               var14.fillReportDetails(var18);
            } catch (Throwable var25) {
            }

            throw new ReportedException(var18);
         }

         var2.setInitialized(true);
      }

      this.getPlayerList().setLevel(var14);
      if (this.worldData.getCustomBossEvents() != null) {
         this.getCustomBossEvents().load(this.worldData.getCustomBossEvents());
      }

      Iterator var17 = var10.entrySet().iterator();

      while(var17.hasNext()) {
         Entry var27 = (Entry)var17.next();
         ResourceKey var19 = (ResourceKey)var27.getKey();
         if (var19 != LevelStem.OVERWORLD) {
            ResourceKey var20 = ResourceKey.create(Registry.DIMENSION_REGISTRY, var19.location());
            DimensionType var21 = ((LevelStem)var27.getValue()).type();
            ChunkGenerator var22 = ((LevelStem)var27.getValue()).generator();
            DerivedLevelData var23 = new DerivedLevelData(this.worldData, var2);
            ServerLevel var24 = new ServerLevel(this, this.executor, this.storageSource, var23, var20, var21, var1, var22, var4, var7, ImmutableList.of(), false);
            var16.addListener(new BorderChangeListener.DelegateBorderChangeListener(var24.getWorldBorder()));
            this.levels.put(var20, var24);
         }
      }

   }

   private static void setInitialSpawn(ServerLevel var0, ServerLevelData var1, boolean var2, boolean var3, boolean var4) {
      ChunkGenerator var5 = var0.getChunkSource().getGenerator();
      if (!var4) {
         var1.setSpawn(BlockPos.ZERO.above(var5.getSpawnHeight()), 0.0F);
      } else if (var3) {
         var1.setSpawn(BlockPos.ZERO.above(), 0.0F);
      } else {
         BiomeSource var6 = var5.getBiomeSource();
         Random var7 = new Random(var0.getSeed());
         BlockPos var8 = var6.findBiomeHorizontal(0, var0.getSeaLevel(), 0, 256, (var0x) -> {
            return var0x.getMobSettings().playerSpawnFriendly();
         }, var7);
         ChunkPos var9 = var8 == null ? new ChunkPos(0, 0) : new ChunkPos(var8);
         if (var8 == null) {
            LOGGER.warn("Unable to find spawn biome");
         }

         boolean var10 = false;
         Iterator var11 = BlockTags.VALID_SPAWN.getValues().iterator();

         while(var11.hasNext()) {
            Block var12 = (Block)var11.next();
            if (var6.getSurfaceBlocks().contains(var12.defaultBlockState())) {
               var10 = true;
               break;
            }
         }

         var1.setSpawn(var9.getWorldPosition().offset(8, var5.getSpawnHeight(), 8), 0.0F);
         int var18 = 0;
         int var19 = 0;
         int var13 = 0;
         int var14 = -1;
         boolean var15 = true;

         for(int var16 = 0; var16 < 1024; ++var16) {
            if (var18 > -16 && var18 <= 16 && var19 > -16 && var19 <= 16) {
               BlockPos var17 = PlayerRespawnLogic.getSpawnPosInChunk(var0, new ChunkPos(var9.x + var18, var9.z + var19), var10);
               if (var17 != null) {
                  var1.setSpawn(var17, 0.0F);
                  break;
               }
            }

            if (var18 == var19 || var18 < 0 && var18 == -var19 || var18 > 0 && var18 == 1 - var19) {
               int var21 = var13;
               var13 = -var14;
               var14 = var21;
            }

            var18 += var13;
            var19 += var14;
         }

         if (var2) {
            ConfiguredFeature var20 = Features.BONUS_CHEST;
            var20.place(var0, var5, var0.random, new BlockPos(var1.getXSpawn(), var1.getYSpawn(), var1.getZSpawn()));
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
      Iterator var5 = this.levels.values().iterator();

      while(true) {
         ServerLevel var6;
         ForcedChunksSavedData var7;
         do {
            if (!var5.hasNext()) {
               this.nextTickTime = Util.getMillis() + 10L;
               this.waitUntilNextTick();
               var1.stop();
               var4.getLightEngine().setTaskPerBatch(5);
               this.updateMobSpawningFlags();
               return;
            }

            var6 = (ServerLevel)var5.next();
            var7 = (ForcedChunksSavedData)var6.getDataStorage().get(ForcedChunksSavedData::new, "chunks");
         } while(var7 == null);

         LongIterator var8 = var7.getChunks().iterator();

         while(var8.hasNext()) {
            long var9 = var8.nextLong();
            ChunkPos var11 = new ChunkPos(var9);
            var6.getChunkSource().updateChunkForced(var11, true);
         }
      }
   }

   protected void detectBundledResources() {
      File var1 = this.storageSource.getLevelPath(LevelResource.MAP_RESOURCE_FILE).toFile();
      if (var1.isFile()) {
         String var2 = this.storageSource.getLevelId();

         try {
            this.setResourcePack("level://" + URLEncoder.encode(var2, StandardCharsets.UTF_8.toString()) + "/" + "resources.zip", "");
         } catch (UnsupportedEncodingException var4) {
            LOGGER.warn("Something went wrong url encoding {}", var2);
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

      ServerLevel var7 = this.overworld();
      ServerLevelData var8 = this.worldData.overworldData();
      var8.setWorldBorder(var7.getWorldBorder().createSettings());
      this.worldData.setCustomBossEvents(this.getCustomBossEvents().save());
      this.storageSource.saveDataTag(this.registryHolder, this.worldData, this.getPlayerList().getSingleplayerData());
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
            } catch (IOException var5) {
               LOGGER.error("Exception closing the level", var5);
            }
         }
      }

      if (this.snooper.isStarted()) {
         this.snooper.interrupt();
      }

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
               SingleTickProfiler var47 = SingleTickProfiler.createTickProfiler("Server");
               this.startProfilerTick(var47);
               this.profiler.startTick();
               this.profiler.push("tick");
               this.tickServer(this::haveTime);
               this.profiler.popPush("nextTickWait");
               this.mayHaveDelayedTasks = true;
               this.delayedTasksMaxNextTickTime = Math.max(Util.getMillis() + 50L, this.nextTickTime);
               this.waitUntilNextTick();
               this.profiler.pop();
               this.profiler.endTick();
               this.endProfilerTick(var47);
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

   protected void doRunTask(TickTask var1) {
      this.getProfiler().incrementCounter("runTask");
      super.doRunTask(var1);
   }

   private void updateStatusIcon(ServerStatus var1) {
      File var2 = this.getFile("server-icon.png");
      if (!var2.exists()) {
         var2 = this.storageSource.getIconFile();
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
      return this.storageSource.getIconFile();
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

      while(var2.hasNext()) {
         ServerLevel var3 = (ServerLevel)var2.next();
         this.profiler.push(() -> {
            return var3 + " " + var3.dimension().location();
         });
         if (this.tickCount % 20 == 0) {
            this.profiler.push("timeSync");
            this.playerList.broadcastAll(new ClientboundSetTimePacket(var3.getGameTime(), var3.getDayTime(), var3.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)), var3.dimension());
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
         GameTestTicker.singleton.tick();
      }

      this.profiler.popPush("server gui refresh");

      for(int var7 = 0; var7 < this.tickables.size(); ++var7) {
         ((Runnable)this.tickables.get(var7)).run();
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
         Iterator var2 = this.packRepository.getSelectedPacks().iterator();

         while(var2.hasNext()) {
            Pack var3 = (Pack)var2.next();
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

   public abstract Optional<String> getModdedStatus();

   public void sendMessage(Component var1, UUID var2) {
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
      LevelData var2 = var1.getLevel().getLevelData();
      var1.connection.send(new ClientboundChangeDifficultyPacket(var2.getDifficulty(), var2.isDifficultyLocked()));
   }

   protected boolean isSpawningMonsters() {
      return this.worldData.getDifficulty() != Difficulty.PEACEFUL;
   }

   public boolean isDemo() {
      return this.isDemo;
   }

   public void setDemo(boolean var1) {
      this.isDemo = var1;
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
         var1.setDynamicData("players_seen", this.playerDataStorage.getSeenPlayers().length);
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
            var1.setDynamicData("world[" + var2 + "][dimension]", var4.dimension().location());
            var1.setDynamicData("world[" + var2 + "][mode]", this.worldData.getGameType());
            var1.setDynamicData("world[" + var2 + "][difficulty]", var4.getDifficulty());
            var1.setDynamicData("world[" + var2 + "][hardcore]", this.worldData.isHardcore());
            var1.setDynamicData("world[" + var2 + "][height]", this.maxBuildHeight);
            var1.setDynamicData("world[" + var2 + "][chunks_loaded]", var4.getChunkSource().getLoadedChunksCount());
            ++var2;
         }
      }

      var1.setDynamicData("worlds", var2);
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

   public abstract boolean publishServer(GameType var1, boolean var2, int var3);

   public int getTickCount() {
      return this.tickCount;
   }

   public Snooper getSnooper() {
      return this.snooper;
   }

   public int getSpawnProtectionRadius() {
      return 16;
   }

   public boolean isUnderSpawnProtection(ServerLevel var1, BlockPos var2, Player var3) {
      return false;
   }

   public void setForceGameType(boolean var1) {
      this.forceGameType = var1;
   }

   public boolean getForceGameType() {
      return this.forceGameType;
   }

   public boolean repliesToStatus() {
      return true;
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
      return this.resources.getAdvancements();
   }

   public ServerFunctionManager getFunctions() {
      return this.functionManager;
   }

   public CompletableFuture<Void> reloadResources(Collection<String> var1) {
      CompletableFuture var2 = CompletableFuture.supplyAsync(() -> {
         Stream var10000 = var1.stream();
         PackRepository var10001 = this.packRepository;
         var10001.getClass();
         return (ImmutableList)var10000.map(var10001::getPack).filter(Objects::nonNull).map(Pack::open).collect(ImmutableList.toImmutableList());
      }, this).thenCompose((var1x) -> {
         return ServerResources.loadResources(var1x, this.isDedicatedServer() ? Commands.CommandSelection.DEDICATED : Commands.CommandSelection.INTEGRATED, this.getFunctionCompilationLevel(), this.executor, this);
      }).thenAcceptAsync((var2x) -> {
         this.resources.close();
         this.resources = var2x;
         this.packRepository.setSelected(var1);
         this.worldData.setDataPackConfig(getSelectedPacks(this.packRepository));
         var2x.updateGlobals();
         this.getPlayerList().saveAll();
         this.getPlayerList().reloadResources();
         this.functionManager.replaceLibrary(this.resources.getFunctionLibrary());
         this.structureManager.onResourceManagerReload(this.resources.getResourceManager());
      }, this);
      if (this.isSameThread()) {
         this.managedBlock(var2::isDone);
      }

      return var2;
   }

   public static DataPackConfig configurePackRepository(PackRepository var0, DataPackConfig var1, boolean var2) {
      var0.reload();
      if (var2) {
         var0.setSelected(Collections.singleton("vanilla"));
         return new DataPackConfig(ImmutableList.of("vanilla"), ImmutableList.of());
      } else {
         LinkedHashSet var3 = Sets.newLinkedHashSet();
         Iterator var4 = var1.getEnabled().iterator();

         while(var4.hasNext()) {
            String var5 = (String)var4.next();
            if (var0.isAvailable(var5)) {
               var3.add(var5);
            } else {
               LOGGER.warn("Missing data pack {}", var5);
            }
         }

         var4 = var0.getAvailablePacks().iterator();

         while(var4.hasNext()) {
            Pack var7 = (Pack)var4.next();
            String var6 = var7.getId();
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
               var6.connection.disconnect(new TranslatableComponent("multiplayer.disconnect.not_whitelisted"));
            }
         }

      }
   }

   public PackRepository getPackRepository() {
      return this.packRepository;
   }

   public Commands getCommands() {
      return this.resources.getCommands();
   }

   public CommandSourceStack createCommandSourceStack() {
      ServerLevel var1 = this.overworld();
      return new CommandSourceStack(this, var1 == null ? Vec3.ZERO : Vec3.atLowerCornerOf(var1.getSharedSpawnPos()), Vec2.ZERO, var1, 4, "Server", new TextComponent("Server"), this, (Entity)null);
   }

   public boolean acceptsSuccess() {
      return true;
   }

   public boolean acceptsFailure() {
      return true;
   }

   public RecipeManager getRecipeManager() {
      return this.resources.getRecipeManager();
   }

   public TagContainer getTags() {
      return this.resources.getTags();
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
      return this.resources.getLootTables();
   }

   public PredicateManager getPredicateManager() {
      return this.resources.getPredicateManager();
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

   public ProfilerFiller getProfiler() {
      return this.profiler;
   }

   public abstract boolean isSingleplayerOwner(GameProfile var1);

   public void saveDebugReport(Path var1) throws IOException {
      Path var2 = var1.resolve("levels");
      Iterator var3 = this.levels.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         ResourceLocation var5 = ((ResourceKey)var4.getKey()).location();
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

   private void startProfilerTick(@Nullable SingleTickProfiler var1) {
      if (this.delayProfilerStart) {
         this.delayProfilerStart = false;
         this.continousProfiler.enable();
      }

      this.profiler = SingleTickProfiler.decorateFiller(this.continousProfiler.getFiller(), var1);
   }

   private void endProfilerTick(@Nullable SingleTickProfiler var1) {
      if (var1 != null) {
         var1.endTick();
      }

      this.profiler = this.continousProfiler.getFiller();
   }

   public boolean isProfiling() {
      return this.continousProfiler.isEnabled();
   }

   public void startProfiling() {
      this.delayProfilerStart = true;
   }

   public ProfileResults finishProfiling() {
      ProfileResults var1 = this.continousProfiler.getResults();
      this.continousProfiler.disable();
      return var1;
   }

   public Path getWorldPath(LevelResource var1) {
      return this.storageSource.getLevelPath(var1);
   }

   public boolean forceSynchronousWrites() {
      return true;
   }

   public StructureManager getStructureManager() {
      return this.structureManager;
   }

   public WorldData getWorldData() {
      return this.worldData;
   }

   public RegistryAccess registryAccess() {
      return this.registryHolder;
   }

   @Nullable
   public TextFilter createTextFilterForPlayer(ServerPlayer var1) {
      return null;
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
      DEMO_SETTINGS = new LevelSettings("Demo World", GameType.SURVIVAL, false, Difficulty.NORMAL, false, new GameRules(), DataPackConfig.DEFAULT);
   }
}
