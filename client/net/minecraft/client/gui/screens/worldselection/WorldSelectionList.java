package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ErrorScreen;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.LoadingDotsText;
import net.minecraft.client.gui.screens.NoticeWithLinkScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.ReportedNbtException;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.validation.ContentValidationException;
import org.slf4j.Logger;

public class WorldSelectionList extends ObjectSelectionList<Entry> {
   public static final DateTimeFormatter DATE_FORMAT;
   static final ResourceLocation ERROR_HIGHLIGHTED_SPRITE;
   static final ResourceLocation ERROR_SPRITE;
   static final ResourceLocation MARKED_JOIN_HIGHLIGHTED_SPRITE;
   static final ResourceLocation MARKED_JOIN_SPRITE;
   static final ResourceLocation WARNING_HIGHLIGHTED_SPRITE;
   static final ResourceLocation WARNING_SPRITE;
   static final ResourceLocation JOIN_HIGHLIGHTED_SPRITE;
   static final ResourceLocation JOIN_SPRITE;
   static final Logger LOGGER;
   static final Component FROM_NEWER_TOOLTIP_1;
   static final Component FROM_NEWER_TOOLTIP_2;
   static final Component SNAPSHOT_TOOLTIP_1;
   static final Component SNAPSHOT_TOOLTIP_2;
   static final Component WORLD_LOCKED_TOOLTIP;
   static final Component WORLD_REQUIRES_CONVERSION;
   static final Component INCOMPATIBLE_VERSION_TOOLTIP;
   static final Component WORLD_EXPERIMENTAL;
   private final Screen screen;
   private CompletableFuture<List<LevelSummary>> pendingLevels;
   @Nullable
   private List<LevelSummary> currentlyDisplayedLevels;
   private final LoadingHeader loadingHeader;
   final EntryType entryType;
   private String filter;
   private boolean hasPolled;
   @Nullable
   private final Consumer<LevelSummary> onEntrySelect;
   @Nullable
   final Consumer<WorldListEntry> onEntryInteract;

   WorldSelectionList(Screen var1, Minecraft var2, int var3, int var4, String var5, @Nullable WorldSelectionList var6, @Nullable Consumer<LevelSummary> var7, @Nullable Consumer<WorldListEntry> var8, EntryType var9) {
      super(var2, var3, var4, 0, 36);
      this.screen = var1;
      this.loadingHeader = new LoadingHeader(var2);
      this.filter = var5;
      this.onEntrySelect = var7;
      this.onEntryInteract = var8;
      this.entryType = var9;
      if (var6 != null) {
         this.pendingLevels = var6.pendingLevels;
      } else {
         this.pendingLevels = this.loadLevels();
      }

      this.addEntry(this.loadingHeader);
      this.handleNewLevels(this.pollLevelsIgnoreErrors());
   }

   protected void clearEntries() {
      this.children().forEach(Entry::close);
      super.clearEntries();
   }

   @Nullable
   private List<LevelSummary> pollLevelsIgnoreErrors() {
      try {
         List var1 = (List)this.pendingLevels.getNow((Object)null);
         if (this.entryType == WorldSelectionList.EntryType.UPLOAD_WORLD) {
            if (var1 == null || this.hasPolled) {
               return null;
            }

            this.hasPolled = true;
            var1 = var1.stream().filter(LevelSummary::canUpload).toList();
         }

         return var1;
      } catch (CancellationException | CompletionException var2) {
         return null;
      }
   }

   public void reloadWorldList() {
      this.pendingLevels = this.loadLevels();
   }

   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      List var5 = this.pollLevelsIgnoreErrors();
      if (var5 != this.currentlyDisplayedLevels) {
         this.handleNewLevels(var5);
      }

