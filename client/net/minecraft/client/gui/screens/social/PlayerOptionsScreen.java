package net.minecraft.client.gui.screens.social;

import com.mojang.authlib.services.response.PresenceStatus;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PopupScreen;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.friends.FriendsListActions;
import net.minecraft.client.gui.screens.friends.IncomingEntry;
import net.minecraft.client.gui.screens.friends.OutgoingEntry;
import net.minecraft.client.gui.screens.friends.PlayerProfileWidget;
import net.minecraft.client.gui.screens.reporting.ReportPlayerScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jspecify.annotations.Nullable;

public class PlayerOptionsScreen extends Screen {
   private static final Identifier DRAFT_REPORT_SPRITE = Identifier.withDefaultNamespace("icon/draft_report");
   private static final Component TITLE = Component.translatable("gui.player_options.title");
   private static final Component UNFRIEND = Component.translatable("gui.player_options.unfriend");
   private static final Component CONFIRM_UNFRIEND = Component.translatable("gui.player_options.unfriend.confirm");
   private static final Component SEND_REQUEST = Component.translatable("gui.player_options.send_request");
   private static final Component CANCEL_REQUEST = Component.translatable("gui.player_options.cancel_request");
   private static final Component MUTE = Component.translatable("gui.player_options.mute");
   private static final Component UNMUTE = Component.translatable("gui.player_options.unmute");
   private static final Component REPORT = Component.translatable("gui.player_options.report");
   private static final Component EDIT_REPORT = Component.translatable("gui.player_options.edit_report");
   private static final Tooltip MUTE_TOOLTIP = Tooltip.create(Component.translatable("gui.player_options.mute.tooltip"));
   private static final Tooltip UNMUTE_TOOLTIP = Tooltip.create(Component.translatable("gui.player_options.unmute.tooltip"));
   private static final Tooltip REPORT_TOOLTIP = Tooltip.create(Component.translatable("gui.player_options.report.tooltip"));
   private static final Tooltip REPORT_DISABLED_TOOLTIP = Tooltip.create(Component.translatable("gui.player_options.report.tooltip.disabled"));
   private static final Tooltip EDIT_REPORT_TOOLTIP = Tooltip.create(Component.translatable("gui.player_options.edit_report.tooltip"));
   private static final int HEADER_HEIGHT = 21;
   private static final int HEADER_CONTENT_MARGIN = -2;
   private static final int FOOTER_HEIGHT = 33;
   private static final int SEPARATOR_HEIGHT = 2;
   private static final int HORIZONTAL_SPACING = 10;
   private static final int VERTICAL_SPACING = 6;
   private static final int PROFILE_BOTTOM_PADDING = 14;
   private static final int BIG_BUTTON_WIDTH = 200;
   private static final int SMALL_BUTTON_SPACING = 4;
   private static final int SMALL_BUTTON_WIDTH = 98;
   private static final int PLAYER_WIDTH = 110;
   private static final int PLAYER_HEIGHT = 160;
   private static final float PLAYER_ROTATION_SCALE = 0.0025F;
   private final @Nullable Screen lastScreen;
   protected final UUID playerId;
   private final String playerName;
   private final FriendsListActions friendsListActions;
   private final Supplier<PlayerSkin> skinGetter;
   private final AvatarRenderState playerRenderState;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 21, 33);
   private @Nullable LinearLayout contents;
   protected final PlayerProfileWidget profileWidget;
   private final SpriteIconButton friendButton;
   private final SpriteIconButton acceptFriendButton;
   private final SpriteIconButton declineFriendButton;
   private final Button muteButton;
   private final SpriteIconButton reportButton;
   private PlayerSocialManager.FriendState friendState;
   private boolean hasDraftReport;

   public PlayerOptionsScreen(final @Nullable Screen lastScreen, final UUID playerId, final String playerName, final FriendsListActions friendsListActions, final Supplier<PlayerSkin> skinGetter, final boolean skinReportable, final boolean chatReportable, final boolean hasRecentMessages) {
      super(TITLE);
      this.friendState = PlayerSocialManager.FriendState.NOT_FRIENDS;
      this.lastScreen = lastScreen;
      this.playerId = playerId;
      this.playerName = playerName;
      this.friendsListActions = friendsListActions;
      this.skinGetter = skinGetter;
      this.playerRenderState = this.createAvatarRenderState();
      ReportingContext reportingContext = this.minecraft.getReportingContext();
      boolean reportingEnabled = reportingContext.sender().isEnabled();
      this.refreshHasDraftReport(reportingContext);
      this.profileWidget = new PlayerProfileWidget(this.minecraft, playerId, playerName);
      this.friendButton = ((SpriteIconButton.Builder)((SpriteIconButton.Builder)SpriteIconButton.builder(SEND_REQUEST, (var1) -> {
         switch (this.friendState) {
            case FRIENDS -> this.confirmRemoveFriend();
            case NOT_FRIENDS -> this.sendFriendRequest();
            case REQUEST_SENT -> this.cancelFriendRequest();
         }

      }, SpriteIconButton.DisplayState.TEXT_ONLY).createNarration((var2) -> {
         MutableComponent var10000;
         switch (this.friendState) {
            case FRIENDS -> var10000 = Component.translatable("gui.friends.narration.button.unfriend", playerName);
            case NOT_FRIENDS -> var10000 = Component.translatable("gui.friends.narration.button.send_request", playerName);
            case REQUEST_SENT -> var10000 = Component.translatable("gui.friends.narration.button.cancel_request", playerName);
            case REQUEST_RECEIVED -> var10000 = Component.empty();
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      })).sprite((WidgetSprites)OutgoingEntry.REVOKE_SPRITE, 12, 12).width(200)).build();
      this.acceptFriendButton = ((SpriteIconButton.Builder)((SpriteIconButton.Builder)((SpriteIconButton.Builder)SpriteIconButton.builder(IncomingEntry.ACCEPT, (var2) -> friendsListActions.acceptIncomingFriendRequest(playerId), SpriteIconButton.DisplayState.TEXT_AND_ICON).sprite((WidgetSprites)IncomingEntry.ACCEPT_SPRITE, 18, 18).tooltip(IncomingEntry.ACCEPT_TOOLTIP)).switchToLoadingAfterPress()).width(98)).build();
      this.declineFriendButton = ((SpriteIconButton.Builder)((SpriteIconButton.Builder)((SpriteIconButton.Builder)SpriteIconButton.builder(IncomingEntry.REJECT, (var2) -> friendsListActions.declineIncomingFriendRequest(playerId), SpriteIconButton.DisplayState.TEXT_AND_ICON).sprite((WidgetSprites)IncomingEntry.REJECT_SPRITE, 18, 18).tooltip(IncomingEntry.REJECT_TOOLTIP)).switchToLoadingAfterPress()).width(98)).build();
      this.muteButton = Button.builder(MUTE, (var1) -> this.muteOrUnmute()).width(200).build();
      this.reportButton = ((SpriteIconButton.Builder)SpriteIconButton.builder(REPORT, (var5) -> this.report(reportingContext, skinReportable, chatReportable, hasRecentMessages), SpriteIconButton.DisplayState.TEXT_ONLY).sprite((Identifier)DRAFT_REPORT_SPRITE, 15, 15).width(200)).build();
      this.friendButton.setLoading(true);
      if (!reportingEnabled) {
         this.reportButton.active = false;
         this.reportButton.setTooltip(REPORT_DISABLED_TOOLTIP);
      }

   }

   private AvatarRenderState createAvatarRenderState() {
      AvatarRenderState state = new AvatarRenderState();
      EntityDimensions dimensions = EntityTypes.PLAYER.getDimensions();
      state.boundingBoxWidth = dimensions.width();
      state.boundingBoxHeight = dimensions.height();
      state.eyeHeight = dimensions.eyeHeight();
      return state;
   }

   public void refreshHasDraftReport(final ReportingContext reportingContext) {
      boolean newHasDraftReport = reportingContext.hasDraftReportFor(this.playerId);
      if (this.hasDraftReport != newHasDraftReport) {
         this.hasDraftReport = newHasDraftReport;
         if (this.isInitialized()) {
            this.refreshState();
         }
      }

   }

   public void added() {
      super.added();
      this.refreshHasDraftReport(Minecraft.getInstance().getReportingContext());
      this.friendsListActions.addFriendListUpdateListener(this::startFriendAction, this::refreshState);
      if (this.isInitialized()) {
         this.refreshState();
      }

   }

   public void removed() {
      this.friendsListActions.removeFriendListUpdateListener();
      super.removed();
   }

   protected void init() {
      this.layout.setContentMarginTop(-2);
      this.layout.addTitleHeader(TITLE, this.font);
      this.contents = (LinearLayout)this.layout.addToContents(LinearLayout.horizontal().spacing(10));
      this.contents.defaultCellSetting().alignVerticallyMiddle();
      this.contents.addChild(new EntityPortraitWidget(110, 160, 0.0025F, this.playerRenderState));
      FrameLayout friendButtonGroup = new FrameLayout(200, 20);
      LinearLayout acceptDeclineButtons = (LinearLayout)friendButtonGroup.addChild(LinearLayout.horizontal().spacing(4));
      acceptDeclineButtons.addChild(this.acceptFriendButton);
      acceptDeclineButtons.addChild(this.declineFriendButton);
      friendButtonGroup.addChild(this.friendButton);
      LinearLayout widgets = (LinearLayout)this.contents.addChild(LinearLayout.vertical().spacing(6));
      widgets.addChild(this.profileWidget, (Consumer)((settings) -> settings.paddingBottom(14)));
      widgets.addChild(friendButtonGroup);
      widgets.addChild(this.muteButton);
      widgets.addChild(this.reportButton);
      this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, (var1) -> this.onClose()).width(200).build());
      this.layout.visitWidgets((x$0) -> this.addRenderableWidget(x$0));
      this.refreshState();
      this.repositionElements();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      if (this.contents != null) {
         FrameLayout.centerInRectangle(this.contents, this.layout.getX(), this.layout.getY() + this.layout.getHeaderHeight(), this.layout.getWidth(), this.layout.getContentHeight());
      }

   }

   private void refreshState() {
      PlayerSocialManager playerSocialManager = this.minecraft.getPlayerSocialManager();
      RemoteFriendListUpdateHandler.State state = playerSocialManager.getFriendListState();
      if (state == RemoteFriendListUpdateHandler.State.SUCCESS) {
         this.updateFriendButtons(playerSocialManager);
      } else {
         this.setFriendButtonStates(false, FriendsListActions.Action.NONE);
         this.friendButton.active = false;
      }

      if (playerSocialManager.isHidden(this.playerId)) {
         this.muteButton.setMessage(UNMUTE);
         this.muteButton.setTooltip(UNMUTE_TOOLTIP);
      } else {
         this.muteButton.setMessage(MUTE);
         this.muteButton.setTooltip(MUTE_TOOLTIP);
      }

      if (this.reportButton.active) {
         if (this.hasDraftReport) {
            this.reportButton.setMessage(EDIT_REPORT);
            this.reportButton.setTooltip(EDIT_REPORT_TOOLTIP);
            this.reportButton.setDisplayState(SpriteIconButton.DisplayState.TEXT_AND_ICON);
         } else {
            this.reportButton.setMessage(REPORT);
            this.reportButton.setTooltip(REPORT_TOOLTIP);
            this.reportButton.setDisplayState(SpriteIconButton.DisplayState.TEXT_ONLY);
         }
      }

      this.refreshDisplayedStatus(playerSocialManager);
   }

   protected void refreshDisplayedStatus(final PlayerSocialManager playerSocialManager) {
      ClientPacketListener connection = this.minecraft.getConnection();
      PresenceStatus presence = connection != null && connection.getOnlinePlayerIds().contains(this.playerId) ? PresenceStatus.ONLINE : PresenceStatus.OFFLINE;
      this.profileWidget.statusWidget().applyPlayerStatus(new PlayerStatus(presence, playerSocialManager.getVisibility(this.playerId)));
   }

   private void updateFriendButtons(final PlayerSocialManager playerSocialManager) {
      FriendsListActions.Action pendingAction = this.friendsListActions.getPendingAction(this.playerId);
      this.friendState = playerSocialManager.getFriendState(this.playerId);
      if (this.friendState == PlayerSocialManager.FriendState.REQUEST_RECEIVED) {
         this.setFriendButtonStates(true, pendingAction);
      } else {
         switch (this.friendState) {
            case FRIENDS:
               this.friendButton.setMessage(UNFRIEND);
               this.friendButton.setDisplayState(SpriteIconButton.DisplayState.TEXT_ONLY);
               break;
            case NOT_FRIENDS:
               this.friendButton.setMessage(SEND_REQUEST);
               this.friendButton.setDisplayState(SpriteIconButton.DisplayState.TEXT_ONLY);
               break;
            case REQUEST_SENT:
               this.friendButton.setMessage(CANCEL_REQUEST);
               this.friendButton.setDisplayState(SpriteIconButton.DisplayState.TEXT_AND_ICON);
         }

         this.setFriendButtonStates(false, pendingAction);
      }

   }

   private void setFriendButtonStates(final boolean incomingRequest, final FriendsListActions.Action pendingAction) {
      boolean nonePending = pendingAction == FriendsListActions.Action.NONE;
      if (incomingRequest) {
         this.acceptFriendButton.active = nonePending;
         this.declineFriendButton.active = nonePending;
         this.acceptFriendButton.setLoading(pendingAction == FriendsListActions.Action.ACCEPT_INCOMING);
         this.declineFriendButton.setLoading(pendingAction == FriendsListActions.Action.DECLINE_INCOMING);
      } else {
         this.friendButton.active = nonePending;
         switch (pendingAction) {
            case ADD_FRIEND:
            case REMOVE_FRIEND:
            case REVOKE_OUTGOING:
               this.friendButton.setLoading(true);
               break;
            default:
               this.friendButton.setLoading(false);
         }
      }

      this.friendButton.setVisible(!incomingRequest);
      this.acceptFriendButton.setVisible(incomingRequest);
      this.declineFriendButton.setVisible(incomingRequest);
   }

   public void tick() {
      this.minecraft.getPlayerSocialManager().getPresenceHandler().tryUpdatePresence();
      ++this.playerRenderState.ageInTicks;
   }

   public void onClose() {
      this.minecraft.gui.setScreen(this.lastScreen);
   }

   protected void extractMenuBackground(final GuiGraphicsExtractor graphics) {
      super.extractMenuBackground(graphics);
      boolean inWorld = this.minecraft.level != null;
      Identifier menuListBackground = inWorld ? Screen.INWORLD_MENU_LIST_BACKGROUND : Screen.MENU_LIST_BACKGROUND;
      graphics.blit(RenderPipelines.GUI_TEXTURED, menuListBackground, this.layout.getX(), this.layout.getHeaderHeight(), (float)this.width, (float)(this.height - this.layout.getFooterHeight()), this.width, this.layout.getContentHeight(), 32, 32);
   }

   public void extractRenderState(final GuiGraphicsExtractor graphics, final int xm, final int ym, final float a) {
      this.playerRenderState.skin = (PlayerSkin)this.skinGetter.get();
      super.extractRenderState(graphics, xm, ym, a);
      this.extractListSeparators(graphics, this.layout, 2);
   }

   private void startFriendAction() {
      this.friendButton.active = false;
      this.acceptFriendButton.active = false;
      this.declineFriendButton.active = false;
   }

   private void confirmRemoveFriend() {
      this.minecraft.gui.setScreen((new PopupScreen.Builder(this, UNFRIEND)).addMessage(CONFIRM_UNFRIEND).addButton(CommonComponents.GUI_REMOVE, (var1) -> {
         this.friendButton.setLoading(true);
         this.friendsListActions.removeFriend(this.playerId);
         this.minecraft.gui.setScreen(this);
      }).addButton(CommonComponents.GUI_CANCEL, (var1) -> this.minecraft.gui.setScreen(this)).build());
   }

   private void sendFriendRequest() {
      this.friendButton.setLoading(true);
      this.friendsListActions.sendFriendRequest(this.playerId);
   }

   private void cancelFriendRequest() {
      this.friendButton.setLoading(true);
      this.friendsListActions.revokeOutgoingFriendRequest(this.playerId);
   }

   private void muteOrUnmute() {
      PlayerSocialManager socialManager = this.minecraft.getPlayerSocialManager();
      Component message;
      if (socialManager.isHidden(this.playerId)) {
         socialManager.showPlayer(this.playerId);
         message = Component.translatable("gui.player_options.unmute.feedback", this.playerName);
      } else {
         socialManager.hidePlayer(this.playerId);
         message = Component.translatable("gui.player_options.mute.feedback", this.playerName);
      }

      this.minecraft.gui.hud.getChat().addClientSystemMessage(message);
      this.minecraft.getNarrator().saySystemNow(message);
      this.refreshState();
   }

   private void report(final ReportingContext reportingContext, final boolean skinReportable, final boolean chatReportable, final boolean hasRecentMessages) {
      PlayerSocialManager socialManager = this.minecraft.getPlayerSocialManager();
      boolean chatDisabledOrBlocked = this.minecraft.player == null || !this.minecraft.player.chatAbilities().canReceivePlayerMessages() || socialManager.isBlocked(this.playerId);
      reportingContext.draftReportHandled(this.minecraft, this, () -> this.minecraft.gui.setScreen(new ReportPlayerScreen(this, reportingContext, this.playerId, this.playerName, this.skinGetter, skinReportable, chatDisabledOrBlocked, chatReportable, hasRecentMessages)), false, (report) -> report.isReportedPlayer(this.playerId));
   }
}
