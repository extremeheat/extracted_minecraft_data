package net.minecraft.client.gui.screens;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MusicToastDisplayState;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
import net.minecraft.resources.Identifier;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.Dialogs;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DialogTags;
import net.minecraft.util.CommonLinks;
import org.jspecify.annotations.Nullable;

public class PauseScreen extends Screen {
   private static final Identifier DRAFT_REPORT_SPRITE = Identifier.withDefaultNamespace("icon/draft_report");
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
   private @Nullable Button disconnectButton;

   public PauseScreen(final boolean showPauseMenu) {
      super(showPauseMenu ? GAME : PAUSED);
      this.showPauseMenu = showPauseMenu;
   }

   public boolean showsPauseMenu() {
      return this.showPauseMenu;
   }

   protected void init() {
      if (this.showPauseMenu) {
         this.createPauseMenu();
      }

      int textWidth = this.font.width((FormattedText)this.title);
      int var10003 = this.width / 2 - textWidth / 2;
      int var10004 = this.showPauseMenu ? 40 : 10;
      Objects.requireNonNull(this.font);
      this.addRenderableWidget(new StringWidget(var10003, var10004, textWidth, 9, this.title, this.font));
   }

   private void createPauseMenu() {
      GridLayout gridLayout = new GridLayout();
      gridLayout.defaultCellSetting().padding(4, 4, 4, 0);
      GridLayout.RowHelper helper = gridLayout.createRowHelper(2);
      helper.addChild(Button.builder(RETURN_TO_GAME, (button) -> {
         this.minecraft.setScreen((Screen)null);
         this.minecraft.mouseHandler.grabMouse();
      }).width(204).build(), 2, gridLayout.newCellSettings().paddingTop(50));
      helper.addChild(this.openScreenButton(ADVANCEMENTS, () -> new AdvancementsScreen(this.minecraft.player.connection.getAdvancements(), this)));
      helper.addChild(this.openScreenButton(STATS, () -> new StatsScreen(this, this.minecraft.player.getStats())));
      Optional<? extends Holder<Dialog>> additions = this.getCustomAdditions();
      if (additions.isEmpty()) {
         addFeedbackButtons(this, helper);
      } else {
         this.addFeedbackSubscreenAndCustomDialogButtons(this.minecraft, (Holder)additions.get(), helper);
      }

      helper.addChild(this.openScreenButton(OPTIONS, () -> new OptionsScreen(this, this.minecraft.options, true)));
      if (this.minecraft.hasSingleplayerServer() && !this.minecraft.getSingleplayerServer().isPublished()) {
         helper.addChild(this.openScreenButton(SHARE_TO_LAN, () -> new ShareToLanScreen(this)));
      } else {
         helper.addChild(this.openScreenButton(PLAYER_REPORTING, () -> new SocialInteractionsScreen(this)));
      }

      this.disconnectButton = (Button)helper.addChild(Button.builder(CommonComponents.disconnectButtonLabel(this.minecraft.isLocalServer()), (button) -> {
         button.active = false;
         this.minecraft.getReportingContext().draftReportHandled(this.minecraft, this, () -> this.minecraft.disconnectFromWorld(ClientLevel.DEFAULT_QUIT_MESSAGE), true);
      }).width(204).build(), 2);
      gridLayout.arrangeElements();
      FrameLayout.alignInRectangle(gridLayout, 0, 0, this.width, this.height, 0.5F, 0.25F);
      gridLayout.visitWidgets(this::addRenderableWidget);
   }

