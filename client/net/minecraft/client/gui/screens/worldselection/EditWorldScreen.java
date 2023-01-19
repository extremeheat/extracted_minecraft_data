package net.minecraft.client.gui.screens.worldselection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonWriter;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.DataResult.PartialResult;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.WorldStem;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class EditWorldScreen extends Screen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson WORLD_GEN_SETTINGS_GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();
   private static final Component NAME_LABEL = Component.translatable("selectWorld.enterName");
   private Button renameButton;
   private final BooleanConsumer callback;
   private EditBox nameEdit;
   private final LevelStorageSource.LevelStorageAccess levelAccess;

   public EditWorldScreen(BooleanConsumer var1, LevelStorageSource.LevelStorageAccess var2) {
      super(Component.translatable("selectWorld.edit.title"));
      this.callback = var1;
      this.levelAccess = var2;
   }

   @Override
   public void tick() {
      this.nameEdit.tick();
   }

   @Override
   protected void init() {
      Button var1 = this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.edit.resetIcon"), var1x -> {
         this.levelAccess.getIconFile().ifPresent(var0 -> FileUtils.deleteQuietly(var0.toFile()));
         var1x.active = false;
      }).bounds(this.width / 2 - 100, this.height / 4 + 0 + 5, 200, 20).build());
      this.addRenderableWidget(
         Button.builder(
               Component.translatable("selectWorld.edit.openFolder"),
               var1x -> Util.getPlatform().openFile(this.levelAccess.getLevelPath(LevelResource.ROOT).toFile())
            )
            .bounds(this.width / 2 - 100, this.height / 4 + 24 + 5, 200, 20)
            .build()
      );
      this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.edit.backup"), var1x -> {
         boolean var2x = makeBackupAndShowToast(this.levelAccess);
         this.callback.accept(!var2x);
      }).bounds(this.width / 2 - 100, this.height / 4 + 48 + 5, 200, 20).build());
      this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.edit.backupFolder"), var1x -> {
         LevelStorageSource var2x = this.minecraft.getLevelSource();
         Path var3x = var2x.getBackupPath();

         try {
            FileUtil.createDirectoriesSafe(var3x);
         } catch (IOException var5) {
            throw new RuntimeException(var5);
         }

         Util.getPlatform().openFile(var3x.toFile());
      }).bounds(this.width / 2 - 100, this.height / 4 + 72 + 5, 200, 20).build());
      this.addRenderableWidget(
         Button.builder(
               Component.translatable("selectWorld.edit.optimize"), var1x -> this.minecraft.setScreen(new BackupConfirmScreen(this, (var1xx, var2x) -> {
                     if (var1xx) {
                        makeBackupAndShowToast(this.levelAccess);
                     }
         
                     this.minecraft
                        .setScreen(OptimizeWorldScreen.create(this.minecraft, this.callback, this.minecraft.getFixerUpper(), this.levelAccess, var2x));
                  }, Component.translatable("optimizeWorld.confirm.title"), Component.translatable("optimizeWorld.confirm.description"), true))
            )
            .bounds(this.width / 2 - 100, this.height / 4 + 96 + 5, 200, 20)
            .build()
      );
      this.addRenderableWidget(
         Button.builder(
               Component.translatable("selectWorld.edit.export_worldgen_settings"),
               var1x -> {
                  DataResult var2x;
                  try (WorldStem var3x = this.minecraft.createWorldOpenFlows().loadWorldStem(this.levelAccess, false)) {
                     RegistryAccess.Frozen var4 = var3x.registries().compositeAccess();
                     RegistryOps var5 = RegistryOps.create(JsonOps.INSTANCE, var4);
                     DataResult var6 = WorldGenSettings.encode(var5, var3x.worldData().worldGenOptions(), var4);
                     var2x = var6.flatMap(var1xx -> {
                        Path var2xx = this.levelAccess.getLevelPath(LevelResource.ROOT).resolve("worldgen_settings_export.json");
         
                        try {
                           JsonWriter var3xx = WORLD_GEN_SETTINGS_GSON.newJsonWriter(Files.newBufferedWriter(var2xx, StandardCharsets.UTF_8));
         
                           try {
                              WORLD_GEN_SETTINGS_GSON.toJson(var1xx, var3xx);
                           } catch (Throwable var7) {
                              if (var3xx != null) {
                                 try {
                                    var3xx.close();
                                 } catch (Throwable var6x) {
                                    var7.addSuppressed(var6x);
                                 }
                              }
         
                              throw var7;
                           }
         
                           if (var3xx != null) {
                              var3xx.close();
                           }
                        } catch (JsonIOException | IOException var8) {
                           return DataResult.error("Error writing file: " + var8.getMessage());
                        }
         
                        return DataResult.success(var2xx.toString());
                     });
                  } catch (Exception var9) {
                     LOGGER.warn("Could not parse level data", var9);
                     var2x = DataResult.error("Could not parse level data: " + var9.getMessage());
                  }
         
                  MutableComponent var10 = Component.literal((String)var2x.get().map(Function.identity(), PartialResult::message));
                  MutableComponent var11 = Component.translatable(
                     var2x.result().isPresent() ? "selectWorld.edit.export_worldgen_settings.success" : "selectWorld.edit.export_worldgen_settings.failure"
                  );
                  var2x.error().ifPresent(var0 -> LOGGER.error("Error exporting world settings: {}", var0));
                  this.minecraft
                     .getToasts()
                     .addToast(SystemToast.multiline(this.minecraft, SystemToast.SystemToastIds.WORLD_GEN_SETTINGS_TRANSFER, var11, var10));
               }
            )
            .bounds(this.width / 2 - 100, this.height / 4 + 120 + 5, 200, 20)
            .build()
      );
      this.renameButton = this.addRenderableWidget(
         Button.builder(Component.translatable("selectWorld.edit.save"), var1x -> this.onRename())
            .bounds(this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20)
            .build()
      );
      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_CANCEL, var1x -> this.callback.accept(false))
            .bounds(this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20)
            .build()
      );
      var1.active = this.levelAccess.getIconFile().filter(var0 -> Files.isRegularFile(var0)).isPresent();
      LevelSummary var2 = this.levelAccess.getSummary();
      String var3 = var2 == null ? "" : var2.getLevelName();
      this.nameEdit = new EditBox(this.font, this.width / 2 - 100, 38, 200, 20, Component.translatable("selectWorld.enterName"));
      this.nameEdit.setValue(var3);
      this.nameEdit.setResponder(var1x -> this.renameButton.active = !var1x.trim().isEmpty());
      this.addWidget(this.nameEdit);
      this.setInitialFocus(this.nameEdit);
   }

   @Override
   public void resize(Minecraft var1, int var2, int var3) {
      String var4 = this.nameEdit.getValue();
      this.init(var1, var2, var3);
      this.nameEdit.setValue(var4);
   }

   @Override
   public void onClose() {
      this.callback.accept(false);
   }

   private void onRename() {
      try {
         this.levelAccess.renameLevel(this.nameEdit.getValue().trim());
         this.callback.accept(true);
      } catch (IOException var2) {
         LOGGER.error("Failed to access world '{}'", this.levelAccess.getLevelId(), var2);
         SystemToast.onWorldAccessFailure(this.minecraft, this.levelAccess.getLevelId());
         this.callback.accept(true);
      }
   }

   public static void makeBackupAndShowToast(LevelStorageSource var0, String var1) {
      boolean var2 = false;

      try (LevelStorageSource.LevelStorageAccess var3 = var0.createAccess(var1)) {
         var2 = true;
         makeBackupAndShowToast(var3);
      } catch (IOException var8) {
         if (!var2) {
            SystemToast.onWorldAccessFailure(Minecraft.getInstance(), var1);
         }

         LOGGER.warn("Failed to create backup of level {}", var1, var8);
      }
   }

   public static boolean makeBackupAndShowToast(LevelStorageSource.LevelStorageAccess var0) {
      long var1 = 0L;
      IOException var3 = null;

      try {
         var1 = var0.makeWorldBackup();
      } catch (IOException var6) {
         var3 = var6;
      }

      if (var3 != null) {
         MutableComponent var7 = Component.translatable("selectWorld.edit.backupFailed");
         MutableComponent var8 = Component.literal(var3.getMessage());
         Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastIds.WORLD_BACKUP, var7, var8));
         return false;
      } else {
         MutableComponent var4 = Component.translatable("selectWorld.edit.backupCreated", var0.getLevelId());
         MutableComponent var5 = Component.translatable("selectWorld.edit.backupSize", Mth.ceil((double)var1 / 1048576.0));
         Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastIds.WORLD_BACKUP, var4, var5));
         return true;
      }
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 15, 16777215);
      drawString(var1, this.font, NAME_LABEL, this.width / 2 - 100, 24, 10526880);
      this.nameEdit.render(var1, var2, var3, var4);
      super.render(var1, var2, var3, var4);
   }
}
