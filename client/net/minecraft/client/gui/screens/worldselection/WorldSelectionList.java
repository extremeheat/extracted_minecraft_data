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
import java.time.ZonedDateTime;
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
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.SelectableEntry;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ErrorScreen;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.LoadingDotsText;
import net.minecraft.client.gui.screens.NoticeWithLinkScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.ReportedNbtException;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.validation.ContentValidationException;
import net.minecraft.world.level.validation.ForbiddenSymlinkInfo;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class WorldSelectionList extends ObjectSelectionList<Entry> {
   public static final DateTimeFormatter DATE_FORMAT;
   private static final Identifier ERROR_HIGHLIGHTED_SPRITE;
   private static final Identifier ERROR_SPRITE;
   private static final Identifier MARKED_JOIN_HIGHLIGHTED_SPRITE;
   private static final Identifier MARKED_JOIN_SPRITE;
   private static final Identifier WARNING_HIGHLIGHTED_SPRITE;
   private static final Identifier WARNING_SPRITE;
   private static final Identifier JOIN_HIGHLIGHTED_SPRITE;
   private static final Identifier JOIN_SPRITE;
   private static final Logger LOGGER;
   private static final Component FROM_NEWER_TOOLTIP_1;
   private static final Component FROM_NEWER_TOOLTIP_2;
   private static final Component SNAPSHOT_TOOLTIP_1;
   private static final Component SNAPSHOT_TOOLTIP_2;
   private static final Component WORLD_LOCKED_TOOLTIP;
   private static final Component WORLD_REQUIRES_CONVERSION;
   private static final Component INCOMPATIBLE_VERSION_TOOLTIP;
   private static final Component WORLD_EXPERIMENTAL;
   private final Screen screen;
   private CompletableFuture<List<LevelSummary>> pendingLevels;
   private @Nullable List<LevelSummary> currentlyDisplayedLevels;
   private final LoadingHeader loadingHeader;
   private final EntryType entryType;
   private String filter;
   private boolean hasPolled;
   private final @Nullable Consumer<LevelSummary> onEntrySelect;
   private final @Nullable Consumer<WorldListEntry> onEntryInteract;

   private WorldSelectionList(final Screen screen, final Minecraft minecraft, final int width, final int height, final String filter, final @Nullable WorldSelectionList oldList, final @Nullable Consumer<LevelSummary> onEntrySelect, final @Nullable Consumer<WorldListEntry> onEntryInteract, final EntryType entryType) {
      super(minecraft, width, height, 0, 36);
      this.screen = screen;
      this.loadingHeader = new LoadingHeader(minecraft);
      this.filter = filter;
      this.onEntrySelect = onEntrySelect;
      this.onEntryInteract = onEntryInteract;
      this.entryType = entryType;
      if (oldList != null) {
         this.pendingLevels = oldList.pendingLevels;
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

   private @Nullable List<LevelSummary> pollLevelsIgnoreErrors() {
      try {
         List<LevelSummary> completedLevels = (List)this.pendingLevels.getNow((Object)null);
         if (this.entryType == WorldSelectionList.EntryType.UPLOAD_WORLD) {
            if (completedLevels == null || this.hasPolled) {
               return null;
            }

            this.hasPolled = true;
            completedLevels = completedLevels.stream().filter(LevelSummary::canUpload).toList();
         }

         return completedLevels;
      } catch (CancellationException | CompletionException var2) {
         return null;
      }
   }

   public void reloadWorldList() {
      this.pendingLevels = this.loadLevels();
   }

   public void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      List<LevelSummary> newLevels = this.pollLevelsIgnoreErrors();
      if (newLevels != this.currentlyDisplayedLevels) {
         this.handleNewLevels(newLevels);
      }

      super.extractWidgetRenderState(graphics, mouseX, mouseY, a);
   }

   private void handleNewLevels(final @Nullable List<LevelSummary> levels) {
      if (levels != null) {
         if (levels.isEmpty()) {
            switch (this.entryType.ordinal()) {
               case 0:
                  CreateWorldScreen.openFresh(this.minecraft, () -> this.minecraft.gui.setScreen((Screen)null));
                  break;
               case 1:
                  this.clearEntries();
                  this.addEntry(new NoWorldsEntry(Component.translatable("mco.upload.select.world.none"), this.screen.getFont()));
            }
         } else {
            this.fillLevels(this.filter, levels);
            this.currentlyDisplayedLevels = levels;
         }

      }
   }

   public void updateFilter(final String newFilter) {
      if (this.currentlyDisplayedLevels != null && !newFilter.equals(this.filter)) {
         this.fillLevels(newFilter, this.currentlyDisplayedLevels);
      }

      this.filter = newFilter;
   }

   private CompletableFuture<List<LevelSummary>> loadLevels() {
      LevelStorageSource.LevelCandidates levelCandidates;
      try {
         levelCandidates = this.minecraft.getLevelSource().findLevelCandidates();
      } catch (LevelStorageException e) {
         LOGGER.error("Couldn't load level list", e);
         this.handleLevelLoadFailure(e.getMessageComponent());
         return CompletableFuture.completedFuture(List.of());
      }

      return this.minecraft.getLevelSource().loadLevelSummaries(levelCandidates).exceptionally((throwable) -> {
         this.minecraft.delayCrash(CrashReport.forThrowable(throwable, "Couldn't load level list"));
         return List.of();
      });
   }

   private void fillLevels(final String filter, final List<LevelSummary> levels) {
      List<Entry> worldEntries = new ArrayList();
      Optional<WorldListEntry> selectedOpt = this.getSelectedOpt();
      WorldListEntry entryToSelect = null;

      for(LevelSummary level : levels.stream().filter((levelx) -> this.filterAccepts(filter.toLowerCase(Locale.ROOT), levelx)).toList()) {
         WorldListEntry worldListEntry = new WorldListEntry(this, level);
         if (selectedOpt.isPresent() && ((WorldListEntry)selectedOpt.get()).getLevelSummary().getLevelId().equals(worldListEntry.getLevelSummary().getLevelId())) {
            entryToSelect = worldListEntry;
         }

         worldEntries.add(worldListEntry);
      }

      this.removeEntries(this.children().stream().filter((entry) -> !worldEntries.contains(entry)).toList());
      worldEntries.forEach((entry) -> {
         if (!this.children().contains(entry)) {
            this.addEntry(entry);
         }

      });
      this.setSelected((Entry)entryToSelect);
      this.notifyListUpdated();
   }

   private boolean filterAccepts(final String filter, final LevelSummary level) {
      return level.getLevelName().toLowerCase(Locale.ROOT).contains(filter) || level.getLevelId().toLowerCase(Locale.ROOT).contains(filter);
   }

   private void notifyListUpdated() {
      this.refreshScrollAmount();
      this.screen.triggerImmediateNarration(true);
   }

   private void handleLevelLoadFailure(final Component message) {
      this.minecraft.gui.setScreen(new ErrorScreen(Component.translatable("selectWorld.unable_to_load"), message));
   }

   public int getRowWidth() {
      return 270;
   }

   public void setSelected(final @Nullable Entry selected) {
      super.setSelected(selected);
      if (this.onEntrySelect != null) {
         Consumer var10000 = this.onEntrySelect;
         LevelSummary var10001;
         if (selected instanceof WorldListEntry) {
            WorldListEntry entry = (WorldListEntry)selected;
            var10001 = entry.summary;
         } else {
            var10001 = null;
         }

         var10000.accept(var10001);
      }

   }

   public Optional<WorldListEntry> getSelectedOpt() {
      Entry selected = (Entry)this.getSelected();
      if (selected instanceof WorldListEntry worldEntry) {
         return Optional.of(worldEntry);
      } else {
         return Optional.empty();
      }
   }

   public void returnToScreen() {
      this.reloadWorldList();
      this.minecraft.gui.setScreen(this.screen);
   }

   public Screen getScreen() {
      return this.screen;
   }

   public void updateWidgetNarration(final NarrationElementOutput output) {
      if (this.children().contains(this.loadingHeader)) {
         this.loadingHeader.updateNarration(output);
      } else {
         super.updateWidgetNarration(output);
      }
   }

   static {
      DATE_FORMAT = Util.localizedDateFormatter(FormatStyle.SHORT);
      ERROR_HIGHLIGHTED_SPRITE = Identifier.withDefaultNamespace("world_list/error_highlighted");
      ERROR_SPRITE = Identifier.withDefaultNamespace("world_list/error");
      MARKED_JOIN_HIGHLIGHTED_SPRITE = Identifier.withDefaultNamespace("world_list/marked_join_highlighted");
      MARKED_JOIN_SPRITE = Identifier.withDefaultNamespace("world_list/marked_join");
      WARNING_HIGHLIGHTED_SPRITE = Identifier.withDefaultNamespace("world_list/warning_highlighted");
      WARNING_SPRITE = Identifier.withDefaultNamespace("world_list/warning");
      JOIN_HIGHLIGHTED_SPRITE = Identifier.withDefaultNamespace("world_list/join_highlighted");
      JOIN_SPRITE = Identifier.withDefaultNamespace("world_list/join");
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

      public @Nullable LevelSummary getLevelSummary() {
         return null;
      }
   }

   public static final class NoWorldsEntry extends Entry {
      private final StringWidget stringWidget;

      public NoWorldsEntry(final Component component, final Font font) {
         super();
         this.stringWidget = new StringWidget(component, font);
      }

      public Component getNarration() {
         return this.stringWidget.getMessage();
      }

      public void extractContent(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
         this.stringWidget.setPosition(this.getContentXMiddle() - this.stringWidget.getWidth() / 2, this.getContentYMiddle() - this.stringWidget.getHeight() / 2);
         this.stringWidget.extractRenderState(graphics, mouseX, mouseY, a);
      }
   }

   public final class WorldListEntry extends Entry implements SelectableEntry {
      private static final int ICON_SIZE = 32;
      private final WorldSelectionList list;
      private final Minecraft minecraft;
      private final Screen screen;
      private final LevelSummary summary;
      private final FaviconTexture icon;
      private final StringWidget worldNameText;
      private final StringWidget idAndLastPlayedText;
      private final StringWidget infoText;
      private @Nullable Path iconFile;

      public WorldListEntry(final WorldSelectionList list, final LevelSummary summary) {
         Objects.requireNonNull(WorldSelectionList.this);
         super();
         this.list = list;
         this.minecraft = list.minecraft;
         this.screen = list.getScreen();
         this.summary = summary;
         this.icon = FaviconTexture.forWorld(this.minecraft.getTextureManager(), summary.getLevelId());
         this.iconFile = summary.getIcon();
         int maxTextWidth = list.getRowWidth() - this.getTextX() - 2;
         Component worldNameComponent = Component.literal(summary.getLevelName());
         this.worldNameText = new StringWidget(worldNameComponent, this.minecraft.font);
         this.worldNameText.setMaxWidth(maxTextWidth);
         if (this.minecraft.font.width((FormattedText)worldNameComponent) > maxTextWidth) {
            this.worldNameText.setTooltip(Tooltip.create(worldNameComponent));
         }

         String levelIdAndDate = summary.getLevelId();
         long lastPlayed = summary.getLastPlayed();
         if (lastPlayed != -1L) {
            ZonedDateTime lastPlayedTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(lastPlayed), ZoneId.systemDefault());
            levelIdAndDate = levelIdAndDate + " (" + WorldSelectionList.DATE_FORMAT.format(lastPlayedTime) + ")";
         }

         Component levelIdAndDateComponent = Component.literal(levelIdAndDate).withColor(-8355712);
         this.idAndLastPlayedText = new StringWidget(levelIdAndDateComponent, this.minecraft.font);
         this.idAndLastPlayedText.setMaxWidth(maxTextWidth);
         if (this.minecraft.font.width(levelIdAndDate) > maxTextWidth) {
            this.idAndLastPlayedText.setTooltip(Tooltip.create(levelIdAndDateComponent));
         }

         Component info = ComponentUtils.mergeStyles(summary.getInfo(), Style.EMPTY.withColor(-8355712));
         this.infoText = new StringWidget(info, this.minecraft.font);
         this.infoText.setMaxWidth(maxTextWidth);
         if (this.minecraft.font.width((FormattedText)info) > maxTextWidth) {
            this.infoText.setTooltip(Tooltip.create(info));
         }

         this.validateIconFile();
         this.loadIcon();
      }

      private void validateIconFile() {
         if (this.iconFile != null) {
            try {
               BasicFileAttributes attributes = Files.readAttributes(this.iconFile, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
               if (attributes.isSymbolicLink()) {
                  List<ForbiddenSymlinkInfo> issues = this.minecraft.directoryValidator().validateSymlink(this.iconFile);
                  if (!issues.isEmpty()) {
                     WorldSelectionList.LOGGER.warn("{}", ContentValidationException.getMessage(this.iconFile, issues));
                     this.iconFile = null;
                  } else {
                     attributes = Files.readAttributes(this.iconFile, BasicFileAttributes.class);
                  }
               }

               if (!attributes.isRegularFile()) {
                  this.iconFile = null;
               }
            } catch (NoSuchFileException var3) {
               this.iconFile = null;
            } catch (IOException e) {
               WorldSelectionList.LOGGER.error("could not validate symlink", e);
               this.iconFile = null;
            }

         }
      }

      public Component getNarration() {
         Component entryNarration = Component.translatable("narrator.select.world_info", this.summary.getLevelName(), Component.translationArg(new Date(this.summary.getLastPlayed())), this.summary.getInfo());
         if (this.summary.isLocked()) {
            entryNarration = CommonComponents.joinForNarration(entryNarration, WorldSelectionList.WORLD_LOCKED_TOOLTIP);
         }

         if (this.summary.isExperimental()) {
            entryNarration = CommonComponents.joinForNarration(entryNarration, WorldSelectionList.WORLD_EXPERIMENTAL);
         }

         return Component.translatable("narrator.select", entryNarration);
      }

      public void extractContent(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
         int textX = this.getTextX();
         this.worldNameText.setPosition(textX, this.getContentY() + 1);
         this.worldNameText.extractRenderState(graphics, mouseX, mouseY, a);
         StringWidget var10000 = this.idAndLastPlayedText;
         int var10002 = this.getContentY();
         Objects.requireNonNull(this.minecraft.font);
         var10000.setPosition(textX, var10002 + 9 + 3);
         this.idAndLastPlayedText.extractRenderState(graphics, mouseX, mouseY, a);
         var10000 = this.infoText;
         var10002 = this.getContentY();
         Objects.requireNonNull(this.minecraft.font);
         var10002 += 9;
         Objects.requireNonNull(this.minecraft.font);
         var10000.setPosition(textX, var10002 + 9 + 3);
         this.infoText.extractRenderState(graphics, mouseX, mouseY, a);
         graphics.blit(RenderPipelines.GUI_TEXTURED, this.icon.textureLocation(), this.getContentX(), this.getContentY(), 0.0F, 0.0F, 32, 32, 32, 32);
         if (this.list.entryType == WorldSelectionList.EntryType.SINGLEPLAYER && hovered) {
            graphics.fill(this.getContentX(), this.getContentY(), this.getContentX() + 32, this.getContentY() + 32, -1601138544);
            int relX = mouseX - this.getContentX();
            int relY = mouseY - this.getContentY();
            boolean isOverIcon = this.mouseOverIcon(relX, relY, 32);
            Identifier joinSprite = isOverIcon ? WorldSelectionList.JOIN_HIGHLIGHTED_SPRITE : WorldSelectionList.JOIN_SPRITE;
            Identifier warningSprite = isOverIcon ? WorldSelectionList.WARNING_HIGHLIGHTED_SPRITE : WorldSelectionList.WARNING_SPRITE;
            Identifier errorSprite = isOverIcon ? WorldSelectionList.ERROR_HIGHLIGHTED_SPRITE : WorldSelectionList.ERROR_SPRITE;
            Identifier joinWithErrorSprite = isOverIcon ? WorldSelectionList.MARKED_JOIN_HIGHLIGHTED_SPRITE : WorldSelectionList.MARKED_JOIN_SPRITE;
            if (this.summary instanceof LevelSummary.SymlinkLevelSummary || this.summary instanceof LevelSummary.CorruptedLevelSummary) {
               graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)errorSprite, this.getContentX(), this.getContentY(), 32, 32);
               graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)joinWithErrorSprite, this.getContentX(), this.getContentY(), 32, 32);
               return;
            }

            if (this.summary.isLocked()) {
               graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)errorSprite, this.getContentX(), this.getContentY(), 32, 32);
               if (isOverIcon) {
                  graphics.setTooltipForNextFrame(this.minecraft.font.split(WorldSelectionList.WORLD_LOCKED_TOOLTIP, 175), mouseX, mouseY);
               }
            } else if (this.summary.requiresManualConversion()) {
               graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)errorSprite, this.getContentX(), this.getContentY(), 32, 32);
               if (isOverIcon) {
                  graphics.setTooltipForNextFrame(this.minecraft.font.split(WorldSelectionList.WORLD_REQUIRES_CONVERSION, 175), mouseX, mouseY);
               }
            } else if (!this.summary.isCompatible()) {
               graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)errorSprite, this.getContentX(), this.getContentY(), 32, 32);
               if (isOverIcon) {
                  graphics.setTooltipForNextFrame(this.minecraft.font.split(WorldSelectionList.INCOMPATIBLE_VERSION_TOOLTIP, 175), mouseX, mouseY);
               }
            } else if (this.summary.shouldBackup()) {
               graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)joinWithErrorSprite, this.getContentX(), this.getContentY(), 32, 32);
               if (this.summary.isDowngrade()) {
                  graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)errorSprite, this.getContentX(), this.getContentY(), 32, 32);
                  if (isOverIcon) {
                     graphics.setTooltipForNextFrame((List)ImmutableList.of(WorldSelectionList.FROM_NEWER_TOOLTIP_1.getVisualOrderText(), WorldSelectionList.FROM_NEWER_TOOLTIP_2.getVisualOrderText()), mouseX, mouseY);
                  }
               } else if (!SharedConstants.getCurrentVersion().stable()) {
                  graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)warningSprite, this.getContentX(), this.getContentY(), 32, 32);
                  if (isOverIcon) {
                     graphics.setTooltipForNextFrame((List)ImmutableList.of(WorldSelectionList.SNAPSHOT_TOOLTIP_1.getVisualOrderText(), WorldSelectionList.SNAPSHOT_TOOLTIP_2.getVisualOrderText()), mouseX, mouseY);
                  }
               }

               if (isOverIcon) {
                  WorldSelectionList.this.handleCursor(graphics);
               }
            } else {
               graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)joinSprite, this.getContentX(), this.getContentY(), 32, 32);
               if (isOverIcon) {
                  WorldSelectionList.this.handleCursor(graphics);
               }
            }
         }

      }

      private int getTextX() {
         return this.getContentX() + 32 + 3;
      }

      public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
         if (this.canInteract()) {
            int relX = (int)event.x() - this.getContentX();
            int relY = (int)event.y() - this.getContentY();
            if (doubleClick || this.mouseOverIcon(relX, relY, 32) && this.list.entryType == WorldSelectionList.EntryType.SINGLEPLAYER) {
               this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, 1.0F));
               Consumer<WorldListEntry> onEntryInteract = this.list.onEntryInteract;
               if (onEntryInteract != null) {
                  onEntryInteract.accept(this);
                  return true;
               }
            }
         }

         return super.mouseClicked(event, doubleClick);
      }

      public boolean keyPressed(final KeyEvent event) {
         if (event.isSelection() && this.canInteract()) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, 1.0F));
            Consumer<WorldListEntry> onEntryInteract = this.list.onEntryInteract;
            if (onEntryInteract != null) {
               onEntryInteract.accept(this);
               return true;
            }
         }

         return super.keyPressed(event);
      }

      public boolean canInteract() {
         return this.summary.primaryActionActive() || this.list.entryType == WorldSelectionList.EntryType.UPLOAD_WORLD;
      }

      public void joinWorld() {
         if (this.summary.primaryActionActive()) {
            if (this.summary instanceof LevelSummary.SymlinkLevelSummary) {
               this.minecraft.gui.setScreen(NoticeWithLinkScreen.createWorldSymlinkWarningScreen(() -> this.minecraft.gui.setScreen(this.screen)));
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
         this.minecraft.gui.setScreen(new ConfirmScreen((result) -> {
            if (result) {
               this.minecraft.gui.setScreen(new ProgressScreen(true));
               this.doDeleteWorld();
            }

            this.list.returnToScreen();
         }, Component.translatable("selectWorld.deleteQuestion"), Component.translatable("selectWorld.deleteWarning", this.summary.getLevelName()), Component.translatable("selectWorld.deleteButton"), CommonComponents.GUI_CANCEL));
      }

      public void doDeleteWorld() {
         LevelStorageSource levelSource = this.minecraft.getLevelSource();
         String levelId = this.summary.getLevelId();

         try (LevelStorageSource.LevelStorageAccess access = levelSource.createAccess(levelId)) {
            access.deleteLevel();
         } catch (IOException e) {
            SystemToast.onWorldDeleteFailure(this.minecraft, levelId);
            WorldSelectionList.LOGGER.error("Failed to delete world {}", levelId, e);
         }

      }

      public void editWorld() {
         this.queueLoadScreen();
         String levelId = this.summary.getLevelId();

         LevelStorageSource.LevelStorageAccess access;
         try {
            access = this.minecraft.getLevelSource().validateAndCreateAccess(levelId);
         } catch (IOException e) {
            SystemToast.onWorldAccessFailure(this.minecraft, levelId);
            WorldSelectionList.LOGGER.error("Failed to access level {}", levelId, e);
            this.list.reloadWorldList();
            return;
         } catch (ContentValidationException e) {
            WorldSelectionList.LOGGER.warn("{}", e.getMessage());
            this.minecraft.gui.setScreen(NoticeWithLinkScreen.createWorldSymlinkWarningScreen(() -> this.minecraft.gui.setScreen(this.screen)));
            return;
         }

         EditWorldScreen editScreen;
         try {
            editScreen = EditWorldScreen.create(this.minecraft, access, (result) -> {
               access.safeClose();
               this.list.returnToScreen();
            });
         } catch (NbtException | ReportedNbtException | IOException e) {
            access.safeClose();
            SystemToast.onWorldAccessFailure(this.minecraft, levelId);
            WorldSelectionList.LOGGER.error("Failed to load world data {}", levelId, e);
            this.list.reloadWorldList();
            return;
         }

         this.minecraft.gui.setScreen(editScreen);
      }

      public void recreateWorld() {
         this.queueLoadScreen();

         try (LevelStorageSource.LevelStorageAccess access = this.minecraft.getLevelSource().validateAndCreateAccess(this.summary.getLevelId())) {
            Pair<LevelSettings, WorldCreationContext> recreatedSettings = this.minecraft.createWorldOpenFlows().recreateWorldData(access);
            LevelSettings levelSettings = (LevelSettings)recreatedSettings.getFirst();
            WorldCreationContext creationContext = (WorldCreationContext)recreatedSettings.getSecond();
            Path dataPackDir = CreateWorldScreen.createTempDataPackDirFromExistingWorld(access.getLevelPath(LevelResource.DATAPACK_DIR), this.minecraft);
            creationContext.validate();
            if (creationContext.options().isOldCustomizedWorld()) {
               this.minecraft.gui.setScreen(new ConfirmScreen((result) -> {
                  Gui var10000 = this.minecraft.gui;
                  Object var5;
                  if (result) {
                     Minecraft var10001 = this.minecraft;
                     WorldSelectionList var10002 = this.list;
                     Objects.requireNonNull(var10002);
                     var5 = CreateWorldScreen.createFromExisting(var10001, var10002::returnToScreen, levelSettings, creationContext, dataPackDir);
                  } else {
                     var5 = this.screen;
                  }

                  var10000.setScreen((Screen)var5);
               }, Component.translatable("selectWorld.recreate.customized.title"), Component.translatable("selectWorld.recreate.customized.text"), CommonComponents.GUI_PROCEED, CommonComponents.GUI_CANCEL));
            } else {
               Gui var10000 = this.minecraft.gui;
               Minecraft var10001 = this.minecraft;
               WorldSelectionList var10002 = this.list;
               Objects.requireNonNull(var10002);
               var10000.setScreen(CreateWorldScreen.createFromExisting(var10001, var10002::returnToScreen, levelSettings, creationContext, dataPackDir));
            }
         } catch (ContentValidationException e) {
            WorldSelectionList.LOGGER.warn("{}", e.getMessage());
            this.minecraft.gui.setScreen(NoticeWithLinkScreen.createWorldSymlinkWarningScreen(() -> this.minecraft.gui.setScreen(this.screen)));
         } catch (Exception e) {
            WorldSelectionList.LOGGER.error("Unable to recreate world", e);
            this.minecraft.gui.setScreen(new AlertScreen(() -> this.minecraft.gui.setScreen(this.screen), Component.translatable("selectWorld.recreate.error.title"), Component.translatable("selectWorld.recreate.error.text")));
         }

      }

      private void queueLoadScreen() {
         this.minecraft.setScreenAndShow(new GenericMessageScreen(Component.translatable("selectWorld.data_read")));
      }

      private void loadIcon() {
         boolean shouldHaveIcon = this.iconFile != null && Files.isRegularFile(this.iconFile, new LinkOption[0]);
         if (shouldHaveIcon) {
            try {
               InputStream stream = Files.newInputStream(this.iconFile);

               try {
                  this.icon.upload(NativeImage.read(stream));
               } catch (Throwable var6) {
                  if (stream != null) {
                     try {
                        stream.close();
                     } catch (Throwable var5) {
                        var6.addSuppressed(var5);
                     }
                  }

                  throw var6;
               }

               if (stream != null) {
                  stream.close();
               }
            } catch (Throwable t) {
               WorldSelectionList.LOGGER.error("Invalid icon for world {}", this.summary.getLevelId(), t);
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

      public LoadingHeader(final Minecraft minecraft) {
         super();
         this.minecraft = minecraft;
      }

      public void extractContent(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
         int labelX = (this.minecraft.gui.screen().width - this.minecraft.font.width((FormattedText)LOADING_LABEL)) / 2;
         int var10000 = this.getContentY();
         int var10001 = this.getContentHeight();
         Objects.requireNonNull(this.minecraft.font);
         int labelY = var10000 + (var10001 - 9) / 2;
         graphics.text(this.minecraft.font, (Component)LOADING_LABEL, labelX, labelY, -1);
         String dots = LoadingDotsText.get(Util.getMillis());
         int dotsX = (this.minecraft.gui.screen().width - this.minecraft.font.width(dots)) / 2;
         Objects.requireNonNull(this.minecraft.font);
         int dotsY = labelY + 9;
         graphics.text(this.minecraft.font, dots, dotsX, dotsY, -8355712);
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
      private @Nullable WorldSelectionList oldList;
      private @Nullable Consumer<LevelSummary> onEntrySelect;
      private @Nullable Consumer<WorldListEntry> onEntryInteract;

      public Builder(final Minecraft minecraft, final Screen screen) {
         super();
         this.type = WorldSelectionList.EntryType.SINGLEPLAYER;
         this.oldList = null;
         this.onEntrySelect = null;
         this.onEntryInteract = null;
         this.minecraft = minecraft;
         this.screen = screen;
      }

      public Builder width(final int width) {
         this.width = width;
         return this;
      }

      public Builder height(final int height) {
         this.height = height;
         return this;
      }

      public Builder filter(final String filter) {
         this.filter = filter;
         return this;
      }

      public Builder oldList(final @Nullable WorldSelectionList oldList) {
         this.oldList = oldList;
         return this;
      }

      public Builder onEntrySelect(final Consumer<LevelSummary> onEntrySelect) {
         this.onEntrySelect = onEntrySelect;
         return this;
      }

      public Builder onEntryInteract(final Consumer<WorldListEntry> onEntryInteract) {
         this.onEntryInteract = onEntryInteract;
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
