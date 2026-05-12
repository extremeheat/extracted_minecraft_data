package net.minecraft.client.gui.screens.friends;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.LoadingDotsWidget;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.PrivacyConfirmLinkScreen;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonLinks;

class FriendsTab extends AbstractFriendsTab {
   private static final Component TAB_TITLE = Component.translatable("gui.friends.tab_friends");
   private static final Component MICROSOFT_ACCOUNT_LINK = Component.translatable("gui.friends.empty_state.link").withStyle((UnaryOperator)((style) -> style.withUnderlined(true).withColor(ChatFormatting.GRAY).withClickEvent(new ClickEvent.OpenUrl(CommonLinks.PRIVACY_AND_ONLINE_SETTINGS))));
   private static final Component EMPTY_STATE;
   private static final Component MANAGE_ACCOUNT_FOOTER;
   private static final Identifier ILLUSTRATION;
   private final Minecraft minecraft;
   private final FriendsOverlayScreen screen;
   private final LinearLayout layout;
   private final LinearLayout friendScrollableContent;
   private final LoadingDotsWidget loadingDotsWidget;
   private final AddFriendWidget addFriendWidget;
   private final ScrollableLayout scrollableLayout;

   FriendsTab(final Minecraft minecraft, final LoadingDotsWidget loadingDotsWidget, final FriendsOverlayScreen screen, final int width, final int height) {
      super(width, height);
      this.minecraft = minecraft;
      this.screen = screen;
      this.layout = LinearLayout.vertical();
      this.layout.defaultCellSetting().alignHorizontallyCenter();
      this.loadingDotsWidget = loadingDotsWidget;
      this.addFriendWidget = new AddFriendWidget(width, this::sendFriendRequest);
      this.layout.addChild(this.addFriendWidget);
      this.friendScrollableContent = LinearLayout.vertical();
      this.friendScrollableContent.defaultCellSetting();
      this.scrollableLayout = new ScrollableLayout(minecraft, this.friendScrollableContent, height - this.addFriendWidget.contentHeight(), ScrollableLayout.ReserveStrategy.RIGHT);
      this.layout.addChild(this.scrollableLayout);
      this.rearrangeElements();
   }

   public void showLoading() {
      this.friendScrollableContent.removeChildren();
      this.friendScrollableContent.addChild(this.createCenteredFrame(this.loadingDotsWidget, this.width - 18, this.height - this.addFriendWidget.contentHeight()));
      this.addFriendWidget.applyState(AddFriendWidget.State.SENDING);
   }

   public void showError(final Component message) {
      this.friendScrollableContent.removeChildren();
      FocusableTextWidget text = FocusableTextWidget.builder(message.copy().withStyle(ChatFormatting.GRAY), this.screen.getFont()).maxWidth(this.width - 16 - 4).alwaysShowBorder(false).backgroundFill(FocusableTextWidget.BackgroundFill.NEVER).build();
      text.setCentered(true);
      this.friendScrollableContent.addChild(this.createCenteredFrame(text, this.width, this.height - this.addFriendWidget.contentHeight()));
      this.addFriendWidget.applyState(AddFriendWidget.State.DISABLED);
   }

