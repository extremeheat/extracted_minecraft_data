package net.minecraft.client.gui.screens.worldselection;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.DatapackLoadFailureScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.Commands;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.slf4j.Logger;

public class WorldOpenFlows {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Minecraft minecraft;
   private final LevelStorageSource levelSource;

   public WorldOpenFlows(Minecraft var1, LevelStorageSource var2) {
      super();
      this.minecraft = var1;
      this.levelSource = var2;
   }

   public void loadLevel(Screen var1, String var2) {
      this.doLoadLevel(var1, var2, false, true);
   }

   public void createFreshLevel(String var1, LevelSettings var2, WorldOptions var3, Function<RegistryAccess, WorldDimensions> var4) {
      LevelStorageSource.LevelStorageAccess var5 = this.createWorldAccess(var1);
      if (var5 != null) {
         PackRepository var6 = ServerPacksSource.createPackRepository(var5);
         WorldDataConfiguration var7 = var2.getDataConfiguration();

         try {
            WorldLoader.PackConfig var8 = new WorldLoader.PackConfig(var6, var7, false, false);
            WorldStem var9 = this.loadWorldDataBlocking(
               var8,
               var3x -> {
                  WorldDimensions.Complete var4x = ((WorldDimensions)var4.apply(var3x.datapackWorldgen()))
                     .bake(var3x.datapackDimensions().registryOrThrow(Registries.LEVEL_STEM));
                  return new WorldLoader.DataLoadOutput<>(
                     new PrimaryLevelData(var2, var3, var4x.specialWorldProperty(), var4x.lifecycle()), var4x.dimensionsRegistryAccess()
                  );
               },
               WorldStem::new
            );
            this.minecraft.doWorldLoad(var1, var5, var6, var9, true);
         } catch (Exception var10) {
            LOGGER.warn("Failed to load datapacks, can't proceed with server load", var10);
            safeCloseAccess(var5, var1);
         }
      }
   }

   @Nullable
   private LevelStorageSource.LevelStorageAccess createWorldAccess(String var1) {
      try {
         return this.levelSource.createAccess(var1);
      } catch (IOException var3) {
         LOGGER.warn("Failed to read level {} data", var1, var3);
         SystemToast.onWorldAccessFailure(this.minecraft, var1);
         this.minecraft.setScreen(null);
         return null;
      }
   }

   public void createLevelFromExistingSettings(
      LevelStorageSource.LevelStorageAccess var1, ReloadableServerResources var2, LayeredRegistryAccess<RegistryLayer> var3, WorldData var4
   ) {
      PackRepository var5 = ServerPacksSource.createPackRepository(var1);
      CloseableResourceManager var6 = (CloseableResourceManager)new WorldLoader.PackConfig(var5, var4.getDataConfiguration(), false, false)
         .createResourceManager()
         .getSecond();
      this.minecraft.doWorldLoad(var1.getLevelId(), var1, var5, new WorldStem(var6, var2, var3, var4), true);
   }

   private WorldStem loadWorldStem(LevelStorageSource.LevelStorageAccess var1, boolean var2, PackRepository var3) throws Exception {
      WorldLoader.PackConfig var4 = this.getPackConfigFromLevelData(var1, var2, var3);
      return this.loadWorldDataBlocking(var4, var1x -> {
         RegistryOps var2x = RegistryOps.create(NbtOps.INSTANCE, var1x.datapackWorldgen());
         Registry var3x = var1x.datapackDimensions().registryOrThrow(Registries.LEVEL_STEM);
         Pair var4x = var1.getDataTag(var2x, var1x.dataConfiguration(), var3x, var1x.datapackWorldgen().allRegistriesLifecycle());
         if (var4x == null) {
            throw new IllegalStateException("Failed to load world");
         } else {
            return new WorldLoader.DataLoadOutput<>((WorldData)var4x.getFirst(), ((WorldDimensions.Complete)var4x.getSecond()).dimensionsRegistryAccess());
         }
      }, WorldStem::new);
   }

