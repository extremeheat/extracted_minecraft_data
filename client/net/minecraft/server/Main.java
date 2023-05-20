package net.minecraft.server;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
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
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.resources.RegistryOps;
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
import net.minecraft.world.level.storage.LevelResource;
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
      ArgumentAcceptingOptionSpec var10 = var1.accepts("singleplayer").withRequiredArg();
      ArgumentAcceptingOptionSpec var11 = var1.accepts("universe").withRequiredArg().defaultsTo(".", new String[0]);
      ArgumentAcceptingOptionSpec var12 = var1.accepts("world").withRequiredArg();
      ArgumentAcceptingOptionSpec var13 = var1.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(-1, new Integer[0]);
      ArgumentAcceptingOptionSpec var14 = var1.accepts("serverId").withRequiredArg();
      OptionSpecBuilder var15 = var1.accepts("jfrProfile");
      ArgumentAcceptingOptionSpec var16 = var1.accepts("pidFile").withRequiredArg().withValuesConvertedBy(new PathConverter(new PathProperties[0]));
      NonOptionArgumentSpec var17 = var1.nonOptions();

      try {
         OptionSet var18 = var1.parse(var0);
         if (var18.has(var9)) {
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
         LevelStorageSource.LevelStorageAccess var28 = var27.createAccess(var26);
         LevelSummary var29 = var28.getSummary();
         if (var29 != null) {
            if (var29.requiresManualConversion()) {
               LOGGER.info("This world must be opened in an older version (like 1.6.4) to be safely converted");
               return;
            }

            if (!var29.isCompatible()) {
               LOGGER.info("This world was created by an incompatible version.");
               return;
            }
         }

         boolean var30 = var18.has(var8);
         if (var30) {
            LOGGER.warn("Safe mode active, only vanilla datapack will be loaded");
         }

         PackRepository var31 = ServerPacksSource.createPackRepository(var28.getLevelPath(LevelResource.DATAPACK_DIR));

         WorldStem var32;
         try {
            WorldLoader.InitConfig var33 = loadOrCreateConfig(var21.getProperties(), var28, var30, var31);
            var32 = Util.<WorldStem>blockUntilDone(
                  var6x -> WorldLoader.load(
                        var33,
                        var5xx -> {
                           Registry var6xx = var5xx.datapackDimensions().registryOrThrow(Registries.LEVEL_STEM);
                           RegistryOps var7x = RegistryOps.create(NbtOps.INSTANCE, var5xx.datapackWorldgen());
                           Pair var8x = var28.getDataTag(var7x, var5xx.dataConfiguration(), var6xx, var5xx.datapackWorldgen().allRegistriesLifecycle());
                           if (var8x != null) {
                              return new WorldLoader.DataLoadOutput<>(
                                 (WorldData)var8x.getFirst(), ((WorldDimensions.Complete)var8x.getSecond()).dimensionsRegistryAccess()
                              );
                           } else {
                              LevelSettings var9x;
                              WorldOptions var10x;
                              WorldDimensions var11x;
                              if (var18.has(var4)) {
                                 var9x = MinecraftServer.DEMO_SETTINGS;
                                 var10x = WorldOptions.DEMO_OPTIONS;
                                 var11x = WorldPresets.createNormalWorldDimensions(var5xx.datapackWorldgen());
                              } else {
                                 DedicatedServerProperties var12x = var21.getProperties();
                                 var9x = new LevelSettings(
                                    var12x.levelName, var12x.gamemode, var12x.hardcore, var12x.difficulty, false, new GameRules(), var5xx.dataConfiguration()
                                 );
                                 var10x = var18.has(var5) ? var12x.worldOptions.withBonusChest(true) : var12x.worldOptions;
                                 var11x = var12x.createDimensions(var5xx.datapackWorldgen());
                              }
         
                              WorldDimensions.Complete var14x = var11x.bake(var6xx);
                              Lifecycle var13x = var14x.lifecycle().add(var5xx.datapackWorldgen().allRegistriesLifecycle());
                              return new WorldLoader.DataLoadOutput<>(
                                 new PrimaryLevelData(var9x, var10x, var14x.specialWorldProperty(), var13x), var14x.dimensionsRegistryAccess()
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

         RegistryAccess.Frozen var39 = var32.registries().compositeAccess();
         if (var18.has(var6)) {
            forceUpgrade(var28, DataFixers.getDataFixer(), var18.has(var7), () -> true, var39.registryOrThrow(Registries.LEVEL_STEM));
         }

         WorldData var34 = var32.worldData();
         var28.saveDataTag(var39, var34);
         final DedicatedServer var35 = MinecraftServer.spin(
            var12x -> {
               DedicatedServer var13x = new DedicatedServer(
                  var12x, var28, var31, var32, var21, DataFixers.getDataFixer(), var25, LoggerChunkProgressListener::new
               );
               var13x.setSingleplayerProfile(var18.has(var10) ? new GameProfile(null, (String)var18.valueOf(var10)) : null);
               var13x.setPort(var18.valueOf(var13));
               var13x.setDemo(var18.has(var4));
               var13x.setId((String)var18.valueOf(var14));
               boolean var14x = !var18.has(var2) && !var18.valuesOf(var17).contains("nogui");
               if (var14x && !GraphicsEnvironment.isHeadless()) {
                  var13x.showGui();
               }
   
               return var13x;
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
      } catch (Exception var38) {
         LOGGER.error(LogUtils.FATAL_MARKER, "Failed to start the minecraft server", var38);
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

   private static WorldLoader.InitConfig loadOrCreateConfig(
      DedicatedServerProperties var0, LevelStorageSource.LevelStorageAccess var1, boolean var2, PackRepository var3
   ) {
      WorldDataConfiguration var4 = var1.getDataConfiguration();
      WorldDataConfiguration var5;
      boolean var6;
      if (var4 != null) {
         var6 = false;
         var5 = var4;
      } else {
         var6 = true;
         var5 = new WorldDataConfiguration(var0.initialDataPackConfiguration, FeatureFlags.DEFAULT_FLAGS);
      }

      WorldLoader.PackConfig var7 = new WorldLoader.PackConfig(var3, var5, var2, var6);
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
