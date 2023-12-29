package net.minecraft.server;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;
import joptsimple.util.PathConverter;
import joptsimple.util.PathProperties;
import net.minecraft.CrashReport;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.ReportedNbtException;
import net.minecraft.network.chat.Component;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.server.level.progress.LoggerChunkProgressListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.profiling.jfr.Environment;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.worldupdate.WorldUpgrader;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelDataAndDimensions;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.slf4j.Logger;

public class Main {
   private static final Logger LOGGER = LogUtils.getLogger();

   public Main() {
      super();
   }

   @DontObfuscate
   public static void main(String[] var0) {
      SharedConstants.tryDetectVersion();
      OptionParser var1 = new OptionParser();
      OptionSpecBuilder var2 = var1.accepts("nogui");
      OptionSpecBuilder var3 = var1.accepts("initSettings", "Initializes 'server.properties' and 'eula.txt', then quits");
      OptionSpecBuilder var4 = var1.accepts("demo");
      OptionSpecBuilder var5 = var1.accepts("bonusChest");
      OptionSpecBuilder var6 = var1.accepts("forceUpgrade");
      OptionSpecBuilder var7 = var1.accepts("eraseCache");
      OptionSpecBuilder var8 = var1.accepts("safeMode", "Loads level with vanilla datapack only");
      AbstractOptionSpec var9 = var1.accepts("help").forHelp();
      ArgumentAcceptingOptionSpec var10 = var1.accepts("universe").withRequiredArg().defaultsTo(".", new String[0]);
      ArgumentAcceptingOptionSpec var11 = var1.accepts("world").withRequiredArg();
      ArgumentAcceptingOptionSpec var12 = var1.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(-1, new Integer[0]);
      ArgumentAcceptingOptionSpec var13 = var1.accepts("serverId").withRequiredArg();
      OptionSpecBuilder var14 = var1.accepts("jfrProfile");
      ArgumentAcceptingOptionSpec var15 = var1.accepts("pidFile").withRequiredArg().withValuesConvertedBy(new PathConverter(new PathProperties[0]));
      NonOptionArgumentSpec var16 = var1.nonOptions();

      try {
         OptionSet var17 = var1.parse(var0);
         if (var17.has(var9)) {
            var1.printHelpOn(System.err);
            return;
         }

         Path var18 = (Path)var17.valueOf(var15);
         if (var18 != null) {
            writePidFile(var18);
         }

         CrashReport.preload();
         if (var17.has(var14)) {
            JvmProfiler.INSTANCE.start(Environment.SERVER);
         }

         Bootstrap.bootStrap();
         Bootstrap.validate();
         Util.startTimerHackThread();
         Path var19 = Paths.get("server.properties");
         DedicatedServerSettings var20 = new DedicatedServerSettings(var19);
         var20.forceSave();
         Path var21 = Paths.get("eula.txt");
         Eula var22 = new Eula(var21);
         if (var17.has(var3)) {
            LOGGER.info("Initialized '{}' and '{}'", var19.toAbsolutePath(), var21.toAbsolutePath());
            return;
         }

         if (!var22.hasAgreedToEULA()) {
            LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
            return;
         }

         File var23 = new File((String)var17.valueOf(var10));
         Services var24 = Services.create(new YggdrasilAuthenticationService(Proxy.NO_PROXY), var23);
         String var25 = (String)Optional.ofNullable((String)var17.valueOf(var11)).orElse(var20.getProperties().levelName);
         LevelStorageSource var26 = LevelStorageSource.createDefault(var23.toPath());
         LevelStorageSource.LevelStorageAccess var27 = var26.validateAndCreateAccess(var25);
         Dynamic var28;
         if (var27.hasWorldData()) {
            LevelSummary var29;
            try {
               var28 = var27.getDataTag();
               var29 = var27.getSummary(var28);
            } catch (NbtException | ReportedNbtException | IOException var39) {
               LevelStorageSource.LevelDirectory var31 = var27.getLevelDirectory();
               LOGGER.warn("Failed to load world data from {}", var31.dataFile(), var39);
               LOGGER.info("Attempting to use fallback");

               try {
                  var28 = var27.getDataTagFallback();
                  var29 = var27.getSummary(var28);
               } catch (NbtException | ReportedNbtException | IOException var38) {
                  LOGGER.error("Failed to load world data from {}", var31.oldDataFile(), var38);
                  LOGGER.error("Failed to load world data from {} and {}. World files may be corrupted. Shutting down.", var31.dataFile(), var31.oldDataFile());
                  return;
               }

               var27.restoreLevelDataFromOld();
            }

            if (var29.requiresManualConversion()) {
               LOGGER.info("This world must be opened in an older version (like 1.6.4) to be safely converted");
               return;
            }

            if (!var29.isCompatible()) {
               LOGGER.info("This world was created by an incompatible version.");
               return;
            }
         } else {
            var28 = null;
         }

         Dynamic var41 = var28;
         boolean var30 = var17.has(var8);
         if (var30) {
            LOGGER.warn("Safe mode active, only vanilla datapack will be loaded");
         }

         PackRepository var42 = ServerPacksSource.createPackRepository(var27);

         WorldStem var32;
         try {
            WorldLoader.InitConfig var33 = loadOrCreateConfig(var20.getProperties(), var41, var30, var42);
            var32 = Util.<WorldStem>blockUntilDone(
                  var6x -> WorldLoader.load(
                        var33,
                        var5xx -> {
                           Registry var6xxx = var5xx.datapackDimensions().registryOrThrow(Registries.LEVEL_STEM);
                           if (var41 != null) {
                              LevelDataAndDimensions var12xx = LevelStorageSource.getLevelDataAndDimensions(
                                 var41, var5xx.dataConfiguration(), var6xxx, var5xx.datapackWorldgen()
                              );
                              return new WorldLoader.DataLoadOutput<>(var12xx.worldData(), var12xx.dimensions().dimensionsRegistryAccess());
                           } else {
                              LOGGER.info("No existing world data, creating new world");
                              LevelSettings var7xx;
                              WorldOptions var8xx;
                              WorldDimensions var9xx;
                              if (var17.has(var4)) {
                                 var7xx = MinecraftServer.DEMO_SETTINGS;
                                 var8xx = WorldOptions.DEMO_OPTIONS;
                                 var9xx = WorldPresets.createNormalWorldDimensions(var5xx.datapackWorldgen());
                              } else {
                                 DedicatedServerProperties var10xx = var20.getProperties();
                                 var7xx = new LevelSettings(
                                    var10xx.levelName,
                                    var10xx.gamemode,
                                    var10xx.hardcore,
                                    var10xx.difficulty,
                                    false,
                                    new GameRules(),
                                    var5xx.dataConfiguration()
                                 );
                                 var8xx = var17.has(var5) ? var10xx.worldOptions.withBonusChest(true) : var10xx.worldOptions;
                                 var9xx = var10xx.createDimensions(var5xx.datapackWorldgen());
                              }
         
                              WorldDimensions.Complete var13xx = var9xx.bake(var6xxx);
                              Lifecycle var11xx = var13xx.lifecycle().add(var5xx.datapackWorldgen().allRegistriesLifecycle());
                              return new WorldLoader.DataLoadOutput<>(
                                 new PrimaryLevelData(var7xx, var8xx, var13xx.specialWorldProperty(), var11xx), var13xx.dimensionsRegistryAccess()
                              );
                           }
                        },
                        WorldStem::new,
                        Util.backgroundExecutor(),
                        var6x
                     )
               )
               .get();
         } catch (Exception var37) {
            LOGGER.warn(
               "Failed to load datapacks, can't proceed with server load. You can either fix your datapacks or reset to vanilla with --safeMode", var37
            );
            return;
         }

         RegistryAccess.Frozen var43 = var32.registries().compositeAccess();
         if (var17.has(var6)) {
            forceUpgrade(var27, DataFixers.getDataFixer(), var17.has(var7), () -> true, var43.registryOrThrow(Registries.LEVEL_STEM));
         }

         WorldData var34 = var32.worldData();
         var27.saveDataTag(var43, var34);
         final DedicatedServer var35 = MinecraftServer.spin(
            var11x -> {
               DedicatedServer var12xx = new DedicatedServer(
                  var11x, var27, var42, var32, var20, DataFixers.getDataFixer(), var24, LoggerChunkProgressListener::new
               );
               var12xx.setPort(var17.valueOf(var12));
               var12xx.setDemo(var17.has(var4));
               var12xx.setId((String)var17.valueOf(var13));
               boolean var13xx = !var17.has(var2) && !var17.valuesOf(var16).contains("nogui");
               if (var13xx && !GraphicsEnvironment.isHeadless()) {
                  var12xx.showGui();
               }
   
               return var12xx;
            }
         );
         Thread var36 = new Thread("Server Shutdown Thread") {
            @Override
            public void run() {
               var35.halt(true);
            }
         };
         var36.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
         Runtime.getRuntime().addShutdownHook(var36);
      } catch (Exception var40) {
         LOGGER.error(LogUtils.FATAL_MARKER, "Failed to start the minecraft server", var40);
      }
   }

