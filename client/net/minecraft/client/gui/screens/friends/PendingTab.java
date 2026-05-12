package net.minecraft.client.gui.screens.friends;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.LoadingDotsWidget;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;

class PendingTab extends AbstractFriendsTab {
   private static final Component RECEIVED_HEADER;
   private static final Component SENT_HEADER;
   private static final Component EMPTY_STATE;
   private final FriendsOverlayScreen screen;
   private final LinearLayout layout;
   private final LinearLayout pendingScrollableContent;
   private final LoadingDotsWidget loadingDotsWidget;
   private final ScrollableLayout scrollableLayout;

   PendingTab(final Minecraft minecraft, final LoadingDotsWidget loadingDotsWidget, final FriendsOverlayScreen screen, final int width, final int height) {
      super(width, height);
      this.screen = screen;
      this.layout = LinearLayout.vertical();
      this.layout.defaultCellSetting().alignHorizontallyCenter();
      this.loadingDotsWidget = loadingDotsWidget;
      this.pendingScrollableContent = LinearLayout.vertical();
      this.scrollableLayout = new ScrollableLayout(minecraft, this.pendingScrollableContent, height, ScrollableLayout.ReserveStrategy.RIGHT);
      this.scrollableLayout.setMaxHeight(height);
      this.layout.addChild(this.scrollableLayout);
      this.rearrangeElements();
   }

   void rearrangeElements() {
      this.scrollableLayout.setMinHeight(this.height);
      this.scrollableLayout.setMaxHeight(this.height);
   }

   public Component getTabTitle() {
      return Component.translatable("gui.friends.requests_count", 0);
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

   public void showLoading() {
      this.pendingScrollableContent.removeChildren();
      this.pendingScrollableContent.addChild(this.createCenteredFrame(this.loadingDotsWidget, this.width - 10, this.height));
   }

   public void showError(final Component message) {
      this.pendingScrollableContent.removeChildren();
      FocusableTextWidget text = FocusableTextWidget.builder(message.copy().withStyle(ChatFormatting.GRAY), this.screen.getFont()).maxWidth(this.width - 16 - 4).alwaysShowBorder(false).backgroundFill(FocusableTextWidget.BackgroundFill.NEVER).build();
      text.setCentered(true);
      this.pendingScrollableContent.addChild(this.createCenteredFrame(text, this.width - 10, this.height));
   }

   public void updateEntries(final List<IncomingEntry> incomingEntries, final List<OutgoingEntry> outgoingEntries) {
      this.pendingScrollableContent.removeChildren();
      if (!incomingEntries.isEmpty()) {
         this.pendingScrollableContent.addChild(FocusableTextWidget.builder(RECEIVED_HEADER, this.screen.getFont()).alwaysShowBorder(false).backgroundFill(FocusableTextWidget.BackgroundFill.NEVER).build(), (Consumer)(LayoutSettings::alignHorizontallyCenter));
         LinearLayout var10001 = this.pendingScrollableContent;
         Objects.requireNonNull(var10001);
         incomingEntries.forEach(var10001::addChild);
      }

      if (!outgoingEntries.isEmpty()) {
         this.pendingScrollableContent.addChild(FocusableTextWidget.builder(SENT_HEADER, this.screen.getFont()).alwaysShowBorder(false).backgroundFill(FocusableTextWidget.BackgroundFill.NEVER).build(), (Consumer)(LayoutSettings::alignHorizontallyCenter));
         LinearLayout var3 = this.pendingScrollableContent;
         Objects.requireNonNull(var3);
         outgoingEntries.forEach(var3::addChild);
      }

   }

   protected Layout entriesContainer() {
      return this.pendingScrollableContent;
   }

   public void showEmpty() {
      this.pendingScrollableContent.removeChildren();
      LinearLayout content = (new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL)).spacing(8);
      content.defaultCellSetting().alignHorizontallyCenter().alignVerticallyMiddle().paddingLeft(8);
      FocusableTextWidget text = FocusableTextWidget.builder(EMPTY_STATE, this.screen.getFont()).alwaysShowBorder(false).backgroundFill(FocusableTextWidget.BackgroundFill.NEVER).build();
      text.setCentered(true);
      content.addChild(text);
      FrameLayout linearLayout = (FrameLayout)this.pendingScrollableContent.addChild(this.createCenteredFrame(content, this.width - 10, this.height));
      linearLayout.defaultChildLayoutSetting().paddingLeft(8).paddingTop(0).alignHorizontallyCenter().alignVerticallyMiddle();
   }

   static {
      RECEIVED_HEADER = Component.translatable("gui.friends.pending.received").withStyle(ChatFormatting.BOLD, ChatFormatting.UNDERLINE);
      SENT_HEADER = Component.translatable("gui.friends.pending.sent").withStyle(ChatFormatting.BOLD, ChatFormatting.UNDERLINE);
      EMPTY_STATE = Component.translatable("gui.friends.pending.empty").withStyle(ChatFormatting.GRAY);
   }
}
