package net.minecraft.gametest.framework;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ServicesKeySet;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.ReportType;
import net.minecraft.SystemReport;
import net.minecraft.Util;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.LoggerChunkProgressListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.debugchart.LocalSampleLogger;
import net.minecraft.util.debugchart.SampleLogger;
import net.minecraft.world.Difficulty;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.slf4j.Logger;

public class GameTestServer extends MinecraftServer {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int PROGRESS_REPORT_INTERVAL = 20;
   private static final int TEST_POSITION_RANGE = 14999992;
   private static final Services NO_SERVICES;
   private final LocalSampleLogger sampleLogger = new LocalSampleLogger(4);
   private List<GameTestBatch> testBatches = new ArrayList();
   private final List<TestFunction> testFunctions;
   private final BlockPos spawnPos;
   private final Stopwatch stopwatch = Stopwatch.createUnstarted();
   private static final GameRules TEST_GAME_RULES;
   private static final WorldOptions WORLD_OPTIONS;
   @Nullable
   private MultipleTestTracker testTracker;

   public static GameTestServer create(Thread var0, LevelStorageSource.LevelStorageAccess var1, PackRepository var2, Collection<TestFunction> var3, BlockPos var4) {
      if (var3.isEmpty()) {
         throw new IllegalArgumentException("No test functions were given!");
      } else {
         var2.reload();
         WorldDataConfiguration var5 = new WorldDataConfiguration(new DataPackConfig(new ArrayList(var2.getAvailableIds()), List.of()), FeatureFlags.REGISTRY.allFlags());
         LevelSettings var6 = new LevelSettings("Test Level", GameType.CREATIVE, false, Difficulty.NORMAL, true, TEST_GAME_RULES, var5);
         WorldLoader.PackConfig var7 = new WorldLoader.PackConfig(var2, var5, false, true);
         WorldLoader.InitConfig var8 = new WorldLoader.InitConfig(var7, Commands.CommandSelection.DEDICATED, 4);

         try {
            LOGGER.debug("Starting resource loading");
            Stopwatch var9 = Stopwatch.createStarted();
            WorldStem var10 = (WorldStem)Util.blockUntilDone((var2x) -> {
               return WorldLoader.load(var8, (var1) -> {
                  Registry var2 = (new MappedRegistry(Registries.LEVEL_STEM, Lifecycle.stable())).freeze();
                  WorldDimensions.Complete var3 = ((WorldPreset)var1.datapackWorldgen().registryOrThrow(Registries.WORLD_PRESET).getHolderOrThrow(WorldPresets.FLAT).value()).createWorldDimensions().bake(var2);
                  return new WorldLoader.DataLoadOutput(new PrimaryLevelData(var6, WORLD_OPTIONS, var3.specialWorldProperty(), var3.lifecycle()), var3.dimensionsRegistryAccess());
               }, WorldStem::new, Util.backgroundExecutor(), var2x);
            }).get();
            var9.stop();
            LOGGER.debug("Finished resource loading after {} ms", var9.elapsed(TimeUnit.MILLISECONDS));
            return new GameTestServer(var0, var1, var2, var10, var3, var4);
         } catch (Exception var11) {
            LOGGER.warn("Failed to load vanilla datapack, bit oops", var11);
            System.exit(-1);
            throw new IllegalStateException();
         }
      }
   }

   private GameTestServer(Thread var1, LevelStorageSource.LevelStorageAccess var2, PackRepository var3, WorldStem var4, Collection<TestFunction> var5, BlockPos var6) {
      super(var1, var2, var3, var4, Proxy.NO_PROXY, DataFixers.getDataFixer(), NO_SERVICES, LoggerChunkProgressListener::createFromGameruleRadius);
      this.testFunctions = Lists.newArrayList(var5);
      this.spawnPos = var6;
   }

   public boolean initServer() {
      this.setPlayerList(new PlayerList(this, this, this.registries(), this.playerDataStorage, 1) {
      });
      this.loadLevel();
      ServerLevel var1 = this.overworld();
      this.testBatches = Lists.newArrayList(GameTestBatchFactory.fromTestFunction(this.testFunctions, var1));
      var1.setDefaultSpawnPos(this.spawnPos, 0.0F);
      int var2 = 20000000;
      var1.setWeatherParameters(20000000, 20000000, false, false);
      LOGGER.info("Started game test server");
      return true;
   }

