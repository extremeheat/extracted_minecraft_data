package net.minecraft.server;

import com.google.common.collect.ImmutableSet;
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
import net.minecraft.Util;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.server.level.progress.LoggerChunkProgressListener;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.worldupdate.WorldUpgrader;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
   private static final Logger LOGGER = LogManager.getLogger();

   public Main() {
      super();
   }

   public static void main(String[] var0) {
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
      NonOptionArgumentSpec var15 = var1.nonOptions();

      try {
         OptionSet var16 = var1.parse(var0);
         if (var16.has(var9)) {
            var1.printHelpOn(System.err);
            return;
         }

         CrashReport.preload();
         Bootstrap.bootStrap();
         Bootstrap.validate();
         Util.startTimerHackThread();
         RegistryAccess.RegistryHolder var17 = RegistryAccess.builtin();
         Path var18 = Paths.get("server.properties");
         DedicatedServerSettings var19 = new DedicatedServerSettings(var17, var18);
         var19.forceSave();
         Path var20 = Paths.get("eula.txt");
         Eula var21 = new Eula(var20);
         if (var16.has(var3)) {
            LOGGER.info("Initialized '{}' and '{}'", var18.toAbsolutePath(), var20.toAbsolutePath());
            return;
         }

         if (!var21.hasAgreedToEULA()) {
            LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
            return;
         }

         File var22 = new File((String)var16.valueOf(var11));
         YggdrasilAuthenticationService var23 = new YggdrasilAuthenticationService(Proxy.NO_PROXY);
         MinecraftSessionService var24 = var23.createMinecraftSessionService();
         GameProfileRepository var25 = var23.createProfileRepository();
         GameProfileCache var26 = new GameProfileCache(var25, new File(var22, MinecraftServer.USERID_CACHE_FILE.getName()));
         String var27 = (String)Optional.ofNullable(var16.valueOf(var12)).orElse(var19.getProperties().levelName);
         LevelStorageSource var28 = LevelStorageSource.createDefault(var22.toPath());
         LevelStorageSource.LevelStorageAccess var29 = var28.createAccess(var27);
         MinecraftServer.convertFromRegionFormatIfNeeded(var29);
         DataPackConfig var30 = var29.getDataPacks();
         boolean var31 = var16.has(var8);
         if (var31) {
            LOGGER.warn("Safe mode active, only vanilla datapack will be loaded");
         }

         PackRepository var32 = new PackRepository(new RepositorySource[]{new ServerPacksSource(), new FolderRepositorySource(var29.getLevelPath(LevelResource.DATAPACK_DIR).toFile(), PackSource.WORLD)});
         DataPackConfig var33 = MinecraftServer.configurePackRepository(var32, var30 == null ? DataPackConfig.DEFAULT : var30, var31);
         CompletableFuture var34 = ServerResources.loadResources(var32.openAllSelected(), Commands.CommandSelection.DEDICATED, var19.getProperties().functionPermissionLevel, Util.backgroundExecutor(), Runnable::run);

         ServerResources var35;
         try {
            var35 = (ServerResources)var34.get();
         } catch (Exception var41) {
            LOGGER.warn("Failed to load datapacks, can't proceed with server load. You can either fix your datapacks or reset to vanilla with --safeMode", var41);
            var32.close();
            return;
         }

         var35.updateGlobals();
         RegistryReadOps var36 = RegistryReadOps.create(NbtOps.INSTANCE, (ResourceManager)var35.getResourceManager(), var17);
         Object var37 = var29.getDataTag(var36, var33);
         if (var37 == null) {
            LevelSettings var38;
            WorldGenSettings var39;
            if (var16.has(var4)) {
               var38 = MinecraftServer.DEMO_SETTINGS;
               var39 = WorldGenSettings.demoSettings(var17);
            } else {
               DedicatedServerProperties var40 = var19.getProperties();
               var38 = new LevelSettings(var40.levelName, var40.gamemode, var40.hardcore, var40.difficulty, false, new GameRules(), var33);
               var39 = var16.has(var5) ? var40.worldGenSettings.withBonusChest() : var40.worldGenSettings;
            }

            var37 = new PrimaryLevelData(var38, var39, Lifecycle.stable());
         }

         if (var16.has(var6)) {
            forceUpgrade(var29, DataFixers.getDataFixer(), var16.has(var7), () -> {
               return true;
            }, ((WorldData)var37).worldGenSettings().levels());
         }

         var29.saveDataTag(var17, (WorldData)var37);
         final DedicatedServer var43 = (DedicatedServer)MinecraftServer.spin((var16x) -> {
            DedicatedServer var17x = new DedicatedServer(var16x, var17, var29, var32, var35, var37, var19, DataFixers.getDataFixer(), var24, var25, var26, LoggerChunkProgressListener::new);
            var17x.setSingleplayerName((String)var16.valueOf(var10));
            var17x.setPort((Integer)var16.valueOf(var13));
            var17x.setDemo(var16.has(var4));
            var17x.setId((String)var16.valueOf(var14));
            boolean var18 = !var16.has(var2) && !var16.valuesOf(var15).contains("nogui");
            if (var18 && !GraphicsEnvironment.isHeadless()) {
               var17x.showGui();
            }

            return var17x;
         });
         Thread var44 = new Thread("Server Shutdown Thread") {
            public void run() {
               var43.halt(true);
            }
         };
         var44.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
         Runtime.getRuntime().addShutdownHook(var44);
      } catch (Exception var42) {
         LOGGER.fatal("Failed to start the minecraft server", var42);
      }

   }

   private static void forceUpgrade(LevelStorageSource.LevelStorageAccess var0, DataFixer var1, boolean var2, BooleanSupplier var3, ImmutableSet<ResourceKey<Level>> var4) {
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
}
