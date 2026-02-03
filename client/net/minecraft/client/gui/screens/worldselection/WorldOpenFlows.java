package net.minecraft.client.gui.screens.worldselection;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.DatapackLoadFailureScreen;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.NoticeWithLinkScreen;
import net.minecraft.client.gui.screens.RecoverWorldDataScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.server.DownloadedPackSource;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.ReportedNbtException;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.util.MemoryReserve;
import net.minecraft.util.Util;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.gamerules.GameRuleMap;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelDataAndDimensions;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.validation.ContentValidationException;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class WorldOpenFlows {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final UUID WORLD_PACK_ID = UUID.fromString("640a6a92-b6cb-48a0-b391-831586500359");
   private final Minecraft minecraft;
   private final LevelStorageSource levelSource;

   public WorldOpenFlows(final Minecraft minecraft, final LevelStorageSource levelSource) {
      super();
      this.minecraft = minecraft;
      this.levelSource = levelSource;
   }

   public void createFreshLevel(final String levelId, final LevelSettings levelSettings, final WorldOptions options, final Function<HolderLookup.Provider, WorldDimensions> dimensionsProvider, final Screen parentScreen) {
      this.minecraft.setScreenAndShow(new GenericMessageScreen(Component.translatable("selectWorld.data_read")));
      LevelStorageSource.LevelStorageAccess levelSourceAccess = this.createWorldAccess(levelId);
      if (levelSourceAccess != null) {
         PackRepository packRepository = ServerPacksSource.createPackRepository(levelSourceAccess);
         WorldDataConfiguration dataConfiguration = levelSettings.getDataConfiguration();

         try {
            WorldLoader.PackConfig packConfig = new WorldLoader.PackConfig(packRepository, dataConfiguration, false, false);
            WorldStem worldStem = (WorldStem)this.loadWorldDataBlocking(packConfig, (context) -> {
               WorldDimensions.Complete dimensions = ((WorldDimensions)dimensionsProvider.apply(context.datapackWorldgen())).bake(context.datapackDimensions().lookupOrThrow(Registries.LEVEL_STEM));
               return new WorldLoader.DataLoadOutput(new PrimaryLevelData(levelSettings, options, dimensions.specialWorldProperty(), dimensions.lifecycle()), dimensions.dimensionsRegistryAccess());
            }, WorldStem::new);
            this.minecraft.doWorldLoad(levelSourceAccess, packRepository, worldStem, true);
         } catch (Exception e) {
            LOGGER.warn("Failed to load datapacks, can't proceed with server load", e);
            levelSourceAccess.safeClose();
            this.minecraft.setScreen(parentScreen);
         }

      }
   }

   private LevelStorageSource.@Nullable LevelStorageAccess createWorldAccess(final String levelId) {
      try {
         return this.levelSource.validateAndCreateAccess(levelId);
      } catch (IOException e) {
         LOGGER.warn("Failed to read level {} data", levelId, e);
         SystemToast.onWorldAccessFailure(this.minecraft, levelId);
         this.minecraft.setScreen((Screen)null);
         return null;
      } catch (ContentValidationException e) {
         LOGGER.warn("{}", e.getMessage());
         this.minecraft.setScreen(NoticeWithLinkScreen.createWorldSymlinkWarningScreen(() -> this.minecraft.setScreen((Screen)null)));
         return null;
      }
   }

   public void createLevelFromExistingSettings(final LevelStorageSource.LevelStorageAccess levelSourceAccess, final ReloadableServerResources serverResources, final LayeredRegistryAccess<RegistryLayer> registryAccess, final WorldData worldData) {
      PackRepository packRepository = ServerPacksSource.createPackRepository(levelSourceAccess);
      CloseableResourceManager resourceManager = (CloseableResourceManager)(new WorldLoader.PackConfig(packRepository, worldData.getDataConfiguration(), false, false)).createResourceManager().getSecond();
      this.minecraft.doWorldLoad(levelSourceAccess, packRepository, new WorldStem(resourceManager, serverResources, registryAccess, worldData), true);
   }

   public WorldStem loadWorldStem(final Dynamic<?> levelDataTag, final boolean safeMode, final PackRepository packRepository) throws Exception {
      WorldLoader.PackConfig packConfig = LevelStorageSource.getPackConfig(levelDataTag, packRepository, safeMode);
      return (WorldStem)this.loadWorldDataBlocking(packConfig, (context) -> {
         Registry<LevelStem> datapackDimensions = context.datapackDimensions().lookupOrThrow(Registries.LEVEL_STEM);
         LevelDataAndDimensions data = LevelStorageSource.getLevelDataAndDimensions(levelDataTag, context.dataConfiguration(), datapackDimensions, context.datapackWorldgen());
         return new WorldLoader.DataLoadOutput(data.worldData(), data.dimensions().dimensionsRegistryAccess());
      }, WorldStem::new);
   }

   public Pair<LevelSettings, WorldCreationContext> recreateWorldData(final LevelStorageSource.LevelStorageAccess levelSourceAccess) throws Exception {
      PackRepository packRepository = ServerPacksSource.createPackRepository(levelSourceAccess);
      Dynamic<?> levelDataTag = levelSourceAccess.getDataTag();
      WorldLoader.PackConfig packConfig = LevelStorageSource.getPackConfig(levelDataTag, packRepository, false);
      return (Pair)this.loadWorldDataBlocking(packConfig, (context) -> {
         Registry<LevelStem> noDatapackDimensions = (new MappedRegistry<LevelStem>(Registries.LEVEL_STEM, Lifecycle.stable())).freeze();
         LevelDataAndDimensions existingData = LevelStorageSource.getLevelDataAndDimensions(levelDataTag, context.dataConfiguration(), noDatapackDimensions, context.datapackWorldgen());

         record Data(LevelSettings levelSettings, WorldOptions options, Registry<LevelStem> existingDimensions) {
            Data {
               super();
            }
         }

         return new WorldLoader.DataLoadOutput(new Data(existingData.worldData().getLevelSettings(), existingData.worldData().worldGenOptions(), existingData.dimensions().dimensions()), context.datapackDimensions());
      }, (resources, managers, registries, loadedData) -> {
         resources.close();
         InitialWorldCreationOptions initialWorldCreationOptions = new InitialWorldCreationOptions(WorldCreationUiState.SelectedGameMode.SURVIVAL, GameRuleMap.of(), (ResourceKey)null);
         return Pair.of(loadedData.levelSettings, new WorldCreationContext(loadedData.options, new WorldDimensions(loadedData.existingDimensions), registries, managers, loadedData.levelSettings.getDataConfiguration(), initialWorldCreationOptions));
      });
   }

   private <D, R> R loadWorldDataBlocking(final WorldLoader.PackConfig packConfig, final WorldLoader.WorldDataSupplier<D> worldDataGetter, final WorldLoader.ResultFactory<D, R> worldDataSupplier) throws Exception {
      long start = Util.getMillis();
      WorldLoader.InitConfig config = new WorldLoader.InitConfig(packConfig, Commands.CommandSelection.INTEGRATED, LevelBasedPermissionSet.GAMEMASTER);
      CompletableFuture<R> resourceLoad = WorldLoader.load(config, worldDataGetter, worldDataSupplier, Util.backgroundExecutor(), this.minecraft);
      Minecraft var10000 = this.minecraft;
      Objects.requireNonNull(resourceLoad);
      var10000.managedBlock(resourceLoad::isDone);
      long end = Util.getMillis();
      LOGGER.debug("World resource load blocked for {} ms", end - start);
      return (R)resourceLoad.get();
   }

   private void askForBackup(final LevelStorageSource.LevelStorageAccess worldAccess, final boolean oldCustomized, final Runnable proceedCallback, final Runnable cancelCallback) {
      Component backupQuestion;
      Component backupWarning;
      if (oldCustomized) {
         backupQuestion = Component.translatable("selectWorld.backupQuestion.customized");
         backupWarning = Component.translatable("selectWorld.backupWarning.customized");
      } else {
         backupQuestion = Component.translatable("selectWorld.backupQuestion.experimental");
         backupWarning = Component.translatable("selectWorld.backupWarning.experimental");
      }

      this.minecraft.setScreen(new BackupConfirmScreen(cancelCallback, (backup, eraseCache) -> {
         if (backup) {
            EditWorldScreen.makeBackupAndShowToast(worldAccess);
         }

         proceedCallback.run();
      }, backupQuestion, backupWarning, false));
   }

   public static void confirmWorldCreation(final Minecraft minecraft, final CreateWorldScreen parent, final Lifecycle lifecycle, final Runnable task, final boolean skipWarning) {
      BooleanConsumer callback = (confirmed) -> {
         if (confirmed) {
            task.run();
         } else {
            minecraft.setScreen(parent);
         }

      };
      if (!skipWarning && lifecycle != Lifecycle.stable()) {
         if (lifecycle == Lifecycle.experimental()) {
            minecraft.setScreen(new ConfirmScreen(callback, Component.translatable("selectWorld.warning.experimental.title"), Component.translatable("selectWorld.warning.experimental.question")));
         } else {
            minecraft.setScreen(new ConfirmScreen(callback, Component.translatable("selectWorld.warning.deprecated.title"), Component.translatable("selectWorld.warning.deprecated.question")));
         }
      } else {
         task.run();
      }

   }

   public void openWorld(final String levelId, final Runnable onCancel) {
      this.minecraft.setScreenAndShow(new GenericMessageScreen(Component.translatable("selectWorld.data_read")));
      LevelStorageSource.LevelStorageAccess worldAccess = this.createWorldAccess(levelId);
      if (worldAccess != null) {
         this.openWorldLoadLevelData(worldAccess, onCancel);
      }
   }

   private void openWorldLoadLevelData(final LevelStorageSource.LevelStorageAccess worldAccess, final Runnable onCancel) {
      this.minecraft.setScreenAndShow(new GenericMessageScreen(Component.translatable("selectWorld.data_read")));

      Dynamic<?> levelDataTag;
      LevelSummary summary;
      try {
         levelDataTag = worldAccess.getDataTag();
         summary = worldAccess.getSummary(levelDataTag);
      } catch (NbtException | ReportedNbtException | IOException var10) {
         this.minecraft.setScreen(new RecoverWorldDataScreen(this.minecraft, (success) -> {
            if (success) {
               this.openWorldLoadLevelData(worldAccess, onCancel);
            } else {
               worldAccess.safeClose();
               onCancel.run();
            }

         }, worldAccess));
         return;
      } catch (OutOfMemoryError e) {
         MemoryReserve.release();
         String detailedMessage = "Ran out of memory trying to read level data of world folder \"" + worldAccess.getLevelId() + "\"";
         LOGGER.error(LogUtils.FATAL_MARKER, detailedMessage);
         OutOfMemoryError detailedException = new OutOfMemoryError("Ran out of memory reading level data");
         detailedException.initCause(e);
         CrashReport crashReport = CrashReport.forThrowable(detailedException, detailedMessage);
         CrashReportCategory worldDetails = crashReport.addCategory("World details");
         worldDetails.setDetail("World folder", worldAccess.getLevelId());
         throw new ReportedException(crashReport);
      }

      this.openWorldCheckVersionCompatibility(worldAccess, summary, levelDataTag, onCancel);
   }

   private void openWorldCheckVersionCompatibility(final LevelStorageSource.LevelStorageAccess worldAccess, final LevelSummary summary, final Dynamic<?> levelDataTag, final Runnable onCancel) {
      if (!summary.isCompatible()) {
         worldAccess.safeClose();
         this.minecraft.setScreen(new AlertScreen(onCancel, Component.translatable("selectWorld.incompatible.title").withColor(-65536), Component.translatable("selectWorld.incompatible.description", summary.getWorldVersionName())));
      } else {
         LevelSummary.BackupStatus backupStatus = summary.backupStatus();
         if (backupStatus.shouldBackup()) {
            String questionKey = "selectWorld.backupQuestion." + backupStatus.getTranslationKey();
            String warningKey = "selectWorld.backupWarning." + backupStatus.getTranslationKey();
            MutableComponent backupQuestion = Component.translatable(questionKey);
            if (backupStatus.isSevere()) {
               backupQuestion.withColor(-2142128);
            }

            Component backupWarning = Component.translatable(warningKey, summary.getWorldVersionName(), SharedConstants.getCurrentVersion().name());
            this.minecraft.setScreen(new BackupConfirmScreen(() -> {
               worldAccess.safeClose();
               onCancel.run();
            }, (backup, eraseCache) -> {
               if (backup) {
                  EditWorldScreen.makeBackupAndShowToast(worldAccess);
               }

               this.openWorldLoadLevelStem(worldAccess, levelDataTag, false, onCancel);
            }, backupQuestion, backupWarning, false));
         } else {
            this.openWorldLoadLevelStem(worldAccess, levelDataTag, false, onCancel);
         }

      }
   }

   private void openWorldLoadLevelStem(final LevelStorageSource.LevelStorageAccess worldAccess, final Dynamic<?> levelDataTag, final boolean safeMode, final Runnable onCancel) {
      this.minecraft.setScreenAndShow(new GenericMessageScreen(Component.translatable("selectWorld.resource_load")));
      PackRepository packRepository = ServerPacksSource.createPackRepository(worldAccess);

      WorldStem worldStem;
      try {
         worldStem = this.loadWorldStem(levelDataTag, safeMode, packRepository);

         for(LevelStem levelStem : worldStem.registries().compositeAccess().lookupOrThrow(Registries.LEVEL_STEM)) {
            levelStem.generator().validate();
         }
      } catch (Exception e) {
         LOGGER.warn("Failed to load level data or datapacks, can't proceed with server load", e);
         if (!safeMode) {
            this.minecraft.setScreen(new DatapackLoadFailureScreen(() -> {
               worldAccess.safeClose();
               onCancel.run();
            }, () -> this.openWorldLoadLevelStem(worldAccess, levelDataTag, true, onCancel)));
         } else {
            worldAccess.safeClose();
            this.minecraft.setScreen(new AlertScreen(onCancel, Component.translatable("datapackFailure.safeMode.failed.title"), Component.translatable("datapackFailure.safeMode.failed.description"), CommonComponents.GUI_BACK, true));
         }

         return;
      }

      this.openWorldCheckWorldStemCompatibility(worldAccess, worldStem, packRepository, onCancel);
   }

   private void openWorldCheckWorldStemCompatibility(final LevelStorageSource.LevelStorageAccess worldAccess, final WorldStem worldStem, final PackRepository packRepository, final Runnable onCancel) {
      WorldData data = worldStem.worldData();
      boolean oldCustomized = data.worldGenOptions().isOldCustomizedWorld();
      boolean unstable = data.worldGenSettingsLifecycle() != Lifecycle.stable();
      if (!oldCustomized && !unstable) {
         this.openWorldLoadBundledResourcePack(worldAccess, worldStem, packRepository, onCancel);
      } else {
         this.askForBackup(worldAccess, oldCustomized, () -> this.openWorldLoadBundledResourcePack(worldAccess, worldStem, packRepository, onCancel), () -> {
            worldStem.close();
            worldAccess.safeClose();
            onCancel.run();
         });
      }
   }

   private void openWorldLoadBundledResourcePack(final LevelStorageSource.LevelStorageAccess worldAccess, final WorldStem worldStem, final PackRepository packRepository, final Runnable onCancel) {
      DownloadedPackSource packSource = this.minecraft.getDownloadedPackSource();
      this.loadBundledResourcePack(packSource, worldAccess).thenApply((unused) -> true).exceptionallyComposeAsync((t) -> {
         LOGGER.warn("Failed to load pack: ", t);
         return this.promptBundledPackLoadFailure();
      }, this.minecraft).thenAcceptAsync((result) -> {
         if (result) {
            this.openWorldCheckDiskSpace(worldAccess, worldStem, packSource, packRepository, onCancel);
         } else {
            packSource.popAll();
            worldStem.close();
            worldAccess.safeClose();
            onCancel.run();
         }

      }, this.minecraft).exceptionally((e) -> {
         this.minecraft.delayCrash(CrashReport.forThrowable(e, "Load world"));
         return null;
      });
   }

   private void openWorldCheckDiskSpace(final LevelStorageSource.LevelStorageAccess worldAccess, final WorldStem worldStem, final DownloadedPackSource packSource, final PackRepository packRepository, final Runnable onCancel) {
      if (worldAccess.checkForLowDiskSpace()) {
         this.minecraft.setScreen(new ConfirmScreen((skip) -> {
            if (skip) {
               this.openWorldDoLoad(worldAccess, worldStem, packRepository);
            } else {
               packSource.popAll();
               worldStem.close();
               worldAccess.safeClose();
               onCancel.run();
            }

         }, Component.translatable("selectWorld.warning.lowDiskSpace.title").withStyle(ChatFormatting.RED), Component.translatable("selectWorld.warning.lowDiskSpace.description"), CommonComponents.GUI_CONTINUE, CommonComponents.GUI_BACK));
      } else {
         this.openWorldDoLoad(worldAccess, worldStem, packRepository);
      }

   }

   private void openWorldDoLoad(final LevelStorageSource.LevelStorageAccess worldAccess, final WorldStem worldStem, final PackRepository packRepository) {
      this.minecraft.doWorldLoad(worldAccess, packRepository, worldStem, false);
   }

   private CompletableFuture<Void> loadBundledResourcePack(final DownloadedPackSource packSource, final LevelStorageSource.LevelStorageAccess levelSourceAccess) {
      Path mapResourceFile = levelSourceAccess.getLevelPath(LevelResource.MAP_RESOURCE_FILE);
      if (Files.exists(mapResourceFile, new LinkOption[0]) && !Files.isDirectory(mapResourceFile, new LinkOption[0])) {
         packSource.configureForLocalWorld();
         CompletableFuture<Void> result = packSource.waitForPackFeedback(WORLD_PACK_ID);
         packSource.pushLocalPack(WORLD_PACK_ID, mapResourceFile);
         return result;
      } else {
         return CompletableFuture.completedFuture((Object)null);
      }
   }

   private CompletableFuture<Boolean> promptBundledPackLoadFailure() {
      CompletableFuture<Boolean> result = new CompletableFuture();
      Minecraft var10000 = this.minecraft;
      Objects.requireNonNull(result);
      var10000.setScreen(new ConfirmScreen(result::complete, Component.translatable("multiplayer.texturePrompt.failure.line1"), Component.translatable("multiplayer.texturePrompt.failure.line2"), CommonComponents.GUI_PROCEED, CommonComponents.GUI_CANCEL));
      return result;
   }
}