   private Optional<? extends Holder<Dialog>> getCustomAdditions() {
      Registry<Dialog> dialogRegistry = this.minecraft.player.connection.registryAccess().lookupOrThrow(Registries.DIALOG);
      Optional<? extends HolderSet<Dialog>> maybeCustomAdditions = dialogRegistry.get(DialogTags.PAUSE_SCREEN_ADDITIONS);
      if (maybeCustomAdditions.isPresent()) {
         HolderSet<Dialog> customAdditions = (HolderSet)maybeCustomAdditions.get();
         if (customAdditions.size() > 0) {
            if (customAdditions.size() == 1) {
               return Optional.of(customAdditions.get(0));
            }

            return dialogRegistry.get(Dialogs.CUSTOM_OPTIONS);
         }
      }

      ServerLinks serverLinks = this.minecraft.player.connection.serverLinks();
      return !serverLinks.isEmpty() ? dialogRegistry.get(Dialogs.SERVER_LINKS) : Optional.empty();
   }

   private static void addFeedbackButtons(final Screen screen, final GridLayout.RowHelper helper) {
      helper.addChild(openLinkButton(screen, SEND_FEEDBACK, SharedConstants.getCurrentVersion().stable() ? CommonLinks.RELEASE_FEEDBACK : CommonLinks.SNAPSHOT_FEEDBACK));
      ((Button)helper.addChild(openLinkButton(screen, REPORT_BUGS, CommonLinks.SNAPSHOT_BUGS_FEEDBACK))).active = !SharedConstants.getCurrentVersion().dataVersion().isSideSeries();
   }

   private void addFeedbackSubscreenAndCustomDialogButtons(final Minecraft minecraft, final Holder<Dialog> dialog, final GridLayout.RowHelper helper) {
      helper.addChild(this.openScreenButton(FEEDBACK_SUBSCREEN, () -> new FeedbackSubScreen(this)));
      helper.addChild(Button.builder(((Dialog)dialog.value()).common().computeExternalTitle(), (button) -> minecraft.player.connection.showDialog(dialog, this)).width(98).tooltip(CUSTOM_OPTIONS_TOOLTIP).build());
   }

   public void tick() {
      if (this.rendersNowPlayingToast()) {
         NowPlayingToast.tickMusicNotes();
      }

   }

   public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      super.extractRenderState(graphics, mouseX, mouseY, a);
      if (this.rendersNowPlayingToast()) {
         NowPlayingToast.extractToast(graphics, this.font);
      }

      if (this.showPauseMenu && this.minecraft.getReportingContext().hasDraftReport() && this.disconnectButton != null) {
         graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)DRAFT_REPORT_SPRITE, this.disconnectButton.getX() + this.disconnectButton.getWidth() - 17, this.disconnectButton.getY() + 3, 15, 15);
      }

   }

   public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      if (this.showPauseMenu) {
         super.extractBackground(graphics, mouseX, mouseY, a);
      }

   }

   public boolean rendersNowPlayingToast() {
      Options options = this.minecraft.options;
      return ((MusicToastDisplayState)options.musicToast().get()).renderInPauseScreen() && options.getFinalSoundSourceVolume(SoundSource.MUSIC) > 0.0F && this.showPauseMenu;
   }

   private Button openScreenButton(final Component message, final Supplier<Screen> newScreen) {
      return Button.builder(message, (button) -> this.minecraft.setScreen((Screen)newScreen.get())).width(98).build();
   }

   private static Button openLinkButton(final Screen screen, final Component message, final URI link) {
      return Button.builder(message, ConfirmLinkScreen.confirmLink(screen, link)).width(98).build();
   }

   private static class FeedbackSubScreen extends Screen {
      private static final Component TITLE = Component.translatable("menu.feedback.title");
      public final Screen parent;
      private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

      protected FeedbackSubScreen(final Screen parent) {
         super(TITLE);
         this.parent = parent;
      }

      protected void init() {
         this.layout.addTitleHeader(TITLE, this.font);
         GridLayout buttonContainer = (GridLayout)this.layout.addToContents(new GridLayout());
         buttonContainer.defaultCellSetting().padding(4, 4, 4, 0);
         GridLayout.RowHelper helper = buttonContainer.createRowHelper(2);
         PauseScreen.addFeedbackButtons(this, helper);
         this.layout.addToFooter(Button.builder(CommonComponents.GUI_BACK, (button) -> this.onClose()).width(200).build());
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
