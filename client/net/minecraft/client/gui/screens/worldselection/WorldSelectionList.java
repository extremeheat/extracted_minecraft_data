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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
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
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.language.I18n;
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
import org.apache.commons.lang3.StringUtils;
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
   private final SelectWorldScreen screen;
   private CompletableFuture<List<LevelSummary>> pendingLevels;
   @Nullable
   private List<LevelSummary> currentlyDisplayedLevels;
   private String filter;
   private final LoadingHeader loadingHeader;

   public WorldSelectionList(SelectWorldScreen var1, Minecraft var2, int var3, int var4, int var5, int var6, String var7, @Nullable WorldSelectionList var8) {
      super(var2, var3, var4, var5, var6);
      this.screen = var1;
      this.loadingHeader = new LoadingHeader(var2);
      this.filter = var7;
      if (var8 != null) {
         this.pendingLevels = var8.pendingLevels;
      } else {
         this.pendingLevels = this.loadLevels();
      }

      this.handleNewLevels(this.pollLevelsIgnoreErrors());
   }

   protected void clearEntries() {
      this.children().forEach(Entry::close);
      super.clearEntries();
   }

   @Nullable
   private List<LevelSummary> pollLevelsIgnoreErrors() {
      try {
         return (List)this.pendingLevels.getNow((Object)null);
      } catch (CancellationException | CompletionException var2) {
         return null;
      }
   }

   void reloadWorldList() {
      this.pendingLevels = this.loadLevels();
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (CommonInputs.selected(var1)) {
         Optional var4 = this.getSelectedOpt();
         if (var4.isPresent()) {
            if (((WorldListEntry)var4.get()).canJoin()) {
               this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, 1.0F));
               ((WorldListEntry)var4.get()).joinWorld();
            }

            return true;
         }
      }

      return super.keyPressed(var1, var2, var3);
   }

   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      List var5 = this.pollLevelsIgnoreErrors();
      if (var5 != this.currentlyDisplayedLevels) {
         this.handleNewLevels(var5);
      }

      super.renderWidget(var1, var2, var3, var4);
   }

   private void handleNewLevels(@Nullable List<LevelSummary> var1) {
      if (var1 == null) {
         this.fillLoadingLevels();
      } else {
         this.fillLevels(this.filter, var1);
      }

      this.currentlyDisplayedLevels = var1;
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

      if (var1.isEmpty()) {
         CreateWorldScreen.openFresh(this.minecraft, (Screen)null);
         return CompletableFuture.completedFuture(List.of());
      } else {
         return this.minecraft.getLevelSource().loadLevelSummaries(var1).exceptionally((var1x) -> {
            this.minecraft.delayCrash(CrashReport.forThrowable(var1x, "Couldn't load level list"));
            return List.of();
         });
      }
   }

   private void fillLevels(String var1, List<LevelSummary> var2) {
      this.clearEntries();
      var1 = var1.toLowerCase(Locale.ROOT);
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         LevelSummary var4 = (LevelSummary)var3.next();
         if (this.filterAccepts(var1, var4)) {
            this.addEntry(new WorldListEntry(this, var4));
         }
      }

      this.notifyListUpdated();
   }

   private boolean filterAccepts(String var1, LevelSummary var2) {
      return var2.getLevelName().toLowerCase(Locale.ROOT).contains(var1) || var2.getLevelId().toLowerCase(Locale.ROOT).contains(var1);
   }

   private void fillLoadingLevels() {
      this.clearEntries();
      this.addEntry(this.loadingHeader);
      this.notifyListUpdated();
   }

   private void notifyListUpdated() {
      this.clampScrollAmount();
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
      SelectWorldScreen var10000 = this.screen;
      LevelSummary var10001;
      if (var1 instanceof WorldListEntry var2) {
         var10001 = var2.summary;
      } else {
         var10001 = null;
      }

      var10000.updateButtonStatus(var10001);
   }

   public Optional<WorldListEntry> getSelectedOpt() {
      Entry var1 = (Entry)this.getSelected();
      if (var1 instanceof WorldListEntry var2) {
         return Optional.of(var2);
      } else {
         return Optional.empty();
      }
   }

   public SelectWorldScreen getScreen() {
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

   public static class LoadingHeader extends Entry {
      private static final Component LOADING_LABEL = Component.translatable("selectWorld.loading_list");
      private final Minecraft minecraft;

      public LoadingHeader(Minecraft var1) {
         super();
         this.minecraft = var1;
      }

      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         int var11 = (this.minecraft.screen.width - this.minecraft.font.width((FormattedText)LOADING_LABEL)) / 2;
         Objects.requireNonNull(this.minecraft.font);
         int var12 = var3 + (var6 - 9) / 2;
         var1.drawString(this.minecraft.font, LOADING_LABEL, var11, var12, 16777215, false);
         String var13 = LoadingDotsText.get(Util.getMillis());
         int var14 = (this.minecraft.screen.width - this.minecraft.font.width(var13)) / 2;
         Objects.requireNonNull(this.minecraft.font);
         int var15 = var12 + 9;
         var1.drawString(this.minecraft.font, var13, var14, var15, -8355712, false);
      }

      public Component getNarration() {
         return LOADING_LABEL;
      }
   }

   public final class WorldListEntry extends Entry {
      private static final int ICON_WIDTH = 32;
      private static final int ICON_HEIGHT = 32;
      private final Minecraft minecraft;
      private final SelectWorldScreen screen;
      final LevelSummary summary;
      private final FaviconTexture icon;
      @Nullable
      private Path iconFile;
      private long lastClickTime;

      public WorldListEntry(final WorldSelectionList var2, final LevelSummary var3) {
         super();
         this.minecraft = var2.minecraft;
         this.screen = var2.getScreen();
         this.summary = var3;
         this.icon = FaviconTexture.forWorld(this.minecraft.getTextureManager(), var3.getLevelId());
         this.iconFile = var3.getIcon();
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

      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         String var11 = this.summary.getLevelName();
         String var12 = this.summary.getLevelId();
         long var13 = this.summary.getLastPlayed();
         if (var13 != -1L) {
            var12 = var12 + " (" + WorldSelectionList.DATE_FORMAT.format(Instant.ofEpochMilli(var13)) + ")";
         }

         if (StringUtils.isEmpty(var11)) {
            String var10000 = I18n.get("selectWorld.world");
            var11 = var10000 + " " + (var2 + 1);
         }

         Component var15 = this.summary.getInfo();
         var1.drawString(this.minecraft.font, var11, var4 + 32 + 3, var3 + 1, 16777215, false);
         Font var10001 = this.minecraft.font;
         int var10003 = var4 + 32 + 3;
         Objects.requireNonNull(this.minecraft.font);
         var1.drawString(var10001, var12, var10003, var3 + 9 + 3, -8355712, false);
         var10001 = this.minecraft.font;
         var10003 = var4 + 32 + 3;
         Objects.requireNonNull(this.minecraft.font);
         int var10004 = var3 + 9;
         Objects.requireNonNull(this.minecraft.font);
         var1.drawString(var10001, var15, var10003, var10004 + 9 + 3, -8355712, false);
         var1.blit(RenderType::guiTextured, this.icon.textureLocation(), var4, var3, 0.0F, 0.0F, 32, 32, 32, 32);
         if ((Boolean)this.minecraft.options.touchscreen().get() || var9) {
            var1.fill(var4, var3, var4 + 32, var3 + 32, -1601138544);
            int var16 = var7 - var4;
            boolean var17 = var16 < 32;
            ResourceLocation var18 = var17 ? WorldSelectionList.JOIN_HIGHLIGHTED_SPRITE : WorldSelectionList.JOIN_SPRITE;
            ResourceLocation var19 = var17 ? WorldSelectionList.WARNING_HIGHLIGHTED_SPRITE : WorldSelectionList.WARNING_SPRITE;
            ResourceLocation var20 = var17 ? WorldSelectionList.ERROR_HIGHLIGHTED_SPRITE : WorldSelectionList.ERROR_SPRITE;
            ResourceLocation var21 = var17 ? WorldSelectionList.MARKED_JOIN_HIGHLIGHTED_SPRITE : WorldSelectionList.MARKED_JOIN_SPRITE;
            if (this.summary instanceof LevelSummary.SymlinkLevelSummary || this.summary instanceof LevelSummary.CorruptedLevelSummary) {
               var1.blitSprite(RenderType::guiTextured, (ResourceLocation)var20, var4, var3, 32, 32);
               var1.blitSprite(RenderType::guiTextured, (ResourceLocation)var21, var4, var3, 32, 32);
               return;
            }

            if (this.summary.isLocked()) {
               var1.blitSprite(RenderType::guiTextured, (ResourceLocation)var20, var4, var3, 32, 32);
               if (var17) {
                  this.screen.setTooltipForNextRenderPass(this.minecraft.font.split(WorldSelectionList.WORLD_LOCKED_TOOLTIP, 175));
               }
            } else if (this.summary.requiresManualConversion()) {
               var1.blitSprite(RenderType::guiTextured, (ResourceLocation)var20, var4, var3, 32, 32);
               if (var17) {
                  this.screen.setTooltipForNextRenderPass(this.minecraft.font.split(WorldSelectionList.WORLD_REQUIRES_CONVERSION, 175));
               }
            } else if (!this.summary.isCompatible()) {
               var1.blitSprite(RenderType::guiTextured, (ResourceLocation)var20, var4, var3, 32, 32);
               if (var17) {
                  this.screen.setTooltipForNextRenderPass(this.minecraft.font.split(WorldSelectionList.INCOMPATIBLE_VERSION_TOOLTIP, 175));
               }
            } else if (this.summary.shouldBackup()) {
               var1.blitSprite(RenderType::guiTextured, (ResourceLocation)var21, var4, var3, 32, 32);
               if (this.summary.isDowngrade()) {
                  var1.blitSprite(RenderType::guiTextured, (ResourceLocation)var20, var4, var3, 32, 32);
                  if (var17) {
                     this.screen.setTooltipForNextRenderPass(ImmutableList.of(WorldSelectionList.FROM_NEWER_TOOLTIP_1.getVisualOrderText(), WorldSelectionList.FROM_NEWER_TOOLTIP_2.getVisualOrderText()));
                  }
               } else if (!SharedConstants.getCurrentVersion().isStable()) {
                  var1.blitSprite(RenderType::guiTextured, (ResourceLocation)var19, var4, var3, 32, 32);
                  if (var17) {
                     this.screen.setTooltipForNextRenderPass(ImmutableList.of(WorldSelectionList.SNAPSHOT_TOOLTIP_1.getVisualOrderText(), WorldSelectionList.SNAPSHOT_TOOLTIP_2.getVisualOrderText()));
                  }
               }
            } else {
               var1.blitSprite(RenderType::guiTextured, (ResourceLocation)var18, var4, var3, 32, 32);
            }
         }

      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         if (!this.summary.primaryActionActive()) {
            return true;
         } else {
            WorldSelectionList.this.setSelected((Entry)this);
            if (!(var1 - (double)WorldSelectionList.this.getRowLeft() <= 32.0) && Util.getMillis() - this.lastClickTime >= 250L) {
               this.lastClickTime = Util.getMillis();
               return super.mouseClicked(var1, var3, var5);
            } else {
               if (this.canJoin()) {
                  this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, 1.0F));
                  this.joinWorld();
               }

               return true;
            }
         }
      }

      public boolean canJoin() {
         return this.summary.primaryActionActive();
      }

      public void joinWorld() {
         if (this.summary.primaryActionActive()) {
            if (this.summary instanceof LevelSummary.SymlinkLevelSummary) {
               this.minecraft.setScreen(NoticeWithLinkScreen.createWorldSymlinkWarningScreen(() -> {
                  this.minecraft.setScreen(this.screen);
               }));
            } else {
               this.minecraft.createWorldOpenFlows().openWorld(this.summary.getLevelId(), () -> {
                  WorldSelectionList.this.reloadWorldList();
                  this.minecraft.setScreen(this.screen);
               });
            }
         }
      }

      public void deleteWorld() {
         this.minecraft.setScreen(new ConfirmScreen((var1) -> {
            if (var1) {
               this.minecraft.setScreen(new ProgressScreen(true));
               this.doDeleteWorld();
            }

            this.minecraft.setScreen(this.screen);
         }, Component.translatable("selectWorld.deleteQuestion"), Component.translatable("selectWorld.deleteWarning", this.summary.getLevelName()), Component.translatable("selectWorld.deleteButton"), CommonComponents.GUI_CANCEL));
      }

      public void doDeleteWorld() {
         LevelStorageSource var1 = this.minecraft.getLevelSource();
         String var2 = this.summary.getLevelId();

         try {
            LevelStorageSource.LevelStorageAccess var3 = var1.createAccess(var2);

            try {
               var3.deleteLevel();
            } catch (Throwable var7) {
               if (var3 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }
               }

               throw var7;
            }

            if (var3 != null) {
               var3.close();
            }
         } catch (IOException var8) {
            SystemToast.onWorldDeleteFailure(this.minecraft, var2);
            WorldSelectionList.LOGGER.error("Failed to delete world {}", var2, var8);
         }

         WorldSelectionList.this.reloadWorldList();
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
            WorldSelectionList.this.reloadWorldList();
            return;
         } catch (ContentValidationException var7) {
            WorldSelectionList.LOGGER.warn("{}", var7.getMessage());
            this.minecraft.setScreen(NoticeWithLinkScreen.createWorldSymlinkWarningScreen(() -> {
               this.minecraft.setScreen(this.screen);
            }));
            return;
         }

         EditWorldScreen var3;
         try {
            var3 = EditWorldScreen.create(this.minecraft, var2, (var2x) -> {
               var2.safeClose();
               if (var2x) {
                  WorldSelectionList.this.reloadWorldList();
               }

               this.minecraft.setScreen(this.screen);
            });
         } catch (NbtException | ReportedNbtException | IOException var5) {
            var2.safeClose();
            SystemToast.onWorldAccessFailure(this.minecraft, var1);
            WorldSelectionList.LOGGER.error("Failed to load world data {}", var1, var5);
            WorldSelectionList.this.reloadWorldList();
            return;
         }

         this.minecraft.setScreen(var3);
      }

      public void recreateWorld() {
         this.queueLoadScreen();

         try {
            LevelStorageSource.LevelStorageAccess var1 = this.minecraft.getLevelSource().validateAndCreateAccess(this.summary.getLevelId());

            try {
               Pair var2 = this.minecraft.createWorldOpenFlows().recreateWorldData(var1);
               LevelSettings var3 = (LevelSettings)var2.getFirst();
               WorldCreationContext var4 = (WorldCreationContext)var2.getSecond();
               Path var5 = CreateWorldScreen.createTempDataPackDirFromExistingWorld(var1.getLevelPath(LevelResource.DATAPACK_DIR), this.minecraft);
               var4.validate();
               if (var4.options().isOldCustomizedWorld()) {
                  this.minecraft.setScreen(new ConfirmScreen((var4x) -> {
                     this.minecraft.setScreen((Screen)(var4x ? CreateWorldScreen.createFromExisting(this.minecraft, this.screen, var3, var4, var5) : this.screen));
                  }, Component.translatable("selectWorld.recreate.customized.title"), Component.translatable("selectWorld.recreate.customized.text"), CommonComponents.GUI_PROCEED, CommonComponents.GUI_CANCEL));
               } else {
                  this.minecraft.setScreen(CreateWorldScreen.createFromExisting(this.minecraft, this.screen, var3, var4, var5));
               }
            } catch (Throwable var7) {
               if (var1 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }
               }

               throw var7;
            }

            if (var1 != null) {
               var1.close();
            }
         } catch (ContentValidationException var8) {
            WorldSelectionList.LOGGER.warn("{}", var8.getMessage());
            this.minecraft.setScreen(NoticeWithLinkScreen.createWorldSymlinkWarningScreen(() -> {
               this.minecraft.setScreen(this.screen);
            }));
         } catch (Exception var9) {
            WorldSelectionList.LOGGER.error("Unable to recreate world", var9);
            this.minecraft.setScreen(new AlertScreen(() -> {
               this.minecraft.setScreen(this.screen);
            }, Component.translatable("selectWorld.recreate.error.title"), Component.translatable("selectWorld.recreate.error.text")));
         }

      }

      private void queueLoadScreen() {
         this.minecraft.forceSetScreen(new GenericMessageScreen(Component.translatable("selectWorld.data_read")));
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
         this.icon.close();
      }

      public String getLevelName() {
         return this.summary.getLevelName();
      }
   }

   public abstract static class Entry extends ObjectSelectionList.Entry<Entry> implements AutoCloseable {
      public Entry() {
         super();
      }

      public void close() {
      }
   }
}
