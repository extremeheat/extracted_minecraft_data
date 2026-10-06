package net.minecraft.client.gui.screens;

import com.google.common.util.concurrent.Runnables;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MusicToastDisplayState;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CommonButtons;
import net.minecraft.client.gui.components.FriendsButton;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.toasts.NowPlayingToast;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.friends.FriendsListActions;
import net.minecraft.client.gui.screens.friends.FriendsOverlayScreen;
import net.minecraft.client.gui.screens.options.OnlineOptionsScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.reporting.DraftIconButton;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.chat.report.Report;
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
   public static final Identifier OTHER_PLAYERS_SPRITE = Identifier.withDefaultNamespace("pause_menu/other_players");
   private static final Identifier SEND_FEEDBACK_SPRITE = Identifier.withDefaultNamespace("pause_menu/feedback");
   private static final Identifier REPORT_BUGS_SPRITE = Identifier.withDefaultNamespace("pause_menu/bug");
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
   private static final Component OPTIONS = Component.translatable("menu.options");
   private static final Component WORLD_OPTIONS = Component.translatable("options.worldOptions.button");
   private static final Component OTHER_PLAYERS = Component.translatable("menu.other_players");
   private static final Component GAME = Component.translatable("menu.game");
   private static final Component PAUSED = Component.translatable("menu.paused");
   private static final Tooltip CUSTOM_OPTIONS_TOOLTIP = Tooltip.create(Component.translatable("menu.custom_options.tooltip"));
   private static final Tooltip NO_OTHER_PLAYERS_TOOLTIP = Tooltip.create(Component.translatable("menu.other_players.no_players"));
   public static final Tooltip DRAFT_REPORT_TOOLTIP = Tooltip.create(Component.translatable("menu.disconnect.draft_report.tooltip"));
   private final FriendsListActions friendsListActions;
   private @Nullable FriendsButton friends;
   private final boolean showPauseMenu;

   public PauseScreen(final boolean showPauseMenu) {
      super(showPauseMenu ? GAME : PAUSED);
      this.showPauseMenu = showPauseMenu;
      this.friendsListActions = new FriendsListActions(this.minecraft);
   }

   public boolean showsPauseMenu() {
      return this.showPauseMenu;
   }

   public void added() {
      super.added();
      if (this.showPauseMenu && this.minecraft.getPlayerSocialManager().isFriendListEnabled()) {
         this.friendsListActions.addFriendListUpdateListener(Runnables.doNothing(), this::onFriendListUpdate);
      }

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
         this.minecraft.gui.setScreen((Screen)null);
         this.minecraft.mouseHandler.grabMouse();
      }).width(204).build(), 2, gridLayout.newCellSettings().paddingTop(50));
      helper.addChild(this.openScreenButton(ADVANCEMENTS, () -> new AdvancementsScreen(this.minecraft.player.connection.getAdvancements(), this)));
      helper.addChild(this.openScreenButton(STATS, () -> new StatsScreen(this, this.minecraft.player.getStats())));
      LinearLayout iconButtonRow = LinearLayout.horizontal().spacing(4);
      this.friends = CommonButtons.friends(20, (var1) -> OnlineOptionsScreen.confirmFriendsListEnabled(this.minecraft, () -> this.minecraft.gui.setScreen(new FriendsOverlayScreen(this.minecraft, this, this.friendsListActions)), this), !this.minecraft.isDemo() && !this.minecraft.isOfflineDeveloperMode());
      iconButtonRow.addChild(this.friends);
      SpriteIconButton playerReportingButton = ((SpriteIconButton.Builder)SpriteIconButton.builder(OTHER_PLAYERS, (var1) -> this.minecraft.gui.setScreen(new SocialInteractionsScreen(this)), true).width(20)).sprite((Identifier)OTHER_PLAYERS_SPRITE, 16, 16).withTootip().build();
      iconButtonRow.addChild(playerReportingButton);
      if (!this.minecraft.isMultiplayerServer()) {
         playerReportingButton.active = false;
         playerReportingButton.setTooltip(NO_OTHER_PLAYERS_TOOLTIP);
      }

      SpriteIconButton reportBugsButton = ((SpriteIconButton.Builder)SpriteIconButton.builder(REPORT_BUGS, ConfirmLinkScreen.confirmLink(this, CommonLinks.SNAPSHOT_BUGS_FEEDBACK), true).width(20)).sprite((Identifier)REPORT_BUGS_SPRITE, 13, 13).withTootip().build();
      reportBugsButton.active = !SharedConstants.getCurrentVersion().dataVersion().isSideSeries();
      iconButtonRow.addChild(reportBugsButton);
      SpriteIconButton feedbackButton = ((SpriteIconButton.Builder)SpriteIconButton.builder(SEND_FEEDBACK, ConfirmLinkScreen.confirmLink(this, SharedConstants.getCurrentVersion().stable() ? CommonLinks.RELEASE_FEEDBACK : CommonLinks.SNAPSHOT_FEEDBACK), true).width(20)).sprite((Identifier)SEND_FEEDBACK_SPRITE, 20, 20).withTootip().build();
      iconButtonRow.addChild(feedbackButton);
      helper.addChild(iconButtonRow, 2, gridLayout.newCellSettings().alignHorizontallyCenter());
      Optional<? extends Holder<Dialog>> additions = this.getCustomAdditions();
      additions.ifPresent((dialogHolder) -> this.addCustomDialogButtons(this.minecraft, dialogHolder, helper));
      if (this.minecraft.level != null) {
         helper.addChild(this.openScreenButton(OPTIONS, () -> new OptionsScreen(this, this.minecraft.options)));
         helper.addChild(this.openScreenButton(WORLD_OPTIONS, () -> new WorldOptionsScreen(this, this.minecraft.level)));
      }

      helper.addChild(new DraftIconButton(204, 20, CommonComponents.disconnectButtonLabel(this.minecraft.isLocalServer()), (button) -> {
         button.active = false;
         this.minecraft.getReportingContext().draftReportHandled(this.minecraft, this, () -> this.minecraft.disconnectFromWorld(ClientLevel.DEFAULT_QUIT_MESSAGE), true, Report::isUnreportableLater);
      }, DRAFT_REPORT_TOOLTIP, Report::isUnreportableLater), 2);
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

   private void addCustomDialogButtons(final Minecraft minecraft, final Holder<Dialog> dialog, final GridLayout.RowHelper helper) {
      helper.addChild(Button.builder(((Dialog)dialog.value()).common().computeExternalTitle(), (button) -> minecraft.player.connection.showDialog(dialog, this)).width(204).tooltip(CUSTOM_OPTIONS_TOOLTIP).build(), 2);
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

   }

   public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      if (this.showPauseMenu) {
         if (this.isTopmostScreen()) {
            this.extractBlurredBackground(graphics);
         }

         this.extractMenuBackground(graphics);
         this.minecraft.gui.hud.extractDeferredSubtitles();
      }

   }

   public void removed() {
      this.friendsListActions.removeFriendListUpdateListener();
      super.removed();
   }

   public boolean rendersNowPlayingToast() {
      Options options = this.minecraft.options;
      return ((MusicToastDisplayState)options.musicToast().get()).renderInPauseScreen() && options.getFinalSoundSourceVolume(SoundSource.MUSIC) > 0.0F && this.showPauseMenu;
   }

   private boolean isTopmostScreen() {
      return this.minecraft.gui.screen() == this;
   }

   private Button openScreenButton(final Component message, final Supplier<Screen> newScreen) {
      return Button.builder(message, (var2) -> this.minecraft.gui.setScreen((Screen)newScreen.get())).width(98).build();
   }

   private void onFriendListUpdate() {
      if (this.friends != null) {
         this.friends.refreshIncomingRequestCount();
      }

   }
}
