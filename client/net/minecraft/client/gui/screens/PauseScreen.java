package net.minecraft.client.gui.screens;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.toasts.NowPlayingToast;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.Dialogs;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DialogTags;
import net.minecraft.util.CommonLinks;

public class PauseScreen extends Screen {
   private static final ResourceLocation DRAFT_REPORT_SPRITE = ResourceLocation.withDefaultNamespace("icon/draft_report");
   private static final int COLUMNS = 2;
   private static final int MENU_PADDING_TOP = 50;
   private static final int BUTTON_PADDING = 4;
   private static final int BUTTON_WIDTH_FULL = 204;
   private static final int BUTTON_WIDTH_HALF = 98;
   private static final Component RETURN_TO_GAME = Component.translatable("menu.returnToGame");
   private static final Component ADVANCEMENTS = Component.translatable("gui.advancements");
   private static final Component STATS = Component.translatable("gui.stats");
   private static final Component SEND_FEEDBACK = Component.translatable("menu.sendFeedback");
   private static final Component REPORT_BUGS = Component.translatable("menu.reportBugs");
   private static final Component FEEDBACK_SUBSCREEN = Component.translatable("menu.feedback");
   private static final Component OPTIONS = Component.translatable("menu.options");
   private static final Component SHARE_TO_LAN = Component.translatable("menu.shareToLan");
   private static final Component PLAYER_REPORTING = Component.translatable("menu.playerReporting");
   private static final Component GAME = Component.translatable("menu.game");
   private static final Component PAUSED = Component.translatable("menu.paused");
   private static final Tooltip CUSTOM_OPTIONS_TOOLTIP = Tooltip.create(Component.translatable("menu.custom_options.tooltip"));
   private final boolean showPauseMenu;
   @Nullable
   private Button disconnectButton;

   public PauseScreen(boolean var1) {
      super(var1 ? GAME : PAUSED);
      this.showPauseMenu = var1;
   }

   public boolean showsPauseMenu() {
      return this.showPauseMenu;
   }

   protected void init() {
      if (this.showPauseMenu) {
         this.createPauseMenu();
      }

      int var1 = this.font.width((FormattedText)this.title);
      int var10003 = this.width / 2 - var1 / 2;
      int var10004 = this.showPauseMenu ? 40 : 10;
      Objects.requireNonNull(this.font);
      this.addRenderableWidget(new StringWidget(var10003, var10004, var1, 9, this.title, this.font));
   }

   private void createPauseMenu() {
      GridLayout var1 = new GridLayout();
      var1.defaultCellSetting().padding(4, 4, 4, 0);
      GridLayout.RowHelper var2 = var1.createRowHelper(2);
      var2.addChild(Button.builder(RETURN_TO_GAME, (var1x) -> {
         this.minecraft.setScreen((Screen)null);
         this.minecraft.mouseHandler.grabMouse();
      }).width(204).build(), 2, var1.newCellSettings().paddingTop(50));
      var2.addChild(this.openScreenButton(ADVANCEMENTS, () -> new AdvancementsScreen(this.minecraft.player.connection.getAdvancements(), this)));
      var2.addChild(this.openScreenButton(STATS, () -> new StatsScreen(this, this.minecraft.player.getStats())));
      Optional var3 = this.getCustomAdditions();
      if (var3.isEmpty()) {
         addFeedbackButtons(this, var2);
      } else {
         this.addFeedbackSubscreenAndCustomDialogButtons(this.minecraft, (Holder)var3.get(), var2);
      }

      var2.addChild(this.openScreenButton(OPTIONS, () -> new OptionsScreen(this, this.minecraft.options)));
      if (this.minecraft.hasSingleplayerServer() && !this.minecraft.getSingleplayerServer().isPublished()) {
         var2.addChild(this.openScreenButton(SHARE_TO_LAN, () -> new ShareToLanScreen(this)));
      } else {
         var2.addChild(this.openScreenButton(PLAYER_REPORTING, () -> new SocialInteractionsScreen(this)));
      }

      this.disconnectButton = (Button)var2.addChild(Button.builder(CommonComponents.disconnectButtonLabel(this.minecraft.isLocalServer()), (var1x) -> {
         var1x.active = false;
         this.minecraft.getReportingContext().draftReportHandled(this.minecraft, this, () -> this.minecraft.disconnectFromWorld(ClientLevel.DEFAULT_QUIT_MESSAGE), true);
      }).width(204).build(), 2);
      var1.arrangeElements();
      FrameLayout.alignInRectangle(var1, 0, 0, this.width, this.height, 0.5F, 0.25F);
      var1.visitWidgets(this::addRenderableWidget);
   }

