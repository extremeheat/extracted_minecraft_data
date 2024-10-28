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
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
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
import net.minecraft.util.MemoryReserve;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelDataAndDimensions;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.validation.ContentValidationException;
import org.slf4j.Logger;

public class WorldOpenFlows {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final UUID WORLD_PACK_ID = UUID.fromString("640a6a92-b6cb-48a0-b391-831586500359");
   private final Minecraft minecraft;
   private final LevelStorageSource levelSource;

   public WorldOpenFlows(Minecraft var1, LevelStorageSource var2) {
      super();
      this.minecraft = var1;
      this.levelSource = var2;
   }

   public void createFreshLevel(String var1, LevelSettings var2, WorldOptions var3, Function<HolderLookup.Provider, WorldDimensions> var4, Screen var5) {
      this.minecraft.forceSetScreen(new GenericMessageScreen(Component.translatable("selectWorld.data_read")));
      LevelStorageSource.LevelStorageAccess var6 = this.createWorldAccess(var1);
      if (var6 != null) {
         PackRepository var7 = ServerPacksSource.createPackRepository(var6);
         WorldDataConfiguration var8 = var2.getDataConfiguration();

         try {
            WorldLoader.PackConfig var9 = new WorldLoader.PackConfig(var7, var8, false, false);
            WorldStem var10 = (WorldStem)this.loadWorldDataBlocking(var9, (var3x) -> {
               WorldDimensions.Complete var4x = ((WorldDimensions)var4.apply(var3x.datapackWorldgen())).bake(var3x.datapackDimensions().lookupOrThrow(Registries.LEVEL_STEM));
               return new WorldLoader.DataLoadOutput(new PrimaryLevelData(var2, var3, var4x.specialWorldProperty(), var4x.lifecycle()), var4x.dimensionsRegistryAccess());
            }, WorldStem::new);
            this.minecraft.doWorldLoad(var6, var7, var10, true);
         } catch (Exception var11) {
            LOGGER.warn("Failed to load datapacks, can't proceed with server load", var11);
            var6.safeClose();
            this.minecraft.setScreen(var5);
         }

      }
   }

   @Nullable
   private LevelStorageSource.LevelStorageAccess createWorldAccess(String var1) {
      try {
         return this.levelSource.validateAndCreateAccess(var1);
      } catch (IOException var3) {
         LOGGER.warn("Failed to read level {} data", var1, var3);
         SystemToast.onWorldAccessFailure(this.minecraft, var1);
         this.minecraft.setScreen((Screen)null);
         return null;
      } catch (ContentValidationException var4) {
         LOGGER.warn("{}", var4.getMessage());
         this.minecraft.setScreen(NoticeWithLinkScreen.createWorldSymlinkWarningScreen(() -> {
            this.minecraft.setScreen((Screen)null);
         }));
         return null;
      }
   }

   public void createLevelFromExistingSettings(LevelStorageSource.LevelStorageAccess var1, ReloadableServerResources var2, LayeredRegistryAccess<RegistryLayer> var3, WorldData var4) {
      PackRepository var5 = ServerPacksSource.createPackRepository(var1);
      CloseableResourceManager var6 = (CloseableResourceManager)(new WorldLoader.PackConfig(var5, var4.getDataConfiguration(), false, false)).createResourceManager().getSecond();
      this.minecraft.doWorldLoad(var1, var5, new WorldStem(var6, var2, var3, var4), true);
   }

   public WorldStem loadWorldStem(Dynamic<?> var1, boolean var2, PackRepository var3) throws Exception {
      WorldLoader.PackConfig var4 = LevelStorageSource.getPackConfig(var1, var3, var2);
      return (WorldStem)this.loadWorldDataBlocking(var4, (var1x) -> {
         Registry var2 = var1x.datapackDimensions().lookupOrThrow(Registries.LEVEL_STEM);
         LevelDataAndDimensions var3 = LevelStorageSource.getLevelDataAndDimensions(var1, var1x.dataConfiguration(), var2, var1x.datapackWorldgen());
         return new WorldLoader.DataLoadOutput(var3.worldData(), var3.dimensions().dimensionsRegistryAccess());
      }, WorldStem::new);
   }

