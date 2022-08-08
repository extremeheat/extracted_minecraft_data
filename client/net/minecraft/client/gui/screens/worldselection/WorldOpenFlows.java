package net.minecraft.client.gui.screens.worldselection;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
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
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelResource;
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

   public void createFreshLevel(String var1, LevelSettings var2, RegistryAccess var3, WorldGenSettings var4) {
      LevelStorageSource.LevelStorageAccess var5 = this.createWorldAccess(var1);
      if (var5 != null) {
         PackRepository var6 = createPackRepository(var5);
         DataPackConfig var7 = var2.getDataPackConfig();

         try {
            WorldLoader.PackConfig var8 = new WorldLoader.PackConfig(var6, var7, false);
            WorldStem var9 = this.loadWorldStem(var8, (var3x, var4x) -> {
               return Pair.of(new PrimaryLevelData(var2, var4, Lifecycle.stable()), var3.freeze());
            });
            this.minecraft.doWorldLoad(var1, var5, var6, var9);
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
         this.minecraft.setScreen((Screen)null);
         return null;
      }
   }

   public void createLevelFromExistingSettings(LevelStorageSource.LevelStorageAccess var1, ReloadableServerResources var2, RegistryAccess.Frozen var3, WorldData var4) {
      PackRepository var5 = createPackRepository(var1);
      CloseableResourceManager var6 = (CloseableResourceManager)(new WorldLoader.PackConfig(var5, var4.getDataPackConfig(), false)).createResourceManager().getSecond();
      this.minecraft.doWorldLoad(var1.getLevelId(), var1, var5, new WorldStem(var6, var2, var3, var4));
   }

   private static PackRepository createPackRepository(LevelStorageSource.LevelStorageAccess var0) {
      return new PackRepository(PackType.SERVER_DATA, new RepositorySource[]{new ServerPacksSource(), new FolderRepositorySource(var0.getLevelPath(LevelResource.DATAPACK_DIR).toFile(), PackSource.WORLD)});
   }

   private WorldStem loadWorldStem(LevelStorageSource.LevelStorageAccess var1, boolean var2, PackRepository var3) throws Exception {
      DataPackConfig var4 = var1.getDataPacks();
      if (var4 == null) {
         throw new IllegalStateException("Failed to load data pack config");
      } else {
         WorldLoader.PackConfig var5 = new WorldLoader.PackConfig(var3, var4, var2);
         return this.loadWorldStem(var5, (var1x, var2x) -> {
            RegistryAccess.Writable var3 = RegistryAccess.builtinCopy();
            RegistryOps var4 = RegistryOps.createAndLoad(NbtOps.INSTANCE, var3, (ResourceManager)var1x);
            WorldData var5 = var1.getDataTag(var4, var2x, var3.allElementsLifecycle());
            if (var5 == null) {
               throw new IllegalStateException("Failed to load world");
            } else {
               return Pair.of(var5, var3.freeze());
            }
         });
      }
   }

   public WorldStem loadWorldStem(LevelStorageSource.LevelStorageAccess var1, boolean var2) throws Exception {
      PackRepository var3 = createPackRepository(var1);
      return this.loadWorldStem(var1, var2, var3);
   }

   private WorldStem loadWorldStem(WorldLoader.PackConfig var1, WorldLoader.WorldDataSupplier<WorldData> var2) throws Exception {
      WorldLoader.InitConfig var3 = new WorldLoader.InitConfig(var1, Commands.CommandSelection.INTEGRATED, 2);
      CompletableFuture var4 = WorldStem.load(var3, var2, Util.backgroundExecutor(), this.minecraft);
      Minecraft var10000 = this.minecraft;
      Objects.requireNonNull(var4);
      var10000.managedBlock(var4::isDone);
      return (WorldStem)var4.get();
   }

   private void doLoadLevel(Screen var1, String var2, boolean var3, boolean var4) {
      LevelStorageSource.LevelStorageAccess var5 = this.createWorldAccess(var2);
      if (var5 != null) {
         PackRepository var6 = createPackRepository(var5);

         WorldStem var7;
         try {
            var7 = this.loadWorldStem(var5, var3, var6);
         } catch (Exception var11) {
            LOGGER.warn("Failed to load datapacks, can't proceed with server load", var11);
            this.minecraft.setScreen(new DatapackLoadFailureScreen(() -> {
               this.doLoadLevel(var1, var2, true, var4);
            }));
            safeCloseAccess(var5, var2);
            return;
         }

         WorldData var8 = var7.worldData();
         boolean var9 = var8.worldGenSettings().isOldCustomizedWorld();
         boolean var10 = var8.worldGenSettingsLifecycle() != Lifecycle.stable();
         if (!var4 || !var9 && !var10) {
            this.minecraft.getClientPackSource().loadBundledResourcePack(var5).thenApply((var0) -> {
               return true;
            }).exceptionallyComposeAsync((var1x) -> {
               LOGGER.warn("Failed to load pack: ", var1x);
               return this.promptBundledPackLoadFailure();
            }, this.minecraft).thenAcceptAsync((var6x) -> {
               if (var6x) {
                  this.minecraft.doWorldLoad(var2, var5, var6, var7);
               } else {
                  var7.close();
                  safeCloseAccess(var5, var2);
                  this.minecraft.getClientPackSource().clearServerPack().thenRunAsync(() -> {
                     this.minecraft.setScreen(var1);
                  }, this.minecraft);
               }

            }, this.minecraft).exceptionally((var1x) -> {
               this.minecraft.delayCrash(CrashReport.forThrowable(var1x, "Load world"));
               return null;
            });
         } else {
            this.askForBackup(var1, var2, var9, () -> {
               this.doLoadLevel(var1, var2, var3, false);
            });
            var7.close();
            safeCloseAccess(var5, var2);
         }
      }
   }

   private CompletableFuture<Boolean> promptBundledPackLoadFailure() {
      CompletableFuture var1 = new CompletableFuture();
      Minecraft var10000 = this.minecraft;
      Objects.requireNonNull(var1);
      var10000.setScreen(new ConfirmScreen(var1::complete, Component.translatable("multiplayer.texturePrompt.failure.line1"), Component.translatable("multiplayer.texturePrompt.failure.line2"), CommonComponents.GUI_PROCEED, CommonComponents.GUI_CANCEL));
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
      BooleanConsumer var4 = (var3x) -> {
         if (var3x) {
            var3.run();
         } else {
            var0.setScreen(var1);
         }

      };
      if (var2 == Lifecycle.stable()) {
         var3.run();
      } else if (var2 == Lifecycle.experimental()) {
         var0.setScreen(new ConfirmScreen(var4, Component.translatable("selectWorld.import_worldgen_settings.experimental.title"), Component.translatable("selectWorld.import_worldgen_settings.experimental.question")));
      } else {
         var0.setScreen(new ConfirmScreen(var4, Component.translatable("selectWorld.import_worldgen_settings.deprecated.title"), Component.translatable("selectWorld.import_worldgen_settings.deprecated.question")));
      }

   }
}
