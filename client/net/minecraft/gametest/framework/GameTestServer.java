package net.minecraft.gametest.framework;

import com.google.common.base.Stopwatch;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ServicesKeySet;
import com.mojang.brigadier.StringReader;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.ReportType;
import net.minecraft.SystemReport;
import net.minecraft.Util;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceSelectorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.LoggingLevelLoadListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.ProfileResolver;
import net.minecraft.server.players.UserNameToIdResolver;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.debugchart.LocalSampleLogger;
import net.minecraft.util.debugchart.SampleLogger;
import net.minecraft.world.Difficulty;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.block.Rotation;
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
   private static final FeatureFlagSet ENABLED_FEATURES;
   private final LocalSampleLogger sampleLogger = new LocalSampleLogger(4);
   private final Optional<String> testSelection;
   private final boolean verify;
   private List<GameTestBatch> testBatches = new ArrayList();
   private final Stopwatch stopwatch = Stopwatch.createUnstarted();
   private static final WorldOptions WORLD_OPTIONS;
   @Nullable
   private MultipleTestTracker testTracker;

   public static GameTestServer create(Thread var0, LevelStorageSource.LevelStorageAccess var1, PackRepository var2, Optional<String> var3, boolean var4) {
      var2.reload();
      ArrayList var5 = new ArrayList(var2.getAvailableIds());
      var5.remove("vanilla");
      var5.addFirst("vanilla");
      WorldDataConfiguration var6 = new WorldDataConfiguration(new DataPackConfig(var5, List.of()), ENABLED_FEATURES);
      LevelSettings var7 = new LevelSettings("Test Level", GameType.CREATIVE, false, Difficulty.NORMAL, true, new GameRules(ENABLED_FEATURES), var6);
      WorldLoader.PackConfig var8 = new WorldLoader.PackConfig(var2, var6, false, true);
      WorldLoader.InitConfig var9 = new WorldLoader.InitConfig(var8, Commands.CommandSelection.DEDICATED, 4);

      try {
         LOGGER.debug("Starting resource loading");
         Stopwatch var10 = Stopwatch.createStarted();
         WorldStem var11 = (WorldStem)Util.blockUntilDone((var2x) -> WorldLoader.load(var9, (var1) -> {
               Registry var2 = (new MappedRegistry(Registries.LEVEL_STEM, Lifecycle.stable())).freeze();
               WorldDimensions.Complete var3 = ((WorldPreset)var1.datapackWorldgen().lookupOrThrow(Registries.WORLD_PRESET).getOrThrow(WorldPresets.FLAT).value()).createWorldDimensions().bake(var2);
               return new WorldLoader.DataLoadOutput(new PrimaryLevelData(var7, WORLD_OPTIONS, var3.specialWorldProperty(), var3.lifecycle()), var3.dimensionsRegistryAccess());
            }, WorldStem::new, Util.backgroundExecutor(), var2x)).get();
         var10.stop();
         LOGGER.debug("Finished resource loading after {} ms", var10.elapsed(TimeUnit.MILLISECONDS));
         return new GameTestServer(var0, var1, var2, var11, var3, var4);
      } catch (Exception var12) {
         LOGGER.warn("Failed to load vanilla datapack, bit oops", var12);
         System.exit(-1);
         throw new IllegalStateException();
      }
   }

   private GameTestServer(Thread var1, LevelStorageSource.LevelStorageAccess var2, PackRepository var3, WorldStem var4, Optional<String> var5, boolean var6) {
      super(var1, var2, var3, var4, Proxy.NO_PROXY, DataFixers.getDataFixer(), NO_SERVICES, LoggingLevelLoadListener.forDedicatedServer());
      this.testSelection = var5;
      this.verify = var6;
   }

   public boolean initServer() {
      this.setPlayerList(new PlayerList(this, this.registries(), this.playerDataStorage, 1) {
      });
      this.loadLevel();
      ServerLevel var1 = this.overworld();
      this.testBatches = this.evaluateTestsToRun(var1);
      LOGGER.info("Started game test server");
      return true;
   }

   private List<GameTestBatch> evaluateTestsToRun(ServerLevel var1) {
      Registry var2 = var1.registryAccess().lookupOrThrow(Registries.TEST_INSTANCE);
      List var3;
      GameTestBatchFactory.TestDecorator var4;
      if (this.testSelection.isPresent()) {
         var3 = getTestsForSelection(var1.registryAccess(), (String)this.testSelection.get()).filter((var0) -> !((GameTestInstance)var0.value()).manualOnly()).toList();
         if (this.verify) {
            var4 = GameTestServer::rotateAndMultiply;
            LOGGER.info("Verify requested. Will run each test that matches {} {} times", this.testSelection.get(), 100 * Rotation.values().length);
         } else {
            var4 = GameTestBatchFactory.DIRECT;
            LOGGER.info("Will run tests matching {} ({} tests)", this.testSelection.get(), var3.size());
         }
      } else {
         var3 = var2.listElements().filter((var0) -> !((GameTestInstance)var0.value()).manualOnly()).toList();
         var4 = GameTestBatchFactory.DIRECT;
      }

      return GameTestBatchFactory.divideIntoBatches(var3, var4, var1);
   }

   private static Stream<GameTestInfo> rotateAndMultiply(Holder.Reference<GameTestInstance> var0, ServerLevel var1) {
      Stream.Builder var2 = Stream.builder();

      for(Rotation var6 : Rotation.values()) {
         for(int var7 = 0; var7 < 100; ++var7) {
            var2.add(new GameTestInfo(var0, var6, var1, RetryOptions.noRetries()));
         }
      }

      return var2.build();
   }

   public static Stream<Holder.Reference<GameTestInstance>> getTestsForSelection(RegistryAccess var0, String var1) {
      return ResourceSelectorArgument.parse(new StringReader(var1), var0.lookupOrThrow(Registries.TEST_INSTANCE)).stream();
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
            this.testTracker.getFailedRequired().forEach(GameTestServer::logFailedTest);
         } else {
            LOGGER.info("All {} required tests passed :)", this.testTracker.getTotalCount());
         }

         if (this.testTracker.hasFailedOptional()) {
            LOGGER.info("{} optional tests failed", this.testTracker.getFailedOptionalCount());
            this.testTracker.getFailedOptional().forEach(GameTestServer::logFailedTest);
         }

         LOGGER.info("====================================================");
      }

   }

   private static void logFailedTest(GameTestInfo var0) {
      if (var0.getRotation() != Rotation.NONE) {
         LOGGER.info("   - {} with rotation {}: {}", new Object[]{var0.id(), var0.getRotation().getSerializedName(), var0.getError().getDescription().getString()});
      } else {
         LOGGER.info("   - {}: {}", var0.id(), var0.getError().getDescription().getString());
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
      System.exit(this.testTracker != null ? this.testTracker.getFailedRequiredCount() : -1);
   }

   public void onServerCrash(CrashReport var1) {
      super.onServerCrash(var1);
      LOGGER.error("Game test server crashed\n{}", var1.getFriendlyReport(ReportType.CRASH));
      System.exit(1);
   }

   private void startTests(ServerLevel var1) {
      BlockPos var2 = new BlockPos(var1.random.nextIntBetweenInclusive(-14999992, 14999992), -59, var1.random.nextIntBetweenInclusive(-14999992, 14999992));
      var1.setDefaultSpawnPos(var2, 0.0F);
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

   public boolean isSingleplayerOwner(NameAndId var1) {
      return false;
   }

   static {
      NO_SERVICES = new Services((MinecraftSessionService)null, ServicesKeySet.EMPTY, (GameProfileRepository)null, new MockUserNameToIdResolver(), new MockProfileResolver());
      ENABLED_FEATURES = FeatureFlags.REGISTRY.allFlags().subtract(FeatureFlagSet.of(FeatureFlags.REDSTONE_EXPERIMENTS, FeatureFlags.MINECART_IMPROVEMENTS));
      WORLD_OPTIONS = new WorldOptions(0L, false, false);
   }

   static class MockUserNameToIdResolver implements UserNameToIdResolver {
      private final Set<NameAndId> savedIds = new HashSet();

      MockUserNameToIdResolver() {
         super();
      }

      public void add(NameAndId var1) {
         this.savedIds.add(var1);
      }

      public Optional<NameAndId> get(String var1) {
         return this.savedIds.stream().filter((var1x) -> var1x.name().equals(var1)).findFirst().or(() -> Optional.of(NameAndId.createOffline(var1)));
      }

      public Optional<NameAndId> get(UUID var1) {
         return this.savedIds.stream().filter((var1x) -> var1x.id().equals(var1)).findFirst();
      }

      public void resolveOfflineUsers(boolean var1) {
      }

      public void save() {
      }
   }

   static class MockProfileResolver implements ProfileResolver {
      MockProfileResolver() {
         super();
      }

      public Optional<GameProfile> fetchByName(String var1) {
         return Optional.empty();
      }

      public Optional<GameProfile> fetchById(UUID var1) {
         return Optional.empty();
      }
   }
}
