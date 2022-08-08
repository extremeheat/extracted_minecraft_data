package net.minecraft.server;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.net.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;
import net.minecraft.CrashReport;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.server.level.progress.LoggerChunkProgressListener;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.profiling.jfr.Environment;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.worldupdate.WorldUpgrader;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
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
      NonOptionArgumentSpec var16 = var1.nonOptions();

      try {
         OptionSet var17 = var1.parse(var0);
         if (var17.has(var9)) {
            var1.printHelpOn(System.err);
            return;
         }

         CrashReport.preload();
         if (var17.has(var15)) {
            JvmProfiler.INSTANCE.start(Environment.SERVER);
         }

         Bootstrap.bootStrap();
         Bootstrap.validate();
         Util.startTimerHackThread();
         Path var18 = Paths.get("server.properties");
         DedicatedServerSettings var19 = new DedicatedServerSettings(var18);
         var19.forceSave();
         Path var20 = Paths.get("eula.txt");
         Eula var21 = new Eula(var20);
         if (var17.has(var3)) {
            LOGGER.info("Initialized '{}' and '{}'", var18.toAbsolutePath(), var20.toAbsolutePath());
            return;
         }

         if (!var21.hasAgreedToEULA()) {
            LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
            return;
         }

         File var22 = new File((String)var17.valueOf(var11));
         Services var23 = Services.create(new YggdrasilAuthenticationService(Proxy.NO_PROXY), var22);
         String var24 = (String)Optional.ofNullable((String)var17.valueOf(var12)).orElse(var19.getProperties().levelName);
         LevelStorageSource var25 = LevelStorageSource.createDefault(var22.toPath());
         LevelStorageSource.LevelStorageAccess var26 = var25.createAccess(var24);
         LevelSummary var27 = var26.getSummary();
         if (var27 != null) {
            if (var27.requiresManualConversion()) {
               LOGGER.info("This world must be opened in an older version (like 1.6.4) to be safely converted");
               return;
            }

            if (!var27.isCompatible()) {
               LOGGER.info("This world was created by an incompatible version.");
               return;
            }
         }

         boolean var28 = var17.has(var8);
         if (var28) {
            LOGGER.warn("Safe mode active, only vanilla datapack will be loaded");
         }

         PackRepository var29 = new PackRepository(PackType.SERVER_DATA, new RepositorySource[]{new ServerPacksSource(), new FolderRepositorySource(var26.getLevelPath(LevelResource.DATAPACK_DIR).toFile(), PackSource.WORLD)});

         WorldStem var30;
         try {
            DataPackConfig var31 = (DataPackConfig)Objects.requireNonNullElse(var26.getDataPacks(), DataPackConfig.DEFAULT);
            WorldLoader.PackConfig var32 = new WorldLoader.PackConfig(var29, var31, var28);
            WorldLoader.InitConfig var33 = new WorldLoader.InitConfig(var32, Commands.CommandSelection.DEDICATED, var19.getProperties().functionPermissionLevel);
            var30 = (WorldStem)Util.blockUntilDone((var6x) -> {
               return WorldStem.load(var33, (var5x, var6) -> {
                  RegistryAccess.Writable var7 = RegistryAccess.builtinCopy();
                  RegistryOps var8 = RegistryOps.createAndLoad(NbtOps.INSTANCE, var7, (ResourceManager)var5x);
                  WorldData var9 = var26.getDataTag(var8, var6, var7.allElementsLifecycle());
                  if (var9 != null) {
                     return Pair.of(var9, var7.freeze());
                  } else {
                     LevelSettings var10;
                     WorldGenSettings var11;
                     if (var17.has(var4)) {
                        var10 = MinecraftServer.DEMO_SETTINGS;
                        var11 = WorldPresets.demoSettings(var7);
                     } else {
                        DedicatedServerProperties var12 = var19.getProperties();
                        var10 = new LevelSettings(var12.levelName, var12.gamemode, var12.hardcore, var12.difficulty, false, new GameRules(), var6);
                        var11 = var17.has(var5) ? var12.getWorldGenSettings(var7).withBonusChest() : var12.getWorldGenSettings(var7);
                     }

                     PrimaryLevelData var13 = new PrimaryLevelData(var10, var11, Lifecycle.stable());
                     return Pair.of(var13, var7.freeze());
                  }
               }, Util.backgroundExecutor(), var6x);
            }).get();
         } catch (Exception var35) {
            LOGGER.warn("Failed to load datapacks, can't proceed with server load. You can either fix your datapacks or reset to vanilla with --safeMode", var35);
            return;
         }

         RegistryAccess.Frozen var37 = var30.registryAccess();
         var19.getProperties().getWorldGenSettings(var37);
         WorldData var38 = var30.worldData();
         if (var17.has(var6)) {
            forceUpgrade(var26, DataFixers.getDataFixer(), var17.has(var7), () -> {
               return true;
            }, var38.worldGenSettings());
         }

         var26.saveDataTag(var37, var38);
         final DedicatedServer var39 = (DedicatedServer)MinecraftServer.spin((var12x) -> {
            DedicatedServer var13x = new DedicatedServer(var12x, var26, var29, var30, var19, DataFixers.getDataFixer(), var23, LoggerChunkProgressListener::new);
            var13x.setSingleplayerProfile(var17.has(var10) ? new GameProfile((UUID)null, (String)var17.valueOf(var10)) : null);
            var13x.setPort((Integer)var17.valueOf(var13));
            var13x.setDemo(var17.has(var4));
            var13x.setId((String)var17.valueOf(var14));
            boolean var14x = !var17.has(var2) && !var17.valuesOf(var16).contains("nogui");
            if (var14x && !GraphicsEnvironment.isHeadless()) {
               var13x.showGui();
            }

            return var13x;
         });
         Thread var34 = new Thread("Server Shutdown Thread") {
            public void run() {
               var39.halt(true);
            }
         };
         var34.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
         Runtime.getRuntime().addShutdownHook(var34);
      } catch (Exception var36) {
         LOGGER.error(LogUtils.FATAL_MARKER, "Failed to start the minecraft server", var36);
      }

   }

   private static void forceUpgrade(LevelStorageSource.LevelStorageAccess var0, DataFixer var1, boolean var2, BooleanSupplier var3, WorldGenSettings var4) {
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