   public Pair<LevelSettings, WorldCreationContext> recreateWorldData(LevelStorageSource.LevelStorageAccess var1) throws Exception {
      PackRepository var2 = ServerPacksSource.createPackRepository(var1);
      WorldLoader.PackConfig var3 = this.getPackConfigFromLevelData(var1, false, var2);
      return this.loadWorldDataBlocking(
         var3,
         var1x -> {
            RegistryOps var2x = RegistryOps.create(NbtOps.INSTANCE, var1x.datapackWorldgen());
            Registry var3x = new MappedRegistry<>(Registries.LEVEL_STEM, Lifecycle.stable()).freeze();
            Pair var4 = var1.getDataTag(var2x, var1x.dataConfiguration(), var3x, var1x.datapackWorldgen().allRegistriesLifecycle());
            if (var4 == null) {
               throw new IllegalStateException("Failed to load world");
            } else {
               return new WorldLoader.DataLoadOutput<>(
                  new 1Data(
                     ((WorldData)var4.getFirst()).getLevelSettings(),
                     ((WorldData)var4.getFirst()).worldGenOptions(),
                     ((WorldDimensions.Complete)var4.getSecond()).dimensions()
                  ),
                  var1x.datapackDimensions()
               );
            }
         },
         (var0, var1x, var2x, var3x) -> {
            var0.close();
            return Pair.of(
               var3x.levelSettings,
               new WorldCreationContext(var3x.options, new WorldDimensions(var3x.existingDimensions), var2x, var1x, var3x.levelSettings.getDataConfiguration())
            );
         }
      );

      record 1Data(LevelSettings a, WorldOptions b, Registry<LevelStem> c) {
         final LevelSettings levelSettings;
         final WorldOptions options;
         final Registry<LevelStem> existingDimensions;

         _Data/* $QF was: 1Data*/(LevelSettings var1, WorldOptions var2, Registry<LevelStem> var3) {
            super();
            this.levelSettings = var1;
            this.options = var2;
            this.existingDimensions = var3;
         }
      }

   }

   private WorldLoader.PackConfig getPackConfigFromLevelData(LevelStorageSource.LevelStorageAccess var1, boolean var2, PackRepository var3) {
      WorldDataConfiguration var4 = var1.getDataConfiguration();
      if (var4 == null) {
         throw new IllegalStateException("Failed to load data pack config");
      } else {
         return new WorldLoader.PackConfig(var3, var4, var2, false);
      }
   }

   public WorldStem loadWorldStem(LevelStorageSource.LevelStorageAccess var1, boolean var2) throws Exception {
      PackRepository var3 = ServerPacksSource.createPackRepository(var1);
      return this.loadWorldStem(var1, var2, var3);
   }

   private <D, R> R loadWorldDataBlocking(WorldLoader.PackConfig var1, WorldLoader.WorldDataSupplier<D> var2, WorldLoader.ResultFactory<D, R> var3) throws Exception {
      WorldLoader.InitConfig var4 = new WorldLoader.InitConfig(var1, Commands.CommandSelection.INTEGRATED, 2);
      CompletableFuture var5 = WorldLoader.load(var4, var2, var3, Util.backgroundExecutor(), this.minecraft);
      this.minecraft.managedBlock(var5::isDone);
      return (R)var5.get();
   }

