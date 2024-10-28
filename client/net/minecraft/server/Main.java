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
import net.minecraft.world.level.chunk.storage.RegionFileVersion;
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
      OptionSpecBuilder var8 = var1.accepts("recreateRegionFiles");
      OptionSpecBuilder var9 = var1.accepts("safeMode", "Loads level with vanilla datapack only");
      AbstractOptionSpec var10 = var1.accepts("help").forHelp();
      ArgumentAcceptingOptionSpec var11 = var1.accepts("universe").withRequiredArg().defaultsTo(".", new String[0]);
      ArgumentAcceptingOptionSpec var12 = var1.accepts("world").withRequiredArg();
      ArgumentAcceptingOptionSpec var13 = var1.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(-1, new Integer[0]);
      ArgumentAcceptingOptionSpec var14 = var1.accepts("serverId").withRequiredArg();
      OptionSpecBuilder var15 = var1.accepts("jfrProfile");
      ArgumentAcceptingOptionSpec var16 = var1.accepts("pidFile").withRequiredArg().withValuesConvertedBy(new PathConverter(new PathProperties[0]));
      NonOptionArgumentSpec var17 = var1.nonOptions();

      try {
         OptionSet var18 = var1.parse(var0);
         if (var18.has(var10)) {
            var1.printHelpOn(System.err);
            return;
         }

         Path var19 = (Path)var18.valueOf(var16);
         if (var19 != null) {
            writePidFile(var19);
         }

         CrashReport.preload();
         if (var18.has(var15)) {
            JvmProfiler.INSTANCE.start(Environment.SERVER);
         }

         Bootstrap.bootStrap();
         Bootstrap.validate();
         Util.startTimerHackThread();
         Path var20 = Paths.get("server.properties");
         DedicatedServerSettings var21 = new DedicatedServerSettings(var20);
         var21.forceSave();
         RegionFileVersion.configure(var21.getProperties().regionFileComression);
         Path var22 = Paths.get("eula.txt");
         Eula var23 = new Eula(var22);
         if (var18.has(var3)) {
            LOGGER.info("Initialized '{}' and '{}'", var20.toAbsolutePath(), var22.toAbsolutePath());
            return;
         }

         if (!var23.hasAgreedToEULA()) {
            LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
            return;
         }

         File var24 = new File((String)var18.valueOf(var11));
         Services var25 = Services.create(new YggdrasilAuthenticationService(Proxy.NO_PROXY), var24);
         String var26 = (String)Optional.ofNullable((String)var18.valueOf(var12)).orElse(var21.getProperties().levelName);
         LevelStorageSource var27 = LevelStorageSource.createDefault(var24.toPath());
         LevelStorageSource.LevelStorageAccess var28 = var27.validateAndCreateAccess(var26);
         Dynamic var29;
         if (var28.hasWorldData()) {
            LevelSummary var30;
            try {
               var29 = var28.getDataTag();
               var30 = var28.getSummary(var29);
            } catch (NbtException | ReportedNbtException | IOException var41) {
               LevelStorageSource.LevelDirectory var32 = var28.getLevelDirectory();
               LOGGER.warn("Failed to load world data from {}", var32.dataFile(), var41);
               LOGGER.info("Attempting to use fallback");

               try {
                  var29 = var28.getDataTagFallback();
                  var30 = var28.getSummary(var29);
               } catch (NbtException | ReportedNbtException | IOException var40) {
                  LOGGER.error("Failed to load world data from {}", var32.oldDataFile(), var40);
                  LOGGER.error("Failed to load world data from {} and {}. World files may be corrupted. Shutting down.", var32.dataFile(), var32.oldDataFile());
                  return;
               }

               var28.restoreLevelDataFromOld();
            }

            if (var30.requiresManualConversion()) {
               LOGGER.info("This world must be opened in an older version (like 1.6.4) to be safely converted");
               return;
            }

            if (!var30.isCompatible()) {
               LOGGER.info("This world was created by an incompatible version.");
               return;
            }
         } else {
            var29 = null;
         }

         Dynamic var43 = var29;
         boolean var31 = var18.has(var9);
         if (var31) {
            LOGGER.warn("Safe mode active, only vanilla datapack will be loaded");
         }

         PackRepository var44 = ServerPacksSource.createPackRepository(var28);

         WorldStem var33;
         try {
            WorldLoader.InitConfig var34 = loadOrCreateConfig(var21.getProperties(), var43, var31, var44);
            var33 = (WorldStem)Util.blockUntilDone((var6x) -> {
               return WorldLoader.load(var34, (var5x) -> {
                  Registry var6 = var5x.datapackDimensions().registryOrThrow(Registries.LEVEL_STEM);
                  if (var43 != null) {
                     LevelDataAndDimensions var13 = LevelStorageSource.getLevelDataAndDimensions(var43, var5x.dataConfiguration(), var6, var5x.datapackWorldgen());
                     return new WorldLoader.DataLoadOutput(var13.worldData(), var13.dimensions().dimensionsRegistryAccess());
                  } else {
                     LOGGER.info("No existing world data, creating new world");
                     LevelSettings var7;
                     WorldOptions var8;
                     WorldDimensions var9;
                     if (var18.has(var4)) {
                        var7 = MinecraftServer.DEMO_SETTINGS;
                        var8 = WorldOptions.DEMO_OPTIONS;
                        var9 = WorldPresets.createNormalWorldDimensions(var5x.datapackWorldgen());
                     } else {
                        DedicatedServerProperties var10 = var21.getProperties();
                        var7 = new LevelSettings(var10.levelName, var10.gamemode, var10.hardcore, var10.difficulty, false, new GameRules(), var5x.dataConfiguration());
                        var8 = var18.has(var5) ? var10.worldOptions.withBonusChest(true) : var10.worldOptions;
                        var9 = var10.createDimensions(var5x.datapackWorldgen());
                     }

                     WorldDimensions.Complete var12 = var9.bake(var6);
                     Lifecycle var11 = var12.lifecycle().add(var5x.datapackWorldgen().allRegistriesLifecycle());
                     return new WorldLoader.DataLoadOutput(new PrimaryLevelData(var7, var8, var12.specialWorldProperty(), var11), var12.dimensionsRegistryAccess());
                  }
               }, WorldStem::new, Util.backgroundExecutor(), var6x);
            }).get();
         } catch (Exception var39) {
            LOGGER.warn("Failed to load datapacks, can't proceed with server load. You can either fix your datapacks or reset to vanilla with --safeMode", var39);
            return;
         }

         RegistryAccess.Frozen var45 = var33.registries().compositeAccess();
         boolean var35 = var18.has(var8);
         if (var18.has(var6) || var35) {
            forceUpgrade(var28, DataFixers.getDataFixer(), var18.has(var7), () -> {
               return true;
            }, var45, var35);
         }

         WorldData var36 = var33.worldData();
         var28.saveDataTag(var45, var36);
         final DedicatedServer var37 = (DedicatedServer)MinecraftServer.spin((var11x) -> {
            DedicatedServer var12 = new DedicatedServer(var11x, var28, var44, var33, var21, DataFixers.getDataFixer(), var25, LoggerChunkProgressListener::createFromGameruleRadius);
            var12.setPort((Integer)var18.valueOf(var13));
            var12.setDemo(var18.has(var4));
            var12.setId((String)var18.valueOf(var14));
            boolean var13x = !var18.has(var2) && !var18.valuesOf(var17).contains("nogui");
            if (var13x && !GraphicsEnvironment.isHeadless()) {
               var12.showGui();
            }

            return var12;
         });
         Thread var38 = new Thread("Server Shutdown Thread") {
            public void run() {
               var37.halt(true);
            }
         };
         var38.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
         Runtime.getRuntime().addShutdownHook(var38);
      } catch (Exception var42) {
         LOGGER.error(LogUtils.FATAL_MARKER, "Failed to start the minecraft server", var42);
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

   private static void forceUpgrade(LevelStorageSource.LevelStorageAccess var0, DataFixer var1, boolean var2, BooleanSupplier var3, RegistryAccess var4, boolean var5) {
      LOGGER.info("Forcing world upgrade!");
      WorldUpgrader var6 = new WorldUpgrader(var0, var1, var4, var2, var5);
      Component var7 = null;

      while(!var6.isFinished()) {
         Component var8 = var6.getStatus();
         if (var7 != var8) {
            var7 = var8;
            LOGGER.info(var6.getStatus().getString());
         }

         int var9 = var6.getTotalChunks();
         if (var9 > 0) {
            int var10 = var6.getConverted() + var6.getSkipped();
            LOGGER.info("{}% completed ({} / {} chunks)...", new Object[]{Mth.floor((float)var10 / (float)var9 * 100.0F), var10, var9});
         }

         if (!var3.getAsBoolean()) {
            var6.cancel();
         } else {
            try {
               Thread.sleep(1000L);
            } catch (InterruptedException var11) {
            }
         }
      }

   }
}
