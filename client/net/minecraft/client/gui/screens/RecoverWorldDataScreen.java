package net.minecraft.client.gui.screens;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.worldselection.EditWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.ReportedNbtException;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.CommonLinks;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.slf4j.Logger;

public class RecoverWorldDataScreen extends Screen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int SCREEN_SIDE_MARGIN = 25;
   private static final Component TITLE;
   private static final Component BUGTRACKER_BUTTON;
   private static final Component RESTORE_BUTTON;
   private static final Component NO_FALLBACK_TOOLTIP;
   private static final Component DONE_TITLE;
   private static final Component DONE_SUCCESS;
   private static final Component DONE_FAILED;
   private static final Component NO_ISSUES;
   private static final Component MISSING_FILE;
   private final BooleanConsumer callback;
   private final LinearLayout layout = LinearLayout.vertical().spacing(8);
   private final Component message;
   private final MultiLineTextWidget messageWidget;
   private final MultiLineTextWidget issuesWidget;
   private final LevelStorageSource.LevelStorageAccess storageAccess;

   public RecoverWorldDataScreen(Minecraft var1, BooleanConsumer var2, LevelStorageSource.LevelStorageAccess var3) {
      super(TITLE);
      this.callback = var2;
      this.message = Component.translatable("recover_world.message", Component.literal(var3.getLevelId()).withStyle(ChatFormatting.GRAY));
      this.messageWidget = new MultiLineTextWidget(this.message, var1.font);
      this.storageAccess = var3;
      Exception var4 = this.collectIssue(var3, false);
      Exception var5 = this.collectIssue(var3, true);
      MutableComponent var6 = Component.empty().append(this.buildInfo(var3, false, var4)).append("\n").append(this.buildInfo(var3, true, var5));
      this.issuesWidget = new MultiLineTextWidget(var6, var1.font);
      boolean var7 = var4 != null && var5 == null;
      this.layout.defaultCellSetting().alignHorizontallyCenter();
      this.layout.addChild(new StringWidget(this.title, var1.font));
      this.layout.addChild(this.messageWidget.setCentered(true));
      this.layout.addChild(this.issuesWidget);
      LinearLayout var8 = LinearLayout.horizontal().spacing(5);
      var8.addChild(Button.builder(BUGTRACKER_BUTTON, ConfirmLinkScreen.confirmLink(this, (URI)CommonLinks.SNAPSHOT_BUGS_FEEDBACK)).size(120, 20).build());
      ((Button)var8.addChild(Button.builder(RESTORE_BUTTON, (var2x) -> {
         this.attemptRestore(var1);
      }).size(120, 20).tooltip(var7 ? null : Tooltip.create(NO_FALLBACK_TOOLTIP)).build())).active = var7;
      this.layout.addChild(var8);
      this.layout.addChild(Button.builder(CommonComponents.GUI_BACK, (var1x) -> {
         this.onClose();
      }).size(120, 20).build());
      this.layout.visitWidgets(this::addRenderableWidget);
   }

   private void attemptRestore(Minecraft var1) {
      Exception var2 = this.collectIssue(this.storageAccess, false);
      Exception var3 = this.collectIssue(this.storageAccess, true);
      if (var2 != null && var3 == null) {
         var1.forceSetScreen(new GenericMessageScreen(Component.translatable("recover_world.restoring")));
         EditWorldScreen.makeBackupAndShowToast(this.storageAccess);
         if (this.storageAccess.restoreLevelDataFromOld()) {
            var1.setScreen(new ConfirmScreen(this.callback, DONE_TITLE, DONE_SUCCESS, CommonComponents.GUI_CONTINUE, CommonComponents.GUI_BACK));
         } else {
            var1.setScreen(new AlertScreen(() -> {
               this.callback.accept(false);
            }, DONE_TITLE, DONE_FAILED));
         }

      } else {
         LOGGER.error("Failed to recover world, files not as expected. level.dat: {}, level.dat_old: {}", var2 != null ? var2.getMessage() : "no issues", var3 != null ? var3.getMessage() : "no issues");
         var1.setScreen(new AlertScreen(() -> {
            this.callback.accept(false);
         }, DONE_TITLE, DONE_FAILED));
      }
   }

   private Component buildInfo(LevelStorageSource.LevelStorageAccess var1, boolean var2, @Nullable Exception var3) {
      if (var2 && var3 instanceof FileNotFoundException) {
         return Component.empty();
      } else {
         MutableComponent var4 = Component.empty();
         Instant var5 = var1.getFileModificationTime(var2);
         MutableComponent var6 = var5 != null ? Component.literal(WorldSelectionList.DATE_FORMAT.format(var5)) : Component.translatable("recover_world.state_entry.unknown");
         var4.append((Component)Component.translatable("recover_world.state_entry", var6.withStyle(ChatFormatting.GRAY)));
         if (var3 == null) {
            var4.append(NO_ISSUES);
         } else if (var3 instanceof FileNotFoundException) {
            var4.append(MISSING_FILE);
         } else if (var3 instanceof ReportedNbtException) {
            var4.append((Component)Component.literal(var3.getCause().toString()).withStyle(ChatFormatting.RED));
         } else {
            var4.append((Component)Component.literal(var3.toString()).withStyle(ChatFormatting.RED));
         }

         return var4;
      }
   }

   @Nullable
   private Exception collectIssue(LevelStorageSource.LevelStorageAccess var1, boolean var2) {
      try {
         if (!var2) {
            var1.getSummary(var1.getDataTag());
         } else {
            var1.getSummary(var1.getDataTagFallback());
         }

         return null;
      } catch (NbtException | ReportedNbtException | IOException var4) {
         return var4;
      }
   }

   protected void init() {
      super.init();
      this.repositionElements();
   }

   protected void repositionElements() {
      this.issuesWidget.setMaxWidth(this.width - 50);
      this.messageWidget.setMaxWidth(this.width - 50);
      this.layout.arrangeElements();
      FrameLayout.centerInRectangle(this.layout, this.getRectangle());
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), this.message);
   }

   public void onClose() {
      this.callback.accept(false);
   }

   static {
      TITLE = Component.translatable("recover_world.title").withStyle(ChatFormatting.BOLD);
      BUGTRACKER_BUTTON = Component.translatable("recover_world.bug_tracker");
      RESTORE_BUTTON = Component.translatable("recover_world.restore");
      NO_FALLBACK_TOOLTIP = Component.translatable("recover_world.no_fallback");
      DONE_TITLE = Component.translatable("recover_world.done.title");
      DONE_SUCCESS = Component.translatable("recover_world.done.success");
      DONE_FAILED = Component.translatable("recover_world.done.failed");
      NO_ISSUES = Component.translatable("recover_world.issue.none").withStyle(ChatFormatting.GREEN);
      MISSING_FILE = Component.translatable("recover_world.issue.missing_file").withStyle(ChatFormatting.RED);
   }
}