   private Optional<? extends Holder<Dialog>> getCustomAdditions() {
      Registry var1 = this.minecraft.player.connection.registryAccess().lookupOrThrow(Registries.DIALOG);
      Optional var2 = var1.get(DialogTags.PAUSE_SCREEN_ADDITIONS);
      if (var2.isPresent()) {
         HolderSet var3 = (HolderSet)var2.get();
         if (var3.size() > 0) {
            if (var3.size() == 1) {
               return Optional.of(var3.get(0));
            }

            return var1.get(Dialogs.CUSTOM_OPTIONS);
         }
      }

      ServerLinks var4 = this.minecraft.player.connection.serverLinks();
      return !var4.isEmpty() ? var1.get(Dialogs.SERVER_LINKS) : Optional.empty();
   }

   static void addFeedbackButtons(Screen var0, GridLayout.RowHelper var1) {
      var1.addChild(openLinkButton(var0, SEND_FEEDBACK, SharedConstants.getCurrentVersion().stable() ? CommonLinks.RELEASE_FEEDBACK : CommonLinks.SNAPSHOT_FEEDBACK));
      ((Button)var1.addChild(openLinkButton(var0, REPORT_BUGS, CommonLinks.SNAPSHOT_BUGS_FEEDBACK))).active = !SharedConstants.getCurrentVersion().dataVersion().isSideSeries();
   }

   private void addFeedbackSubscreenAndCustomDialogButtons(Minecraft var1, Holder<Dialog> var2, GridLayout.RowHelper var3) {
      var3.addChild(this.openScreenButton(FEEDBACK_SUBSCREEN, () -> new FeedbackSubScreen(this)));
      var3.addChild(Button.builder(((Dialog)var2.value()).common().computeExternalTitle(), (var3x) -> var1.player.connection.showDialog(var2, this)).width(98).tooltip(CUSTOM_OPTIONS_TOOLTIP).build());
   }

   public void tick() {
      if (this.rendersNowPlayingToast()) {
         NowPlayingToast.tickMusicNotes();
      }

   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      if (this.rendersNowPlayingToast()) {
         NowPlayingToast.renderToast(var1, this.font);
      }

      if (this.showPauseMenu && this.minecraft != null && this.minecraft.getReportingContext().hasDraftReport() && this.disconnectButton != null) {
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)DRAFT_REPORT_SPRITE, this.disconnectButton.getX() + this.disconnectButton.getWidth() - 17, this.disconnectButton.getY() + 3, 15, 15);
      }

   }

   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      if (this.showPauseMenu) {
         super.renderBackground(var1, var2, var3, var4);
      }

   }

   public boolean rendersNowPlayingToast() {
      Options var1 = this.minecraft.options;
      return (Boolean)var1.showNowPlayingToast().get() && var1.getFinalSoundSourceVolume(SoundSource.MUSIC) > 0.0F && this.showPauseMenu;
   }

   private Button openScreenButton(Component var1, Supplier<Screen> var2) {
      return Button.builder(var1, (var2x) -> this.minecraft.setScreen((Screen)var2.get())).width(98).build();
   }

   private static Button openLinkButton(Screen var0, Component var1, URI var2) {
      return Button.builder(var1, ConfirmLinkScreen.confirmLink(var0, var2)).width(98).build();
   }

   static class FeedbackSubScreen extends Screen {
      private static final Component TITLE = Component.translatable("menu.feedback.title");
      public final Screen parent;
      private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

      protected FeedbackSubScreen(Screen var1) {
         super(TITLE);
         this.parent = var1;
      }

      protected void init() {
         this.layout.addTitleHeader(TITLE, this.font);
         GridLayout var1 = (GridLayout)this.layout.addToContents(new GridLayout());
         var1.defaultCellSetting().padding(4, 4, 4, 0);
         GridLayout.RowHelper var2 = var1.createRowHelper(2);
         PauseScreen.addFeedbackButtons(this, var2);
         this.layout.addToFooter(Button.builder(CommonComponents.GUI_BACK, (var1x) -> this.onClose()).width(200).build());
         this.layout.visitWidgets(this::addRenderableWidget);
         this.repositionElements();
      }

      protected void repositionElements() {
         this.layout.arrangeElements();
      }

      public void onClose() {
         this.minecraft.setScreen(this.parent);
      }
   }
}