   public Pair<LevelSettings, WorldCreationContext> recreateWorldData(LevelStorageSource.LevelStorageAccess var1) throws Exception {
      PackRepository var2 = ServerPacksSource.createPackRepository(var1);
      Dynamic var3 = var1.getDataTag();
      WorldLoader.PackConfig var4 = LevelStorageSource.getPackConfig(var3, var2, false);
      return (Pair)this.loadWorldDataBlocking(var4, (var1x) -> {
         Registry var2 = (new MappedRegistry(Registries.LEVEL_STEM, Lifecycle.stable())).freeze();
         LevelDataAndDimensions var3x = LevelStorageSource.getLevelDataAndDimensions(var3, var1x.dataConfiguration(), var2, var1x.datapackWorldgen());

         record 1Data(LevelSettings levelSettings, WorldOptions options, Registry<LevelStem> existingDimensions) {
            final LevelSettings levelSettings;
            final WorldOptions options;
            final Registry<LevelStem> existingDimensions;

            _Data/* $FF was: 1Data*/(LevelSettings var1, WorldOptions var2, Registry<LevelStem> var3) {
               super();
               this.levelSettings = var1;
               this.options = var2;
               this.existingDimensions = var3;
            }

            public LevelSettings levelSettings() {
               return this.levelSettings;
            }

            public WorldOptions options() {
               return this.options;
            }

            public Registry<LevelStem> existingDimensions() {
               return this.existingDimensions;
            }
         }

         return new WorldLoader.DataLoadOutput(new 1Data(var3x.worldData().getLevelSettings(), var3x.worldData().worldGenOptions(), var3x.dimensions().dimensions()), var1x.datapackDimensions());
      }, (var0, var1x, var2x, var3x) -> {
         var0.close();
         InitialWorldCreationOptions var4 = new InitialWorldCreationOptions(WorldCreationUiState.SelectedGameMode.SURVIVAL, Set.of(), (ResourceKey)null);
         return Pair.of(var3x.levelSettings, new WorldCreationContext(var3x.options, new WorldDimensions(var3x.existingDimensions), var2x, var1x, var3x.levelSettings.getDataConfiguration(), var4));
      });
   }

   private <D, R> R loadWorldDataBlocking(WorldLoader.PackConfig var1, WorldLoader.WorldDataSupplier<D> var2, WorldLoader.ResultFactory<D, R> var3) throws Exception {
      WorldLoader.InitConfig var4 = new WorldLoader.InitConfig(var1, Commands.CommandSelection.INTEGRATED, 2);
      CompletableFuture var5 = WorldLoader.load(var4, var2, var3, Util.backgroundExecutor(), this.minecraft);
      Minecraft var10000 = this.minecraft;
      Objects.requireNonNull(var5);
      var10000.managedBlock(var5::isDone);
      return var5.get();
   }

   private void askForBackup(LevelStorageSource.LevelStorageAccess var1, boolean var2, Runnable var3, Runnable var4) {
      MutableComponent var5;
      MutableComponent var6;
      if (var2) {
         var5 = Component.translatable("selectWorld.backupQuestion.customized");
         var6 = Component.translatable("selectWorld.backupWarning.customized");
      } else {
         var5 = Component.translatable("selectWorld.backupQuestion.experimental");
         var6 = Component.translatable("selectWorld.backupWarning.experimental");
      }

      this.minecraft.setScreen(new BackupConfirmScreen(var4, (var2x, var3x) -> {
         if (var2x) {
            EditWorldScreen.makeBackupAndShowToast(var1);
         }

         var3.run();
      }, var5, var6, false));
   }

   public static void confirmWorldCreation(Minecraft var0, CreateWorldScreen var1, Lifecycle var2, Runnable var3, boolean var4) {
      BooleanConsumer var5 = (var3x) -> {
         if (var3x) {
            var3.run();
         } else {
            var0.setScreen(var1);
         }

      };
      if (!var4 && var2 != Lifecycle.stable()) {
         if (var2 == Lifecycle.experimental()) {
            var0.setScreen(new ConfirmScreen(var5, Component.translatable("selectWorld.warning.experimental.title"), Component.translatable("selectWorld.warning.experimental.question")));
         } else {
            var0.setScreen(new ConfirmScreen(var5, Component.translatable("selectWorld.warning.deprecated.title"), Component.translatable("selectWorld.warning.deprecated.question")));
         }
      } else {
         var3.run();
      }

   }

