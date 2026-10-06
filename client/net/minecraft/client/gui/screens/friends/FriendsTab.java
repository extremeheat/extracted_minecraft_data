package net.minecraft.client.gui.screens.friends;

import com.mojang.authlib.services.response.PresenceResponse;
import com.mojang.authlib.services.response.PresenceStatus;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.LoadingDotsWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.EqualSpacingLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.PrivacyConfirmLinkScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonLinks;

class FriendsTab extends AbstractFriendsTab {
   public static final Component TAB_TITLE = Component.translatable("gui.friends.tab_friends");
   private static final Component MICROSOFT_ACCOUNT_LINK = Component.translatable("gui.friends.empty_state.link").withStyle((UnaryOperator)((style) -> style.withUnderlined(true).withColor(ChatFormatting.GRAY).withClickEvent(new ClickEvent.OpenUrl(CommonLinks.PRIVACY_AND_ONLINE_SETTINGS))));
   private static final Component EMPTY_STATE;
   private static final Component MANAGE_ACCOUNT_FOOTER;
   private static final Component NO_SEARCH_RESULTS;
   private static final Identifier ILLUSTRATION;
   private static final int HEADER_VERTICAL_PADDING = 4;
   private static final int WIDGET_SPACING = 3;
   private static final int BUTTON_SIZE = 20;
   private final FriendsOverlayScreen screen;
   private final LoadingDotsWidget loadingDotsWidget;
   private final EqualSpacingLayout profileArea;
   private final PlayerProfileWidget profileWidget;
   private final AddFriendWidget addFriendWidget;
   private final FriendsListFilter friendsListFilter = new FriendsListFilter(this::updateListView);
   private final FriendsListOrder friendsListOrder = new FriendsListOrder(this::updateListView);

   public FriendsTab(final Minecraft minecraft, final LoadingDotsWidget loadingDotsWidget, final FriendsOverlayScreen screen, final int width, final int height) {
      super(minecraft, width, height);
      this.screen = screen;
      this.loadingDotsWidget = loadingDotsWidget;
      User user = minecraft.getUser();
      this.profileWidget = new PlayerProfileWidget(minecraft, user.getProfileId(), user.getName());
      int listWidth = this.getListContentWidth();
      this.profileArea = new EqualSpacingLayout(listWidth, 0, EqualSpacingLayout.Orientation.HORIZONTAL);
      this.profileArea.defaultChildLayoutSetting().alignVerticallyMiddle();
      this.profileArea.addChild(this.profileWidget);
      this.profileArea.addChild(minecraft.options.sharePresence().createButton(minecraft.options, 0, 0, 20));
      this.addFriendWidget = new AddFriendWidget(this.friendsListFilter, this.friendsListOrder, listWidth, this::updateListView);
      LinearLayout header = (LinearLayout)this.headerFrame.addChild(LinearLayout.vertical().spacing(3), (Consumer)((settings) -> settings.paddingVertical(4)));
      header.addChild(this.profileArea, (Consumer)(LayoutSettings::alignHorizontallyCenter));
      header.addChild(ImageWidget.sprite(width, 2, LIST_SEPARATOR_TOP));
      header.addChild(this.addFriendWidget, (Consumer)(LayoutSettings::alignHorizontallyCenter));
      this.rearrangeElements();
   }

   public void showLoading() {
      this.showCenteredContent(this.loadingDotsWidget);
      this.addFriendWidget.applyState(AddFriendWidget.State.SENDING);
   }

   private void showGenericEmptyState(final Component message) {
      MultiLineTextWidget text = this.createCenteredText(message, this.screen.getFont(), this.getListContentWidth());
      this.showCenteredContent(text);
   }

   public void showError(final Component message) {
      this.showGenericEmptyState(message.copy().withStyle(ChatFormatting.GRAY));
      this.addFriendWidget.applyState(AddFriendWidget.State.DISABLED);
   }

   public void showEmpty() {
      LinearLayout content = LinearLayout.vertical().spacing(8);
      content.defaultCellSetting().alignHorizontallyCenter().alignVerticallyMiddle();
      content.addChild(ImageWidget.sprite(128, 48, ILLUSTRATION));
      int maxWidth = this.getListContentWidth();
      MultiLineTextWidget textWidget = this.createCenteredText(EMPTY_STATE, this.screen.getFont(), maxWidth);
      textWidget.setComponentClickHandler((style) -> {
         ClickEvent patt1$temp = style.getClickEvent();
         if (patt1$temp instanceof ClickEvent.OpenUrl $b$0) {
            ClickEvent.OpenUrl var10000 = $b$0;

            try {
               var7 = var10000.uri();
            } catch (Throwable var6) {
               throw new MatchException(var6.toString(), var6);
            }

            URI patt2$temp = var7;
            PrivacyConfirmLinkScreen.confirmLinkNow(this.screen, patt2$temp);
         }

      });
      content.addChild(textWidget);
      this.showCenteredContent(content);
      this.addFriendWidget.applyState(this.addFriendWidget.getValue().isEmpty() ? AddFriendWidget.State.EMPTY_INPUT : AddFriendWidget.State.READY);
   }

   private void updateListView() {
      this.screen.refreshLists();
   }

   public Component getTabExtraNarration() {
      return CommonComponents.EMPTY;
   }

   public void updateEntries(final List<FriendEntry> friendEntries) {
      this.friendsListFilter.filter(friendEntries);
      friendEntries.sort(this.friendsListOrder);
      if (friendEntries.isEmpty()) {
         this.showGenericEmptyState(NO_SEARCH_RESULTS);
      } else {
         this.scrollableLayout.alignVerticallyTop();
         this.scrollableContent.removeChildren();
         LinearLayout var10001 = this.scrollableContent;
         Objects.requireNonNull(var10001);
         friendEntries.forEach(var10001::addChild);
         if (this.friendsListFilter.isEmpty()) {
            this.showManageAccountFooter(this.screen, MANAGE_ACCOUNT_FOOTER);
         }
      }

      this.addFriendWidget.applyState(this.addFriendWidget.getValue().isEmpty() ? AddFriendWidget.State.EMPTY_INPUT : AddFriendWidget.State.READY);
   }

   public void applyPresenceUpdate(final PresenceResponse latestPresence) {
      this.scrollableContent.visitWidgets((widget) -> {
         if (widget instanceof FriendEntry entry) {
            entry.applyPresence(latestPresence);
         }

      });
   }

   public void applyOwnPresence(final PresenceStatus presenceStatus) {
      this.profileWidget.statusWidget().applyPresence(presenceStatus);
   }

   public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_LIGHT_SPRITE, this.profileArea.getX() - 6, this.profileArea.getY() - 4, this.profileArea.getWidth() + 12, this.profileArea.getHeight() + 4 + 3);
   }

   static {
      EMPTY_STATE = Component.translatable("gui.friends.empty_state", MICROSOFT_ACCOUNT_LINK).withStyle(ChatFormatting.GRAY);
      MANAGE_ACCOUNT_FOOTER = Component.translatable("gui.friends.manage_account_footer", MICROSOFT_ACCOUNT_LINK).withStyle(ChatFormatting.GRAY);
      NO_SEARCH_RESULTS = Component.translatable("gui.friends.search.no_results").withStyle(ChatFormatting.GRAY);
      ILLUSTRATION = Identifier.withDefaultNamespace("friends/illustrations_00");
   }
}
