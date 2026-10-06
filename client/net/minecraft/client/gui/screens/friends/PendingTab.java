package net.minecraft.client.gui.screens.friends;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.LoadingDotsWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

class PendingTab extends AbstractFriendsTab {
   private static final Component RECEIVED_HEADER;
   private static final Component SENT_HEADER;
   private static final Component EMPTY_STATE;
   private static final Component DISABLED_STATE;
   private static final Component SEARCH;
   private static final Component NO_SEARCH_RESULTS;
   private static final int SEARCH_BAR_REQUEST_THRESHOLD = 15;
   private static final int HEADER_VERTICAL_PADDING = 5;
   private final FriendsOverlayScreen screen;
   private final LoadingDotsWidget loadingDotsWidget;
   private boolean showingSearchBar;
   private final FriendsListFilter friendsListFilter = new FriendsListFilter(this::updateListView);

   public PendingTab(final Minecraft minecraft, final LoadingDotsWidget loadingDotsWidget, final FriendsOverlayScreen screen, final int width, final int height) {
      super(minecraft, width, height);
      this.screen = screen;
      this.loadingDotsWidget = loadingDotsWidget;
      this.rearrangeElements();
   }

   public Component getTabExtraNarration() {
      return Component.empty();
   }

   private void updateListView() {
      this.screen.refreshLists();
   }

   private void showSearchBar() {
      if (!this.showingSearchBar) {
         this.showingSearchBar = true;
         LinearLayout header = (LinearLayout)this.headerFrame.addChild(LinearLayout.vertical());
         EditBox searchBox = (EditBox)header.addChild(new EditBox(this.screen.getFont(), this.getListContentWidth(), 20, SEARCH), (Consumer)((settings) -> settings.padding(6, 5)));
         searchBox.setHint(SEARCH);
         searchBox.setResponder((value) -> this.friendsListFilter.updateSearchFilter(value.trim()));
         header.addChild(ImageWidget.sprite(this.width, 2, LIST_SEPARATOR_TOP));
      }
   }

   private void hideSearchBar() {
      if (this.showingSearchBar) {
         this.showingSearchBar = false;
         this.headerFrame.removeChildren();
         this.friendsListFilter.updateSearchFilter("");
      }

   }

   public void showLoading() {
      this.showCenteredContent(this.loadingDotsWidget);
   }

   public void showError(final Component message) {
      MultiLineTextWidget text = this.createCenteredText(message.copy().withStyle(ChatFormatting.GRAY), this.screen.getFont(), this.getListContentWidth());
      this.showCenteredContent(text);
   }

   public void updateEntries(final List<IncomingEntry> incomingEntries, final List<OutgoingEntry> outgoingEntries) {
      int totalRequestsCount = incomingEntries.size() + outgoingEntries.size();
      if (totalRequestsCount < 15) {
         this.hideSearchBar();
      } else {
         this.showSearchBar();
      }

      this.friendsListFilter.filter(incomingEntries);
      this.friendsListFilter.filter(outgoingEntries);
      if (incomingEntries.isEmpty() && outgoingEntries.isEmpty()) {
         this.showEmpty(NO_SEARCH_RESULTS);
      } else {
         this.scrollableLayout.alignVerticallyTop();
         this.scrollableContent.removeChildren();
         if (!incomingEntries.isEmpty()) {
            this.scrollableContent.addChild(this.createText(RECEIVED_HEADER, this.screen.getFont(), this.getListContentWidth()), (Consumer)(LayoutSettings::alignHorizontallyCenter));
            LinearLayout var10001 = this.scrollableContent;
            Objects.requireNonNull(var10001);
            incomingEntries.forEach(var10001::addChild);
         }

         if (!outgoingEntries.isEmpty()) {
            this.scrollableContent.addChild(this.createText(SENT_HEADER, this.screen.getFont(), this.getListContentWidth()), (Consumer)(LayoutSettings::alignHorizontallyCenter));
            LinearLayout var4 = this.scrollableContent;
            Objects.requireNonNull(var4);
            outgoingEntries.forEach(var4::addChild);
         }

      }
   }

   private void showEmpty(final Component message) {
      MultiLineTextWidget text = this.createCenteredText(message, this.screen.getFont(), this.getListContentWidth());
      this.showCenteredContent(text);
   }

   public void showEmpty() {
      this.showEmpty(EMPTY_STATE);
      this.hideSearchBar();
   }

   public void showDisabled() {
      this.showEmpty(DISABLED_STATE);
      this.hideSearchBar();
   }

   public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      if (this.showingSearchBar) {
         graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_LIGHT_SPRITE, this.headerFrame.getX(), this.headerFrame.getY(), this.headerFrame.getWidth(), this.headerFrame.getHeight() - 2);
      }
   }

   static {
      RECEIVED_HEADER = Component.translatable("gui.friends.pending.received").withStyle(ChatFormatting.BOLD, ChatFormatting.UNDERLINE);
      SENT_HEADER = Component.translatable("gui.friends.pending.sent").withStyle(ChatFormatting.BOLD, ChatFormatting.UNDERLINE);
      EMPTY_STATE = Component.translatable("gui.friends.pending.empty").withStyle(ChatFormatting.GRAY);
      DISABLED_STATE = Component.translatable("gui.friends.pending.disabled").withStyle(ChatFormatting.GRAY);
      SEARCH = Component.translatable("gui.friends.pending.search");
      NO_SEARCH_RESULTS = Component.translatable("gui.friends.pending.search.no_results").withStyle(ChatFormatting.GRAY);
   }
}