   public void openWorld(String var1, Runnable var2) {
      this.minecraft.forceSetScreen(new GenericMessageScreen(Component.translatable("selectWorld.data_read")));
      LevelStorageSource.LevelStorageAccess var3 = this.createWorldAccess(var1);
      if (var3 != null) {
         this.openWorldLoadLevelData(var3, var2);
      }
   }

   private void openWorldLoadLevelData(LevelStorageSource.LevelStorageAccess var1, Runnable var2) {
      this.minecraft.forceSetScreen(new GenericMessageScreen(Component.translatable("selectWorld.data_read")));

      Dynamic var3;
      LevelSummary var4;
      try {
         var3 = var1.getDataTag();
         var4 = var1.getSummary(var3);
      } catch (NbtException | ReportedNbtException | IOException var10) {
         this.minecraft.setScreen(new RecoverWorldDataScreen(this.minecraft, (var3x) -> {
            if (var3x) {
               this.openWorldLoadLevelData(var1, var2);
            } else {
               var1.safeClose();
               var2.run();
            }

         }, var1));
         return;
      } catch (OutOfMemoryError var11) {
         MemoryReserve.release();
         String var6 = "Ran out of memory trying to read level data of world folder \"" + var1.getLevelId() + "\"";
         LOGGER.error(LogUtils.FATAL_MARKER, var6);
         OutOfMemoryError var7 = new OutOfMemoryError("Ran out of memory reading level data");
         var7.initCause(var11);
         CrashReport var8 = CrashReport.forThrowable(var7, var6);
         CrashReportCategory var9 = var8.addCategory("World details");
         var9.setDetail("World folder", (Object)var1.getLevelId());
         throw new ReportedException(var8);
      }

      this.openWorldCheckVersionCompatibility(var1, var4, var3, var2);
   }

   private void openWorldCheckVersionCompatibility(LevelStorageSource.LevelStorageAccess var1, LevelSummary var2, Dynamic<?> var3, Runnable var4) {
      if (!var2.isCompatible()) {
         var1.safeClose();
         this.minecraft.setScreen(new AlertScreen(var4, Component.translatable("selectWorld.incompatible.title").withColor(-65536), Component.translatable("selectWorld.incompatible.description", var2.getWorldVersionName())));
      } else {
         LevelSummary.BackupStatus var5 = var2.backupStatus();
         if (var5.shouldBackup()) {
            String var6 = "selectWorld.backupQuestion." + var5.getTranslationKey();
            String var7 = "selectWorld.backupWarning." + var5.getTranslationKey();
            MutableComponent var8 = Component.translatable(var6);
            if (var5.isSevere()) {
               var8.withColor(-2142128);
            }

            MutableComponent var9 = Component.translatable(var7, var2.getWorldVersionName(), SharedConstants.getCurrentVersion().getName());
            this.minecraft.setScreen(new BackupConfirmScreen(() -> {
               var1.safeClose();
               var4.run();
            }, (var4x, var5x) -> {
               if (var4x) {
                  EditWorldScreen.makeBackupAndShowToast(var1);
               }

               this.openWorldLoadLevelStem(var1, var3, false, var4);
            }, var8, var9, false));
         } else {
            this.openWorldLoadLevelStem(var1, var3, false, var4);
         }

      }
   }

   private void openWorldLoadLevelStem(LevelStorageSource.LevelStorageAccess var1, Dynamic<?> var2, boolean var3, Runnable var4) {
      this.minecraft.forceSetScreen(new GenericMessageScreen(Component.translatable("selectWorld.resource_load")));
      PackRepository var5 = ServerPacksSource.createPackRepository(var1);

      WorldStem var6;
      try {
         var6 = this.loadWorldStem(var2, var3, var5);
         Iterator var7 = var6.registries().compositeAccess().lookupOrThrow(Registries.LEVEL_STEM).iterator();

         while(var7.hasNext()) {
            LevelStem var8 = (LevelStem)var7.next();
            var8.generator().validate();
         }
      } catch (Exception var9) {
         LOGGER.warn("Failed to load level data or datapacks, can't proceed with server load", var9);
         if (!var3) {
            this.minecraft.setScreen(new DatapackLoadFailureScreen(() -> {
               var1.safeClose();
               var4.run();
            }, () -> {
               this.openWorldLoadLevelStem(var1, var2, true, var4);
            }));
         } else {
            var1.safeClose();
            this.minecraft.setScreen(new AlertScreen(var4, Component.translatable("datapackFailure.safeMode.failed.title"), Component.translatable("datapackFailure.safeMode.failed.description"), CommonComponents.GUI_BACK, true));
         }

         return;
      }

      this.openWorldCheckWorldStemCompatibility(var1, var6, var5, var4);
   }