   private static void writePidFile(Path var0) {
      try {
         long var1 = ProcessHandle.current().pid();
         Files.writeString(var0, Long.toString(var1));
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   private static WorldLoader.InitConfig loadOrCreateConfig(DedicatedServerProperties var0, @Nullable Dynamic<?> var1, boolean var2, PackRepository var3) {
      boolean var4;
      WorldDataConfiguration var5;
      if (var1 != null) {
         WorldDataConfiguration var6 = LevelStorageSource.readDataConfig(var1);
         var4 = false;
         var5 = var6;
      } else {
         var4 = true;
         var5 = new WorldDataConfiguration(var0.initialDataPackConfiguration, FeatureFlags.DEFAULT_FLAGS);
      }

      WorldLoader.PackConfig var7 = new WorldLoader.PackConfig(var3, var5, var2, var4);
      return new WorldLoader.InitConfig(var7, Commands.CommandSelection.DEDICATED, var0.functionPermissionLevel);
   }

   private static void forceUpgrade(LevelStorageSource.LevelStorageAccess var0, DataFixer var1, boolean var2, BooleanSupplier var3, Registry<LevelStem> var4) {
      LOGGER.info("Forcing world upgrade!");
      WorldUpgrader var5 = new WorldUpgrader(var0, var1, var4, var2);
      Component var6 = null;

      while(!var5.isFinished()) {
         Component var7 = var5.getStatus();
         if (var6 != var7) {
            var6 = var7;
            LOGGER.info(var5.getStatus().getString());
         }

         int var8 = var5.getTotalChunks();
         if (var8 > 0) {
            int var9 = var5.getConverted() + var5.getSkipped();
            LOGGER.info("{}% completed ({} / {} chunks)...", new Object[]{Mth.floor((float)var9 / (float)var8 * 100.0F), var9, var8});
         }

         if (!var3.getAsBoolean()) {
            var5.cancel();
         } else {
            try {
               Thread.sleep(1000L);
            } catch (InterruptedException var10) {
            }
         }
      }
   }
}
