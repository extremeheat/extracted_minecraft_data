package net.minecraft.client.gui.screens.friends;

import com.mojang.authlib.yggdrasil.response.PresenceResponse;
import com.mojang.authlib.yggdrasil.response.PresenceStatusDto;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.LoadingDotsWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.PrivacyConfirmLinkScreen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonLinks;
import org.jspecify.annotations.Nullable;

class FriendsTab extends AbstractFriendsTab {
   private static final Component TAB_TITLE = Component.translatable("gui.friends.tab_friends");
   private static final Component MICROSOFT_ACCOUNT_LINK = Component.translatable("gui.friends.empty_state.link").withStyle((UnaryOperator)((style) -> style.withUnderlined(true).withColor(ChatFormatting.GRAY).withClickEvent(new ClickEvent.OpenUrl(CommonLinks.PRIVACY_AND_ONLINE_SETTINGS))));
   private static final Component EMPTY_STATE;
   private static final Component MANAGE_ACCOUNT_FOOTER;
   private static final Identifier ILLUSTRATION;
   private final FriendsOverlayScreen screen;
   private final LinearLayout layout;
   private final LinearLayout friendScrollableContent;
   private final LoadingDotsWidget loadingDotsWidget;
   private final AddFriendWidget addFriendWidget;
   private final ScrollableLayout scrollableLayout;
   private @Nullable FrameLayout contentFrame;

   FriendsTab(final Minecraft minecraft, final LoadingDotsWidget loadingDotsWidget, final FriendsOverlayScreen screen, final int width, final int height) {
      super(width, height);
      this.screen = screen;
      this.layout = LinearLayout.vertical();
      this.layout.defaultCellSetting().alignHorizontallyCenter();
      this.loadingDotsWidget = loadingDotsWidget;
      this.addFriendWidget = new AddFriendWidget(width, this::onSendFriendRequestFinished);
      this.layout.addChild(this.addFriendWidget);
      this.friendScrollableContent = LinearLayout.vertical();
      this.friendScrollableContent.defaultCellSetting();
      this.scrollableLayout = new ScrollableLayout(minecraft, this.friendScrollableContent, height - this.addFriendWidget.contentHeight(), ScrollableLayout.ReserveStrategy.BOTH);
      this.scrollableLayout.setScrollbarSpacing(2);
      this.layout.addChild(this.scrollableLayout);
      this.rearrangeElements();
   }

   public void showLoading() {
      this.friendScrollableContent.removeChildren();
      this.contentFrame = this.createCenteredFrame(this.loadingDotsWidget, this.getListContentWidth(), this.height - this.addFriendWidget.contentHeight());
      this.friendScrollableContent.addChild(this.contentFrame);
      this.addFriendWidget.applyState(AddFriendWidget.State.SENDING);
   }

   public void showError(final Component message) {
      this.friendScrollableContent.removeChildren();
      int maxWidth = this.getListContentWidth();
      MultiLineTextWidget text = this.createCenteredText(message.copy().withStyle(ChatFormatting.GRAY), this.screen.getFont(), maxWidth);
      this.contentFrame = this.createCenteredFrame(text, maxWidth, this.height - this.addFriendWidget.contentHeight());
      this.friendScrollableContent.addChild(this.contentFrame);
      this.addFriendWidget.applyState(AddFriendWidget.State.DISABLED);
   }

   public void showEmpty() {
      this.friendScrollableContent.removeChildren();
      LinearLayout content = (new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL)).spacing(8);
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
            PrivacyConfirmLinkScreen.confirmLinkNow(this.screen, (URI)patt2$temp);
         }

      });
      content.addChild(textWidget);
      int frameHeight = this.scrollableLayout.getHeight();
      this.contentFrame = this.createCenteredFrame(content, maxWidth, frameHeight);
      this.friendScrollableContent.addChild(this.contentFrame);
      this.addFriendWidget.applyState(this.addFriendWidget.getValue().isEmpty() ? AddFriendWidget.State.EMPTY_INPUT : AddFriendWidget.State.READY);
   }

   void rearrangeElements() {
      this.scrollableLayout.setMinHeight(this.height - this.addFriendWidget.contentHeight());
      this.scrollableLayout.setMaxHeight(this.height - this.addFriendWidget.contentHeight());
      if (this.contentFrame != null) {
         this.contentFrame.setMinHeight(this.height - this.addFriendWidget.contentHeight());
      }

   }

   private void onSendFriendRequestFinished() {
      this.screen.refreshLists();
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
      this.contentFrame = null;
      LinearLayout var10001 = this.friendScrollableContent;
      Objects.requireNonNull(var10001);
      friendEntries.forEach(var10001::addChild);
      this.friendScrollableContent.addChild(this.createManageAccountFooter());
      this.addFriendWidget.applyState(this.addFriendWidget.getValue().isEmpty() ? AddFriendWidget.State.EMPTY_INPUT : AddFriendWidget.State.READY);
   }

   void applyPresenceUpdate(final PresenceResponse latestPresence) {
      this.friendScrollableContent.visitWidgets((widget) -> {
         if (widget instanceof FriendEntry entry) {
            PresenceStatusDto newPresenceStatus = null;

            for(PresenceStatusDto presenceStatus : latestPresence.presence()) {
               if (presenceStatus.profileId().equals(entry.playerId())) {
                  newPresenceStatus = presenceStatus;
                  break;
               }
            }

            entry.applyPresence(newPresenceStatus);
         }

      });
   }

   private FrameLayout createManageAccountFooter() {
      int maxWidth = this.getListContentWidth();
      MultiLineTextWidget textWidget = this.createCenteredText(MANAGE_ACCOUNT_FOOTER, this.screen.getFont(), maxWidth);
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
            AbstractWidget.playButtonClickSound(Minecraft.getInstance().getSoundManager());
            PrivacyConfirmLinkScreen.confirmLinkNow(this.screen, (URI)patt2$temp);
         }

      });
      FrameLayout frame = new FrameLayout(maxWidth, textWidget.getHeight());
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