   private void openWorldCheckWorldStemCompatibility(LevelStorageSource.LevelStorageAccess var1, WorldStem var2, PackRepository var3, Runnable var4) {
      WorldData var5 = var2.worldData();
      boolean var6 = var5.worldGenOptions().isOldCustomizedWorld();
      boolean var7 = var5.worldGenSettingsLifecycle() != Lifecycle.stable();
      if (!var6 && !var7) {
         this.openWorldLoadBundledResourcePack(var1, var2, var3, var4);
      } else {
         this.askForBackup(var1, var6, () -> {
            this.openWorldLoadBundledResourcePack(var1, var2, var3, var4);
         }, () -> {
            var2.close();
            var1.safeClose();
            var4.run();
         });
      }
   }

   private void openWorldLoadBundledResourcePack(LevelStorageSource.LevelStorageAccess var1, WorldStem var2, PackRepository var3, Runnable var4) {
      DownloadedPackSource var5 = this.minecraft.getDownloadedPackSource();
      this.loadBundledResourcePack(var5, var1).thenApply((var0) -> {
         return true;
      }).exceptionallyComposeAsync((var1x) -> {
         LOGGER.warn("Failed to load pack: ", var1x);
         return this.promptBundledPackLoadFailure();
      }, this.minecraft).thenAcceptAsync((var6) -> {
         if (var6) {
            this.openWorldCheckDiskSpace(var1, var2, var5, var3, var4);
         } else {
            var5.popAll();
            var2.close();
            var1.safeClose();
            var4.run();
         }

      }, this.minecraft).exceptionally((var1x) -> {
         this.minecraft.delayCrash(CrashReport.forThrowable(var1x, "Load world"));
         return null;
      });
   }

   private void openWorldCheckDiskSpace(LevelStorageSource.LevelStorageAccess var1, WorldStem var2, DownloadedPackSource var3, PackRepository var4, Runnable var5) {
      if (var1.checkForLowDiskSpace()) {
         this.minecraft.setScreen(new ConfirmScreen((var6) -> {
            if (var6) {
               this.openWorldDoLoad(var1, var2, var4);
            } else {
               var3.popAll();
               var2.close();
               var1.safeClose();
               var5.run();
            }

         }, Component.translatable("selectWorld.warning.lowDiskSpace.title").withStyle(ChatFormatting.RED), Component.translatable("selectWorld.warning.lowDiskSpace.description"), CommonComponents.GUI_CONTINUE, CommonComponents.GUI_BACK));
      } else {
         this.openWorldDoLoad(var1, var2, var4);
      }

   }

   private void openWorldDoLoad(LevelStorageSource.LevelStorageAccess var1, WorldStem var2, PackRepository var3) {
      this.minecraft.doWorldLoad(var1, var3, var2, false);
   }

   private CompletableFuture<Void> loadBundledResourcePack(DownloadedPackSource var1, LevelStorageSource.LevelStorageAccess var2) {
      Path var3 = var2.getLevelPath(LevelResource.MAP_RESOURCE_FILE);
      if (Files.exists(var3, new LinkOption[0]) && !Files.isDirectory(var3, new LinkOption[0])) {
         var1.configureForLocalWorld();
         CompletableFuture var4 = var1.waitForPackFeedback(WORLD_PACK_ID);
         var1.pushLocalPack(WORLD_PACK_ID, var3);
         return var4;
      } else {
         return CompletableFuture.completedFuture((Object)null);
      }
   }

   private CompletableFuture<Boolean> promptBundledPackLoadFailure() {
      CompletableFuture var1 = new CompletableFuture();
      Minecraft var10000 = this.minecraft;
      Objects.requireNonNull(var1);
      var10000.setScreen(new ConfirmScreen(var1::complete, Component.translatable("multiplayer.texturePrompt.failure.line1"), Component.translatable("multiplayer.texturePrompt.failure.line2"), CommonComponents.GUI_PROCEED, CommonComponents.GUI_CANCEL));
      return var1;
   }
}