      super.renderWidget(var1, var2, var3, var4);
   }

   private void handleNewLevels(@Nullable List<LevelSummary> var1) {
      if (var1 != null) {
         if (var1.isEmpty()) {
            switch (this.entryType.ordinal()) {
               case 0:
                  CreateWorldScreen.openFresh(this.minecraft, (Runnable)null);
                  break;
               case 1:
                  this.clearEntries();
                  this.addEntry(new NoWorldsEntry(Component.translatable("mco.upload.select.world.none"), this.screen.getFont()));
            }
         } else {
            this.fillLevels(this.filter, var1);
            this.currentlyDisplayedLevels = var1;
         }

      }
   }

   public void updateFilter(String var1) {
      if (this.currentlyDisplayedLevels != null && !var1.equals(this.filter)) {
         this.fillLevels(var1, this.currentlyDisplayedLevels);
      }

      this.filter = var1;
   }

   private CompletableFuture<List<LevelSummary>> loadLevels() {
      LevelStorageSource.LevelCandidates var1;
      try {
         var1 = this.minecraft.getLevelSource().findLevelCandidates();
      } catch (LevelStorageException var3) {
         LOGGER.error("Couldn't load level list", var3);
         this.handleLevelLoadFailure(var3.getMessageComponent());
         return CompletableFuture.completedFuture(List.of());
      }

      return this.minecraft.getLevelSource().loadLevelSummaries(var1).exceptionally((var1x) -> {
         this.minecraft.delayCrash(CrashReport.forThrowable(var1x, "Couldn't load level list"));
         return List.of();
      });
   }

   private void fillLevels(String var1, List<LevelSummary> var2) {
      ArrayList var3 = new ArrayList();
      Optional var4 = this.getSelectedOpt();
      WorldListEntry var5 = null;

      for(LevelSummary var7 : var2.stream().filter((var2x) -> this.filterAccepts(var1.toLowerCase(Locale.ROOT), var2x)).toList()) {
         WorldListEntry var8 = new WorldListEntry(this, var7);
         if (var4.isPresent() && ((WorldListEntry)var4.get()).getLevelSummary().getLevelId().equals(var8.getLevelSummary().getLevelId())) {
            var5 = var8;
         }

         var3.add(var8);
      }

      this.removeEntries(this.children().stream().filter((var1x) -> !var3.contains(var1x)).toList());
      var3.forEach((var1x) -> {
         if (!this.children().contains(var1x)) {
            this.addEntry(var1x);
         }

      });
      this.setSelected((Entry)var5);
      this.notifyListUpdated();
   }

   private boolean filterAccepts(String var1, LevelSummary var2) {
      return var2.getLevelName().toLowerCase(Locale.ROOT).contains(var1) || var2.getLevelId().toLowerCase(Locale.ROOT).contains(var1);
   }

   private void notifyListUpdated() {
      this.refreshScrollAmount();
      this.screen.triggerImmediateNarration(true);
   }

   private void handleLevelLoadFailure(Component var1) {
      this.minecraft.setScreen(new ErrorScreen(Component.translatable("selectWorld.unable_to_load"), var1));
   }

   public int getRowWidth() {
      return 270;
   }

   public void setSelected(@Nullable Entry var1) {
      super.setSelected(var1);
      if (this.onEntrySelect != null) {
         Consumer var10000 = this.onEntrySelect;
         LevelSummary var10001;
         if (var1 instanceof WorldListEntry) {
            WorldListEntry var2 = (WorldListEntry)var1;
            var10001 = var2.summary;
         } else {
            var10001 = null;
         }

         var10000.accept(var10001);
      }

   }

   public Optional<WorldListEntry> getSelectedOpt() {
      Entry var1 = (Entry)this.getSelected();
      if (var1 instanceof WorldListEntry var2) {
         return Optional.of(var2);
      } else {
         return Optional.empty();
      }
   }

   public void returnToScreen() {
      this.reloadWorldList();
      this.minecraft.setScreen(this.screen);
   }

   public Screen getScreen() {
      return this.screen;
   }

   public void updateWidgetNarration(NarrationElementOutput var1) {
      if (this.children().contains(this.loadingHeader)) {
         this.loadingHeader.updateNarration(var1);
      } else {
         super.updateWidgetNarration(var1);
      }
   }

   static {
      DATE_FORMAT = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withZone(ZoneId.systemDefault());
      ERROR_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("world_list/error_highlighted");
      ERROR_SPRITE = ResourceLocation.withDefaultNamespace("world_list/error");
      MARKED_JOIN_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("world_list/marked_join_highlighted");
      MARKED_JOIN_SPRITE = ResourceLocation.withDefaultNamespace("world_list/marked_join");
      WARNING_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("world_list/warning_highlighted");
      WARNING_SPRITE = ResourceLocation.withDefaultNamespace("world_list/warning");
      JOIN_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("world_list/join_highlighted");
      JOIN_SPRITE = ResourceLocation.withDefaultNamespace("world_list/join");
      LOGGER = LogUtils.getLogger();
      FROM_NEWER_TOOLTIP_1 = Component.translatable("selectWorld.tooltip.fromNewerVersion1").withStyle(ChatFormatting.RED);
      FROM_NEWER_TOOLTIP_2 = Component.translatable("selectWorld.tooltip.fromNewerVersion2").withStyle(ChatFormatting.RED);
      SNAPSHOT_TOOLTIP_1 = Component.translatable("selectWorld.tooltip.snapshot1").withStyle(ChatFormatting.GOLD);
      SNAPSHOT_TOOLTIP_2 = Component.translatable("selectWorld.tooltip.snapshot2").withStyle(ChatFormatting.GOLD);
      WORLD_LOCKED_TOOLTIP = Component.translatable("selectWorld.locked").withStyle(ChatFormatting.RED);
      WORLD_REQUIRES_CONVERSION = Component.translatable("selectWorld.conversion.tooltip").withStyle(ChatFormatting.RED);
      INCOMPATIBLE_VERSION_TOOLTIP = Component.translatable("selectWorld.incompatible.tooltip").withStyle(ChatFormatting.RED);
      WORLD_EXPERIMENTAL = Component.translatable("selectWorld.experimental");
   }

   public abstract static class Entry extends ObjectSelectionList.Entry<Entry> implements AutoCloseable {
      public Entry() {
         super();
      }

      public void close() {
      }

      @Nullable
      public LevelSummary getLevelSummary() {
         return null;
      }
   }

   public static final class NoWorldsEntry extends Entry {
      private final StringWidget stringWidget;

      public NoWorldsEntry(Component var1, Font var2) {
         super();
         this.stringWidget = new StringWidget(var1, var2);
      }

      public Component getNarration() {
         return this.stringWidget.getMessage();
      }

      public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
         this.stringWidget.setPosition(this.getContentXMiddle() - this.stringWidget.getWidth() / 2, this.getContentYMiddle() - this.stringWidget.getHeight() / 2);
         this.stringWidget.render(var1, var2, var3, var5);
      }
   }

   public static final class WorldListEntry extends Entry {
      private static final int ICON_WIDTH = 32;
      private static final int ICON_HEIGHT = 32;
      private final WorldSelectionList list;
      private final Minecraft minecraft;
      private final Screen screen;
      final LevelSummary summary;
      private final FaviconTexture icon;
      private final StringWidget worldNameText;
      private final StringWidget idAndLastPlayedText;
      private final StringWidget infoText;
      @Nullable
      private Path iconFile;

      public WorldListEntry(WorldSelectionList var1, LevelSummary var2) {
         super();
         this.list = var1;
         this.minecraft = var1.minecraft;
         this.screen = var1.getScreen();
         this.summary = var2;
         this.icon = FaviconTexture.forWorld(this.minecraft.getTextureManager(), var2.getLevelId());
         this.iconFile = var2.getIcon();
         int var3 = var1.getRowWidth() - this.getTextX() - 2;
         MutableComponent var4 = Component.literal(var2.getLevelName());
         this.worldNameText = new StringWidget(var4, this.minecraft.font);
         this.worldNameText.setMaxWidth(var3);
         if (this.minecraft.font.width((FormattedText)var4) > var3) {
            this.worldNameText.setTooltip(Tooltip.create(var4));
         }

         String var5 = var2.getLevelId();
         long var6 = var2.getLastPlayed();
         if (var6 != -1L) {
            var5 = var5 + " (" + WorldSelectionList.DATE_FORMAT.format(Instant.ofEpochMilli(var6)) + ")";
         }

         MutableComponent var8 = Component.literal(var5);
         this.idAndLastPlayedText = (new StringWidget(var8, this.minecraft.font)).setColor(-8355712);
         this.idAndLastPlayedText.setMaxWidth(var3);
         if (this.minecraft.font.width(var5) > var3) {
            this.idAndLastPlayedText.setTooltip(Tooltip.create(var8));
         }

         Component var9 = var2.getInfo();
         this.infoText = (new StringWidget(var9, this.minecraft.font)).setColor(-8355712);
         this.infoText.setMaxWidth(var3);
         if (this.minecraft.font.width((FormattedText)var9) > var3) {
            this.infoText.setTooltip(Tooltip.create(var9));
         }

         this.validateIconFile();
         this.loadIcon();
      }

      private void validateIconFile() {
         if (this.iconFile != null) {
            try {
               BasicFileAttributes var1 = Files.readAttributes(this.iconFile, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
               if (var1.isSymbolicLink()) {
                  List var2 = this.minecraft.directoryValidator().validateSymlink(this.iconFile);
                  if (!var2.isEmpty()) {
                     WorldSelectionList.LOGGER.warn("{}", ContentValidationException.getMessage(this.iconFile, var2));
                     this.iconFile = null;
                  } else {
                     var1 = Files.readAttributes(this.iconFile, BasicFileAttributes.class);
                  }
               }

               if (!var1.isRegularFile()) {
                  this.iconFile = null;
               }
            } catch (NoSuchFileException var3) {
               this.iconFile = null;
            } catch (IOException var4) {
               WorldSelectionList.LOGGER.error("could not validate symlink", var4);
               this.iconFile = null;
            }

         }
      }

      public Component getNarration() {
         MutableComponent var1 = Component.translatable("narrator.select.world_info", this.summary.getLevelName(), Component.translationArg(new Date(this.summary.getLastPlayed())), this.summary.getInfo());
         if (this.summary.isLocked()) {
            var1 = CommonComponents.joinForNarration(var1, WorldSelectionList.WORLD_LOCKED_TOOLTIP);
         }

         if (this.summary.isExperimental()) {
            var1 = CommonComponents.joinForNarration(var1, WorldSelectionList.WORLD_EXPERIMENTAL);
         }

         return Component.translatable("narrator.select", var1);
      }

      public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
         int var6 = this.getTextX();
         this.worldNameText.setPosition(var6, this.getContentY() + 1);
         this.worldNameText.render(var1, var2, var3, var5);
         StringWidget var10000 = this.idAndLastPlayedText;
         int var10002 = this.getContentY();
         Objects.requireNonNull(this.minecraft.font);
         var10000.setPosition(var6, var10002 + 9 + 3);
         this.idAndLastPlayedText.render(var1, var2, var3, var5);
         var10000 = this.infoText;
         var10002 = this.getContentY();
         Objects.requireNonNull(this.minecraft.font);
         var10002 += 9;
         Objects.requireNonNull(this.minecraft.font);
         var10000.setPosition(var6, var10002 + 9 + 3);
         this.infoText.render(var1, var2, var3, var5);
         var1.blit(RenderPipelines.GUI_TEXTURED, this.icon.textureLocation(), this.getContentX(), this.getContentY(), 0.0F, 0.0F, 32, 32, 32, 32);
         if (this.list.entryType == WorldSelectionList.EntryType.SINGLEPLAYER && ((Boolean)this.minecraft.options.touchscreen().get() || var4)) {
            var1.fill(this.getContentX(), this.getContentY(), this.getContentX() + 32, this.getContentY() + 32, -1601138544);
            int var7 = var2 - this.getContentX();
            boolean var8 = var7 < 32;
            ResourceLocation var9 = var8 ? WorldSelectionList.JOIN_HIGHLIGHTED_SPRITE : WorldSelectionList.JOIN_SPRITE;
            ResourceLocation var10 = var8 ? WorldSelectionList.WARNING_HIGHLIGHTED_SPRITE : WorldSelectionList.WARNING_SPRITE;
            ResourceLocation var11 = var8 ? WorldSelectionList.ERROR_HIGHLIGHTED_SPRITE : WorldSelectionList.ERROR_SPRITE;
            ResourceLocation var12 = var8 ? WorldSelectionList.MARKED_JOIN_HIGHLIGHTED_SPRITE : WorldSelectionList.MARKED_JOIN_SPRITE;
            if (this.summary instanceof LevelSummary.SymlinkLevelSummary || this.summary instanceof LevelSummary.CorruptedLevelSummary) {
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)var11, this.getContentX(), this.getContentY(), 32, 32);
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)var12, this.getContentX(), this.getContentY(), 32, 32);
               return;
            }

            if (this.summary.isLocked()) {
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)var11, this.getContentX(), this.getContentY(), 32, 32);
               if (var8) {
                  var1.setTooltipForNextFrame(this.minecraft.font.split(WorldSelectionList.WORLD_LOCKED_TOOLTIP, 175), var2, var3);
               }
            } else if (this.summary.requiresManualConversion()) {
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)var11, this.getContentX(), this.getContentY(), 32, 32);
               if (var8) {
                  var1.setTooltipForNextFrame(this.minecraft.font.split(WorldSelectionList.WORLD_REQUIRES_CONVERSION, 175), var2, var3);
               }
            } else if (!this.summary.isCompatible()) {
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)var11, this.getContentX(), this.getContentY(), 32, 32);
               if (var8) {
                  var1.setTooltipForNextFrame(this.minecraft.font.split(WorldSelectionList.INCOMPATIBLE_VERSION_TOOLTIP, 175), var2, var3);
               }
            } else if (this.summary.shouldBackup()) {
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)var12, this.getContentX(), this.getContentY(), 32, 32);
               if (this.summary.isDowngrade()) {
                  var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)var11, this.getContentX(), this.getContentY(), 32, 32);
                  if (var8) {
                     var1.setTooltipForNextFrame((List)ImmutableList.of(WorldSelectionList.FROM_NEWER_TOOLTIP_1.getVisualOrderText(), WorldSelectionList.FROM_NEWER_TOOLTIP_2.getVisualOrderText()), var2, var3);
                  }
               } else if (!SharedConstants.getCurrentVersion().stable()) {
                  var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)var10, this.getContentX(), this.getContentY(), 32, 32);
                  if (var8) {
                     var1.setTooltipForNextFrame((List)ImmutableList.of(WorldSelectionList.SNAPSHOT_TOOLTIP_1.getVisualOrderText(), WorldSelectionList.SNAPSHOT_TOOLTIP_2.getVisualOrderText()), var2, var3);
                  }
               }
            } else {
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)var9, this.getContentX(), this.getContentY(), 32, 32);
            }
         }

      }

      private int getTextX() {
         return this.getContentX() + 32 + 3;
      }

      public boolean mouseClicked(double var1, double var3, int var5, boolean var6) {
         if (this.canInteract() && (var6 || var1 - (double)this.list.getRowLeft() <= 32.0 && this.list.entryType == WorldSelectionList.EntryType.SINGLEPLAYER)) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, 1.0F));
            Consumer var7 = this.list.onEntryInteract;
            if (var7 != null) {
               var7.accept(this);
               return true;
            }
         }

         return super.mouseClicked(var1, var3, var5, var6);
      }

      public boolean keyPressed(int var1, int var2, int var3) {
         if (CommonInputs.selected(var1) && this.canInteract()) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, 1.0F));
            Consumer var4 = this.list.onEntryInteract;
            if (var4 != null) {
               var4.accept(this);
               return true;
            }
         }

         return super.keyPressed(var1, var2, var3);
      }

      public boolean canInteract() {
         return this.summary.primaryActionActive() || this.list.entryType == WorldSelectionList.EntryType.UPLOAD_WORLD;
      }

      public void joinWorld() {
         if (this.summary.primaryActionActive()) {
            if (this.summary instanceof LevelSummary.SymlinkLevelSummary) {
               this.minecraft.setScreen(NoticeWithLinkScreen.createWorldSymlinkWarningScreen(() -> this.minecraft.setScreen(this.screen)));
            } else {
               WorldOpenFlows var10000 = this.minecraft.createWorldOpenFlows();
               String var10001 = this.summary.getLevelId();
               WorldSelectionList var10002 = this.list;
               Objects.requireNonNull(var10002);
               var10000.openWorld(var10001, var10002::returnToScreen);
            }
         }
      }

      public void deleteWorld() {
         this.minecraft.setScreen(new ConfirmScreen((var1) -> {
            if (var1) {
               this.minecraft.setScreen(new ProgressScreen(true));
               this.doDeleteWorld();
            }

            this.list.returnToScreen();
         }, Component.translatable("selectWorld.deleteQuestion"), Component.translatable("selectWorld.deleteWarning", this.summary.getLevelName()), Component.translatable("selectWorld.deleteButton"), CommonComponents.GUI_CANCEL));
      }

      public void doDeleteWorld() {
         LevelStorageSource var1 = this.minecraft.getLevelSource();
         String var2 = this.summary.getLevelId();

         try (LevelStorageSource.LevelStorageAccess var3 = var1.createAccess(var2)) {
            var3.deleteLevel();
         } catch (IOException var8) {
            SystemToast.onWorldDeleteFailure(this.minecraft, var2);
            WorldSelectionList.LOGGER.error("Failed to delete world {}", var2, var8);
         }

      }

      public void editWorld() {
         this.queueLoadScreen();
         String var1 = this.summary.getLevelId();

         LevelStorageSource.LevelStorageAccess var2;
         try {
            var2 = this.minecraft.getLevelSource().validateAndCreateAccess(var1);
         } catch (IOException var6) {
            SystemToast.onWorldAccessFailure(this.minecraft, var1);
            WorldSelectionList.LOGGER.error("Failed to access level {}", var1, var6);
            this.list.reloadWorldList();
            return;
         } catch (ContentValidationException var7) {
            WorldSelectionList.LOGGER.warn("{}", var7.getMessage());
            this.minecraft.setScreen(NoticeWithLinkScreen.createWorldSymlinkWarningScreen(() -> this.minecraft.setScreen(this.screen)));
            return;
         }

         EditWorldScreen var3;
         try {
            var3 = EditWorldScreen.create(this.minecraft, var2, (var2x) -> {
               var2.safeClose();
               this.list.returnToScreen();
            });
         } catch (NbtException | ReportedNbtException | IOException var5) {
            var2.safeClose();
            SystemToast.onWorldAccessFailure(this.minecraft, var1);
            WorldSelectionList.LOGGER.error("Failed to load world data {}", var1, var5);
            this.list.reloadWorldList();
            return;
         }

         this.minecraft.setScreen(var3);
      }

      public void recreateWorld() {
         this.queueLoadScreen();

         try (LevelStorageSource.LevelStorageAccess var1 = this.minecraft.getLevelSource().validateAndCreateAccess(this.summary.getLevelId())) {
            Pair var2 = this.minecraft.createWorldOpenFlows().recreateWorldData(var1);
            LevelSettings var3 = (LevelSettings)var2.getFirst();
            WorldCreationContext var4 = (WorldCreationContext)var2.getSecond();
            Path var5 = CreateWorldScreen.createTempDataPackDirFromExistingWorld(var1.getLevelPath(LevelResource.DATAPACK_DIR), this.minecraft);
            var4.validate();
            if (var4.options().isOldCustomizedWorld()) {
               this.minecraft.setScreen(new ConfirmScreen((var4x) -> {
                  Minecraft var10000 = this.minecraft;
                  Object var5x;
                  if (var4x) {
                     Minecraft var10001 = this.minecraft;
                     WorldSelectionList var10002 = this.list;
                     Objects.requireNonNull(var10002);
                     var5x = CreateWorldScreen.createFromExisting(var10001, var10002::returnToScreen, var3, var4, var5);
                  } else {
                     var5x = this.screen;
                  }

                  var10000.setScreen((Screen)var5x);
               }, Component.translatable("selectWorld.recreate.customized.title"), Component.translatable("selectWorld.recreate.customized.text"), CommonComponents.GUI_PROCEED, CommonComponents.GUI_CANCEL));
            } else {
               Minecraft var10000 = this.minecraft;
               Minecraft var10001 = this.minecraft;
               WorldSelectionList var10002 = this.list;
               Objects.requireNonNull(var10002);
               var10000.setScreen(CreateWorldScreen.createFromExisting(var10001, var10002::returnToScreen, var3, var4, var5));
            }
         } catch (ContentValidationException var8) {
            WorldSelectionList.LOGGER.warn("{}", var8.getMessage());
            this.minecraft.setScreen(NoticeWithLinkScreen.createWorldSymlinkWarningScreen(() -> this.minecraft.setScreen(this.screen)));
         } catch (Exception var9) {
            WorldSelectionList.LOGGER.error("Unable to recreate world", var9);
            this.minecraft.setScreen(new AlertScreen(() -> this.minecraft.setScreen(this.screen), Component.translatable("selectWorld.recreate.error.title"), Component.translatable("selectWorld.recreate.error.text")));
         }

      }

      private void queueLoadScreen() {
         this.minecraft.setScreenAndShow(new GenericMessageScreen(Component.translatable("selectWorld.data_read")));
      }

      private void loadIcon() {
         boolean var1 = this.iconFile != null && Files.isRegularFile(this.iconFile, new LinkOption[0]);
         if (var1) {
            try {
               InputStream var2 = Files.newInputStream(this.iconFile);

               try {
                  this.icon.upload(NativeImage.read(var2));
               } catch (Throwable var6) {
                  if (var2 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var5) {
                        var6.addSuppressed(var5);
                     }
                  }

                  throw var6;
               }

               if (var2 != null) {
                  var2.close();
               }
            } catch (Throwable var7) {
               WorldSelectionList.LOGGER.error("Invalid icon for world {}", this.summary.getLevelId(), var7);
               this.iconFile = null;
            }
         } else {
            this.icon.clear();
         }

      }

      public void close() {
         if (!this.icon.isClosed()) {
            this.icon.close();
         }

      }

      public String getLevelName() {
         return this.summary.getLevelName();
      }

      public LevelSummary getLevelSummary() {
         return this.summary;
      }
   }

   public static class LoadingHeader extends Entry {
      private static final Component LOADING_LABEL = Component.translatable("selectWorld.loading_list");
      private final Minecraft minecraft;

      public LoadingHeader(Minecraft var1) {
         super();
         this.minecraft = var1;
      }

      public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
         int var6 = (this.minecraft.screen.width - this.minecraft.font.width((FormattedText)LOADING_LABEL)) / 2;
         int var10000 = this.getContentY();
         int var10001 = this.getContentHeight();
         Objects.requireNonNull(this.minecraft.font);
         int var7 = var10000 + (var10001 - 9) / 2;
         var1.drawString(this.minecraft.font, (Component)LOADING_LABEL, var6, var7, -1);
         String var8 = LoadingDotsText.get(Util.getMillis());
         int var9 = (this.minecraft.screen.width - this.minecraft.font.width(var8)) / 2;
         Objects.requireNonNull(this.minecraft.font);
         int var10 = var7 + 9;
         var1.drawString(this.minecraft.font, var8, var9, var10, -8355712);
      }

      public Component getNarration() {
         return LOADING_LABEL;
      }
   }

   public static class Builder {
      private final Minecraft minecraft;
      private final Screen screen;
      private int width;
      private int height;
      private String filter = "";
      private EntryType type;
      @Nullable
      private WorldSelectionList oldList;
      @Nullable
      private Consumer<LevelSummary> onEntrySelect;
      @Nullable
      private Consumer<WorldListEntry> onEntryInteract;

      public Builder(Minecraft var1, Screen var2) {
         super();
         this.type = WorldSelectionList.EntryType.SINGLEPLAYER;
         this.oldList = null;
         this.onEntrySelect = null;
         this.onEntryInteract = null;
         this.minecraft = var1;
         this.screen = var2;
      }

      public Builder width(int var1) {
         this.width = var1;
         return this;
      }

      public Builder height(int var1) {
         this.height = var1;
         return this;
      }

      public Builder filter(String var1) {
         this.filter = var1;
         return this;
      }

      public Builder oldList(@Nullable WorldSelectionList var1) {
         this.oldList = var1;
         return this;
      }

      public Builder onEntrySelect(Consumer<LevelSummary> var1) {
         this.onEntrySelect = var1;
         return this;
      }

      public Builder onEntryInteract(Consumer<WorldListEntry> var1) {
         this.onEntryInteract = var1;
         return this;
      }

      public Builder uploadWorld() {
         this.type = WorldSelectionList.EntryType.UPLOAD_WORLD;
         return this;
      }

      public WorldSelectionList build() {
         return new WorldSelectionList(this.screen, this.minecraft, this.width, this.height, this.filter, this.oldList, this.onEntrySelect, this.onEntryInteract, this.type);
      }
   }

   public static enum EntryType {
      SINGLEPLAYER,
      UPLOAD_WORLD;

      private EntryType() {
      }

      // $FF: synthetic method
      private static EntryType[] $values() {
         return new EntryType[]{SINGLEPLAYER, UPLOAD_WORLD};
      }
   }
}
