package net.minecraft.server;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import com.mojang.jtracy.DiscontinuousFrame;
import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.Proxy;
import java.nio.file.FileStore;
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
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.FileUtil;
import net.minecraft.ReportType;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.Util;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestTicker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.ChatDecorator;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.server.level.DemoMode;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.server.network.ServerHibernateConfigPacketListenerImpl;
import net.minecraft.server.network.TextFilter;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.util.ModCheck;
import net.minecraft.util.Mth;
import net.minecraft.util.NativeModuleLister;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SignatureValidator;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.debugchart.RemoteDebugSampleType;
import net.minecraft.util.debugchart.SampleLogger;
import net.minecraft.util.debugchart.TpsDebugDimensions;
import net.minecraft.util.profiling.EmptyProfileResults;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.ResultField;
import net.minecraft.util.profiling.SingleTickProfiler;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.profiling.metrics.profiling.ActiveMetricsRecorder;
import net.minecraft.util.profiling.metrics.profiling.InactiveMetricsRecorder;
import net.minecraft.util.profiling.metrics.profiling.MetricsRecorder;
import net.minecraft.util.profiling.metrics.profiling.ServerMetricsSamplersProvider;
import net.minecraft.util.profiling.metrics.storage.MetricsPersister;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.chunk.storage.ChunkIOErrorReporter;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.slf4j.Logger;

public abstract class MinecraftServer extends ReentrantBlockableEventLoop<TickTask> implements ServerInfo, ChunkIOErrorReporter, CommandSource {
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
   private static final int AUTOSAVE_INTERVAL = 6000;
   private static final int MIMINUM_AUTOSAVE_TICKS = 100;
   private static final int MAX_TICK_LATENCY = 3;
   public static final int ABSOLUTE_MAX_WORLD_SIZE = 29999984;
   public static final LevelSettings DEMO_SETTINGS;
   public static final GameProfile ANONYMOUS_PLAYER_PROFILE;
   public static final long PLAYER_RECONFIG_KICK_PERIOD;
   protected final LevelStorageSource.LevelStorageAccess storageSource;
   protected final PlayerDataStorage playerDataStorage;
   private final List<Runnable> tickables = Lists.newArrayList();
   private MetricsRecorder metricsRecorder;
   private Consumer<ProfileResults> onMetricsRecordingStopped;
   private Consumer<Path> onMetricsRecordingFinished;
   private boolean willStartRecordingMetrics;
   @Nullable
   private TimeProfiler debugCommandProfiler;
   private boolean debugCommandProfilerDelayStart;
   private final ServerConnectionListener connection;
   @Nullable
   private ServerStatus status;
   @Nullable
   private ServerStatus.Favicon statusIcon;
   private final RandomSource random;
   private final DataFixer fixerUpper;
   private String localIp;
   private int port;
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
   private boolean waitingForNextTick;
   private long delayedTasksMaxNextTickTimeNanos;
   private boolean mayHaveDelayedTasks;
   private boolean enforceWhitelist;
   private float smoothedTickTimeMillis;
   private final Executor executor;
   @Nullable
   private String serverId;
   private int emptyTicks;
   private volatile boolean isSaving;
   private static final AtomicReference<RuntimeException> fatalException;
   private final SuppressedExceptionCollector suppressedExceptions;
   private final DiscontinuousFrame tickFrame;
   @Nullable
   protected TheGame theGame;
   @Nullable
   public TheGame theGameButSuperSpecialOneForLoading;
   @Nullable
   private CompletableFuture<MinecraftServer> reloadFuture;
   @Nullable
   private CompletableFuture<MinecraftServer> rejoinFuture;

   public static <S extends MinecraftServer> S spin(PackRepository var0, WorldStem var1, WorldReloader var2, ChunkProgressListenerFactory var3, Function<Thread, S> var4) {
      AtomicReference var5 = new AtomicReference();
      Thread var6 = new Thread(() -> ((MinecraftServer)var5.get()).runServer(var0, var1, var2, var3), "Server thread");
      var6.setUncaughtExceptionHandler((var0x, var1x) -> LOGGER.error("Uncaught exception in server thread", var1x));
      if (Runtime.getRuntime().availableProcessors() > 4) {
         var6.setPriority(8);
      }

      MinecraftServer var7 = (MinecraftServer)var4.apply(var6);
      var5.set(var7);
      var6.start();
      return (S)var7;
   }

