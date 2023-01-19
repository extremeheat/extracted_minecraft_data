package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ErrorScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.LoadingDotsText;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class WorldSelectionList extends ObjectSelectionList<WorldSelectionList.Entry> {
   static final Logger LOGGER = LogUtils.getLogger();
   static final DateFormat DATE_FORMAT = new SimpleDateFormat();
   static final ResourceLocation ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
   static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/world_selection.png");
   static final Component FROM_NEWER_TOOLTIP_1 = Component.translatable("selectWorld.tooltip.fromNewerVersion1").withStyle(ChatFormatting.RED);
   static final Component FROM_NEWER_TOOLTIP_2 = Component.translatable("selectWorld.tooltip.fromNewerVersion2").withStyle(ChatFormatting.RED);
   static final Component SNAPSHOT_TOOLTIP_1 = Component.translatable("selectWorld.tooltip.snapshot1").withStyle(ChatFormatting.GOLD);
   static final Component SNAPSHOT_TOOLTIP_2 = Component.translatable("selectWorld.tooltip.snapshot2").withStyle(ChatFormatting.GOLD);
   static final Component WORLD_LOCKED_TOOLTIP = Component.translatable("selectWorld.locked").withStyle(ChatFormatting.RED);
   static final Component WORLD_REQUIRES_CONVERSION = Component.translatable("selectWorld.conversion.tooltip").withStyle(ChatFormatting.RED);
   private final SelectWorldScreen screen;
   private CompletableFuture<List<LevelSummary>> pendingLevels;
   @Nullable
   private List<LevelSummary> currentlyDisplayedLevels;
   private String filter;
   private final WorldSelectionList.LoadingHeader loadingHeader;

   public WorldSelectionList(
      SelectWorldScreen var1, Minecraft var2, int var3, int var4, int var5, int var6, int var7, String var8, @Nullable WorldSelectionList var9
   ) {
      super(var2, var3, var4, var5, var6, var7);
      this.screen = var1;
      this.loadingHeader = new WorldSelectionList.LoadingHeader(var2);
      this.filter = var8;
      if (var9 != null) {
         this.pendingLevels = var9.pendingLevels;
      } else {
         this.pendingLevels = this.loadLevels();
      }

      this.handleNewLevels(this.pollLevelsIgnoreErrors());
   }

   @Nullable
   private List<LevelSummary> pollLevelsIgnoreErrors() {
      try {
         return this.pendingLevels.getNow(null);
      } catch (CancellationException | CompletionException var2) {
         return null;
      }
   }

   void reloadWorldList() {
      this.pendingLevels = this.loadLevels();
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      List var5 = this.pollLevelsIgnoreErrors();
      if (var5 != this.currentlyDisplayedLevels) {
         this.handleNewLevels(var5);
      }

      super.render(var1, var2, var3, var4);
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
         CreateWorldScreen.openFresh(this.minecraft, null);
         return CompletableFuture.completedFuture(List.of());
      } else {
         return this.minecraft.getLevelSource().loadLevelSummaries(var1).exceptionally(var1x -> {
            this.minecraft.delayCrash(CrashReport.forThrowable(var1x, "Couldn't load level list"));
            return List.of();
         });
      }
   }

   private void fillLevels(String var1, List<LevelSummary> var2) {
      this.clearEntries();
      var1 = var1.toLowerCase(Locale.ROOT);

      for(LevelSummary var4 : var2) {
         if (this.filterAccepts(var1, var4)) {
            this.addEntry(new WorldSelectionList.WorldListEntry(this, var4));
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
      this.screen.triggerImmediateNarration(true);
   }

   private void handleLevelLoadFailure(Component var1) {
      this.minecraft.setScreen(new ErrorScreen(Component.translatable("selectWorld.unable_to_load"), var1));
   }

   @Override
   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 20;
   }

   @Override
   public int getRowWidth() {
      return super.getRowWidth() + 50;
   }

   @Override
   protected boolean isFocused() {
      return this.screen.getFocused() == this;
   }

   public void setSelected(@Nullable WorldSelectionList.Entry var1) {
      super.setSelected(var1);
      this.screen.updateButtonStatus(var1 != null && var1.isSelectable());
   }

   @Override
   protected void moveSelection(AbstractSelectionList.SelectionDirection var1) {
      this.moveSelection(var1, WorldSelectionList.Entry::isSelectable);
   }

   public Optional<WorldSelectionList.WorldListEntry> getSelectedOpt() {
      WorldSelectionList.Entry var1 = this.getSelected();
      return var1 instanceof WorldSelectionList.WorldListEntry var2 ? Optional.of(var2) : Optional.empty();
   }

   public SelectWorldScreen getScreen() {
      return this.screen;
   }

   @Override
   public void updateNarration(NarrationElementOutput var1) {
      if (this.children().contains(this.loadingHeader)) {
         this.loadingHeader.updateNarration(var1);
      } else {
         super.updateNarration(var1);
      }
   }

   public abstract static class Entry extends ObjectSelectionList.Entry<WorldSelectionList.Entry> implements AutoCloseable {
      public Entry() {
         super();
      }

      public abstract boolean isSelectable();

      @Override
      public void close() {
      }
   }

   public static class LoadingHeader extends WorldSelectionList.Entry {
      private static final Component LOADING_LABEL = Component.translatable("selectWorld.loading_list");
      private final Minecraft minecraft;

      public LoadingHeader(Minecraft var1) {
         super();
         this.minecraft = var1;
      }

      @Override
      public void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         int var11 = (this.minecraft.screen.width - this.minecraft.font.width(LOADING_LABEL)) / 2;
         int var12 = var3 + (var6 - 9) / 2;
         this.minecraft.font.draw(var1, LOADING_LABEL, (float)var11, (float)var12, 16777215);
         String var13 = LoadingDotsText.get(Util.getMillis());
         int var14 = (this.minecraft.screen.width - this.minecraft.font.width(var13)) / 2;
         int var15 = var12 + 9;
         this.minecraft.font.draw(var1, var13, (float)var14, (float)var15, 8421504);
      }

      @Override
      public Component getNarration() {
         return LOADING_LABEL;
      }

      @Override
      public boolean isSelectable() {
         return false;
      }
   }

   public final class WorldListEntry extends WorldSelectionList.Entry implements AutoCloseable {
      private static final int ICON_WIDTH = 32;
      private static final int ICON_HEIGHT = 32;
      private static final int ICON_OVERLAY_X_JOIN = 0;
      private static final int ICON_OVERLAY_X_JOIN_WITH_NOTIFY = 32;
      private static final int ICON_OVERLAY_X_WARNING = 64;
      private static final int ICON_OVERLAY_X_ERROR = 96;
      private static final int ICON_OVERLAY_Y_UNSELECTED = 0;
      private static final int ICON_OVERLAY_Y_SELECTED = 32;
      private final Minecraft minecraft;
      private final SelectWorldScreen screen;
      private final LevelSummary summary;
      private final ResourceLocation iconLocation;
      @Nullable
      private Path iconFile;
      @Nullable
      private final DynamicTexture icon;
      private long lastClickTime;

      public WorldListEntry(WorldSelectionList var2, LevelSummary var3) {
         super();
         this.minecraft = var2.minecraft;
         this.screen = var2.getScreen();
         this.summary = var3;
         String var4 = var3.getLevelId();
         this.iconLocation = new ResourceLocation(
            "minecraft", "worlds/" + Util.sanitizeName(var4, ResourceLocation::validPathChar) + "/" + Hashing.sha1().hashUnencodedChars(var4) + "/icon"
         );
         this.iconFile = var3.getIcon();
         if (!Files.isRegularFile(this.iconFile)) {
            this.iconFile = null;
         }

         this.icon = this.loadServerIcon();
      }

      @Override
      public Component getNarration() {
         MutableComponent var1 = Component.translatable(
            "narrator.select.world",
            this.summary.getLevelName(),
            new Date(this.summary.getLastPlayed()),
            this.summary.isHardcore()
               ? Component.translatable("gameMode.hardcore")
               : Component.translatable("gameMode." + this.summary.getGameMode().getName()),
            this.summary.hasCheats() ? Component.translatable("selectWorld.cheats") : CommonComponents.EMPTY,
            this.summary.getWorldVersionName()
         );
         MutableComponent var2;
         if (this.summary.isLocked()) {
            var2 = CommonComponents.joinForNarration(var1, WorldSelectionList.WORLD_LOCKED_TOOLTIP);
         } else {
            var2 = var1;
         }

         return Component.translatable("narrator.select", var2);
      }

      @Override
      public void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         String var11 = this.summary.getLevelName();
         String var12 = this.summary.getLevelId() + " (" + WorldSelectionList.DATE_FORMAT.format(new Date(this.summary.getLastPlayed())) + ")";
         if (StringUtils.isEmpty(var11)) {
            var11 = I18n.get("selectWorld.world") + " " + (var2 + 1);
         }

         Component var13 = this.summary.getInfo();
         this.minecraft.font.draw(var1, var11, (float)(var4 + 32 + 3), (float)(var3 + 1), 16777215);
         this.minecraft.font.draw(var1, var12, (float)(var4 + 32 + 3), (float)(var3 + 9 + 3), 8421504);
         this.minecraft.font.draw(var1, var13, (float)(var4 + 32 + 3), (float)(var3 + 9 + 9 + 3), 8421504);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, this.icon != null ? this.iconLocation : WorldSelectionList.ICON_MISSING);
         RenderSystem.enableBlend();
         GuiComponent.blit(var1, var4, var3, 0.0F, 0.0F, 32, 32, 32, 32);
         RenderSystem.disableBlend();
         if (this.minecraft.options.touchscreen().get() || var9) {
            RenderSystem.setShaderTexture(0, WorldSelectionList.ICON_OVERLAY_LOCATION);
            GuiComponent.fill(var1, var4, var3, var4 + 32, var3 + 32, -1601138544);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            int var14 = var7 - var4;
            boolean var15 = var14 < 32;
            int var16 = var15 ? 32 : 0;
            if (this.summary.isLocked()) {
               GuiComponent.blit(var1, var4, var3, 96.0F, (float)var16, 32, 32, 256, 256);
               if (var15) {
                  this.screen.setTooltipForNextRenderPass(this.minecraft.font.split(WorldSelectionList.WORLD_LOCKED_TOOLTIP, 175));
               }
            } else if (this.summary.requiresManualConversion()) {
               GuiComponent.blit(var1, var4, var3, 96.0F, (float)var16, 32, 32, 256, 256);
               if (var15) {
                  this.screen.setTooltipForNextRenderPass(this.minecraft.font.split(WorldSelectionList.WORLD_REQUIRES_CONVERSION, 175));
               }
            } else if (this.summary.markVersionInList()) {
               GuiComponent.blit(var1, var4, var3, 32.0F, (float)var16, 32, 32, 256, 256);
               if (this.summary.askToOpenWorld()) {
                  GuiComponent.blit(var1, var4, var3, 96.0F, (float)var16, 32, 32, 256, 256);
                  if (var15) {
                     this.screen
                        .setTooltipForNextRenderPass(
                           ImmutableList.of(
                              WorldSelectionList.FROM_NEWER_TOOLTIP_1.getVisualOrderText(), WorldSelectionList.FROM_NEWER_TOOLTIP_2.getVisualOrderText()
                           )
                        );
                  }
               } else if (!SharedConstants.getCurrentVersion().isStable()) {
                  GuiComponent.blit(var1, var4, var3, 64.0F, (float)var16, 32, 32, 256, 256);
                  if (var15) {
                     this.screen
                        .setTooltipForNextRenderPass(
                           ImmutableList.of(
                              WorldSelectionList.SNAPSHOT_TOOLTIP_1.getVisualOrderText(), WorldSelectionList.SNAPSHOT_TOOLTIP_2.getVisualOrderText()
                           )
                        );
                  }
               }
            } else {
               GuiComponent.blit(var1, var4, var3, 0.0F, (float)var16, 32, 32, 256, 256);
            }
         }
      }

      @Override
      public boolean mouseClicked(double var1, double var3, int var5) {
         if (this.summary.isDisabled()) {
            return true;
         } else {
            WorldSelectionList.this.setSelected((WorldSelectionList.Entry)this);
            this.screen.updateButtonStatus(WorldSelectionList.this.getSelectedOpt().isPresent());
            if (var1 - (double)WorldSelectionList.this.getRowLeft() <= 32.0) {
               this.joinWorld();
               return true;
            } else if (Util.getMillis() - this.lastClickTime < 250L) {
               this.joinWorld();
               return true;
            } else {
               this.lastClickTime = Util.getMillis();
               return false;
            }
         }
      }

      public void joinWorld() {
         if (!this.summary.isDisabled()) {
            LevelSummary.BackupStatus var1 = this.summary.backupStatus();
            if (var1.shouldBackup()) {
               String var2 = "selectWorld.backupQuestion." + var1.getTranslationKey();
               String var3 = "selectWorld.backupWarning." + var1.getTranslationKey();
               MutableComponent var4 = Component.translatable(var2);
               if (var1.isSevere()) {
                  var4.withStyle(ChatFormatting.BOLD, ChatFormatting.RED);
               }

               MutableComponent var5 = Component.translatable(var3, this.summary.getWorldVersionName(), SharedConstants.getCurrentVersion().getName());
               this.minecraft.setScreen(new BackupConfirmScreen(this.screen, (var1x, var2x) -> {
                  if (var1x) {
                     String var3x = this.summary.getLevelId();

                     try (LevelStorageSource.LevelStorageAccess var4x = this.minecraft.getLevelSource().createAccess(var3x)) {
                        EditWorldScreen.makeBackupAndShowToast(var4x);
                     } catch (IOException var9) {
                        SystemToast.onWorldAccessFailure(this.minecraft, var3x);
                        WorldSelectionList.LOGGER.error("Failed to backup level {}", var3x, var9);
                     }
                  }

                  this.loadWorld();
               }, var4, var5, false));
            } else if (this.summary.askToOpenWorld()) {
               this.minecraft
                  .setScreen(
                     new ConfirmScreen(
                        var1x -> {
                           if (var1x) {
                              try {
                                 this.loadWorld();
                              } catch (Exception var3x) {
                                 WorldSelectionList.LOGGER.error("Failure to open 'future world'", var3x);
                                 this.minecraft
                                    .setScreen(
                                       new AlertScreen(
                                          () -> this.minecraft.setScreen(this.screen),
                                          Component.translatable("selectWorld.futureworld.error.title"),
                                          Component.translatable("selectWorld.futureworld.error.text")
                                       )
                                    );
                              }
                           } else {
                              this.minecraft.setScreen(this.screen);
                           }
                        },
                        Component.translatable("selectWorld.versionQuestion"),
                        Component.translatable("selectWorld.versionWarning", this.summary.getWorldVersionName()),
                        Component.translatable("selectWorld.versionJoinButton"),
                        CommonComponents.GUI_CANCEL
                     )
                  );
            } else {
               this.loadWorld();
            }
         }
      }

      public void deleteWorld() {
         this.minecraft
            .setScreen(
               new ConfirmScreen(
                  var1 -> {
                     if (var1) {
                        this.minecraft.setScreen(new ProgressScreen(true));
                        this.doDeleteWorld();
                     }
         
                     this.minecraft.setScreen(this.screen);
                  },
                  Component.translatable("selectWorld.deleteQuestion"),
                  Component.translatable("selectWorld.deleteWarning", this.summary.getLevelName()),
                  Component.translatable("selectWorld.deleteButton"),
                  CommonComponents.GUI_CANCEL
               )
            );
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

         WorldSelectionList.this.reloadWorldList();
      }

      public void editWorld() {
         this.queueLoadScreen();
         String var1 = this.summary.getLevelId();

         try {
            LevelStorageSource.LevelStorageAccess var2 = this.minecraft.getLevelSource().createAccess(var1);
            this.minecraft.setScreen(new EditWorldScreen(var3x -> {
               try {
                  var2.close();
               } catch (IOException var5) {
                  WorldSelectionList.LOGGER.error("Failed to unlock level {}", var1, var5);
               }

               if (var3x) {
                  WorldSelectionList.this.reloadWorldList();
               }

               this.minecraft.setScreen(this.screen);
            }, var2));
         } catch (IOException var3) {
            SystemToast.onWorldAccessFailure(this.minecraft, var1);
            WorldSelectionList.LOGGER.error("Failed to access level {}", var1, var3);
            WorldSelectionList.this.reloadWorldList();
         }
      }

      public void recreateWorld() {
         this.queueLoadScreen();

         try (LevelStorageSource.LevelStorageAccess var1 = this.minecraft.getLevelSource().createAccess(this.summary.getLevelId())) {
            Pair var2 = this.minecraft.createWorldOpenFlows().recreateWorldData(var1);
            LevelSettings var3 = (LevelSettings)var2.getFirst();
            WorldCreationContext var4 = (WorldCreationContext)var2.getSecond();
            Path var5 = CreateWorldScreen.createTempDataPackDirFromExistingWorld(var1.getLevelPath(LevelResource.DATAPACK_DIR), this.minecraft);
            if (var4.options().isOldCustomizedWorld()) {
               this.minecraft
                  .setScreen(
                     new ConfirmScreen(
                        var4x -> this.minecraft.setScreen((Screen)(var4x ? CreateWorldScreen.createFromExisting(this.screen, var3, var4, var5) : this.screen)),
                        Component.translatable("selectWorld.recreate.customized.title"),
                        Component.translatable("selectWorld.recreate.customized.text"),
                        CommonComponents.GUI_PROCEED,
                        CommonComponents.GUI_CANCEL
                     )
                  );
            } else {
               this.minecraft.setScreen(CreateWorldScreen.createFromExisting(this.screen, var3, var4, var5));
            }
         } catch (Exception var8) {
            WorldSelectionList.LOGGER.error("Unable to recreate world", var8);
            this.minecraft
               .setScreen(
                  new AlertScreen(
                     () -> this.minecraft.setScreen(this.screen),
                     Component.translatable("selectWorld.recreate.error.title"),
                     Component.translatable("selectWorld.recreate.error.text")
                  )
               );
         }
      }

      private void loadWorld() {
         this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         if (this.minecraft.getLevelSource().levelExists(this.summary.getLevelId())) {
            this.queueLoadScreen();
            this.minecraft.createWorldOpenFlows().loadLevel(this.screen, this.summary.getLevelId());
         }
      }

      private void queueLoadScreen() {
         this.minecraft.forceSetScreen(new GenericDirtMessageScreen(Component.translatable("selectWorld.data_read")));
      }

      @Nullable
      private DynamicTexture loadServerIcon() {
         boolean var1 = this.iconFile != null && Files.isRegularFile(this.iconFile);
         if (var1) {
            try {
               DynamicTexture var5;
               try (InputStream var2 = Files.newInputStream(this.iconFile)) {
                  NativeImage var3 = NativeImage.read(var2);
                  Validate.validState(var3.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                  Validate.validState(var3.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                  DynamicTexture var4 = new DynamicTexture(var3);
                  this.minecraft.getTextureManager().register(this.iconLocation, var4);
                  var5 = var4;
               }

               return var5;
            } catch (Throwable var8) {
               WorldSelectionList.LOGGER.error("Invalid icon for world {}", this.summary.getLevelId(), var8);
               this.iconFile = null;
               return null;
            }
         } else {
            this.minecraft.getTextureManager().release(this.iconLocation);
            return null;
         }
      }

      @Override
      public void close() {
         if (this.icon != null) {
            this.icon.close();
         }
      }

      public String getLevelName() {
         return this.summary.getLevelName();
      }

      @Override
      public boolean isSelectable() {
         return !this.summary.isDisabled();
      }
   }
}