   public void showEmpty() {
      this.friendScrollableContent.removeChildren();
      LinearLayout content = (new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL)).spacing(8);
      content.defaultCellSetting().alignHorizontallyCenter().alignVerticallyMiddle().paddingLeft(8);
      content.addChild(ImageWidget.sprite(128, 48, ILLUSTRATION));
      FocusableTextWidget textWidget = FocusableTextWidget.builder(EMPTY_STATE, this.screen.getFont()).maxWidth(this.width - 16 - 4).alwaysShowBorder(false).backgroundFill(FocusableTextWidget.BackgroundFill.NEVER).build();
      textWidget.setCentered(true);
      textWidget.setComponentClickHandler((style) -> {
         ClickEvent patt0$temp = style.getClickEvent();
         if (patt0$temp instanceof ClickEvent.OpenUrl openUrl) {
            PrivacyConfirmLinkScreen.confirmLinkNow(this.screen, (URI)openUrl.uri());
         }

      });
      content.addChild(textWidget);
      int frameHeight = this.scrollableLayout.getHeight();
      FrameLayout linearLayout = (FrameLayout)this.friendScrollableContent.addChild(this.createCenteredFrame(content, this.width - 10, frameHeight));
      linearLayout.defaultChildLayoutSetting().paddingLeft(8).paddingTop(0).alignHorizontallyCenter().alignVerticallyMiddle();
      this.addFriendWidget.applyState(this.addFriendWidget.getValue().isEmpty() ? AddFriendWidget.State.EMPTY_INPUT : AddFriendWidget.State.READY);
   }

   void rearrangeElements() {
      this.scrollableLayout.setMinHeight(this.height - this.addFriendWidget.contentHeight());
      this.scrollableLayout.setMaxHeight(this.height - this.addFriendWidget.contentHeight());
   }

   private void sendFriendRequest() {
      String name = this.addFriendWidget.getValue();
      if (!name.isBlank() && !this.checkLocalConflicts(name)) {
         this.addFriendWidget.applyState(AddFriendWidget.State.SENDING);
         this.layout.arrangeElements();
         this.minecraft.getPlayerSocialManager().sendFriendRequest(name).thenAcceptAsync((var1) -> {
            this.addFriendWidget.setValue("");
            this.addFriendWidget.applyState(AddFriendWidget.State.EMPTY_INPUT);
            this.screen.refreshLists();
         }, this.minecraft);
      } else {
         this.addFriendWidget.applyState(AddFriendWidget.State.EMPTY_INPUT);
         this.layout.arrangeElements();
      }
   }

   private boolean checkLocalConflicts(final String name) {
      String selfName = this.minecraft.getUser().getName();
      if (selfName.equalsIgnoreCase(name)) {
         return true;
      } else {
         PlayerSocialManager social = this.minecraft.getPlayerSocialManager();
         return this.contains(name, social.getFriends()) || this.contains(name, social.getOutgoingRequests()) || this.contains(name, social.getIncomingRequests());
      }
   }

   private boolean contains(final String playerName, final List<PlayerSocialManager.PlayerData> players) {
      for(PlayerSocialManager.PlayerData playerData : players) {
         if (playerData.name().equalsIgnoreCase(playerName)) {
            return true;
         }
      }

      return false;
   }

   public Component getTabTitle() {
      return TAB_TITLE;
   }

   public Component getTabExtraNarration() {
      return Component.empty();
   }

   public void visitChildren(final Consumer<AbstractWidget> childrenConsumer) {
      this.layout.visitWidgets(childrenConsumer);
   }

   public void doLayout(final ScreenRectangle screenRectangle) {
      this.layout.arrangeElements();
      FrameLayout.alignInRectangle(this.layout, screenRectangle, 0.5F, 0.16666667F);
   }

   public Layout getLayout() {
      return this.layout;
   }

   void updateEntries(final List<FriendEntry> friendEntries) {
      this.friendScrollableContent.removeChildren();
      LinearLayout var10001 = this.friendScrollableContent;
      Objects.requireNonNull(var10001);
      friendEntries.forEach(var10001::addChild);
      this.friendScrollableContent.addChild(this.createManageAccountFooter());
   }

   private FrameLayout createManageAccountFooter() {
      FocusableTextWidget textWidget = FocusableTextWidget.builder(MANAGE_ACCOUNT_FOOTER, this.screen.getFont()).maxWidth(this.width - 16 - 4).alwaysShowBorder(false).backgroundFill(FocusableTextWidget.BackgroundFill.NEVER).build();
      textWidget.setCentered(true);
      textWidget.setComponentClickHandler((style) -> {
         ClickEvent patt0$temp = style.getClickEvent();
         if (patt0$temp instanceof ClickEvent.OpenUrl openUrl) {
            PrivacyConfirmLinkScreen.confirmLinkNow(this.screen, (URI)openUrl.uri());
         }

      });
      FrameLayout frame = new FrameLayout(this.width - 10, textWidget.getHeight());
      frame.defaultChildLayoutSetting().alignHorizontallyCenter().alignVerticallyMiddle();
      frame.addChild(textWidget);
      return frame;
   }

   protected Layout entriesContainer() {
      return this.friendScrollableContent;
   }

   static {
      EMPTY_STATE = Component.translatable("gui.friends.empty_state", MICROSOFT_ACCOUNT_LINK).withStyle(ChatFormatting.GRAY);
      MANAGE_ACCOUNT_FOOTER = Component.translatable("gui.friends.manage_account_footer", MICROSOFT_ACCOUNT_LINK).withStyle(ChatFormatting.GRAY);
      ILLUSTRATION = Identifier.withDefaultNamespace("friends/illustrations_00");
   }
}
