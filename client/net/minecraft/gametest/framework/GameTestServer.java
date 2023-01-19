package net.minecraft.gametest.framework;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import java.net.Proxy;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.SystemReport;
import net.minecraft.Util;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.LoggerChunkProgressListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.SignatureValidator;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.slf4j.Logger;

public class GameTestServer extends MinecraftServer {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int PROGRESS_REPORT_INTERVAL = 20;
   private static final Services NO_SERVICES = new Services(null, SignatureValidator.NO_VALIDATION, null, null);
   private final List<GameTestBatch> testBatches;
   private final BlockPos spawnPos;
   private static final GameRules TEST_GAME_RULES = Util.make(new GameRules(), var0 -> {
      var0.getRule(GameRules.RULE_DOMOBSPAWNING).set(false, null);
      var0.getRule(GameRules.RULE_WEATHER_CYCLE).set(false, null);
   });
   private static final LevelSettings TEST_SETTINGS = new LevelSettings(
      "Test Level", GameType.CREATIVE, false, Difficulty.NORMAL, true, TEST_GAME_RULES, DataPackConfig.DEFAULT
   );
   @Nullable
   private MultipleTestTracker testTracker;

   public static GameTestServer create(
      Thread var0, LevelStorageSource.LevelStorageAccess var1, PackRepository var2, Collection<GameTestBatch> var3, BlockPos var4
   ) {
      if (var3.isEmpty()) {
         throw new IllegalArgumentException("No test batches were given!");
      } else {
         WorldLoader.PackConfig var5 = new WorldLoader.PackConfig(var2, DataPackConfig.DEFAULT, false);
         WorldLoader.InitConfig var6 = new WorldLoader.InitConfig(var5, Commands.CommandSelection.DEDICATED, 4);

         try {
            LOGGER.debug("Starting resource loading");
            Stopwatch var7 = Stopwatch.createStarted();
            WorldStem var8 = Util.<WorldStem>blockUntilDone(
                  var1x -> WorldStem.load(
                        var6,
                        (var0xx, var1xx) -> {
                           RegistryAccess.Frozen var2x = RegistryAccess.BUILTIN.get();
                           WorldGenSettings var3x = var2x.<WorldPreset>registryOrThrow(Registry.WORLD_PRESET_REGISTRY)
                              .getHolderOrThrow(WorldPresets.FLAT)
                              .value()
                              .createWorldGenSettings(0L, false, false);
                           PrimaryLevelData var4x = new PrimaryLevelData(TEST_SETTINGS, var3x, Lifecycle.stable());
                           return Pair.of(var4x, var2x);
                        },
                        Util.backgroundExecutor(),
                        var1x
                     )
               )
               .get();
            var7.stop();
            LOGGER.debug("Finished resource loading after {} ms", var7.elapsed(TimeUnit.MILLISECONDS));
            return new GameTestServer(var0, var1, var2, var8, var3, var4);
         } catch (Exception var9) {
            LOGGER.warn("Failed to load vanilla datapack, bit oops", var9);
            System.exit(-1);
            throw new IllegalStateException();
         }
      }
   }

   private GameTestServer(
      Thread var1, LevelStorageSource.LevelStorageAccess var2, PackRepository var3, WorldStem var4, Collection<GameTestBatch> var5, BlockPos var6
   ) {
      super(var1, var2, var3, var4, Proxy.NO_PROXY, DataFixers.getDataFixer(), NO_SERVICES, LoggerChunkProgressListener::new);
      this.testBatches = Lists.newArrayList(var5);
      this.spawnPos = var6;
   }

   @Override
   public boolean initServer() {
      this.setPlayerList(new PlayerList(this, this.registryAccess(), this.playerDataStorage, 1) {
      });
      this.loadLevel();
      ServerLevel var1 = this.overworld();
      var1.setDefaultSpawnPos(this.spawnPos, 0.0F);
      int var2 = 20000000;
      var1.setWeatherParameters(20000000, 20000000, false, false);
      LOGGER.info("Started game test server");
      return true;
   }

   @Override
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
         LOGGER.info("========= {} GAME TESTS COMPLETE ======================", this.testTracker.getTotalCount());
         if (this.testTracker.hasFailedRequired()) {
            LOGGER.info("{} required tests failed :(", this.testTracker.getFailedRequiredCount());
            this.testTracker.getFailedRequired().forEach(var0 -> LOGGER.info("   - {}", var0.getTestName()));
         } else {
            LOGGER.info("All {} required tests passed :)", this.testTracker.getTotalCount());
         }

         if (this.testTracker.hasFailedOptional()) {
            LOGGER.info("{} optional tests failed", this.testTracker.getFailedOptionalCount());
            this.testTracker.getFailedOptional().forEach(var0 -> LOGGER.info("   - {}", var0.getTestName()));
         }

         LOGGER.info("====================================================");
      }
   }

   @Override
   public SystemReport fillServerSystemReport(SystemReport var1) {
      var1.setDetail("Type", "Game test server");
      return var1;
   }

   @Override
   public void onServerExit() {
      super.onServerExit();
      LOGGER.info("Game test server shutting down");
      System.exit(this.testTracker.getFailedRequiredCount());
   }

   @Override
   public void onServerCrash(CrashReport var1) {
      super.onServerCrash(var1);
      LOGGER.error("Game test server crashed\n{}", var1.getFriendlyReport());
      System.exit(1);
   }

   private void startTests(ServerLevel var1) {
      Collection var2 = GameTestRunner.runTestBatches(this.testBatches, new BlockPos(0, -60, 0), Rotation.NONE, var1, GameTestTicker.SINGLETON, 8);
      this.testTracker = new MultipleTestTracker(var2);
      LOGGER.info("{} tests are now running!", this.testTracker.getTotalCount());
   }

   private boolean haveTestsStarted() {
      return this.testTracker != null;
   }

   @Override
   public boolean isHardcore() {
      return false;
   }

   @Override
   public int getOperatorUserPermissionLevel() {
      return 0;
   }

   @Override
   public int getFunctionCompilationLevel() {
      return 4;
   }

   @Override
   public boolean shouldRconBroadcast() {
      return false;
   }

   @Override
   public boolean isDedicatedServer() {
      return false;
   }

   @Override
   public int getRateLimitPacketsPerSecond() {
      return 0;
   }

   @Override
   public boolean isEpollEnabled() {
      return false;
   }

   @Override
   public boolean isCommandBlockEnabled() {
      return true;
   }

   @Override
   public boolean isPublished() {
      return false;
   }

   @Override
   public boolean shouldInformAdmins() {
      return false;
   }

   @Override
   public boolean isSingleplayerOwner(GameProfile var1) {
      return false;
   }
}