   private void doLoadLevel(Screen var1, String var2, boolean var3, boolean var4) {
      LevelStorageSource.LevelStorageAccess var5 = this.createWorldAccess(var2);
      if (var5 != null) {
         PackRepository var6 = ServerPacksSource.createPackRepository(var5);

         WorldStem var7;
         try {
            var7 = this.loadWorldStem(var5, var3, var6);
         } catch (Exception var11) {
            LOGGER.warn("Failed to load level data or datapacks, can't proceed with server load", var11);
            this.minecraft.setScreen(new DatapackLoadFailureScreen(() -> this.doLoadLevel(var1, var2, true, var4)));
            safeCloseAccess(var5, var2);
            return;
         }

         WorldData var8 = var7.worldData();
         boolean var9 = var8.worldGenOptions().isOldCustomizedWorld();
         boolean var10 = var8.worldGenSettingsLifecycle() != Lifecycle.stable();
         if (!var4 || !var9 && !var10) {
            this.minecraft.getDownloadedPackSource().loadBundledResourcePack(var5).thenApply(var0 -> true).exceptionallyComposeAsync(var1x -> {
               LOGGER.warn("Failed to load pack: ", var1x);
               return this.promptBundledPackLoadFailure();
            }, this.minecraft).thenAcceptAsync(var6x -> {
               if (var6x) {
                  this.minecraft.doWorldLoad(var2, var5, var6, var7, false);
               } else {
                  var7.close();
                  safeCloseAccess(var5, var2);
                  this.minecraft.getDownloadedPackSource().clearServerPack().thenRunAsync(() -> this.minecraft.setScreen(var1), this.minecraft);
               }
            }, this.minecraft).exceptionally(var1x -> {
               this.minecraft.delayCrash(CrashReport.forThrowable(var1x, "Load world"));
               return null;
            });
         } else {
            this.askForBackup(var1, var2, var9, () -> this.doLoadLevel(var1, var2, var3, false));
            var7.close();
            safeCloseAccess(var5, var2);
         }
      }
   }

   private CompletableFuture<Boolean> promptBundledPackLoadFailure() {
      CompletableFuture var1 = new CompletableFuture();
      this.minecraft
         .setScreen(
            new ConfirmScreen(
               var1::complete,
               Component.translatable("multiplayer.texturePrompt.failure.line1"),
               Component.translatable("multiplayer.texturePrompt.failure.line2"),
               CommonComponents.GUI_PROCEED,
               CommonComponents.GUI_CANCEL
            )
         );
      return var1;
   }

   private static void safeCloseAccess(LevelStorageSource.LevelStorageAccess var0, String var1) {
      try {
         var0.close();
      } catch (IOException var3) {
         LOGGER.warn("Failed to unlock access to level {}", var1, var3);
      }
   }

   private void askForBackup(Screen var1, String var2, boolean var3, Runnable var4) {
      MutableComponent var5;
      MutableComponent var6;
      if (var3) {
         var5 = Component.translatable("selectWorld.backupQuestion.customized");
         var6 = Component.translatable("selectWorld.backupWarning.customized");
      } else {
         var5 = Component.translatable("selectWorld.backupQuestion.experimental");
         var6 = Component.translatable("selectWorld.backupWarning.experimental");
      }

      this.minecraft.setScreen(new BackupConfirmScreen(var1, (var3x, var4x) -> {
         if (var3x) {
            EditWorldScreen.makeBackupAndShowToast(this.levelSource, var2);
         }

         var4.run();
      }, var5, var6, false));
   }

   public static void confirmWorldCreation(Minecraft var0, CreateWorldScreen var1, Lifecycle var2, Runnable var3) {
      BooleanConsumer var4 = var3x -> {
         if (var3x) {
            var3.run();
         } else {
            var0.setScreen(var1);
         }
      };
      if (var2 == Lifecycle.stable()) {
         var3.run();
      } else if (var2 == Lifecycle.experimental()) {
         var0.setScreen(
            new ConfirmScreen(
               var4, Component.translatable("selectWorld.warning.experimental.title"), Component.translatable("selectWorld.warning.experimental.question")
            )
         );
      } else {
         var0.setScreen(
            new ConfirmScreen(
               var4, Component.translatable("selectWorld.warning.deprecated.title"), Component.translatable("selectWorld.warning.deprecated.question")
            )
         );
      }
   }
}