   public MinecraftServer(Thread var1, LevelStorageSource.LevelStorageAccess var2, Proxy var3, DataFixer var4, Services var5) {
      super("Server");
      this.metricsRecorder = InactiveMetricsRecorder.INSTANCE;
      this.onMetricsRecordingStopped = (var1x) -> this.stopRecordingMetrics();
      this.onMetricsRecordingFinished = (var0) -> {
      };
      this.random = RandomSource.create();
      this.port = -1;
      this.running = true;
      this.ticksUntilAutosave = 6000;
      this.tickTimesNanos = new long[100];
      this.aggregatedTickTimesNanos = 0L;
      this.lastTickNanos = Util.getNanos();
      this.taskExecutionStartNanos = Util.getNanos();
      this.nextTickTimeNanos = Util.getNanos();
      this.waitingForNextTick = false;
      this.suppressedExceptions = new SuppressedExceptionCollector();
      this.proxy = var3;
      this.services = var5;
      if (var5.profileCache() != null) {
         var5.profileCache().setExecutor(this);
      }

      this.connection = new ServerConnectionListener(this);
      this.storageSource = var2;
      this.playerDataStorage = var2.createPlayerStorage();
      this.fixerUpper = var4;
      this.serverThread = var1;
      this.executor = Util.backgroundExecutor();
      this.tickFrame = TracyClient.createDiscontinuousFrame("Server Tick");
   }

   @Nullable
   public TheGame theGame() {
      return this.theGame;
   }

   public Executor taskExecutor() {
      return this.executor;
   }

   protected abstract boolean initServer() throws IOException;

   protected abstract TheGame initGame(PackRepository var1, WorldStem var2, ChunkProgressListenerFactory var3) throws IOException;

   public void forceDifficulty(TheGame var1) {
   }

   public abstract int getOperatorUserPermissionLevel();

   public abstract int getFunctionCompilationLevel();

   public abstract boolean shouldRconBroadcast();

   public void close() {
      this.stopServer(this.theGame());
   }

   public void stopServer(@Nullable TheGame var1) {
      if (this.metricsRecorder.isRecording()) {
         this.cancelRecordingMetrics();
      }

      LOGGER.info("Stopping server");
      this.getConnection().stop();
      this.isSaving = true;
      if (var1 != null) {
         this.stopTheGame(var1);
      }

      try {
         this.storageSource.close();
      } catch (IOException var3) {
         LOGGER.error("Failed to unlock level {}", this.storageSource.getLevelId(), var3);
      }

   }

