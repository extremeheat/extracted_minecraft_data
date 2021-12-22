package net.minecraft.server;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Lifecycle;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.net.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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
import net.minecraft.resources.RegistryReadOps;
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
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.profiling.jfr.Environment;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.worldupdate.WorldUpgrader;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
   private static final Logger LOGGER;

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
         RegistryAccess.RegistryHolder var18 = RegistryAccess.builtin();
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

         File var23 = new File((String)var17.valueOf(var11));
         YggdrasilAuthenticationService var24 = new YggdrasilAuthenticationService(Proxy.NO_PROXY);
         MinecraftSessionService var25 = var24.createMinecraftSessionService();
         GameProfileRepository var26 = var24.createProfileRepository();
         GameProfileCache var27 = new GameProfileCache(var26, new File(var23, MinecraftServer.USERID_CACHE_FILE.getName()));
         String var28 = (String)Optional.ofNullable((String)var17.valueOf(var12)).orElse(var20.getProperties().levelName);
         LevelStorageSource var29 = LevelStorageSource.createDefault(var23.toPath());
         LevelStorageSource.LevelStorageAccess var30 = var29.createAccess(var28);
         LevelSummary var31 = var30.getSummary();
         if (var31 != null) {
            if (var31.requiresManualConversion()) {
               LOGGER.info("This world must be opened in an older version (like 1.6.4) to be safely converted");
               return;
            }

            if (!var31.isCompatible()) {
               LOGGER.info("This world was created by an incompatible version.");
               return;
            }
         }

         DataPackConfig var32 = var30.getDataPacks();
         boolean var33 = var17.has(var8);
         if (var33) {
            LOGGER.warn("Safe mode active, only vanilla datapack will be loaded");
         }

         PackRepository var34 = new PackRepository(PackType.SERVER_DATA, new RepositorySource[]{new ServerPacksSource(), new FolderRepositorySource(var30.getLevelPath(LevelResource.DATAPACK_DIR).toFile(), PackSource.WORLD)});
         DataPackConfig var35 = MinecraftServer.configurePackRepository(var34, var32 == null ? DataPackConfig.DEFAULT : var32, var33);
         CompletableFuture var36 = ServerResources.loadResources(var34.openAllSelected(), var18, Commands.CommandSelection.DEDICATED, var20.getProperties().functionPermissionLevel, Util.backgroundExecutor(), Runnable::run);

         ServerResources var37;
         try {
            var37 = (ServerResources)var36.get();
         } catch (Exception var43) {
            LOGGER.warn("Failed to load datapacks, can't proceed with server load. You can either fix your datapacks or reset to vanilla with --safeMode", var43);
            var34.close();
            return;
         }

         var37.updateGlobals();
         RegistryReadOps var38 = RegistryReadOps.createAndLoad(NbtOps.INSTANCE, (ResourceManager)var37.getResourceManager(), var18);
         var20.getProperties().getWorldGenSettings(var18);
         Object var39 = var30.getDataTag(var38, var35);
         if (var39 == null) {
            LevelSettings var40;
            WorldGenSettings var41;
            if (var17.has(var4)) {
               var40 = MinecraftServer.DEMO_SETTINGS;
               var41 = WorldGenSettings.demoSettings(var18);
            } else {
               DedicatedServerProperties var42 = var20.getProperties();
               var40 = new LevelSettings(var42.levelName, var42.gamemode, var42.hardcore, var42.difficulty, false, new GameRules(), var35);
               var41 = var17.has(var5) ? var42.getWorldGenSettings(var18).withBonusChest() : var42.getWorldGenSettings(var18);
            }

            var39 = new PrimaryLevelData(var40, var41, Lifecycle.stable());
         }

         if (var17.has(var6)) {
            forceUpgrade(var30, DataFixers.getDataFixer(), var17.has(var7), () -> {
               return true;
            }, ((WorldData)var39).worldGenSettings());
         }

         var30.saveDataTag(var18, (WorldData)var39);
         final DedicatedServer var46 = (DedicatedServer)MinecraftServer.spin((var16x) -> {
            DedicatedServer var17x = new DedicatedServer(var16x, var18, var30, var34, var37, var39, var20, DataFixers.getDataFixer(), var25, var26, var27, LoggerChunkProgressListener::new);
            var17x.setSingleplayerName((String)var17.valueOf(var10));
            var17x.setPort((Integer)var17.valueOf(var13));
            var17x.setDemo(var17.has(var4));
            var17x.setId((String)var17.valueOf(var14));
            boolean var18x = !var17.has(var2) && !var17.valuesOf(var16).contains("nogui");
            if (var18x && !GraphicsEnvironment.isHeadless()) {
               var17x.showGui();
            }

            return var17x;
         });
         Thread var45 = new Thread("Server Shutdown Thread") {
            public void run() {
               var46.halt(true);
            }
         };
         var45.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
         Runtime.getRuntime().addShutdownHook(var45);
      } catch (Exception var44) {
         LOGGER.fatal("Failed to start the minecraft server", var44);
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
            LOGGER.info("{}% completed ({} / {} chunks)...", Mth.floor((float)var9 / (float)var8 * 100.0F), var9, var8);
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

   static {
      Util.preInitLog4j();
      LOGGER = LogManager.getLogger();
   }
}
