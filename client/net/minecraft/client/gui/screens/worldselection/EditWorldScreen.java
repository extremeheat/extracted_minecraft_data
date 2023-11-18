package net.minecraft.client.gui.screens.worldselection;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.validation.ContentValidationException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class EditWorldScreen extends Screen {
   private static final Logger LOGGER = LogUtils.getLogger();
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
   protected void init() {
      this.renameButton = Button.builder(Component.translatable("selectWorld.edit.save"), var1x -> this.onRename())
         .bounds(this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20)
         .build();
      this.nameEdit = new EditBox(this.font, this.width / 2 - 100, 38, 200, 20, Component.translatable("selectWorld.enterName"));
      LevelSummary var1 = this.levelAccess.getSummary();
      String var2 = var1 == null ? "" : var1.getLevelName();
      this.nameEdit.setValue(var2);
      this.nameEdit.setResponder(var1x -> this.renameButton.active = !Util.isBlank(var1x));
      this.addWidget(this.nameEdit);
      Button var3 = this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.edit.resetIcon"), var1x -> {
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
      this.addRenderableWidget(this.renameButton);
      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_CANCEL, var1x -> this.callback.accept(false))
            .bounds(this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20)
            .build()
      );
      var3.active = this.levelAccess.getIconFile().filter(var0 -> Files.isRegularFile(var0)).isPresent();
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

      try (LevelStorageSource.LevelStorageAccess var3 = var0.validateAndCreateAccess(var1)) {
         var2 = true;
         makeBackupAndShowToast(var3);
      } catch (IOException var8) {
         if (!var2) {
            SystemToast.onWorldAccessFailure(Minecraft.getInstance(), var1);
         }

         LOGGER.warn("Failed to create backup of level {}", var1, var8);
      } catch (ContentValidationException var9) {
         LOGGER.warn("{}", var9.getMessage());
         SystemToast.onWorldAccessFailure(Minecraft.getInstance(), var1);
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
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, this.title, this.width / 2, 15, 16777215);
      var1.drawString(this.font, NAME_LABEL, this.width / 2 - 100 + 1, 24, 10526880);
      this.nameEdit.render(var1, var2, var3, var4);
   }
}