   public void tickServer(BooleanSupplier var1) {
      super.tickServer(var1);
      ServerLevel var2 = this.overworld();
      if (!this.haveTestsStarted()) {
         this.startTests(var2);
      }

      if (var2.getGameTime() % 20L == 0L) {
         LOGGER.info(this.testTracker.getProgressBar());
      }

      if (this.testTracker.isDone()) {
         this.halt(false);
         LOGGER.info(this.testTracker.getProgressBar());
         GlobalTestReporter.finish();
         LOGGER.info("========= {} GAME TESTS COMPLETE IN {} ======================", this.testTracker.getTotalCount(), this.stopwatch.stop());
         if (this.testTracker.hasFailedRequired()) {
            LOGGER.info("{} required tests failed :(", this.testTracker.getFailedRequiredCount());
            this.testTracker.getFailedRequired().forEach((var0) -> {
               LOGGER.info("   - {}", var0.getTestName());
            });
         } else {
            LOGGER.info("All {} required tests passed :)", this.testTracker.getTotalCount());
         }

         if (this.testTracker.hasFailedOptional()) {
            LOGGER.info("{} optional tests failed", this.testTracker.getFailedOptionalCount());
            this.testTracker.getFailedOptional().forEach((var0) -> {
               LOGGER.info("   - {}", var0.getTestName());
            });
         }

         LOGGER.info("====================================================");
      }

   }

   public SampleLogger getTickTimeLogger() {
      return this.sampleLogger;
   }

   public boolean isTickTimeLoggingEnabled() {
      return false;
   }

   public void waitUntilNextTick() {
      this.runAllTasks();
   }

   public SystemReport fillServerSystemReport(SystemReport var1) {
      var1.setDetail("Type", "Game test server");
      return var1;
   }

   public void onServerExit() {
      super.onServerExit();
      LOGGER.info("Game test server shutting down");
      System.exit(this.testTracker.getFailedRequiredCount());
   }

   public void onServerCrash(CrashReport var1) {
      super.onServerCrash(var1);
      LOGGER.error("Game test server crashed\n{}", var1.getFriendlyReport(ReportType.CRASH));
      System.exit(1);
   }

   private void startTests(ServerLevel var1) {
      BlockPos var2 = new BlockPos(var1.random.nextIntBetweenInclusive(-14999992, 14999992), -59, var1.random.nextIntBetweenInclusive(-14999992, 14999992));
      GameTestRunner var3 = GameTestRunner.Builder.fromBatches(this.testBatches, var1).newStructureSpawner(new StructureGridSpawner(var2, 8, false)).build();
      List var4 = var3.getTestInfos();
      this.testTracker = new MultipleTestTracker(var4);
      LOGGER.info("{} tests are now running at position {}!", this.testTracker.getTotalCount(), var2.toShortString());
      this.stopwatch.reset();
      this.stopwatch.start();
      var3.start();
   }

   private boolean haveTestsStarted() {
      return this.testTracker != null;
   }

   public boolean isHardcore() {
      return false;
   }

   public int getOperatorUserPermissionLevel() {
      return 0;
   }

   public int getFunctionCompilationLevel() {
      return 4;
   }

   public boolean shouldRconBroadcast() {
      return false;
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

   public boolean isCommandBlockEnabled() {
      return true;
   }

   public boolean isPublished() {
      return false;
   }

   public boolean shouldInformAdmins() {
      return false;
   }

   public boolean isSingleplayerOwner(GameProfile var1) {
      return false;
   }

   static {
      NO_SERVICES = new Services((MinecraftSessionService)null, ServicesKeySet.EMPTY, (GameProfileRepository)null, (GameProfileCache)null);
      TEST_GAME_RULES = (GameRules)Util.make(new GameRules(), (var0) -> {
         ((GameRules.BooleanValue)var0.getRule(GameRules.RULE_DOMOBSPAWNING)).set(false, (MinecraftServer)null);
         ((GameRules.BooleanValue)var0.getRule(GameRules.RULE_WEATHER_CYCLE)).set(false, (MinecraftServer)null);
         ((GameRules.IntegerValue)var0.getRule(GameRules.RULE_RANDOMTICKING)).set(0, (MinecraftServer)null);
         ((GameRules.BooleanValue)var0.getRule(GameRules.RULE_DOFIRETICK)).set(false, (MinecraftServer)null);
      });
      WORLD_OPTIONS = new WorldOptions(0L, false, false);
   }
}
