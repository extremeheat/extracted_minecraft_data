package net.minecraft.client.gui.screens.worldselection;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.ChatFormatting;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.ReportedNbtException;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class EditWorldScreen extends Screen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component NAME_LABEL = Component.translatable("selectWorld.enterName").withStyle(ChatFormatting.GRAY);
   private static final Component RESET_ICON_BUTTON = Component.translatable("selectWorld.edit.resetIcon");
   private static final Component FOLDER_BUTTON = Component.translatable("selectWorld.edit.openFolder");
   private static final Component BACKUP_BUTTON = Component.translatable("selectWorld.edit.backup");
   private static final Component BACKUP_FOLDER_BUTTON = Component.translatable("selectWorld.edit.backupFolder");
   private static final Component OPTIMIZE_BUTTON = Component.translatable("selectWorld.edit.optimize");
   private static final Component OPTIMIZE_TITLE = Component.translatable("optimizeWorld.confirm.title");
   private static final Component OPTIMIIZE_DESCRIPTION = Component.translatable("optimizeWorld.confirm.description");
   private static final Component SAVE_BUTTON = Component.translatable("selectWorld.edit.save");
   private static final int DEFAULT_WIDTH = 200;
   private static final int VERTICAL_SPACING = 4;
   private static final int HALF_WIDTH = 98;
   private final LinearLayout layout = LinearLayout.vertical().spacing(5);
   private final BooleanConsumer callback;
   private final LevelStorageSource.LevelStorageAccess levelAccess;

   public static EditWorldScreen create(Minecraft var0, LevelStorageSource.LevelStorageAccess var1, BooleanConsumer var2) throws IOException {
      LevelSummary var3 = var1.getSummary(var1.getDataTag());
      return new EditWorldScreen(var0, var1, var3.getLevelName(), var2);
   }

   private EditWorldScreen(Minecraft var1, LevelStorageSource.LevelStorageAccess var2, String var3, BooleanConsumer var4) {
      super(Component.translatable("selectWorld.edit.title"));
      this.callback = var4;
      this.levelAccess = var2;
      Font var5 = var1.font;
      this.layout.addChild(new SpacerElement(200, 20));
      this.layout.addChild(new StringWidget(NAME_LABEL, var5));
      EditBox var6 = this.layout.addChild(new EditBox(var5, 200, 20, NAME_LABEL));
      var6.setValue(var3);
      LinearLayout var7 = LinearLayout.horizontal().spacing(4);
      Button var8 = var7.addChild(Button.builder(SAVE_BUTTON, var2x -> this.onRename(var6.getValue())).width(98).build());
      var7.addChild(Button.builder(CommonComponents.GUI_CANCEL, var1x -> this.onClose()).width(98).build());
      var6.setResponder(var1x -> var8.active = !Util.isBlank(var1x));
      this.layout.addChild(Button.builder(RESET_ICON_BUTTON, var1x -> {
         var2.getIconFile().ifPresent(var0x -> FileUtils.deleteQuietly(var0x.toFile()));
         var1x.active = false;
      }).width(200).build()).active = var2.getIconFile().filter(var0 -> Files.isRegularFile(var0)).isPresent();
      this.layout
         .addChild(Button.builder(FOLDER_BUTTON, var1x -> Util.getPlatform().openFile(var2.getLevelPath(LevelResource.ROOT).toFile())).width(200).build());
      this.layout.addChild(Button.builder(BACKUP_BUTTON, var2x -> {
         boolean var3xx = makeBackupAndShowToast(var2);
         this.callback.accept(!var3xx);
      }).width(200).build());
      this.layout.addChild(Button.builder(BACKUP_FOLDER_BUTTON, var1x -> {
         LevelStorageSource var2xx = var1.getLevelSource();
         Path var3xx = var2xx.getBackupPath();

         try {
            FileUtil.createDirectoriesSafe(var3xx);
         } catch (IOException var5xx) {
            throw new RuntimeException(var5xx);
         }

         Util.getPlatform().openFile(var3xx.toFile());
      }).width(200).build());
      this.layout.addChild(Button.builder(OPTIMIZE_BUTTON, var3x -> var1.setScreen(new BackupConfirmScreen(() -> var1.setScreen(this), (var3xx, var4x) -> {
            if (var3xx) {
               makeBackupAndShowToast(var2);
            }

            var1.setScreen(OptimizeWorldScreen.create(var1, this.callback, var1.getFixerUpper(), var2, var4x));
         }, OPTIMIZE_TITLE, OPTIMIIZE_DESCRIPTION, true))).width(200).build());
      this.layout.addChild(new SpacerElement(200, 20));
      this.layout.addChild(var7);
      this.setInitialFocus(var6);
      this.layout.visitWidgets(var1x -> {
      });
   }

   @Override
   protected void init() {
      this.repositionElements();
   }

   @Override
   protected void repositionElements() {
      this.layout.arrangeElements();
      FrameLayout.centerInRectangle(this.layout, this.getRectangle());
   }

   @Override
   public void onClose() {
      this.callback.accept(false);
   }

   private void onRename(String var1) {
      try {
         this.levelAccess.renameLevel(var1);
      } catch (NbtException | ReportedNbtException | IOException var3) {
         LOGGER.error("Failed to access world '{}'", this.levelAccess.getLevelId(), var3);
         SystemToast.onWorldAccessFailure(this.minecraft, this.levelAccess.getLevelId());
      }

      this.callback.accept(true);
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
         Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastId.WORLD_BACKUP, var7, var8));
         return false;
      } else {
         MutableComponent var4 = Component.translatable("selectWorld.edit.backupCreated", var0.getLevelId());
         MutableComponent var5 = Component.translatable("selectWorld.edit.backupSize", Mth.ceil((double)var1 / 1048576.0));
         Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastId.WORLD_BACKUP, var4, var5));
         return true;
      }
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, this.title, this.width / 2, 15, 16777215);
   }
}
