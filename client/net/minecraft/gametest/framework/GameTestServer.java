package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.serialization.Lifecycle;
import java.net.Proxy;
import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.SystemReport;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerResources;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.LoggerChunkProgressListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameTestServer extends MinecraftServer {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int PROGRESS_REPORT_INTERVAL = 20;
   private final List<GameTestBatch> testBatches;
   private final BlockPos spawnPos;
   private static final GameRules TEST_GAME_RULES = (GameRules)Util.make(new GameRules(), (var0) -> {
      ((GameRules.BooleanValue)var0.getRule(GameRules.RULE_DOMOBSPAWNING)).set(false, (MinecraftServer)null);
      ((GameRules.BooleanValue)var0.getRule(GameRules.RULE_WEATHER_CYCLE)).set(false, (MinecraftServer)null);
   });
   private static final LevelSettings TEST_SETTINGS;
   @Nullable
   private MultipleTestTracker testTracker;

   public GameTestServer(Thread var1, LevelStorageSource.LevelStorageAccess var2, PackRepository var3, ServerResources var4, Collection<GameTestBatch> var5, BlockPos var6, RegistryAccess.RegistryHolder var7) {
      this(var1, var2, var3, var4, var5, var6, var7, var7.registryOrThrow(Registry.BIOME_REGISTRY), var7.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY));
   }

   private GameTestServer(Thread var1, LevelStorageSource.LevelStorageAccess var2, PackRepository var3, ServerResources var4, Collection<GameTestBatch> var5, BlockPos var6, RegistryAccess.RegistryHolder var7, Registry<Biome> var8, Registry<DimensionType> var9) {
      super(var1, var7, var2, new PrimaryLevelData(TEST_SETTINGS, new WorldGenSettings(0L, false, false, WorldGenSettings.withOverworld((Registry)var9, (MappedRegistry)DimensionType.defaultDimensions(var7, 0L), new FlatLevelSource(FlatLevelGeneratorSettings.getDefault(var8)))), Lifecycle.stable()), var3, Proxy.NO_PROXY, DataFixers.getDataFixer(), var4, (MinecraftSessionService)null, (GameProfileRepository)null, (GameProfileCache)null, LoggerChunkProgressListener::new);
      this.testBatches = Lists.newArrayList(var5);
      this.spawnPos = var6;
      if (var5.isEmpty()) {
         throw new IllegalArgumentException("No test batches were given!");
      }
   }

   public boolean initServer() {
      this.setPlayerList(new PlayerList(this, this.registryHolder, this.playerDataStorage, 1) {
      });
      this.loadLevel();
      ServerLevel var1 = this.overworld();
      var1.setDefaultSpawnPos(this.spawnPos, 0.0F);
      int var2 = 20000000;
      var1.setWeatherParameters(20000000, 20000000, false, false);
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
         LOGGER.info("========= {} GAME TESTS COMPLETE ======================", this.testTracker.getTotalCount());
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

   public SystemReport fillServerSystemReport(SystemReport var1) {
      var1.setDetail("Type", "Game test server");
      return var1;
   }

   public void onServerExit() {
      super.onServerExit();
      System.exit(this.testTracker.getFailedRequiredCount());
   }

   public void onServerCrash(CrashReport var1) {
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
      TEST_SETTINGS = new LevelSettings("Test Level", GameType.CREATIVE, false, Difficulty.NORMAL, true, TEST_GAME_RULES, DataPackConfig.DEFAULT);
   }
}