   private void stopTheGame(TheGame var1) {
      PlayerList var2 = var1.playerList();
      LOGGER.info("Saving players");
      var2.saveAll();
      var2.removeAll();
      LOGGER.info("Saving worlds");
      Collection var3 = var1.getAllLevels();

      for(ServerLevel var5 : var3) {
         if (var5 != null) {
            var5.noSave = false;
         }
      }

      while(var3.stream().anyMatch((var0) -> var0.getChunkSource().chunkMap.hasWork())) {
         this.nextTickTimeNanos = Util.getNanos() + TimeUtil.NANOSECONDS_PER_MILLISECOND;

         for(ServerLevel var7 : var3) {
            var7.getChunkSource().deactivateTicketsOnClosing();
            var7.getChunkSource().tick(() -> true, false);
         }

         this.waitUntilNextTick();
      }

      var1.saveAllChunks(false, true, false);
      this.isSaving = false;
      var1.close();
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

   protected void runServer(PackRepository var1, WorldStem var2, WorldReloader var3, ChunkProgressListenerFactory var4) {
      try {
         if (!this.initServer()) {
            throw new IllegalStateException("Failed to initialize server");
         }

         while(this.running) {
            TheGame var5 = this.initGame(var1, var2, var4);
            this.theGame = var5;
            if (this.reloadFuture != null) {
               this.restoreAllPlayersConfigPlayers(var5);
               this.rejoinFuture = this.reloadFuture;
               this.reloadFuture = null;
            }

            this.innerServerLoopWowo(var5);
            if (this.reloadFuture != null) {
               this.waitForPlayersToEnterConfig();
            }

            this.stopTheGame(var5);
            CompoundTag var48 = var5.playerList().getSingleplayerData();
            this.theGame = null;
            if (this.running) {
               var2 = var3.reload(this.storageSource, var1, var2, var48);
            }
         }
      } catch (Throwable var46) {
         LOGGER.error("Encountered an unexpected exception", var46);
         CrashReport var6 = constructOrExtractCrashReport(var46);
         this.fillSystemReport(var6.getSystemReport());
         Path var7 = this.getServerDirectory().resolve("crash-reports").resolve("crash-" + Util.getFilenameFormattedDateTime() + "-server.txt");
         if (var6.saveToFile(var7, ReportType.CRASH)) {
            LOGGER.error("This crash report has been saved to: {}", var7.toAbsolutePath());
         } else {
            LOGGER.error("We were unable to save this crash report to disk.");
         }

         this.onServerCrash(var6);
      } finally {
         try {
            this.stopped = true;
            this.stopServer(this.theGame());
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

   private void restoreAllPlayersConfigPlayers(TheGame var1) {
      for(Connection var3 : this.connection.getConnections()) {
         ServerConfigurationPacketListenerImpl var5 = var3.getPacketListener();
         if (var5 instanceof ServerHibernateConfigPacketListenerImpl var4) {
            var5 = var4.unfreeze(var1);
            var3.replaceListener(var5);
            var5.returnToWorld();
         } else {
            LOGGER.warn("Found weird listener while restoring players: {}", var3.getPacketListener());
         }
      }

   }

   private void waitForPlayersToEnterConfig() {
      long var1 = Util.getMillis();

      while(this.running) {
         boolean var3 = true;

         for(Connection var5 : this.connection.getConnections()) {
            if (var5.isConnected() && !(var5.getPacketListener() instanceof ServerHibernateConfigPacketListenerImpl)) {
               var3 = false;
               break;
            }
         }

         if (var3) {
            break;
         }

         long var8 = Util.getMillis();
         if (var8 - var1 > PLAYER_RECONFIG_KICK_PERIOD) {
            for(Connection var7 : this.connection.getConnections()) {
               if (var7.isConnected() && !var7.isMemoryConnection() && !(var7.getPacketListener() instanceof ServerHibernateConfigPacketListenerImpl)) {
                  LOGGER.warn("Kicking player {} due to slow reconfig", var7.getLoggableAddress(this.logIPs()));
                  var7.disconnect((Component)Component.translatable("player.kick.too_long"));
               }
            }
         }
      }

   }

   private void innerServerLoopWowo(TheGame var1) {
      this.nextTickTimeNanos = Util.getNanos();
      this.statusIcon = (ServerStatus.Favicon)this.loadStatusIcon().orElse((Object)null);
      this.status = this.buildServerStatus(var1);
      ServerTickRateManager var2 = var1.tickRateManager();

      while(this.running && this.reloadFuture == null) {
         if (this.rejoinFuture != null && this.connection.getConnections().stream().allMatch((var0) -> var0.getPacketListener() != null && var0.getPacketListener().protocol() == ConnectionProtocol.PLAY)) {
            this.rejoinFuture.complete(this);
            this.rejoinFuture = null;
         }

         long var3;
         if (!this.isPaused() && var2.isSprinting() && var2.checkShouldSprintThisTick()) {
            var3 = 0L;
            this.nextTickTimeNanos = Util.getNanos();
            this.lastOverloadWarningNanos = this.nextTickTimeNanos;
         } else {
            var3 = var2.nanosecondsPerTick();
            long var5 = Util.getNanos() - this.nextTickTimeNanos;
            if (var5 > OVERLOADED_THRESHOLD_NANOS + 20L * var3 && this.nextTickTimeNanos - this.lastOverloadWarningNanos >= OVERLOADED_WARNING_INTERVAL_NANOS + 100L * var3) {
               long var7 = var5 / var3;
               LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", var5 / TimeUtil.NANOSECONDS_PER_MILLISECOND, var7);
               this.nextTickTimeNanos += var7 * var3;
               this.lastOverloadWarningNanos = this.nextTickTimeNanos;
            }
         }

         boolean var16 = var3 == 0L;
         if (this.debugCommandProfilerDelayStart) {
            this.debugCommandProfilerDelayStart = false;
            this.debugCommandProfiler = new TimeProfiler(Util.getNanos(), this.tickCount);
         }

         this.nextTickTimeNanos += var3;

         try {
            Profiler.Scope var6 = Profiler.use(this.createProfiler());

            try {
               ProfilerFiller var17 = Profiler.get();
               var17.push("tick");
               this.tickFrame.start();
               this.tickServer(var1, var16 ? () -> false : this::haveTime);
               this.tickFrame.end();
               var17.popPush("nextTickWait");
               this.mayHaveDelayedTasks = true;
               this.delayedTasksMaxNextTickTimeNanos = Math.max(Util.getNanos() + var3, this.nextTickTimeNanos);
               this.startMeasuringTaskExecutionTime();
               this.waitUntilNextTick();
               this.finishMeasuringTaskExecutionTime();
               if (var16) {
                  var2.endTickWork();
               }

               var17.pop();
               this.logFullTickTime();
            } catch (Throwable var14) {
               if (var6 != null) {
                  try {
                     var6.close();
                  } catch (Throwable var13) {
                     var14.addSuppressed(var13);
                  }
               }

               throw var14;
            }

            if (var6 != null) {
               var6.close();
            }
         } finally {
            this.endMetricsRecordingTick();
         }

         this.isReady = true;
         JvmProfiler.INSTANCE.onServerTick(this.smoothedTickTimeMillis);
      }

   }

   private void logFullTickTime() {
      long var1 = Util.getNanos();
      SampleLogger var3 = this.getTickTimeLoggerIfEnabled();
      if (var3 != null) {
         var3.logSample(var1 - this.lastTickNanos);
      }

      this.lastTickNanos = var1;
   }

   private void startMeasuringTaskExecutionTime() {
      if (this.getTickTimeLoggerIfEnabled() != null) {
         this.taskExecutionStartNanos = Util.getNanos();
         this.idleTimeNanos = 0L;
      }

   }

   private void finishMeasuringTaskExecutionTime() {
      SampleLogger var1 = this.getTickTimeLoggerIfEnabled();
      if (var1 != null) {
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

   public static boolean throwIfFatalException() {
      RuntimeException var0 = (RuntimeException)fatalException.get();
      if (var0 != null) {
         throw var0;
      } else {
         return true;
      }
   }

   public static void setFatalException(RuntimeException var0) {
      fatalException.compareAndSet((Object)null, var0);
   }

   public void managedBlock(BooleanSupplier var1) {
      super.managedBlock(() -> throwIfFatalException() && var1.getAsBoolean());
   }

   protected void waitUntilNextTick() {
      this.runAllTasks();
      this.waitingForNextTick = true;

      try {
         this.managedBlock(() -> !this.haveTime());
      } finally {
         this.waitingForNextTick = false;
      }

   }

   public void waitForTasks() {
      boolean var1 = this.getTickTimeLoggerIfEnabled() != null;
      long var2 = var1 ? Util.getNanos() : 0L;
      long var4 = this.waitingForNextTick ? this.nextTickTimeNanos - Util.getNanos() : 100000L;
      LockSupport.parkNanos("waiting for tasks", var4);
      if (var1) {
         this.idleTimeNanos += Util.getNanos() - var2;
      }

   }

   public TickTask wrapRunnable(Runnable var1) {
      return new TickTask(this.tickCount, var1);
   }

   protected boolean shouldRun(TickTask var1) {
      return var1.getTick() + 3 < this.tickCount || this.haveTime();
   }

   public boolean pollTask() {
      TheGame var1 = this.theGame();
      if (var1 == null) {
         var1 = this.theGameButSuperSpecialOneForLoading;
      }

      if (var1 == null) {
         return false;
      } else {
         boolean var2 = this.pollTaskInternal(var1);
         this.mayHaveDelayedTasks = var2;
         return var2;
      }
   }

   private boolean pollTaskInternal(TheGame var1) {
      if (super.pollTask()) {
         return true;
      } else {
         if (var1.tickRateManager().isSprinting() || this.haveTime()) {
            for(ServerLevel var3 : var1.getAllLevels()) {
               if (var3.getChunkSource().pollTask()) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   protected void doRunTask(TickTask var1) {
      Profiler.get().incrementCounter("runTask");
      super.doRunTask(var1);
   }

   private Optional<ServerStatus.Favicon> loadStatusIcon() {
      Optional var1 = Optional.of(this.getFile("server-icon.png")).filter((var0) -> Files.isRegularFile(var0, new LinkOption[0])).or(() -> this.storageSource.getIconFile().filter((var0) -> Files.isRegularFile(var0, new LinkOption[0])));
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

   public Path getServerDirectory() {
      return Path.of("");
   }

   public void onServerCrash(CrashReport var1) {
   }

   public void onServerExit() {
   }

   public boolean isPaused() {
      return false;
   }

   public void tickServer(TheGame var1, BooleanSupplier var2) {
      long var3 = Util.getNanos();
      int var5 = this.pauseWhileEmptySeconds() * 20;
      if (var5 > 0) {
         if (var1.playerList().getPlayerCount() == 0 && !var1.tickRateManager().isSprinting()) {
            ++this.emptyTicks;
         } else {
            this.emptyTicks = 0;
         }

         if (this.emptyTicks >= var5) {
            if (this.emptyTicks == var5) {
               LOGGER.info("Server empty for {} seconds, pausing", this.pauseWhileEmptySeconds());
               this.autoSave(var1);
            }

            this.tickConnection();
            return;
         }
      }

      ++this.tickCount;
      var1.tickRateManager().tick();
      this.tickChildren(var1, var2);
      if (var3 - this.lastServerStatus >= STATUS_EXPIRE_TIME_NANOS) {
         this.lastServerStatus = var3;
         this.status = this.buildServerStatus(var1);
      }

      --this.ticksUntilAutosave;
      if (this.ticksUntilAutosave <= 0) {
         this.autoSave(var1);
      }

      ProfilerFiller var6 = Profiler.get();
      var6.push("tallying");
      long var7 = Util.getNanos() - var3;
      int var9 = this.tickCount % 100;
      this.aggregatedTickTimesNanos -= this.tickTimesNanos[var9];
      this.aggregatedTickTimesNanos += var7;
      this.tickTimesNanos[var9] = var7;
      this.smoothedTickTimeMillis = this.smoothedTickTimeMillis * 0.8F + (float)var7 / (float)TimeUtil.NANOSECONDS_PER_MILLISECOND * 0.19999999F;
      this.logTickMethodTime(var3);
      var6.pop();
   }

   private void autoSave(TheGame var1) {
      this.ticksUntilAutosave = this.computeNextAutosaveInterval(var1);
      LOGGER.debug("Autosave started");
      ProfilerFiller var2 = Profiler.get();
      var2.push("save");
      this.saveEverything(var1, true, false, false);
      var2.pop();
      LOGGER.debug("Autosave finished");
   }

   private void logTickMethodTime(long var1) {
      SampleLogger var3 = this.getTickTimeLoggerIfEnabled();
      if (var3 != null) {
         var3.logPartialSample(Util.getNanos() - var1, TpsDebugDimensions.TICK_SERVER_METHOD.ordinal());
      }

   }

   private int computeNextAutosaveInterval(TheGame var1) {
      ServerTickRateManager var3 = var1.tickRateManager();
      float var2;
      if (var3.isSprinting()) {
         long var4 = this.getAverageTickTimeNanos() + 1L;
         var2 = (float)TimeUtil.NANOSECONDS_PER_SECOND / (float)var4;
      } else {
         var2 = var3.tickrate();
      }

      boolean var6 = true;
      return Math.max(100, (int)(var2 * 300.0F));
   }

   public void onTickRateChanged(TheGame var1) {
      int var2 = this.computeNextAutosaveInterval(var1);
      if (var2 < this.ticksUntilAutosave) {
         this.ticksUntilAutosave = var2;
      }

   }

   @Nullable
   protected abstract SampleLogger getTickTimeLoggerIfEnabled();

   private ServerStatus buildServerStatus(@Nullable TheGame var1) {
      ServerStatus.Players var2 = this.buildPlayerStatus(var1 != null ? var1.playerList() : null);
      return new ServerStatus(Component.nullToEmpty(this.motd), Optional.of(var2), Optional.of(ServerStatus.Version.current()), Optional.ofNullable(this.statusIcon), this.enforceSecureProfile());
   }

   private ServerStatus.Players buildPlayerStatus(@Nullable PlayerList var1) {
      List var2 = var1 != null ? var1.getPlayers() : List.of();
      int var3 = this.getMaxPlayers();
      if (this.hidesOnlinePlayers()) {
         return new ServerStatus.Players(var3, var2.size(), List.of());
      } else {
         int var4 = Math.min(var2.size(), 12);
         ObjectArrayList var5 = new ObjectArrayList(var4);
         int var6 = Mth.nextInt(this.random, 0, var2.size() - var4);

         for(int var7 = 0; var7 < var4; ++var7) {
            ServerPlayer var8 = (ServerPlayer)var2.get(var6 + var7);
            var5.add(var8.allowsListing() ? var8.getGameProfile() : ANONYMOUS_PLAYER_PROFILE);
         }

         Util.shuffle(var5, this.random);
         return new ServerStatus.Players(var3, var2.size(), var5);
      }
   }

   protected void tickChildren(TheGame var1, BooleanSupplier var2) {
      ProfilerFiller var3 = Profiler.get();
      PlayerList var4 = var1.playerList();
      var4.getPlayers().forEach((var0) -> var0.connection.suspendFlushing());
      var3.push("commandFunctions");
      var1.getFunctions().tick();
      var3.popPush("levels");

      for(ServerLevel var6 : var1.getAllLevels()) {
         var3.push((Supplier)(() -> {
            String var10000 = String.valueOf(var6);
            return var10000 + " " + String.valueOf(var6.dimension().location());
         }));
         if (this.tickCount % 20 == 0) {
            var3.push("timeSync");
            var1.synchronizeTime(var6);
            var3.pop();
         }

         var3.push("tick");

         try {
            var6.tick(var2);
         } catch (Throwable var9) {
            CrashReport var8 = CrashReport.forThrowable(var9, "Exception ticking world");
            var6.fillReportDetails(var8);
            throw new ReportedException(var8);
         }

         var3.pop();
         var3.pop();
      }

      var3.popPush("connection");
      this.tickConnection();
      var3.popPush("players");
      var4.tick();
      if (var1.tickRateManager().runsNormally()) {
         GameTestTicker.SINGLETON.tick();
      }

      var3.popPush("server gui refresh");

      for(int var10 = 0; var10 < this.tickables.size(); ++var10) {
         ((Runnable)this.tickables.get(var10)).run();
      }

      var3.popPush("send chunks");

      for(ServerPlayer var12 : var4.getPlayers()) {
         var12.connection.chunkSender.sendNextChunks(var12);
         var12.connection.resumeFlushing();
      }

      var3.pop();
   }

   public void tickConnection() {
      this.getConnection().tick();
   }

   public boolean isLevelEnabled(Level var1) {
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

   public Path getFile(String var1) {
      return this.getServerDirectory().resolve(var1);
   }

   public String getServerVersion() {
      return SharedConstants.getCurrentVersion().getName();
   }

   public int getPlayerCount() {
      TheGame var1 = this.theGame();
      return var1 != null ? var1.playerList().getPlayerCount() : 0;
   }

   public int getMaxPlayers() {
      TheGame var1 = this.theGame();
      return var1 != null ? var1.playerList().getMaxPlayers() : 0;
   }

   @DontObfuscate
   public String getServerModName() {
      return "vanilla";
   }

   public SystemReport fillSystemReport(SystemReport var1) {
      var1.setDetail("Server Running", (Supplier)(() -> Boolean.toString(this.running)));
      TheGame var2 = this.theGame();
      if (var2 != null) {
         var1.setDetail("The Game", "Still On");
         PlayerList var3 = var2.playerList();
         var1.setDetail("Player Count", (Supplier)(() -> {
            int var10000 = var3.getPlayerCount();
            return var10000 + " / " + var3.getMaxPlayers() + "; " + String.valueOf(var3.getPlayers());
         }));
         var2.fillSystemReport(var1);
      } else {
         var1.setDetail("The Game", "Lost");
      }

      SuppressedExceptionCollector var10002 = this.suppressedExceptions;
      Objects.requireNonNull(var10002);
      var1.setDetail("Suppressed Exceptions", var10002::dump);
      if (this.serverId != null) {
         var1.setDetail("Server Id", (Supplier)(() -> this.serverId));
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

   public int getScaledTrackingDistance(int var1) {
      return var1;
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

   public abstract boolean isPublished();

   public ServerConnectionListener getConnection() {
      return this.connection;
   }

   public boolean isReady() {
      return this.isReady;
   }

   public boolean hasGui() {
      return false;
   }

   public boolean publishServer(TheGame var1, @Nullable GameType var2, boolean var3, int var4) {
      return false;
   }

   public int getTickCount() {
      return this.tickCount;
   }

   public int getSpawnProtectionRadius() {
      return 0;
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

   public void setPlayerIdleTimeout(TheGame var1, int var2) {
      this.playerIdleTimeout = var2;
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

   public void kickUnlistedPlayers(CommandSourceStack var1) {
      if (this.isEnforceWhitelist()) {
         PlayerList var2 = var1.playerList();
         UserWhiteList var3 = var2.getWhiteList();

         for(ServerPlayer var6 : Lists.newArrayList(var2.getPlayers())) {
            if (!var3.isWhiteListed(var6.getGameProfile())) {
               var6.connection.disconnect(Component.translatable("multiplayer.disconnect.not_whitelisted"));
            }
         }

      }
   }

   public boolean acceptsSuccess() {
      return true;
   }

   public boolean acceptsFailure() {
      return true;
   }

   public abstract boolean shouldInformAdmins();

   public boolean isEnforceWhitelist() {
      return this.enforceWhitelist;
   }

   public void setEnforceWhitelist(boolean var1) {
      this.enforceWhitelist = var1;
   }

   public float getCurrentSmoothedTickTime() {
      return this.smoothedTickTimeMillis;
   }

   public long getAverageTickTimeNanos() {
      return this.aggregatedTickTimesNanos / (long)Math.min(100, Math.max(this.tickCount, 1));
   }

   public long[] getTickTimesNanos() {
      return this.tickTimesNanos;
   }

   public int getProfilePermissions(GameProfile var1) {
      TheGame var2 = this.theGame();
      if (var2 != null) {
         PlayerList var3 = var2.playerList();
         if (var3.isOp(var1)) {
            ServerOpListEntry var4 = (ServerOpListEntry)var3.getOps().get(var1);
            if (var4 != null) {
               return var4.getLevel();
            }

            if (this.isSingleplayerOwner(var1)) {
               return 4;
            }

            if (this.isSingleplayer()) {
               return var3.isAllowCommandsForAllPlayers() ? 4 : 0;
            }

            return this.getOperatorUserPermissionLevel();
         }
      }

      return 0;
   }

   public abstract boolean isSingleplayerOwner(GameProfile var1);

   public void dumpServerProperties(Path var1) throws IOException {
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

   public void saveDebugReport(Path var1) {
      try {
         TheGame var2 = this.theGame();
         if (var2 != null) {
            var2.saveDebugReport(var1);
         }

         this.dumpClasspath(var1.resolve("classpath.txt"));
         this.dumpMiscStats(var1.resolve("stats.txt"));
         this.dumpThreads(var1.resolve("threads.txt"));
         this.dumpServerProperties(var1.resolve("server.properties.txt"));
         this.dumpNativeModules(var1.resolve("modules.txt"));
      } catch (IOException var3) {
         LOGGER.warn("Failed to save debug report", var3);
      }

   }

   private void dumpClasspath(Path var1) throws IOException {
      BufferedWriter var2 = Files.newBufferedWriter(var1);

      try {
         String var3 = System.getProperty("java.class.path");
         String var4 = System.getProperty("path.separator");

         for(String var6 : Splitter.on(var4).split(var3)) {
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
         for(ThreadInfo var8 : var3) {
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

      label49: {
         try {
            label50: {
               ArrayList var3;
               try {
                  var3 = Lists.newArrayList(NativeModuleLister.listModules());
               } catch (Throwable var7) {
                  LOGGER.warn("Failed to list native modules", var7);
                  break label50;
               }

               var3.sort(Comparator.comparing((var0) -> var0.name));
               Iterator var4 = var3.iterator();

               while(true) {
                  if (!var4.hasNext()) {
                     break label49;
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

   private ProfilerFiller createProfiler() {
      if (this.willStartRecordingMetrics) {
         this.metricsRecorder = ActiveMetricsRecorder.createStarted(new ServerMetricsSamplersProvider(Util.timeSource, this.isDedicatedServer()), Util.timeSource, Util.ioPool(), new MetricsPersister("server"), this.onMetricsRecordingStopped, (var1) -> {
            this.executeBlocking(() -> this.saveDebugReport(var1.resolve("server")));
            this.onMetricsRecordingFinished.accept(var1);
         });
         this.willStartRecordingMetrics = false;
      }

      this.metricsRecorder.startTick();
      return SingleTickProfiler.decorateFiller(this.metricsRecorder.getProfiler(), SingleTickProfiler.createTickProfiler("Server"));
   }

   public void endMetricsRecordingTick() {
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
   }

   public Path getWorldPath(LevelResource var1) {
      return this.storageSource.getLevelPath(var1);
   }

   public boolean forceSynchronousWrites() {
      return true;
   }

   public TextFilter createTextFilterForPlayer(ServerPlayer var1) {
      return TextFilter.DUMMY;
   }

   public ServerPlayerGameMode createGameModeForPlayer(ServerPlayer var1) {
      return (ServerPlayerGameMode)(this.isDemo() ? new DemoMode(var1) : new ServerPlayerGameMode(var1));
   }

   @Nullable
   public GameType getForcedGameType(TheGame var1) {
      return null;
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

   public void runNextTickNow() {
      this.nextTickTimeNanos = Util.getNanos();
   }

   public void runTicksDuringServerPrepare() {
      this.nextTickTimeNanos = Util.getNanos() + PREPARE_LEVELS_DEFAULT_DELAY_NANOS;
      this.waitUntilNextTick();
   }

   public CompletableFuture<MinecraftServer> sayGoodbye() {
      if (this.reloadFuture != null) {
         LOGGER.warn("Already reloading");
         return this.reloadFuture;
      } else {
         TheGame var1 = this.theGame();
         if (var1 == null) {
            LOGGER.warn("Am already dead");
            return null;
         } else {
            this.reloadFuture = new CompletableFuture();

            for(ServerPlayer var4 : List.copyOf(var1.playerList().getPlayers())) {
               var4.connection.switchToConfig();
            }

            return this.reloadFuture;
         }
      }
   }

   public boolean acceptsTransfers() {
      return false;
   }

   private void storeChunkIoError(CrashReport var1, ChunkPos var2, RegionStorageInfo var3) {
      Util.ioPool().execute(() -> {
         try {
            Path var4 = this.getFile("debug");
            FileUtil.createDirectoriesSafe(var4);
            String var5 = FileUtil.sanitizeName(var3.level());
            Path var6 = var4.resolve("chunk-" + var5 + "-" + Util.getFilenameFormattedDateTime() + "-server.txt");
            FileStore var7 = Files.getFileStore(var4);
            long var8 = var7.getUsableSpace();
            if (var8 < 8192L) {
               LOGGER.warn("Not storing chunk IO report due to low space on drive {}", var7.name());
               return;
            }

            CrashReportCategory var10 = var1.addCategory("Chunk Info");
            Objects.requireNonNull(var3);
            var10.setDetail("Level", var3::level);
            var10.setDetail("Dimension", (CrashReportDetail)(() -> var3.dimension().location().toString()));
            Objects.requireNonNull(var3);
            var10.setDetail("Storage", var3::type);
            Objects.requireNonNull(var2);
            var10.setDetail("Position", var2::toString);
            var1.saveToFile(var6, ReportType.CHUNK_IO_ERROR);
            LOGGER.info("Saved details to {}", var1.getSaveFile());
         } catch (Exception var11) {
            LOGGER.warn("Failed to store chunk IO exception", var11);
         }

      });
   }

   public void reportChunkLoadFailure(Throwable var1, RegionStorageInfo var2, ChunkPos var3) {
      LOGGER.error("Failed to load chunk {},{}", new Object[]{var3.x, var3.z, var1});
      this.suppressedExceptions.addEntry("chunk/load", var1);
      this.storeChunkIoError(CrashReport.forThrowable(var1, "Chunk load failure"), var3, var2);
   }

   public void reportChunkSaveFailure(Throwable var1, RegionStorageInfo var2, ChunkPos var3) {
      LOGGER.error("Failed to save chunk {},{}", new Object[]{var3.x, var3.z, var1});
      this.suppressedExceptions.addEntry("chunk/save", var1);
      this.storeChunkIoError(CrashReport.forThrowable(var1, "Chunk save failure"), var3, var2);
   }

   public void reportPacketHandlingException(Throwable var1, PacketType<?> var2) {
      this.suppressedExceptions.addEntry("packet/" + var2.toString(), var1);
   }

   public ServerLinks serverLinks() {
      return ServerLinks.EMPTY;
   }

   protected int pauseWhileEmptySeconds() {
      return 0;
   }

   public boolean isSpawningMonsters(TheGame var1) {
      return var1.isSpawningMonsters();
   }

   public boolean isHardcore(TheGame var1) {
      return var1.isHardcore();
   }

   public void setDefaultGameType(TheGame var1, GameType var2) {
      var1.setDefaultGameType(var2);
   }

   public boolean saveEverything(TheGame var1, boolean var2, boolean var3, boolean var4) {
      boolean var5;
      try {
         this.isSaving = true;
         var1.playerList().saveAll();
         var5 = var1.saveAllChunks(var2, var3, var4);
      } finally {
         this.isSaving = false;
      }

      return var5;
   }

   // $FF: synthetic method
   public void doRunTask(final Runnable var1) {
      this.doRunTask((TickTask)var1);
   }

   // $FF: synthetic method
   public boolean shouldRun(final Runnable var1) {
      return this.shouldRun((TickTask)var1);
   }

   // $FF: synthetic method
   public Runnable wrapRunnable(final Runnable var1) {
      return this.wrapRunnable(var1);
   }

   static {
      OVERLOADED_THRESHOLD_NANOS = 20L * TimeUtil.NANOSECONDS_PER_SECOND / 20L;
      OVERLOADED_WARNING_INTERVAL_NANOS = 10L * TimeUtil.NANOSECONDS_PER_SECOND;
      STATUS_EXPIRE_TIME_NANOS = 5L * TimeUtil.NANOSECONDS_PER_SECOND;
      PREPARE_LEVELS_DEFAULT_DELAY_NANOS = 10L * TimeUtil.NANOSECONDS_PER_MILLISECOND;
      DEMO_SETTINGS = new LevelSettings("Demo World", GameType.SURVIVAL, false, Difficulty.NORMAL, false, new GameRules(FeatureFlags.DEFAULT_FLAGS), WorldDataConfiguration.DEFAULT);
      ANONYMOUS_PLAYER_PROFILE = new GameProfile(Util.NIL_UUID, "Anonymous Player");
      PLAYER_RECONFIG_KICK_PERIOD = TimeUnit.SECONDS.toMillis(30L);
      fatalException = new AtomicReference();
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

   @FunctionalInterface
   public interface WorldReloader {
      WorldStem reload(LevelStorageSource.LevelStorageAccess var1, PackRepository var2, WorldStem var3, @Nullable CompoundTag var4);
   }
}
